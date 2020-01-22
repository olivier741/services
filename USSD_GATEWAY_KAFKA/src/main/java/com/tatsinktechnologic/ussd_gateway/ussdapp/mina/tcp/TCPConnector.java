/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.mina.tcp;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.logging.PropertyConfigFile;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.UssdMessage;
import com.tatsinktechnologic.ussd_gateway.ussdapp.ussdgwinterface.Connector;
import com.tatsinktechnologic.ussd_gateway.utils.BlockQueue;
import com.tatsinktechnologic.ussd_gateway.utils.UtilsFunctions;
import java.security.Key;
import javax.crypto.Cipher;
import org.apache.log4j.Logger;


public class TCPConnector
  implements Connector
{
  private int id;
  private String name;
  private Logger logger;
  private Sender sender;
  private Receiver receiver;
  private int bkConId;
  
  public TCPConnector(int id, String name, String configFile)
    throws Exception
  {
    this.logger = Logger.getLogger(name);
    this.id = id;
    this.name = name;
    
    init(configFile);
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
    this.sender = new Sender(Logger.getLogger(this.name + "-Sender"), pIp, pPort, hUser, hPass);
    
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
    this.bkConId = Integer.parseInt(propFile.getParam("bkConId"));
    if (this.bkConId < 0) {
      throw new Exception("bkConId is invalid (require >= 0): " + this.bkConId);
    }
    if (this.bkConId == this.id) {
      throw new Exception("bakConId must difference connector id. Connector: " + this.id);
    }
    this.logger.info("backup connector id: " + this.bkConId);
    if (this.bkConId == 0) {
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
  }
  
  public void send(UssdMessage msg)
    throws Exception
  {
    this.sender.send(msg);
  }
  
  public void setQueue(BlockQueue queue)
  {
    this.receiver.setQueue(queue);
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
    buf.append(this.bkConId);
    buf.append("\r\n");
    return buf.toString();
  }
  
  public boolean haveBkConnector()
  {
    return this.bkConId > 0;
  }
  
  public int getBkConnectorId()
  {
    return this.bkConId;
  }
  
  public int getId()
  {
    return this.id;
  }
}
