/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.receiver;

import com.tatsinktechnologic.beans.Message_Exchg;
import com.tatsinktechnologic.beans.Process_Request;
import com.tatsinktechnologic.beans_entity.Action_Type;
import com.tatsinktechnologic.beans_entity.Command;
import com.tatsinktechnologic.beans_entity.Mo_Hist;
import com.tatsinktechnologic.beans_entity.Request_Conf;
import com.tatsinktechnologic.loadconfig.Load_Configuration;
import com.tatsinktechnologic.dao_repository.Mo_HistJpaController;
import com.tatsinktechnologic.process.Process_AccountMng;
import com.tatsinktechnologic.process.Process_Add_ChatGroup;
import com.tatsinktechnologic.process.Process_ChangeAlias;
import com.tatsinktechnologic.process.Process_Check;
import com.tatsinktechnologic.process.Process_Delete;
import com.tatsinktechnologic.process.Process_Guide;
import com.tatsinktechnologic.process.Process_Register;
import com.tatsinktechnologic.sender.Sender;
import com.tatsinktechnologic.util.ConverterXML_JSON;
import com.tatsinktechnologic.xml.Application;
import com.tatsinktechnologic.xml.Kafka_Conf;
import com.tatsinktechnologic.xml.ReceiverThread_Conf;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
import javax.persistence.EntityManagerFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author olivier
 */
public class Receiver implements Runnable {

    private Logger logger = Logger.getLogger(Receiver.class);
    private EntityManagerFactory emf;
    private Properties consumerprops;
    private int id;
    private int numberRecord;
    private String client_id;
    private String topic;
    private int sleep_duration;
    private static Kafka_Conf kafka_conf;
    private static Application app_conf;
    private static ReceiverThread_Conf receiverthead_conf;
    private InetAddress address;
    private static List<Request_Conf> ListCOMMAND_CONF = null;
    private static HashMap<String, Command> SETCOMMAND = null;
    private static Load_Configuration commonConfig = Load_Configuration.getConfigurationLoader();

    public Receiver(int id) {
        this.emf = commonConfig.getEmf();
        this.id = id;
        this.app_conf = commonConfig.getApp_conf();
        this.kafka_conf = app_conf.getKafka_conf();
        this.topic = kafka_conf.getConsumer_topic();
        this.numberRecord = kafka_conf.getNumberRecord();
        this.receiverthead_conf = app_conf.getReceiver_thread();
        this.sleep_duration = receiverthead_conf.getSleep_duration();

        consumerprops = (Properties)SerializationUtils.clone(commonConfig.getConsummerProps());

        String new_client_id = consumerprops.getProperty("client.id");
        this.client_id = new_client_id + "_" + id;
        consumerprops.put("client.id", client_id);

        ListCOMMAND_CONF = commonConfig.getListCOMMAND_CONF();
        SETCOMMAND = commonConfig.getSETCOMMAND();
        address = gethostName();

        logger.info("LIST OF COMMAND : ");
        for (Request_Conf req_conf : ListCOMMAND_CONF) {
            logger.info(req_conf);
        }
    }

    @Override
    public void run() {

        KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(consumerprops);
        ReceiverConsumerRebalanceListener rebalanceListener = new ReceiverConsumerRebalanceListener();
        consumer.subscribe(Collections.singletonList(topic), rebalanceListener);

        logger.info("################################## START RECEIVER ###########################");

        while (true) {
            ConsumerRecords<byte[], byte[]> records = consumer.poll(numberRecord);

            for (ConsumerRecord<byte[], byte[]> record : records) {

                String word = String.valueOf(record.value());
                Timestamp receive_time = new Timestamp(System.currentTimeMillis());

                logger.info("request receive   : " + word);
                Message_Exchg msg_exch = ConverterXML_JSON.convertJsonToMsgExch(word);

                String transaction_id = msg_exch.getTransaction_id();
                String content = msg_exch.getContent().toUpperCase().trim();
                String receiver = msg_exch.getReceiver().toUpperCase().trim();
                String msisdn = msg_exch.getSender().toUpperCase().trim();
                String exchange_mode = msg_exch.getExchange_mode().toUpperCase().trim();

                List<Request_Conf> listCommand_conf = getCheck_CommandConf(content);

                logger.info("transaction_id   : " + transaction_id);
                logger.info("content send by customer   : " + content);
                logger.info("customer phone number      : " + msisdn);
                logger.info("short code                 : " + receiver);

                if (listCommand_conf.size() > 0) {
                    // good syntax send by customer
                    Request_Conf current_cmd_conf = listCommand_conf.get(0);
                    Process_Request process_req = new Process_Request();

                    process_req.setTransaction_id(transaction_id);
                    process_req.setReceiver(receiver);
                    process_req.setMsisdn(msisdn);
                    process_req.setContent(content);
                    process_req.setAction_description(current_cmd_conf.getAction_description());
                    process_req.setAction_type(current_cmd_conf.getAction_type());
                    process_req.setChannel(current_cmd_conf.getReceive_channel());
                    process_req.setCommand_code(current_cmd_conf.getCommand_code());
                    process_req.setParam_length(current_cmd_conf.getParam_length());
                    process_req.setParam_name(current_cmd_conf.getParam_name());
                    process_req.setParam_pattern(current_cmd_conf.getParam_pattern());
                    process_req.setProduct_name(current_cmd_conf.getProduct_code());
                    process_req.setReceive_time(receive_time);
                    process_req.setSplit_separate(current_cmd_conf.getSplit_separator());
                    process_req.setExchange_mode(exchange_mode);

                    String split_sep = "\\s+";
                    if (current_cmd_conf.getSplit_separator() != null) {
                        split_sep = current_cmd_conf.getSplit_separator();
                    }
                    List<String> listCommand = Arrays.asList(content.split(split_sep));
                    String param_value = "";
                    for (int i = 1; i < listCommand.size(); i++) {
                        //check parameter 
                        param_value += listCommand.get(i) + " ";

                    }
                    process_req.setParam_value(param_value.toUpperCase().trim());

                    if (current_cmd_conf.getAction_type() != null) {

                        Action_Type action_type = current_cmd_conf.getAction_type();
                        switch (action_type) {
                            case REGISTER:
                                Process_Register.addMo_Queue(process_req);
                                logger.info("Emitte to Register Process : " + process_req);
                                break;
                            case CHECK_REG:
                                Process_Check.addMo_Queue(process_req);
                                logger.info("Emitte to Check Process : " + process_req);
                                break;
                            case DELETE_REG:
                                Process_Delete.addMo_Queue(process_req);
                                logger.info("Emitte to Delete Process : " + process_req);
                                break;
                            case GUIDE_REG:
                                Process_Guide.addMo_Queue(process_req);
                                logger.info("Emitte to Guide Process : " + process_req);
                                break;
                            case LIST_REG:
//                                Process_ListReg.addMo_Queue(process_req);
                                logger.info("Emitte to List Registration : " + process_req);
                                break;
                            case ACC_CHANGE_ALIAS:
                                Process_ChangeAlias.addMo_Queue(process_req);
                                logger.info("Emitte to Change Alias : " + process_req);
                                break;
                            case ACC_ADD_CHATGROUP:
                                Process_Add_ChatGroup.addMo_Queue(process_req);
                                logger.info("Emitte to Change Alias : " + process_req);
                                break;
                            case ACC_DEL_CHATGROUP:
                                
                            case ACC_LIST_ALL_CHATGROUP:
                            case ACC_LIST_REG_CHATGROUP:
                            case ACC_LIST_NOTREG_CHATGROUP:
                                Process_AccountMng.addMo_Queue(process_req);
                                logger.info("Emitte to List Registration : " + process_req);
                                break;
                            default:
                                //action not existe
                                process_req.setNotification_code("ACTION-NOT-DEFINE");

                                // send to sender
                                Sender.addMo_Queue(process_req);

                                Command cmd = SETCOMMAND.get(process_req.getCommand_code());
                                Mo_Hist mo_hist = new Mo_Hist();
                                mo_hist.setAction_type(process_req.getAction_type());
                                mo_hist.setChannel(process_req.getChannel());
                                mo_hist.setContent(content);
                                mo_hist.setMsisdn(msisdn);
                                mo_hist.setReceive_time(receive_time);
                                mo_hist.setCommand(cmd);
                                mo_hist.setTransaction_id(transaction_id);
                                mo_hist.setProcess_unit("Receiver");
                                mo_hist.setIP_unit(address.getHostName() + "@" + address.getHostAddress());
                                mo_hist.setError_description("ACTION NOT DEFINE");
                                mo_hist.setExchange_mode(exchange_mode);

                                Mo_HistJpaController mo_histController = new Mo_HistJpaController(emf);
                                mo_histController.create(mo_hist);

                                logger.info("insert into mo_his ");

                                break;

                        }

                    } else {
                        process_req.setNotification_code("RECEIVER-NOT-ACTION-TYPE");
                        process_req.setReceive_time(receive_time);
                        // send to sender
                        Sender.addMo_Queue(process_req);

                        Command cmd = SETCOMMAND.get(process_req.getCommand_code());
                        Mo_Hist mo_hist = new Mo_Hist();
                        mo_hist.setAction_type(process_req.getAction_type());
                        mo_hist.setChannel(process_req.getChannel());
                        mo_hist.setContent(content);
                        mo_hist.setMsisdn(msisdn);
                        mo_hist.setReceive_time(receive_time);
                        mo_hist.setTransaction_id(transaction_id);
                        mo_hist.setCommand(cmd);
                        mo_hist.setProcess_unit("Receiver");
                        mo_hist.setIP_unit(address.getHostName() + "@" + address.getHostAddress());
                        mo_hist.setError_description("NOT ACTION TYPE");
                        mo_hist.setExchange_mode(exchange_mode);

                        Mo_HistJpaController mo_histController = new Mo_HistJpaController(emf);
                        mo_histController.create(mo_hist);

                        logger.info("insert into mo_his ");
                    }

                } else {
                    // wrong syntax send by customer
                    Process_Request process_req = new Process_Request();
                    process_req.setTransaction_id(transaction_id);
                    process_req.setReceiver(receiver);
                    process_req.setMsisdn(msisdn);
                    process_req.setReceive_time(receive_time);
                    process_req.setContent(content);
                    process_req.setNotification_code("RECEIVER-WRONG-SYNTAX");

                    // send to sender
                    Sender.addMo_Queue(process_req);

                    Mo_Hist mo_hist = new Mo_Hist();
                    mo_hist.setAction_type(process_req.getAction_type());
                    mo_hist.setChannel(process_req.getChannel());
                    mo_hist.setContent(content);
                    mo_hist.setMsisdn(msisdn);
                    mo_hist.setReceive_time(receive_time);
                    mo_hist.setTransaction_id(transaction_id);
                    mo_hist.setProcess_unit("Receiver");
                    mo_hist.setIP_unit(address.getHostName() + "@" + address.getHostAddress());
                    mo_hist.setError_description("WRONG SYNTAX");
                    mo_hist.setExchange_mode(exchange_mode);

                    Mo_HistJpaController mo_histController = new Mo_HistJpaController(emf);
                    mo_histController.create(mo_hist);

                    logger.info("insert into mo_his ");

                }
            }

            consumer.commitSync();

            try {
                Thread.sleep(sleep_duration);
            } catch (Exception e) {

            }
        }

    }

    private static class ReceiverConsumerRebalanceListener implements ConsumerRebalanceListener {

        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            System.out.println("Called onPartitionsRevoked with partitions:" + partitions);
        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
            System.out.println("Called onPartitionsAssigned with partitions:" + partitions);
        }
    }

    private static List<Request_Conf> getCheck_CommandConf(final String command) {

        List<Request_Conf> result = new ArrayList<Request_Conf>();

        for (Request_Conf cmd_conf : ListCOMMAND_CONF) {
            result.add(cmd_conf);
        }

        CollectionUtils.filter(result, new Predicate() {

            @Override
            public boolean evaluate(Object o) {

                Integer check_lengh_param;
                String check_param_pattern = null;
                String check_cmd = null;
                String separator = null;

                // get separator from database. if not separator, get space as default
                separator = ((Request_Conf) o).getSplit_separator();
                if (StringUtils.isBlank(separator)) {
                    separator = "\\s+";
                }

                // get number of parameter from database
                check_lengh_param = ((Request_Conf) o).getParam_length();
                if (check_lengh_param == null) {
                    check_lengh_param = 0;
                }

                // get regex String of parameter from database
                check_param_pattern = ((Request_Conf) o).getParam_pattern();
                if (StringUtils.isBlank(check_param_pattern)) {
                    check_param_pattern = "";
                }

                // get command from database
                check_cmd = ((Request_Conf) o).getCommand_code().toUpperCase().trim();

                // build regex pattern to check parameter
                Pattern pattern_param = Pattern.compile(check_param_pattern);

                // build regex pattern to check separator between command or parameter
                Pattern pattern_separator = Pattern.compile(separator);

                // get list command + param send by customer
                List<String> listCommand = Arrays.asList(pattern_separator.split(command));

                //check number of parameter 
                boolean match_lenght = true;
                if (check_lengh_param != 0) {
                    match_lenght = (listCommand.size() - 1) == check_lengh_param;
                }

                //check command
                boolean match_cmd = check_cmd.equals(listCommand.get(0).toUpperCase().trim());

                boolean match_param = true;

                if (check_lengh_param > 0 && match_lenght) {
                    // check separator                    
                    for (int i = 1; i < listCommand.size(); i++) {
                        //check parameter 
                        match_param = match_param && pattern_param.matcher(listCommand.get(i).toUpperCase().trim()).find();
                    }

                }

                return match_cmd && match_lenght && match_param;
            }

        });

        return result;
    }

    private InetAddress gethostName() {

        InetAddress addr = null;

        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            logger.error("Hostname can not be resolved", ex);
        }
        return addr;
    }

    public String convertDate_String(Timestamp date, String format) {

        /*
     * To convert java.util.Date to String, use SimpleDateFormat class.
         */
 /*
     * crate new SimpleDateFormat instance with desired date format.
      * We are going to use yyyy-mm-dd hh:mm:ss here.
         */
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat(format);

        //to convert Date to String, use format method of SimpleDateFormat class.
        String strDate = dateFormat.format(date);

        return strDate;
    }

    public static void executeRunnables(final ExecutorService service, List<Runnable> runnables) {
        //On exécute chaque "Runnable" de la liste "runnables"
        for (Runnable r : runnables) {

            service.execute(r);
        }
        service.shutdown();
    }

}
