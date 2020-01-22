/*
 * Copyright 2018 olivier.tatsinkou.
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

import com.tatsinktechnologic.configuration.ConfigLoader;
import com.tatsinktechnologic.ws.server.SMSGW_Thread;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.tatsinktechnologic.smpp.gateway.SMSGateway;
import com.tatsinktechnologic.xml.service_listener.Kafka_Topic_Crud;
import com.tatsinktechnologic.xml.service_listener.Service;
import com.tatsinktechnologic.xml.service_listener.Service_listener;
import java.io.File;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

/**
 *
 * @author olivier.tatsinkou
 */
public class SMSGW {

    /**
     * @param args the command line arguments
     */
    private static Logger logger = Logger.getLogger(SMSGW.class);
    private static Service_listener service_listener;
    private static ConfigLoader xmlConfig;

    public static void main(String[] args) {
        // TODO code application logic here
        logger.info("Initialising...");
        PropertyConfigurator.configure("etc" + File.separator + "log4j.cfg");
        logger.info("Load log4j config file done.");

        logger.info("############# START SMS GATEWAY ###################");

        xmlConfig = ConfigLoader.getConfigurationLoader();
        service_listener = xmlConfig.getService_listener();
        Kafka_Topic_Crud kafka_topic_crud = service_listener.getKafka_topic_crud();
        List<Service> listService = service_listener.getServices();

        // create delivery topic 
        createTopicIfNotExists(service_listener.getDelivery_topic(), kafka_topic_crud);

        for (Service service : listService) {
            // create mo topic
            createTopicIfNotExists(service.getMofilter().getTopic(), kafka_topic_crud);

            // create mt topc
            createTopicIfNotExists(service.getMtfilter().getTopic(), kafka_topic_crud);
        }
        //process receiver
        SMSGateway.getSenderGateway();

        //process sender
       List<List<Runnable>> runnables = new ArrayList<List<Runnable>>();
       List<Runnable> service_runnables;
       int val = 0;
        for (Service service : listService) {
            service_runnables = new ArrayList<Runnable>();
            
            int poolNumber = service.getPoolthread();
            if (poolNumber <= 0) {
                poolNumber = 1;
            }
            for (int i = 0; i < poolNumber; i++) {
                service_runnables.add(new SMSGW_Thread(service,val+"_"+i));
            }
            ExecutorService service_execute = Executors.newFixedThreadPool(poolNumber);
            SMSGW_Thread.executeRunnables(service_execute, service_runnables);
            runnables.add(service_runnables);
            val++;
        }

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
