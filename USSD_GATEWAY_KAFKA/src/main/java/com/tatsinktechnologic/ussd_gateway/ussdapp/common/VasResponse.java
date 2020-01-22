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

public class VasResponse
  implements Serializable
{
  private String id;
  private String content;
  private transient String loggedContent;
  private byte[] encryptedContent;
  private boolean needEncrypt;
  private long sendRecvTime;
  private int connectorId;
  
  public VasResponse(String id)
  {
    this.id = id;
    this.needEncrypt = false;
  }
  
  public String getContent()
  {
    return this.content;
  }
  
  public byte[] getEncryptedContent()
  {
    return this.encryptedContent;
  }
  
  public String getId()
  {
    return this.id;
  }
  
  public boolean isNeedEncrypt()
  {
    return this.needEncrypt;
  }
  
  public int getConnectorId()
  {
    return this.connectorId;
  }
  
  public long getSendRecvTime()
  {
    return this.sendRecvTime;
  }
  
  public void setContent(String content)
  {
    this.content = content;
    if (content != null) {
      this.loggedContent = content;
    }
  }
  
  public void setEncryptedContent(byte[] encryptedContent)
  {
    this.encryptedContent = encryptedContent;
  }
  
  public void setNeedEncrypt(boolean needEncrypt)
  {
    this.needEncrypt = needEncrypt;
  }
  
  public void setSendRecvTime(long sendRecvTime)
  {
    this.sendRecvTime = sendRecvTime;
  }
  
  public void setConnectorId(int connectorId)
  {
    this.connectorId = connectorId;
  }
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    buf.append("id: ");
    buf.append(this.id);
    buf.append("\r\ncontent: ");
    if (this.needEncrypt) {
      buf.append("encrypted!");
    } else {
      buf.append(this.content);
    }
    return buf.toString();
  }
  
  public String getLoggedContent()
  {
    return this.loggedContent;
  }
  
  public void setLoggedContent()
  {
    this.loggedContent = this.content;
  }
}

