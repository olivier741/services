/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml.service_listener;

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

    public MtFilterSMS(@Element(name="content",required = false) String content,
                       @Element(name="receiver",required = false) String receiver,
                       @Element(name="sender",required = false) String sender) {
        this.content = content;
        this.receiver = receiver;
        this.sender = sender;
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

    @Override
    public String toString() {
        return "MtFilterSMS{" + "content=" + content + ", receiver=" + receiver + ", sender=" + sender + '}';
    }


    
}
