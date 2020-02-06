///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.tatsinktechnologic.resfull.services;
//
///**
// *
// * @author olivier.tatsinkou
// */
//import com.tatsinktechnologic.beans.Message_Exchg;
//import com.tatsinktechnologic.configuration.ConfigLoader;
//import com.tatsinktechnologic.xml.service_listener.Service;
//import com.tatsinktechnologic.xml.service_listener.Service_listener;
//import java.io.IOException;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//import java.util.Properties;
//import java.util.concurrent.ExecutorService;
//import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.common.TopicPartition;
//import org.apache.log4j.Logger;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.tatsinktechnologic.smpp.gateway.SMSGateway;
//import org.apache.commons.lang.SerializationUtils;
//
//public class SMSGW_Thread implements Runnable {
//
//    private static final Logger logger = Logger.getLogger(SMSGW_Thread.class);
//
//    private Service service;
//    private ConfigLoader config;
//    private SMSGateway senderGateway;
//    private KafkaConsumer<byte[], byte[]> consumer;
//    private String client_id;
//
//    public SMSGW_Thread(Service service, String id) {
//        this.config = ConfigLoader.getConfigurationLoader();
//        this.service = service;
//        this.client_id = id;
//        this.senderGateway = SMSGateway.getSenderGateway();
//
//        Properties props = (Properties)SerializationUtils.clone(config.getConsumer_props());
//        String new_client_id = props.getProperty("client.id");
//        new_client_id = new_client_id + "_" + id;
//        props.put("client.id", new_client_id);
//
//        String topic = service.getMtfilter().getTopic();
//
//        consumer = new KafkaConsumer<>(props);
//        TestConsumerRebalanceListener rebalanceListener = new TestConsumerRebalanceListener();
//        consumer.subscribe(Collections.singletonList(topic), rebalanceListener);
//    }
//
//    @Override
//    public void run() {
//        int numberRecord = service.getMtfilter().getNumberRecord();
//
//        while (true) {
//
//            ConsumerRecords<byte[], byte[]> records = consumer.poll(numberRecord);
//            for (ConsumerRecord<byte[], byte[]> record : records) {
////                System.out.printf("Received Message topic =%s, partition =%s, offset = %d, key = %s, value = %s\n", record.topic(), record.partition(), record.offset(), record.key(), record.value()+" "+client_id);
//                Message_Exchg mt_msg = convertJSON(String.valueOf(record.value()));
//
//                String user = service.getService_id();
//                String content = mt_msg.getContent();
//                String receiver = mt_msg.getReceiver();
//                String sender = mt_msg.getSender();
//
//                List<Service> listservices = Service_listener.getSMS_ServiceMt(user, content, receiver, sender, config.getService_listener().getServices());
//
//                if (listservices.size() > 0) {
//
//                    List<String> response = senderGateway.sendSMS(user, sender, receiver, content,client_id);
//
//                    if (response != null) {
//                        logger.info("SUCCESS_SMS_CONTENT");
//                        logger.info("SUCCESS Submition of : " + sender + " --> " + receiver + " ( msg =" + content + " )");
//                    } else {
//
//                        logger.info("response is null: retry send SMS");
//
//                        response = senderGateway.sendSMS(user, sender, receiver, content,client_id);
//
//                        logger.info("SUCCESS_SMS_CONTENT");
//                        logger.info("SUCCESS Submition of : " + sender + " --> " + receiver + " ( msg =" + content + " )");
//                    }
//
//                } else {
//                    logger.error("ERROR_SMS_SYNTAX");
//                    logger.error("Wrong Submition of : " + sender + " --> " + receiver + " ( msg =" + content + " )");
//                }
//
//            }
//
//            consumer.commitSync();
//        }
//
//    }
//
//    private static class TestConsumerRebalanceListener implements ConsumerRebalanceListener {
//
//        @Override
//        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
//            logger.info("Called onPartitionsRevoked with partitions:" + partitions);
//        }
//
//        @Override
//        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
//            logger.info("Called onPartitionsAssigned with partitions:" + partitions);
//        }
//    }
//
//    private Message_Exchg convertJSON(String msg_json) {
//        Message_Exchg result = null;
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            result = mapper.readValue(msg_json, Message_Exchg.class);
//        } catch (IOException e) {
//            logger.error("cannot convert message to Oject", e);
//        }
//        return result;
//    }
//
//    public static void executeRunnables(final ExecutorService service, List<Runnable> runnables) {
//
//        for (Runnable r : runnables) {
//            service.execute(r);
//        }
//        //On ferme l'executor une fois les taches finies
//        //En effet shutdown va attendre la fin d'exécution des tâches
//        service.shutdown();
//    }
//
//}
