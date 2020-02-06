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
    
    private String service;
    private String input;
    private String status;
    private String resp;
    private String action;
    private String desc;
    private String topic;

    public UssdMenu(String service, String input, String status, String resp, String action, String desc, String topic) {
        this.service = service;
        this.input = input;
        this.status = status;
        this.resp = resp;
        this.action = action;
        this.desc = desc;
        this.topic = topic;
    }
    
    

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.service);
        hash = 79 * hash + Objects.hashCode(this.input);
        hash = 79 * hash + Objects.hashCode(this.status);
        hash = 79 * hash + Objects.hashCode(this.resp);
        hash = 79 * hash + Objects.hashCode(this.action);
        hash = 79 * hash + Objects.hashCode(this.desc);
        hash = 79 * hash + Objects.hashCode(this.topic);
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
        if (!Objects.equals(this.service, other.service)) {
            return false;
        }
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
        if (!Objects.equals(this.topic, other.topic)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UssdMenu{" + "service=" + service + ", input=" + input + ", status=" + status + ", resp=" + resp + ", action=" + action + ", desc=" + desc + ", topic=" + topic + '}';
    }

  
   
}
