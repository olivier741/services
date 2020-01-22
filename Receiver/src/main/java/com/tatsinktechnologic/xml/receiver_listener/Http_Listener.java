/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml.receiver_listener;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name="http_listerner")
public class Http_Listener {
     @Element(name="http_IP",required = true) 
    private String http_IP;
     
    @Element(name="http_port",required = true) 
    private int http_port;
    
    @Element(name="ws_security_mode",required = true) 
    private int ws_security_mode;
    
    public Http_Listener(@Element(name="http_IP",required = true)  String http_IP,
                         @Element(name="http_port",required = true)  int http_port,
                         @Element(name="ws_security_mode",required = true)  int ws_security_mode) {
        this.http_IP=http_IP;
        this.http_port = http_port;
        this.ws_security_mode=ws_security_mode;
       
    }

    public String getHttp_IP() {
        return http_IP;
    }

    public int getHttp_port() {
        return http_port;
    }

    public int getWs_security_mode() {
        return ws_security_mode;
    }

    public void setHttp_IP(String http_IP) {
        this.http_IP = http_IP;
    }

    public void setHttp_port(int http_port) {
        this.http_port = http_port;
    }

    public void setWs_security_mode(int ws_security_mode) {
        this.ws_security_mode = ws_security_mode;
    }

    @Override
    public String toString() {
        return "Http_Listener{" + "http_IP=" + http_IP + ", http_port=" + http_port + ", ws_security_mode=" + ws_security_mode + '}';
    }

}
