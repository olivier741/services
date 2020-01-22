package com.viettel.bccsgw.utils;

import java.io.PrintStream;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

public class Test
  extends ProcessThreadMX
{
  public Test(String threadName)
    throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException
  {
    super(threadName);
    registerAgent("test:type=Test");
  }
  
  public static void main(String[] args)
    throws ConnectionException, Exception
  {
    Test t = new Test("test");
    t.start();
    for (;;)
    {
      Thread.sleep(5000L);
    }
  }
  
  protected void process()
  {
    System.out.println("running ....");
    synchronized (this.lock)
    {
      try
      {
        this.lock.wait(20000L);
      }
      catch (InterruptedException ex) {}
    }
  }
}

