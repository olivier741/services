/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans;

import java.util.Objects;

/**
 *
 * @author olivier
 */
public class UssdMenu {
    
    private String input;
    private String status;
    private String resp;
    private String action;
    private String desc;

    public UssdMenu(String input, String status, String resp, String action, String desc) {
        this.input = input;
        this.status = status;
        this.resp = resp;
        this.action = action;
        this.desc = desc;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResp() {
        return resp;
    }

    public void setResp(String resp) {
        this.resp = resp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.input);
        hash = 47 * hash + Objects.hashCode(this.status);
        hash = 47 * hash + Objects.hashCode(this.resp);
        hash = 47 * hash + Objects.hashCode(this.action);
        hash = 47 * hash + Objects.hashCode(this.desc);
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
        final UssdMenu other = (UssdMenu) obj;
        if (!Objects.equals(this.input, other.input)) {
            return false;
        }
        if (!Objects.equals(this.status, other.status)) {
            return false;
        }
        if (!Objects.equals(this.resp, other.resp)) {
            return false;
        }
        if (!Objects.equals(this.action, other.action)) {
            return false;
        }
        if (!Objects.equals(this.desc, other.desc)) {
            return false;
        }
        return true;
    }
    
    

    @Override
    public String toString() {
        return "UssdMenu{" + "input=" + input + ", status=" + status + ", resp=" + resp + ", action=" + action + ", desc=" + desc + '}';
    }
    
    
}
