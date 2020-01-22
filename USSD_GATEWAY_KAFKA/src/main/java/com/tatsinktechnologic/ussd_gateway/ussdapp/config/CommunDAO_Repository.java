/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.config;


import com.tatsinktechnologic.xml.Application;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author olivier
 */
public class CommunDAO_Repository implements Serializable{

    private static Logger logger = Logger.getLogger(CommunDAO_Repository.class);
    private EntityManagerFactory emf;
    private Application app_conf;
//    private HashMap<String,WS_AccessManagement> SETACCESSMANAGEMENT = new HashMap<String,WS_AccessManagement>();
    
    private CommunDAO_Repository() {

        Properties database_prop = null;
        try {
            // set up new properties object from file "jpa.properties"
            FileInputStream propFile = new FileInputStream("etc" + File.separator + "jpa.properties");
            database_prop = new Properties(System.getProperties());
            database_prop.load(propFile);

            // set the system properties
            System.setProperties(database_prop);

        } catch (java.io.FileNotFoundException e) {
            logger.error("Can't find jpa.properties");
        } catch (java.io.IOException e) {
            logger.error("I/O failed.");
        }
        
        File file_http_listener = new File("etc" + File.separator + "app_config.xml");
        
        Serializer serializer_http_listener = new Persister();
        
        try {
            app_conf = serializer_http_listener.read(Application.class, file_http_listener);
            logger.info("successfull load : etc" + File.separator + "app_config.xml");
        } catch (Exception e) {
            logger.error("ERROR in config file",e);
        }
 
        emf = Persistence.createEntityManagerFactory(database_prop.getProperty("DB_PU1.jpa.datasource"));
//        getAccessManagement(emf);
       
    }
     
    private static class SingletonCommunDAO{
        private static final CommunDAO_Repository _communDAO_Repository= new CommunDAO_Repository();
    }
    
    public static CommunDAO_Repository getConfigurationLoader(){
        return SingletonCommunDAO._communDAO_Repository;
    }

    public EntityManagerFactory getEmf() {
        return emf;
    }

    public Application getApp_conf() {
        return app_conf;
    }

//    public HashMap<String, WS_AccessManagement> getSETACCESSMANAGEMENT() {
//        return SETACCESSMANAGEMENT;
//    }
//
//    private  void getAccessManagement(EntityManagerFactory emf){
//        WS_AccessManagementJpaController accessMngControler = new WS_AccessManagementJpaController(emf);
//        List<WS_AccessManagement> listAccessMng = accessMngControler.findWS_AccessManagementEntities();
//        SETACCESSMANAGEMENT.clear();
//        for (WS_AccessManagement accessMng : listAccessMng){
//            SETACCESSMANAGEMENT.put(accessMng.getWs_client().getClient_name()+"_"+accessMng.getWs_block_api().getBlock_api_name(), accessMng);
//        }  
//    }     
//    
  
}
