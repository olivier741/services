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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author olivier.tatsinkou
 */
@Entity
@Table(name = "serviceHistory", indexes = {
    @Index(name = "IDX_SERVHIST_MSISDN", columnList = "msisdn")})
public class ServiceHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private int serviceHistory_id;

    private String transaction_id;
    private String msisdn;
    private String product_code;
    private String service_name;
    private String description;

    @Column(nullable = true)
    @CreationTimestamp
    private Timestamp process_time;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "content_id", nullable = true)
    private ContentMessage content;

    public int getServiceHistory_id() {
        return serviceHistory_id;
    }

    public void setServiceHistory_id(int serviceHistory_id) {
        this.serviceHistory_id = serviceHistory_id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public Timestamp getProcess_time() {
        return process_time;
    }

    public void setProcess_time(Timestamp process_time) {
        this.process_time = process_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

   

    public ContentMessage getContent() {
        return content;
    }

    public void setContent(ContentMessage content) {
        this.content = content;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.serviceHistory_id;
        hash = 47 * hash + Objects.hashCode(this.transaction_id);
        hash = 47 * hash + Objects.hashCode(this.msisdn);
        hash = 47 * hash + Objects.hashCode(this.product_code);
        hash = 47 * hash + Objects.hashCode(this.service_name);
        hash = 47 * hash + Objects.hashCode(this.process_time);
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
        final ServiceHistory other = (ServiceHistory) obj;
        if (this.serviceHistory_id != other.serviceHistory_id) {
            return false;
        }
        if (!Objects.equals(this.transaction_id, other.transaction_id)) {
            return false;
        }
        if (!Objects.equals(this.msisdn, other.msisdn)) {
            return false;
        }
        if (!Objects.equals(this.product_code, other.product_code)) {
            return false;
        }
        if (!Objects.equals(this.service_name, other.service_name)) {
            return false;
        }
        if (!Objects.equals(this.process_time, other.process_time)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ServiceHistory{" + "serviceHistory_id=" + serviceHistory_id + ", transaction_id=" + transaction_id + ", msisdn=" + msisdn + ", product_code=" + product_code + ", service_name=" + service_name + ", process_time=" + process_time + '}';
    }

   

}
