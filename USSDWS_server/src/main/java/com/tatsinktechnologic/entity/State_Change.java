/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entity;

/**
 *
 * @author olivier.tatsinkou
 */

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "state_change",uniqueConstraints={@UniqueConstraint(columnNames = {"current_state_id","next_state_id"})})
public class State_Change implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private int state_change_id;

    private String input;
    private int priority;
    
    @ManyToOne
    @JoinColumn(name="current_state_id")
    private State current_state;

    @ManyToOne
    @JoinColumn(name="next_state_id")
    private State next_state;


    public int getState_change_id() {
        return state_change_id;
    }

    public void setState_change_id(int state_change_id) {
        this.state_change_id = state_change_id;
    }

    public State getCurrent_state() {
        return current_state;
    }

    public void setCurrent_state(State current_state) {
        this.current_state = current_state;
    }

    public State getNext_state() {
        return next_state;
    }

    public void setNext_state(State next_state) {
        this.next_state = next_state;
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
        int hash = 7;
        hash = 23 * hash + this.state_change_id;
        hash = 23 * hash + Objects.hashCode(this.input);
        hash = 23 * hash + this.priority;
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
        return "State_Change{" + "state_change_id=" + state_change_id + ", input=" + input + ", priority=" + priority + '}';
    }

    
    
}
