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
@Table(name = "mo_fake")
public class Mo_Fake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private int mo_fake_id;
    
    @Column(nullable = false, unique = true)
    private String command;
    
    private String param;
    
    private String channel;
    
    private int bizid;
    
    private String input;

    public int getMo_fake_id() {
        return mo_fake_id;
    }

    public void setMo_fake_id(int mo_fake_id) {
        this.mo_fake_id = mo_fake_id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getBizid() {
        return bizid;
    }

    public void setBizid(int bizid) {
        this.bizid = bizid;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.mo_fake_id;
        hash = 97 * hash + Objects.hashCode(this.command);
        hash = 97 * hash + Objects.hashCode(this.param);
        hash = 97 * hash + Objects.hashCode(this.channel);
        hash = 97 * hash + this.bizid;
        hash = 97 * hash + Objects.hashCode(this.input);
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
        final Mo_Fake other = (Mo_Fake) obj;
        if (this.mo_fake_id != other.mo_fake_id) {
            return false;
        }
        if (this.bizid != other.bizid) {
            return false;
        }
        if (!Objects.equals(this.command, other.command)) {
            return false;
        }
        if (!Objects.equals(this.param, other.param)) {
            return false;
        }
        if (!Objects.equals(this.channel, other.channel)) {
            return false;
        }
        if (!Objects.equals(this.input, other.input)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Mo_Fake{" + "mo_fake_id=" + mo_fake_id + ", command=" + command + ", param=" + param + ", channel=" + channel + ", bizid=" + bizid + ", input=" + input + '}';
    }

    
}
