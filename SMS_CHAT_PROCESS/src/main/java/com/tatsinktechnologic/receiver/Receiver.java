/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.receiver;

import com.tatsinktechnologic.dao_commun_repository.CommunRepository;
import com.tatsinktechnologic.loadconfig.Load_Configuration;
import com.tatsinktechnologic.beans.Message_Exchg;
import com.tatsinktechnologic.beans.Process_Request;
import com.tatsinktechnologic.entity.register.Register;
import com.tatsinktechnologic.process.SMSProcessManyToMany;
import com.tatsinktechnologic.process.SMSProcessOneToMany;
import com.tatsinktechnologic.process.SMSProcessOneToOne;
import com.tatsinktechnologic.util.ConverterXML_JSON;
import com.tatsinktechnologic.xml.Application;
import com.tatsinktechnologic.xml.Chat_Process;
import com.tatsinktechnologic.xml.Chat_Type;
import com.tatsinktechnologic.xml.Kafka_Conf;
import com.tatsinktechnologic.xml.ReceiverThread_Conf;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
import javax.persistence.EntityManagerFactory;
import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

/**
 *
 * @author olivier
 */
public class Receiver implements Runnable {

    private Logger logger = Logger.getLogger(Receiver.class);
    private EntityManagerFactory emf;
    private Properties consumerprops;
    private int id;
    private int numberRecord;
    private String client_id;
    private String topic;
    private int sleep_duration;
    private static Kafka_Conf kafka_conf;
    private static Application app_conf;
    private static ReceiverThread_Conf receiverthead_conf;
    private InetAddress address;
    private CommunRepository communRepo;
    private static Load_Configuration commonConfig = Load_Configuration.getConfigurationLoader();
    private KafkaConsumer<byte[], byte[]> consumer;
    private ReceiverConsumerRebalanceListener rebalanceListener;
    

    public Receiver(int id) {
        this.emf = commonConfig.getEmf();
        this.id = id;
        this.app_conf = commonConfig.getApp_conf();
        this.kafka_conf = app_conf.getKafka_conf();
        this.topic = kafka_conf.getConsumer_topic();
        this.numberRecord = kafka_conf.getNumberRecord();
        this.receiverthead_conf = app_conf.getReceiver_thread();
        this.sleep_duration = receiverthead_conf.getSleep_duration();

        consumerprops = (Properties)SerializationUtils.clone(commonConfig.getConsummerProps());

        
        
        String new_client_id = consumerprops.getProperty("client.id");
        this.client_id = new_client_id + "_" + id;
        consumerprops.put("client.id", client_id);
        address = gethostName();
        communRepo = new CommunRepository(emf);

        this.consumer = new KafkaConsumer<>(consumerprops);
        this.rebalanceListener = new ReceiverConsumerRebalanceListener();
        consumer.subscribe(Collections.singletonList(topic), rebalanceListener);
    }

    @Override
    public void run() {

        logger.info("################################## RECEIVE NEW CHAT ###########################");

        while (true) {
            ConsumerRecords<byte[], byte[]> records = consumer.poll(numberRecord);

            for (ConsumerRecord<byte[], byte[]> record : records) {

                String word = String.valueOf(record.value());
                Timestamp receive_time = new Timestamp(System.currentTimeMillis());

                logger.info("request receive   : " + word);
                Message_Exchg msg_exch = ConverterXML_JSON.convertJsonToMsgExch(word);

                String transaction_id = msg_exch.getTransaction_id();
                String content = msg_exch.getContent().toUpperCase().trim();
                String receiver = msg_exch.getReceiver().toUpperCase().trim();
                String msisdn = msg_exch.getSender().toUpperCase().trim();
                String exchange_mode = msg_exch.getExchange_mode().toUpperCase().trim();

                logger.info("transaction_id   : " + transaction_id);
                logger.info("content send by customer   : " + content);
                logger.info("customer phone number      : " + msisdn);
                logger.info("short code                 : " + receiver);

                List<Chat_Process> listChatProc = Application.getChat_Process(receiver, app_conf.getListChat_Process());

                if (listChatProc != null && !listChatProc.isEmpty()) {
                    Chat_Process chat_process = listChatProc.get(0);
                    Chat_Type chat_type = chat_process.getType();

                    Pattern ptn = Pattern.compile("\\|");
                    List<String> list_product = Arrays.asList(ptn.split(chat_process.getList_product_name()));

                    List<Register> listreg = communRepo.getListRegister(msisdn, receive_time, list_product);

                    if (listreg != null && !listreg.isEmpty()) {
                        Process_Request process_req = new Process_Request();

                        process_req.setTransaction_id(transaction_id);
                        process_req.setReceiver(receiver);
                        process_req.setMsisdn(msisdn);
                        process_req.setContent(content);
                        process_req.setReceive_time(receive_time);
                        process_req.setExchange_mode(exchange_mode);

                        switch (chat_type) {
                            case ONE_TO_ONE:
                                process_req.setNotification_code("ONE_TO_ONE-" + receiver);
                                SMSProcessOneToOne.addMo_Queue(process_req);
                                logger.info("Emitte to SMS-ONE_TO_ONE : " + process_req);
                                break;
                            case MANY_TO_ONE:
                                process_req.setNotification_code("MANY_TO_ONE-" + receiver);
                                SMSProcessOneToMany.addMo_Queue(process_req);
                                logger.info("Emitte to SMS-MANY_TO_ONE : " + process_req);
                                break;
                            case MANY_TO_MANY:
                                process_req.setNotification_code("MANY_TO_MANY-" + receiver);
                                SMSProcessManyToMany.addMo_Queue(process_req);
                                logger.info("Emitte to SMS-MANY_TO_MANY : " + process_req);
                                break;
                        }
                    }

                }

            }

            consumer.commitSync();

            try {
                Thread.sleep(sleep_duration);
            } catch (Exception e) {

            }
        }

    }

    private static class ReceiverConsumerRebalanceListener implements ConsumerRebalanceListener {

        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            System.out.println("Called onPartitionsRevoked with partitions:" + partitions);
        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
            System.out.println("Called onPartitionsAssigned with partitions:" + partitions);
        }
    }

    private InetAddress gethostName() {

        InetAddress addr = null;

        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            logger.error("Hostname can not be resolved", ex);
        }
        return addr;
    }

    public String convertDate_String(Timestamp date, String format) {

        DateFormat dateFormat = new SimpleDateFormat(format);

        //to convert Date to String, use format method of SimpleDateFormat class.
        String strDate = dateFormat.format(date);

        return strDate;
    }

    public static void executeRunnables(final ExecutorService service, List<Runnable> runnables) {
        //On exécute chaque "Runnable" de la liste "runnables"
        for (Runnable r : runnables) {

            service.execute(r);
        }
        service.shutdown();
    }

}
