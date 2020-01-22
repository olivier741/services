/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ws.handler;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.configuration.XML_ConfigLoader;
import com.tatsinktechnologic.xml.user_listener.Rcv_User_Connection;
import com.tatsinktechnologic.xml.user_listener.Receiver_User_Listener;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.wss4j.common.ext.WSPasswordCallback;




public class ClientPasswordCallback implements CallbackHandler {
    
    private static Logger logger = LogManager.getLogger(ClientPasswordCallback.class);
   
     public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
         
        XML_ConfigLoader xmlConfig = XML_ConfigLoader.getConfigurationLoader();
        Receiver_User_Listener rcv_user_listener = xmlConfig.getRcv_user_listener();
       
        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
        
        Rcv_User_Connection user_conn = rcv_user_listener.selectUser_con(pc.getIdentifier());
 
//         set the password for our message.
        if ((user_conn != null)&& pc.getIdentifier().equalsIgnoreCase(user_conn.getService_id())){
            pc.setPassword(user_conn.getPassword());
        } 

    }
}
