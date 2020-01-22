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
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author olivier
 */
@Entity
public class WS_Billing_Hist implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long bill_id;

    private String reg_transaction_id;
    private String msisdn;
    private String Operator;
    private String client_name;
    private String webservice_name;
    private long charge_amount;
    private String charge_reason;
    private String client_IP;
    
    @Enumerated(EnumType.STRING)
    private Type_Charging type_changing;
    
    @Enumerated(EnumType.STRING)
    private Type_Account type_account;
    
    @Column(nullable = true)
    @CreationTimestamp
    private Timestamp charge_time;
    
    private long duration;
     
    private String charge_error;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ws_access_mng_id", nullable = true)
    private WS_AccessManagement access_management;

    public String getClient_IP() {
        return client_IP;
    }

    public void setClient_IP(String client_IP) {
        this.client_IP = client_IP;
    }

    
    
    public String getReg_transaction_id() {
        return reg_transaction_id;
    }

    public void setReg_transaction_id(String reg_transaction_id) {
        this.reg_transaction_id = reg_transaction_id;
    }

    public Long getBill_id() {
        return bill_id;
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

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setCharge_error(String charge_error) {
        this.charge_error = charge_error;
    }

    public void setAccess_management(WS_AccessManagement access_management) {
        this.access_management = access_management;
    }

    public long getCharge_amount() {
        return charge_amount;
    }

    public void setCharge_amount(long charge_amount) {
        this.charge_amount = charge_amount;
    }

    public String getCharge_reason() {
        return charge_reason;
    }

    public void setCharge_reason(String charge_reason) {
        this.charge_reason = charge_reason;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.bill_id);
        hash = 19 * hash + Objects.hashCode(this.reg_transaction_id);
        hash = 19 * hash + Objects.hashCode(this.msisdn);
        hash = 19 * hash + Objects.hashCode(this.Operator);
        hash = 19 * hash + Objects.hashCode(this.client_name);
        hash = 19 * hash + Objects.hashCode(this.webservice_name);
        hash = 19 * hash + (int) (this.charge_amount ^ (this.charge_amount >>> 32));
        hash = 19 * hash + Objects.hashCode(this.charge_reason);
        hash = 19 * hash + Objects.hashCode(this.type_changing);
        hash = 19 * hash + Objects.hashCode(this.type_account);
        hash = 19 * hash + Objects.hashCode(this.charge_time);
        hash = 19 * hash + (int) (this.duration ^ (this.duration >>> 32));
        hash = 19 * hash + Objects.hashCode(this.charge_error);
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
        if (this.charge_amount != other.charge_amount) {
            return false;
        }
        if (this.duration != other.duration) {
            return false;
        }
        if (!Objects.equals(this.reg_transaction_id, other.reg_transaction_id)) {
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
        if (!Objects.equals(this.charge_reason, other.charge_reason)) {
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
        return true;
    }

    @Override
    public String toString() {
        return "WS_Billing_Hist{" + "bill_id=" + bill_id + ", reg_transaction_id=" + reg_transaction_id + ", msisdn=" + msisdn + ", Operator=" + Operator + ", client_name=" + client_name + ", webservice_name=" + webservice_name + ", charge_amount=" + charge_amount + ", charge_reason=" + charge_reason + ", type_changing=" + type_changing + ", type_account=" + type_account + ", charge_time=" + charge_time + ", duration=" + duration + ", charge_error=" + charge_error + '}';
    }

}
