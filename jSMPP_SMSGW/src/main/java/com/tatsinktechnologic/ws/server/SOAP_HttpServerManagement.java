/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ws.server;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.configuration.XML_ConfigLoader;
import com.tatsinktechnologic.ws.handler.ServerPasswordCallback;
import com.tatsinktechnologic.ws.interfaces.Sender_SMSInterface;
import com.tatsinktechnologic.ws.process.Sender_SMS;
import com.tatsinktechnologic.xml.service_listener.Service_listener;
import com.tatsinktechnologic.xml.service_listener.Http_Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;

public class SOAP_HttpServerManagement implements Runnable {
    
    private static Logger logger = LogManager.getLogger(SOAP_HttpServerManagement.class);
    
    private Http_Listener http_listener ;
    private Service_listener service_listener;
    private XML_ConfigLoader xmlConfig;

    public SOAP_HttpServerManagement() {
        this.xmlConfig= XML_ConfigLoader.getConfigurationLoader();
        this.service_listener=xmlConfig.getService_listener();
        this.http_listener=xmlConfig.getHttp();        
    }
    
    @Override
    public void run() {
        try {
            
            Sender_SMS send_sms = new Sender_SMS();            
            JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
            svrFactory.setServiceClass(Sender_SMSInterface.class);  
            svrFactory.setAddress("http://"+http_listener.getHttp_IP()+":"+http_listener.getHttp_port()+"/sender_sms");
            svrFactory.setServiceBean(send_sms);
            
            Map inProps = new HashMap();
            inProps.put( WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
            
            if (http_listener.getWs_security_mode()==0){
                // Password type : plain text
              inProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);  
            }else{
                // for hashed password use:
               inProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST); 
            }

            inProps.put( WSHandlerConstants.PW_CALLBACK_CLASS, ServerPasswordCallback.class.getName() );

            svrFactory.getInInterceptors().add(new LoggingInInterceptor());
            
            //UsernameToken security headers
            svrFactory.getInInterceptors().add( new WSS4JInInterceptor( inProps ) );
            
            svrFactory.getOutInterceptors().add(new LoggingOutInterceptor());
  
            svrFactory.create();           
            logger.info("%%%%%%%%%%% SUCCESSFULL LOAD WEB SERVICE SERVER %%%%%%%%%%%%%%");
       } catch (Exception e) {
           logger.error("cannot publish webservice :", e);
       
       }
    }
    
    public static void executeRunnables(final ExecutorService service, List<Runnable> runnables){

		for(Runnable r : runnables){
			service.execute(r);
		}
		//On ferme l'executor une fois les taches finies
		//En effet shutdown va attendre la fin d'exécution des tâches
		service.shutdown();
	}
   
}
