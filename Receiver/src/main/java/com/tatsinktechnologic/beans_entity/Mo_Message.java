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
import javax.persistence.Transient;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author olivier.tatsinkou
 */
@Entity
public class Mo_Message implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = LogManager.getLogger(Mo_Message.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mo_generator")
    @SequenceGenerator(name="mo_generator", sequenceName = "Mo_Message_seq",allocationSize=1)
    private Long mo_message_id;
    
    private String msisdn;
    private String message;
    private Date receive_date;
    private String shortcode_channel;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "command_id", nullable = false)
    private Command command;

    public Long getMo_message_id() {
        return mo_message_id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getMessage() {
        return message;
    }

    public Date getReceive_date() {
        return receive_date;
    }

    public String getShortcode_channel() {
        return shortcode_channel;
    }

    public Command getCommand() {
        return command;
    }

    public void setMo_message_id(Long mo_message_id) {
        this.mo_message_id = mo_message_id;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReceive_date(Date receive_date) {
        this.receive_date = receive_date;
    }

    public void setShortcode_channel(String shortcode_channel) {
        this.shortcode_channel = shortcode_channel;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.mo_message_id);
        hash = 37 * hash + Objects.hashCode(this.msisdn);
        hash = 37 * hash + Objects.hashCode(this.message);
        hash = 37 * hash + Objects.hashCode(this.receive_date);
        hash = 37 * hash + Objects.hashCode(this.shortcode_channel);
        hash = 37 * hash + Objects.hashCode(this.command);
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
        final Mo_Message other = (Mo_Message) obj;
        if (!Objects.equals(this.msisdn, other.msisdn)) {
            return false;
        }
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        if (!Objects.equals(this.shortcode_channel, other.shortcode_channel)) {
            return false;
        }
        if (!Objects.equals(this.mo_message_id, other.mo_message_id)) {
            return false;
        }
        if (!Objects.equals(this.receive_date, other.receive_date)) {
            return false;
        }
        if (!Objects.equals(this.command, other.command)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Mo_Message{" + "mo_message_id=" + mo_message_id + ", msisdn=" + msisdn + ", message=" + message + ", receive_date=" + receive_date + ", shortcode_channel=" + shortcode_channel + ", command=" + command + '}';
    }
    
    
  
    
}
