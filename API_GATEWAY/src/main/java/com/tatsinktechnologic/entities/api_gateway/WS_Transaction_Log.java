/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entities.api_gateway;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import org.apache.log4j.Logger;
import org.hibernate.annotations.CreationTimestamp;


/**
 *
 * @author olivier
 */
@Entity
public class WS_Transaction_Log implements Serializable {

    private static final long serialVersionUID = 1L;
        
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long ws_transaction_id;
    
    private String reg_transaction_id;
    private String msisdn;
    private String Operator;
    private String client_name;
    private String webservice_name;
    private String client_IP;
    
    @Lob
    private String request;
    
    @Lob
    private String response;
        
    private long duration;   
    private String error_code;
    
    @Column(nullable = true)
    @CreationTimestamp
    private Timestamp transaction_time;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ws_access_mng_id", nullable = true)
    private WS_AccessManagement access_management;

    public String getClient_IP() {
        return client_IP;
    }

    public void setClient_IP(String client_IP) {
        this.client_IP = client_IP;
    }

   
    
    public Long getWs_transaction_id() {
        return ws_transaction_id;
    }

    public String getReg_transaction_id() {
        return reg_transaction_id;
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

    public String getRequest() {
        return request;
    }

    public String getResponse() {
        return response;
    }

    public long getDuration() {
        return duration;
    }

    public String getError_code() {
        return error_code;
    }

    public WS_AccessManagement getAccess_management() {
        return access_management;
    }

    public void setWs_transaction_id(Long ws_transaction_id) {
        this.ws_transaction_id = ws_transaction_id;
    }

    public void setReg_transaction_id(String reg_transaction_id) {
        this.reg_transaction_id = reg_transaction_id;
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

    public void setRequest(String request) {
        this.request = request;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public void setAccess_management(WS_AccessManagement access_management) {
        this.access_management = access_management;
    }

    public Timestamp getTransaction_time() {
        return transaction_time;
    }

    public void setTransaction_time(Timestamp transaction_time) {
        this.transaction_time = transaction_time;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.ws_transaction_id);
        hash = 23 * hash + Objects.hashCode(this.reg_transaction_id);
        hash = 23 * hash + Objects.hashCode(this.msisdn);
        hash = 23 * hash + Objects.hashCode(this.Operator);
        hash = 23 * hash + Objects.hashCode(this.client_name);
        hash = 23 * hash + Objects.hashCode(this.webservice_name);
        hash = 23 * hash + Objects.hashCode(this.request);
        hash = 23 * hash + Objects.hashCode(this.response);
        hash = 23 * hash + (int) (this.duration ^ (this.duration >>> 32));
        hash = 23 * hash + Objects.hashCode(this.error_code);
        hash = 23 * hash + Objects.hashCode(this.transaction_time);
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
        final WS_Transaction_Log other = (WS_Transaction_Log) obj;
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
        if (!Objects.equals(this.request, other.request)) {
            return false;
        }
        if (!Objects.equals(this.response, other.response)) {
            return false;
        }
        if (!Objects.equals(this.error_code, other.error_code)) {
            return false;
        }
        if (!Objects.equals(this.ws_transaction_id, other.ws_transaction_id)) {
            return false;
        }
        if (!Objects.equals(this.transaction_time, other.transaction_time)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WS_Transaction_Log{" + "ws_transaction_id=" + ws_transaction_id + ", reg_transaction_id=" + reg_transaction_id + ", msisdn=" + msisdn + ", Operator=" + Operator + ", client_name=" + client_name + ", webservice_name=" + webservice_name + ", request=" + request + ", response=" + response + ", duration=" + duration + ", error_code=" + error_code + ", transaction_time=" + transaction_time + '}';
    }

}
