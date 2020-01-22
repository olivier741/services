package com.tatsinktechnologic.stormTopology.receiverBolt;

import com.tatsinktechnologic.beans.Process_Request;
import com.tatsinktechnologic.beans.Message_Exchg;
import com.tatsinktechnologic.dao.ConnectionPool;
import com.tatsinktechnologic.stormTopology.main.*;
import com.tatsinktechnologic.util.ConverterJSON;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class ReceiverBolt extends BaseBasicBolt {

	private static final long serialVersionUID = -5353547217135922477L;
        private static Logger logger = Logger.getLogger(ReceiverBolt.class);
        private static HashMap<String,Process_Request> COMMAND_CONF_HASH = null;
        
        @Override
        public void prepare(Map stormConf, TopologyContext context) {
            super.prepare(stormConf, context); //To change body of generated methods, choose Tools | Templates.
            COMMAND_CONF_HASH = ConnectionPool.getCOMMAND_CONF_HASH();
        }

	public void execute(Tuple input, BasicOutputCollector collector) {
                logger.info("################################## START PROCESSING IN RECEIVERBOLT ###########################");
                
		String word = input.getString(0);
                Timestamp receive_time = new Timestamp(System.currentTimeMillis());
                logger.info("request in receiver Bolt   : "+ word);
                Message_Exchg msg_exch= ConverterJSON.convertJsonToMsgExch(word);
                
                String content = msg_exch.getContent().toUpperCase().trim();
                String receiver= msg_exch.getReceiver().toUpperCase().trim();
                String sender = msg_exch.getSender().toUpperCase().trim();
                
                List<Process_Request>  listCommand_conf = getCheck_CommandConf(content);
                
                logger.info("content send by customer   : "+ content);
                logger.info("customer phone number      : "+ sender);
                logger.info("short code                 : "+ receiver);
                
                if (listCommand_conf.size()>0){
                    // good syntax send by customer
                    Process_Request current_cmd_conf = listCommand_conf.get(0);
                    current_cmd_conf.setReceiver(receiver);
                    current_cmd_conf.setMsisdn(sender);
                    current_cmd_conf.setContent(content);
                    List<String> listCommand =  Arrays.asList(current_cmd_conf.getParam_pattern_separate().split(content)); 
                    String param_value = "";
                    for (int i = 1 ; i<listCommand.size(); i++ ){
                        //check parameter 
                       param_value+=listCommand.get(i)+" ";
                        
                   } 
                    current_cmd_conf.setParam_value(param_value.toUpperCase().trim());
                    String process_request = ConverterJSON.convertProcess_rqToJson(current_cmd_conf);
                    
                    if (current_cmd_conf.getAction_type().equals("REG")){
                        collector.emit(KafkaTopology.PROCESS_STREAM_REG,new Values(process_request));
                        logger.info("Emitte to Register Process : "+process_request);
                    }else if (current_cmd_conf.getAction_type().equals("CHECK")){
                        collector.emit(KafkaTopology.PROCESS_STREAM_CHECK,new Values(process_request));
                        logger.info("Emitte to Check Process : "+process_request);
                    }else if (current_cmd_conf.getAction_type().equals("DEL")){
                        collector.emit(KafkaTopology.PROCESS_STREAM_DEL,new Values(process_request));
                        logger.info("Emitte to Delete Process : "+process_request);
                    }else if (current_cmd_conf.getAction_type().equals("GUIDE")){
                        collector.emit(KafkaTopology.PROCESS_STREAM_GUIDE,new Values(process_request));
                        logger.info("Emitte to Guide Process : "+process_request);
                    }else{
                        //action not existe
                        current_cmd_conf.setNotification_code("ACTION-NOT-DEFINE");

                        collector.emit(KafkaTopology.SENDER_STREAM,new Values(process_request));
                        logger.info("Emitte to SenderBolt : "+process_request);

                        ConnectionPool.insertMo_his(current_cmd_conf.getMsisdn(),current_cmd_conf.getContent(), receiver, current_cmd_conf.getCommand_code(), current_cmd_conf.getParam_value(), receive_time, "", "ACTION NOT DEFINE", 0, "ReceiverBolt");
                        logger.info("insert into mo_his ");
                    }
                    

                }else{
                    // wrong syntax send by customer
                    Process_Request current_cmd_conf = new Process_Request();
                    current_cmd_conf.setReceiver(receiver);
                    current_cmd_conf.setMsisdn(sender);
                    current_cmd_conf.setContent(content);
                    current_cmd_conf.setNotification_code("RECEIVER-WRONG-SYNTAX");
                    String process_request = ConverterJSON.convertProcess_rqToJson(current_cmd_conf);
                    
                    collector.emit(KafkaTopology.SENDER_STREAM,new Values(process_request));
                    logger.info("Emitte to SenderBolt : "+process_request);
                     
                    ConnectionPool.insertMo_his(sender,content, receiver, current_cmd_conf.getCommand_code(), current_cmd_conf.getParam_value(), receive_time, "", "WRONG SYNTAX", 0, "ReceiverBolt");
                    logger.info("insert into mo_his ");
                }
                
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// here we declare we will be emitting tuples 
		declarer.declareStream(KafkaTopology.SENDER_STREAM,         new Fields("sender"));
		declarer.declareStream(KafkaTopology.PROCESS_STREAM_REG,    new Fields("process_reg"));
                declarer.declareStream(KafkaTopology.PROCESS_STREAM_CHECK,  new Fields("process_check"));
                declarer.declareStream(KafkaTopology.PROCESS_STREAM_DEL,    new Fields("process_del"));
                declarer.declareStream(KafkaTopology.PROCESS_STREAM_GUIDE,  new Fields("process_guide"));
	}
        
        
    
     private  static List<Process_Request> getCheck_CommandConf(final String command){
         
        List<Process_Request> result =new ArrayList<Process_Request>();
        
        for (Map.Entry cmd_conf : COMMAND_CONF_HASH.entrySet()) {
            result.add((Process_Request)cmd_conf.getValue());
        }
         
         CollectionUtils.filter(result, new Predicate() {

            @Override
            public boolean evaluate(Object o) {
                
                int check_lengh_param;
                String check_param_pattern= null;
                String check_cmd = null;
                String separator = null;
                
                // get separator from database. if not separator, get space as default
                separator = ((Process_Request) o).getParam_pattern_separate();
                if (separator == null) separator= "\\s+";
                
                // get number of parameter from database
                check_lengh_param= ((Process_Request) o).getParam_length();
                
                // get regex String of parameter from database
                check_param_pattern= ((Process_Request) o).getParam_pattern();
                
                // get command from database
                check_cmd = ((Process_Request) o).getCommand_code().toUpperCase().trim();
                
                // build regex pattern to check parameter
                Pattern pattern_param = Pattern.compile(check_param_pattern.toUpperCase().trim());
                
                // build regex pattern to check separator between command or parameter
                Pattern pattern_separator = Pattern.compile(separator.toUpperCase().trim());

                // get list command + param send by customer
                List<String> listCommand =  Arrays.asList(pattern_separator.split(command)); 
                
                //check number of parameter 
                boolean match_lenght = (listCommand.size()-1) == check_lengh_param;
                
                //check command
                boolean match_cmd = check_cmd.equals(listCommand.get(0).toUpperCase().trim());

                boolean match_separator= true;
                boolean match_param = true;
                
                if (check_lengh_param>0){
                    // check separator
                    match_separator= pattern_separator.matcher(command).find();
                    
                    for (int i = 1 ; i<listCommand.size(); i++ ){
                        //check parameter 
                        match_param = match_param && pattern_param.matcher(listCommand.get(i).toUpperCase().trim()).find();
                        
                   }  
                    
                }

                return  match_cmd && match_separator && match_lenght && match_param;
            }

        });
        
        return result;
    }
}
