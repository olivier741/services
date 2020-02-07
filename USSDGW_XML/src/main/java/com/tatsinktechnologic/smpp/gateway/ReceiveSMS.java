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
import com.tatsinktechnologic.beans.Message_Exchg;
import com.tatsinktechnologic.beans.StateMenu;
import com.tatsinktechnologic.beans.USSDType;
import com.tatsinktechnologic.beans.UssdMenu;
import com.tatsinktechnologic.beans.UssdMessage;
import com.tatsinktechnologic.config.ConfigLoader;
import com.tatsinktechnologic.resfull.services.API_USSDService;
import com.tatsinktechnologic.utils.ConverterJSON;
import com.tatsinktechnologic.utils.Generator;
import com.tatsinktechnologic.xml.kafka.USSD_Conf;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Provider.Service;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

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
 * @author olivier.tatsinkou
 */
public class ReceiveSMS implements MessageReceiverListener {

    /* USSDMSG_TYPE_SUB_SEND_REQ = 100;
       USSDMSG_TYPE_SUB_SEND_RSP = 101;
       USSDMSG_TYPE_SUB_CANCEL = 102;
       USSDMSG_TYPE_TRANS_ERR = 104;
       USSDMSG_TYPE_SUB_RECV_OK = 103;
       USSDMSG_TYPE_APP_SEND_MENU = 202;
       USSDMSG_TYPE_APP_SEND_RSP = 203;
       USSDMSG_TYPE_APP_SEND_NOTIFY_FIRST = 201;
       USSDMSG_TYPE_APP_CANCEL = 205;
       USSDMSG_TYPE_APP_CLOSE_TRANS = 206;
       USSDMSG_TYPE_APP_SEND_REQ_FIRST = 200;
       USSDMSG_TYPE_APP_SEND_NOTIFY = 204;
     */
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);
    private static final Logger logger = Logger.getLogger(ReceiveSMS.class);
    private static ConfigLoader communConf = ConfigLoader.getConfigurationLoader();
    private static HashMap<String, String> SET_STATE;
    private static HashMap<String, StateMenu> PROCESSING_STATE = new HashMap<String, StateMenu>();
    private static HashMap<String, UssdMenu> setUssdMenu;

    private static USSD_Conf ussd_conf;
    private static Properties props = new Properties();
    private static Producer<String, String> producer;
    private static ProducerCallback callback;
    private static ProducerRecord<String, String> kafka_data;
    private static int ussdMsg_timeout;
    private static int smpp_enableReport;
    private static String smpp_dateFormat;
    private static String delv_topic;

    private SMSGateway senderGateway;

    static {
        setUssdMenu = communConf.getSetUssdMenu();
        props = communConf.getProduct_props();
        callback = new ProducerCallback();
        ussd_conf = communConf.getUssdconfig();
        ussdMsg_timeout = ussd_conf.getApi_conf().getUssdMessage_Timeout();
        smpp_enableReport = ussd_conf.getSmpp_conf().getEnable_delivery();
        smpp_dateFormat = ussd_conf.getSmpp_conf().getDate_format();
        delv_topic = ussd_conf.getSmpp_conf().getDeliviery_topic();
        SET_STATE = new HashMap<String, String>();

    }

    public ReceiveSMS(String user, String id) {
        super();
        Properties producerprops = new Properties();

        producerprops = (Properties) SerializationUtils.clone(props);

        String new_client_id = producerprops.getProperty("client.id");
        new_client_id = new_client_id + "_" + id;
        producerprops.put("client.id", new_client_id);

        producer = new KafkaProducer<String, String>(producerprops);
        senderGateway = SMSGateway.getSenderGateway();
    }

    public ReceiveSMS(Session s, String id) {
        this("default_user", id);
    }

    @Override
    public DataSmResult onAcceptDataSm(DataSm arg0, Session arg1) throws ProcessRequestException {
        logger.info("Received SMS on onAcceptDataSm :  " + arg0);
        return null;
    }

    @Override
    public void onAcceptAlertNotification(AlertNotification arg0) {
        logger.info("Received SMS on onAcceptAlertNotification : " + arg0);
    }

    @Override
    public void onAcceptDeliverSm(DeliverSm arg0) throws ProcessRequestException {

        if (smpp_enableReport == 1) {

            if (MessageType.SMSC_DEL_RECEIPT.containedIn(arg0.getEsmClass())) {
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
                    DeliveryMessage delivery_msg = null;

                    delivery_msg = new DeliveryMessage(messageId,
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

                    String message_send = ConverterJSON.convertDeliveryToJson(delivery_msg);
                    kafka_data = new ProducerRecord<String, String>(delv_topic, message_send);
                    producer.send(kafka_data, callback);
                    logger.info("Delivery Receive : " + message_send);
                    logger.info("SUCCESS DELIVERY report  topic -->" + delv_topic + " |   message: msg_id = " + messageId + " from " + arg0.getSourceAddr() + " --> " + arg0.getDestAddress());
                    logger.info("SUCCESS DELIVERY information : " + delReceipt);
                    logger.info("SUCCESS  send delivery report : " + delivery_msg);

                } catch (InvalidDeliveryReceiptException e) {
                    logger.error("Failed getting delivery receipt", e);
                }

                logger.info("SMSC_DEL_RECEIPT : " + arg0.toString());
            } else {
                logger.info("-------------- START RECEIVE MO SMS -----------");
                runUSSD(arg0);
            }

        } else {
            if (!MessageType.SMSC_DEL_RECEIPT.containedIn(arg0.getEsmClass())) {
                logger.info("-------------- START RECEIVE MO SMS -----------");

                runUSSD(arg0);
            } else {
                try {
                    DeliveryReceipt delReceipt = arg0.getShortMessageAsDeliveryReceipt();
                    // lets cover the id to hex string format
                    long id = Long.parseLong(delReceipt.getId()) & 0xffffffff;
                    String messageId = Long.toString(id, 16).toUpperCase();

                    /*
                     * you can update the status of your submitted message on the
                     * database based on messageId
                     */
                    DeliveryMessage delivery_msg = null;

                   

                    delivery_msg = new DeliveryMessage(messageId,
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

                    String message_send = ConverterJSON.convertDeliveryToJson(delivery_msg);
                    logger.info("########## SMSGW ARE NOT SETTING TO FOWARD DELIVERY REPORT ################## ");
                    logger.info("Delivery Receive : " + message_send);
                    logger.info("SUCCESS DELIVERY report  topic -->" + delv_topic + " |   message: msg_id = " + messageId + " from " + arg0.getSourceAddr() + " --> " + arg0.getDestAddress());
                    logger.info("SUCCESS DELIVERY information : " + delReceipt);
                    logger.info("SUCCESS  send delivery report : " + delivery_msg);
                } catch (Exception e) {
                    logger.error("Failed getting delivery receipt", e);
                }

            }
        }

    }

    private void runUSSD(DeliverSm arg0) {
        
        String shortcode = arg0.getDestAddress();
        String msisdn = arg0.getSourceAddr();
        byte[] data = arg0.getShortMessage();
        String ussd_message = null;
        if (arg0.getShortMessage() != null) {
            if (arg0.getDataCoding() == (byte) 8) {
                ussd_message = HexUtil.convertBytesToHexString(data, 0, data.length);
            } else {
                ussd_message = new String(data);
            }
        }
        String menuServ = "";
        UssdMessage ussdRequest = new UssdMessage();

        ussdRequest.setMsisdn(msisdn);

        String transaction_id = ussdRequest.getTransId();
        int message_type = ussdRequest.getType();

        UssdMessage ussdResponse = new UssdMessage();

        ussdResponse.setCharSet(ussdRequest.getCharSet());
        ussdResponse.setConnectorId(ussdRequest.getConnectorId());
        ussdResponse.setDlgId(ussdRequest.getDlgId());
        ussdResponse.setEncryptedUssdString(ussdRequest.getEncryptedUssdString());
        ussdResponse.setHlrGT(ussdRequest.getHlrGT());
        ussdResponse.setImsi(ussdRequest.getImsi());
        ussdResponse.setLoggedString(ussdRequest.getLoggedString());
        ussdResponse.setMsisdn(ussdRequest.getMsisdn());
        ussdResponse.setSendRecvTime(ussdRequest.getSendRecvTime());
        ussdResponse.setTransId(ussdRequest.getTransId());
        String stateMenu_key;
        UssdMenu ussdMenu;
        StateMenu state;
        String shorCode;
        switch (message_type) {

            case USSDType.USSDMSG_TYPE_SUB_SEND_REQ:  // first message 
                stateMenu_key = ussd_message;
                shorCode = getShortCode(ussd_message);

                state = new StateMenu();
                state.setShort_code(shorCode);
                state.setMsisdn(msisdn);
                state.setInput(stateMenu_key);
                state.setTransaction_id(transaction_id);

                if (PROCESSING_STATE != null && PROCESSING_STATE.size() > 0 && PROCESSING_STATE.containsKey(transaction_id)) {
                    logger.info("FIST REQUEST : Remove --> state : " + PROCESSING_STATE.get(transaction_id) + " -----> Transaction : " + transaction_id);
                    PROCESSING_STATE.remove(transaction_id);
                }

                ussdMenu = setUssdMenu.get(stateMenu_key);

                if (ussdMenu != null) {                     // check if menu exist
                    String menuResp = ussdMenu.getResp();
                    String menuAct = ussdMenu.getAction();
                    String menuTopic = ussdMenu.getTopic();
                    menuServ = ussdMenu.getService();
                    String menuStatus = ussdMenu.getStatus();

                    if (menuStatus.equals("START-MENU")) {
                        PROCESSING_STATE.put(transaction_id, state);

                        if (!StringUtils.isBlank(menuResp)) {
                            ussdResponse.setUssdString(menuResp);
                            ussdResponse.setType(USSDType.USSDMSG_TYPE_APP_SEND_MENU);
                        }

                        if (!StringUtils.isBlank(menuAct)) {
                            processAction(menuTopic, menuServ, msisdn, shorCode, menuAct);
                        }

                        logger.info("START-MENU USSD MENU : " + ussdMenu + " -----> Transaction : " + transaction_id);

                    } else if (menuStatus.equals("START-END")) {
                        if (!StringUtils.isBlank(menuResp)) {
                            ussdResponse.setUssdString(menuResp);
                            ussdResponse.setType(USSDType.USSDMSG_TYPE_APP_SEND_RSP);
                        }

                        if (!StringUtils.isBlank(menuAct)) {
                            processAction(menuTopic, menuServ, msisdn, shorCode, menuAct);
                        }
                        logger.info("START-END USSD MENU : " + ussdMenu + " -----> Transaction : " + transaction_id);
                    } else {
                        logger.info("THIS USSD REQUEST CANNOT BE PROCESS : " + ussdMenu + " -----> Transaction : " + transaction_id);
                    }
                } else {
                    logger.info("THIS USSD REQUEST CANNOT BE PROCESS : " + ussdMenu + " -----> Transaction : " + transaction_id);
                }

                break;

            case USSDType.USSDMSG_TYPE_SUB_SEND_RSP:

                if (PROCESSING_STATE != null && PROCESSING_STATE.size() > 0 && !PROCESSING_STATE.containsKey(transaction_id)) {
                    logger.info("FIST REQUEST : Remove --> state : " + PROCESSING_STATE.get(transaction_id) + " -----> Transaction : " + transaction_id);
                    state = PROCESSING_STATE.get(transaction_id);
                    stateMenu_key = state.getInput() + "-" + ussd_message;

                    shorCode = state.getShort_code();
                    state.setInput(stateMenu_key);

                    ussdMenu = setUssdMenu.get(stateMenu_key);

                    if (ussdMenu != null) {
                        String menuResp = ussdMenu.getResp();
                        String menuAct = ussdMenu.getAction();
                        String menuTopic = ussdMenu.getTopic();
                        menuServ = ussdMenu.getService();
                        String menuStatus = ussdMenu.getStatus();

                        if (menuStatus.equals("MENU")) {
                            PROCESSING_STATE.put(transaction_id, state);

                            if (!StringUtils.isBlank(menuResp)) {
                                ussdResponse.setUssdString(menuResp);
                                ussdResponse.setType(USSDType.USSDMSG_TYPE_APP_SEND_MENU);
                            }

                            if (!StringUtils.isBlank(menuAct)) {
                                processAction(menuTopic, menuServ, msisdn, shorCode, menuAct);
                            }

                            logger.info("USSD MENU : " + ussdMenu + " -----> Transaction : " + transaction_id);

                        } else if (menuStatus.equals("END")) {
                            if (!StringUtils.isBlank(menuResp)) {
                                ussdResponse.setUssdString(menuResp);
                                ussdResponse.setType(USSDType.USSDMSG_TYPE_APP_SEND_RSP);
                            }

                            if (!StringUtils.isBlank(menuAct)) {
                                processAction(menuTopic, menuServ, msisdn, shorCode, menuAct);
                            }
                            logger.info("END USSD MENU : " + ussdMenu + " -----> Transaction : " + transaction_id);
                        } else {
                            logger.info("THIS USSD REQUEST CANNOT BE PROCESS : " + ussdMenu + " -----> Transaction : " + transaction_id);
                        }
                    } else {
                        logger.info("THIS USSD REQUEST CANNOT BE PROCESS : " + ussdMenu + " -----> Transaction : " + transaction_id);
                    }
                }

                break;

            case USSDType.USSDMSG_TYPE_SUB_CANCEL:
                if (PROCESSING_STATE != null && PROCESSING_STATE.size() > 0 && !PROCESSING_STATE.containsKey(transaction_id)) {
                    logger.info("CANCEL REQUEST : Remove --> state : " + PROCESSING_STATE.get(transaction_id) + " -----> Transaction : " + transaction_id);
                    PROCESSING_STATE.remove(transaction_id);
                } else {
                    logger.info("TRANSACTION NOT EXIST :   -----> Transaction : " + transaction_id);
                }

                break;

            case USSDType.USSDMSG_TYPE_SUB_RECV_OK:

                break;

            case USSDType.USSDMSG_TYPE_TRANS_ERR:

                if (PROCESSING_STATE != null && PROCESSING_STATE.size() > 0 && !PROCESSING_STATE.containsKey(transaction_id)) {
                    logger.info("ERROR REQUEST : Remove --> state : " + PROCESSING_STATE.get(transaction_id) + " -----> Transaction : " + transaction_id);
                    PROCESSING_STATE.remove(transaction_id);
                } else {
                    logger.info("TRANSACTION NOT EXIST :   -----> Transaction : " + transaction_id);
                }
                break;

            default:
                logger.info("USSDMSG_TYPE NOT DEFINE :  " + message_type + " -----> Transaction : " + transaction_id);
                break;
        }

        List<String> response = senderGateway.sendSMS(menuServ, shortcode, msisdn, ussdResponse.getUssdString(), "ussd_client");
    }

    private String convertToDate(Date dateValue) {

        String result = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmm");

        if (smpp_dateFormat != null) {
            formatter = new SimpleDateFormat(smpp_dateFormat);
            try {
                result = formatter.format(dateValue);
            } catch (Exception e) {
                logger.error("cannot convert " + dateValue + " to String following format : " + smpp_dateFormat);
                formatter = new SimpleDateFormat("yyMMddHHmm");
                try {
                    result = formatter.format(dateValue);
                } catch (Exception e1) {
                    logger.error("cannot convert " + dateValue + " to String following format yyMMddHHmm", e1);
                }
            }
        } else {
            try {
                result = formatter.format(dateValue);
            } catch (Exception e) {
                logger.error("cannot convert " + dateValue + " to String following format yyMMddHHmm", e);
            }
        }

        return result;
    }

    private void processAction(String topic, String srv_name, String msisdn, String short_code, String content) {

        String trans_ID = Generator.getTransaction();
        Message_Exchg msg_exch = new Message_Exchg(trans_ID, srv_name, "", msisdn, short_code, content, "USSD");

        // get and check user from config file 
        String message_send = ConverterJSON.convertMsgExchToJson(msg_exch);
        kafka_data = new ProducerRecord<String, String>(topic, trans_ID, message_send);
        producer.send(kafka_data, callback);
        logger.info("USSD Receive : " + message_send);
        logger.info("SUCCESS SEND TO APPLICATION : topic -->" + topic + " | " + msisdn + " --> " + short_code + " [msg = " + content + " ]");

    }

    private String getShortCode(String ussdMsg) {
        String result = null;
        String val1 = null;
        String val2 = null;
        if ((ussdMsg.startsWith("*")) || (ussdMsg.startsWith("#"))) {
            val1 = ussdMsg.substring(1);
            val2 = substringBeforeLast(val1, "*");
            result = substringBeforeLast(val1, "*");
            if (val2.equals(val1)) {
                result = substringBeforeLast(val1, "#");
            }
        }
        return result;
    }

    private static String substringBeforeLast(String str, String separator) {
        if ((StringUtils.isBlank(str)) || (StringUtils.isBlank(separator))) {
            return str;
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    private static class ProducerCallback implements Callback {

        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null) {
                logger.error("Error while producing message to topic :" + recordMetadata, e);
            } else {
                String message = String.format("Producer client ID :  " + props.getProperty("client.id") + " -- Topic: %s -- Partition: %s -- offset: %s", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
                logger.info(message);
            }
        }
    }

}
