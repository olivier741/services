/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author olivier.tatsinkou
 */
@XmlRootElement(name = "UssdMessage")
public class UssdMessage implements Serializable{
    
    private int type;
    private String transId;
    private String msisdn;
    private String imsi;
    private String ussdString;
    private int charSet;
    private transient String loggedString;
    private byte[] encryptedUssdString;
    private int connectorId;
    private String hlrGT;
    private int dlgId;
    private int timeout;
    private long sendRecvTime;

    public UssdMessage() {
    }

    
    public int getType() {
        return type;
    }

    public String getTransId() {
        return transId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getImsi() {
        return imsi;
    }

    public String getUssdString() {
        return ussdString;
    }

    public int getCharSet() {
        return charSet;
    }

    public String getLoggedString() {
        return loggedString;
    }

    public byte[] getEncryptedUssdString() {
        return encryptedUssdString;
    }

    public int getConnectorId() {
        return connectorId;
    }

    public String getHlrGT() {
        return hlrGT;
    }

    public int getDlgId() {
        return dlgId;
    }

    public int getTimeout() {
        return timeout;
    }

    public long getSendRecvTime() {
        return sendRecvTime;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public void setUssdString(String ussdString) {
        this.ussdString = ussdString;
    }

    public void setCharSet(int charSet) {
        this.charSet = charSet;
    }

    public void setLoggedString(String loggedString) {
        this.loggedString = loggedString;
    }

    public void setEncryptedUssdString(byte[] encryptedUssdString) {
        this.encryptedUssdString = encryptedUssdString;
    }

    public void setConnectorId(int connectorId) {
        this.connectorId = connectorId;
    }

    public void setHlrGT(String hlrGT) {
        this.hlrGT = hlrGT;
    }

    public void setDlgId(int dlgId) {
        this.dlgId = dlgId;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setSendRecvTime(long sendRecvTime) {
        this.sendRecvTime = sendRecvTime;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.type;
        hash = 79 * hash + Objects.hashCode(this.transId);
        hash = 79 * hash + Objects.hashCode(this.msisdn);
        hash = 79 * hash + Objects.hashCode(this.imsi);
        hash = 79 * hash + Objects.hashCode(this.ussdString);
        hash = 79 * hash + this.charSet;
        hash = 79 * hash + Objects.hashCode(this.loggedString);
        hash = 79 * hash + Arrays.hashCode(this.encryptedUssdString);
        hash = 79 * hash + this.connectorId;
        hash = 79 * hash + Objects.hashCode(this.hlrGT);
        hash = 79 * hash + this.dlgId;
        hash = 79 * hash + this.timeout;
        hash = 79 * hash + (int) (this.sendRecvTime ^ (this.sendRecvTime >>> 32));
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
        final UssdMessage other = (UssdMessage) obj;
        if (this.type != other.type) {
            return false;
        }
        if (this.charSet != other.charSet) {
            return false;
        }
        if (this.connectorId != other.connectorId) {
            return false;
        }
        if (this.dlgId != other.dlgId) {
            return false;
        }
        if (this.timeout != other.timeout) {
            return false;
        }
        if (this.sendRecvTime != other.sendRecvTime) {
            return false;
        }
        if (!Objects.equals(this.transId, other.transId)) {
            return false;
        }
        if (!Objects.equals(this.msisdn, other.msisdn)) {
            return false;
        }
        if (!Objects.equals(this.imsi, other.imsi)) {
            return false;
        }
        if (!Objects.equals(this.ussdString, other.ussdString)) {
            return false;
        }
        if (!Objects.equals(this.loggedString, other.loggedString)) {
            return false;
        }
        if (!Objects.equals(this.hlrGT, other.hlrGT)) {
            return false;
        }
        if (!Arrays.equals(this.encryptedUssdString, other.encryptedUssdString)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UssdMessage{" + "type=" + type + ",\n transId=" + transId + ",\n msisdn=" + msisdn + ",\n imsi=" + imsi + ",\n ussdString=" + ussdString + ",\n charSet=" + charSet + ",\n loggedString=" + loggedString + ",\n encryptedUssdString=" + encryptedUssdString + ",\n connectorId=" + connectorId + ",\n hlrGT=" + hlrGT + ",\n dlgId=" + dlgId + ",\n timeout=" + timeout + ",\n sendRecvTime=" + sendRecvTime + '}';
    }
    
    
    
}
