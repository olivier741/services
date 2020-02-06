/*
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.tatsinktechnologic.smpp.gateway;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.tatsinktechnologic.config.Smpp_ConfigLoader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.I0Itec.zkclient.Gateway;
import org.apache.log4j.Logger;


import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.Address;
import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.OptionalParameters;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.ReplaceIfPresentFlag;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.SubmitMultiResult;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.extra.SessionState;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.Session;
import org.jsmpp.session.SessionStateListener;

/**
 * This is an implementation of {@link Gateway}. This gateway will reconnect for a specified interval if the session is
 * closed.
 *
 * @author olivier.tatsinkou
 */

public class SMSGateway  {
    
  private static final Logger logger = Logger.getLogger(SMSGateway.class);
  private SMPPSession session = null;
  private String remoteIpAddress;
  private int remotePort;
  private BindParameter bindParam;
  private long reconnectInterval = 5000L; // 5 seconds
  private Submit_LongSMS submitlongsms;
  private ReceiveSMS rcvSMS;
  
   
  private static Smpp_ConfigLoader configLoad = Smpp_ConfigLoader.getConfigurationLoader();

  /**
   * Construct auto reconnect gateway with specified IP address, port and SMPP Bind parameters.
   *
   * @param remoteIpAddress is the SMSC IP address.
   * @param remotePort      is the SMSC port.
   * @param bindParam       is the SMPP Bind parameters.
   */
  private SMSGateway(String remoteIpAddress, int remotePort,BindParameter bindParam) {
    this.remoteIpAddress = remoteIpAddress;
    this.remotePort = remotePort;
    this.bindParam = bindParam;
    this.submitlongsms = new Submit_LongSMS();
    try {
        Random rand = new Random();
        int randomNum = rand.nextInt((10) + 1) ;
        rcvSMS = new ReceiveSMS(session,String.valueOf(randomNum));
        session = newSession();
        session.setMessageReceiverListener(rcvSMS);
    } catch (IOException e) {
        logger.error("cannot get session connection",e);
    } catch (Exception e) {
       logger.error("cannot get session connection",e); 
    }
    
    
  }

    private static class SingletonSenderGateway {
        private static final SMSGateway _senderGateway = new SMSGateway(  configLoad.getIP_address(), 
                                                                                configLoad.getPort(), 
                                                                                new BindParameter(    configLoad.getBindType(), 
                                                                                                      configLoad.getSystemId(), 
                                                                                                      configLoad.getPassword(), 
                                                                                                      configLoad.getSystemType(), 
                                                                                                      configLoad.getAddrTon(), 
                                                                                                      configLoad.getAddrNpi(), 
                                                                                                      configLoad.getAddressRange()
                                                                                            ));
      
    }
  
    public static SMSGateway getSenderGateway(){
        return SingletonSenderGateway._senderGateway;
    }

  public List<String> sendSMS(String user,String Sender, String Receiver, String content,String client_id){
      List<String> listmessagID = null;
      logger.info("%%%%%%%%%%%%%%%%%% START SEND SMS %%%%%%%%%%%%%%%%%%%%%%");
         
      try {
            if (rcvSMS==null){
                 Random rand = new Random();
                int randomNum = rand.nextInt((10) + 1) ;
                rcvSMS = new ReceiveSMS(user,client_id+"_"+randomNum);
            }
            listmessagID =  submitlongsms.sendSMS(session,user,Sender,Receiver,content,rcvSMS);
      } catch (Exception e) {
          e.printStackTrace();
      }
      return listmessagID;
   }
  
  /* (non-Javadoc)
   * @see org.jsmpp.examples.gateway.Gateway#submitShortMessage(java.lang.String, org.jsmpp.bean.TypeOfNumber, org.jsmpp.bean.NumberingPlanIndicator, java.lang.String, org.jsmpp.bean.TypeOfNumber, org.jsmpp.bean.NumberingPlanIndicator, java.lang.String, org.jsmpp.bean.ESMClass, byte, byte, java.lang.String, java.lang.String, org.jsmpp.bean.RegisteredDelivery, byte, org.jsmpp.bean.DataCoding, byte, byte[], org.jsmpp.bean.OptionalParameter[])
   */
  private String submitShortMessage(String serviceType,
                                   TypeOfNumber sourceAddrTon, 
                                   NumberingPlanIndicator sourceAddrNpi,
                                   String sourceAddr, 
                                   TypeOfNumber destAddrTon,
                                   NumberingPlanIndicator destAddrNpi, 
                                   String destinationAddr,
                                   ESMClass esmClass, 
                                   byte protocolId, 
                                   byte priorityFlag,
                                   String scheduleDeliveryTime, 
                                   String validityPeriod,
                                   RegisteredDelivery registeredDelivery, 
                                   byte replaceIfPresentFlag,
                                   DataCoding dataCoding, 
                                   byte smDefaultMsgId, 
                                   byte[] shortMessage,
                                   OptionalParameter... optionalParameters) throws PDUException,
      ResponseTimeoutException, InvalidResponseException,
      NegativeResponseException, IOException {

    return getSession().submitShortMessage(serviceType, sourceAddrTon,
        sourceAddrNpi, sourceAddr, destAddrTon, destAddrNpi,
        destinationAddr, esmClass, protocolId, priorityFlag,
        scheduleDeliveryTime, validityPeriod, registeredDelivery,
        replaceIfPresentFlag, dataCoding, smDefaultMsgId, shortMessage,
        optionalParameters);
  }

  /* (non-Javadoc)
   * @see org.jsmpp.examples.gateway.Gateway#submitMultiLongMessage(java.lang.String, org.jsmpp.bean.TypeOfNumber, org.jsmpp.bean.NumberingPlanIndicator, java.lang.String, org.jsmpp.bean.TypeOfNumber, org.jsmpp.bean.NumberingPlanIndicator, java.lang.String, org.jsmpp.bean.ESMClass, byte, byte, java.lang.String, java.lang.String, org.jsmpp.bean.RegisteredDelivery, byte, org.jsmpp.bean.DataCoding, byte, byte[], org.jsmpp.bean.OptionalParameter[])
   */
    private String submitMultiLongMessage(  String serviceType,
                                            TypeOfNumber sourceAddrTon, 
                                            NumberingPlanIndicator sourceAddrNpi,
                                            String sourceAddr, 
                                            TypeOfNumber destAddrTon,
                                            NumberingPlanIndicator destAddrNpi, 
                                            String destinationAddr,
                                            ESMClass esmClass, 
                                            byte protocolId, 
                                            byte priorityFlag,
                                            String scheduleDeliveryTime, 
                                            String validityPeriod,
                                            ReplaceIfPresentFlag replaceIfPresentFlag,
                                            DataCoding dataCoding, 
                                            byte smDefaultMsgId, 
                                            String shortMessage,
                                            int sms_max_size) throws PDUException, ResponseTimeoutException, InvalidResponseException, NegativeResponseException, IOException {
        
         String result = null;
         List<String> listMessage = Arrays.asList(Iterables.toArray( Splitter.fixedLength(sms_max_size).split(shortMessage),String.class)); 
         List<String> listaddres = Arrays.asList(destinationAddr.split(";"));
         Address[] addresses = new Address[listaddres.size()];
         
         for (int i =0;i<listaddres.size();i++){
             Address address = new Address(destAddrTon, destAddrNpi, listaddres.get(i));
             addresses[i]=address;
         }
         
         
        Random random = new Random();

        final int totalSegments = listMessage.size();
        OptionalParameter sarMsgRefNum = OptionalParameters.newSarMsgRefNum((short)random.nextInt());
        OptionalParameter sarTotalSegments = OptionalParameters.newSarTotalSegments(totalSegments);
        
        
        
        for (int i = 0; i < totalSegments; i++) {
            int seqNum = i + 1;
            String MSG = listMessage.get(i);
            OptionalParameter sarSegmentSeqnum = OptionalParameters.newSarSegmentSeqnum(seqNum);
           
            result = sendMessage(   session,
                                    MSG,
                                    sourceAddr,
                                    addresses,
                                    serviceType,
                                    sourceAddrTon, 
                                    sourceAddrNpi,
                                    esmClass,
                                    protocolId, 
                                    priorityFlag,
                                    scheduleDeliveryTime, 
                                    validityPeriod,
                                    replaceIfPresentFlag,
                                    dataCoding, 
                                    smDefaultMsgId,
                                    sarMsgRefNum,
                                    sarSegmentSeqnum,
                                    sarTotalSegments);
           
            logger.info("Message submitted, message_id is " + result);
        }
        
        return result;
    }
     
     
   private  String sendMessage(         SMPPSession session, 
                                        String message, 
                                        String sourceaddres,
                                        Address[] destaddres,
                                        String serviceType,
                                        TypeOfNumber sourceAddrTon, 
                                        NumberingPlanIndicator sourceAddrNpi,
                                        ESMClass esmClass,
                                        byte protocolId, 
                                        byte priorityFlag,
                                        String scheduleDeliveryTime, 
                                        String validityPeriod,
                                        ReplaceIfPresentFlag replaceIfPresentFlag,
                                        DataCoding dataCoding, 
                                        byte smDefaultMsgId,
                                        OptionalParameter sarMsgRefNum, 
                                        OptionalParameter sarSegmentSeqnum, 
                                        OptionalParameter sarTotalSegments) {
        String messageId = null;
      
        try {
        // Set listener to receive deliver_sm
//        session.setMessageReceiverListener(new Receive(session));

        // requesting delivery report
        final RegisteredDelivery registeredDelivery = new RegisteredDelivery();
        registeredDelivery.setSMSCDeliveryReceipt(SMSCDeliveryReceipt.SUCCESS_FAILURE);
            
        SubmitMultiResult result = getSession().submitMultiple(serviceType, 
                                                           sourceAddrTon, 
                                                           sourceAddrNpi, 
                                                           sourceaddres,
                                                           destaddres, 
                                                           esmClass, 
                                                           protocolId, 
                                                           priorityFlag, 
                                                           scheduleDeliveryTime, 
                                                           validityPeriod,
                                                           registeredDelivery, 
                                                           replaceIfPresentFlag,
                                                           dataCoding, 
                                                           smDefaultMsgId,
                                                           message.getBytes(),
                                                           sarMsgRefNum, 
                                                           sarSegmentSeqnum, 
                                                           sarTotalSegments
                                                           );


         for(Address addres : destaddres){
            logger.info("SUCCESS Send : "+sourceaddres+" --> "+addres.getAddress()+" msg = "+message); 
         }
         
         logger.info("Messages submitted, result is " + result.getMessageId());
         messageId =result.getMessageId();
        } catch (PDUException e) {
            // Invalid PDU parameter
            logger.error("Invalid PDU parameter",e);
        } catch (ResponseTimeoutException e) {
            // Response timeout
            logger.error("Response timeout",e);
        } catch (InvalidResponseException e) {
            // Invalid response
           logger.error("Receive invalid respose",e);
        } catch (NegativeResponseException e) {
            // Receiving negative response (non-zero command_status)
            logger.error("Receive negative response",e);
        } catch (IOException e) {
            logger.error("IO error occur",e);
        }
        return messageId;
    }

  /**
   * Create new {@link SMPPSession} complete with the {@link SessionStateListenerImpl}.
   *
   * @return the {@link SMPPSession}.
   * @throws IOException if the creation of new session failed.
   */
  private SMPPSession newSession() throws IOException {
    SMPPSession tmpSession = new SMPPSession(remoteIpAddress, remotePort, bindParam);
    tmpSession.addSessionStateListener(new SessionStateListenerImpl());
    return tmpSession;
  }
  
  
  

  /**
   * Get the session. If the session still null or not in bound state, then IO exception will be thrown.
   *
   * @return the valid session.
   * @throws IOException if there is no valid session or session creation is invalid.
   */
  private SMPPSession getSession() throws IOException {
    if (session == null) {
      logger.info("Initiate session for the first time to "+remoteIpAddress+":"+remotePort);
      session = newSession();
    }
    else if (!session.getSessionState().isBound()) {
      throw new IOException("We have no valid session yet");
    }
    return session;
  }

  /**
   * Reconnect session after specified interval.
   *
   * @param timeInMillis is the interval.
   */
  private void reconnectAfter(final long timeInMillis) {
    new Thread() {
      @Override
      public void run() {
        logger.info("Schedule reconnect after "+timeInMillis+" millis");
        try {
          Thread.sleep(timeInMillis);
        }
        catch (InterruptedException e) {
        }

        int attempt = 0;
        while (session == null || session.getSessionState().equals(SessionState.CLOSED)) {
          try {
            logger.info("Reconnecting attempt #"+(++attempt)+" ...");
            session = newSession();
          }
          catch (IOException e) {
            logger.error("Failed opening connection and bind to " + remoteIpAddress + ":" + remotePort, e);
            // wait for a second
            try {
              Thread.sleep(1000);
            }
            catch (InterruptedException ee) {
            }
          }
        }
      }
    }.start();
  }

  /**
   * This class will receive the notification from {@link SMPPSession} for the state changes. It will schedule to
   * re-initialize session.
   *
   * @author uudashr
   */
  private class SessionStateListenerImpl implements SessionStateListener {
    public void onStateChange(SessionState newState, SessionState oldState, Session source) {
      logger.debug("State changed from "+oldState+" to "+newState);
      if (newState.equals(SessionState.CLOSED)) {
        logger.info("Session "+source.getSessionId()+" closed");
        reconnectAfter(reconnectInterval);
      }
    }
  }
  

}
