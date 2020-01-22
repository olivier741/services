/*
 * Copyright 2019 olivier.tatsinkou.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.viettel.mmserver.agent;

/**
 *
 * @author olivier.tatsinkou
 */
import com.viettel.mmserver.base.CommandInterface;
import com.viettel.mmserver.base.ConfigParam;
import com.viettel.mmserver.base.Log;
import com.viettel.mmserver.base.LogConfig;
import com.viettel.mmserver.base.MmLogManager;
import com.viettel.mmserver.base.StatusCollector;
import com.viettel.mmserver.log.appender.RegisterHostHandler;
import com.viettel.mmserver.scheduler.Scheduler;
import com.viettel.mmserver.warnning.BlockedThreadDetector;
import com.viettel.mmserver.warnning.ErrorDefinitionHandler;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import org.apache.log4j.PropertyConfigurator;

public final class JmxAgent
{
  public static final String PRO_AGENT_PORT = "com.viettel.mmserver.agent.port";
  public static final String PRO_AGENT_IP = "com.viettel.mmserver.agent.ip";
  public static final String PUBLIC_IP = "java.rmi.server.hostname";
  public static final String PRO_AGENT_AUTHENTICATE = "com.viettel.mmserver.agent.authenticate";
  public static final String LOG4J_LOCATION = "com.viettel.mmserver.log4j.path";
  public static final String PRO_LOG4J_PORT = "com.viettel.mmserver.log4j.port";
  public static final String PRO_APP_ID = "com.viettel.mmserver.appid";
  public static final String PRO_DEPARTMENT = "com.viettel.mmserver.department";
  public static final String USE_SCHEDULER = "com.viettel.mmserver.scheduling";
  public static final String USE_ACTIONLOG = "com.viettel.mmserver.actionLog";
  public static final String DEFAULT_LOG4J_LOCATION = "../etc/log4j.cfg";
  public static final String BLOCKED_DETECT_ENABLE = "com.viettel.mmserver.blockedDetect.enable";
  public static final String DEFAULT_TIMEOUT = "com.viettel.mmserver.blockedDetect.defaultTimeout";
  public static final String DEFAULT_POOLING_PERIOD = "com.viettel.mmserver.blockedDetect.defaultPoolingPeriod";
  public static final String SEND_UNBLOCKED_SMS = "com.viettel.mmserver.blockedDetect.unblockedSMS";
  public static final String SEND_SMS = "com.viettel.mmserver.blockedDetect.defaultBlockedSMS";
  public static final String LOAD_BALANCING_AGENT = "com.viettel.mmserver.loadBalancingAgentClass";
  private static String ip = null;
  private static String publicIp = null;
  
  public static synchronized String getIp()
  {
    if (ip == null)
    {
      StringBuilder ipStringBuilder = new StringBuilder();
      Enumeration<NetworkInterface> interfaces = null;
      try
      {
        interfaces = NetworkInterface.getNetworkInterfaces();
      }
      catch (SocketException e)
      {
        Log.info("Can not get server's IP address ", e);
      }
      if (interfaces != null)
      {
        while (interfaces.hasMoreElements())
        {
          NetworkInterface i = (NetworkInterface)interfaces.nextElement();
          
          Enumeration<InetAddress> addresses = i.getInetAddresses();
          while (addresses.hasMoreElements())
          {
            InetAddress address = (InetAddress)addresses.nextElement();
            if ((!address.isLoopbackAddress()) && (address.isSiteLocalAddress()))
            {
              ipStringBuilder.append(address.getHostAddress());
              ipStringBuilder.append("/");
            }
          }
        }
        if (ipStringBuilder.length() > 0)
        {
          ipStringBuilder.deleteCharAt(ipStringBuilder.length() - 1);
          ip = ipStringBuilder.toString();
        }
        else
        {
          ip = MmJMXServerSec.getIp();
        }
      }
      else
      {
        ip = MmJMXServerSec.getIp();
      }
    }
    return ip;
  }
  
  public static void premain(String args)
    throws Exception
  {
    ConfigParam.getInstance(System.getProperty("com.viettel.mmserver.appid"), System.getProperty("com.viettel.mmserver.department"));
    String log4jConfigurationFile = System.getProperty("com.viettel.mmserver.log4j.path");
    if (log4jConfigurationFile != null) {
      PropertyConfigurator.configure(log4jConfigurationFile);
    }
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
    

    Log.info("Initlizing a RegisterHostHandler");
    RegisterHostHandler.getInstance().start();
    Log.info("Initlized a RegisterHostHandler");
    

    MmLogManager.getInstance();
    CommandInterface.getInstance();
    
    MmJMXServerSec.setPort(Integer.parseInt(System.getProperty("com.viettel.mmserver.agent.port")));
    MmJMXServerSec.setIp(System.getProperty("com.viettel.mmserver.agent.ip"));
    

    Log.info("JmxAgent starting with ip: " + MmJMXServerSec.getIp() + " and port: " + MmJMXServerSec.getPort());
    
    MmJMXServerSec.getInstance().start();
    Log.info("JmxAgent started with ip: " + MmJMXServerSec.getIp() + " and port: " + MmJMXServerSec.getPort());
    
    publicIp = System.getProperty("java.rmi.server.hostname");
    if (publicIp == null) {
      LogConfig.getInstance(System.getProperty("com.viettel.mmserver.agent.ip"), Integer.parseInt(System.getProperty("com.viettel.mmserver.log4j.port")));
    } else {
      LogConfig.getInstance(publicIp, Integer.parseInt(System.getProperty("com.viettel.mmserver.log4j.port")));
    }
  }
  
  public static void main(String[] args)
  {
    try
    {
      PropertyConfigurator.configure(System.getProperty("com.viettel.mmserver.log4j.path", "../etc/log4j.cfg"));
      Log.info("Starting M&M Server...");
      ConfigParam.getInstance(System.getProperty("com.viettel.mmserver.appid"), System.getProperty("com.viettel.mmserver.department"));
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

