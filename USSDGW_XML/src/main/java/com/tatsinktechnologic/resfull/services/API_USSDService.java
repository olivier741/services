/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.resfull.services;

import com.tatsinktechnologic.beans.Message_Exchg;
import com.tatsinktechnologic.beans.StateMenu;
import com.tatsinktechnologic.beans.USSDType;
import com.tatsinktechnologic.beans.UssdMenu;
import com.tatsinktechnologic.beans.UssdMessage;
import com.tatsinktechnologic.config.ConfigLoader;
import com.tatsinktechnologic.resfull.interfaces.IUSSDService;
import com.tatsinktechnologic.utils.ConverterJSON;
import com.tatsinktechnologic.utils.Generator;
import com.tatsinktechnologic.xml.kafka.USSD_Conf;
import java.util.HashMap;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

/**
 *
 * @author olivier.tatsinkou
 */
public class API_USSDService implements IUSSDService {

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
    private static Logger logger = Logger.getLogger(API_USSDService.class);
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

    static {
        setUssdMenu = communConf.getSetUssdMenu();
        props = communConf.getProduct_props();
        producer = new KafkaProducer<String, String>(props);
        callback = new ProducerCallback();
        ussd_conf = communConf.getUssdconfig();
        ussdMsg_timeout = ussd_conf.getApi_conf().getUssdMessage_Timeout();
        SET_STATE = new HashMap<String, String>();
    }

    @Override
    public UssdMessage addUssdMessage_post(UssdMessage ussdMessage) {
        logger.info("--------------message receive from USSDGW POST FUNCTION---------------");
        logger.info(ussdMessage);
        return addUssdMessage(ussdMessage);
    }

    @Override
    public UssdMessage addUssdMessage_get(UssdMessage ussdMessage) {
        logger.info("--------------message receive from USSDGW GET FUNCTION---------------");
        logger.info(ussdMessage);
        return addUssdMessage(ussdMessage);
    }

    @Override
    public UssdMessage addUssdMessage_put(UssdMessage ussdMessage) {
        logger.info("--------------message receive from USSDGW PUT FUNCTION---------------");
        logger.info(ussdMessage);
        return addUssdMessage(ussdMessage);
    }

    @Override
    public UssdMessage addUssdMessage_head(UssdMessage ussdMessage) {
        logger.info("--------------message receive from USSDGW HEAD FUNCTION---------------");
        logger.info(ussdMessage);
        return addUssdMessage(ussdMessage);
    }

    @Override
    public UssdMessage addUssdMessage_delete(UssdMessage ussdMessage) {
        logger.info("--------------message receive from USSDGW DELETE FUNCTION---------------");
        logger.info(ussdMessage);
        return addUssdMessage(ussdMessage);
    }

    @Override
    public UssdMessage addUssdMessage_patch(UssdMessage ussdMessage) {
        logger.info("--------------message receive from USSDGW PATCH FUNCTION---------------");
        logger.info(ussdMessage);
        return addUssdMessage(ussdMessage);
    }

    @Override
    public UssdMessage addUssdMessage_options(UssdMessage ussdMessage) {
        logger.info("--------------message receive from USSDGW OPTION FUNCTION---------------");
        logger.info(ussdMessage);
        return addUssdMessage(ussdMessage);
    }

    private UssdMessage addUssdMessage(UssdMessage ussdRequest) {

        String transaction_id = ussdRequest.getTransId();
        int message_type = ussdRequest.getType();
        String msisdn = ussdRequest.getMsisdn();
        String ussd_message = ussdRequest.getUssdString().trim();

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
                    String menuServ = ussdMenu.getService();
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
                        String menuServ = ussdMenu.getService();
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
                }else{
                   logger.info("TRANSACTION NOT EXIST :   -----> Transaction : " + transaction_id); 
                }

                break;

            case USSDType.USSDMSG_TYPE_SUB_RECV_OK:

                break;

            case USSDType.USSDMSG_TYPE_TRANS_ERR:

                if (PROCESSING_STATE != null && PROCESSING_STATE.size() > 0 && !PROCESSING_STATE.containsKey(transaction_id)) {
                    logger.info("ERROR REQUEST : Remove --> state : " + PROCESSING_STATE.get(transaction_id) + " -----> Transaction : " + transaction_id);
                    PROCESSING_STATE.remove(transaction_id);
                }else{
                   logger.info("TRANSACTION NOT EXIST :   -----> Transaction : " + transaction_id); 
                }
                break;

            default:
                logger.info("USSDMSG_TYPE NOT DEFINE :  "+message_type+" -----> Transaction : " + transaction_id); 
                break;
        }

        return ussdResponse;
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
