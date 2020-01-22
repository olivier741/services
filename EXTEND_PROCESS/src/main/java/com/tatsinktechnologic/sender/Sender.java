/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.sender;

import com.tatsinktechnologic.beans.Process_Request;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import javax.persistence.EntityManagerFactory;
import org.apache.log4j.Logger;


/**
 *
 * @author olivier
 */
public class Sender implements Runnable{
private static Logger logger = Logger.getLogger(Sender.class);
    
    private EntityManagerFactory emf;
    private int sleep_duration;
  
    private static BlockingQueue<Process_Request> reg_queue;


    public static void addMo_Queue(Process_Request process_req){
        try {
            reg_queue.put(process_req);
            logger.info("ADD message in the queue :"+ process_req);
        } catch (InterruptedException e) {
            logger.error("Error to add in reg_queue :"+ process_req,e);
        }

    }
  


    public Sender(EntityManagerFactory emf,BlockingQueue<Process_Request> reg_queue,int id,int numberRecord,String topic,int sleep_duration) {
        this.emf = emf;
        this.reg_queue=reg_queue;
        this.sleep_duration= sleep_duration;
    }
    
    @Override
    public void run() {
       
        logger.info("################################## START SENDER ###########################");
        while (true){
            // Removing an element from the Queue using poll()
            // The poll() method returns null if the Queue is empty.
            Process_Request process_mo = null;
             try{
                //consuming messages 
                process_mo = reg_queue.take();
                logger.info("Get message in the sender queue :"+ process_mo);
                logger.info("Sender Queue size :"+ reg_queue.size());
            } catch (InterruptedException e) {
                logger.error("Error to Get in reg_queue :"+ process_mo,e);
            }

             
            if (process_mo != null){
                
            }else{
                
            }
            
          

            try {
                Thread.sleep(sleep_duration);
            } catch (Exception e) {
            }

        }
        
        
    }
     public static void executeRunnables(final ExecutorService service, List<Runnable> runnables){
                //On exécute chaque "Runnable" de la liste "runnables"
		for(Runnable r : runnables){

			service.execute(r);
		}
		service.shutdown();
	}
}
