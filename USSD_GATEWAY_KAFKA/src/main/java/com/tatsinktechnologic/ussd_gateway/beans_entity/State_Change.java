/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.beans_entity;

/**
 *
 * @author olivier.tatsinkou
 */

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "state_change",uniqueConstraints={@UniqueConstraint(columnNames = {"state_id_1","state_id_2"})})
public class State_Change
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private int state_change_id;

    private int state_id_1;
    private int state_id_2;
    private String input;
    private int priority;

    public int getState_change_id() {
        return state_change_id;
    }

    public void setState_change_id(int state_change_id) {
        this.state_change_id = state_change_id;
    }

    public int getState_id_1() {
        return state_id_1;
    }

    public void setState_id_1(int state_id_1) {
        this.state_id_1 = state_id_1;
    }

    public int getState_id_2() {
        return state_id_2;
    }

    public void setState_id_2(int state_id_2) {
        this.state_id_2 = state_id_2;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + this.state_change_id;
        hash = 43 * hash + this.state_id_1;
        hash = 43 * hash + this.state_id_2;
        hash = 43 * hash + Objects.hashCode(this.input);
        hash = 43 * hash + this.priority;
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
        final State_Change other = (State_Change) obj;
        if (this.state_change_id != other.state_change_id) {
            return false;
        }
        if (this.state_id_1 != other.state_id_1) {
            return false;
        }
        if (this.state_id_2 != other.state_id_2) {
            return false;
        }
        if (this.priority != other.priority) {
            return false;
        }
        if (!Objects.equals(this.input, other.input)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "State_Change{" + "state_change_id=" + state_change_id + ", state_id_1=" + state_id_1 + ", state_id_2=" + state_id_2 + ", input=" + input + ", priority=" + priority + '}';
    }
  
  
    
}
