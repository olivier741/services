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
import java.util.Arrays;
import java.util.Enumeration;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class MmLogManager
  extends StandardMBean
  implements MmLogManagerMBean
{
  private static MmLogManager instance;
  
  public static MmLogManager getInstance()
  {
    try
    {
      if (instance == null) {
        instance = new MmLogManager();
      }
      return instance;
    }
    catch (Exception e)
    {
      Log.error(e);
      throw new RuntimeException("Critical error when init MmLogManager!");
    }
  }
  
  private MmLogManager()
    throws InstanceAlreadyExistsException, MBeanRegistrationException, MalformedObjectNameException, NotCompliantMBeanException
  {
    super(MmLogManagerMBean.class);
    MMbeanServer.getInstance().registerMBean(this, new ObjectName("Tools:name=ManagerLogger"));
  }
  
  public String summary()
  {
    StringBuilder sb = new StringBuilder();
    
    sb.append(getString(Logger.getRootLogger(), "   "));
    
    Enumeration allLoggers = LogManager.getCurrentLoggers();
    while (allLoggers.hasMoreElements())
    {
      Logger logger = (Logger)allLoggers.nextElement();
      sb.append(getString(logger, "   "));
    }
    return sb.toString();
  }
  
  public String getLevel(String loggerName)
  {
    Logger logger = LogManager.exists(loggerName);
    if (logger == null) {
      return "Logger " + loggerName + " not exists";
    }
    return "Logger " + loggerName + " level " + logger.getLevel();
  }
  
  public String setLevel(String loggerName, String level)
  {
    Logger logger = LogManager.exists(loggerName);
    if (logger == null) {
      return "Logger " + loggerName + " not exists";
    }
    logger.setLevel(Level.toLevel(level));
    return "Set " + level + " to " + loggerName + " resulted: " + getLevel(loggerName);
  }
  
  public String getRootLevel(String nothing)
  {
    return "" + LogManager.getRootLogger().getLevel();
  }
  
  public String setRootLevel(String level)
  {
    LogManager.getRootLogger().setLevel(Level.toLevel(level));
    return "Set " + level + " to rootLogger resulted: " + getRootLevel("");
  }
  
  private String getString(Logger logger, String indent)
  {
    StringBuilder sb = new StringBuilder();
    
    sb.append(indent + "+" + logger.getName() + " level=" + logger.getLevel() + "\r\n");
    
    Enumeration<Appender> apps = logger.getAllAppenders();
    while (apps.hasMoreElements()) {
      sb.append(indent + "   ||=>" + ((Appender)apps.nextElement()).getName() + "\r\n");
    }
    sb.append(indent + "\r\n");
    
    return sb.toString();
  }
  
  protected String getDescription(MBeanInfo info)
  {
    return "Manager logger of log4j. Provide get/set/summary by logger name";
  }
  
  protected String getDescription(MBeanOperationInfo op, MBeanParameterInfo param, int sequence)
  {
    if (op.getName().equals("setLevel"))
    {
      switch (sequence)
      {
      case 0: 
        return "Logger name that its level will change";
      case 1: 
        return "Logger level, valid values are: DEBUG, INFO, WARN, ERROR, FATAL. All unknow level will convert to DEBUG";
      }
      return null;
    }
    if (op.getName().equals("setRootLevel"))
    {
      switch (sequence)
      {
      case 0: 
        return "Root level, valid values are: DEBUG, INFO, WARN, ERROR, FATAL. All unknow level will convert to DEBUG";
      }
      return null;
    }
    if (op.getName().equals("getRootLevel"))
    {
      switch (sequence)
      {
      case 0: 
        return "Anything here";
      }
      return null;
    }
    if (op.getName().equals("getLevel"))
    {
      switch (sequence)
      {
      case 0: 
        return "Logger name that its level will get";
      }
      return null;
    }
    return null;
  }
  
  protected String getParameterName(MBeanOperationInfo op, MBeanParameterInfo param, int sequence)
  {
    if (op.getName().equals("setLevel"))
    {
      switch (sequence)
      {
      case 0: 
        return "loggerName";
      case 1: 
        return "level";
      }
      return null;
    }
    if (op.getName().equals("setRootLevel"))
    {
      switch (sequence)
      {
      case 0: 
        return "level";
      }
      return null;
    }
    if (op.getName().equals("getLevel"))
    {
      switch (sequence)
      {
      case 0: 
        return "loggerName";
      }
      return null;
    }
    if (op.getName().equals("getRootLevel"))
    {
      switch (sequence)
      {
      case 0: 
        return "n/a";
      }
      return null;
    }
    return null;
  }
  
  protected String getDescription(MBeanOperationInfo info)
  {
    MBeanParameterInfo[] params = info.getSignature();
    String[] signature = new String[params.length];
    for (int i = 0; i < params.length; i++) {
      signature[i] = params[i].getType();
    }
    String[] methodSignature = { String.class.getName(), String.class.getName() };
    if ((info.getName().equals("setLevel")) && (Arrays.equals(signature, methodSignature))) {
      return "Set level by logger name. Valid levels are: DEBUG, INFO, WARN, ERROR, FATAL, other unknow level will convert to DEBUG";
    }
    methodSignature = new String[] { String.class.getName() };
    if ((info.getName().equals("setRootLevel")) && (Arrays.equals(signature, methodSignature))) {
      return "Set root level. Valid levels are: DEBUG, INFO, WARN, ERROR, FATAL, other unknow level will convert to DEBUG";
    }
    methodSignature = new String[] { String.class.getName() };
    if ((info.getName().equals("getRootLevel")) && (Arrays.equals(signature, methodSignature))) {
      return "Get root level. No parameter need, one here just for by pass attribute";
    }
    methodSignature = new String[] { String.class.getName() };
    if ((info.getName().equals("getLevel")) && (Arrays.equals(signature, methodSignature))) {
      return "Get level by logger name.";
    }
    methodSignature = new String[0];
    if ((info.getName().equals("summary")) && (Arrays.equals(signature, methodSignature))) {
      return "Summary all loggers and their appenders.";
    }
    return null;
  }
  
  public MBeanInfo getMBeanInfo()
  {
    MBeanInfo mbinfo = super.getMBeanInfo();
    return new MBeanInfo(mbinfo.getClassName(), mbinfo.getDescription(), mbinfo.getAttributes(), mbinfo.getConstructors(), mbinfo.getOperations(), getNotificationInfo());
  }
  
  public MBeanNotificationInfo[] getNotificationInfo()
  {
    return new MBeanNotificationInfo[0];
  }
}

