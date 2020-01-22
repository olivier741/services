/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml;

import java.util.Objects;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Element;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name="mofilterSMS")
public class MoFilterSMS  {

    @Element(name="content",required = false) 
    private String content;
    
    @Element(name="receiver",required = false) 
    private String receiver;
    
    @Element(name="sender",required = false) 
    private String sender;
    
    @Element(name="topic",required = false) 
    private String topic;

    public MoFilterSMS(@Element(name="content",required = false) String content,
                       @Element(name="receiver",required = false) String receiver,
                       @Element(name="sender",required = false) String sender,
                       @Element(name="topic",required = false) String topic) {
        this.content = content;
        this.receiver = receiver;
        this.sender = sender;
        this.topic=topic;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.content);
        hash = 97 * hash + Objects.hashCode(this.receiver);
        hash = 97 * hash + Objects.hashCode(this.sender);
        hash = 97 * hash + Objects.hashCode(this.topic);
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
        final MoFilterSMS other = (MoFilterSMS) obj;
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
        return "MoFilterSMS{" + "content=" + content + ", receiver=" + receiver + ", sender=" + sender + ", topic=" + topic + '}';
    }

  

}
