/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.tcp.main;


import com.tatsinktechnologic.beans_entity.ProcessKey;
import com.tatsinktechnologic.beans_entity.Process_Unit;
import com.tatsinktechnologic.configuration.XML_ConfigLoader;
import com.tatsinktechnologic.dao.*;
import com.tatsinktechnologic.dao.exceptions.PreexistingEntityException;
import com.tatsinktechnologic.ws.server.SOAP_HttpServerManagement;
import com.tatsinktechnologic.xml.receiver_listener.Receiver_Listener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class Receiver {

    /**
     * @param args the command line arguments
     */
    
    private static EntityManagerFactory emf;
    private static Logger logger = LogManager.getLogger(Receiver.class);
    private static Receiver_Listener receiver_listener;
    private static XML_ConfigLoader xmlConfig;
    
    public static void main(String[] args)  throws Throwable{
        // TODO code application logic here
        xmlConfig= XML_ConfigLoader.getConfigurationLoader(); 
        receiver_listener= xmlConfig.getReceiver_listener();

        EntityManagerFactory emf = xmlConfig.getEmf();

        InetAddress address = gethostName();

        String application_node = receiver_listener.getNode_name();
        
        logger.info("node_name = "+application_node);

        Process_UnitJpaController process_unitController = new Process_UnitJpaController(emf);
        
        ProcessKey processkey = new ProcessKey();
        processkey.setNode_name(application_node);
        processkey.setServer_name(address.getHostName());
        
        Process_Unit process_unit = new Process_Unit();
        process_unit.setProcess_unit_id(processkey);
        process_unit.setIP_address(address.getHostAddress());
        
        try {
             process_unitController.create(process_unit);
         } catch (PreexistingEntityException e) { 
             logger.error("ALREADY EXIST : "+process_unit);
             process_unitController.edit(process_unit);
        } catch (Exception e) {
            logger.error("cannot insert : "+process_unit);
        }
       
        List<Runnable> runnables = new ArrayList<Runnable>();
        ExecutorService execute = Executors.newSingleThreadExecutor();
        runnables.add(new SOAP_HttpServerManagement(process_unit));
        SOAP_HttpServerManagement.executeRunnables(execute, runnables);
        logger.info("Receiver is launch ........");
              
    }
    
  
   private static InetAddress gethostName(){
       
       InetAddress addr = null;

            try
            {
                addr = InetAddress.getLocalHost();
            }
            catch (UnknownHostException ex)
            {
                logger.error("Hostname can not be resolved",ex);
            }
       return addr;
   }

    
}
