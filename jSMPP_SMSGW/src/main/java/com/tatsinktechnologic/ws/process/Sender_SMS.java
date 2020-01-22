/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ws.process;


import com.tatsinktechnologic.beans.SenderResponse;
import com.tatsinktechnologic.configuration.XML_ConfigLoader;
import com.tatsinktechnologic.ws.handler.ServerPasswordCallback;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.annotation.Resource;
import javax.jws.*;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import com.tatsinktechnologic.ws.interfaces.Sender_SMSInterface;
import com.tatsinktechnologic.xml.service_listener.Service;
import com.tatsinktechnologic.xml.service_listener.Service_listener;
import com.tatsinktechnologic.xml.user_listener.Send_User_Connection;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.tatsinktechnologic.smpp.gateway.SMSGateway;


/**
 *
 * @author olivier.tatsinkou
 */

@WebService(serviceName="Sender_SMS",endpointInterface = "com.tatsinktechnologic.ws.interfaces.Sender_SMSInterface")
public class Sender_SMS implements Sender_SMSInterface{
   
     private static  Properties prop = new Properties();
     private static Logger logger = LogManager.getLogger(Sender_SMS.class);
     private Send_User_Connection user_con= null;
     private Service_listener service_listener;
     private SMSGateway senderGateway;
     private XML_ConfigLoader xmlConfig ;
     
    @Resource
    WebServiceContext webServiceContext; 

    public Sender_SMS() {
        this.xmlConfig = XML_ConfigLoader.getConfigurationLoader();
        this.senderGateway= SMSGateway.getSenderGateway();
        this.service_listener=xmlConfig.getService_listener();  

    }

    public SenderResponse send_sms(@WebParam(name = "Sender") String sender,@WebParam(name = "Receiver") String receiver,@WebParam(name = "MsgContent") String content){
        SenderResponse resp = null;
        user_con= ServerPasswordCallback.getUser_conn();
        logger.info("#################### START SEND PDU ############################");
        if (checkIP(user_con.getClient_IP())){

            List<Service> listservices = Service_listener.getSMS_ServiceMt(user_con.getUser(),content, receiver, sender, service_listener.getServices());

            if (listservices.size() >0){

                List<String> response = senderGateway.sendSMS(user_con.getUser(),sender,receiver,content);

                if (response!=null){
                    resp = new SenderResponse("SUCCESS_SMS_CONTENT  ",response.toString(),0, "SMS Content have been send successfully");
                    logger.info("SUCCESS_SMS_CONTENT");
                    logger.info("SUCCESS Submition of : "+ sender+ " --> " +receiver +" ( msg ="+content+" )");
                }else {

                    logger.info("response is null: retry send SMS" );

                    response = senderGateway.sendSMS(user_con.getUser(),sender,receiver,content);

                    resp = new SenderResponse("SUCCESS_SMS_CONTENT  ",response.toString(),0, "SMS Content have been send successfully");
                    logger.info("SUCCESS_SMS_CONTENT");
                    logger.info("SUCCESS Submition of : "+ sender+ " --> " +receiver +" ( msg ="+content+" )");
                }

            }else{
                resp = new SenderResponse("ERROR_SMS_CONTENT",null,-6, "SMS Content not respect the filter define for this user");
                logger.error("ERROR_SMS_CONTENT");
                logger.error("Wrong Submition of : "+ sender+ " --> " +receiver +" ( msg ="+content+" )");
            }

        }else{
            resp = new SenderResponse("ERROR_HOST_Auth",null,-5, "Your IP address is deny");
        }           
        logger.info("#################### END SEND PDU ############################");
        return resp;
    }
    

     
     private boolean checkIP(String IP_address) { 
       
        boolean result = false;
        List<String> listIP= Arrays.asList(IP_address.split(","));

        MessageContext messageContext = webServiceContext.getMessageContext();
        HttpServletRequest request = (HttpServletRequest) messageContext.get(MessageContext.SERVLET_REQUEST); 
        String callerIpAddress = request.getRemoteAddr();

        logger.info("Client IP Address = " + callerIpAddress); 

        if (listIP.contains(callerIpAddress)) result= true;
        return result;
   }
     
}

