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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author olivier
 */
@Entity
@Table(name = "ws_block_api")
public class WS_Block_API implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long ws_block_api_id;
    
    @Column(nullable = false, unique = true)
    private String block_api_name;
   
    @Column(nullable = true)
    private String description;
    
    @Column(nullable = true)
    @CreationTimestamp
    private Timestamp create_time;
    
    @Column(nullable = true)
    @UpdateTimestamp
    private Timestamp update_time;
    
    @OneToMany(mappedBy = "ws_block_api")
    private Set<WS_Block_WebserviceRel> listWS_Block_WebserviceRel = new HashSet<>();

    @OneToMany(mappedBy = "ws_block_api")
    private Set<WS_AccessManagement> listAccessManagement = new HashSet<>();

    public Long getWs_block_api_id() {
        return ws_block_api_id;
    }

    public void setWs_block_api_id(Long ws_block_api_id) {
        this.ws_block_api_id = ws_block_api_id;
    }

    public String getBlock_api_name() {
        return block_api_name;
    }

    public void setBlock_api_name(String block_api_name) {
        this.block_api_name = block_api_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

  
    public Set<WS_AccessManagement> getListAccessManagement() {
        return listAccessManagement;
    }

    public void setListAccessManagement(Set<WS_AccessManagement> listAccessManagement) {
        this.listAccessManagement = listAccessManagement;
    }

    public Set<WS_Block_WebserviceRel> getListWS_Block_WebserviceRel() {
        return listWS_Block_WebserviceRel;
    }

    public void setListWS_Block_WebserviceRel(Set<WS_Block_WebserviceRel> listWS_Block_WebserviceRel) {
        this.listWS_Block_WebserviceRel = listWS_Block_WebserviceRel;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.ws_block_api_id);
        hash = 29 * hash + Objects.hashCode(this.block_api_name);
        hash = 29 * hash + Objects.hashCode(this.description);
        hash = 29 * hash + Objects.hashCode(this.create_time);
        hash = 29 * hash + Objects.hashCode(this.update_time);
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
        final WS_Block_API other = (WS_Block_API) obj;
        if (!Objects.equals(this.block_api_name, other.block_api_name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.ws_block_api_id, other.ws_block_api_id)) {
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
        return "WS_Block_API{" + "ws_block_api_id=" + ws_block_api_id + ", block_api_name=" + block_api_name + ", description=" + description + ", create_time=" + create_time + ", update_time=" + update_time + '}';
    }
    
 

}
