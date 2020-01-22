/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 *
 * @author olivier.tatsinkou
 */
@Entity
@Table(name = "state")
public class State implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private int state_id;

    private String content;
    private int bizId;
    private int x;
    private int y;
    private int width;
    private int height;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State_Level state_level;

    @OneToMany(mappedBy = "current_state", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<State_Change> listCurrent_state;

    @OneToMany(mappedBy = "next_state", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<State_Change> listnext_state;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "defaultStateId")
    private State defaultState;

    @OneToMany(mappedBy = "defaultState")
    private Set<State> ListState = new HashSet<State>();

    public int getState_id() {
        return state_id;
    }

    public void setState_id(int state_id) {
        this.state_id = state_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getBizId() {
        return bizId;
    }

    public void setBizId(int bizId) {
        this.bizId = bizId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public State_Level getState_level() {
        return state_level;
    }

    public void setState_level(State_Level state_level) {
        this.state_level = state_level;
    }

    public Collection<State_Change> getListCurrent_state() {
        return listCurrent_state;
    }

    public void setListCurrent_state(Collection<State_Change> listCurrent_state) {
        this.listCurrent_state = listCurrent_state;
    }

    public Collection<State_Change> getListnext_state() {
        return listnext_state;
    }

    public void setListnext_state(Collection<State_Change> listnext_state) {
        this.listnext_state = listnext_state;
    }

    public State getDefaultState() {
        return defaultState;
    }

    public void setDefaultState(State defaultState) {
        this.defaultState = defaultState;
    }

    public Set<State> getListState() {
        return ListState;
    }

    public void setListState(Set<State> ListState) {
        this.ListState = ListState;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.state_id;
        hash = 79 * hash + Objects.hashCode(this.content);
        hash = 79 * hash + this.bizId;
        hash = 79 * hash + this.x;
        hash = 79 * hash + this.y;
        hash = 79 * hash + this.width;
        hash = 79 * hash + this.height;
        hash = 79 * hash + Objects.hashCode(this.state_level);
        hash = 79 * hash + Objects.hashCode(this.defaultState);
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
        final State other = (State) obj;
        if (this.state_id != other.state_id) {
            return false;
        }
        if (this.bizId != other.bizId) {
            return false;
        }
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        if (this.width != other.width) {
            return false;
        }
        if (this.height != other.height) {
            return false;
        }
        if (!Objects.equals(this.content, other.content)) {
            return false;
        }
        if (this.state_level != other.state_level) {
            return false;
        }
        if (!Objects.equals(this.defaultState, other.defaultState)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "State{" + "state_id=" + state_id + ", content=" + content + ", bizId=" + bizId  + ", state_level=" + state_level + '}';
    }
    
    

}
