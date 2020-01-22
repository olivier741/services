/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans;

import java.sql.Timestamp;

/**
 *
 * @author olivier.tatsinkou
 */
public class Process_Request {
    private String msisdn;
    private String receiver;
    private String content;
    private int command_id;
    private String command_code;
    private int param_id;
    private String param_pattern_separate;
    private String param_name;
    private int param_length;
    private String param_pattern;
    private String param_value;
    private String channel;
    private int syntax_id;
    private int action_id;
    private String action_type;
    private String action_description;
    private Timestamp receive_time;
    private String product_name;
    private String notification_code;
    private String language;

    public String getMsisdn() {
        return msisdn;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public int getCommand_id() {
        return command_id;
    }

    public String getCommand_code() {
        return command_code;
    }

    public int getParam_id() {
        return param_id;
    }

    public String getParam_pattern_separate() {
        return param_pattern_separate;
    }

    public String getParam_name() {
        return param_name;
    }

    public int getParam_length() {
        return param_length;
    }

    public String getParam_pattern() {
        return param_pattern;
    }

    public String getParam_value() {
        return param_value;
    }

    public String getChannel() {
        return channel;
    }

    public int getSyntax_id() {
        return syntax_id;
    }

    public int getAction_id() {
        return action_id;
    }

    public String getAction_type() {
        return action_type;
    }

    public String getAction_description() {
        return action_description;
    }

    public Timestamp getReceive_time() {
        return receive_time;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getNotification_code() {
        return notification_code;
    }

    public String getLanguage() {
        return language;
    }
    
   
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCommand_id(int command_id) {
        this.command_id = command_id;
    }

    public void setCommand_code(String command_code) {
        this.command_code = command_code;
    }

    public void setParam_id(int param_id) {
        this.param_id = param_id;
    }

    public void setParam_pattern_separate(String param_pattern_separate) {
        this.param_pattern_separate = param_pattern_separate;
    }

    public void setParam_name(String param_name) {
        this.param_name = param_name;
    }

    public void setParam_length(int param_length) {
        this.param_length = param_length;
    }

    public void setParam_pattern(String param_pattern) {
        this.param_pattern = param_pattern;
    }

    public void setParam_value(String param_value) {
        this.param_value = param_value;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setSyntax_id(int syntax_id) {
        this.syntax_id = syntax_id;
    }

    public void setAction_id(int action_id) {
        this.action_id = action_id;
    }

    public void setAction_type(String action_type) {
        this.action_type = action_type;
    }

    public void setAction_description(String action_description) {
        this.action_description = action_description;
    }

    public void setReceive_time(Timestamp receive_time) {
        this.receive_time = receive_time;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setNotification_code(String notification_code) {
        this.notification_code = notification_code;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.msisdn != null ? this.msisdn.hashCode() : 0);
        hash = 71 * hash + (this.receiver != null ? this.receiver.hashCode() : 0);
        hash = 71 * hash + (this.content != null ? this.content.hashCode() : 0);
        hash = 71 * hash + this.command_id;
        hash = 71 * hash + (this.command_code != null ? this.command_code.hashCode() : 0);
        hash = 71 * hash + this.param_id;
        hash = 71 * hash + (this.param_pattern_separate != null ? this.param_pattern_separate.hashCode() : 0);
        hash = 71 * hash + (this.param_name != null ? this.param_name.hashCode() : 0);
        hash = 71 * hash + this.param_length;
        hash = 71 * hash + (this.param_pattern != null ? this.param_pattern.hashCode() : 0);
        hash = 71 * hash + (this.param_value != null ? this.param_value.hashCode() : 0);
        hash = 71 * hash + (this.channel != null ? this.channel.hashCode() : 0);
        hash = 71 * hash + this.syntax_id;
        hash = 71 * hash + this.action_id;
        hash = 71 * hash + (this.action_type != null ? this.action_type.hashCode() : 0);
        hash = 71 * hash + (this.action_description != null ? this.action_description.hashCode() : 0);
        hash = 71 * hash + (this.receive_time != null ? this.receive_time.hashCode() : 0);
        hash = 71 * hash + (this.product_name != null ? this.product_name.hashCode() : 0);
        hash = 71 * hash + (this.notification_code != null ? this.notification_code.hashCode() : 0);
        hash = 71 * hash + (this.language != null ? this.language.hashCode() : 0);
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
        if (this.command_id != other.command_id) {
            return false;
        }
        if (this.param_id != other.param_id) {
            return false;
        }
        if (this.param_length != other.param_length) {
            return false;
        }
        if (this.syntax_id != other.syntax_id) {
            return false;
        }
        if (this.action_id != other.action_id) {
            return false;
        }
        if ((this.msisdn == null) ? (other.msisdn != null) : !this.msisdn.equals(other.msisdn)) {
            return false;
        }
        if ((this.receiver == null) ? (other.receiver != null) : !this.receiver.equals(other.receiver)) {
            return false;
        }
        if ((this.content == null) ? (other.content != null) : !this.content.equals(other.content)) {
            return false;
        }
        if ((this.command_code == null) ? (other.command_code != null) : !this.command_code.equals(other.command_code)) {
            return false;
        }
        if ((this.param_pattern_separate == null) ? (other.param_pattern_separate != null) : !this.param_pattern_separate.equals(other.param_pattern_separate)) {
            return false;
        }
        if ((this.param_name == null) ? (other.param_name != null) : !this.param_name.equals(other.param_name)) {
            return false;
        }
        if ((this.param_pattern == null) ? (other.param_pattern != null) : !this.param_pattern.equals(other.param_pattern)) {
            return false;
        }
        if ((this.param_value == null) ? (other.param_value != null) : !this.param_value.equals(other.param_value)) {
            return false;
        }
        if ((this.channel == null) ? (other.channel != null) : !this.channel.equals(other.channel)) {
            return false;
        }
        if ((this.action_type == null) ? (other.action_type != null) : !this.action_type.equals(other.action_type)) {
            return false;
        }
        if ((this.action_description == null) ? (other.action_description != null) : !this.action_description.equals(other.action_description)) {
            return false;
        }
        if ((this.product_name == null) ? (other.product_name != null) : !this.product_name.equals(other.product_name)) {
            return false;
        }
        if ((this.notification_code == null) ? (other.notification_code != null) : !this.notification_code.equals(other.notification_code)) {
            return false;
        }
        if ((this.language == null) ? (other.language != null) : !this.language.equals(other.language)) {
            return false;
        }
        if (this.receive_time != other.receive_time && (this.receive_time == null || !this.receive_time.equals(other.receive_time))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Process_Request{" + "msisdn=" + msisdn + ", receiver=" + receiver + ", content=" + content + ", command_id=" + command_id + ", command_code=" + command_code + ", param_id=" + param_id + ", param_pattern_separate=" + param_pattern_separate + ", param_name=" + param_name + ", param_length=" + param_length + ", param_pattern=" + param_pattern + ", param_value=" + param_value + ", channel=" + channel + ", syntax_id=" + syntax_id + ", action_id=" + action_id + ", action_type=" + action_type + ", action_description=" + action_description + ", receive_time=" + receive_time + ", product_name=" + product_name + ", notification_code=" + notification_code + ", language=" + language + '}';
    }
    
    

   
   
}
