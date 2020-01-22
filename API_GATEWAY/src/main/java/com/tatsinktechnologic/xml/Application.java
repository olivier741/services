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
    
    @Element(name="method",required = true) 
    private String method;
    
    @Element(name="security_mode",required = true) 
    private String security_mode;
    
    @Element(name="tokenSecret",required = true) 
    private String tokenSecret;
    
    @Element(name="tokenDuration",required = true) 
    private int tokenDuration;
    
   
    
    
  
    public Application(@Element(name="url",required = true) String url, 
                        @Element(name="method",required = true) String method,
                        @Element(name="security_mode",required = true) String security_mode,
                        @Element(name="tokenSecret",required = true) String tokenSecret,
                        @Element(name="tokenDuration",required = true) int tokenDuration) {
        
        this.url = url;  
        this.method=method;
        this.security_mode=security_mode;
        this.tokenSecret=tokenSecret;
        this.tokenDuration=tokenDuration;

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getSecurity_mode() {
        return security_mode;
    }

    public void setSecurity_mode(String security_mode) {
        this.security_mode = security_mode;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public int getTokenDuration() {
        return tokenDuration;
    }

    public void setTokenDuration(int tokenDuration) {
        this.tokenDuration = tokenDuration;
    }

    @Override
    public String toString() {
        return "Application{" + "url=" + url + ", method=" + method + ", security_mode=" + security_mode + ", tokenSecret=" + tokenSecret + ", tokenDuration=" + tokenDuration + '}';
    }

   
}
