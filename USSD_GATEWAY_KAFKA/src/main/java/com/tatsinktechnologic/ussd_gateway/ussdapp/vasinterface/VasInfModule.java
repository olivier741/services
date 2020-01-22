/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.vasinterface;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.ussdapp.common.Utils;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasRequest;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasResponse;
import com.tatsinktechnologic.ussd_gateway.ussdapp.config.DBConfig;
import com.tatsinktechnologic.ussd_gateway.ussdapp.config.UssdAppConfig;
import com.tatsinktechnologic.ussd_gateway.ussdapp.dblog.DBLog;
import com.tatsinktechnologic.ussd_gateway.utils.BlockQueue;
import java.lang.management.ManagementFactory;
import javax.management.ObjectName;
import org.apache.log4j.Logger;

public class VasInfModule   implements VasInfModuleMBean
{
  private VasConnectorMgr connectorMgr;
  private BlockQueue sendQ;
  private int sendQSize;
  private BlockQueue concentrateRecvQ;
  private int concentRecvQSize;
  private VasSendThread[] sendThreads;
  private Logger logger;
  private UssdAppConfig config;
  private DBConfig dbConfig;
  private long numMsgSent;
  private long numMsgReceived;
  private final Object lockNumMsgSent = new Object();
  private final Object lockNumMsgReceived = new Object();
  private DBLog dbLogger;
  
  public VasInfModule(UssdAppConfig config, DBConfig dbConfig)  throws Exception
  {
    this.logger = Logger.getLogger(VasInfModule.class);
    this.config = config;
    this.dbConfig = dbConfig;
    this.logger.info("init object");
    init();
    this.logger.info("regist Mbean");
    try
    {
      ManagementFactory.getPlatformMBeanServer().registerMBean(this, new ObjectName("VasInfModule:name=vasInfModule"));
    } catch (Exception ex)
    {
      this.logger.error("regist mbean fail: " + ex.getMessage());
    }
  }
  
  private void init() throws Exception
  {
    this.numMsgReceived = 0L;
    this.numMsgSent = 0L;
    
    this.logger.info("make queue store msg to send to VAS");
    this.sendQSize = this.config.getVasSendQueueSize();
    this.logger.info("queue size = " + this.sendQSize);
    this.sendQ = new BlockQueue(0, this.sendQSize);
    
    this.logger.info("make queue to store msg received from VAS");
    this.concentRecvQSize = this.config.getVasRecvQueueSize();
    this.logger.info("queue size = " + this.concentRecvQSize);
    this.concentrateRecvQ = new BlockQueue(0, this.concentRecvQSize);
    
    this.logger.info("make connector manager");
    this.connectorMgr = new VasConnectorMgr(this.dbConfig);
    this.connectorMgr.setStoreQueue(this.concentrateRecvQ);
    
    this.logger.info("make threads for send");
    int size = this.config.getNumVasSendThread();
    this.logger.info("num thread = " + size);
    this.sendThreads = new VasSendThread[size];
    
    for (int i = 0; i < size; i++)
    {
      this.sendThreads[i] = new VasSendThread("vasSendThread-" + i);
      this.sendThreads[i].setConnectorMgr(this.connectorMgr);
      this.sendThreads[i].setSendQueue(this.sendQ);
      this.sendThreads[i].setErrQueue(this.concentrateRecvQ);
      this.sendThreads[i].setVasInfModule(this);
    }
  }
  
  public void sendToVas(VasRequest msg)
  {
    try
    {
      this.sendQ.enqueue(msg);
    }
    catch (IndexOutOfBoundsException ex)
    {
      this.logger.error("@vasinf.sendqueue - Send queue is full!");
      throw ex;
    }
  }
  
  public Object getVasRsp()
  {
    Object msg = this.concentrateRecvQ.dequeue();
    if (msg == null) {
      return null;
    }
    if ((msg instanceof VasResponse))
    {
      updateNumMsgReceived();
      
      this.dbLogger.logVasRsp((VasResponse)msg);
    }
    return msg;
  }
  
  public void start()
  {
    this.logger.info("start send threads");
    for (VasSendThread sendThread : this.sendThreads) {
      sendThread.start();
    }
    this.logger.info("start connectors");
    this.connectorMgr.start();
  }
  
  public void stop()
  {
    this.logger.info("stop send threads");
    for (VasSendThread sendThread : this.sendThreads) {
      sendThread.stop();
    }
    this.logger.info("stop connectors");
    this.connectorMgr.stop();
  }
  
  public void reloadNumSendThread()
    throws Exception
  {
    this.logger.info("reload num send threads");
    this.logger.info("get new number thread");
    this.logger.info("reload config file");
    this.config.loadCnfFile();
    int size = this.config.getNumVasSendThread();
    this.logger.info("new num thread = " + size);
    
    this.logger.info("stop and unregist mbean current threads");
    for (VasSendThread thread : this.sendThreads) {
      thread.destroy();
    }
    this.logger.info("make new threads");
    this.sendThreads = new VasSendThread[size];
    for (int i = 0; i < size; i++)
    {
      this.sendThreads[i] = new VasSendThread("vasSendThread-" + i);
      this.sendThreads[i].setConnectorMgr(this.connectorMgr);
      this.sendThreads[i].setSendQueue(this.sendQ);
      this.sendThreads[i].setErrQueue(this.concentrateRecvQ);
      this.sendThreads[i].setVasInfModule(this);
    }
    this.logger.info("start all threads");
    for (VasSendThread thread : this.sendThreads) {
      thread.start();
    }
  }
  
  public void reloadNumSendThread(int numThread)
    throws Exception
  {
    if ((numThread < 1) || (numThread > 150)) {
      throw new Exception("number must in rang [1, 150]");
    }
    this.logger.info("reload num send threads");
    this.logger.info("new amount: " + numThread);
    this.logger.info("stop and unregist mbean current threads");
    for (VasSendThread thread : this.sendThreads) {
      thread.destroy();
    }
    this.logger.info("make new threads");
    this.sendThreads = new VasSendThread[numThread];
    for (int i = 0; i < numThread; i++)
    {
      this.sendThreads[i] = new VasSendThread("vasSendThread-" + i);
      this.sendThreads[i].setConnectorMgr(this.connectorMgr);
      this.sendThreads[i].setSendQueue(this.sendQ);
      this.sendThreads[i].setErrQueue(this.concentrateRecvQ);
      this.sendThreads[i].setVasInfModule(this);
    }
    this.logger.info("start all threads");
    for (VasSendThread thread : this.sendThreads) {
      thread.start();
    }
  }
  
  public String viewAllInfo()
  {
    StringBuilder buf = new StringBuilder("VASInterface module info:\r\n");
    buf.append("Num send threads: ");
    buf.append(this.sendThreads.length);
    buf.append("\r\n");
    

    buf.append("--------------------------\r\n");
    buf.append(this.connectorMgr.toString());
    buf.append("--------------------------\r\n");
    
    buf.append("Send Queue: size = ");
    buf.append(this.sendQSize);
    buf.append("\r\n");
    
    buf.append("Receive Queue: size = ");
    buf.append(this.concentRecvQSize);
    
    return buf.toString();
  }
  
  public String viewLoadInfo()
  {
    StringBuilder buf = new StringBuilder("Load info:\r\n");
    buf.append("Num request in send queue: ");
    int tmp = this.sendQ.size();
    buf.append(tmp);
    buf.append(" (");
    buf.append(Utils.percent(tmp, this.sendQSize));
    buf.append("%)\r\n");
    
    buf.append("Num response in receive queue: ");
    tmp = this.concentrateRecvQ.size();
    buf.append(tmp);
    buf.append(" (");
    buf.append(Utils.percent(tmp, this.concentRecvQSize));
    buf.append("%)\r\n");
    
    buf.append("Total request sent to vas: ");
    buf.append(getNumMsgSent());
    buf.append("\r\n");
    
    buf.append("Total response received from vas: ");
    buf.append(getNumMsgReceived());
    
    return buf.toString();
  }
  
  public void updateNumMsgSent()
  {
    synchronized (this.lockNumMsgSent)
    {
      this.numMsgSent += 1L;
    }
  }
  
  private long getNumMsgSent()
  {
    synchronized (this.lockNumMsgSent)
    {
      return this.numMsgSent;
    }
  }
  
  public void updateNumMsgReceived()
  {
    synchronized (this.lockNumMsgReceived)
    {
      this.numMsgReceived += 1L;
    }
  }
  
  private long getNumMsgReceived()
  {
    synchronized (this.lockNumMsgReceived)
    {
      return this.numMsgReceived;
    }
  }
  
  public void writeLogVasReq(VasRequest req)
  {
    this.dbLogger.logVasReq(req);
  }
  
  public void writeLogVasRsp(VasResponse rsp)
  {
    this.dbLogger.logVasRsp(rsp);
  }
  
  public void link2DBLogModule(DBLog logger)
  {
    this.dbLogger = logger;
  }
  
  public void reloadConnectorRules()
    throws Exception
  {
    this.connectorMgr.reloadConnectorRules();
  }
  
  public void removeWaitReq(String reqId)
  {
    this.dbLogger.removeWaitReq(reqId);
  }
}
