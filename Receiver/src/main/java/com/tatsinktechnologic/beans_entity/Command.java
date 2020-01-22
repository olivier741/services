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
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


/**
 *
 * @author olivier
 */
@Entity
public class Command implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = LogManager.getLogger(Command.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cmd_generator")
    @SequenceGenerator(name="cmd_generator", sequenceName = "cmd_seq",allocationSize=1)
    private Long command_id;

    private String command_name;
    private String Split_separator;
   
    
    @ElementCollection
    @CollectionTable(name = "PARAM",joinColumns = @JoinColumn(name = "command_id"))
    private Set<Param> listParam = new HashSet<Param>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;
    
    @OneToMany(mappedBy = "command",fetch = FetchType.LAZY)
    private Set<Configuration> listConfig = new HashSet<>();
    
    @OneToMany(mappedBy = "command",fetch = FetchType.LAZY)
    private Set<Mo_Message> listMo_Message = new HashSet<>();
    
    @OneToMany(mappedBy = "command",fetch = FetchType.LAZY)
    private Set<Mo_Message_His> listMo_Message_His = new HashSet<>();
    
    @OneToMany(mappedBy = "command",fetch = FetchType.LAZY)
    private Set<Mt_Message> listMt_Message = new HashSet<>();
    
    @OneToMany(mappedBy = "command",fetch = FetchType.LAZY)
    private Set<Mt_Message_His> listMt_Message_His = new HashSet<>();

    public Long getCommand_id() {
        return command_id;
    }

    public String getCommand_name() {
        return command_name;
    }

    public String getSplit_separator() {
        return Split_separator;
    }

    public Set<Param> getListParam() {
        return listParam;
    }

    public Action getAction() {
        return action;
    }

    public Set<Configuration> getListConfig() {
        return listConfig;
    }

    public Set<Mo_Message> getListMo_Message() {
        return listMo_Message;
    }

    public Set<Mt_Message> getListMt_Message() {
        return listMt_Message;
    }

    public void setCommand_id(Long command_id) {
        this.command_id = command_id;
    }

    public void setCommand_name(String command_name) {
        this.command_name = command_name;
    }

    public void setSplit_separator(String Split_separator) {
        this.Split_separator = Split_separator;
    }

    public void setListParam(Set<Param> listParam) {
        this.listParam = listParam;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setListConfig(Set<Configuration> listConfig) {
        this.listConfig = listConfig;
    }

    public void setListMo_Message(Set<Mo_Message> listMo_Message) {
        this.listMo_Message = listMo_Message;
    }

    public void setListMt_Message(Set<Mt_Message> listMt_Message) {
        this.listMt_Message = listMt_Message;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.command_id);
        hash = 29 * hash + Objects.hashCode(this.command_name);
        hash = 29 * hash + Objects.hashCode(this.Split_separator);
        hash = 29 * hash + Objects.hashCode(this.listParam);
        hash = 29 * hash + Objects.hashCode(this.action);
        hash = 29 * hash + Objects.hashCode(this.listConfig);
        hash = 29 * hash + Objects.hashCode(this.listMo_Message);
        hash = 29 * hash + Objects.hashCode(this.listMt_Message);
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
        final Command other = (Command) obj;
        if (!Objects.equals(this.command_name, other.command_name)) {
            return false;
        }
        if (!Objects.equals(this.Split_separator, other.Split_separator)) {
            return false;
        }
        if (!Objects.equals(this.command_id, other.command_id)) {
            return false;
        }
        if (!Objects.equals(this.listParam, other.listParam)) {
            return false;
        }
        if (!Objects.equals(this.action, other.action)) {
            return false;
        }
        if (!Objects.equals(this.listConfig, other.listConfig)) {
            return false;
        }
        if (!Objects.equals(this.listMo_Message, other.listMo_Message)) {
            return false;
        }
        if (!Objects.equals(this.listMt_Message, other.listMt_Message)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Command{" + "command_id=" + command_id + ", command_name=" + command_name + ", Split_separator=" + Split_separator + ", listParam=" + listParam + ", action=" + action + '}';
    }

  
}
