/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ws.handler;

import com.tatsinktechnologic.configuration.XML_ConfigLoader;
import com.tatsinktechnologic.xml.smsgw_listener.SmsgwClient;
import com.tatsinktechnologic.xml.smsgw_listener.Smsgw_Listener;
import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.wss4j.common.ext.WSPasswordCallback;
/**
 *
 * @author olivier.tatsinkou
 */
public class ServerPasswordCallback implements CallbackHandler {
   
    private static Logger logger = LogManager.getLogger(ServerPasswordCallback.class);
    private static SmsgwClient smsgwclient = null;
    

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

        XML_ConfigLoader xmlConfig = XML_ConfigLoader.getConfigurationLoader();
        Smsgw_Listener smsgw_listener = xmlConfig.getSmsgw_listener();
        
        for (int i = 0; i < callbacks.length; i++) {

            WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];

            if (pc.getUsage() == WSPasswordCallback.USERNAME_TOKEN) {

                //You must set a password for the user, WSS4J would compare
                //the password with the password sent by client, if they match
                //message will be processed. Any mismatch in password will result in a SOAP Fault.
                smsgwclient = smsgw_listener.selectsmsgw_user(pc.getIdentifier());
                
                if(smsgwclient!= null && pc.getIdentifier().equalsIgnoreCase(smsgwclient.getUser())) {
                    pc.setPassword(smsgwclient.getPassword());
                }
                
            }
        }

    }

    public static SmsgwClient getsmsgw_client() {
        return smsgwclient;
    }
    
    
}
