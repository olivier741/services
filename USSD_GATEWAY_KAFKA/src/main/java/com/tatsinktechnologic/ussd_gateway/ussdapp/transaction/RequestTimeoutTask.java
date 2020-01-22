/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.transaction;

/**
 *
 * @author olivier.tatsinkou
 */
import java.util.TimerTask;

public class RequestTimeoutTask
  extends TimerTask
{
  private String requestId;
  private CoreProcessModule coreModule;
  
  public RequestTimeoutTask(String requestId, CoreProcessModule coreModule)
  {
    this.requestId = requestId;
    this.coreModule = coreModule;
  }
  
  public void run()
  {
    Transaction trans = this.coreModule.getWaitedTrans(this.requestId);
    if (trans != null)
    {
      trans.processVasTimeout();
      
      this.coreModule.pushWaitedVasReqToLog(this.requestId, "Timeout");
    }
  }
}