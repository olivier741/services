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

public class UssdMessage
  implements Serializable
{
  public static final int USSDMSG_TYPE_SUB_SEND_REQ = 100;
  public static final int USSDMSG_TYPE_SUB_SEND_RSP = 101;
  public static final int USSDMSG_TYPE_SUB_CANCEL = 102;
  public static final int USSDMSG_TYPE_TRANS_ERR = 104;
  public static final int USSDMSG_TYPE_SUB_RECV_OK = 103;
  public static final int USSDMSG_TYPE_APP_SEND_MENU = 202;
  public static final int USSDMSG_TYPE_APP_SEND_RSP = 203;
  public static final int USSDMSG_TYPE_APP_SEND_NOTIFY_FIRST = 201;
  public static final int USSDMSG_TYPE_APP_CANCEL = 205;
  public static final int USSDMSG_TYPE_APP_CLOSE_TRANS = 206;
  public static final int USSDMSG_TYPE_APP_SEND_REQ_FIRST = 200;
  public static final int USSDMSG_TYPE_APP_SEND_NOTIFY = 204;
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
  
  public void setTimeout(int timeout)
  {
    this.timeout = timeout;
  }
  
  public void setSendRecvTime(long sendRecvTime)
  {
    this.sendRecvTime = sendRecvTime;
  }
  
  public void setConnectorId(int connectorId)
  {
    this.connectorId = connectorId;
  }
  
  public void setHlrGT(String hlrGT)
  {
    this.hlrGT = hlrGT;
  }
  
  public void setImsi(String imsi)
  {
    this.imsi = imsi;
  }
  
  public void setMsisdn(String msisdn)
  {
    this.msisdn = msisdn;
  }
  
  public void setUssdString(String ussdString)
  {
    this.ussdString = ussdString;
    if (ussdString != null) {
      this.loggedString = ussdString;
    }
  }
  
  public void setDlgId(int dlgId)
  {
    this.dlgId = dlgId;
  }
  
  public void setTransId(String transId)
  {
    this.transId = transId;
  }
  
  public void setEncryptedUssdString(byte[] encryptedUssdString)
  {
    this.encryptedUssdString = encryptedUssdString;
  }
  
  public int getConnectorId()
  {
    return this.connectorId;
  }
  
  public String getHlrGT()
  {
    return this.hlrGT;
  }
  
  public long getSendRecvTime()
  {
    return this.sendRecvTime;
  }
  
  public String getImsi()
  {
    return this.imsi;
  }
  
  public String getMsisdn()
  {
    return this.msisdn;
  }
  
  public String getTransId()
  {
    return this.transId;
  }
  
  public int getType()
  {
    return this.type;
  }
  
  public int getTimeout()
  {
    return this.timeout;
  }
  
  public String getUssdString()
  {
    return this.ussdString;
  }
  
  public int getDlgId()
  {
    return this.dlgId;
  }
  
  public byte[] getEncryptedUssdString()
  {
    return this.encryptedUssdString;
  }
  
  public UssdMessage(int type)
  {
    this.type = type;
    
    this.charSet = 1;
  }
  
  public boolean isFirstUssdAppMsg()
  {
    return (this.type == 201) || (this.type == 200);
  }
  
  public boolean isFisrtHlrMsg()
  {
    return this.type == 100;
  }
  
  public boolean transClosedByApp()
  {
    return (this.type == 205) || (this.type == 206) || (this.type == 203);
  }
  
  public boolean transClosedBySub()
  {
    return (this.type == 102) || (this.type == 104) || (this.type == 103);
  }
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder("\r\n--------------------------");
    buf.append("\r\ntype: ");
    buf.append(this.type);
    buf.append("\r\ntransaction id: ");
    buf.append(this.transId);
    buf.append("\r\nussd string: ");
    buf.append(this.ussdString);
    buf.append("\r\ncharset: ");
    buf.append(this.charSet);
    buf.append("\r\nmsisdn: ");
    buf.append(this.msisdn);
    buf.append("\r\nimsi: ");
    buf.append(this.imsi);
    buf.append("\r\ntimeout (s): ");
    buf.append(this.timeout);
    buf.append("\r\n--------------------------");
    
    return buf.toString();
  }
  
  public void setCharSet(int charSet)
  {
    this.charSet = charSet;
  }
  
  public int getCharSet()
  {
    return this.charSet;
  }
  
  public void setUssdString(String ussdString, int charSet)
  {
    this.ussdString = ussdString;
    this.charSet = charSet;
    if (ussdString != null) {
      this.loggedString = ussdString;
    }
  }
  
  public String getLoggedString()
  {
    return this.loggedString;
  }
  
  public void setLoggedString()
  {
    this.loggedString = this.ussdString;
  }
}

