/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name = "application")
public class Application {

    @Element(name = "kafka", required = true)
    private Kafka_Conf kafka_conf;

    @Element(name = "sender_thread", required = true)
    private SenderThread_Conf sender_thread;

    @Element(name = "receiver_thread", required = true)
    private ReceiverThread_Conf receiver_thread;

    @ElementList(inline = true, name = "chat_process", required = true)
    private List<Chat_Process> listChat_Process = new ArrayList<Chat_Process>();

    public Application(@Element(name = "kafka", required = true) Kafka_Conf kafka_conf,
            @Element(name = "sender_thread", required = true) SenderThread_Conf sender_thread,
            @Element(name = "receiver_thread", required = true) ReceiverThread_Conf receiver_thread,
            @ElementList(inline = true, name = "chat_process", required = true) List<Chat_Process> listChat_Process) {

        this.sender_thread = sender_thread;
        this.receiver_thread = receiver_thread;
        this.listChat_Process = listChat_Process;
        this.kafka_conf = kafka_conf;
    }

    public Kafka_Conf getKafka_conf() {
        return kafka_conf;
    }

    public SenderThread_Conf getSender_thread() {
        return sender_thread;
    }

    public ReceiverThread_Conf getReceiver_thread() {
        return receiver_thread;
    }

    public List<Chat_Process> getListChat_Process() {
        return listChat_Process;
    }
    
    
    
     public  static List<Chat_Process> getChat_Process(final String regex_label,List<Chat_Process> listChat_Process){
         
        List<Chat_Process> result =new ArrayList<Chat_Process>();
        for(Chat_Process chatProc : listChat_Process){
            result.add(chatProc);
        }
         
         CollectionUtils.filter(result, new Predicate() {

            @Override
            public boolean evaluate(Object o) {

                String checkChatProcess= null;

                checkChatProcess= ((Chat_Process) o).getRegx_label();

                Pattern pattern_chat = Pattern.compile(checkChatProcess);
                
                return pattern_chat.matcher(regex_label).find();
            }

        });
        
        return result;
    }
      

}
