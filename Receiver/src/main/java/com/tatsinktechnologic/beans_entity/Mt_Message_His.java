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
import java.sql.Date;
import java.util.Objects;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author olivier
 */
@Entity
public class Mt_Message_His implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = LogManager.getLogger(Mt_Message_His.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mt_his_generator")
    @SequenceGenerator(name="mt_his_generator", sequenceName = "Mt_his_Message_seq",allocationSize=1)
    private Long mt_message_his_id;
    
    private String msisdn;
    
    @Lob 
    private String message;
    
    private String shortcode_channel;
    private Date receive_date;
    private Date send_date;
    
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Logger getLogger() {
        return logger;
    }

    public Long getMt_message_his_id() {
        return mt_message_his_id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getMessage() {
        return message;
    }

    public String getShortcode_channel() {
        return shortcode_channel;
    }

    public Date getReceive_date() {
        return receive_date;
    }

    public Date getSend_date() {
        return send_date;
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

    public void setMt_message_his_id(Long mt_message_his_id) {
        this.mt_message_his_id = mt_message_his_id;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setShortcode_channel(String shortcode_channel) {
        this.shortcode_channel = shortcode_channel;
    }

    public void setReceive_date(Date receive_date) {
        this.receive_date = receive_date;
    }

    public void setSend_date(Date send_date) {
        this.send_date = send_date;
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
        hash = 19 * hash + Objects.hashCode(this.logger);
        hash = 19 * hash + Objects.hashCode(this.mt_message_his_id);
        hash = 19 * hash + Objects.hashCode(this.msisdn);
        hash = 19 * hash + Objects.hashCode(this.message);
        hash = 19 * hash + Objects.hashCode(this.shortcode_channel);
        hash = 19 * hash + Objects.hashCode(this.receive_date);
        hash = 19 * hash + Objects.hashCode(this.send_date);
        hash = 19 * hash + Objects.hashCode(this.command);
        hash = 19 * hash + Objects.hashCode(this.process_unit);
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
        final Mt_Message_His other = (Mt_Message_His) obj;
        if (!Objects.equals(this.msisdn, other.msisdn)) {
            return false;
        }
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        if (!Objects.equals(this.shortcode_channel, other.shortcode_channel)) {
            return false;
        }
        if (!Objects.equals(this.logger, other.logger)) {
            return false;
        }
        if (!Objects.equals(this.mt_message_his_id, other.mt_message_his_id)) {
            return false;
        }
        if (!Objects.equals(this.receive_date, other.receive_date)) {
            return false;
        }
        if (!Objects.equals(this.send_date, other.send_date)) {
            return false;
        }
        if (!Objects.equals(this.command, other.command)) {
            return false;
        }
        if (!Objects.equals(this.process_unit, other.process_unit)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Mt_Message_His{" + "logger=" + logger + ", mt_message_his_id=" + mt_message_his_id + ", msisdn=" + msisdn + ", message=" + message + ", shortcode_channel=" + shortcode_channel + ", receive_date=" + receive_date + ", send_date=" + send_date + ", command=" + command + ", process_unit=" + process_unit + '}';
    }

}
