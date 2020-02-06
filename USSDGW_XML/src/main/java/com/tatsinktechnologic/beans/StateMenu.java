/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans;

import java.util.Objects;

/**
 *
 * @author olivier.tatsinkou
 */
public class StateMenu {
    
    private String msisdn;
    private String transaction_id;
    private String short_code;
    private String input;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
    

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
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.msisdn);
        hash = 53 * hash + Objects.hashCode(this.transaction_id);
        hash = 53 * hash + Objects.hashCode(this.short_code);
        hash = 53 * hash + Objects.hashCode(this.input);
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
        final StateMenu other = (StateMenu) obj;
        if (!Objects.equals(this.msisdn, other.msisdn)) {
            return false;
        }
        if (!Objects.equals(this.transaction_id, other.transaction_id)) {
            return false;
        }
        if (!Objects.equals(this.short_code, other.short_code)) {
            return false;
        }
        if (!Objects.equals(this.input, other.input)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ShortCode_Msisdn{" + "msisdn=" + msisdn + ", transaction_id=" + transaction_id + ", short_code=" + short_code + ", input=" + input + '}';
    }

}
