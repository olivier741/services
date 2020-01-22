/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.beans_entity;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author olivier.tatsinkou
 */
@Entity
@Table(name = "state")
public class State {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private int state_id;
    
    private int stateType;
    private String content;
    private int timeout;
    private int cpId;
    private int bizId;
    private int defaultStateId;
    private int charSet;
    private int encrypted;
    private int x;
    private int y;
    private int width;
    private int height;

    public int getState_id() {
        return state_id;
    }

    public void setState_id(int state_id) {
        this.state_id = state_id;
    }

    public int getStateType() {
        return stateType;
    }

    public void setStateType(int stateType) {
        this.stateType = stateType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getCpId() {
        return cpId;
    }

    public void setCpId(int cpId) {
        this.cpId = cpId;
    }

    public int getBizId() {
        return bizId;
    }

    public void setBizId(int bizId) {
        this.bizId = bizId;
    }

    public int getDefaultStateId() {
        return defaultStateId;
    }

    public void setDefaultStateId(int defaultStateId) {
        this.defaultStateId = defaultStateId;
    }

    public int getCharSet() {
        return charSet;
    }

    public void setCharSet(int charSet) {
        this.charSet = charSet;
    }

    public int getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(int encrypted) {
        this.encrypted = encrypted;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.state_id;
        hash = 97 * hash + this.stateType;
        hash = 97 * hash + Objects.hashCode(this.content);
        hash = 97 * hash + this.timeout;
        hash = 97 * hash + this.cpId;
        hash = 97 * hash + this.bizId;
        hash = 97 * hash + this.defaultStateId;
        hash = 97 * hash + this.charSet;
        hash = 97 * hash + this.encrypted;
        hash = 97 * hash + this.x;
        hash = 97 * hash + this.y;
        hash = 97 * hash + this.width;
        hash = 97 * hash + this.height;
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
        if (this.stateType != other.stateType) {
            return false;
        }
        if (this.timeout != other.timeout) {
            return false;
        }
        if (this.cpId != other.cpId) {
            return false;
        }
        if (this.bizId != other.bizId) {
            return false;
        }
        if (this.defaultStateId != other.defaultStateId) {
            return false;
        }
        if (this.charSet != other.charSet) {
            return false;
        }
        if (this.encrypted != other.encrypted) {
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
        return true;
    }

    @Override
    public String toString() {
        return "State{" + "state_id=" + state_id + ", stateType=" + stateType + ", content=" + content + ", timeout=" + timeout + ", cpId=" + cpId + ", bizId=" + bizId + ", defaultStateId=" + defaultStateId + ", charSet=" + charSet + ", encrypted=" + encrypted + ", x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + '}';
    }
    
    
    
    
   
}
