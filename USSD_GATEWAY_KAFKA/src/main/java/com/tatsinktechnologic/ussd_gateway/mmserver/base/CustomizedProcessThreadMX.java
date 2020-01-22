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

import com.tatsinktechnologic.ussd_gateway.mmserver.scheduler.DailyTask;
import com.tatsinktechnologic.ussd_gateway.mmserver.scheduler.Scheduler;
import com.tatsinktechnologic.ussd_gateway.mmserver.scheduler.Task;
import java.util.ArrayList;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;

public abstract class CustomizedProcessThreadMX
  extends ProcessThreadMX
{
  Thread processorThread = null;
  
  public CustomizedProcessThreadMX(String threadName)
  {
    super(threadName);
  }
  
  public CustomizedProcessThreadMX(String threadName, String description)
  {
    super(threadName, description);
  }
  
  protected MBeanOperationInfo[] buildOperations()
  {
    MBeanOperationInfo[] mbInfors = super.buildOperations();
    MBeanOperationInfo[] mbNewInfors = new MBeanOperationInfo[mbInfors.length + 2];
    System.arraycopy(mbInfors, 0, mbNewInfors, 0, mbInfors.length);
    MBeanParameterInfo[] params = new MBeanParameterInfo[0];
    mbNewInfors[mbInfors.length] = new MBeanOperationInfo("oneTimeProcess", "process one time", params, "void", 1);
    





    params = new MBeanParameterInfo[0];
    mbNewInfors[(mbInfors.length + 1)] = new MBeanOperationInfo("stopOneTimeProcess", "stop one time process", params, "void", 1);
    




    return mbNewInfors;
  }
  
  public Object invoke(String operationName, Object[] params, String[] signature)
    throws MBeanException, ReflectionException
  {
    if (operationName == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"), "Cannot invoke a null operation in " + this.dClassName);
    }
    if (operationName.equals("oneTimeProcess"))
    {
      processOneTime();
      return null;
    }
    if (operationName.equals("stopOneTimeProcess"))
    {
      stopOneTimeProcess();
      return null;
    }
    return super.invoke(operationName, params, signature);
  }
  
  private void processOneTime()
  {
    if (!this.running)
    {
      this.running = true;
      this.logger.info("starting " + this.threadName + " process...");
      this.processorThread = new Thread(new OneTimeProcess());
      this.processorThread.setPriority(this.priority);
      this.processorThread.setName(this.threadName);
      this.processorThread.start();
      setProcessStatus(0);
      this.logger.info(this.threadName + " process  is started");
    }
    else
    {
      this.logger.info(this.threadName + " process  is started");
    }
  }
  
  private void stopOneTimeProcess()
  {
    if (this.running)
    {
      setProcessStatus(2);
      prepareStop();
      this.logger.info("stop " + this.threadName + " process");
      this.running = false;
      synchronized (this.lock)
      {
        this.lock.notifyAll();
      }
      if (this.processorThread != null) {
        this.processorThread.interrupt();
      }
      try
      {
        if ((this.processorThread != null) && (this.processorThread.isAlive()))
        {
          this.logger.info("waiting " + this.threadName + " process stop...");
          this.processorThread.join();
        }
      }
      catch (InterruptedException ex)
      {
        this.logger.error("stop process exception:" + ex);
      }
      finally
      {
        this.logger.info(this.threadName + " process is stopped");
      }
      setProcessStatus(1);
    }
  }
  
  private class OneTimeProcess   implements Runnable
  {
    private OneTimeProcess() {}
    
    public void run()
    {
      CustomizedProcessThreadMX.this.setProcessStatus(0);
      try
      {
        CustomizedProcessThreadMX.this.process();
        Thread.yield();
      }
      catch (RuntimeException ex)
      {
        ex.printStackTrace();
        CustomizedProcessThreadMX.this.logger.error("catch runtime exception[" + ex.toString() + "]");
      }
      CustomizedProcessThreadMX.this.logger.info(CustomizedProcessThreadMX.this.threadName + " is dead");
      CustomizedProcessThreadMX.this.setProcessStatus(1);
    }
  }
  
  public void start()
  {
    if (checkCoLich()) {
      return;
    }
    super.start();
  }
  
  public boolean checkCoLich()
  {
    ArrayList<Task> taskList = (ArrayList)Scheduler.getInstance("Scheduler").getTaskList();
    for (Task task : taskList)
    {
      task.getStrMethodName().equals("start");
      if (task.getStrObjectName().equals(this.objectName))
      {
        if ((task.getTaskType().equals(Scheduler.TaskType.DAILY)) && 
          (task.getIntStatus() == 1))
        {
          DailyTask dailyTask = (DailyTask)task;
          dailyTask.getStartDate();
          dailyTask.getStartTime();
          dailyTask.getEndDate();
          dailyTask.getRepeatedTask().getRepeatedRecurrencePeriod();
        }
        return true;
      }
    }
    return false;
  }
}
