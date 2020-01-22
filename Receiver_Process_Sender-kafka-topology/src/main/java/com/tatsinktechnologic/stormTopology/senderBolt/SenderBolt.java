package com.tatsinktechnologic.stormTopology.senderBolt;

import com.tatsinktechnologic.beans.Message_Exchg;
import com.tatsinktechnologic.beans.Notification_Conf;
import com.tatsinktechnologic.beans.Process_Request;
import com.tatsinktechnologic.dao.ConnectionPool;
import com.tatsinktechnologic.util.ConverterJSON;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

public class SenderBolt extends BaseBasicBolt {

	private static final long serialVersionUID = -5353547217135922477L;
        private static Logger logger = Logger.getLogger(SenderBolt.class);
        private static HashMap<String,Notification_Conf> SET_NOTIFICATION = null;
        private Properties topologyprops;
        private Properties producerprops;
        private Producer<String, String> producer;
        private ProducerCallback callback;
        private ProducerRecord<String, String> data;
        
         @Override
        public void prepare(Map stormConf, TopologyContext context) {
            super.prepare(stormConf, context); //To change body of generated methods, choose Tools | Templates.
            SET_NOTIFICATION = ConnectionPool.getSET_NOTIFICATION();
            topologyprops = new Properties();
            producerprops = new Properties();
            
            try {
                topologyprops.load(new FileInputStream("etc" + File.separator + "topology.properties"));
            } catch (IOException e) {
                logger.error("cannot load topology.properties", e);
            }
             
            try {
                producerprops.load(new FileInputStream("etc" + File.separator + "producer.properties"));
            } catch (IOException e) {
                logger.error("cannot load producer.properties", e);
            }
            
            producer = new KafkaProducer<String, String>(producerprops);
            callback = new ProducerCallback();
        }

	public void execute(Tuple input, BasicOutputCollector collector) {
             // get the sentence from the tuple and print it
             logger.info("################################## START PROCESSING IN SENDERBOLT ###########################");
             // Get the word from the tuple
             String word = input.getString(0);
             Timestamp receive_time = new Timestamp(System.currentTimeMillis());
             logger.info("request in SENDER Bolt   : "+ word);
             
             Process_Request process_req= ConverterJSON.convertJsonToProcess_rq(word);
             String key_notif = process_req.getNotification_code()+"_"+process_req.getLanguage();
             String content = SET_NOTIFICATION.get(key_notif.toUpperCase()).getParam_value();
             
             String new_client_id = producerprops.getProperty("client.id");
             String client_id= new_client_id+"_"+process_req.getMsisdn();
             producerprops.put("client.id", client_id);
             Random rnd = new Random();
             int key_val = rnd.nextInt();
             
             if (content!=null){
                Message_Exchg  msg_exch = new Message_Exchg("","",process_req.getChannel(), process_req.getMsisdn(), content);
             
                String message_send = ConverterJSON.convertMsgExchToJson(msg_exch);

                data = new ProducerRecord<String, String>(topologyprops.getProperty("procucer_topic"), "key-"+key_val+"-"+process_req.getMsisdn(),message_send );
                producer.send(data, callback);

                ConnectionPool.insertMt_his(process_req.getMsisdn(), content, process_req.getChannel(), receive_time);
                logger.info("MT SMS Sent : "+message_send);
                logger.info("SUCCESS SEND TO SMSGW : topic -->"+topologyprops.getProperty("procucer_topic")+" | " + process_req.getChannel() +" --> "+process_req.getMsisdn() +" [msg = "+content+" ]"); 
                
             }else{
                
                 logger.error("Error : content is not provid");  
             }
             
	}
        
        
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// here we declare we will be emitting tuples 
	}

	 private class ProducerCallback implements Callback {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if (e != null) {
                    System.out.println("Error while producing message to topic :" + recordMetadata);
                    e.printStackTrace();
                } else {
                    String message = String.format("Producer "+ producerprops.getProperty("client.id")+" sent message to topic:%s partition:%s  offset:%s", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
                    System.out.println(message);
                }
            }
   }
}
