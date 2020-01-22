/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.main;

import com.tatsinktechnologic.beans.Process_Request;
import com.tatsinktechnologic.loadconfig.Load_Configuration;
import com.tatsinktechnologic.process.Process_Extend;
import com.tatsinktechnologic.sender.Sender;
import com.tatsinktechnologic.xml.Application;
import com.tatsinktechnologic.xml.Kafka_Conf;
import com.tatsinktechnologic.xml.ProcessThread_Ext;
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
 * @author olivier
 */
public class Start_ExtendCharge {

    /**
     * @param args the command line arguments
     */
    
    private static Logger logger = Logger.getLogger(Start_ExtendCharge.class);

    private static EntityManagerFactory emf;
    private static SenderThread_Conf sendthread_conf;
    private static ProcessThread_Ext extendthread_conf;

    private static Kafka_Conf kafka_conf;
    private static Application app_conf;
    private static BlockingQueue<Process_Request> send_queue;
    
     private static Load_Configuration commonConfig = Load_Configuration.getConfigurationLoader();

    public static void main(String[] args) {
        // TODO code application logic here
        
        PropertyConfigurator.configure("etc" + File.separator + "log4j.cfg");
        logger.info("Load log4j config file done.");
        
       
        app_conf = commonConfig.getApp_conf();
        sendthread_conf= app_conf.getSender_thread();
        extendthread_conf = app_conf.getProcess_thread_extend();
        kafka_conf = app_conf.getKafka_conf();
           
        emf = commonConfig.getEmf();
        
        int numberRecord = kafka_conf.getNumberRecord();
        String prod_topic = kafka_conf.getProducer_topic();

        int send_thread_num =sendthread_conf.getNumberThread();
        int send_thread_pool=sendthread_conf.getThreadPool();
        int send_sleep_duration = sendthread_conf.getSleep_duration();
        int send_maxQueue = sendthread_conf.getMaxQueue();

        
        int extendProcess_num =extendthread_conf.getNumberThread();
        int extendProcess_pool=extendthread_conf.getThreadPool();
        int extendProcess_maxQueue = extendthread_conf.getMaxQueue();
        
        send_queue = new ArrayBlockingQueue<>(send_maxQueue);

        List<Runnable> sender_runnables =new ArrayList<Runnable>();
        List<Runnable> extendProcess_runnables =new ArrayList<Runnable>();

       // sender
        for(int i = 0 ; i<send_thread_num; i++){
            sender_runnables.add(new Sender(emf,send_queue,i,numberRecord,prod_topic,send_sleep_duration));
        }   
        ExecutorService send_Execute = Executors.newFixedThreadPool(send_thread_pool);
	Sender.executeRunnables(send_Execute, sender_runnables);
        
        

        // process reg
        for(int i = 0 ; i<extendProcess_num; i++){
            extendProcess_runnables.add(new Process_Extend(extendProcess_maxQueue,i));
        }   
        ExecutorService process_Execute_reg = Executors.newFixedThreadPool(extendProcess_pool);
	Process_Extend.executeRunnables(process_Execute_reg, extendProcess_runnables);
    }
    
}
