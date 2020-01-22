/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entity.sms_chat;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author olivier
 */
@Entity
@Table(name = "mo_push")
public class Mo_Push implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private int mo_push_id;
    
    private String msisdn;
    private String channel;
    private String operator;
    private String groupe_name;
    private int user_id;
    @Lob
    private String message;
    
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp  receive_time;

    public int getMo_push_id() {
        return mo_push_id;
    }

    public void setMo_push_id(int mo_push_id) {
        this.mo_push_id = mo_push_id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getGroupe_name() {
        return groupe_name;
    }

    public void setGroupe_name(String groupe_name) {
        this.groupe_name = groupe_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getReceive_time() {
        return receive_time;
    }

    public void setReceive_time(Timestamp receive_time) {
        this.receive_time = receive_time;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.mo_push_id;
        hash = 97 * hash + Objects.hashCode(this.msisdn);
        hash = 97 * hash + Objects.hashCode(this.channel);
        hash = 97 * hash + Objects.hashCode(this.operator);
        hash = 97 * hash + Objects.hashCode(this.groupe_name);
        hash = 97 * hash + this.user_id;
        hash = 97 * hash + Objects.hashCode(this.message);
        hash = 97 * hash + Objects.hashCode(this.receive_time);
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
        final Mo_Push other = (Mo_Push) obj;
        if (this.mo_push_id != other.mo_push_id) {
            return false;
        }
        if (this.user_id != other.user_id) {
            return false;
        }
        if (!Objects.equals(this.msisdn, other.msisdn)) {
            return false;
        }
        if (!Objects.equals(this.channel, other.channel)) {
            return false;
        }
        if (!Objects.equals(this.operator, other.operator)) {
            return false;
        }
        if (!Objects.equals(this.groupe_name, other.groupe_name)) {
            return false;
        }
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        if (!Objects.equals(this.receive_time, other.receive_time)) {
            return false;
        }
        return true;
    }
    
    
    
}
