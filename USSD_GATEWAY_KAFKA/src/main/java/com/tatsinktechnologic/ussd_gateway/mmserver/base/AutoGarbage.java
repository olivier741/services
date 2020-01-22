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

