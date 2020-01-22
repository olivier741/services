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
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import static scala.reflect.internal.util.NoPosition.line;

public class ConfigParam
  implements ConfigParamMBean
{
  private static ConfigParam instance;
  private String appID = "";
  
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
  
  public static synchronized ConfigParam getInstance(String appID)
  {
    try
    {
      if (instance == null) {
        instance = new ConfigParam(appID);
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
  
  public ConfigParam(String appID)
    throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException
  {
    if (appID != null) {
      this.appID = appID;
    }
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

