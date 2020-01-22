/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.gateway.bean;

import java.util.Objects;
import javax.xml.soap.SOAPMessage;

/**
 *
 * @author olivier.tatsinkou
 */
public class API_Response {
    
    private WS_Response ws_response;
    private SOAPMessage soapResponse;

    public WS_Response getWs_response() {
        return ws_response;
    }

    public void setWs_response(WS_Response ws_response) {
        this.ws_response = ws_response;
    }

    public SOAPMessage getSoapResponse() {
        return soapResponse;
    }

    public void setSoapResponse(SOAPMessage soapResponse) {
        this.soapResponse = soapResponse;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.ws_response);
        hash = 97 * hash + Objects.hashCode(this.soapResponse);
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
        final API_Response other = (API_Response) obj;
        if (!Objects.equals(this.ws_response, other.ws_response)) {
            return false;
        }
        if (!Objects.equals(this.soapResponse, other.soapResponse)) {
            return false;
        }
        return true;
    }
    
    
    
    
}
