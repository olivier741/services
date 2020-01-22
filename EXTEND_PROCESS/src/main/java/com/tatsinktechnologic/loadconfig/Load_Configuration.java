/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.loadconfig;

import com.tatsinktechnologic.beans_entity.Command;
import com.tatsinktechnologic.beans_entity.Notification_Conf;
import com.tatsinktechnologic.beans_entity.Product;
import com.tatsinktechnologic.dao_repository.CommandJpaController;
import com.tatsinktechnologic.dao_repository.Notification_ConfJpaController;
import com.tatsinktechnologic.dao_repository.ProductJpaController;
import com.tatsinktechnologic.xml.Application;
import com.tatsinktechnologic.xml.Charging_Api;
import com.tatsinktechnologic.xml.View_Api;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.apache.log4j.Logger;

/**
 *
 * @author olivier
 */
public class Load_Configuration implements Serializable{

    private static Logger logger = Logger.getLogger(Load_Configuration.class);
    
    private EntityManagerFactory emf;
    private Application app_conf;
    private Charging_Api charging_api;
    private View_Api view_api;
    private HashMap<String,Command> SETCOMMAND = new HashMap<String,Command>();
    private HashMap<String,Notification_Conf> SETNOTIFICATION = new HashMap<String,Notification_Conf>();
    private HashMap<String,Product> SETPRODUCT= new HashMap<String,Product>();
    private Properties consummerProps= new Properties();
    private Properties producerProps= new Properties();
    
    private Load_Configuration() {

        try {
             consummerProps.load(new FileInputStream("etc" + File.separator + "consumer.properties"));
         } catch (IOException e) {
             logger.error("cannot load consumer.properties config file", e);
         }
        
        
         try {
             producerProps.load(new FileInputStream("etc" + File.separator + "producer.properties"));
         } catch (IOException e) {
             logger.error("cannot load consumer.properties config file", e);
         }
        
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
        
        File file_app_listener = new File("etc" + File.separator + "app_config.xml");
        File file_charge_listener = new File("etc" + File.separator + "api_gateway.xml");
        File file_view_listener = new File("etc" + File.separator + "api_viewinfo.xml");
        
        Serializer serializer_app_listener = new Persister();
        Serializer serializer_charge_listener = new Persister();
        Serializer serializer_view_listener = new Persister();
        
        try {
            app_conf = serializer_app_listener.read(Application.class, file_app_listener);
            logger.info("successfull load : etc" + File.separator + "app_config.xml");
        } catch (Exception e) {
            logger.error("ERROR in config file http_listener.xml",e);
        }
        
        try {
            charging_api = serializer_charge_listener.read(Charging_Api.class, file_charge_listener);
            
            if (charging_api!=null){
                logger.info("successfull load : etc" + File.separator + "api_charging.xml");
            }else{
                logger.error("CANNOT load : etc" + File.separator + "api_charging.xml");
            }
            
        } catch (Exception e) {
            logger.error("ERROR in config file api_charging.xml",e);
        }
        
         try {
            view_api = serializer_view_listener.read(View_Api.class, file_view_listener);
            logger.info("successfull load : etc" + File.separator + "api_viewinfo.xml");
        } catch (Exception e) {
            logger.error("ERROR in config file api_viewinfo.xml",e);
        }
        
        emf = Persistence.createEntityManagerFactory(database_prop.getProperty("DB_PU1.jpa.datasource"));
        getCommand(emf);
        getNotificationConf(emf);
        getProduct(emf);
        
    }
     
    
    private static class SingletonCommunDAO{
        private static final Load_Configuration _communDAO_Repository= new Load_Configuration();
    }
    
    public static Load_Configuration getConfigurationLoader(){
        return SingletonCommunDAO._communDAO_Repository;
    }

    public EntityManagerFactory getEmf() {
        return emf;
    }

    public Application getApp_conf() {
        return app_conf;
    }

    public Charging_Api getCharging_api() {
        return charging_api;
    }

    public View_Api getView_api() {
        return view_api;
    }

    public HashMap<String, Command> getSETCOMMAND() {
        return SETCOMMAND;
    }


    public HashMap<String, Notification_Conf> getSETNOTIFICATION() {
        return SETNOTIFICATION;
    }

    public HashMap<String, Product> getSETPRODUCT() {
        return SETPRODUCT;
    }


    public Properties getConsummerProps() {
        return consummerProps;
    }

    public Properties getProducerProps() {
        return producerProps;
    }
     
    
    
    private void getNotificationConf(EntityManagerFactory emf){
        Notification_ConfJpaController notifControler = new Notification_ConfJpaController(emf);
        List<Notification_Conf> listNotif = notifControler.findNotification_ConfEntities();
        SETNOTIFICATION.clear();
        for (Notification_Conf notif : listNotif){
          SETNOTIFICATION.put(notif.getNofication_name(), notif);
        }
    }

    private  void getCommand(EntityManagerFactory emf){
        CommandJpaController cmdControler = new CommandJpaController(emf);
        List<Command> listCommand = cmdControler.findCommandEntities();
        SETCOMMAND.clear();
        for (Command comd : listCommand){
            SETCOMMAND.put(comd.getCommand_code(), comd);
        }  
    }     
    
    private  void getProduct(EntityManagerFactory emf){
        ProductJpaController productControler = new ProductJpaController(emf);
        List<Product> listProduct = productControler.findProductEntities();
        SETPRODUCT.clear();
        for (Product prod : listProduct){
            SETPRODUCT.put(prod.getProduct_code(), prod);
        }  
    }   
     
}
