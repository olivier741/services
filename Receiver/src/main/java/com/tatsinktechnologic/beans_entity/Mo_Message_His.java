/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans_entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.sql.Date;
import java.util.Objects;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author olivier
 */
@Entity
public class Mo_Message_His implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = LogManager.getLogger(Mo_Message_His.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mo_his_generator")
    @SequenceGenerator(name="mo_his_generator", sequenceName = "Mo_Message_his_seq",allocationSize=1)
    private Long mo_message_his_id;
    
    private String msisdn;
    private String mo_message;
    private String shortcode_channel;
    private Date receive_date;
    private Date process_date;
    private int action_type;
    private String charge_policy;
    private long charge_fee;
    private String charge_status;
    private String charge_error;
    private String charge_date;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "command_id", nullable = true)
    private Command command;
    
    
   @ManyToOne
    @JoinColumns({
        @JoinColumn(
            name = "server_name",
            referencedColumnName = "server_name"),
        @JoinColumn(
            name = "node_name",
            referencedColumnName = "node_name")
    })
    private Process_Unit process_unit;

    public Logger getLogger() {
        return logger;
    }

    public Long getMo_message_his_id() {
        return mo_message_his_id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getMo_message() {
        return mo_message;
    }

    public String getShortcode_channel() {
        return shortcode_channel;
    }

    public Date getReceive_date() {
        return receive_date;
    }

    public Date getProcess_date() {
        return process_date;
    }

    public int getAction_type() {
        return action_type;
    }

    public String getCharge_policy() {
        return charge_policy;
    }

    public long getCharge_fee() {
        return charge_fee;
    }

    public String getCharge_status() {
        return charge_status;
    }

    public String getCharge_error() {
        return charge_error;
    }

    public String getCharge_date() {
        return charge_date;
    }

    public Command getCommand() {
        return command;
    }

    public Process_Unit getProcess_unit() {
        return process_unit;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setMo_message_his_id(Long mo_message_his_id) {
        this.mo_message_his_id = mo_message_his_id;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setMo_message(String mo_message) {
        this.mo_message = mo_message;
    }

    public void setShortcode_channel(String shortcode_channel) {
        this.shortcode_channel = shortcode_channel;
    }

    public void setReceive_date(Date receive_date) {
        this.receive_date = receive_date;
    }

    public void setProcess_date(Date process_date) {
        this.process_date = process_date;
    }

    public void setAction_type(int action_type) {
        this.action_type = action_type;
    }

    public void setCharge_policy(String charge_policy) {
        this.charge_policy = charge_policy;
    }

    public void setCharge_fee(long charge_fee) {
        this.charge_fee = charge_fee;
    }

    public void setCharge_status(String charge_status) {
        this.charge_status = charge_status;
    }

    public void setCharge_error(String charge_error) {
        this.charge_error = charge_error;
    }

    public void setCharge_date(String charge_date) {
        this.charge_date = charge_date;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public void setProcess_unit(Process_Unit process_unit) {
        this.process_unit = process_unit;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.mo_message_his_id);
        hash = 61 * hash + Objects.hashCode(this.msisdn);
        hash = 61 * hash + Objects.hashCode(this.mo_message);
        hash = 61 * hash + Objects.hashCode(this.shortcode_channel);
        hash = 61 * hash + Objects.hashCode(this.receive_date);
        hash = 61 * hash + Objects.hashCode(this.process_date);
        hash = 61 * hash + this.action_type;
        hash = 61 * hash + Objects.hashCode(this.charge_policy);
        hash = 61 * hash + (int) (this.charge_fee ^ (this.charge_fee >>> 32));
        hash = 61 * hash + Objects.hashCode(this.charge_status);
        hash = 61 * hash + Objects.hashCode(this.charge_error);
        hash = 61 * hash + Objects.hashCode(this.charge_date);
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
        final Mo_Message_His other = (Mo_Message_His) obj;
        if (this.action_type != other.action_type) {
            return false;
        }
        if (this.charge_fee != other.charge_fee) {
            return false;
        }
        if (!Objects.equals(this.msisdn, other.msisdn)) {
            return false;
        }
        if (!Objects.equals(this.mo_message, other.mo_message)) {
            return false;
        }
        if (!Objects.equals(this.shortcode_channel, other.shortcode_channel)) {
            return false;
        }
        if (!Objects.equals(this.charge_policy, other.charge_policy)) {
            return false;
        }
        if (!Objects.equals(this.charge_status, other.charge_status)) {
            return false;
        }
        if (!Objects.equals(this.charge_error, other.charge_error)) {
            return false;
        }
        if (!Objects.equals(this.charge_date, other.charge_date)) {
            return false;
        }
        if (!Objects.equals(this.mo_message_his_id, other.mo_message_his_id)) {
            return false;
        }
        if (!Objects.equals(this.receive_date, other.receive_date)) {
            return false;
        }
        if (!Objects.equals(this.process_date, other.process_date)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Mo_Message_His{" + "mo_message_his_id=" + mo_message_his_id + ", msisdn=" + msisdn + ", mo_message=" + mo_message + ", shortcode_channel=" + shortcode_channel + ", receive_date=" + receive_date + ", process_date=" + process_date + ", action_type=" + action_type + ", charge_policy=" + charge_policy + ", charge_fee=" + charge_fee + ", charge_status=" + charge_status + ", charge_error=" + charge_error + ", charge_date=" + charge_date + '}';
    }
    
    


}
