/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans_entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author olivier
 */
@Entity
public class Configuration implements Serializable {

    private static final long serialVersionUID = 1L;
    
   @Transient
    private Logger logger = LogManager.getLogger(Configuration.class);
        
        
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cfg_generator")
    @SequenceGenerator(name="cfg_generator", sequenceName = "cfg_seq",allocationSize=1)
    private Long ccnfiguration_id;

    private String process_param;
    private String process_value;
    private String process_description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "command_id", nullable = true)
    private Command command;
    
    @ManyToOne
    @JoinColumns({
        @JoinColumn(
            name = "server_name",
            referencedColumnName = "server_name"),
        @JoinColumn(
            name = "node_name",
            referencedColumnName = "node_name")
    })
    private Process_Unit process_unit;

    public Long getCcnfiguration_id() {
        return ccnfiguration_id;
    }

    public String getProcess_param() {
        return process_param;
    }

    public String getProcess_value() {
        return process_value;
    }

    public String getProcess_description() {
        return process_description;
    }

    public Command getCommand() {
        return command;
    }

    public Process_Unit getProcess_unit() {
        return process_unit;
    }

    public void setCcnfiguration_id(Long ccnfiguration_id) {
        this.ccnfiguration_id = ccnfiguration_id;
    }

   
    public void setProcess_param(String process_param) {
        this.process_param = process_param;
    }

    public void setProcess_value(String process_value) {
        this.process_value = process_value;
    }

    public void setProcess_description(String process_description) {
        this.process_description = process_description;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public void setProcess_unit(Process_Unit process_unit) {
        this.process_unit = process_unit;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.ccnfiguration_id);
        hash = 53 * hash + Objects.hashCode(this.process_param);
        hash = 53 * hash + Objects.hashCode(this.process_value);
        hash = 53 * hash + Objects.hashCode(this.process_description);
        hash = 53 * hash + Objects.hashCode(this.command);
        hash = 53 * hash + Objects.hashCode(this.process_unit);
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
        final Configuration other = (Configuration) obj;
        if (!Objects.equals(this.process_param, other.process_param)) {
            return false;
        }
        if (!Objects.equals(this.process_value, other.process_value)) {
            return false;
        }
        if (!Objects.equals(this.process_description, other.process_description)) {
            return false;
        }
        if (!Objects.equals(this.ccnfiguration_id, other.ccnfiguration_id)) {
            return false;
        }
        if (!Objects.equals(this.command, other.command)) {
            return false;
        }
        if (!Objects.equals(this.process_unit, other.process_unit)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Configuration{" + "ccnfiguration_id=" + ccnfiguration_id + ", process_param=" + process_param + ", process_value=" + process_value + ", process_description=" + process_description + ", command=" + command + ", process_unit=" + process_unit + '}';
    }
  
    
}
