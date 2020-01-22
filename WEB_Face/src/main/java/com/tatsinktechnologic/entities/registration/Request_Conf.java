/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entities.registration;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

/**
 *
 * @author olivier.tatsinkou
 */
@Entity
@Subselect(
"   SELECT cmd.command_id,  \n"+
"          cmd.command_code,  \n" +
"	   cmd.split_separator, \n" +
"	   cmd.channel, \n" +
"	   ac.action_name,  \n" +
"	   ac.action_type,  \n" +
"	   ac.action_description,    \n" +
"	   pro.end_date, \n" +
"	   pro.end_reg_time, \n" +
"	   pro.extend_fee,   \n" +
"	   pro.isextend, \n" +
"	   pro.pending_duration, \n" +
"	   pro.product_code, \n" +
"	   pro.reg_fee,  \n" +
"	   pro.register_day, \n" +
"	   pro.restrict_product, \n" +
"	   pro.start_date,   \n" +
"	   pro.start_reg_time,   \n" +
"	   pro.validity, \n" +
"	   ser.service_name, \n" +
"          ser.receive_channel, \n" +
"          ser.send_channel, \n" +
"          ser.service_provider, \n" +
"          ser.service_description,\n" +
"          ser.create_time, \n" +
"	   pr.param_name,   \n" +
"	   pr.param_pattern,    \n" +
"	   pr.param_length,   \n" +
"	   pr.paramDescription  \n" +
"   FROM Command cmd   \n" +
"   LEFT JOIN PARAM pr ON cmd.command_id = pr.command_id \n" +
"   LEFT JOIN Action ac ON cmd.action_id = ac.action_id \n" +
"   LEFT JOIN Product pro ON ac.product_id = pro.product_id \n" +
"   LEFT JOIN Service ser ON pro.service_id = ser.service_id ")
@Synchronize({"Command", "PARAM","Product","Service"})
@Immutable
public class Request_Conf implements Serializable {
    @Id
    private int command_id;
    private String command_code;
    private String split_separator;
    private String channel;
    
    private String action_name;
    
    @Enumerated(EnumType.STRING)
    private Action_Type action_type;
    
    private String action_description;
    
    private String product_code;
    private Long reg_fee;
    private String restrict_product; 
    private String register_day;     // 'Day allow register: 0-Sunday, 1-Monday, 2-Tuesday, 3-Wednesday, ... not information mean registration every day'
    private Timestamp start_date;    //  2019-04-16 23:00:01-07:00:00  this promotion will launch  the 2019-04-16 at 11PM 
    private Timestamp end_date;      //  2050-04-16 23:00:01-07:00:00  this promotion will end  the 2050-04-16 at 11PM 
    private Time start_reg_time;     //  23:00:01  star time in one day from when customer allow to register
    private Time end_reg_time;       //  07:00:00  end time in one day from when customer not allow to register
    private Boolean isextend;
    private Long extend_fee;
    private String validity;          // D1 mean customer must have this offer for one Day 
    private String pending_duration;  
    private String service_name;
    private String receive_channel;
    private String send_channel;
    private String service_provider;
    private String service_description;
    
    private String param_name;
    private Integer param_length;
    private String param_pattern;
    private String paramDescription; 

    public int getCommand_id() {
        return command_id;
    }

    public String getCommand_code() {
        return command_code;
    }

    public String getSplit_separator() {
        return split_separator;
    }

    public String getChannel() {
        return channel;
    }

    public String getAction_name() {
        return action_name;
    }

    public Action_Type getAction_type() {
        return action_type;
    }

    public String getAction_description() {
        return action_description;
    }

    public String getProduct_code() {
        return product_code;
    }

    public Long getReg_fee() {
        return reg_fee;
    }

    public String getRestrict_product() {
        return restrict_product;
    }

    public String getRegister_day() {
        return register_day;
    }

    public Timestamp getStart_date() {
        return start_date;
    }

    public Timestamp getEnd_date() {
        return end_date;
    }

    public Time getStart_reg_time() {
        return start_reg_time;
    }

    public Time getEnd_reg_time() {
        return end_reg_time;
    }

    public Boolean getIsextend() {
        return isextend;
    }

    public Long getExtend_fee() {
        return extend_fee;
    }

 

    public String getValidity() {
        return validity;
    }

    public String getPending_duration() {
        return pending_duration;
    }

    public String getService_name() {
        return service_name;
    }

    public String getReceive_channel() {
        return receive_channel;
    }

    public String getSend_channel() {
        return send_channel;
    }

    public String getService_provider() {
        return service_provider;
    }

    public String getService_description() {
        return service_description;
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

    public String getParamDescription() {
        return paramDescription;
    }

    public void setCommand_id(int command_id) {
        this.command_id = command_id;
    }

    public void setCommand_code(String command_code) {
        this.command_code = command_code;
    }

    public void setSplit_separator(String split_separator) {
        this.split_separator = split_separator;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setAction_name(String action_name) {
        this.action_name = action_name;
    }

    public void setAction_type(Action_Type action_type) {
        this.action_type = action_type;
    }

    public void setAction_description(String action_description) {
        this.action_description = action_description;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public void setReg_fee(Long reg_fee) {
        this.reg_fee = reg_fee;
    }

    public void setRestrict_product(String restrict_product) {
        this.restrict_product = restrict_product;
    }

    public void setRegister_day(String register_day) {
        this.register_day = register_day;
    }

    public void setStart_date(Timestamp start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(Timestamp end_date) {
        this.end_date = end_date;
    }

    public void setStart_reg_time(Time start_reg_time) {
        this.start_reg_time = start_reg_time;
    }

    public void setEnd_reg_time(Time end_reg_time) {
        this.end_reg_time = end_reg_time;
    }

    public void setIsextend(Boolean isextend) {
        this.isextend = isextend;
    }

    public void setExtend_fee(Long extend_fee) {
        this.extend_fee = extend_fee;
    }

  

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public void setPending_duration(String pending_duration) {
        this.pending_duration = pending_duration;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public void setReceive_channel(String receive_channel) {
        this.receive_channel = receive_channel;
    }

    public void setSend_channel(String send_channel) {
        this.send_channel = send_channel;
    }

    public void setService_provider(String service_provider) {
        this.service_provider = service_provider;
    }

    public void setService_description(String service_description) {
        this.service_description = service_description;
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

    public void setParamDescription(String paramDescription) {
        this.paramDescription = paramDescription;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.command_id);
        hash = 97 * hash + Objects.hashCode(this.command_code);
        hash = 97 * hash + Objects.hashCode(this.split_separator);
        hash = 97 * hash + Objects.hashCode(this.channel);
        hash = 97 * hash + Objects.hashCode(this.action_name);
        hash = 97 * hash + Objects.hashCode(this.action_type);
        hash = 97 * hash + Objects.hashCode(this.action_description);
        hash = 97 * hash + Objects.hashCode(this.product_code);
        hash = 97 * hash + Objects.hashCode(this.reg_fee);
        hash = 97 * hash + Objects.hashCode(this.restrict_product);
        hash = 97 * hash + Objects.hashCode(this.register_day);
        hash = 97 * hash + Objects.hashCode(this.start_date);
        hash = 97 * hash + Objects.hashCode(this.end_date);
        hash = 97 * hash + Objects.hashCode(this.start_reg_time);
        hash = 97 * hash + Objects.hashCode(this.end_reg_time);
        hash = 97 * hash + Objects.hashCode(this.isextend);
        hash = 97 * hash + Objects.hashCode(this.extend_fee);
        hash = 97 * hash + Objects.hashCode(this.validity);
        hash = 97 * hash + Objects.hashCode(this.pending_duration);
        hash = 97 * hash + Objects.hashCode(this.service_name);
        hash = 97 * hash + Objects.hashCode(this.receive_channel);
        hash = 97 * hash + Objects.hashCode(this.send_channel);
        hash = 97 * hash + Objects.hashCode(this.service_provider);
        hash = 97 * hash + Objects.hashCode(this.service_description);
        hash = 97 * hash + Objects.hashCode(this.param_name);
        hash = 97 * hash + Objects.hashCode(this.param_length);
        hash = 97 * hash + Objects.hashCode(this.param_pattern);
        hash = 97 * hash + Objects.hashCode(this.paramDescription);
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
        final Request_Conf other = (Request_Conf) obj;
        if (!Objects.equals(this.command_code, other.command_code)) {
            return false;
        }
        if (!Objects.equals(this.split_separator, other.split_separator)) {
            return false;
        }
        if (!Objects.equals(this.channel, other.channel)) {
            return false;
        }
        if (!Objects.equals(this.action_name, other.action_name)) {
            return false;
        }
        if (!Objects.equals(this.action_type, other.action_type)) {
            return false;
        }
        if (!Objects.equals(this.action_description, other.action_description)) {
            return false;
        }
        if (!Objects.equals(this.product_code, other.product_code)) {
            return false;
        }
        if (!Objects.equals(this.restrict_product, other.restrict_product)) {
            return false;
        }
        if (!Objects.equals(this.register_day, other.register_day)) {
            return false;
        }
       
        if (!Objects.equals(this.validity, other.validity)) {
            return false;
        }
        if (!Objects.equals(this.pending_duration, other.pending_duration)) {
            return false;
        }
        if (!Objects.equals(this.service_name, other.service_name)) {
            return false;
        }
        if (!Objects.equals(this.receive_channel, other.receive_channel)) {
            return false;
        }
        if (!Objects.equals(this.send_channel, other.send_channel)) {
            return false;
        }
        if (!Objects.equals(this.service_provider, other.service_provider)) {
            return false;
        }
        if (!Objects.equals(this.service_description, other.service_description)) {
            return false;
        }
        if (!Objects.equals(this.param_name, other.param_name)) {
            return false;
        }
        if (!Objects.equals(this.param_pattern, other.param_pattern)) {
            return false;
        }
        if (!Objects.equals(this.paramDescription, other.paramDescription)) {
            return false;
        }
        if (!Objects.equals(this.command_id, other.command_id)) {
            return false;
        }
        if (!Objects.equals(this.reg_fee, other.reg_fee)) {
            return false;
        }
        if (!Objects.equals(this.start_date, other.start_date)) {
            return false;
        }
        if (!Objects.equals(this.end_date, other.end_date)) {
            return false;
        }
        if (!Objects.equals(this.start_reg_time, other.start_reg_time)) {
            return false;
        }
        if (!Objects.equals(this.end_reg_time, other.end_reg_time)) {
            return false;
        }
        if (!Objects.equals(this.isextend, other.isextend)) {
            return false;
        }
        if (!Objects.equals(this.extend_fee, other.extend_fee)) {
            return false;
        }
        if (!Objects.equals(this.param_length, other.param_length)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Request_Conf{" + "command_id=" + command_id + ", command_code=" + command_code + ", split_separator=" + split_separator + ", channel=" + channel + ", action_name=" + action_name + ", action_type=" + action_type + ", action_description=" + action_description + ", product_code=" + product_code + ", reg_fee=" + reg_fee + ", restrict_product=" + restrict_product + ", register_day=" + register_day + ", start_date=" + start_date + ", end_date=" + end_date + ", start_reg_time=" + start_reg_time + ", end_reg_time=" + end_reg_time + ", isextend=" + isextend + ", extend_fee=" + extend_fee + ", validity=" + validity + ", pending_duration=" + pending_duration + ", service_name=" + service_name + ", receive_channel=" + receive_channel + ", send_channel=" + send_channel + ", service_provider=" + service_provider + ", service_description=" + service_description + ", param_name=" + param_name + ", param_length=" + param_length + ", param_pattern=" + param_pattern + ", paramDescription=" + paramDescription + '}';
    }

   
   
}
