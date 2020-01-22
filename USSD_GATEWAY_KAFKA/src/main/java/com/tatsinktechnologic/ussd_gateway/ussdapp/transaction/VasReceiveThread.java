/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.transaction;

import com.tatsinktechnologic.ussd_gateway.mmserver.base.ProcessThreadMX;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasRequest;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasResponse;

/**
 *
 * @author olivier.tatsinkou
 */

public class VasReceiveThread
  extends ProcessThreadMX
{
  private CoreProcessModule coreModule;
  
  public VasReceiveThread(String name)
  {
    super(name, "thread for process vas messages");
    try
    {
      registerAgent("CoreModule:type=vasReceiveThread,name=" + name);
    }
    catch (Exception ex)
    {
      this.logger.error("regist mbean fail: " + ex.getMessage());
    }
  }
  
  public void setCoreModule(CoreProcessModule coreModule)
  {
    this.coreModule = coreModule;
  }
  
  public void process()
  {
    this.logger.info("receive vas message ...");
    Object msg = this.coreModule.getRspFromVas();
    if (msg == null)
    {
      this.logger.info("no message to receive");
      return;
    }
    if ((msg instanceof VasRequest))
    {
      this.logger.info("message is vas request which send error.");
      
      this.logger.info("get transaction");
      VasRequest req = (VasRequest)msg;
      Transaction tran = this.coreModule.getWaitedTrans(req.getId());
      if (tran == null)
      {
        this.logger.warn("transaction is closed before. Maybe sub cancel");
        return;
      }
      this.logger.info("give message to transaction process");
      tran.processErrSendVasReq();
    }
    else
    {
      this.logger.info("message is vas response. get transaction");
      VasResponse rsp = (VasResponse)msg;
      Transaction tran = this.coreModule.getWaitedTrans(rsp.getId());
      if (tran == null)
      {
        this.logger.warn("transaction is closed before. Maybe sub cancel");
        return;
      }
      this.logger.info("give message to transaction process");
      tran.processVasRsp(rsp);
    }
  }
  
  public void destroy()
  {
    stop();
    try
    {
      unregisterAgent();
    }
    catch (Exception ex)
    {
      this.logger.error("unregist mbean fail: " + ex.getMessage(), ex);
    }
  }
}

