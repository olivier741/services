/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entities.api_gateway;

import java.io.Serializable;
import java.sql.Timestamp;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author olivier
 */
@Entity
@Table(name = " ws_block_webserviceRel", uniqueConstraints = {@UniqueConstraint(columnNames = { "ws_block_api_id","execution_order","ws_webservice_id" })})
public class WS_Block_WebserviceRel implements Serializable {
    
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long ws_Block_WebserviceRel_id;
     
    @Column(nullable = false)
    private int execution_order;
     
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ws_block_api_id", nullable = true)
    private WS_Block_API ws_block_api;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ws_webservice_id", nullable = true)
    private WS_Webservice ws_webservice;
    
    @Column(nullable = true)
    @CreationTimestamp
    private Timestamp create_time;
    
    @Column(nullable = true)
    @UpdateTimestamp
    private Timestamp update_time;

    public int getExecution_order() {
        return execution_order;
    }

    public void setExecution_order(int execution_order) {
        this.execution_order = execution_order;
    }

    
    
    public Long getWs_Block_WebserviceRel_id() {
        return ws_Block_WebserviceRel_id;
    }

    public void setWs_Block_WebserviceRel_id(Long ws_Block_WebserviceRel_id) {
        this.ws_Block_WebserviceRel_id = ws_Block_WebserviceRel_id;
    }

    public WS_Block_API getWs_block_api() {
        return ws_block_api;
    }

    public void setWs_block_api(WS_Block_API ws_block_api) {
        this.ws_block_api = ws_block_api;
    }

    public WS_Webservice getWs_webservice() {
        return ws_webservice;
    }

    public void setWs_webservice(WS_Webservice ws_webservice) {
        this.ws_webservice = ws_webservice;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.ws_Block_WebserviceRel_id);
        hash = 43 * hash + this.execution_order;
        hash = 43 * hash + Objects.hashCode(this.ws_block_api);
        hash = 43 * hash + Objects.hashCode(this.ws_webservice);
        hash = 43 * hash + Objects.hashCode(this.create_time);
        hash = 43 * hash + Objects.hashCode(this.update_time);
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
        final WS_Block_WebserviceRel other = (WS_Block_WebserviceRel) obj;
        if (this.execution_order != other.execution_order) {
            return false;
        }
        if (!Objects.equals(this.ws_Block_WebserviceRel_id, other.ws_Block_WebserviceRel_id)) {
            return false;
        }
        if (!Objects.equals(this.ws_block_api, other.ws_block_api)) {
            return false;
        }
        if (!Objects.equals(this.ws_webservice, other.ws_webservice)) {
            return false;
        }
        if (!Objects.equals(this.create_time, other.create_time)) {
            return false;
        }
        if (!Objects.equals(this.update_time, other.update_time)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WS_Block_WebserviceRel{" + "ws_Block_WebserviceRel_id=" + ws_Block_WebserviceRel_id + ", execution_order=" + execution_order + ", ws_block_api=" + ws_block_api + ", ws_webservice=" + ws_webservice + ", create_time=" + create_time + ", update_time=" + update_time + '}';
    }

   
}
