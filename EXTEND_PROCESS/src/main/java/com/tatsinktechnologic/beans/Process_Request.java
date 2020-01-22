/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans;

import com.tatsinktechnologic.beans_entity.Action_Type;
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
    private String split_separate;
    private String param_name;
    private Integer param_length;
    private String param_pattern;
    private String param_value;
    private String channel;
    
    private Action_Type action_type;
    
    private String action_description;
    private Timestamp receive_time;
    private String product_name;
    private String notification_code;
    private String language;

    public String getTransaction_id() {
        return transaction_id;
    }

    
    public String getMsisdn() {
        return msisdn;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public String getCommand_code() {
        return command_code;
    }

    public String getSplit_separate() {
        return split_separate;
    }

    public String getParam_name() {
        return param_name;
    }

    public Integer getParam_length() {
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

    public Action_Type getAction_type() {
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

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
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

    public void setCommand_code(String command_code) {
        this.command_code = command_code;
    }

    public void setSplit_separate(String split_separate) {
        this.split_separate = split_separate;
    }

    
    public void setParam_name(String param_name) {
        this.param_name = param_name;
    }

    public void setParam_length(Integer param_length) {
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

    public void setAction_type(Action_Type action_type) {
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
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.transaction_id);
        hash = 79 * hash + Objects.hashCode(this.msisdn);
        hash = 79 * hash + Objects.hashCode(this.receiver);
        hash = 79 * hash + Objects.hashCode(this.content);
        hash = 79 * hash + Objects.hashCode(this.command_code);
        hash = 79 * hash + Objects.hashCode(this.split_separate);
        hash = 79 * hash + Objects.hashCode(this.param_name);
        hash = 79 * hash + Objects.hashCode(this.param_length);
        hash = 79 * hash + Objects.hashCode(this.param_pattern);
        hash = 79 * hash + Objects.hashCode(this.param_value);
        hash = 79 * hash + Objects.hashCode(this.channel);
        hash = 79 * hash + Objects.hashCode(this.action_type);
        hash = 79 * hash + Objects.hashCode(this.action_description);
        hash = 79 * hash + Objects.hashCode(this.receive_time);
        hash = 79 * hash + Objects.hashCode(this.product_name);
        hash = 79 * hash + Objects.hashCode(this.notification_code);
        hash = 79 * hash + Objects.hashCode(this.language);
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
        if (!Objects.equals(this.split_separate, other.split_separate)) {
            return false;
        }
        if (!Objects.equals(this.param_name, other.param_name)) {
            return false;
        }
        if (!Objects.equals(this.param_pattern, other.param_pattern)) {
            return false;
        }
        if (!Objects.equals(this.param_value, other.param_value)) {
            return false;
        }
        if (!Objects.equals(this.channel, other.channel)) {
            return false;
        }
        if (!Objects.equals(this.action_type, other.action_type)) {
            return false;
        }
        if (!Objects.equals(this.action_description, other.action_description)) {
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
        if (!Objects.equals(this.param_length, other.param_length)) {
            return false;
        }
        if (!Objects.equals(this.receive_time, other.receive_time)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Process_Request{" + "transaction_id=" + transaction_id + ",\n msisdn=" + msisdn + ",\n receiver=" + receiver + ",\n content=" + content + ",\n command_code=" + command_code + ",\n split_separate=" + split_separate + ",\n param_name=" + param_name + ",\n param_length=" + param_length + ",\n param_pattern=" + param_pattern + ",\n param_value=" + param_value + ",\n channel=" + channel + ",\n action_type=" + action_type + ",\n action_description=" + action_description + ",\n receive_time=" + receive_time + ",\n product_name=" + product_name + ",\n notification_code=" + notification_code + ",\n language=" + language + '}';
    }

}
