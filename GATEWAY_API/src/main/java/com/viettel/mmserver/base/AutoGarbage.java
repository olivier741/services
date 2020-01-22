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
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

public final class AutoGarbage
  extends ProcessThreadMX
{
  private static AutoGarbage instance;
  private static final int AUTO_GARBAGE_TIME = 3600000;
  
  public static AutoGarbage getInstance()
    throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException
  {
    if (instance == null) {
      try
      {
        instance = new AutoGarbage("AutoGarbage");
      }
      catch (NotCompliantMBeanException e)
      {
        throw new RuntimeException("Autogarbage singleton instance error.", e);
      }
    }
    return instance;
  }
  
  private AutoGarbage(String threadName)
    throws NotCompliantMBeanException, MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException
  {
    super(threadName);
    registerAgent(threadName);
  }
  
  protected void process()
  {
    try
    {
      Thread.sleep(3600000L);
      Log.info("[REQUEST SYSTEM AUTO GARBAGE]");
      System.gc();
    }
    catch (InterruptedException ex)
    {
      Log.error(ex);
    }
  }
}