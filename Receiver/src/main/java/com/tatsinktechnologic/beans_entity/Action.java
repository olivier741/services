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
public class Action implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = LogManager.getLogger(Action.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "act_generator")
    @SequenceGenerator(name="act_generator", sequenceName = "act_seq",allocationSize=1)
    private Long action_id;

    private String action_name;
    private int action_type;
    private String description;
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @OneToMany(mappedBy = "action",fetch = FetchType.LAZY)
    private Set<Command> listCommand = new HashSet<>();
    

    public Long getAction_id() {
        return action_id;
    }

    public String getAction_name() {
        return action_name;
    }

    public int getAction_type() {
        return action_type;
    }

    public String getDescription() {
        return description;
    }

    public Product getProduct() {
        return product;
    }

    public Set<Command> getListCommand() {
        return listCommand;
    }

    public void setAction_id(Long action_id) {
        this.action_id = action_id;
    }

    public void setAction_name(String action_name) {
        this.action_name = action_name;
    }

    public void setAction_type(int action_type) {
        this.action_type = action_type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setListCommand(Set<Command> listCommand) {
        this.listCommand = listCommand;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.action_id);
        hash = 89 * hash + Objects.hashCode(this.action_name);
        hash = 89 * hash + this.action_type;
        hash = 89 * hash + Objects.hashCode(this.description);
        hash = 89 * hash + Objects.hashCode(this.product);
        hash = 89 * hash + Objects.hashCode(this.listCommand);
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
        final Action other = (Action) obj;
        if (this.action_type != other.action_type) {
            return false;
        }
        if (!Objects.equals(this.action_name, other.action_name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.action_id, other.action_id)) {
            return false;
        }
        if (!Objects.equals(this.product, other.product)) {
            return false;
        }
        if (!Objects.equals(this.listCommand, other.listCommand)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Action{" + "action_id=" + action_id + ", action_name=" + action_name + ", action_type=" + action_type + ", description=" + description + ", product=" + product + ", listCommand=" + listCommand + '}';
    }

   
}
