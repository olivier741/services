/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entities.api_gateway;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author olivier
 */
@Entity
@Table(name = "ws_header_param",uniqueConstraints={@UniqueConstraint(columnNames = {"param_key","param_value","ws_webservice_id"})})
public class WS_Header_Param implements Serializable{
    
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer ws_header_param_id;
    
    @Column(nullable = false, unique = true)
    private String param_key;
    
    @Column(nullable = false)
    private String param_value;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ws_webservice_id", nullable = false)
    private WS_Webservice ws_webservice;

    public Integer getWs_header_param_id() {
        return ws_header_param_id;
    }

    public void setWs_header_param_id(Integer ws_header_param_id) {
        this.ws_header_param_id = ws_header_param_id;
    }

    public String getParam_key() {
        return param_key;
    }

    public void setParam_key(String param_key) {
        this.param_key = param_key;
    }

    public String getParam_value() {
        return param_value;
    }

    public void setParam_value(String param_value) {
        this.param_value = param_value;
    }

    public WS_Webservice getWs_webservice() {
        return ws_webservice;
    }

    public void setWs_webservice(WS_Webservice ws_webservice) {
        this.ws_webservice = ws_webservice;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.ws_header_param_id);
        hash = 83 * hash + Objects.hashCode(this.param_key);
        hash = 83 * hash + Objects.hashCode(this.param_value);
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
        final WS_Header_Param other = (WS_Header_Param) obj;
        if (!Objects.equals(this.param_key, other.param_key)) {
            return false;
        }
        if (!Objects.equals(this.param_value, other.param_value)) {
            return false;
        }
        if (!Objects.equals(this.ws_header_param_id, other.ws_header_param_id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WS_Header_Param{" + "ws_header_param_id=" + ws_header_param_id + ", param_key=" + param_key + ", param_value=" + param_value + ", ws_webservice=" + ws_webservice + '}';
    }

   
    
    
    
}
