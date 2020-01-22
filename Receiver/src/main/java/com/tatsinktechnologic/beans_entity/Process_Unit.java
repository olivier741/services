/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans_entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author olivier
 */
@Entity
public class Process_Unit implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = LogManager.getLogger(Process_Unit.class);
    
    @EmbeddedId
    private ProcessKey process_unit_id;
    private String IP_address;
    
    @OneToMany(mappedBy = "process_unit",fetch = FetchType.LAZY)
    private Set<Mo_Message_His> listMo_Message_His = new HashSet<>();
    
    @OneToMany(mappedBy = "process_unit",fetch = FetchType.LAZY)
    private Set<Mt_Message_His> listMt_Message_his = new HashSet<>();
    
    @OneToMany(mappedBy = "process_unit",fetch = FetchType.LAZY)
    private Set<Charge_His> listCharge_His = new HashSet<>();
    
    @OneToMany(mappedBy = "process_unit",fetch = FetchType.LAZY)
    private Set<Configuration> listConfiguration = new HashSet<>();

    public ProcessKey getProcess_unit_id() {
        return process_unit_id;
    }

    public String getIP_address() {
        return IP_address;
    }

    public Set<Mo_Message_His> getListMo_Message_His() {
        return listMo_Message_His;
    }

    public Set<Mt_Message_His> getListMt_Message_his() {
        return listMt_Message_his;
    }

    public Set<Charge_His> getListCharge_His() {
        return listCharge_His;
    }

    public Set<Configuration> getListConfiguration() {
        return listConfiguration;
    }

    public void setProcess_unit_id(ProcessKey process_unit_id) {
        this.process_unit_id = process_unit_id;
    }

    public void setIP_address(String IP_address) {
        this.IP_address = IP_address;
    }

    public void setListMo_Message_His(Set<Mo_Message_His> listMo_Message_His) {
        this.listMo_Message_His = listMo_Message_His;
    }

    public void setListMt_Message_his(Set<Mt_Message_His> listMt_Message_his) {
        this.listMt_Message_his = listMt_Message_his;
    }

    public void setListCharge_His(Set<Charge_His> listCharge_His) {
        this.listCharge_His = listCharge_His;
    }

    public void setListConfiguration(Set<Configuration> listConfiguration) {
        this.listConfiguration = listConfiguration;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.process_unit_id);
        hash = 29 * hash + Objects.hashCode(this.IP_address);
        hash = 29 * hash + Objects.hashCode(this.listMo_Message_His);
        hash = 29 * hash + Objects.hashCode(this.listMt_Message_his);
        hash = 29 * hash + Objects.hashCode(this.listCharge_His);
        hash = 29 * hash + Objects.hashCode(this.listConfiguration);
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
        final Process_Unit other = (Process_Unit) obj;
        if (!Objects.equals(this.IP_address, other.IP_address)) {
            return false;
        }
        if (!Objects.equals(this.process_unit_id, other.process_unit_id)) {
            return false;
        }
        if (!Objects.equals(this.listMo_Message_His, other.listMo_Message_His)) {
            return false;
        }
        if (!Objects.equals(this.listMt_Message_his, other.listMt_Message_his)) {
            return false;
        }
        if (!Objects.equals(this.listCharge_His, other.listCharge_His)) {
            return false;
        }
        if (!Objects.equals(this.listConfiguration, other.listConfiguration)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Process_Unit{" + "process_unit_id=" + process_unit_id + ", IP_address=" + IP_address + '}';
    }

   


}
