/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.common;

/**
 *
 * @author olivier.tatsinkou
 */
import java.io.Serializable;

public class VasRequest
  implements Serializable
{
  private String id;
  private String msisdn;
  private String imsi;
  private int bizId;
  private String params;
  private transient String loggedParams;
  private byte[] encryptedParams;
  private int connectorId;
  private long sendRecvTime;
  private boolean needEncrypt;
  private String transId;
  private volatile boolean valid;
  
  public VasRequest(String id)
  {
    this.id = id;
    this.needEncrypt = false;
    this.valid = true;
  }
  
  public void setBizId(int bizId)
  {
    this.bizId = bizId;
  }
  
  public void setConnectorId(int connectorId)
  {
    this.connectorId = connectorId;
  }
  
  public void setEncryptedParams(byte[] encryptedParams)
  {
    this.encryptedParams = encryptedParams;
  }
  
  public void setImsi(String imsi)
  {
    this.imsi = imsi;
  }
  
  public void setMsisdn(String msisdn)
  {
    this.msisdn = msisdn;
  }
  
  public void setNeedEncrypt(boolean needEncrypt)
  {
    this.needEncrypt = needEncrypt;
  }
  
  public void setParams(String params)
  {
    this.params = params;
    if (params != null) {
      this.loggedParams = params;
    }
  }
  
  public void setSendRecvTime(long sendRecvTime)
  {
    this.sendRecvTime = sendRecvTime;
  }
  
  public int getBizId()
  {
    return this.bizId;
  }
  
  public int getConnectorId()
  {
    return this.connectorId;
  }
  
  public byte[] getEncryptedParams()
  {
    return this.encryptedParams;
  }
  
  public String getId()
  {
    return this.id;
  }
  
  public String getImsi()
  {
    return this.imsi;
  }
  
  public String getMsisdn()
  {
    return this.msisdn;
  }
  
  public boolean isNeedEncrypt()
  {
    return this.needEncrypt;
  }
  
  public String getParams()
  {
    return this.params;
  }
  
  public long getSendRecvTime()
  {
    return this.sendRecvTime;
  }
  
  public VasResponse makeResponse()
  {
    VasResponse rsp = new VasResponse(this.id);
    return rsp;
  }
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    buf.append("id: ");
    buf.append(this.id);
    buf.append("\r\nmsisdn: ");
    buf.append(this.msisdn);
    buf.append("\r\nimsi: ");
    buf.append(this.imsi);
    buf.append("\r\nbizid: ");
    buf.append(this.bizId);
    buf.append("\r\nparams: ");
    if (this.needEncrypt) {
      buf.append("encrypted!");
    } else {
      buf.append(this.params);
    }
    return buf.toString();
  }
  
  public String getLoggedParams()
  {
    return this.loggedParams;
  }
  
  public void setLoggedParams()
  {
    this.loggedParams = this.params;
  }
  
  public String getTransId()
  {
    return this.transId;
  }
  
  public void setTransId(String transId)
  {
    this.transId = transId;
  }
  
  public void setValid(boolean valid)
  {
    this.valid = valid;
  }
  
  public boolean isValid()
  {
    return this.valid;
  }
}