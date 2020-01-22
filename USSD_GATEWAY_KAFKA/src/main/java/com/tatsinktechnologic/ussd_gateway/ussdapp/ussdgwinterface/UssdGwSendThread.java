/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.ussdgwinterface;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.mmserver.base.ProcessThreadMX;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.UssdMessage;
import com.tatsinktechnologic.ussd_gateway.utils.BlockQueue;

public class UssdGwSendThread  extends ProcessThreadMX
{
  private BlockQueue sendQueue;
  private BlockQueue errQueue;
  private UssdGwConnectorMgr connectorMgr;
  private UssdGwInfModule ussdGWInfModule;
  
  public UssdGwSendThread(String threadName)
  {
    super(threadName, "thread for send message to ussdgw");
    try
    {
      registerAgent("UssdGwInfModule:type=SendThread,name=" + threadName);
    }
    catch (Exception ex)
    {
      this.logger.error("regist mbean fail: " + ex.getMessage());
    }
  }
  
  public void setConnector(UssdGwConnectorMgr connectorMgr)
  {
    this.connectorMgr = connectorMgr;
  }
  
  public void setSendQueue(BlockQueue sendQueue)
  {
    this.sendQueue = sendQueue;
  }
  
  public void setUssdGWInfModule(UssdGwInfModule ussdGWInfModule)
  {
    this.ussdGWInfModule = ussdGWInfModule;
  }
  
  public void setErrQueue(BlockQueue errQueue)
  {
    this.errQueue = errQueue;
  }
  
  public void process() {
    this.logger.info("receiving message to send to UssdGW ...");
    UssdMessage msg = (UssdMessage)this.sendQueue.dequeue();
    if (msg == null)
    {
      this.logger.info("no message to received");
      return;
    }
    this.logger.info("received a ussd message:" + msg);
    try
    {
      this.logger.info("get ussdgw connector to send");
      Connector connector = this.connectorMgr.getConnector(msg.getConnectorId());
      if (connector == null) {
        throw new Exception("No connector to send. Maybe missing connector id in message");
      }
      this.logger.info("send message by connector " + msg.getConnectorId());
      connector.send(msg);
      

      msg.setSendRecvTime(System.currentTimeMillis());
      this.ussdGWInfModule.writeLogDb(msg);
      
      this.ussdGWInfModule.updateNumMsgSent();
    }
    catch (Exception ex)
    {
      this.logger.error("send to gateway fail: " + ex.getMessage());
      
      notifyErr2Transaction(msg, "can't send to UssdGW");
    }
    this.logger.info("send complete");
  }
  
  private void notifyErr2Transaction(UssdMessage msg, String reason)
  {
    if (!msg.transClosedByApp())
    {
      UssdMessage errMsg = new UssdMessage(104);
      errMsg.setTransId(msg.getTransId());
      errMsg.setUssdString(reason);
      try
      {
        this.errQueue.enqueue(errMsg);
      }
      catch (IndexOutOfBoundsException ex)
      {
        this.logger.error("@ussdGWInfModule.recvQ - Queue store messages received from gw is full");
      }
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

