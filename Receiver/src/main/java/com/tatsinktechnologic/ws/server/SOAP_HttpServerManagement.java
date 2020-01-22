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

import com.tatsinktechnologic.beans_entity.Process_Unit;
import com.tatsinktechnologic.configuration.XML_ConfigLoader;
import com.tatsinktechnologic.ws.handler.ServerPasswordCallback;
import com.tatsinktechnologic.ws.process.Receiver_SMS;
import com.tatsinktechnologic.xml.receiver_listener.Http_Listener;
import com.tatsinktechnologic.xml.receiver_listener.Receiver_Listener;

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
import com.tatsinktechnologic.ws.interfaces.Receiver_SMSInterface;

public class SOAP_HttpServerManagement implements Runnable {
    
    private static Logger logger = LogManager.getLogger(SOAP_HttpServerManagement.class);
    
    private Http_Listener http_listener ;
    private Receiver_Listener receiver_listener;
    private XML_ConfigLoader xmlConfig;
    private Process_Unit process_unit;

    public SOAP_HttpServerManagement(Process_Unit process_unit) {
        this.process_unit=process_unit;
        this.xmlConfig= XML_ConfigLoader.getConfigurationLoader();
        this.receiver_listener=xmlConfig.getReceiver_listener();
        this.http_listener=xmlConfig.getHttp();        
    }
    
    @Override
    public void run() {
        try {
            
            Receiver_SMS receive_sms = new Receiver_SMS(process_unit);            
            JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
            svrFactory.setServiceClass(Receiver_SMSInterface.class);  
            svrFactory.setAddress("http://"+http_listener.getHttp_IP()+":"+http_listener.getHttp_port()+"/receiver");
            svrFactory.setServiceBean(receive_sms);
            
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
