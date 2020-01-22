/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.resfull.services;

import com.tatsinktechnologic.dao_commun_repository.CommunDAO_Repository;
import com.tatsinktechnologic.entity.Mo_Fake;
import com.tatsinktechnologic.entity.State;
import com.tatsinktechnologic.entity.State_Change;
import com.tatsinktechnologic.entity.State_Level;
import com.tatsinktechnologic.util.ConverterJSON;
import com.tatsinktechnologic.util.Generator;
import com.tatsinktechnologic.beans.Message_Exchg;
import com.tatsinktechnologic.beans.ShortCode_Msisdn;
import com.tatsinktechnologic.beans.USSDType;
import com.tatsinktechnologic.beans.UssdMessage;
import com.tatsinktechnologic.resfull.interfaces.IUSSDService;
import com.tatsinktechnologic.xml.Service;
import com.tatsinktechnologic.xml.Ussd_API_Conf;
import java.util.HashMap;
import java.util.List;
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
public class USSDService implements IUSSDService {

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
    private static Logger logger = Logger.getLogger(USSDService.class);
    private static CommunDAO_Repository communConf = CommunDAO_Repository.getConfigurationLoader();
    private static HashMap<String, State_Change> FIRST_STATECHANGE;
    private static HashMap<String, State_Change> ALL_STATECHANGE;
    private static HashMap<String, Mo_Fake> SET_MO_FAKE;
    private static HashMap<String, State> PROCESSING_STATE = new HashMap<String, State>();
    private static HashMap<String, ShortCode_Msisdn> PROCESSING_SHORTCODE = new HashMap<String, ShortCode_Msisdn>();

    private State current_state;
    private State_Change current_state_change;

    private static Ussd_API_Conf ussd_api_conf;
    private static Properties props = new Properties();
    private static Producer<String, String> producer;
    private static ProducerCallback callback;
    private static ProducerRecord<String, String> kafka_data;
    private static int ussdMsg_timeout;

    static {
        FIRST_STATECHANGE = communConf.getFIRST_STATECHANGE();
        ALL_STATECHANGE = communConf.getALL_STATECHANGE();
        SET_MO_FAKE = communConf.getSET_MO_FAKE();
        props = communConf.getProduct_props();
        producer = new KafkaProducer<String, String>(props);
        callback = new ProducerCallback();
        ussd_api_conf = communConf.getUssd_api_conf();
        ussdMsg_timeout = ussd_api_conf.getUssdMessage_Timeout();
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
        String ussd_message = ussdRequest.getUssdString();
        ShortCode_Msisdn SC_msissdn = new ShortCode_Msisdn();

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

        switch (message_type) {

            case USSDType.USSDMSG_TYPE_SUB_SEND_REQ:  // first message 
                SC_msissdn.setShort_code(getShortCode(ussd_message));
                SC_msissdn.setMsisdn(msisdn);
                SC_msissdn.setTransaction_id(transaction_id);

                if (PROCESSING_STATE != null && PROCESSING_STATE.size() > 0 && PROCESSING_STATE.containsKey(transaction_id)) {
                    logger.info("Remove --> state : " + PROCESSING_STATE.get(transaction_id) + " -----> Transaction : " + transaction_id);
                    PROCESSING_STATE.remove(transaction_id);
                }

                if (PROCESSING_SHORTCODE != null && PROCESSING_SHORTCODE.size() > 0 && PROCESSING_SHORTCODE.containsKey(transaction_id)) {
                    logger.info("Remove --> ShortCode : " + PROCESSING_SHORTCODE.get(transaction_id) + " -----> Transaction : " + transaction_id);
                    PROCESSING_SHORTCODE.remove(transaction_id);

                }

                current_state_change = FIRST_STATECHANGE.get(ussd_message);
                if (current_state_change != null) {
                    current_state = current_state_change.getNext_state();
                    ussdResponse = process(current_state, ussdRequest, SC_msissdn);
                    logger.info("CURRENT USSD MENU : " + current_state + " -----> Transaction : " + transaction_id);
                } else {
                    logger.info("Not USSD MENU Define -----> Transaction : " + transaction_id);
                }

                break;

            case USSDType.USSDMSG_TYPE_SUB_SEND_RSP:
                State previour_state = null;
                if (PROCESSING_STATE != null && PROCESSING_STATE.size() > 0 && PROCESSING_STATE.containsKey(transaction_id)) {
                    previour_state = PROCESSING_STATE.get(transaction_id);
                    logger.info("Remove --> previous state : " + previour_state + " -----> Transaction : " + transaction_id);
                    PROCESSING_STATE.remove(transaction_id);
                }

                if (PROCESSING_SHORTCODE != null && PROCESSING_SHORTCODE.size() > 0 && PROCESSING_SHORTCODE.containsKey(transaction_id)) {
                    SC_msissdn = PROCESSING_SHORTCODE.get(transaction_id);
                    logger.info("Remove --> ShortCode and MSISDN: " + SC_msissdn + " -----> Transaction : " + transaction_id);
                    PROCESSING_SHORTCODE.remove(transaction_id);
                }

                if (previour_state != null) {
                    current_state_change = ALL_STATECHANGE.get(previour_state.getState_id() + "_" + ussd_message);
                    if (current_state_change != null) {
                        current_state = current_state_change.getNext_state();
                        ussdResponse = process(current_state, ussdRequest, SC_msissdn);
                        logger.info("CURRENT USSD MENU : " + current_state + " -----> Transaction : " + transaction_id);
                    } else {
                        logger.info("Not Next USSD MENU Define -----> Transaction : " + transaction_id);
                    }
                } else {
                    logger.info("Not Previous USSD MENU Define -----> Transaction : " + transaction_id);
                }

                break;

            case USSDType.USSDMSG_TYPE_SUB_CANCEL:

                break;

            case USSDType.USSDMSG_TYPE_SUB_RECV_OK:

                break;

            case USSDType.USSDMSG_TYPE_TRANS_ERR:

                break;

            default:
        }

        return ussdResponse;
    }

    private UssdMessage process(State cur_state, UssdMessage ussdRequest, ShortCode_Msisdn SC_msisdn) {
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

        String transaction_id = ussdRequest.getTransId();
        String ussd_message = ussdRequest.getUssdString();

        String short_code = SC_msisdn.getShort_code();
        String msisdn = SC_msisdn.getMsisdn();

        if (cur_state != null) {
            String message = cur_state.getContent();
            State_Level level = cur_state.getState_level();
            int bizid = cur_state.getBizId();

            /*
                        prepare ussd notification whitout processing
             */
            if (level == State_Level.TERMINATE_RESP) {
                ussdResponse.setUssdString(message);
                ussdResponse.setType(USSDType.USSDMSG_TYPE_APP_SEND_RSP);
            }

            /*
                        prepare ussd notification and send command for processing
             */
            if (level == State_Level.TERMINATE_PROC) {
                Mo_Fake mo_fake = SET_MO_FAKE.get(bizid+"_"+ussd_message);

                if (mo_fake != null) {
                    String content = mo_fake.getCommand();
                    if (mo_fake.getParam() != null) {
                        content = content + mo_fake.getParam();
                    }

                    List<Service> listservices = Ussd_API_Conf.getSMS_ServiceMo(content.trim().toUpperCase(), short_code.trim().toUpperCase(), msisdn.trim().toUpperCase(), ussd_api_conf.getServices());

                    if (listservices.size() > 0) {
                        for (Service service : listservices) {
                            service = listservices.get(0);
                            String service_id = service.getService_id();
                            String topic_kafka = service.getMofilter().getTopic();
                            String trans_ID = Generator.getTransaction();
                            Message_Exchg msg_exch = new Message_Exchg(trans_ID, service_id, "", msisdn, short_code, content, "USSD");

                            // get and check user from config file 
                            String message_send = ConverterJSON.convertMsgExchToJson(msg_exch);
                            kafka_data = new ProducerRecord<String, String>(topic_kafka, trans_ID, message_send);
                            producer.send(kafka_data, callback);
                            logger.info("USSD Receive : " + message_send);
                            logger.info("SUCCESS SEND TO APPLICATION : topic -->" + topic_kafka + " | " + msisdn + " --> " + short_code + " [msg = " + content + " ]");
                        }
                    } else {
                        logger.warn("Service not define for Command in USSDGW_API file: \n"
                                + " Content =  " + content.trim().toUpperCase() + "\n "
                                + " Short Code = " + short_code.trim().toUpperCase() + "\n "
                                + " Msisdn = " + msisdn.trim().toUpperCase());
                    }

                    ussdResponse.setUssdString(message);
                    ussdResponse.setType(USSDType.USSDMSG_TYPE_APP_SEND_RSP);
                } else {
                    logger.warn("Command not define in MO_Fake : \n"
                            + " USSD Message =  " + ussd_message.trim().toUpperCase() + "\n "
                            + " Short Code = " + short_code.trim().toUpperCase() + "\n "
                            + " Msisdn = " + msisdn.trim().toUpperCase());
                }

            }

            /*
                 prepare ussd Menu notification
             */
            if (level == State_Level.INTERMEDIATE) {
                ussdResponse.setUssdString(message);
                ussdResponse.setType(USSDType.USSDMSG_TYPE_APP_SEND_MENU);
                ussdResponse.setTimeout(ussdMsg_timeout);
                PROCESSING_STATE.put(transaction_id, cur_state);
                PROCESSING_SHORTCODE.put(transaction_id, SC_msisdn);
            }
        } else {
            logger.warn("USSD Message don't define : \n"
                    + " USSD Message =  " + ussd_message.trim().toUpperCase() + "\n "
                    + " Short Code = " + short_code.trim().toUpperCase() + "\n "
                    + " Msisdn = " + msisdn.trim().toUpperCase());

        }
        return ussdResponse;
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
