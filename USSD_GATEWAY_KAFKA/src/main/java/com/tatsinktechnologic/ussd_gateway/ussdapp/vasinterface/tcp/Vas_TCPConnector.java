/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.vasinterface.tcp;

/**
 *
 * @author olivier.tatsinkou
 */
import com.tatsinktechnologic.ussd_gateway.logging.PropertyConfigFile;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasRequest;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasResponse;
import com.tatsinktechnologic.ussd_gateway.utils.BlockQueue;
import com.tatsinktechnologic.ussd_gateway.utils.UtilsFunctions;
import java.security.Key;
import java.util.Timer;
import javax.crypto.Cipher;
import org.apache.log4j.Logger;

public class Vas_TCPConnector
{
  private int id;
  private String name;
  private Logger logger;
  private Sender sender;
  private Receiver receiver;
  private int bakConId;
  private int numErrToSwitchBak;
  private int numSendErr;
  private int timeToSwitchMainMillis;
  private boolean swBak;
  private Timer timer;
  private SwitchMainTask timerTask;
  
  public Vas_TCPConnector(int id, String name, String configFile)
    throws Exception
  {
    this.logger = Logger.getLogger(name);
    this.id = id;
    this.name = name;
    
    this.swBak = false;
    this.numSendErr = 0;
    
    init(configFile);
    

    this.timer = new Timer();
    this.timerTask = null;
  }
  
  private void init(String cfgFile)
    throws Exception
  {
    PropertyConfigFile propFile = new PropertyConfigFile(cfgFile);
    



    int hPort = Integer.parseInt(propFile.getParam("hport"));
    this.logger.debug("hPort=" + hPort);
    
    int pPort = Integer.parseInt(propFile.getParam("pport"));
    this.logger.debug("pPort=" + pPort);
    
    String pIp = propFile.getParam("pip");
    if (pIp == null) {
      throw new Exception("missing pear Ip (pip)");
    }
    this.logger.debug("pIp=" + pIp);
    
    String pUser = propFile.getParam("puser");
    if (pUser == null) {
      throw new Exception("missing pear user (puser)");
    }
    this.logger.debug("pUser=" + pUser);
    
    String hUser = propFile.getParam("huser");
    if (hUser == null) {
      throw new Exception("missing host User (huser)");
    }
    this.logger.debug("hUser=" + hUser);
    
    String pPass = propFile.getParam("ppass");
    if (pPass == null) {
      throw new Exception("missing pear pass (ppass)");
    }
    this.logger.debug("pPass=******");
    
    String hPass = propFile.getParam("hpass");
    if (hPass == null) {
      throw new Exception("missing host pass (hpass)");
    }
    this.logger.debug("hPass=******");
    

    int idleTime = Integer.parseInt(propFile.getParam("idleTime", "0"));
    this.logger.info("idle time = " + idleTime);
    


    int allMsgEncrypted = Integer.parseInt(propFile.getParam("allMsgEcrypted", "0"));
    this.logger.info("allMsgEncrypted = " + allMsgEncrypted);
    boolean isAllMsgEncrypted = allMsgEncrypted == 1;
    

    this.logger.info("make sender");
    this.sender = new Sender(Logger.getLogger(this.name + "-Sender"), pIp, pPort, hUser, hPass, isAllMsgEncrypted);
    
    this.logger.info("make receiver");
    this.receiver = new Receiver(Logger.getLogger(this.name + "-Receiver"), this.id, pIp, hPort, pUser, pPass, idleTime, isAllMsgEncrypted);
    


    this.logger.info("load secret key if any");
    String keyFile = propFile.getParam("keyFile");
    if (keyFile == null)
    {
      this.logger.warn("keyfile not set");
      if (allMsgEncrypted == 1)
      {
        this.logger.error("All Message encrypted mode but not keyfile not found!");
        throw new Exception("Keyfile must set when allMsgEncrypted turn on ");
      }
    }
    else
    {
      this.logger.info("encryption mode");
      Key secKey = (Key)UtilsFunctions.loadKeyFromJofFile(keyFile);
      Cipher encoder = Cipher.getInstance("AES");
      Cipher decoder = Cipher.getInstance("AES");
      encoder.init(1, secKey);
      decoder.init(2, secKey);
      
      this.sender.setEncoder(encoder);
      this.receiver.setDecoder(decoder);
    }
    this.logger.info("get backup connector id");
    this.bakConId = Integer.parseInt(propFile.getParam("bkConId",String.valueOf(0)));
    if (this.bakConId < 0) {
      throw new Exception("bkConId is invalid (require >= 0): " + this.bakConId);
    }
    if (this.bakConId == this.id) {
      throw new Exception("bakConId must difference connector id. Connector: " + this.id);
    }
    this.logger.info("backup connector id: " + this.bakConId);
    if (this.bakConId != 0)
    {
      this.logger.info("get num errors to switch to backup connector");
      this.numErrToSwitchBak = Integer.parseInt(propFile.getParam("numErrToSwBk",String.valueOf(10)));
      if (this.numErrToSwitchBak <= 0) {
        throw new Exception("numErrToSwBk is invalid (require > 0): " + this.numErrToSwitchBak);
      }
      this.logger.info("num Error to switch backup: " + this.numErrToSwitchBak);
      

      this.logger.info("get time to switch back main connector");
      this.timeToSwitchMainMillis =  Integer.parseInt(propFile.getParam("timeToSwMain",String.valueOf(15))); 
      if (this.timeToSwitchMainMillis <= 0) {
        throw new Exception("timeToSwMain is invalid (require > 0): " + this.timeToSwitchMainMillis);
      }
      this.logger.info("time to switch back main connector (minute): " + this.timeToSwitchMainMillis);
      
      this.timeToSwitchMainMillis *= 60000;
    }
    else
    {
      this.logger.warn("None backup connector for connector: " + this.id);
    }
  }
  
  public void start()
  {
    try
    {
      this.receiver.start();
    }
    catch (Exception ex)
    {
      throw new RuntimeException("Start connector " + this.id + " fail!", ex);
    }
  }
  
  public void stop()
  {
    this.sender.stop();
    this.receiver.stop();
    

    this.timer.cancel();
  }
  
  public synchronized void sendRequest(VasRequest req)
    throws Exception
  {
    if (haveBkConnector())
    {
      try
      {
        this.sender.send(req);
      }
      catch (Exception ex)
      {
        increaseNumSendErr();
        
        throw ex;
      }
      resetNumSendErr();
    }
    else
    {
      this.sender.send(req);
    }
  }
  
  public void sendResponse(VasResponse rsp)
    throws Exception
  {
    this.sender.send(rsp);
  }
  
  public void setQueue(BlockQueue queue)
  {
    this.receiver.setQueue(queue);
  }
  
  public int getId()
  {
    return this.id;
  }
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder("TCP two way Connector: ");
    buf.append(this.name);
    buf.append(" (id = ");
    buf.append(this.id);
    buf.append("):\r\n");
    

    buf.append(this.sender.toString());
    buf.append("\r\n");
    buf.append(this.receiver.toString());
    

    buf.append("\r\nBackup connector id: ");
    buf.append(this.bakConId);
    buf.append("\r\nNum send err to switch to backup: ");
    buf.append(this.numErrToSwitchBak);
    buf.append("\r\nTime to switch to main connector (ms): ");
    buf.append(this.timeToSwitchMainMillis);
    return buf.toString();
  }
  
  public int getBakConId()
  {
    return this.bakConId;
  }
  
  public synchronized boolean isSwBak()
  {
    return this.swBak;
  }
  
  public void setSwBak(boolean swBak)
  {
    this.swBak = swBak;
  }
  
  private void increaseNumSendErr()
  {
    this.numSendErr += 1;
    if (this.numSendErr >= this.numErrToSwitchBak) {
      if (!this.swBak)
      {
        this.swBak = true;
        
        this.timerTask = new SwitchMainTask(this);
        this.timer.schedule(this.timerTask, this.timeToSwitchMainMillis);
      }
    }
  }
  
  private void resetNumSendErr()
  {
    this.numSendErr = 0;
    if (this.swBak)
    {
      this.swBak = false;
      this.timerTask.cancel();
    }
  }
  
  public boolean haveBkConnector()
  {
    return this.bakConId > 0;
  }
  
  public synchronized void swToMainConnection()
  {
    this.logger.info("switch to main connection");
    this.numSendErr = 0;
    this.swBak = false;
  }
}

