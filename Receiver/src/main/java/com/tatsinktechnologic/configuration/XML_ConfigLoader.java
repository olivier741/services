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
package com.tatsinktechnologic.configuration;

import com.tatsinktechnologic.xml.receiver_listener.Http_Listener;
import com.tatsinktechnologic.xml.receiver_listener.MoFilterSMS;
import com.tatsinktechnologic.xml.receiver_listener.Receiver_Listener;
import com.tatsinktechnologic.xml.smsgw_listener.Smsgw_Listener;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author olivier.tatsinkou
 */
public class XML_ConfigLoader {
     private static Logger logger = LogManager.getLogger(XML_ConfigLoader.class);
     
    private Smsgw_Listener smsgw_listener;
    private Receiver_Listener receiver_listener ;
    private Http_Listener http;
    private MoFilterSMS mofilter;
    private EntityManagerFactory emf;
    private static final String PERSISTENCE_UNIT_NAME= "DB_PU1";

    private XML_ConfigLoader() {
        
        try {
             Configurator.initialize(null, "etc" + File.separator + "log4j2.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Load log4j2.xml config file done.");
        
       try {
            // set up new properties object from file "jpa.properties"
            FileInputStream propFile = new FileInputStream("etc" + File.separator + "jpa.properties");
            Properties p = new Properties(System.getProperties());
            p.load(propFile);

            // set the system properties
            System.setProperties(p);

        } catch (java.io.FileNotFoundException e) {
            logger.error("Can't find jpa.properties");
        } catch (java.io.IOException e) {
            logger.error("I/O failed.");
        }

        File file_receiver_listener = new File("etc" + File.separator + "receiver_listener.xml");
        File file_smsgw_listener = new File("etc" + File.separator + "smsgw_listener.xml");
   
        Serializer serializer_receiver_listener = new Persister();
        Serializer serializer_smsgw_listener = new Persister();

        // get configuration of receiver_listener.xml
        try {
            receiver_listener = serializer_receiver_listener.read(Receiver_Listener.class, file_receiver_listener);
            http=receiver_listener.getHttp_listener();
            mofilter=receiver_listener.getMofilter();
            logger.info("successfull load : etc" + File.separator + "receiver_listener.xml");
        } catch (Exception e) {
            logger.error("ERROR in config file receiver_listener.xml",e);
        }

         // get configuration of smsgw_listener.xml
        try {
            smsgw_listener = serializer_smsgw_listener.read(Smsgw_Listener.class, file_smsgw_listener);
            logger.info("successfull load : etc" + File.separator + "smsgw_listener.xml");
        } catch (Exception e) {
            logger.error("ERROR in config file smsgw_listener.xml",e);
        }
        
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

    }
    
    
    private static class SingletonXMLConfig{
        private static final XML_ConfigLoader _xmlconfigLoad = new XML_ConfigLoader();
    }
    
    public static XML_ConfigLoader getConfigurationLoader(){
        return SingletonXMLConfig._xmlconfigLoad;
    }

    public Smsgw_Listener getSmsgw_listener() {
        return smsgw_listener;
    }

    public Receiver_Listener getReceiver_listener() {
        return receiver_listener;
    }

    public MoFilterSMS getMofilter() {
        return mofilter;
    }

    public Http_Listener getHttp() {
        return http;
    }

    public EntityManagerFactory getEmf() {
        return emf;
    }

    
}
