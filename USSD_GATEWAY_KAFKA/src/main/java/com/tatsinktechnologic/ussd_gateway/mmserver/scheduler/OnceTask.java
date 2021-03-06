/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.scheduler;

/**
 *
 * @author olivier.tatsinkou
 */
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import org.apache.log4j.Logger;

public class OnceTask
  extends Task
{
  public OnceTask() {}
  
  public OnceTask(int taskId, String strAppId, String strObjectName, String strMethodName, Scheduler.TaskType taskType, Date startTime, Date startDate, Date endDate, int intStatus, String strUserName)
  {
    super(taskId, strAppId, strObjectName, strMethodName, taskType, startTime, startDate, endDate, intStatus, strUserName);
  }
  
  public boolean schedule()
  {
    Date now = new Date();
    Date startRunTime = getStartRunTime();
    if (now.getTime() > startRunTime.getTime())
    {
      this.logger.info("Task " + this.taskId + " is out of Date and not going to be scheduled");
      return false;
    }
    this.logger.info("Task " + this.taskId + " will run in " + new SimpleDateFormat("dd/MM/yyyy HH-mm-ss").format(startRunTime));
    
    this.timer.schedule(this, startRunTime);
    return true;
  }
  
  public long getRecurrencePeriod()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Task getSibbling()
  {
    OnceTask sibbling = new OnceTask();
    sibbling.setTaskId(this.taskId);
    sibbling.strAppId = this.strAppId;
    sibbling.intStatus = this.intStatus;
    sibbling.repeatedTask = null;
    sibbling.startDate = new Date(this.startDate.getTime());
    sibbling.realStartDate = new Date(this.realStartDate.getTime());
    sibbling.startTime = new Date(this.startTime.getTime());
    sibbling.strMethodName = this.strMethodName;
    sibbling.strObjectName = this.strObjectName;
    sibbling.strUserName = this.strUserName;
    sibbling.timer = this.timer;
    sibbling.parameters = this.parameters;
    sibbling.signature = this.signature;
    sibbling.logger = this.logger;
    sibbling.scheduler = this.scheduler;
    sibbling.repeatedTask = this.repeatedTask;
    sibbling.taskType = this.taskType;
    return sibbling;
  }
  
  public boolean isLegible()
  {
    if (this.realStartDate == null) {
      return false;
    }
    if (this.intStatus != 1) {
      return false;
    }
    return (this.strObjectName != null) && (this.strMethodName != null) && (this.startTime != null);
  }
  
  public void setRecurrencePeriod(long period)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Date getNextTimeExecution()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
