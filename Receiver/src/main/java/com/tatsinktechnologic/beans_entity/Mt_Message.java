/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans_entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import java.sql.Date;
import javax.persistence.Lob;
import javax.persistence.Transient;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author olivier
 */
@Entity
public class Mt_Message implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = LogManager.getLogger(Mt_Message.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mt_generator")
    @SequenceGenerator(name="mt_generator", sequenceName = "Mt_Message_seq",allocationSize=1)
    private Long mt_message_id;
    
    private String msisdn;
    
    @Lob 
    private String message;
    private Date process_date;
    private String shortcode_channel;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "command_id", nullable = true)
    private Command command;

    public Logger getLogger() {
        return logger;
    }

    public Long getMt_message_id() {
        return mt_message_id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getMessage() {
        return message;
    }

    public Date getProcess_date() {
        return process_date;
    }

    public String getShortcode_channel() {
        return shortcode_channel;
    }

    public Command getCommand() {
        return command;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setMt_message_id(Long mt_message_id) {
        this.mt_message_id = mt_message_id;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setProcess_date(Date process_date) {
        this.process_date = process_date;
    }

    public void setShortcode_channel(String shortcode_channel) {
        this.shortcode_channel = shortcode_channel;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.logger);
        hash = 13 * hash + Objects.hashCode(this.mt_message_id);
        hash = 13 * hash + Objects.hashCode(this.msisdn);
        hash = 13 * hash + Objects.hashCode(this.message);
        hash = 13 * hash + Objects.hashCode(this.process_date);
        hash = 13 * hash + Objects.hashCode(this.shortcode_channel);
        hash = 13 * hash + Objects.hashCode(this.command);
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
        final Mt_Message other = (Mt_Message) obj;
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
        if (!Objects.equals(this.mt_message_id, other.mt_message_id)) {
            return false;
        }
        if (!Objects.equals(this.process_date, other.process_date)) {
            return false;
        }
        if (!Objects.equals(this.command, other.command)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Mt_Message{" + "logger=" + logger + ", mt_message_id=" + mt_message_id + ", msisdn=" + msisdn + ", message=" + message + ", process_date=" + process_date + ", shortcode_channel=" + shortcode_channel + ", command=" + command + '}';
    }


    
}
