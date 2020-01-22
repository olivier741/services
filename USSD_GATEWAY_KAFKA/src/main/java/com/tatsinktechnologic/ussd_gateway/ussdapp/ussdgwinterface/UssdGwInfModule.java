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

import com.tatsinktechnologic.ussd_gateway.ussdapp.common.UssdMessage;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.Utils;
import com.tatsinktechnologic.ussd_gateway.ussdapp.config.DBConfig;
import com.tatsinktechnologic.ussd_gateway.ussdapp.config.UssdAppConfig;
import com.tatsinktechnologic.ussd_gateway.ussdapp.dblog.DBLog;
import com.tatsinktechnologic.ussd_gateway.utils.BlockQueue;
import java.lang.management.ManagementFactory;
import javax.management.ObjectName;
import org.apache.log4j.Logger;


public class UssdGwInfModule  implements UssdGwInfModuleMBean
{
  private BlockQueue sendQ;
  private int sendQSize;
  private BlockQueue recvQ;
  private int recvQSize;
  private UssdGwSendThread[] sendThreads;
  private Logger logger;
  private UssdAppConfig config;
  private long numMsgSent;
  private long numMsgDelivered;
  private DBLog dbLogger;
  private UssdGwConnectorMgr connectorMgr;
  private final Object lockNumMsgSent = new Object();
  private final Object lockNumMsgDelivered = new Object();
  private DBConfig dbConfig;
  
  public UssdGwInfModule(UssdAppConfig config, DBConfig dbConfig)
    throws Exception
  {
    this.logger = Logger.getLogger(UssdGwInfModule.class);
    this.config = config;
    this.dbConfig = dbConfig;
    this.logger.info("init object");
    init();
    this.logger.info("regist Mbean");
    try
    {
      ManagementFactory.getPlatformMBeanServer().registerMBean(this, new ObjectName("UssdGwInfModule:name=ussdGwInfModule"));
    }
    catch (Exception ex)
    {
      this.logger.error("regist mbean fail: " + ex.getMessage());
    }
  }
  
  private void init()
    throws Exception
  {
    this.numMsgDelivered = 0L;
    this.numMsgSent = 0L;
    
    this.logger.info("make queue store msg receive from ussdGW");
    this.recvQSize = this.config.getUssdgwRecvQueueSize();
    this.logger.info("queue size = " + this.recvQSize);
    this.recvQ = new BlockQueue(0, this.recvQSize);
   
    this.logger.info("make queue to store msg to send to UssdGW");
    this.sendQSize = this.config.getUssdgwSendQueueSize();
    this.logger.info("queue size = " + this.sendQSize);
    this.sendQ = new BlockQueue(0, this.sendQSize);
    
    this.logger.info("make ussdgw connectors ");
    
    this.connectorMgr = new UssdGwConnectorMgr(this.dbConfig);
    this.connectorMgr.setStoreQueue(this.recvQ);

    this.logger.info("make threads for send");
    int size = this.config.getNumGwSendThread();
    this.logger.info("num thread = " + size);
    this.sendThreads = new UssdGwSendThread[size];
    for (int i = 0; i < size; i++)
    {
      this.sendThreads[i] = new UssdGwSendThread("gwSendThread-" + i);
      initSendThread(this.sendThreads[i]);
    }
  }
  
  public void start()
  {
    this.logger.info("start send threads");
    for (UssdGwSendThread sendThread : this.sendThreads) {
      sendThread.start();
    }
    this.logger.info("start ussdgw connectors");
    this.connectorMgr.start();
  }
  
  public void stop()
  {
    this.logger.info("stop send threads");
    for (UssdGwSendThread sendThread : this.sendThreads) {
      sendThread.stop();
    }
    this.logger.info("stop ussdgw connectors");
    this.connectorMgr.stop();
  }
  
  public void reloadNumSendThread()
    throws Exception
  {
    this.logger.info("reload num send threads");
    this.logger.info("get new number thread");
    this.logger.info("reload config file");
    this.config.loadCnfFile();
    int size = this.config.getNumGwSendThread();
    this.logger.info("new num thread = " + size);
    
    this.logger.info("stop and unregist mbean current threads");
    for (UssdGwSendThread thread : this.sendThreads) {
      thread.destroy();
    }
    this.logger.info("make new threads");
    this.sendThreads = new UssdGwSendThread[size];
    for (int i = 0; i < size; i++)
    {
      this.sendThreads[i] = new UssdGwSendThread("gwSendThread-" + i);
      initSendThread(this.sendThreads[i]);
    }
    this.logger.info("start all threads");
    for (UssdGwSendThread thread : this.sendThreads) {
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
    for (UssdGwSendThread thread : this.sendThreads) {
      thread.destroy();
    }
    this.logger.info("make new threads");
    this.sendThreads = new UssdGwSendThread[numThread];
    for (int i = 0; i < numThread; i++)
    {
      this.sendThreads[i] = new UssdGwSendThread("gwSendThread-" + i);
      initSendThread(this.sendThreads[i]);
    }
    this.logger.info("start all threads");
    for (UssdGwSendThread thread : this.sendThreads) {
      thread.start();
    }
  }
  
  private void initSendThread(UssdGwSendThread thread)
  {
    thread.setConnector(this.connectorMgr);
    thread.setSendQueue(this.sendQ);
    thread.setErrQueue(this.recvQ);
    thread.setUssdGWInfModule(this);
  }
  
  public String viewAllInfo()
  {
    StringBuilder buf = new StringBuilder("UssdGWInterface module info:\r\n");
    buf.append("Num send threads: ");
    buf.append(this.sendThreads.length);
    
    buf.append("\r\n----------------------\r\n");
    buf.append(this.connectorMgr.toString());
    buf.append("\r\n----------------------");
    

    buf.append("\r\nSend Queue: size = ");
    buf.append(this.sendQSize);
    
    buf.append("\r\nReceive Queue: size = ");
    buf.append(this.recvQSize);
    
    return buf.toString();
  }
  
  public String viewLoadInfo()
  {
    StringBuilder buf = new StringBuilder("Load info:\r\n");
    buf.append("Num messages in send queue: ");
    int tmp = this.sendQ.size();
    buf.append(tmp);
    buf.append(" (");
    buf.append(Utils.percent(tmp, this.sendQSize));
    buf.append("%)\r\n");
    
    buf.append("Num messages in receive queue: ");
    tmp = this.recvQ.size();
    buf.append(tmp);
    buf.append(" (");
    buf.append(Utils.percent(tmp, this.recvQSize));
    buf.append("%)\r\n");
    
    buf.append("Total messages sent to ussdGW: ");
    buf.append(getNumMsgSent());
    buf.append("\r\n");
    
    buf.append("Total messages receive from ussdGw: ");
    buf.append(getNumMsgDelivered());
    
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
  
  public long getNumMsgDelivered()
  {
    synchronized (this.lockNumMsgDelivered)
    {
      return this.numMsgDelivered;
    }
  }
  
  public void updateNumMsgDelivered()
  {
    synchronized (this.lockNumMsgDelivered)
    {
      this.numMsgDelivered += 1L;
    }
  }
  
  public void writeLogDb(UssdMessage msg)
  {
    this.dbLogger.logUssdMsg(msg);
  }
  
  public void link2DBLogModule(DBLog logger)
  {
    this.dbLogger = logger;
  }
  
  public void sendToGW(UssdMessage msg)
  {
    try
    {
      this.sendQ.enqueue(msg);
    }
    catch (IndexOutOfBoundsException ex)
    {
      this.logger.error("@ussdGWInfModule.sendQ - Queue store sending ussd message is full");
      throw ex;
    }
  }
  
  public UssdMessage getGWMsg()
  {
    UssdMessage msg = (UssdMessage)this.recvQ.dequeue();
    if (msg == null) {
      return null;
    }
    updateNumMsgDelivered();
    
    this.dbLogger.logUssdMsg(msg);
    return msg;
  }
}