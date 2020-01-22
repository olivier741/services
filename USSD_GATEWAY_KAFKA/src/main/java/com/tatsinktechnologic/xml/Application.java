/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name="application")
public class Application {
    
    @Element(name="url",required = true) 
    private String url;
    
    @Element(name="ip_allow",required = true) 
    private String ip_allow;
  
    public Application(@Element(name="url",required = true) String url, 
                        @Element(name="ip_allow",required = true) String ip_allow) {
        
        this.url = url;  
        this.ip_allow=ip_allow;
     

    }

    public String getUrl() {
        return url;
    }

    public String getIp_allow() {
        return ip_allow;
    }

    @Override
    public String toString() {
        return "Application{" + "url=" + url + ", ip_allow=" + ip_allow + '}';
    }

  
   
}
