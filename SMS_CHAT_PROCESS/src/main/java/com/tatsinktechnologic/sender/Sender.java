/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.sender;

import com.tatsinktechnologic.beans.Message_Exchg;
import com.tatsinktechnologic.beans.Process_Request;
import com.tatsinktechnologic.loadconfig.Load_Configuration;
import com.tatsinktechnologic.util.ConverterXML_JSON;
import com.tatsinktechnologic.xml.Application;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import javax.persistence.EntityManagerFactory;
import org.apache.commons.lang.SerializationUtils;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

/**
 *
 * @author olivier
 */
public class Sender implements Runnable {

    private static Logger logger = Logger.getLogger(Sender.class);

    private EntityManagerFactory emf;
    private int sleep_duration;
    private Load_Configuration xmlConfig;
    private Application app_conf;
    private Properties product_props;
    private Producer<String, String> producer;
    private ProducerCallback callback;
    private ProducerRecord<String, String> kafka_data;

    private static BlockingQueue<Process_Request> send_queue;

    public static void addMo_Queue(Process_Request process_req) {
        try {
            send_queue.put(process_req);
            logger.info("ADD message in the queue :" + process_req);
        } catch (InterruptedException e) {
            logger.error("Error to add in reg_queue :" + process_req, e);
        }
    }

    public Sender(EntityManagerFactory emf, BlockingQueue<Process_Request> send_queue, int id, int numberRecord, String topic, int sleep_duration) {
        this.emf = emf;
        this.send_queue = send_queue;
        this.sleep_duration = sleep_duration;
        this.xmlConfig = Load_Configuration.getConfigurationLoader();
        this.app_conf = xmlConfig.getApp_conf();
        this.product_props =(Properties)SerializationUtils.clone(xmlConfig.getProducerProps());

        String new_client_id = product_props.getProperty("client.id");
        new_client_id = new_client_id + "_" + id;
        product_props.put("client.id", new_client_id);

        this.producer = new KafkaProducer<String, String>(product_props);
        this.callback = new ProducerCallback();
    }

    @Override
    public void run() {

        logger.info("################################## START SENDER ###########################");
        String topic_kafka = app_conf.getKafka_conf().getProducer_topic();

        while (true) {
            // Removing an element from the Queue using poll()
            // The poll() method returns null if the Queue is empty.
            Process_Request process_mo = null;
            try {
                //consuming messages 
                process_mo = send_queue.take();
                logger.info("Get message in the sender queue :" + process_mo);
                logger.info("Sender Queue size :" + send_queue.size());
            } catch (InterruptedException e) {
                logger.error("Error to Get in reg_queue :" + process_mo, e);
            }

            if (process_mo != null) {

                String sender = process_mo.getChannel();
                String receiver = process_mo.getMsisdn();
                String content = process_mo.getContent();

                Message_Exchg msg_exch = new Message_Exchg(null, null, null, sender, receiver, content, null);

                String message_send = ConverterXML_JSON.convertMsgExchToJson(msg_exch);
                kafka_data = new ProducerRecord<String, String>(topic_kafka, message_send);
                producer.send(kafka_data, callback);
            } else {

            }

            try {
                Thread.sleep(sleep_duration);
            } catch (Exception e) {
            }

        }

    }

    private class ProducerCallback implements Callback {

        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null) {
                logger.error("Error while producing message to topic :" + recordMetadata, e);
            } else {
                String message = String.format("Producer client ID :  " + product_props.getProperty("client.id") + " -- Topic: %s -- Partition: %s -- offset: %s", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
                logger.info(message);
            }
        }
    }

    public static void executeRunnables(final ExecutorService service, List<Runnable> runnables) {
        //On exécute chaque "Runnable" de la liste "runnables"
        for (Runnable r : runnables) {

            service.execute(r);
        }
        service.shutdown();
    }
}
