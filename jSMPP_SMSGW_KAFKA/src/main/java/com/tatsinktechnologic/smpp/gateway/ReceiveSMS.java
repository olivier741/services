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
import com.tatsinktechnologic.beans.ReceiverResponse;
import com.tatsinktechnologic.configuration.ConfigLoader;
import com.tatsinktechnologic.util.ConverterJSON;
import com.tatsinktechnologic.util.Generator;
import com.tatsinktechnologic.xml.service_listener.Service;
import com.tatsinktechnologic.xml.service_listener.Service_listener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang.SerializationUtils;
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
 * @author olivier
 */
public class ReceiveSMS implements MessageReceiverListener {

    private static final ExecutorService pool = Executors.newFixedThreadPool(10);

    private static final Logger logger = Logger.getLogger(ReceiveSMS.class);

    private ConfigLoader xmlConfig;
    private Service_listener service_listener;
    private int smpp_enableReport;
    private String smpp_dateFormat;
    private Properties producerprops = new Properties();
    private Producer<String, String> producer;
    private ProducerCallback callback;
    private ProducerRecord<String, String> kafka_data;

    public ReceiveSMS(String user, String id) {
        super();
        this.xmlConfig = ConfigLoader.getConfigurationLoader();

        this.service_listener = xmlConfig.getService_listener();
        this.smpp_enableReport = service_listener.getSmpp_report();
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("etc" + File.separator + "producer.properties"));
        } catch (IOException e) {
            logger.error("cannot load Oracle config file", e);
        }
        producerprops = (Properties) SerializationUtils.clone(props);

        String new_client_id = producerprops.getProperty("client.id");
        new_client_id = new_client_id + "_" + id;
        producerprops.put("client.id", new_client_id);

        producer = new KafkaProducer<String, String>(producerprops);
        callback = new ProducerCallback();
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

                    String delivery_topic = service_listener.getDelivery_topic();

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
                    kafka_data = new ProducerRecord<String, String>(delivery_topic, message_send);
                    producer.send(kafka_data, callback);
                    logger.info("Delivery Receive : " + message_send);
                    logger.info("SUCCESS DELIVERY report  topic -->" + delivery_topic + " |   message: msg_id = " + messageId + " from " + arg0.getSourceAddr() + " --> " + arg0.getDestAddress());
                    logger.info("SUCCESS DELIVERY information : " + delReceipt);
                    logger.info("SUCCESS  send delivery report : " + delivery_msg);

                } catch (InvalidDeliveryReceiptException e) {
                    logger.error("Failed getting delivery receipt", e);
                }

                logger.info("SMSC_DEL_RECEIPT : " + arg0.toString());
            } else {
                logger.info("-------------- START RECEIVE MO SMS -----------");

                String receiver = arg0.getDestAddress();
                String sender = arg0.getSourceAddr();
                byte[] data = arg0.getShortMessage();
                String content = null;
                if (arg0.getShortMessage() != null) {
                    if (arg0.getDataCoding() == (byte) 8) {
                        content = HexUtil.convertBytesToHexString(data, 0, data.length);
                    } else {
                        content = new String(data);
                    }
                }

                List<Service> listservices = Service_listener.getSMS_ServiceMo(content.trim().toUpperCase(), receiver.trim().toUpperCase(), sender.trim().toUpperCase(), service_listener.getServices());

                if (listservices.size() > 0) {
                    for (Service service : listservices) {
                        service = listservices.get(0);
                        String service_id = service.getService_id();
                        String topic_kafka = service.getMofilter().getTopic();
                        Message_Exchg msg_exch = new Message_Exchg(Generator.getTransaction(), service_id, arg0.getId(), sender, receiver, content, "SMS");

                        // get and check user from config file 
                        String message_send = ConverterJSON.convertMsgExchToJson(msg_exch);
                        kafka_data = new ProducerRecord<String, String>(topic_kafka, message_send);
                        producer.send(kafka_data, callback);
                        logger.info("SMS Receive : " + message_send);
                        logger.info("SUCCESS SEND TO APPLICATION : topic -->" + topic_kafka + " | " + sender + " --> " + receiver + " [msg = " + content + " ]");
                    }

                } else {
                    logger.warn("No Service provide to process this SMS");
                    logger.warn("Lossing SMS: " + sender + " --> " + receiver + " [msg = " + content + " ]");
                }

            }

        } else {
            if (!MessageType.SMSC_DEL_RECEIPT.containedIn(arg0.getEsmClass())) {
                logger.info("-------------- START RECEIVE MO SMS -----------");

                String receiver = arg0.getDestAddress();
                String sender = arg0.getSourceAddr();
                byte[] data = arg0.getShortMessage();
                String content = null;
                if (arg0.getShortMessage() != null) {
                    if (arg0.getDataCoding() == (byte) 8) {
                        content = HexUtil.convertBytesToHexString(data, 0, data.length);
                    } else {
                        content = new String(data);
                    }
                }

                List<Service> listservices = Service_listener.getSMS_ServiceMo(content.trim().toUpperCase(), receiver.trim().toUpperCase(), sender.trim().toUpperCase(), service_listener.getServices());

                if (listservices.size() > 0) {
                    for (Service service : listservices) {
                        service = listservices.get(0);
                        String service_id = service.getService_id();
                        String topic_kafka = service.getMofilter().getTopic();
                        Message_Exchg msg_exch = new Message_Exchg(Generator.getTransaction(), service_id, arg0.getId(), sender, receiver, content, "SMS");

                        // get and check user from config file 
                        String message_send = ConverterJSON.convertMsgExchToJson(msg_exch);
                        kafka_data = new ProducerRecord<String, String>(topic_kafka, message_send);
                        producer.send(kafka_data, callback);
                        logger.info("SMS Receive : " + message_send);
                        logger.info("SUCCESS SEND TO APPLICATION : topic -->" + topic_kafka + " | " + sender + " --> " + receiver + " [msg = " + content + " ]");
                    }

                } else {
                    logger.warn("No Service provide to process this SMS");
                    logger.warn("Lossing SMS: " + sender + " --> " + receiver + " [msg = " + content + " ]");
                }
            }else{
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

                    String delivery_topic = service_listener.getDelivery_topic();

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
                    logger.info("########## SMSGW ARE NOT SETTING TO FOWARD DELIVERY REPORT ################## " );
                    logger.info("Delivery Receive : " + message_send);
                    logger.info("SUCCESS DELIVERY report  topic -->" + delivery_topic + " |   message: msg_id = " + messageId + " from " + arg0.getSourceAddr() + " --> " + arg0.getDestAddress());
                    logger.info("SUCCESS DELIVERY information : " + delReceipt);
                    logger.info("SUCCESS  send delivery report : " + delivery_msg);
                } catch (Exception e) {
                      logger.error("Failed getting delivery receipt", e);
                }
                
            }
        }

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

    private class ProducerCallback implements Callback {

        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null) {
                logger.error("Error while producing message to topic :" + recordMetadata, e);
            } else {
                String message = String.format("Producer client ID :  " + producerprops.getProperty("client.id") + " -- Topic: %s -- Partition: %s -- offset: %s", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
                logger.info(message);
            }
        }
    }

}
