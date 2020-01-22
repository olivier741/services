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
import org.apache.log4j.Logger;

public class Log
{
  private static Logger internal;
  
  static
  {
    try
    {
      internal = Logger.getLogger("mmserver");
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

