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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class ConfigParam
  implements ConfigParamMBean
{
  private static ConfigParam instance;
  private String appID = "";
  private String department = "";
  
  public static synchronized ConfigParam getInstance()
  {
    try
    {
      if (instance == null) {
        instance = new ConfigParam();
      }
      return instance;
    }
    catch (Exception e)
    {
      throw new RuntimeException("Critical error when init LogConfig!");
    }
  }
  
  public static synchronized ConfigParam getInstance(String appID, String department)
  {
    try
    {
      if (instance == null) {
        instance = new ConfigParam(appID, department);
      }
      return instance;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new RuntimeException("Critical error when init LogConfig!");
    }
  }
  
  public ConfigParam()
    throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException
  {
    MMbeanServer.getInstance().registerMBean(this, new ObjectName("Tools:name=ConfigParams"));
  }
  
  public ConfigParam(String appID, String department)
    throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException
  {
    if (appID != null) {
      this.appID = appID;
    }
    this.department = department;
    try
    {
      MMbeanServer.getInstance().registerMBean(this, new ObjectName("Tools:name=ConfigParams"));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void setAppID(String appID)
  {
    this.appID = appID;
  }
  
  public String getAppID()
  {
    return this.appID;
  }
  
  public String loadAppId()
  {
    return this.appID;
  }
  
  public String getDepartment()
  {
    return this.department;
  }
  
  public void setDepartment(String department)
  {
    this.department = department;
  }
  
  public String loadParams(String strSource)
  {
    if (strSource != null)
    {
      BufferedReader br = null;
      try
      {
        br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(strSource))));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
          sb.append(line + "\n");
        }
        br.close();
        return sb.toString();
      }
      catch (Exception ex)
      {
        String line;
        Log.error(ex);
        return "";
      }
      finally
      {
        try
        {
          br.close();
        }
        catch (IOException ex)
        {
          Log.error(ex);
        }
      }
    }
    return "";
  }
  
  public void saveParams(String strConfig, String strSource)
  {
    if ((strSource != null) && (strConfig != null))
    {
      Writer output = null;
      try
      {
        output = new BufferedWriter(new FileWriter(strSource));
        output.write(strConfig);
      }
      catch (Exception ex)
      {
        Log.error(ex);
      }
      finally
      {
        if (output != null) {
          try
          {
            output.close();
          }
          catch (IOException ex)
          {
            Log.error(ex);
          }
        }
      }
    }
  }
}
