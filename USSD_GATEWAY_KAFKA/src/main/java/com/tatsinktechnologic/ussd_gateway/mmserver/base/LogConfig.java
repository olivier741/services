/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.base;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.mmserver.agent.MMbeanServer;
import java.io.IOException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class LogConfig
  implements LogConfigMBean
{
  private static LogConfig instance;
  private int port;
  private String ip;
  
  public static synchronized LogConfig getInstance()
  {
    try
    {
      if (instance == null) {
        instance = new LogConfig();
      }
      return instance;
    }
    catch (Exception e)
    {
      throw new RuntimeException("Critical error when init LogConfig!");
    }
  }
  
  public static synchronized LogConfig getInstance(String ip, int port)
  {
    try
    {
      if (instance == null) {
        instance = new LogConfig(ip, port);
      }
      return instance;
    }
    catch (Exception e)
    {
      throw new RuntimeException("Critical error when init LogConfig!");
    }
  }
  
  private LogConfig()
    throws IOException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException
  {
    MMbeanServer.getInstance().registerMBean(this, new ObjectName("Tools:name=LoadLogConfig"));
  }
  
  private LogConfig(String ip, int port)
    throws IOException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException
  {
    this.port = port;
    this.ip = ip;
    MMbeanServer.getInstance().registerMBean(this, new ObjectName("Tools:name=LoadLogConfig"));
  }
  
  public String getLoggingIP()
  {
    return this.ip;
  }
  
  public int getLoggingPort()
  {
    return this.port;
  }
  
  public void setLoggingIp(String ip)
  {
    this.ip = ip;
  }
  
  public void setLoggingPort(int port)
  {
    this.port = port;
  }
}

