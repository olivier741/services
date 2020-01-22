/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussd;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.ussdapp.config.DBConfig;
import com.tatsinktechnologic.ussd_gateway.ussdapp.config.UssdAppConfig;
import com.tatsinktechnologic.ussd_gateway.ussdapp.dblog.DBLog;
import com.tatsinktechnologic.ussd_gateway.ussdapp.transaction.CoreProcessModule;
import com.tatsinktechnologic.ussd_gateway.ussdapp.ussdgwinterface.UssdGwInfModule;
import com.tatsinktechnologic.ussd_gateway.ussdapp.vasinterface.VasInfModule;
import org.apache.log4j.Logger;

public class UssdApp
{
  private static final String CNF_FILE = "../etc/config.cfg";
  private static UssdApp ussdApp;
  private VasInfModule vasInfModule;
  private CoreProcessModule coreModule;
  private UssdGwInfModule ussdGwInfModule;
  private Logger logger;
  private UssdAppConfig config;
  private DBConfig dbConfig;
  private DBLog dbLog;
  
  public static synchronized UssdApp getInstance()     throws Exception
  {
    if (ussdApp == null) {
      ussdApp = new UssdApp();
    }
    return ussdApp;
  }
  
  private UssdApp() throws Exception
  {
    this.logger = Logger.getLogger("UssdApp");
    init();
  }
  
  private synchronized void init()  throws Exception
  {
    this.logger.info("load config file: ../etc/config.cfg");
    this.config = new UssdAppConfig("../etc/config.cfg");
    this.logger.info("make db config");
    this.dbConfig = new DBConfig(this.config);
    try
    {
      this.logger.info("make modules");
      this.logger.info("init module vas communicator");
      this.vasInfModule = new VasInfModule(this.config, this.dbConfig);
      
      this.logger.info("init module manage dialogues");
      this.coreModule = new CoreProcessModule(this.config, this.dbConfig);
      
      this.logger.info("init module communicate with ussd gw");
      this.ussdGwInfModule = new UssdGwInfModule(this.config, this.dbConfig);
      
      this.logger.info("make module write log DB");
      this.dbLog = new DBLog(this.config);
      
      this.logger.info("link modules together");
      this.logger.info("vas interface --> db log");
      this.vasInfModule.link2DBLogModule(this.dbLog);
      
      this.logger.info("transaction manager --> vas interface");
      this.coreModule.linkToVasInfModule(this.vasInfModule);
      this.logger.info("transaction manager --> gw interface");
      this.coreModule.linkToGwInfModule(this.ussdGwInfModule);
      this.logger.info("transaction manager --> dblog");
      this.coreModule.linkToDbLogModule(this.dbLog);
      
      this.logger.info("ussd gw interface --> db log");
      this.ussdGwInfModule.link2DBLogModule(this.dbLog);
    }
    finally
    {
      this.logger.info("disconnect DB to release resource, because no longer used.");
      this.dbConfig.disconnect();
    }
  }
  
  public void start()
  {
    this.logger.info("Start module vas communicator");
    this.vasInfModule.start();
    
    this.logger.info("Start module transaction mgr");
    this.coreModule.start();
    
    this.logger.info("Start module ussdgw communicate");
    this.ussdGwInfModule.start();
    
    this.logger.info("start module dblog");
    this.dbLog.start();
  }
  
  public void stop()
  {
    this.logger.info("Stop module vas communicator");
    this.vasInfModule.stop();
    
    this.logger.info("Stop module transaction mgr");
    this.coreModule.stop();
    
    this.logger.info("Stop module ussdgw communicate");
    this.ussdGwInfModule.stop();
    
    this.logger.info("stop module dblog");
    this.dbLog.stop();
    
    this.logger.info("release dbconnection");
    this.dbConfig.disconnect();
  }
}

