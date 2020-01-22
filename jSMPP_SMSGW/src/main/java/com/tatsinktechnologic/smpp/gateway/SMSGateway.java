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
import com.tatsinktechnologic.configuration.Smpp_ConfigLoader;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an implementation of {@link Gateway}. This gateway will reconnect for a specified interval if the session is
 * closed.
 *
 * @author uudashr
 */

public class SMSGateway  {
    
  private static final Logger LOGGER = LoggerFactory.getLogger(SMSGateway.class);
  private SMPPSession session = null;
  private String remoteIpAddress;
  private int remotePort;
  private BindParameter bindParam;
  private long reconnectInterval = 5000L; // 5 seconds
  private Submit_LongSMS submitlongsms;
  
   
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
        session = newSession();
        session.setMessageReceiverListener(new Receive(session));
    } catch (IOException e) {
        LOGGER.error("cannot get session connection",e);
    } catch (Exception e) {
       LOGGER.error("cannot get session connection",e); 
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

  public List<String> sendSMS(String user,String Sender, String Receiver, String content){
      List<String> listmessagID = null;
      LOGGER.info("%%%%%%%%%%%%%%%%%% START SEND SMS %%%%%%%%%%%%%%%%%%%%%%");
         
      try {
            listmessagID =  submitlongsms.sendSMS(session,user,Sender,Receiver,content);
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
           
            LOGGER.info("Message submitted, message_id is " + result);
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
//       String result = session.submitShortMessage( serviceType, 
//                                    sourceAddrTon,
//                                    sourceAddrNpi, 
//                                    sourceaddres,
//                                    destaddres[0].getTypeOfNumber(),
//                                    destaddres[0].getNumberingPlanIndicator(),
//                                    destaddres[0].getAddress(), 
//                                    esmClass, 
//                                    protocolId, 
//                                    priorityFlag,
//                                    scheduleDeliveryTime, 
//                                    validityPeriod, 
//                                    registeredDelivery,
//                                    replaceIfPresentFlag.value(), 
//                                    dataCoding, 
//                                    smDefaultMsgId, 
//                                    message.getBytes(),
//                                    sarMsgRefNum, 
//                                    sarSegmentSeqnum, 
//                                    sarTotalSegments);

         for(Address addres : destaddres){
            LOGGER.info("SUCCESS Send : "+sourceaddres+" --> "+addres.getAddress()+" msg = "+message); 
         }
         
//         LOGGER.info("Messages submitted, result is " + result);
//         messageId =result;
         
         LOGGER.info("Messages submitted, result is " + result.getMessageId());
         messageId =result.getMessageId();
        } catch (PDUException e) {
            // Invalid PDU parameter
            System.err.println("Invalid PDU parameter");
            e.printStackTrace();
        } catch (ResponseTimeoutException e) {
            // Response timeout
            System.err.println("Response timeout");
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            // Invalid response
            System.err.println("Receive invalid respose");
            e.printStackTrace();
        } catch (NegativeResponseException e) {
            // Receiving negative response (non-zero command_status)
            System.err.println("Receive negative response");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO error occur");
            e.printStackTrace();
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
      LOGGER.info("Initiate session for the first time to {}:{}", remoteIpAddress, remotePort);
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
        LOGGER.info("Schedule reconnect after {} millis", timeInMillis);
        try {
          Thread.sleep(timeInMillis);
        }
        catch (InterruptedException e) {
        }

        int attempt = 0;
        while (session == null || session.getSessionState().equals(SessionState.CLOSED)) {
          try {
            LOGGER.info("Reconnecting attempt #{} ...", ++attempt);
            session = newSession();
          }
          catch (IOException e) {
            LOGGER.error("Failed opening connection and bind to " + remoteIpAddress + ":" + remotePort, e);
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
      LOGGER.debug("State changed from {} to {}",oldState , newState);
      if (newState.equals(SessionState.CLOSED)) {
        LOGGER.info("Session {} closed", source.getSessionId());
        reconnectAfter(reconnectInterval);
      }
    }
  }
  
//  public static byte[][] getParts(String messageBody, String charSet) {
//        int maximumSingleMessageSize = 0;
//        int maximumMultipartMessageSegmentSize = 0;
//        byte[] byteSingleMessage = null;
//        
//        if (! CharsetUtil.NAME_UCS_2.equals(charSet)) {
//            byteSingleMessage = CharsetUtil.encode(messageBody, charSet);
//            maximumSingleMessageSize = MAX_SINGLE_MSG_SEGMENT_SIZE_7BIT;
//            maximumMultipartMessageSegmentSize = MAX_MULTIPART_MSG_SEGMENT_SIZE_7BIT;
//        } else {
//            byteSingleMessage = CharsetUtil.encode(messageBody, charSet);
//            maximumSingleMessageSize = MAX_SINGLE_MSG_SEGMENT_SIZE_UCS2;
//            maximumMultipartMessageSegmentSize = MAX_MULTIPART_MSG_SEGMENT_SIZE_UCS2;
//        }
//        byte[][] byteMessagesArray = null;
//        if (messageBody.length() > maximumSingleMessageSize) {
//            // split message according to the maximum length of a segment
//            byteMessagesArray = splitUnicodeMessage(byteSingleMessage, maximumMultipartMessageSegmentSize);
//            // set UDHI so PDU will decode the header
//       //     esmClass = new ESMClass(MessageMode.DEFAULT, TrayIcon.MessageType.DEFAULT, GSMSpecificFeature.UDHI);
//        } else {
//            byteMessagesArray = new byte[][] { byteSingleMessage };
//         //   esmClass = new ESMClass();
//        }
//
//
//        return  byteMessagesArray;
//    }
//
//    private static byte[][] splitUnicodeMessage(byte[] aMessage, Integer maximumMultipartMessageSegmentSize) {
//
//        final byte UDHIE_HEADER_LENGTH = 0x05;
//        final byte UDHIE_IDENTIFIER_SAR = 0x00;
//        final byte UDHIE_SAR_LENGTH = 0x03;
//
//        // determine how many messages have to be sent
//        int numberOfSegments = aMessage.length / maximumMultipartMessageSegmentSize;
//        int messageLength = aMessage.length;
//        if (numberOfSegments > 255) {
//            numberOfSegments = 255;
//            messageLength = numberOfSegments * maximumMultipartMessageSegmentSize;
//        }
//        if ((messageLength % maximumMultipartMessageSegmentSize) > 0) {
//            numberOfSegments++;
//        }
//
//        // prepare array for all of the msg segments
//        byte[][] segments = new byte[numberOfSegments][];
//
//        int lengthOfData;
//
//        // generate new reference number
//        byte[] referenceNumber = new byte[1];
//        new Random().nextBytes(referenceNumber);
//
//        // split the message adding required headers
//        for (int i = 0; i < numberOfSegments; i++) {
//            if (numberOfSegments - i == 1) {
//                lengthOfData = messageLength - i * maximumMultipartMessageSegmentSize;
//            } else {
//                lengthOfData = maximumMultipartMessageSegmentSize;
//            }
//
//            // new array to store the header
//            segments[i] = new byte[6 + lengthOfData];
//
//            // UDH header
//            // doesn't include itself, its header length
//            segments[i][0] = UDHIE_HEADER_LENGTH;
//            // SAR identifier
//            segments[i][1] = UDHIE_IDENTIFIER_SAR;
//            // SAR length
//            segments[i][2] = UDHIE_SAR_LENGTH;
//            // reference number (same for all messages)
//            segments[i][3] = referenceNumber[0];
//            // total number of segments
//            segments[i][4] = (byte) numberOfSegments;
//            // segment number
//            segments[i][5] = (byte) (i + 1);
//
//            // copy the data into the array
//            System.arraycopy(aMessage, (i * maximumMultipartMessageSegmentSize), segments[i], 6, lengthOfData);
//
//        }
//        return segments;
//    }

}
