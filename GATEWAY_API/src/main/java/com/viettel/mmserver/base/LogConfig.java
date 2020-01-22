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
package com.viettel.mmserver.base;

/**
 *
 * @author olivier.tatsinkou
 */
import com.viettel.mmserver.agent.MMbeanServer;
import java.io.IOException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
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