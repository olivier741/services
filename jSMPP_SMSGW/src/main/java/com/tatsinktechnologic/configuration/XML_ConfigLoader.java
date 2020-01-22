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

import com.tatsinktechnologic.xml.service_listener.Http_Listener;
import com.tatsinktechnologic.xml.service_listener.Service_listener;
import com.tatsinktechnologic.xml.user_listener.Receiver_User_Listener;
import com.tatsinktechnologic.xml.user_listener.Sender_User_Listener;
import java.io.File;
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
     
    private Service_listener service_listener;
    private Sender_User_Listener send_user_listener ;
    private Receiver_User_Listener rcv_user_listener ;
    private Http_Listener http;

    private XML_ConfigLoader() {
        
        try {
             Configurator.initialize(null, "etc" + File.separator + "log4j2.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Load log4j2.xml config file done.");

        
        File file_service_listener = new File("etc" + File.separator + "service_listener.xml");
        File file_send_user_listener = new File("etc" + File.separator + "sender_user_listener.xml");
        File file_rcv_user_listener = new File("etc" + File.separator + "receiver_user_listener.xml");
   
        Serializer serializer_service_listener = new Persister();
        Serializer serializer_send_user_listener = new Persister();
        Serializer serializer_rcv_user_listener = new Persister();

        // get configuration of service_listener.xml
        try {
            service_listener = serializer_service_listener.read(Service_listener.class, file_service_listener);
            http=service_listener.getHttp_listener();
            logger.info("successfull load : etc" + File.separator + "service_listener.xml");
        } catch (Exception e) {
            logger.error("ERROR in config file service_listener.xml",e);
        }

         // get configuration of sender_user_listener.xml
        try {
            send_user_listener = serializer_send_user_listener.read(Sender_User_Listener.class, file_send_user_listener);
            logger.info("successfull load : etc" + File.separator + "sender_user_listener.xml");
        } catch (Exception e) {
            logger.error("ERROR in config file sender_user_listener.xml",e);
        }
        
         // get configuration of receiver_user_listener.xml
        try {
            rcv_user_listener = serializer_rcv_user_listener.read(Receiver_User_Listener.class, file_rcv_user_listener);
            logger.info("successfull load : etc" + File.separator + "receiver_user_listener.xml");
        } catch (Exception e) {
            logger.error("ERROR in config file receiver_user_listener.xml",e);
        }

    }
    
    
    private static class SingletonXMLConfig{
        private static final XML_ConfigLoader _xmlconfigLoad = new XML_ConfigLoader();
    }
    
    public static XML_ConfigLoader getConfigurationLoader(){
        return SingletonXMLConfig._xmlconfigLoad;
    }

    public Service_listener getService_listener() {
        return service_listener;
    }

    public Sender_User_Listener getSend_user_listener() {
        return send_user_listener;
    }

    public Receiver_User_Listener getRcv_user_listener() {
        return rcv_user_listener;
    }

    public Http_Listener getHttp() {
        return http;
    }
    
}
