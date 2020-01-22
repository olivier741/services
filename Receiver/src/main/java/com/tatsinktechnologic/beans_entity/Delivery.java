/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans_entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author olivier.tatsinkou
 */
@Entity
public class Delivery implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = LogManager.getLogger(Action.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dlv_generator")
    @SequenceGenerator(name="dlv_generator", sequenceName = "dlv_seq",allocationSize=1)
    private Long dlv_id;
    
    private String message_id;
    private String sender;
    private String receiver;
    private String delivery_id;
    private int delivery_sub;
    private int delivery_dlvrd;
    private Date delivery_submitDate;
    private Date delivery_doneDate;
    private String delivery_status;
    private String delivery_err;
    private String delivery_text;

    public Long getDlv_id() {
        return dlv_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getDelivery_id() {
        return delivery_id;
    }

    public int getDelivery_sub() {
        return delivery_sub;
    }

    public int getDelivery_dlvrd() {
        return delivery_dlvrd;
    }

    public Date getDelivery_submitDate() {
        return delivery_submitDate;
    }

    public Date getDelivery_doneDate() {
        return delivery_doneDate;
    }

    public String getDelivery_status() {
        return delivery_status;
    }

    public String getDelivery_err() {
        return delivery_err;
    }

    public String getDelivery_text() {
        return delivery_text;
    }

    public void setDlv_id(Long dlv_id) {
        this.dlv_id = dlv_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setDelivery_id(String delivery_id) {
        this.delivery_id = delivery_id;
    }

    public void setDelivery_sub(int delivery_sub) {
        this.delivery_sub = delivery_sub;
    }

    public void setDelivery_dlvrd(int delivery_dlvrd) {
        this.delivery_dlvrd = delivery_dlvrd;
    }

    public void setDelivery_submitDate(Date delivery_submitDate) {
        this.delivery_submitDate = delivery_submitDate;
    }

    public void setDelivery_doneDate(Date delivery_doneDate) {
        this.delivery_doneDate = delivery_doneDate;
    }

    public void setDelivery_status(String delivery_status) {
        this.delivery_status = delivery_status;
    }

    public void setDelivery_err(String delivery_err) {
        this.delivery_err = delivery_err;
    }

    public void setDelivery_text(String delivery_text) {
        this.delivery_text = delivery_text;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.dlv_id);
        hash = 89 * hash + Objects.hashCode(this.message_id);
        hash = 89 * hash + Objects.hashCode(this.sender);
        hash = 89 * hash + Objects.hashCode(this.receiver);
        hash = 89 * hash + Objects.hashCode(this.delivery_id);
        hash = 89 * hash + this.delivery_sub;
        hash = 89 * hash + this.delivery_dlvrd;
        hash = 89 * hash + Objects.hashCode(this.delivery_submitDate);
        hash = 89 * hash + Objects.hashCode(this.delivery_doneDate);
        hash = 89 * hash + Objects.hashCode(this.delivery_status);
        hash = 89 * hash + Objects.hashCode(this.delivery_err);
        hash = 89 * hash + Objects.hashCode(this.delivery_text);
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
        final Delivery other = (Delivery) obj;
        if (this.delivery_sub != other.delivery_sub) {
            return false;
        }
        if (this.delivery_dlvrd != other.delivery_dlvrd) {
            return false;
        }
        if (!Objects.equals(this.message_id, other.message_id)) {
            return false;
        }
        if (!Objects.equals(this.sender, other.sender)) {
            return false;
        }
        if (!Objects.equals(this.receiver, other.receiver)) {
            return false;
        }
        if (!Objects.equals(this.delivery_id, other.delivery_id)) {
            return false;
        }
        if (!Objects.equals(this.delivery_status, other.delivery_status)) {
            return false;
        }
        if (!Objects.equals(this.delivery_err, other.delivery_err)) {
            return false;
        }
        if (!Objects.equals(this.delivery_text, other.delivery_text)) {
            return false;
        }
        if (!Objects.equals(this.dlv_id, other.dlv_id)) {
            return false;
        }
        if (!Objects.equals(this.delivery_submitDate, other.delivery_submitDate)) {
            return false;
        }
        if (!Objects.equals(this.delivery_doneDate, other.delivery_doneDate)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DeliveryMessage{" + "dlv_id=" + dlv_id + ", message_id=" + message_id + ", sender=" + sender + ", receiver=" + receiver + ", delivery_id=" + delivery_id + ", delivery_sub=" + delivery_sub + ", delivery_dlvrd=" + delivery_dlvrd + ", delivery_submitDate=" + delivery_submitDate + ", delivery_doneDate=" + delivery_doneDate + ", delivery_status=" + delivery_status + ", delivery_err=" + delivery_err + ", delivery_text=" + delivery_text + '}';
    }
    
    
}
