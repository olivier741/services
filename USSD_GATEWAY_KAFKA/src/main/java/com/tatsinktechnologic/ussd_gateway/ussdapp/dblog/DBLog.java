/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.dblog;

/**
 *
 * @author olivier.tatsinkou
 */
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.UssdMessage;
import com.tatsinktechnologic.ussd_gateway.logging.LogWriter;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.PairVasReqRsp;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.Utils;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasRequest;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasResponse;
import com.tatsinktechnologic.ussd_gateway.ussdapp.config.UssdAppConfig;
import com.tatsinktechnologic.ussd_gateway.utils.BlockQueue;
import java.lang.management.ManagementFactory;
import java.util.Hashtable;
import javax.crypto.Cipher;
import javax.management.ObjectName;
import org.apache.log4j.Logger;


public class DBLog
  implements DBLogMBean
{
  private LogWriter vasLogger;
  private LogWriter ussdLogger;
  private UssdAppConfig config;
  private BlockQueue vasQueue;
  private int vasQueueSize;
  private BlockQueue ussdQueue;
  private int ussdQueueSize;
  private VasLogWriterThread[] vasLogThreads;
  private UssdGwLogWriterThread[] ussdGwLogThreads;
  private Logger logger;
  private Hashtable<String, VasRequest> vasReqList = new Hashtable();
  private Cipher encoder;
  private int sid;
  
  public DBLog(UssdAppConfig configer)
    throws Exception
  {
    this.logger = Logger.getLogger(DBLog.class);
    
    this.config = configer;
    this.encoder = this.config.getDblogEncoder();
    
    this.logger.info("init dblog module");
    init();
    this.logger.info("regist MBean");
    try
    {
      ManagementFactory.getPlatformMBeanServer().registerMBean(this, new ObjectName("DBLog:name=dbLogModule"));
    }
    catch (Exception ex)
    {
      this.logger.error("regist mbean fail: " + ex.getMessage());
    }
  }
  
  private void init()
  {
    this.logger.info("make vas logwriter");
    String configFile = this.config.getVasLogConfigFile();
    this.logger.debug("config file: " + configFile);
    String subPath = this.config.getVasLogSubPath();
    this.logger.debug("subpath: " + subPath);
    String name = this.config.getVasLogName();
    this.logger.debug("name: " + name);
    this.vasLogger = new LogWriter(configFile, subPath, name);
    
    this.logger.info("make ussd logwriter");
    configFile = this.config.getUssdLogConfigFile();
    this.logger.debug("config file: " + configFile);
    subPath = this.config.getUssdLogSubPath();
    this.logger.debug("subpath: " + subPath);
    name = this.config.getUssdLogName();
    this.logger.debug("name: " + name);
    this.ussdLogger = new LogWriter(configFile, subPath, name);
    

    this.vasQueueSize = this.config.getVasLogQueueSize();
    this.vasQueue = new BlockQueue(0, this.vasQueueSize);
    this.logger.debug("vas queue size = " + this.vasQueueSize);
    
    this.ussdQueueSize = this.config.getUssdLogQueueSize();
    this.ussdQueue = new BlockQueue(0, this.ussdQueueSize);
    this.logger.debug("ussd queue size = " + this.ussdQueueSize);
    

    this.sid = this.config.getSystemId();
    this.logger.debug("sid = " + this.sid);
    


    this.logger.info("make threads for write log (vas message) communicate with CP");
    int numThread = this.config.getNumVasLogThread();
    this.logger.debug("num thread = " + numThread);
    this.vasLogThreads = new VasLogWriterThread[numThread];
    for (int i = 0; i < numThread; i++)
    {
      this.vasLogThreads[i] = new VasLogWriterThread("vasLog-" + i, this.vasLogger, this);
      this.vasLogThreads[i].setVasQueue(this.vasQueue);
      this.vasLogThreads[i].setSid(this.sid);
    }
    this.logger.info("make threads for write log (ussd message) communicate with ussd gateway");
    numThread = this.config.getNumUssdLogThread();
    this.logger.debug("num thread = " + numThread);
    this.ussdGwLogThreads = new UssdGwLogWriterThread[numThread];
    for (int i = 0; i < numThread; i++)
    {
      this.ussdGwLogThreads[i] = new UssdGwLogWriterThread("gwLog-" + i, this.ussdLogger, this);
      this.ussdGwLogThreads[i].setUssdQueue(this.ussdQueue);
      this.ussdGwLogThreads[i].setSid(this.sid);
    }
  }
  
  public void logUssdMsg(UssdMessage msg)
  {
    try
    {
      this.ussdQueue.enqueue(msg);
    }
    catch (IndexOutOfBoundsException ex)
    {
      this.logger.error("@dblog.ussdqueue - Queue write log ussd message is full");
    }
  }
  
  public void logVasReq(VasRequest msg)
  {
    this.vasReqList.put(msg.getId(), msg);
  }
  
  public void logVasRsp(VasResponse msg)
  {
    String rspId = msg.getId();
    VasRequest req = (VasRequest)this.vasReqList.remove(rspId);
    if (req != null)
    {
      PairVasReqRsp logObj = new PairVasReqRsp(req, msg);
      try
      {
        this.vasQueue.enqueue(logObj);
      }
      catch (IndexOutOfBoundsException ex)
      {
        this.logger.error("@dblog.VASqueue - Queue write log VAS message is full");
      }
    }
    else
    {
      this.logger.warn("received a response, but no waited request!");
    }
  }
  
  public void start()
  {
    this.logger.info("start logwriters");
    this.ussdLogger.start();
    this.vasLogger.start();
    
    this.logger.info("start thread write log vas");
    for (VasLogWriterThread thread : this.vasLogThreads) {
      thread.start();
    }
    this.logger.info("start thread write log ussdGW");
    for (UssdGwLogWriterThread thread : this.ussdGwLogThreads) {
      thread.start();
    }
  }
  
  public void stop()
  {
    this.logger.info("stop thread write log vas");
    for (VasLogWriterThread thread : this.vasLogThreads) {
      thread.stop();
    }
    this.logger.info("stop thread write log ussdGW");
    for (UssdGwLogWriterThread thread : this.ussdGwLogThreads) {
      thread.stop();
    }
    this.logger.info("stop logwriters");
    this.ussdLogger.stop();
    this.vasLogger.stop();
  }
  
  public String viewQueueInfo()
  {
    StringBuffer buf = new StringBuffer("VAS messages in queue: ");
    buf.append(this.vasQueue.size());
    buf.append("\r\nQueue size: ");
    buf.append(this.vasQueueSize);
    buf.append("\r\n");
    buf.append(this.vasLogger.getLoadInfo());
    
    buf.append("\r\n\r\nUssd messages in queue: ");
    buf.append(this.ussdQueue.size());
    buf.append("\r\nQueue size: ");
    buf.append(this.ussdQueueSize);
    buf.append("\r\n");
    buf.append(this.ussdLogger.getLoadInfo());
    
    buf.append("\r\n\r\nSystem Id: ");
    buf.append(this.sid);
    
    buf.append("\r\nNum waited Vas request: ");
    buf.append(this.vasReqList.size());
    
    return buf.toString();
  }
  
  public synchronized String encryptString(byte[] data)
  {
    try
    {
      byte[] encrypted = this.encoder.doFinal(data);
      return Utils.toHexString(encrypted);
    }
    catch (Exception e)
    {
      this.logger.error("encrypt error: " + e.getMessage());
    }
    return null;
  }
  
  public void removeWaitReq(String reqId)
  {
    this.vasReqList.remove(reqId);
  }
  
  public boolean isEncryptMode()
  {
    return this.encoder != null;
  }
}
