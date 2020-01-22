/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.gateway.bean;

import java.util.List;
import java.util.Map;

/**
 *
 * @author olivier.tatsinkou
 */
public class Soap_Resp {
    
    
    private String soap_request;
    private String soap_response;
    private int soap_erro_code;
    private Map<String, String> soap_head_request1;
    private Map<String, MyList> soap_head_request2;

    public int getSoap_erro_code() {
        return soap_erro_code;
    }

    public void setSoap_erro_code(int soap_erro_code) {
        this.soap_erro_code = soap_erro_code;
    }

    public Map<String, String> getSoap_head_request1() {
        return soap_head_request1;
    }

    public void setSoap_head_request1(Map<String, String> soap_head_request1) {
        this.soap_head_request1 = soap_head_request1;
    }

    public Map<String, MyList> getSoap_head_request2() {
        return soap_head_request2;
    }

    public void setSoap_head_request2(Map<String, MyList> soap_head_request2) {
        this.soap_head_request2 = soap_head_request2;
    }


    public String getSoap_request() {
        return soap_request;
    }

    public void setSoap_request(String soap_request) {
        this.soap_request = soap_request;
    }

    public String getSoap_response() {
        return soap_response;
    }

    public void setSoap_response(String soap_response) {
        this.soap_response = soap_response;
    }
    
    
}
