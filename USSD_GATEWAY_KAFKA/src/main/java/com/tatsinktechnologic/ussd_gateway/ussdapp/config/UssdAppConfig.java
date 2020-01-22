/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.config;

/**
 *
 * @author olivier.tatsinkou
 */
import com.tatsinktechnologic.ussd_gateway.logging.PropertyConfigFile;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.Utils;
import java.security.Key;
import javax.crypto.Cipher;

public class UssdAppConfig
{
  public static final int MAX_THREAD = 150;
  private static final String DEFAULT_MAX_VAS_QUEUE_SIZE = "5000";
  private static final String DEFAULT_MAX_USSD_QUEUE_SIZE = "5000";
  private static final String DEFAULT_MAX_SIZE_VAS_SEND_QUEUE = "10000";
  private static final String DEFAULT_MAX_SIZE_VAS_RECEIVE_QUEUE = "10000";
  private static final String DEFAULT_MAX_SIZE_USSDGW_RECEIVE_QUEUE = "10000";
  private static final String DEFAULT_MAX_SIZE_USSDGW_SEND_QUEUE = "10000";
  private static final String DEFAULT_DB_LOGIN_TIMEOUT = "10";
  private static final String DEFAULT_DB_QUERY_TIMEOUT = "20";
  private PropertyConfigFile propCfgFile;
  
  public UssdAppConfig(String cnfFile)
    throws Exception
  {
    this.propCfgFile = new PropertyConfigFile(cnfFile);
  }
  
  public void loadCnfFile()
    throws Exception
  {
    this.propCfgFile.loadCnfFile();
  }
  
  public String getVasLogConfigFile()
  {
    return this.propCfgFile.getParam("vasLogConfigFile");
  }
  
  public String getVasLogSubPath()
  {
    return this.propCfgFile.getParam("vasLogSubPath");
  }
  
  public String getVasLogName()
  {
    return this.propCfgFile.getParam("vasLogName");
  }
  
  public String getUssdLogConfigFile()
  {
    return this.propCfgFile.getParam("ussdLogConfigFile");
  }
  
  public String getUssdLogSubPath()
  {
    return this.propCfgFile.getParam("ussdLogSubPath");
  }
  
  public String getUssdLogName()
  {
    return this.propCfgFile.getParam("ussdLogName");
  }
  
  public int getVasLogQueueSize()
  {
    return Integer.parseInt(this.propCfgFile.getParam("vasLogQueueSize", "5000"));
  }
  
  public int getUssdLogQueueSize()
  {
    return Integer.parseInt(this.propCfgFile.getParam("ussdLogQueueSize", "5000"));
  }
  
  public int getNumVasLogThread()
  {
    return getNumThreadConfig("numVasLogThread");
  }
  
  public int getNumUssdLogThread()
  {
    return getNumThreadConfig("numUssdLogThread");
  }
  
  public int getSystemId()
  {
    return Integer.parseInt(this.propCfgFile.getParam("sid"));
  }
  
  public Cipher getDblogEncoder()
    throws Exception
  {
    String keyFile = this.propCfgFile.getParam("dblogSecretKey");
    if (keyFile == null) {
      return null;
    }
    Key key = (Key)Utils.loadKeyFromJofFile(keyFile);
    

    Cipher encoder = Cipher.getInstance("AES");
    encoder.init(1, key);
    
    return encoder;
  }
  
  public int getNumUssdgwWorkerThread()
  {
    return getNumThreadConfig("numUssdgwWorkerThread");
  }
  
  public int getNumVasWorkerThread()
  {
    return getNumThreadConfig("numVasWorkerThread");
  }
  
  public int getVasSendQueueSize()
  {
    return Integer.parseInt(this.propCfgFile.getParam("vasSendQueueSize", "10000"));
  }
  
  public int getVasRecvQueueSize()
  {
    return Integer.parseInt(this.propCfgFile.getParam("vasRecvQueueSize", "10000"));
  }
  
  public int getNumVasSendThread()
  {
    return getNumThreadConfig("numVasSendThread");
  }
  
  public int getUssdgwSendQueueSize()
  {
    return Integer.parseInt(this.propCfgFile.getParam("gwSendQueueSize", "10000"));
  }
  
  public int getUssdgwRecvQueueSize()
  {
    return Integer.parseInt(this.propCfgFile.getParam("gwRecvQueueSize", "10000"));
  }
  
  public int getNumGwSendThread()
  {
    return getNumThreadConfig("numUssdgwSendThread");
  }
  
  public String getConnectorCfgFile()
  {
    return this.propCfgFile.getParam("connectorCfgFile");
  }
  
  public String getDBDriver()
  {
    return this.propCfgFile.getParam("dbDriver");
  }
  
  public String getDBEncryptFile()
  {
    return this.propCfgFile.getParam("dbConfig");
  }
  
  public int getDbLoginTimeout()
  {
    return Integer.parseInt(this.propCfgFile.getParam("dbLoginTimeout", "10"));
  }
  
  public int getDbQueryTimeout()
  {
    return Integer.parseInt(this.propCfgFile.getParam("dbQueryTimeout", "20"));
  }
  
  private int getNumThreadConfig(String name)
  {
    String strValue = this.propCfgFile.getParam(name);
    if (strValue == null) {
      return Utils.getNumCoreCPU();
    }
    return Integer.parseInt(strValue);
  }
}