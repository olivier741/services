/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans_entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author olivier
 */
@Embeddable
public class ProcessKey implements Serializable {

    @Transient
    private Logger logger = LogManager.getLogger(ProcessKey.class);
        
    private String server_name;
    
    private String node_name;

    public ProcessKey() {
    }

    public ProcessKey(String server_name, String node_name) {
        this.server_name = server_name;
        this.node_name = node_name;
    }
    
    

    public String getServer_name() {
        return server_name;
    }

    public String getNode_name() {
        return node_name;
    }

    public void setServer_name(String server_name) {
        this.server_name = server_name;
    }

    public void setNode_name(String node_name) {
        this.node_name = node_name;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.server_name);
        hash = 47 * hash + Objects.hashCode(this.node_name);
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
        final ProcessKey other = (ProcessKey) obj;
        if (!Objects.equals(this.server_name, other.server_name)) {
            return false;
        }
        if (!Objects.equals(this.node_name, other.node_name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ProcessKey{" + "server_name=" + server_name + ", node_name=" + node_name + '}';
    }
    
    
    
    
}
