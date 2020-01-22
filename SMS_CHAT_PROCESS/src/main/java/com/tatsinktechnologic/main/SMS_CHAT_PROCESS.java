/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.main;

import com.tatsinktechnologic.loadconfig.Load_Configuration;
import com.tatsinktechnologic.receiver.Receiver;
import com.tatsinktechnologic.beans.Process_Request;
import com.tatsinktechnologic.process.SMSProcessManyToMany;
import com.tatsinktechnologic.process.SMSProcessOneToMany;
import com.tatsinktechnologic.process.SMSProcessOneToOne;
import com.tatsinktechnologic.process.SMSProcess_LoadMo_Push;
import com.tatsinktechnologic.process.SMSProcess_Notification;
import com.tatsinktechnologic.process.SMSProcess_Push;
import com.tatsinktechnologic.sender.Sender;
import com.tatsinktechnologic.xml.Application;
import com.tatsinktechnologic.xml.Chat_Process;
import com.tatsinktechnologic.xml.Chat_Type;
import com.tatsinktechnologic.xml.Kafka_Conf;
import com.tatsinktechnologic.xml.ReceiverThread_Conf;
import com.tatsinktechnologic.xml.SenderThread_Conf;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.EntityManagerFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author olivier.tatsinkou
 */
public class SMS_CHAT_PROCESS {

    /**
     * @param args the command line arguments
     */
    private static Logger logger = Logger.getLogger(SMS_CHAT_PROCESS.class);

    private static EntityManagerFactory emf;
    private static SenderThread_Conf sendthread_conf;
    private static ReceiverThread_Conf receiverthead_conf;
    private static List<Chat_Process> listchatProc;

    private static Kafka_Conf kafka_conf;
    private static Application app_conf;
    private static BlockingQueue<Process_Request> send_queue;
    private static List<BlockingQueue<Process_Request>> listChat_pro_queue;
//    private static BlockingQueue<Process_Request> guide_queue;
//    private static BlockingQueue<Process_Request> check_queue;

    private static Load_Configuration commonConfig = Load_Configuration.getConfigurationLoader();

    public static void main(String[] args) {
        // TODO code application logic here
        PropertyConfigurator.configure("etc" + File.separator + "log4j.cfg");
        logger.info("Load log4j config file done.");

        app_conf = commonConfig.getApp_conf();

        sendthread_conf = app_conf.getSender_thread();
        receiverthead_conf = app_conf.getReceiver_thread();
        listchatProc = app_conf.getListChat_Process();
        kafka_conf = app_conf.getKafka_conf();

        emf = commonConfig.getEmf();

        List<Runnable> receiver_runnables = new ArrayList<Runnable>();
        List<Runnable> sender_runnables = new ArrayList<Runnable>();
        List<Runnable> loadMo_Push_runnables = new ArrayList<Runnable>();
        List<List<Runnable>> listSMSProcess_runnable = new ArrayList<List<Runnable>>();

        int numberRecord = kafka_conf.getNumberRecord();
        String prod_topic = kafka_conf.getProducer_topic();

        int send_thread_num = sendthread_conf.getNumberThread();
        int send_thread_pool = sendthread_conf.getThreadPool();
        int send_sleep_duration = sendthread_conf.getSleep_duration();
        int send_maxQueue = sendthread_conf.getMaxQueue();

        int receive_thread_num = receiverthead_conf.getNumberThread();
        int receive_thread_pool = receiverthead_conf.getThreadPool();

        listChat_pro_queue = new ArrayList<BlockingQueue<Process_Request>>();
        

        for (Chat_Process chatProc : listchatProc) {
            int process_num = chatProc.getNumberThread();
            int process_pool = chatProc.getThreadPool();
            int process_maxQueue = chatProc.getMaxQueue();
            Chat_Type chat_type = chatProc.getType();

            if (process_num <= 1) {
                process_num = 2;
                process_pool = 2;
            }

            BlockingQueue<Process_Request> chatProc_queue = new ArrayBlockingQueue<>(process_maxQueue);
            listChat_pro_queue.add(chatProc_queue);
            List<Runnable> checkProcess_runnables = new ArrayList<Runnable>();

            // process chat
            switch (chat_type) {
                case ONE_TO_ONE:
                    for (int i = 0; i < process_num; i++) {
                        checkProcess_runnables.add(new SMSProcessOneToOne(chatProc_queue, chatProc, emf));
                    }

                    ExecutorService process_Execute_chat_onetoone = Executors.newFixedThreadPool(process_pool);
                    SMSProcessOneToOne.executeRunnables(process_Execute_chat_onetoone, checkProcess_runnables);
                    break;

                case MANY_TO_ONE:
                    for (int i = 0; i < process_num; i++) {
                        checkProcess_runnables.add(new SMSProcessOneToMany(chatProc_queue, chatProc, emf));
                    }

                    ExecutorService process_Execute_chat_manytoone = Executors.newFixedThreadPool(process_pool);
                    SMSProcessOneToOne.executeRunnables(process_Execute_chat_manytoone, checkProcess_runnables);
                    break;

                case MANY_TO_MANY:
                    for (int i = 0; i < process_num; i++) {
                        checkProcess_runnables.add(new SMSProcessManyToMany(chatProc_queue, chatProc, emf));
                    }

                    ExecutorService process_Execute_chat_manytomany = Executors.newFixedThreadPool(process_pool);
                    SMSProcessOneToOne.executeRunnables(process_Execute_chat_manytomany, checkProcess_runnables);
                    break;

                case NOTIFICATION:
                    for (int i = 0; i < process_num; i++) {
                        checkProcess_runnables.add(new SMSProcess_Notification(i, process_maxQueue, chatProc, emf));
                    }

                    ExecutorService process_Execute_notification = Executors.newFixedThreadPool(process_pool);
                    SMSProcessOneToOne.executeRunnables(process_Execute_notification, checkProcess_runnables);
                    break;
                case PUSH:
                    for (int i = 0; i < process_num; i++) {
                        checkProcess_runnables.add(new SMSProcess_Push(i, process_maxQueue, chatProc, emf));
                    }

                    ExecutorService process_Execute_push = Executors.newFixedThreadPool(process_pool);
                    SMSProcessOneToOne.executeRunnables(process_Execute_push, checkProcess_runnables);
                    break;

            }
            listSMSProcess_runnable.add(checkProcess_runnables);
        }

        send_queue = new ArrayBlockingQueue<>(send_maxQueue);

        // sender
        if (send_thread_num <= 1) {
            send_thread_num = 2;
            send_thread_pool = 2;
        }
        for (int i = 0; i < send_thread_num; i++) {
            sender_runnables.add(new Sender(emf, send_queue, i, numberRecord, prod_topic, send_sleep_duration));
        }
        ExecutorService send_Execute = Executors.newFixedThreadPool(send_thread_pool);
        Sender.executeRunnables(send_Execute, sender_runnables);

        // receiver
        if (receive_thread_num <= 1) {
            receive_thread_num = 2;
            receive_thread_pool = 2;
        }
        for (int i = 0; i < receive_thread_num; i++) {
            receiver_runnables.add(new Receiver(i));
        }
        ExecutorService receiver_execute = Executors.newFixedThreadPool(receive_thread_pool);
        Receiver.executeRunnables(receiver_execute, receiver_runnables);

        // load Mo_Push
        loadMo_Push_runnables.add(new SMSProcess_LoadMo_Push());
        
        ExecutorService loadMo_Push_execute = Executors.newFixedThreadPool(1);
        SMSProcess_LoadMo_Push.executeRunnables(loadMo_Push_execute, loadMo_Push_runnables);
    }

}
