/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.warnning;

/**
 *
 * @author olivier.tatsinkou
 */
public class TimeoutInfo
{
  private int timeout = 0;
  private boolean sendSMS = false;
  
  public TimeoutInfo(int timeout, boolean sendSMS)
  {
    this.timeout = timeout;
    this.sendSMS = sendSMS;
  }
  
  public boolean isSendSMS()
  {
    return this.sendSMS;
  }
  
  public void setSendSMS(boolean sendSMS)
  {
    this.sendSMS = sendSMS;
  }
  
  public int getTimeout()
  {
    return this.timeout;
  }
  
  public void setTimeout(int timeout)
  {
    this.timeout = timeout;
  }
}

