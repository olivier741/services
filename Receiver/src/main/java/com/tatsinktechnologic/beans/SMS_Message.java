/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans;

import java.io.Serializable;

/**
 *
 * @author olivier.tatsinkou
 */
public class SMS_Message implements Serializable {
    
    private static final long serialVersionUID = 8217287396365894807L;
    private String user;
    private String password;
    private String content;
    private String receiver;
    private String sender;

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
    

    public String getContent() {
        return content;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setContent(String content) {
        this.content = content;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "SMS_Message{" + "user=" + user + ", password=" + password + ", content=" + content + ", receiver=" + receiver + ", sender=" + sender + '}';
    }
        
}
