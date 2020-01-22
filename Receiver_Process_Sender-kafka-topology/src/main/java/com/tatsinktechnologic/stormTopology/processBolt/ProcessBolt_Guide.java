package com.tatsinktechnologic.stormTopology.processBolt;

import com.tatsinktechnologic.beans.Process_Request;
import com.tatsinktechnologic.beans.Product;
import com.tatsinktechnologic.beans.Sub_Register;
import com.tatsinktechnologic.dao.ConnectionPool;
import com.tatsinktechnologic.stormTopology.main.*;
import com.tatsinktechnologic.util.ConverterJSON;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class ProcessBolt_Guide extends BaseBasicBolt {

	private static final long serialVersionUID = -5353547217135922477L;
        private static Logger logger = Logger.getLogger(ProcessBolt_Register.class);
        private static HashMap<String,Process_Request> COMMAND_CONF_HASH = null;
        private static HashMap<String,Product> SET_PRODUCT = null;

        
	// list used for aggregating the words
	private List<String> words = new ArrayList<String>();
        
         @Override
        public void prepare(Map stormConf, TopologyContext context) {
            super.prepare(stormConf, context); //To change body of generated methods, choose Tools | Templates.
            COMMAND_CONF_HASH = ConnectionPool.getCOMMAND_CONF_HASH();
            SET_PRODUCT = ConnectionPool.getSET_PRODUCT();
        }

	public void execute(Tuple input, BasicOutputCollector collector) {
             logger.info("################################## START PROCESSING IN PROCESSBOLT_GUIDE ###########################");
             // Get the word from the tuple
             String word = input.getString(0);
             Timestamp receive_time = new Timestamp(System.currentTimeMillis());
             logger.info("request in Process Bolt   : "+ word);
             Process_Request process_req= ConverterJSON.convertJsonToProcess_rq(word);
             String msisdn = process_req.getMsisdn();
             String content = process_req.getContent();
             String command = process_req.getCommand_code();
             String receiver = process_req.getReceiver();
             
             Product prod = SET_PRODUCT.get(process_req.getParam_value());
             int state = 0;
             if (prod != null){
                 /*
                   check daily validity of registration 
                 
                 */
                 Timestamp start_time = prod.getStart_time();
                 Timestamp end_time = prod.getEnd_time();

                 if (start_time!=null ){
                     
                     if (end_time!=null){
                         if (start_time.before(end_time)){
                             if (receive_time.before(end_time)){
                                state=0;
                             }else{
                                state=2;
                             }
                         }else{
                            state=3; 
                         }
                     }else{
                         state=0;
                     }
                     
                 }else {
                      if (end_time!=null){
                         
                         if (receive_time.before(end_time)){
                            state=0;
                         }else{
                            state=2;
                         }
                         
                     }else{
                         state=0;
                     }
                 }
             }else{
                 state = 1;
             }
             
             if(state == 1){
                    process_req.setNotification_code("GUIDE-PRODUCT-NOT-EXIST");
                    String process_request = ConverterJSON.convertProcess_rqToJson(process_req);
                    collector.emit(KafkaTopology.SENDER_STREAM,new Values(process_request));
                    logger.info("Emitte to SenderBolt : "+process_request);
                     
                    ConnectionPool.insertMo_his(msisdn,content, receiver, command, process_req.getParam_value(), receive_time, "", "PRODUCT NOT EXIST", 0, "ProcessBolt");
                    logger.info("insert into mo_his ");
             }
             
             if (state == 2){
                process_req.setNotification_code("GUIDE-PRODUCT-IS-EXPIRE-"+prod.getProduct_name());
                String process_request = ConverterJSON.convertProcess_rqToJson(process_req);
                collector.emit(KafkaTopology.SENDER_STREAM,new Values(process_request));
                logger.info("Emitte to SenderBolt : "+process_request);

                ConnectionPool.insertMo_his(msisdn,content, receiver, command, process_req.getParam_value(), receive_time, "", "PRODUCT IS EXPIRE :"+prod.getProduct_name(), 0, "ProcessBolt");
                logger.info("insert into mo_his ");
             }
             
             if (state == 3){
                process_req.setNotification_code("GUIDE-PRODUCT-WRONG-TIME-PERIOD-"+prod.getProduct_name());
                String process_request = ConverterJSON.convertProcess_rqToJson(process_req);
                collector.emit(KafkaTopology.SENDER_STREAM,new Values(process_request));
                logger.info("Emitte to SenderBolt : "+process_request);

                ConnectionPool.insertMo_his(msisdn,content, receiver, command, process_req.getParam_value(), receive_time,"", "PRODUCT WRONG TIME PERIOD OF "+prod.getProduct_name(), 0, "ProcessBolt");
                logger.info("insert into mo_his ");
             }
             
            
             if(state == 0){
                 
                 /*
                  check hour validity of registration
                 
                 */
                    String reg_day = prod.getRegister_day();
                    String reg_time = prod.getRegister_time();
                    
                    List<String> listDay = Arrays.asList(reg_day.split(";"));
                    int state1 = 0;
                    if (listDay.size()>0){
                        Calendar calendar = Calendar.getInstance();
                        String day = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));

                        if (listDay.contains(day)){
                            List<String> listHeure = Arrays.asList(reg_time.split("-"));
                            if (listHeure.size()>1){
                                    int cmphour1 = compareHour(listHeure.get(0));
                                    int cmphour2 = compareHour(listHeure.get(1));
                                    if (cmphour1<0){
                                        state1= 2;
                                    }else {
                                            if (cmphour2==1){
                                                state1=2;
                                            }else  {
                                                state1=0;
                                            }
                                    } 
                            }
                        }else {
                           state1=1; 
                        }
                    }else{
                      state1=0;  
                    }

                    if (state1 == 1){
                        process_req.setNotification_code("GUIDE-PRODUCT-NOT-VALID-DAY-"+prod.getProduct_name());
                        String process_request = ConverterJSON.convertProcess_rqToJson(process_req);
                        collector.emit(KafkaTopology.SENDER_STREAM,new Values(process_request));
                        logger.info("Emitte to SenderBolt : "+process_request);

                        ConnectionPool.insertMo_his(msisdn,content, receiver, command, process_req.getParam_value(), receive_time, "", "PRODUCT NOT VALID DAY of "+prod.getProduct_name(), 0, "ProcessBolt");
                        logger.info("insert into mo_his ");
                   }
                    
                    if (state1 == 2){
                        process_req.setNotification_code("GUIDE-PRODUCT-INVALID-FRAMETIME-"+prod.getProduct_name());
                        String process_request = ConverterJSON.convertProcess_rqToJson(process_req);
                        collector.emit(KafkaTopology.SENDER_STREAM,new Values(process_request));
                        logger.info("Emitte to SenderBolt : "+process_request);

                        ConnectionPool.insertMo_his(msisdn,content, receiver, command, process_req.getParam_value(), receive_time, "", "PRODUCT INVALID FRAME TIME OF "+prod.getProduct_name(), 0, "ProcessBolt");
                        logger.info("insert into mo_his ");
                   }

                   if (state1==0){
                       
                       /*
                        check if customer allow or deny to register from table

                       */
                       String table_allow = prod.getList_allow();
                       int state2 = 0;
                       if (table_allow!=null){
                           if (!ConnectionPool.checkAllowToRegister(table_allow, msisdn)){
                              state2 = 1; 
                           }
                       }else{
                         state2 = 0;  
                       }
                       
                       if (state2==1){
                            process_req.setNotification_code("GUIDE-CUSTOMER-DENY-TABLE-"+prod.getProduct_name());
                            String process_request = ConverterJSON.convertProcess_rqToJson(process_req);
                            collector.emit(KafkaTopology.SENDER_STREAM,new Values(process_request));
                            logger.info("Emitte to SenderBolt : "+process_request);

                            ConnectionPool.insertMo_his(msisdn,content, receiver, command, process_req.getParam_value(), receive_time, "", "CUSTOMER IS DENY IN TABLE : "+table_allow.toUpperCase()+" OF "+prod.getProduct_name(), 0, "ProcessBolt");
                            logger.info("insert into mo_his ");
                       }
                       
                       
                       if (state2==0){
                            process_req.setNotification_code("GUIDE-PRODUCT-SUCCESS-"+prod.getProduct_name());
                            String process_request = ConverterJSON.convertProcess_rqToJson(process_req);
                            collector.emit(KafkaTopology.SENDER_STREAM,new Values(process_request));
                            logger.info("Emitte to SenderBolt : "+process_request);

                            ConnectionPool.insertMo_his(msisdn,content, receiver, command, process_req.getParam_value(), receive_time, process_req.getAction_type(), "SUCCESS GET GUIDE : "+prod.getProduct_name(), prod.getReg_fee(), "ProcessBolt");
                            logger.info("insert into mo_his "); 

                          }
                     }
             }
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// here we declare we will be emitting tuples with
		// a single field called "sentence"
		declarer.declareStream(KafkaTopology.SENDER_STREAM, new Fields("sender"));
	}
        
        
        private int compareHour(String hour_value){
             try {
                Calendar now = Calendar.getInstance();

                Calendar date1 = Calendar.getInstance();
                date1.set(Calendar.HOUR_OF_DAY, date1.getTime().getHours() );
                date1.set(Calendar.MINUTE, date1.getTime().getMinutes() );
                date1.set(Calendar.SECOND, date1.getTime().getSeconds());

                String[] parts = hour_value.split(":");
                Calendar date2 = Calendar.getInstance();
                date2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
                date2.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
                date2.set(Calendar.SECOND, Integer.parseInt(parts[2]));

                if (date1.before(date2)) {
                    return -1;
                }else if (date1.after(date2)){
                    return 1;
                }else {
                    return 0;
                }
            } catch (Exception e) {
                return 2;
            }
  
               
        }

}
