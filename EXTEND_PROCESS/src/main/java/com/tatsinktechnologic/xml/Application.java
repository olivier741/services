/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name="application")
public class Application {
    
    @Element(name="kafka",required = true) 
    private Kafka_Conf kafka_conf;

    @Element(name="sender_thread",required = true) 
    private SenderThread_Conf sender_thread;

    
    @Element(name="process_thread_extend",required = true) 
    private ProcessThread_Ext process_thread_extend;
    
   
    public Application(@Element(name="kafka",required = true) Kafka_Conf kafka_conf,
                        @Element(name="sender_thread",required = true)  SenderThread_Conf sender_thread, 
                        @Element(name="process_thread_extend",required = true) ProcessThread_Ext process_thread_extend) {
        
        this.sender_thread = sender_thread;  
        this.process_thread_extend=process_thread_extend;
        this.kafka_conf=kafka_conf;
    }

    public ProcessThread_Ext getProcess_thread_extend() {
        return process_thread_extend;
    }

    public Kafka_Conf getKafka_conf() {
        return kafka_conf;
    }

    public SenderThread_Conf getSender_thread() {
        return sender_thread;
    }

    public void setKafka_conf(Kafka_Conf kafka_conf) {
        this.kafka_conf = kafka_conf;
    }

    public void setSender_thread(SenderThread_Conf sender_thread) {
        this.sender_thread = sender_thread;
    }

    @Override
    public String toString() {
        return "Application{" + "kafka_conf=" + kafka_conf + ", sender_thread=" + sender_thread + ", process_thread_extend=" + process_thread_extend + '}';
    }

}
