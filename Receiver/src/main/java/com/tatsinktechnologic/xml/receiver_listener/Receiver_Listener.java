/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml.receiver_listener;

import java.util.regex.Pattern;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name="receiver")
public class Receiver_Listener {
    @Element( name="node_name" ,required = true)
    private String node_name;  
    
    @Element( name="smspp_dataFormat" ,required = false)
    private String smspp_dataFormat;  
    
    @Element( name="http_listener" ,required = true)
    private Http_Listener http_listener;
    @Element(name="mofilterSMS" ,required = true) 
    private MoFilterSMS mofilter;

    public Receiver_Listener(   @Element( name="node_name" ,required = true)String node_name,
                                @Element( name="smspp_dataFormat" ,required = false) String smspp_dataFormat,
                                @Element( name="http_listener" ,required = true) Http_Listener http_listener, 
                                @Element(name="mofilterSMS" ,required = true) MoFilterSMS mofilter) {
        this.node_name=node_name;
        this.smspp_dataFormat=smspp_dataFormat;
        this.http_listener = http_listener;
        this.mofilter = mofilter;
    }

    public String getNode_name() {
        return node_name;
    }

    public String getSmspp_dataFormat() {
        return smspp_dataFormat;
    }

    
    public Http_Listener getHttp_listener() {
        return http_listener;
    }

    public MoFilterSMS getMofilter() {
        return mofilter;
    }
    
    
   public  boolean checkMo(final String content,final String receiver,final String sender){
         
        String checkContent= mofilter.getContent();
        String checkSender= mofilter.getSender();
        String checkReceiver= mofilter.getReceiver();

        Pattern pattern_cont = Pattern.compile(checkContent);
        Pattern pattern_send = Pattern.compile(checkSender);
        Pattern pattern_rec = Pattern.compile(checkReceiver);

        boolean match_cont = pattern_cont.matcher(content).find();
        boolean match_send = pattern_send.matcher(sender).find();
        boolean match_rec = pattern_rec.matcher(receiver).find();

        return match_cont && match_send && match_rec;
            
   }     

    @Override
    public String toString() {
        return "Receiver_Listener{" + "node_name=" + node_name + ", smspp_dataFormat=" + smspp_dataFormat + ", http_listener=" + http_listener + ", mofilter=" + mofilter + '}';
    }

}
