/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.vasinterface;

import com.tatsinktechnologic.ussd_gateway.mmserver.base.ProcessThreadMX;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasRequest;
import com.tatsinktechnologic.ussd_gateway.ussdapp.vasinterface.tcp.Vas_TCPConnector;
import com.tatsinktechnologic.ussd_gateway.utils.BlockQueue;


/**
 *
 * @author olivier.tatsinkou
 */



public class VasSendThread   extends ProcessThreadMX
{
  private BlockQueue sendQueue;
  private BlockQueue errQueue;
  private VasConnectorMgr connectorMgr;
  private VasInfModule vasInfModule;
  
  public VasSendThread(String threadName)
  {
    super(threadName, "thread for send message to vas");
    try
    {
      registerAgent("VasInfModule:type=SendThread,name=" + threadName);
    }
    catch (Exception ex)
    {
      this.logger.error("regist mbean fail: " + ex.getMessage());
    }
  }
  
  public void setConnectorMgr(VasConnectorMgr connectorMgr)
  {
    this.connectorMgr = connectorMgr;
  }
  
  public void setSendQueue(BlockQueue sendQueue)
  {
    this.sendQueue = sendQueue;
  }
  
  public void setErrQueue(BlockQueue errQueue)
  {
    this.errQueue = errQueue;
  }
  
  public void setVasInfModule(VasInfModule vasInfModule)
  {
    this.vasInfModule = vasInfModule;
  }
  
  public void process()
  {
    this.logger.info("receiving message to send to VAS ...");
    VasRequest req = (VasRequest)this.sendQueue.dequeue();
    if (req == null)
    {
      this.logger.info("no message to received");
      return;
    }
    this.logger.info("received a vas request:" + req);
    if (!req.isValid())
    {
      this.logger.info("request have been invalid. Abort!");
      return;
    }
    this.logger.info("get connector for process");
    

    Vas_TCPConnector connector = this.connectorMgr.getConnector(req);
    if (connector == null)
    {
      this.logger.error("Connector not found");
      
      giveBackToSender(req);
      return;
    }
    this.vasInfModule.writeLogVasReq(req);
    
    req.setSendRecvTime(System.currentTimeMillis());
    boolean sendOk = true;
    

    String paramBackup = req.getParams();
    boolean needEncryptedBackup = req.isNeedEncrypt();
    if (connector.haveBkConnector())
    {
      this.logger.info("have switch to backup connector. Get backup connector");
      Vas_TCPConnector bkConnector = this.connectorMgr.getConnector(connector.getBakConId());
      if (bkConnector == null)
      {
        this.logger.error("No backup connector with id " + connector.getBakConId() + ". Comeback main connector");
        try
        {
          req.setConnectorId(connector.getId());
          connector.sendRequest(req);
        }
        catch (Exception ex2)
        {
          this.logger.error("Send request by main connector fail: " + ex2.getMessage());
          sendOk = false;
        }
      }
      else
      {
        this.logger.info("have backup connector id " + bkConnector.getId() + ". Send request by it.");
        try
        {
          req.setConnectorId(bkConnector.getId());
          
          bkConnector.sendRequest(req);
        }
        catch (Exception ex)
        {
          this.logger.error("Send request by backup connector err: " + ex.getMessage() + ". Retry by main connector");
          try
          {
            req.setParams(paramBackup);
            req.setNeedEncrypt(needEncryptedBackup);
            req.setEncryptedParams(null);
            
            req.setConnectorId(connector.getId());
            connector.sendRequest(req);
            
            this.logger.info("send by main connector OK. Switch to main connector");
          }
          catch (Exception ex2)
          {
            this.logger.error("Send by main connector also error: " + ex2.getMessage());
            sendOk = false;
          }
        }
      }
    }
    else
    {
      try
      {
        req.setConnectorId(connector.getId());
        connector.sendRequest(req);
      }
      catch (Exception ex2)
      {
        this.logger.error("Send request fail: " + ex2.getMessage() + ". Retry by backup connector");
        if (connector.haveBkConnector())
        {
          Vas_TCPConnector bkConnector = this.connectorMgr.getConnector(connector.getBakConId());
          if (bkConnector == null)
          {
            this.logger.error("Backup connector id is invalid: " + bkConnector.getId());
            sendOk = false;
          }
          else
          {
            try
            {
              req.setParams(paramBackup);
              req.setNeedEncrypt(needEncryptedBackup);
              req.setEncryptedParams(null);
              
              req.setConnectorId(bkConnector.getId());
              bkConnector.sendRequest(req);
            }
            catch (Exception ex)
            {
              this.logger.error("Send by backup connector fail: " + ex.getMessage());
              sendOk = false;
            }
          }
        }
        else
        {
          this.logger.warn("No backup connector for connector " + connector.getId());
          sendOk = false;
        }
      }
    }
    if (sendOk)
    {
      this.vasInfModule.updateNumMsgSent();
    }
    else
    {
      giveBackToSender(req);
      
      this.vasInfModule.removeWaitReq(req.getId());
    }
    this.logger.info("send complete");
  }
  
  private void giveBackToSender(VasRequest req)
  {
    try
    {
      this.errQueue.enqueue(req);
    }
    catch (IndexOutOfBoundsException e)
    {
      this.logger.error("@vasinf.recvqueue - Queue is full");
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
