/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans;


import java.sql.Timestamp;
import java.util.Objects;

/**
 *
 * @author olivier.tatsinkou
 */
public class Process_Request {
    
    private String transaction_id;
    private String msisdn;
    private String receiver;
    private String content;
    private String command_code;
    private String channel;
    private Timestamp receive_time;
    private String product_name;
    private String notification_code;
    private String language;
    private String exchange_mode;

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommand_code() {
        return command_code;
    }

    public void setCommand_code(String command_code) {
        this.command_code = command_code;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Timestamp getReceive_time() {
        return receive_time;
    }

    public void setReceive_time(Timestamp receive_time) {
        this.receive_time = receive_time;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getNotification_code() {
        return notification_code;
    }

    public void setNotification_code(String notification_code) {
        this.notification_code = notification_code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getExchange_mode() {
        return exchange_mode;
    }

    public void setExchange_mode(String exchange_mode) {
        this.exchange_mode = exchange_mode;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.transaction_id);
        hash = 37 * hash + Objects.hashCode(this.msisdn);
        hash = 37 * hash + Objects.hashCode(this.receiver);
        hash = 37 * hash + Objects.hashCode(this.content);
        hash = 37 * hash + Objects.hashCode(this.command_code);
        hash = 37 * hash + Objects.hashCode(this.channel);
        hash = 37 * hash + Objects.hashCode(this.receive_time);
        hash = 37 * hash + Objects.hashCode(this.product_name);
        hash = 37 * hash + Objects.hashCode(this.notification_code);
        hash = 37 * hash + Objects.hashCode(this.language);
        hash = 37 * hash + Objects.hashCode(this.exchange_mode);
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
        final Process_Request other = (Process_Request) obj;
        if (!Objects.equals(this.transaction_id, other.transaction_id)) {
            return false;
        }
        if (!Objects.equals(this.msisdn, other.msisdn)) {
            return false;
        }
        if (!Objects.equals(this.receiver, other.receiver)) {
            return false;
        }
        if (!Objects.equals(this.content, other.content)) {
            return false;
        }
        if (!Objects.equals(this.command_code, other.command_code)) {
            return false;
        }
        if (!Objects.equals(this.channel, other.channel)) {
            return false;
        }
        if (!Objects.equals(this.product_name, other.product_name)) {
            return false;
        }
        if (!Objects.equals(this.notification_code, other.notification_code)) {
            return false;
        }
        if (!Objects.equals(this.language, other.language)) {
            return false;
        }
        if (!Objects.equals(this.exchange_mode, other.exchange_mode)) {
            return false;
        }
        if (!Objects.equals(this.receive_time, other.receive_time)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Process_Request{" + "transaction_id=" + transaction_id + ", msisdn=" + msisdn + ", receiver=" + receiver + ", content=" + content + ", command_code=" + command_code + ", channel=" + channel + ", receive_time=" + receive_time + ", product_name=" + product_name + ", notification_code=" + notification_code + ", language=" + language + ", exchange_mode=" + exchange_mode + '}';
    }

    
    

}
