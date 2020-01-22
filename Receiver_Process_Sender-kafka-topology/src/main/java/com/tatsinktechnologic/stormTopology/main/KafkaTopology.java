package com.tatsinktechnologic.stormTopology.main;

import com.tatsinktechnologic.stormTopology.processBolt.ProcessBolt_Check;
import com.tatsinktechnologic.stormTopology.processBolt.ProcessBolt_Del;
import com.tatsinktechnologic.stormTopology.processBolt.ProcessBolt_Guide;
import com.tatsinktechnologic.stormTopology.processBolt.ProcessBolt_Register;
import com.tatsinktechnologic.stormTopology.receiverBolt.ReceiverBolt;
import com.tatsinktechnologic.stormTopology.senderBolt.SenderBolt;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class KafkaTopology {
    
    private static Logger logger = Logger.getLogger(KafkaTopology.class);
    
    public static final String SENDER_STREAM = "sender";
    public static final String PROCESS_STREAM_REG = "process_reg";
    public static final String PROCESS_STREAM_CHECK = "process_check";
    public static final String PROCESS_STREAM_DEL = "process_del";
    public static final String PROCESS_STREAM_GUIDE = "process_guide";
    
    public static void main(String[] args) {
            
                LocalCluster cluster = new LocalCluster();
                
                Properties confprops = new Properties();
                
                 PropertyConfigurator.configure("etc" + File.separator + "log4j.cfg");
                 logger.info("Load log4j config file done.");

      
                try {
                      confprops.load(new FileInputStream("etc" + File.separator + "topology.properties"));
                  } catch (IOException e) {
                      logger.error("cannot load topology.properties", e);
                  }
                
                String zookeeper_host = confprops.getProperty("zookeeper_host");
                String zookeeper_root = confprops.getProperty("zookeeper_root");
                String group_id=confprops.getProperty("group_id");
                String consumer_topic = confprops.getProperty("consumer_topic");
                
                String kafkaSpout_Name = confprops.getProperty("KafkaSpout_Name");
                int kafkaSpout_NumThread = 4;
                
                try { 
                   kafkaSpout_NumThread = Integer.parseInt(confprops.getProperty("KafkaSpout_NumThread")); 
                } catch(Exception e) { 
                   logger.error("kafkaSpout_NumThread parameter have wrong value. System start with default value kafkaSpout_NumThread = 4 ",e);
                } 
                
                int kafkaSpout_NumTask = 0;
                try { 
                   kafkaSpout_NumTask = Integer.parseInt(confprops.getProperty("KafkaSpout_NumTask")); 
                } catch(Exception e) { 
                   logger.error("kafkaSpout_NumTask parameter have wrong value. System start with default value kafkaSpout_NumTask = 0 ",e);
                } 

                String rcvBolt_Name = confprops.getProperty("RcvBolt_Name");
                int rcvBolt_NumThread = 4;
                try { 
                   rcvBolt_NumThread = Integer.parseInt(confprops.getProperty("RcvBolt_NumThread")); 
                } catch(Exception e) { 
                   logger.error("RcvBolt_NumThread parameter have wrong value. System start with default value RcvBolt_NumThread = 4 ",e);
                } 
                
                int rcvBolt_NumTask = 0;
                try { 
                   rcvBolt_NumTask = Integer.parseInt(confprops.getProperty("RcvBolt_NumTask")); 
                } catch(Exception e) { 
                   logger.error("RcvBolt_NumTask parameter have wrong value. System start with default value RcvBolt_NumTask = 0 ",e);
                } 
                
                String sendBolt_Name = confprops.getProperty("SendBolt_Name");
                int sendBolt_NumThread = 4;

                 try { 
                   sendBolt_NumThread = Integer.parseInt(confprops.getProperty("SendBolt_NumThread")); 
                } catch(Exception e) { 
                   logger.error("SendBolt_NumThread parameter have wrong value. System start with default value SendBolt_NumThread = 4 ",e);
                } 
                
                int sendBolt_NumTask = 0;
                try { 
                   sendBolt_NumTask = Integer.parseInt(confprops.getProperty("SendBolt_NumTask")); 
                } catch(Exception e) { 
                   logger.error("SendBolt_NumTask parameter have wrong value. System start with default value SendBolt_NumTask = 0 ",e);
                } 
                
                
                String processBolt_Name = confprops.getProperty("ProcessBolt_Name");
                int processBolt_NumThread = 4;
                
                 try { 
                   processBolt_NumThread = Integer.parseInt(confprops.getProperty("ProcessBolt_NumThread")); 
                } catch(Exception e) { 
                   logger.error("ProcessBolt_NumThread parameter have wrong value. System start with default value ProcessBolt_NumThread = 4 ",e);
                } 
                 
                int processBolt_NumTask = 0;
                try { 
                   processBolt_NumTask = Integer.parseInt(confprops.getProperty("ProcessBolt_NumTask")); 
                } catch(Exception e) { 
                   logger.error("ProcessBolt.NumTask parameter have wrong value. System start with default value ProcessBolt.NumTask = 0 ",e);
                } 
                
                String topology_Name = confprops.getProperty("topology_Name");
                int topology_Debug = 0;
                 try { 
                   topology_Debug = Integer.parseInt(confprops.getProperty("topology_Debug")); 
                } catch(Exception e) { 
                   logger.error("topology_Debug parameter have wrong value. System start with default value topology_Debug = 0 ",e);
                } 
                
                int topology_MaxSpoutPending = 0;
                 try { 
                   topology_MaxSpoutPending = Integer.parseInt(confprops.getProperty("topology_MaxSpoutPending")); 
                } catch(Exception e) { 
                   logger.error("topology_MaxSpoutPending parameter have wrong value. System start with default value ",e);
                } 
                 
                int topology_NumWorkers = 4;
                 try { 
                   topology_NumWorkers = Integer.parseInt(confprops.getProperty("topology_NumWorkers")); 
                } catch(Exception e) { 
                   logger.error("topology_NumWorkers parameter have wrong value. System start with default value topology.NumWorkers = 4 ",e);
                } 
                 
                int topology_MessageTimeoutSecs = 0;
                 try { 
                   topology_MessageTimeoutSecs = Integer.parseInt(confprops.getProperty("topology_MessageTimeoutSecs")); 
                } catch(Exception e) { 
                   logger.error("topology_MessageTimeoutSecs parameter have wrong value. System start with default value",e);
                } 
                 
                int topology_MaxTaskParallelism = 0;
                 try { 
                   topology_MaxTaskParallelism = Integer.parseInt(confprops.getProperty("topology_MaxTaskParallelism")); 
                } catch(Exception e) { 
                   logger.error("topology_MaxTaskParallelism parameter have wrong value. System start with default value",e);
                } 
                 
                int topology_NumAckers = 0;
                 try { 
                   topology_NumAckers = Integer.parseInt(confprops.getProperty("topology_NumAckers")); 
                } catch(Exception e) { 
                   logger.error("topology_NumAckers parameter have wrong value. System start with default value",e);
                } 
                 
		try {
			// zookeeper hosts for the Kafka cluster
			BrokerHosts zkHosts = new ZkHosts(zookeeper_host);

			// Create the KafkaSpout configuartion
			// Second argument is the topic name
			// Third argument is the zookeepr root for Kafka
			// Fourth argument is consumer group id
			SpoutConfig kafkaConfig = new SpoutConfig(zkHosts, consumer_topic,zookeeper_root,group_id);

			// Specify that the kafka messages are String
			// We want to consume all the first messages in the topic everytime
			// we run the topology to help in debugging. In production, this
			// property should be false
			kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
			kafkaConfig.startOffsetTime = kafka.api.OffsetRequest.EarliestTime();

			// Now we create the topology
			TopologyBuilder builder = new TopologyBuilder();

			// set the kafka spout class
                        if (kafkaSpout_NumTask>kafkaSpout_NumThread){
                            builder.setSpout(kafkaSpout_Name, new KafkaSpout(kafkaConfig), kafkaSpout_NumThread).setNumTasks(kafkaSpout_NumTask);
                        }else{
                            builder.setSpout(kafkaSpout_Name, new KafkaSpout(kafkaConfig), kafkaSpout_NumThread); 
                        }

			// set the ReceiverBolt and SenderBolt class
                        if (rcvBolt_NumTask>rcvBolt_NumThread){
                            builder.setBolt(rcvBolt_Name, new ReceiverBolt(), rcvBolt_NumThread).shuffleGrouping(kafkaSpout_Name).setNumTasks(rcvBolt_NumTask);
                        }else{
                            builder.setBolt(rcvBolt_Name, new ReceiverBolt(), rcvBolt_NumThread).shuffleGrouping(kafkaSpout_Name);
                        }
			

                        if (processBolt_NumTask>processBolt_NumThread){
                            builder.setBolt(processBolt_Name+"_"+1, new ProcessBolt_Register(), processBolt_NumThread).shuffleGrouping(rcvBolt_Name,PROCESS_STREAM_REG).setNumTasks(sendBolt_NumTask);
                            builder.setBolt(processBolt_Name+"_"+2, new ProcessBolt_Check(), processBolt_NumThread).shuffleGrouping(rcvBolt_Name,PROCESS_STREAM_CHECK).setNumTasks(sendBolt_NumTask);
                            builder.setBolt(processBolt_Name+"_"+3, new ProcessBolt_Del(), processBolt_NumThread).shuffleGrouping(rcvBolt_Name,PROCESS_STREAM_DEL).setNumTasks(sendBolt_NumTask);
                            builder.setBolt(processBolt_Name+"_"+4, new ProcessBolt_Guide(), processBolt_NumThread).shuffleGrouping(rcvBolt_Name,PROCESS_STREAM_GUIDE).setNumTasks(sendBolt_NumTask);
                        }else{
                            builder.setBolt(processBolt_Name+"_"+1, new ProcessBolt_Register(), processBolt_NumThread).shuffleGrouping(rcvBolt_Name,PROCESS_STREAM_REG);
                            builder.setBolt(processBolt_Name+"_"+2, new ProcessBolt_Check(), processBolt_NumThread).shuffleGrouping(rcvBolt_Name,PROCESS_STREAM_CHECK);
                            builder.setBolt(processBolt_Name+"_"+3, new ProcessBolt_Del(), processBolt_NumThread).shuffleGrouping(rcvBolt_Name,PROCESS_STREAM_DEL);
                            builder.setBolt(processBolt_Name+"_"+4, new ProcessBolt_Guide(), processBolt_NumThread).shuffleGrouping(rcvBolt_Name,PROCESS_STREAM_GUIDE);
                      
                        }
                        
                         if (sendBolt_NumTask>sendBolt_NumThread){
                            builder.setBolt(sendBolt_Name+"_"+1, new SenderBolt(), sendBolt_NumThread).shuffleGrouping(rcvBolt_Name,SENDER_STREAM).setNumTasks(sendBolt_NumTask);
                            builder.setBolt(sendBolt_Name+"_"+2, new SenderBolt(), sendBolt_NumThread).shuffleGrouping(processBolt_Name+"_"+1,SENDER_STREAM).setNumTasks(sendBolt_NumTask);
                            builder.setBolt(sendBolt_Name+"_"+3, new SenderBolt(), sendBolt_NumThread).shuffleGrouping(processBolt_Name+"_"+2,SENDER_STREAM).setNumTasks(sendBolt_NumTask);
                            builder.setBolt(sendBolt_Name+"_"+4, new SenderBolt(), sendBolt_NumThread).shuffleGrouping(processBolt_Name+"_"+3,SENDER_STREAM).setNumTasks(sendBolt_NumTask);
                            builder.setBolt(sendBolt_Name+"_"+5, new SenderBolt(), sendBolt_NumThread).shuffleGrouping(processBolt_Name+"_"+4,SENDER_STREAM).setNumTasks(sendBolt_NumTask);
                         }else{
                            builder.setBolt(sendBolt_Name+"_"+1, new SenderBolt(), sendBolt_NumThread).shuffleGrouping(rcvBolt_Name,SENDER_STREAM);
                            builder.setBolt(sendBolt_Name+"_"+2, new SenderBolt(), sendBolt_NumThread).shuffleGrouping(processBolt_Name+"_"+1,SENDER_STREAM);
                            builder.setBolt(sendBolt_Name+"_"+3, new SenderBolt(), sendBolt_NumThread).shuffleGrouping(processBolt_Name+"_"+2,SENDER_STREAM);
                            builder.setBolt(sendBolt_Name+"_"+4, new SenderBolt(), sendBolt_NumThread).shuffleGrouping(processBolt_Name+"_"+3,SENDER_STREAM);
                            builder.setBolt(sendBolt_Name+"_"+5, new SenderBolt(), sendBolt_NumThread).shuffleGrouping(processBolt_Name+"_"+4,SENDER_STREAM);
                         }
                         

			// create an instance of LocalCluster class for executing topology
			// in local mode.
			
			Config conf = new Config();
                        
                        conf.setNumWorkers(topology_NumWorkers);
			if (topology_Debug==1)             conf.setDebug(true);
                        if (topology_MessageTimeoutSecs>0) conf.setMessageTimeoutSecs(topology_MessageTimeoutSecs); 
                        if (topology_MaxSpoutPending>0)    conf.setMaxSpoutPending(topology_MaxSpoutPending);                        
                        if (topology_MaxTaskParallelism>0) conf.setMaxTaskParallelism(topology_MaxTaskParallelism);
                        if (topology_NumAckers>0)          conf.setNumAckers(topology_NumAckers);
                        
                        
                        StormSubmitter.submitTopologyWithProgressBar(topology_Name, conf,builder.createTopology());
			
		} catch (Exception exception) {
			logger.error("Thread interrupted exception : " ,exception);                       
                        // kill the KafkaTopology
                        cluster.killTopology(topology_Name);
                        // shutdown the storm test cluster
                        cluster.shutdown();
		}
	}
}
