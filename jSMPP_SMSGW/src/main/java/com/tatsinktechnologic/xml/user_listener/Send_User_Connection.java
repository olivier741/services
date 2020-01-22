/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml.user_listener;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name="user_connection")
public class Send_User_Connection {
   
    @Attribute(name="user",required = true) 
    private String user;
    
    @Attribute(name="password",required = true) 
    private String password;

    @Element(name="client_IP" ,required = true)
    private String client_IP ;

    public Send_User_Connection( @Attribute(name="user",required = true) String user,
                            @Attribute(name="password",required = true) String password,
                            @Element(name="client_IP" ,required = true)String client_IP) {
        this.user = user;
        this.password = password;       
        this.client_IP = client_IP;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getClient_IP() {
        return client_IP;
    }

    @Override
    public String toString() {
        return "User_Connection{" + "user=" + user + ", password=" + password + ", client_IP=" + client_IP + '}';
    }

        
}
