/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.agent;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.mmserver.base.CommandInterface;
import com.tatsinktechnologic.ussd_gateway.mmserver.base.ConfigParam;
import com.tatsinktechnologic.ussd_gateway.mmserver.base.Log;
import com.tatsinktechnologic.ussd_gateway.mmserver.base.LogConfig;
import com.tatsinktechnologic.ussd_gateway.mmserver.base.MmLogManager;
import com.tatsinktechnologic.ussd_gateway.mmserver.base.StatusCollector;
import com.tatsinktechnologic.ussd_gateway.mmserver.scheduler.Scheduler;
import com.tatsinktechnologic.ussd_gateway.mmserver.warnning.BlockedThreadDetector;
import com.tatsinktechnologic.ussd_gateway.mmserver.warnning.ErrorDefinitionHandler;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import org.apache.log4j.PropertyConfigurator;

public final class JmxAgent
{
  public static final String PRO_AGENT_PORT = "com.viettel.mmserver.agent.port";
  public static final String PRO_AGENT_IP = "com.viettel.mmserver.agent.ip";
  public static final String PRO_AGENT_AUTHENTICATE = "com.viettel.mmserver.agent.authenticate";
  public static final String LOG4J_LOCATION = "com.viettel.mmserver.log4j.path";
  public static final String PRO_LOG4J_PORT = "com.viettel.mmserver.log4j.port";
  public static final String PRO_APP_ID = "com.viettel.mmserver.appid";
  public static final String USE_SCHEDULER = "com.viettel.mmserver.scheduling";
  public static final String USE_ACTIONLOG = "com.viettel.mmserver.actionLog";
  public static final String DEFAULT_LOG4J_LOCATION = "../etc/log4j.cfg";
  public static final String BLOCKED_DETECT_ENABLE = "com.viettel.mmserver.blockedDetect.enable";
  public static final String DEFAULT_TIMEOUT = "com.viettel.mmserver.blockedDetect.defaultTimeout";
  public static final String DEFAULT_POOLING_PERIOD = "com.viettel.mmserver.blockedDetect.defaultPoolingPeriod";
  public static final String SEND_UNBLOCKED_SMS = "com.viettel.mmserver.blockedDetect.unblockedSMS";
  public static final String SEND_SMS = "com.viettel.mmserver.blockedDetect.defaultBlockedSMS";
  public static final String LOAD_BALANCING_AGENT = "com.viettel.mmserver.loadBalancingAgentClass";
  
  public static void premain(String args)
    throws Exception
  {
    ConfigParam.getInstance(System.getProperty("com.viettel.mmserver.appid"));
    String log4jConfigurationFile = System.getProperty("com.viettel.mmserver.log4j.path");
    if (log4jConfigurationFile != null) {
      PropertyConfigurator.configure(log4jConfigurationFile);
    }
    System.out.println("Starting M&M Server...");
    Log.info("Starting M&M Server...");
    Log.info("Application ID: " + ConfigParam.getInstance().getAppID());
    String scheduling = System.getProperty("com.viettel.mmserver.scheduling");
    if ((scheduling == null) || (scheduling.trim().equals("1"))) {
      Scheduler.getInstance("Scheduler").start();
    }
    String blockedDetectEnable = System.getProperty("com.viettel.mmserver.blockedDetect.enable");
    if ((blockedDetectEnable == null) || (blockedDetectEnable.equals("1"))) {
      BlockedThreadDetector.getInstance().start();
    }
    Log.info("Register Status Collector");
    new StatusCollector();
    




    String loadBalacingAgent = System.getProperty("com.viettel.mmserver.loadBalancingAgentClass");
    try
    {
      if ((loadBalacingAgent != null) && (!loadBalacingAgent.equals("")))
      {
        Class loadBalancingAgentClass = Class.forName(loadBalacingAgent);
        loadBalancingAgentClass.newInstance();
      }
    }
    catch (Exception ex)
    {
      Log.error("Error when load balencing agent " + ex);
    }
    Log.info("Initlizing a ErrorDefinitionHandler");
    ErrorDefinitionHandler.getInstance().start();
    Log.info("Initlized a ErrorDefinitionHandler");
    
    MmLogManager.getInstance();
    CommandInterface.getInstance();
    
    MmJMXServerSec.setPort(Integer.parseInt(System.getProperty("com.viettel.mmserver.agent.port")));
    MmJMXServerSec.setIp(System.getProperty("com.viettel.mmserver.agent.ip"));
    System.out.println("JmxAgent starting with ip: " + MmJMXServerSec.getIp() + " and port: " + MmJMXServerSec.getPort());
    System.out.println("IP: " + InetAddress.getLocalHost().getHostAddress());
    Log.info("JmxAgent starting with ip: " + MmJMXServerSec.getIp() + " and port: " + MmJMXServerSec.getPort());
    Log.info("IP: " + InetAddress.getLocalHost().getHostAddress());
    MmJMXServerSec.getInstance().start();
    Log.info("JmxAgent started with ip: " + MmJMXServerSec.getIp() + " and port: " + MmJMXServerSec.getPort());
    
    LogConfig.getInstance(System.getProperty("com.viettel.mmserver.agent.ip"), Integer.parseInt(System.getProperty("com.viettel.mmserver.log4j.port")));
    Log.info("Log Socket with ip: " + LogConfig.getInstance().getLoggingIP() + " and port: " + LogConfig.getInstance().getLoggingPort());
    
    Log.info("M&M Server started!");
  }
  
  public static void main(String[] args)
  {
    try
    {
      PropertyConfigurator.configure(System.getProperty("com.viettel.mmserver.log4j.path", "../etc/log4j.cfg"));
      Log.info("Starting M&M Server...");
      ConfigParam.getInstance(System.getProperty("com.viettel.mmserver.appid"));
      Log.info("Application ID: " + ConfigParam.getInstance().getAppID());
      Scheduler.getInstance("Scheduler").start();
      Log.info("Scheduler started");
      Log.info("Initlizing a ErrorDefinitionHandler");
      ErrorDefinitionHandler.getInstance().start();
      Log.info("Initlized a ErrorDefinitionHandler");
      MmLogManager.getInstance();
      CommandInterface.getInstance();
      MmJMXServerSec.setPort(Integer.parseInt(System.getProperty("com.viettel.mmserver.agent.port")));
      MmJMXServerSec.setIp(System.getProperty("com.viettel.mmserver.agent.ip"));
      MmJMXServerSec.getInstance().start();
      Log.info("JmxAgent started with ip: " + MmJMXServerSec.getIp() + " and port: " + MmJMXServerSec.getPort());
      LogConfig.getInstance(System.getProperty("com.viettel.mmserver.agent.ip"), Integer.parseInt(System.getProperty("com.viettel.mmserver.log4j.port")));
      
      Log.info("Log Socket with ip: " + LogConfig.getInstance().getLoggingIP() + " and port: " + LogConfig.getInstance().getLoggingPort());
      
      Log.info("M&M Server started!");
      if ((args[0] != null) && (args[0].equals("stop"))) {
        MmJMXServerSec.getInstance().stop();
      }
    }
    catch (MalformedURLException ex)
    {
      ex.printStackTrace();
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }
}

