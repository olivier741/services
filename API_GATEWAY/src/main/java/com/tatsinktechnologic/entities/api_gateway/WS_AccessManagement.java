/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entities.api_gateway;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import org.apache.log4j.Logger;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


/**
 *
 * @author olivier
 */
@Entity
@Table(name = "ws_accessManagement",uniqueConstraints={@UniqueConstraint(columnNames = {"ws_client_id","ws_block_api_id"})})
public class WS_AccessManagement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long ws_access_mng_id;

    @Column(nullable = false, unique = true)
    private String access_name;
    
    @Column(nullable = true)
    private int max_connexion_allocate;
    
    @Column(nullable = true)
    private int max_request_allocate;
    
    @Column(nullable = true)
    private Timestamp start_time;
    
    @Column(nullable = true)
    private Timestamp expire_time;
    
    @Column(nullable = true)
    private String description;
    
    @Column(nullable = true)
    @CreationTimestamp
    private Timestamp create_time;
    
    @Column(nullable = true)
    @UpdateTimestamp
    private Timestamp update_time;
       
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ws_client_id", nullable = true)
    private WS_Client ws_client;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ws_block_api_id", nullable = true)
    private WS_Block_API ws_block_api;
    
    @OneToMany(mappedBy = "access_management")
    private Set<WS_Transaction_Log> listtransaction_log = new HashSet<>();
    
    @OneToMany(mappedBy = "access_management")
    private Set<WS_Billing_Hist> listbilling_hist = new HashSet<>();

    public Long getWs_access_mng_id() {
        return ws_access_mng_id;
    }

    public void setWs_access_mng_id(Long ws_access_mng_id) {
        this.ws_access_mng_id = ws_access_mng_id;
    }

    public String getAccess_name() {
        return access_name;
    }

    public void setAccess_name(String access_name) {
        this.access_name = access_name;
    }

    public int getMax_connexion_allocate() {
        return max_connexion_allocate;
    }

    public void setMax_connexion_allocate(int max_connexion_allocate) {
        this.max_connexion_allocate = max_connexion_allocate;
    }

    public int getMax_request_allocate() {
        return max_request_allocate;
    }

    public void setMax_request_allocate(int max_request_allocate) {
        this.max_request_allocate = max_request_allocate;
    }

    public Timestamp getStart_time() {
        return start_time;
    }

    public void setStart_time(Timestamp start_time) {
        this.start_time = start_time;
    }

    public Timestamp getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(Timestamp expire_time) {
        this.expire_time = expire_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WS_Client getWs_client() {
        return ws_client;
    }

    public void setWs_client(WS_Client ws_client) {
        this.ws_client = ws_client;
    }

    public WS_Block_API getWs_block_api() {
        return ws_block_api;
    }

    public void setWs_block_api(WS_Block_API ws_block_api) {
        this.ws_block_api = ws_block_api;
    }

    public Set<WS_Transaction_Log> getListtransaction_log() {
        return listtransaction_log;
    }

    public void setListtransaction_log(Set<WS_Transaction_Log> listtransaction_log) {
        this.listtransaction_log = listtransaction_log;
    }

    public Set<WS_Billing_Hist> getListbilling_hist() {
        return listbilling_hist;
    }

    public void setListbilling_hist(Set<WS_Billing_Hist> listbilling_hist) {
        this.listbilling_hist = listbilling_hist;
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
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.ws_access_mng_id);
        hash = 67 * hash + Objects.hashCode(this.access_name);
        hash = 67 * hash + this.max_connexion_allocate;
        hash = 67 * hash + this.max_request_allocate;
        hash = 67 * hash + Objects.hashCode(this.start_time);
        hash = 67 * hash + Objects.hashCode(this.expire_time);
        hash = 67 * hash + Objects.hashCode(this.description);
        hash = 67 * hash + Objects.hashCode(this.ws_client);
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
        final WS_AccessManagement other = (WS_AccessManagement) obj;
        if (this.max_connexion_allocate != other.max_connexion_allocate) {
            return false;
        }
        if (this.max_request_allocate != other.max_request_allocate) {
            return false;
        }
        if (!Objects.equals(this.access_name, other.access_name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.ws_access_mng_id, other.ws_access_mng_id)) {
            return false;
        }
        if (!Objects.equals(this.start_time, other.start_time)) {
            return false;
        }
        if (!Objects.equals(this.expire_time, other.expire_time)) {
            return false;
        }
        if (!Objects.equals(this.ws_client, other.ws_client)) {
            return false;
        }
      
        return true;
    }

    @Override
    public String toString() {
        return "WS_AccessManagement{" + "ws_access_mng_id=" + ws_access_mng_id + ", access_name=" + access_name + ", max_connexion_allocate=" + max_connexion_allocate + ", max_request_allocate=" + max_request_allocate + ", start_time=" + start_time + ", expire_time=" + expire_time + ", description=" + description + ", ws_client=" + ws_client + '}';
    }

  
}
