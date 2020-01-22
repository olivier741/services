/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans;

/**
 *
 * @author olivier.tatsinkou
 */
public class ShortCode_Msisdn {
    
    private String msisdn;
    private String transaction_id;
    private String short_code;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getShort_code() {
        return short_code;
    }

    public void setShort_code(String short_code) {
        this.short_code = short_code;
    }

    @Override
    public String toString() {
        return "ShortCode_Msisdn{" + "msisdn=" + msisdn + ", transaction_id=" + transaction_id + ", short_code=" + short_code + '}';
    }
    
    
}
