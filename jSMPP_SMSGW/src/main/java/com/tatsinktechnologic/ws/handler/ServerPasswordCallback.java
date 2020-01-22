/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ws.handler;

import com.tatsinktechnologic.configuration.XML_ConfigLoader;
import com.tatsinktechnologic.xml.user_listener.Send_User_Connection;
import com.tatsinktechnologic.xml.user_listener.Sender_User_Listener;
import java.io.File;
import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
/**
 *
 * @author olivier.tatsinkou
 */
public class ServerPasswordCallback implements CallbackHandler {
   
    private static Logger logger = LogManager.getLogger(ServerPasswordCallback.class);
    private static Send_User_Connection user_conn = null;
    

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

        XML_ConfigLoader xmlConfig = XML_ConfigLoader.getConfigurationLoader();
        Sender_User_Listener send_user_listener = xmlConfig.getSend_user_listener();
        
        for (int i = 0; i < callbacks.length; i++) {

            WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];

            if (pc.getUsage() == WSPasswordCallback.USERNAME_TOKEN) {

                //You must set a password for the user, WSS4J would compare
                //the password with the password sent by client, if they match
                //message will be processed. Any mismatch in password will result in a SOAP Fault.

                if (send_user_listener.selectUser_con(pc.getIdentifier())!=null){
                  user_conn = send_user_listener.selectUser_con(pc.getIdentifier());
                }

                if(user_conn!= null && pc.getIdentifier().equalsIgnoreCase(user_conn.getUser())) {
                    pc.setPassword(user_conn.getPassword());
                }
                
            }
        }

    }

    public static Send_User_Connection getUser_conn() {
        return user_conn;
    }
    
    
}
