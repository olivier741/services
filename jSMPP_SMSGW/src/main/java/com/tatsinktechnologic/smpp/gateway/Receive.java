/*
 * Copyright 2018 olivier.
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
package com.tatsinktechnologic.smpp.gateway;

import com.tatsinktechnologic.beans.DeliveryMessage;
import com.tatsinktechnologic.beans.ReceiverResponse;
import com.tatsinktechnologic.configuration.XML_ConfigLoader;
import com.tatsinktechnologic.ws.client.SOAP_Listener;
import com.tatsinktechnologic.xml.service_listener.Service;
import com.tatsinktechnologic.xml.service_listener.Service_listener;
import com.tatsinktechnologic.xml.user_listener.Rcv_User_Connection;
import com.tatsinktechnologic.xml.user_listener.Receiver_User_Listener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.MessageType;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.Session;
import org.jsmpp.util.HexUtil;
import org.jsmpp.util.InvalidDeliveryReceiptException;

/**
 *
 * @author olivier
 */
public class Receive implements MessageReceiverListener {

    private static final ExecutorService pool = Executors.newFixedThreadPool(10);

    private static final Logger logger = Logger.getLogger(Receive.class);
    
    private XML_ConfigLoader xmlConfig;
    private Service_listener service_listener;
    private Receiver_User_Listener rcv_user_listener;
    private int smpp_enableReport;
    private String smpp_dateFormat;
    private String user ;
    
    public Receive(String user){
      super();
      this.user=user;
      this.xmlConfig = XML_ConfigLoader.getConfigurationLoader();
      this.service_listener=xmlConfig.getService_listener();
      this.rcv_user_listener=xmlConfig.getRcv_user_listener();
      this.smpp_dateFormat= rcv_user_listener.getSmspp_dataFormat();
      this.smpp_enableReport=rcv_user_listener.getSmpp_enableReport();
    }

    public Receive(Session s){
       this("default_user");
    }

    @Override
    public DataSmResult onAcceptDataSm(DataSm arg0, Session arg1) throws ProcessRequestException {
        
        
        logger.info("Received SMS 1 " + arg0);

        return null;
    }

    @Override
    public void onAcceptAlertNotification(AlertNotification arg0) {

         logger.info("Received SMS 2 " + arg0);
    }

    @Override
    public void onAcceptDeliverSm(DeliverSm arg0) throws ProcessRequestException {

            ReceiverResponse response = null;
            
            if(MessageType.SMSC_DEL_RECEIPT.containedIn(arg0.getEsmClass()) && (smpp_enableReport==1)){
               logger.info("-------------- START RECEIVE DELIVERY SMS -----------");
                
               
                // this message is delivery receipt
                try {
                    DeliveryReceipt delReceipt = arg0.getShortMessageAsDeliveryReceipt();

                    // lets cover the id to hex string format
                    long id = Long.parseLong(delReceipt.getId()) & 0xffffffff;
                    String messageId = Long.toString(id, 16).toUpperCase();

                    /*
                     * you can update the status of your submitted message on the
                     * database based on messageId
                     */
                      Rcv_User_Connection user_conn = rcv_user_listener.selectUser_con(user);
                      DeliveryMessage delivery_msg =null;
                       if (user_conn!=null){
                             
                                 SOAP_Listener soaplistener = new SOAP_Listener(user_conn);
                                 delivery_msg =new DeliveryMessage(     messageId, 
                                                                        arg0.getDestAddress(),
                                                                        arg0.getSourceAddr(),
                                                                        delReceipt.getId(),
                                                                        String.valueOf(delReceipt.getSubmitted()),
                                                                        String.valueOf(delReceipt.getDelivered()),
                                                                        convertToDate(delReceipt.getSubmitDate()),
                                                                        convertToDate(delReceipt.getDoneDate()),
                                                                        delReceipt.getFinalStatus().name(),
                                                                        delReceipt.getError(),
                                                                         ""
                                                                        );
                                 response = soaplistener.Send_to_report(delivery_msg);
                                 
                                 if (response.getValue()==0){
                                    logger.info("SUCCESS DELIVERY report for user = "+user +" message: msg_id = "+messageId+" from "+arg0.getSourceAddr()+" --> "+arg0.getDestAddress());
                                    logger.info("SUCCESS DELIVERY information : "+delReceipt);
                                    logger.info("SUCCESS  send delivery report : "+delivery_msg);
                                 }else if (response.getValue()==1){
                                     logger.error("AUTH ERROR, IP not allow  TO  send delivery report : "+delivery_msg);
                                 }
                       }else{
                          logger.error("Not user provide to get Delevery Report "); 
                       }

                      
                } catch (InvalidDeliveryReceiptException e) {
                    logger.error("Failed getting delivery receipt", e);
                }

                
                logger.info("SMSC_DEL_RECEIPT : "+arg0.toString());
            }else{
                logger.info("-------------- START RECEIVE MO SMS -----------");
                String receiver = arg0.getDestAddress().trim().toUpperCase();
                String sender = arg0.getSourceAddr().trim().toUpperCase();
                byte[] data = arg0.getShortMessage();
                String content = null;
                if (arg0.getShortMessage() != null) {
                    if (arg0.getDataCoding() == (byte) 8) {
                        content = HexUtil.convertBytesToHexString(data, 0,data.length);
                    } else {
                        content = new String(data).trim().toUpperCase();
                    }
                }
                
                    List<Service> listservices = Service_listener.getSMS_ServiceMo(content, receiver, sender, service_listener.getServices());
                    if (listservices.size() >0){
                           Service service = listservices.get(0);
                           String service_id = service.getService_id();
                           Rcv_User_Connection user_conn = rcv_user_listener.selectUser_con(service_id);
                          
                            // get and check user from config file 

                             if (user_conn!=null){
                             
                                 SOAP_Listener soaplistener = new SOAP_Listener(user_conn);
                                 response = soaplistener.Send_to_receiver(sender, receiver, content);

                                 
                                 if (response.getValue()==0){
                                    logger.info("SUCCESS SEND TO APPLICATION : " + sender +" --> "+receiver +" [msg = "+content+" ]");
                                 }else if (response.getValue()==1 || response.getValue()==2){
                                    logger.error("WRONG SYNTAX SEND TO APPLICATION : " + sender +" --> "+receiver +" [msg = "+content+" ]");
                                 }else if (response.getValue()==-1){
                                    logger.error("WRONG COMMAND SEND TO APPLICATION : " + sender +" --> "+receiver +" [msg = "+content+" ]");
                                 }else if (response.getValue()==-2){
                                    logger.error("AUTH ERROR, IP not allow  TO  SEND TO APPLICATION : " + sender +" --> "+receiver +" [msg = "+content+" ]");
                                 }
                                 
                           }

                    }else {
                        logger.warn("No Service provide to process this SMS");
                        logger.warn("Lossing SMS: " + sender +" --> "+receiver +" [msg = "+content+" ]");
                    }
                
            }

    }
    
    private String  convertToDate(Date dateValue){

        String result = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmm");

        if (smpp_dateFormat!=null){
            formatter = new SimpleDateFormat(smpp_dateFormat);
            try {
              result = formatter.format(dateValue);
          } catch (Exception e) {
             logger.error("cannot convert "+ dateValue + " to String following format : "+smpp_dateFormat);
             formatter = new SimpleDateFormat("yyMMddHHmm");
             try {
                 result = formatter.format(dateValue);
             } catch (Exception e1) {
                 logger.error("cannot convert "+ dateValue + " to String following format yyMMddHHmm",e1);
             }
          }
        }else {
         try {
              result = formatter.format(dateValue);
          } catch (Exception e) {
              logger.error("cannot convert "+ dateValue + " to String following format yyMMddHHmm",e);
          }
        }

    return result;
   }

}