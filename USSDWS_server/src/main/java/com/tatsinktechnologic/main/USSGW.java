/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.main;

import com.tatsinktechnologic.dao_commun_repository.CommunDAO_Repository;
import com.tatsinktechnologic.resfull.services.USSDService;
import com.tatsinktechnologic.xml.Kafka_Topic_Crud;
import com.tatsinktechnologic.xml.Service;
import com.tatsinktechnologic.xml.Ussd_API_Conf;
import java.io.File;
import java.util.List;
import java.util.Properties;
import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
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
public class USSGW {

    /**
     * @param args the command line arguments
     */
    private static Logger logger = Logger.getLogger(USSGW.class);
    private static CommunDAO_Repository communConf = CommunDAO_Repository.getConfigurationLoader();
    private static Ussd_API_Conf ussd_api_config;

    public static void main(String[] args) {

        PropertyConfigurator.configure("etc" + File.separator + "log4j.cfg");
        logger.info("Load log4j config file done.");

        ussd_api_config = communConf.getUssd_api_conf();

        Kafka_Topic_Crud kafka_topic_crud = ussd_api_config.getKafka_topic_crud();
        List<Service> listService = ussd_api_config.getServices();

        // create delivery topic 
        createTopicIfNotExists(ussd_api_config.getDelivery_topic(), kafka_topic_crud);

        for (Service service : listService) {
            // create mo topic
            createTopicIfNotExists(service.getMofilter().getTopic(), kafka_topic_crud);

            // create mt topc
            createTopicIfNotExists(service.getMtfilter().getTopic(), kafka_topic_crud);
        }

        
        // start API 
        JAXRSServerFactoryBean factoryBean = new JAXRSServerFactoryBean();
        factoryBean.setResourceClasses(USSDService.class);

        factoryBean.setResourceProvider(new SingletonResourceProvider(new USSDService()));

        factoryBean.getInInterceptors().add(new LoggingInInterceptor());

        factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());

        factoryBean.setAddress(ussd_api_config.getURL());

        Server server = factoryBean.create();
       logger.info("############# USSD APPLICATION IS UP ##############");
    }

    public static boolean createTopicIfNotExists(String topicName, Kafka_Topic_Crud kafka_topic_crud) {
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
