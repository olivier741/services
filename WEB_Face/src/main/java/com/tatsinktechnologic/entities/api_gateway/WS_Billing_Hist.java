/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entities.api_gateway;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Transient;
import org.apache.log4j.Logger;

/**
 *
 * @author olivier
 */
@Entity
public class WS_Billing_Hist implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = Logger.getLogger(WS_Billing_Hist.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long bill_id;

    private String transaction_id;
    private String msisdn;
    private String Operator;
    private String client_name;
    private String webservice_name;
    
    @Enumerated(EnumType.STRING)
    private Type_Charging type_changing;
    
    @Enumerated(EnumType.STRING)
    private Type_Account type_account;
    
    private Timestamp charge_time;
    private long fee;
    
    @Lob 
    private String charge_request;
    
    @Lob 
    private String charge_response;
    
    private long duration;
     
    private String charge_error;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ws_access_mng_id", nullable = true)
    private WS_AccessManagement access_management;

    public Long getBill_id() {
        return bill_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getOperator() {
        return Operator;
    }

    public String getClient_name() {
        return client_name;
    }

    public String getWebservice_name() {
        return webservice_name;
    }

    public Type_Charging getType_changing() {
        return type_changing;
    }

    public Type_Account getType_account() {
        return type_account;
    }

    public Timestamp getCharge_time() {
        return charge_time;
    }

    public long getFee() {
        return fee;
    }

    public String getCharge_request() {
        return charge_request;
    }

    public String getCharge_response() {
        return charge_response;
    }

    public long getDuration() {
        return duration;
    }

    public String getCharge_error() {
        return charge_error;
    }

    public WS_AccessManagement getAccess_management() {
        return access_management;
    }

    public void setBill_id(Long bill_id) {
        this.bill_id = bill_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setOperator(String Operator) {
        this.Operator = Operator;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public void setWebservice_name(String webservice_name) {
        this.webservice_name = webservice_name;
    }

    public void setType_changing(Type_Charging type_changing) {
        this.type_changing = type_changing;
    }

    public void setType_account(Type_Account type_account) {
        this.type_account = type_account;
    }

    public void setCharge_time(Timestamp charge_time) {
        this.charge_time = charge_time;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public void setCharge_request(String charge_request) {
        this.charge_request = charge_request;
    }

    public void setCharge_response(String charge_response) {
        this.charge_response = charge_response;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setCharge_error(String charge_error) {
        this.charge_error = charge_error;
    }

    public void setAccess_management(WS_AccessManagement access_management) {
        this.access_management = access_management;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.bill_id);
        hash = 83 * hash + Objects.hashCode(this.transaction_id);
        hash = 83 * hash + Objects.hashCode(this.msisdn);
        hash = 83 * hash + Objects.hashCode(this.Operator);
        hash = 83 * hash + Objects.hashCode(this.client_name);
        hash = 83 * hash + Objects.hashCode(this.webservice_name);
        hash = 83 * hash + Objects.hashCode(this.type_changing);
        hash = 83 * hash + Objects.hashCode(this.type_account);
        hash = 83 * hash + Objects.hashCode(this.charge_time);
        hash = 83 * hash + (int) (this.fee ^ (this.fee >>> 32));
        hash = 83 * hash + Objects.hashCode(this.charge_request);
        hash = 83 * hash + Objects.hashCode(this.charge_response);
        hash = 83 * hash + (int) (this.duration ^ (this.duration >>> 32));
        hash = 83 * hash + Objects.hashCode(this.charge_error);
        hash = 83 * hash + Objects.hashCode(this.access_management);
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
        final WS_Billing_Hist other = (WS_Billing_Hist) obj;
        if (this.fee != other.fee) {
            return false;
        }
        if (this.duration != other.duration) {
            return false;
        }
        if (!Objects.equals(this.transaction_id, other.transaction_id)) {
            return false;
        }
        if (!Objects.equals(this.msisdn, other.msisdn)) {
            return false;
        }
        if (!Objects.equals(this.Operator, other.Operator)) {
            return false;
        }
        if (!Objects.equals(this.client_name, other.client_name)) {
            return false;
        }
        if (!Objects.equals(this.webservice_name, other.webservice_name)) {
            return false;
        }
        if (!Objects.equals(this.charge_request, other.charge_request)) {
            return false;
        }
        if (!Objects.equals(this.charge_response, other.charge_response)) {
            return false;
        }
        if (!Objects.equals(this.charge_error, other.charge_error)) {
            return false;
        }
        if (!Objects.equals(this.bill_id, other.bill_id)) {
            return false;
        }
        if (this.type_changing != other.type_changing) {
            return false;
        }
        if (this.type_account != other.type_account) {
            return false;
        }
        if (!Objects.equals(this.charge_time, other.charge_time)) {
            return false;
        }
        if (!Objects.equals(this.access_management, other.access_management)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WS_Billing_Hist{" + "bill_id=" + bill_id + ", transaction_id=" + transaction_id + ", msisdn=" + msisdn + ", Operator=" + Operator + ", client_name=" + client_name + ", webservice_name=" + webservice_name + ", type_changing=" + type_changing + ", type_account=" + type_account + ", charge_time=" + charge_time + ", fee=" + fee + ", charge_request=" + charge_request + ", charge_response=" + charge_response + ", duration=" + duration + ", charge_error=" + charge_error + ", access_management=" + access_management + '}';
    }

   
}
