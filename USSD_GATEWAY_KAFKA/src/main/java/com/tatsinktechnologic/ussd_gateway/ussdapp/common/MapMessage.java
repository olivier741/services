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
public class MapMessage
{
  public static final int ISO_8859_1 = 1;
  public static final int UNICODE = 2;
  private int type;
  private int dlgId;
  private int invokeId;
  private String params;
  private byte[] appContext;
  private String ussdString;
  private byte[] encode;
  private int charSet;
  private String msisdn;
  private String imsi;
  private int mapInst;
  private int userReason;
  private int releaseMethod;
  private int proReason;
  private int proNotice;
  private int proErr;
  private int userErr;
  private int openResult;
  private int refuseReason;
  private String sccpSrcAddr;
  private String sccpDstAddr;
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
  
  public String getImsi()
  {
    return this.imsi;
  }
  
  public String getParams()
  {
    return this.params;
  }
  
  public String getSccpDstAddr()
  {
    return this.sccpDstAddr;
  }
  
  public String getSccpSrcAddr()
  {
    return this.sccpSrcAddr;
  }
  
  public long getSendRecvTime()
  {
    return this.sendRecvTime;
  }
  
  public int getReleaseMethod()
  {
    return this.releaseMethod;
  }
  
  public void setReleaseMethod(int releaseMethod)
  {
    this.releaseMethod = releaseMethod;
  }
  
  public int getProErr()
  {
    return this.proErr;
  }
  
  public int getInvokeId()
  {
    return this.invokeId;
  }
  
  public int getOpenResult()
  {
    return this.openResult;
  }
  
  public int getRefuseReason()
  {
    return this.refuseReason;
  }
  
  public int getUserErr()
  {
    return this.userErr;
  }
  
  public synchronized String getUssdString()
  {
    if (this.ussdString != null) {
      return this.ussdString;
    }
    if (this.encode == null) {
      return null;
    }
    try
    {
      if (this.charSet == 2) {
        this.ussdString = new String(this.encode, "UTF-16BE");
      } else {
        this.ussdString = new String(this.encode, "ISO-8859-1");
      }
    }
    catch (Exception ex)
    {
      this.ussdString = null;
    }
    return this.ussdString;
  }
  
  public int getProNotice()
  {
    return this.proNotice;
  }
  
  public int getProReason()
  {
    return this.proReason;
  }
  
  public int getUserReason()
  {
    return this.userReason;
  }
  
  public void setUssdString(String ussdString)
  {
    this.ussdString = ussdString;
    try
    {
      if (ussdString != null) {
        this.encode = ussdString.getBytes("ISO-8859-1");
      } else {
        this.encode = null;
      }
    }
    catch (Exception ex)
    {
      this.encode = null;
    }
    this.charSet = 1;
  }
  
  public int getDlgId()
  {
    return this.dlgId;
  }
  
  public int getMapInst()
  {
    return this.mapInst;
  }
  
  public int getType()
  {
    return this.type;
  }
  
  public void setType(int type)
  {
    this.type = type;
  }
  
  public byte[] getAppContext()
  {
    return this.appContext;
  }
  
  public void setAppContext(byte[] appContext)
  {
    this.appContext = appContext;
  }
  
  public void setInvokeId(int invokeID)
  {
    this.invokeId = invokeID;
  }
  
  public String getMsisdn()
  {
    return this.msisdn;
  }
  
  public void setMsisdn(String msisdn)
  {
    this.msisdn = msisdn;
  }
  
  public void setImsi(String imsi)
  {
    this.imsi = imsi;
  }
  
  public void setDlgId(int dlgId)
  {
    this.dlgId = dlgId;
  }
  
  public void setMapInst(int mapInst)
  {
    this.mapInst = mapInst;
  }
  
  public void setOpenResult(int openResult)
  {
    this.openResult = openResult;
  }
  
  public void setRefuseReason(int refuseReason)
  {
    this.refuseReason = refuseReason;
  }
  
  public void setSccpDstAddr(String sccpDstAddr)
  {
    this.sccpDstAddr = sccpDstAddr;
  }
  
  public void setSccpSrcAddr(String sccpSrcAddr)
  {
    this.sccpSrcAddr = sccpSrcAddr;
  }
  
  public void setUserErr(int userErr)
  {
    this.userErr = userErr;
  }
  
  public void setUserReason(int userReason)
  {
    this.userReason = userReason;
  }
  
  public void setProReason(int proReason)
  {
    this.proReason = proReason;
  }
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder(200);
    
    buf.append("\r\n--------------------------------------------------------\r\n");
    buf.append("Message Type: ");
    buf.append(MapDefine.getMapTypeName(this.type));
    buf.append("\r\n");
    
    buf.append("Dialogue ID: ");
    buf.append(this.dlgId);
    buf.append("\r\n");
    
    buf.append("Map instance: ");
    buf.append(this.mapInst);
    buf.append("\r\n");
    switch (this.type)
    {
    case 1: 
    case 2: 
      buf.append("MSISDN = ");
      buf.append(this.msisdn);
      buf.append("\r\n");
      
      buf.append("IMSI = ");
      buf.append(this.imsi);
      buf.append("\r\n");
      
      buf.append("GT source = ");
      buf.append(this.sccpSrcAddr);
      buf.append("\r\n");
      
      buf.append("GT dest = ");
      buf.append(this.sccpDstAddr);
      buf.append("\r\n");
      break;
    case 129: 
    case 130: 
      buf.append("open result = ");
      buf.append(this.openResult);
      buf.append("\r\n");
      
      buf.append("open refuse reason = ");
      buf.append(this.refuseReason);
      buf.append("\r\n");
      break;
    case 7: 
    case 8: 
      buf.append("u_abort reason = ");
      buf.append(MapDefine.printUAbortReason(this.userReason));
      buf.append("\r\n");
      break;
    case 9: 
      buf.append("p_abort reason = ");
      buf.append(MapDefine.printPAbortReason(this.proReason));
      buf.append("\r\n");
      break;
    case 10: 
      buf.append("provider notice reason = ");
      buf.append(MapDefine.printPNoticeReason(this.proNotice));
      buf.append("\r\n");
      break;
    case 3: 
    case 4: 
      buf.append("close method = ");
      buf.append(this.releaseMethod);
      buf.append("\r\n");
      break;
    case 23: 
    case 24: 
    case 25: 
    case 26: 
    case 27: 
    case 28: 
      buf.append("Invoke ID: ");
      buf.append(this.invokeId);
      buf.append("\r\n");
      
      buf.append("USSD = ");
      
      buf.append(getUssdString());
      buf.append("\r\n");
      
      buf.append("charset = ");
      buf.append(this.charSet);
      buf.append("\r\n");
      
      buf.append("timeout(s) = ");
      buf.append(this.timeout);
      buf.append("\r\n");
      break;
    case 145: 
    case 146: 
    case 147: 
    case 148: 
    case 149: 
    case 150: 
      buf.append("Invoke ID: ");
      buf.append(this.invokeId);
      buf.append("\r\n");
      
      buf.append("USSD = ");
      
      buf.append(getUssdString());
      buf.append("\r\n");
      
      buf.append("charset = ");
      buf.append(this.charSet);
      buf.append("\r\n");
      
      buf.append("provider error = ");
      buf.append(this.proErr);
      buf.append("\r\n");
      
      buf.append("user error = ");
      buf.append(this.userErr);
      buf.append("\r\n");
    }
    buf.append("params: ");
    buf.append(this.params);
    buf.append("\r\n");
    
    buf.append("--------------------------------------------------------");
    
    return buf.toString();
  }
  
  public boolean isSubEnd()
  {
    if ((this.type == 8) || (this.type == 9) || (this.type == 4)) {
      return true;
    }
    return false;
  }
  
  public boolean isAppEnd()
  {
    if ((this.type == 7) || (this.type == 3)) {
      return true;
    }
    return false;
  }
  
  public boolean isSubBegin()
  {
    return this.type == 2;
  }
  
  public void setUssdString(String ussdString, int charSet)
  {
    this.ussdString = ussdString;
    
    this.charSet = 1;
    try
    {
      if (ussdString != null)
      {
        if (charSet == 2)
        {
          this.encode = ussdString.getBytes("UTF-16BE");
          this.charSet = 2;
        }
        else
        {
          this.encode = ussdString.getBytes("ISO-8859-1");
        }
      }
      else {
        this.encode = null;
      }
    }
    catch (Exception ex)
    {
      this.encode = null;
    }
  }
}
