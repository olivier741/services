/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.common;

/**
 *
 * @author olivier.tatsinkou
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyConfigFile
{
  private String cnfFile;
  private Properties cnfParams;
  
  public PropertyConfigFile(String cnfFile)
    throws Exception
  {
    this.cnfFile = cnfFile;
    loadCnfFile();
  }
  
  public synchronized void loadCnfFile()
    throws Exception
  {
    Properties cnfParamsTmp = new Properties();
    FileInputStream propsFile = null;
    try
    {
      propsFile = new FileInputStream(this.cnfFile);
      cnfParamsTmp.load(propsFile);
      
      this.cnfParams = cnfParamsTmp; return;
    }
    finally
    {
      if (propsFile != null) {
        try
        {
          propsFile.close();
        }
        catch (IOException e) {}
      }
    }
  }
  
  public synchronized String getParam(String paramName)
  {
    return this.cnfParams.getProperty(paramName);
  }
  
  public synchronized String getParam(String paramName, String defaultValue)
  {
    return this.cnfParams.getProperty(paramName, defaultValue);
  }
}


