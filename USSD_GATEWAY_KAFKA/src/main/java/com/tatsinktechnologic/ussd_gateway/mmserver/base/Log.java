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
import org.apache.log4j.Logger;

public class Log
{
  private static Logger internal;
  
  static
  {
    try
    {
      internal = null;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static void setLogger(Logger logger)
  {
    internal = logger;
  }
  
  public static void debug(Object message)
  {
    if (internal != null) {
      internal.debug(message);
    }
  }
  
  public static void debug(Object message, Throwable t)
  {
    if (internal != null) {
      internal.debug(message, t);
    }
  }
  
  public static void info(Object message)
  {
    if (internal != null) {
      internal.info(message);
    }
  }
  
  public static void info(Object message, Throwable t)
  {
    if (internal != null) {
      internal.info(message, t);
    }
  }
  
  public static void warn(Object message)
  {
    if (internal != null) {
      internal.warn(message);
    }
  }
  
  public static void warn(Object message, Throwable t)
  {
    if (internal != null) {
      internal.warn(message, t);
    }
  }
  
  public static void error(Object message)
  {
    if (internal != null) {
      internal.error(message);
    }
  }
  
  public static void error(Object message, Throwable t)
  {
    if (internal != null) {
      internal.error(message, t);
    }
  }
  
  public static void fatal(Object message)
  {
    if (internal != null) {
      internal.fatal(message);
    }
  }
  
  public static void fatal(Object message, Throwable t)
  {
    if (internal != null) {
      internal.fatal(message, t);
    }
  }
}
