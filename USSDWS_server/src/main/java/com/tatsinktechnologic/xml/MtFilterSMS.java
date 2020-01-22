/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml;

import java.util.Objects;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name="mtfilterSMS")
public class MtFilterSMS {
    
    @Element(name="content",required = false) 
    private String content;
    
    @Element(name="receiver",required = false) 
    private String receiver;
    
    @Element(name="sender",required = false) 
    private String sender;
    
    @Element(name="topic",required = false)
    private String topic;
    
    @Element(name="numberRecord",required = false) 
    private int numberRecord;

    public MtFilterSMS(@Element(name="content",required = false) String content,
                       @Element(name="receiver",required = false) String receiver,
                       @Element(name="sender",required = false) String sender,
                       @Element(name="topic",required = false) String topic,
                       @Element(name="numberRecord",required = false) int numberRecord) {
        this.content = content;
        this.receiver = receiver;
        this.sender = sender;
        this.topic=topic;
        this.numberRecord=numberRecord;
    }

    public String getContent() {
        return content;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }
    
     public String getTopic() {
        return topic;
    }

    public int getNumberRecord() {
        return numberRecord;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.content);
        hash = 23 * hash + Objects.hashCode(this.receiver);
        hash = 23 * hash + Objects.hashCode(this.sender);
        hash = 23 * hash + Objects.hashCode(this.topic);
        hash = 23 * hash + this.numberRecord;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MtFilterSMS other = (MtFilterSMS) obj;
        if (this.numberRecord != other.numberRecord) {
            return false;
        }
        if (!Objects.equals(this.content, other.content)) {
            return false;
        }
        if (!Objects.equals(this.receiver, other.receiver)) {
            return false;
        }
        if (!Objects.equals(this.sender, other.sender)) {
            return false;
        }
        if (!Objects.equals(this.topic, other.topic)) {
            return false;
        }
        return true;
    }
    
    
    

    @Override
    public String toString() {
        return "MtFilterSMS{" + "content=" + content + ", receiver=" + receiver + ", sender=" + sender + ", topic=" + topic + ", numberRecord=" + numberRecord + '}';
    }


}
