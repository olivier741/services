/*
 * Copyright 2020 olivier.tatsinkou.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tatsinktechnologic.main;

import com.tatsinktechnologic.config.ConfigLoader;
import com.tatsinktechnologic.config.Smpp_ConfigLoader;
import com.tatsinktechnologic.resfull.services.API_USSDService;
import com.tatsinktechnologic.smpp.gateway.SMSGateway;
import com.tatsinktechnologic.xml.kafka.API_Conf;
import com.tatsinktechnologic.xml.kafka.KAFKA_Conf;
import com.tatsinktechnologic.xml.kafka.SMPP_Conf;
import com.tatsinktechnologic.xml.kafka.USSD_Conf;
import java.io.File;
import java.util.List;
import java.util.Properties;
import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author olivier.tatsinkou
 */
public class USSDGW {

    /**
     * @param args the command line arguments
     */
    private static Logger logger = Logger.getLogger(USSDGW.class);
    private static ConfigLoader communConf;
     private static Smpp_ConfigLoader configLoad;

    public static void main(String[] args) {
        // TODO code application logic here

        PropertyConfigurator.configure("etc" + File.separator + "log4j.cfg");
        logger.info("Load log4j config file done.");

        communConf = ConfigLoader.getConfigurationLoader();
        USSD_Conf ussd_conf = communConf.getUssdconfig();

        KAFKA_Conf kafka_conf = communConf.getKafka_conf();

        List<String> listTopic = communConf.getListTopic();
        API_Conf api_conf = ussd_conf.getApi_conf();
        SMPP_Conf smpp_conf = ussd_conf.getSmpp_conf();

        for (String topic : listTopic) {
            // create topic
            createTopicIfNotExists(topic, kafka_conf);
        }

        String connect_type = ussd_conf.getConnect_type();
        if (!StringUtils.isBlank(connect_type)) {
            if (connect_type.equals("API")) {
               logger.info("############# REST API SELECTED ##############");
                // start API 
                JAXRSServerFactoryBean factoryBean = new JAXRSServerFactoryBean();
                factoryBean.setResourceClasses(API_USSDService.class);
                factoryBean.setResourceProvider(new SingletonResourceProvider(new API_USSDService()));
                factoryBean.getInInterceptors().add(new LoggingInInterceptor());
                factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
                factoryBean.setAddress(api_conf.getURL());
                Server server = factoryBean.create();
            } else if (connect_type.equals("SMPP")) {
                logger.info("############# SMPP SELECTED ##############");
                configLoad = Smpp_ConfigLoader.getConfigurationLoader();
                // create delivery topic 
                String delv_topic =smpp_conf.getDeliviery_topic();
                if (!StringUtils.isBlank(delv_topic)){
                    createTopicIfNotExists(delv_topic, kafka_conf);
                }
                
                //process receiver
                SMSGateway.getSenderGateway();

            } else if (connect_type.equals("BOTH")) {
                logger.info("############# BOTH (REST API and SMPP SELECTED ##############");
                
                 // create delivery topic 
                String delv_topic =smpp_conf.getDeliviery_topic();
                if (!StringUtils.isBlank(delv_topic)){
                    createTopicIfNotExists(delv_topic, kafka_conf);
                }
                
                logger.info("############# START SMPP ##############");
                try {
                    configLoad = Smpp_ConfigLoader.getConfigurationLoader();
                    SMSGateway.getSenderGateway();
                } catch (Exception e) {
                    logger.error("SMPP NOT LAUNCH",e);
                }
                
                
                
                 logger.info("############# START API ##############");
                // start API 
                JAXRSServerFactoryBean factoryBean = new JAXRSServerFactoryBean();
                factoryBean.setResourceClasses(API_USSDService.class);
                factoryBean.setResourceProvider(new SingletonResourceProvider(new API_USSDService()));
                factoryBean.getInInterceptors().add(new LoggingInInterceptor());
                factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
                factoryBean.setAddress(api_conf.getURL());
                Server server = factoryBean.create();

            } else {
                logger.error("############ WRONG CONNECT_TYPE. connect_type must be API, SMPP, BOTH #################");
            }
        }

        logger.info("############# USSD APPLICATION IS UP ##############");
    }

    public static boolean createTopicIfNotExists(String topicName, KAFKA_Conf kafka_topic_crud) {
        ZkClient zkClient = null;
        ZkUtils zkUtils = null;
        boolean result = false;
        String zookeeperHosts = kafka_topic_crud.getZookeeper_addr();
        int partitions = kafka_topic_crud.getPartitions();
        int replicationFactor = kafka_topic_crud.getReplication();
        int sessionTimeOutInMs = kafka_topic_crud.getSessionTimeOutInMs() * 1000;
        int connectionTimeOutInMs = kafka_topic_crud.getConnectionTimeOutInMs() * 1000;

        try {
            zkClient = new ZkClient(zookeeperHosts, sessionTimeOutInMs, connectionTimeOutInMs, ZKStringSerializer$.MODULE$);
            zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperHosts), false);

            Properties topicConfiguration = new Properties();

            if (!AdminUtils.topicExists(zkUtils, topicName)) {
                AdminUtils.createTopic(zkUtils, topicName, partitions, replicationFactor, topicConfiguration, RackAwareMode.Enforced$.MODULE$);
                logger.info("############# Topic " + topicName + " created ##############");
                result = true;
            } else {
                logger.info("############ Topic " + topicName + " already exists #################");
            }

        } catch (Exception ex) {
            logger.error("cannot create topic : " + topicName, ex);
        } finally {
            if (zkClient != null) {
                zkClient.close();
            }
        }

        return result;
    }

}
