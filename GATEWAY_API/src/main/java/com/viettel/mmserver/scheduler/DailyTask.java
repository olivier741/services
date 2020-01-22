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
package com.viettel.mmserver.scheduler;

/**
 *
 * @author olivier.tatsinkou
 */
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import org.apache.log4j.Logger;

public class DailyTask
  extends Task
{
  private int recurrenceTime;
  private long recurrencePeriod;
  private static final long MILISECOND_OF_ONE_DAY = 86400000L;
  
  public DailyTask() {}
  
  public DailyTask(int taskId, String strAppId, String strObjectName, String strMethodName, Scheduler.TaskType taskType, Date startTime, Date startDate, Date endDate, int intStatus, String strUserName, int recurrenceTime)
  {
    super(taskId, strAppId, strObjectName, strMethodName, taskType, startTime, startDate, endDate, intStatus, strUserName);
    
    this.recurrenceTime = recurrenceTime;
    this.recurrencePeriod = (recurrenceTime * 86400000L);
  }
  
  public boolean schedule()
  {
    Date now = new Date();
    if (getStartRunTime().getTime() > now.getTime())
    {
      setFirstScheduledRunTime(getStartRunTime());
    }
    else
    {
      Calendar d = Calendar.getInstance();
      d.setTime(getStartRunTime());
      d.add(5, this.recurrenceTime);
      setFirstScheduledRunTime(d.getTime());
    }
    if (getFirstScheduledRunTime().after(this.endDate))
    {
      this.logger.info("Task " + this.taskId + " will not run in " + new SimpleDateFormat("dd/MM/yyyy HH-mm-ss").format(getFirstScheduledRunTime()) + " due to after endDate " + new SimpleDateFormat("dd/MM/yyyy HH-mm-ss").format(this.endDate));
      

      return true;
    }
    this.logger.info("Task " + this.taskId + " will run in " + new SimpleDateFormat("dd/MM/yyyy HH-mm-ss").format(getFirstScheduledRunTime()));
    
    this.timer.scheduleAtFixedRate(this, getFirstScheduledRunTime(), getRecurrencePeriod());
    return true;
  }
  
  public long getRecurrencePeriod()
  {
    return this.recurrencePeriod;
  }
  
  public void setRecurrencePeriod(long period)
  {
    this.recurrencePeriod = period;
  }
  
  public void setRecurrenceTime(int recurrenceTime)
  {
    this.recurrenceTime = recurrenceTime;
  }
  
  public int getRecurrenceTime()
  {
    return this.recurrenceTime;
  }
  
  public DailyTask getSibbling()
  {
    DailyTask sibbling = new DailyTask();
    sibbling.taskId = this.taskId;
    sibbling.strAppId = this.strAppId;
    sibbling.endDate = new Date(this.endDate.getTime());
    sibbling.intStatus = this.intStatus;
    sibbling.recurrencePeriod = this.recurrencePeriod;
    sibbling.recurrenceTime = this.recurrenceTime;
    sibbling.repeatedTask = this.repeatedTask;
    sibbling.startDate = new Date(this.startDate.getTime());
    sibbling.realStartDate = new Date(this.realStartDate.getTime());
    sibbling.startTime = new Date(this.startTime.getTime());
    sibbling.strMethodName = this.strMethodName;
    sibbling.strObjectName = this.strObjectName;
    sibbling.strUserName = this.strUserName;
    sibbling.timer = this.timer;
    sibbling.firstScheduledRunTime = new Date(getFirstScheduledRunTime().getTime());
    sibbling.parameters = this.parameters;
    sibbling.signature = this.signature;
    sibbling.logger = this.logger;
    sibbling.scheduler = this.scheduler;
    sibbling.repeatedTask = this.repeatedTask;
    sibbling.taskType = this.taskType;
    return sibbling;
  }
  
  public Date getNextTimeExecution()
  {
    Calendar nextTimeExecution = Calendar.getInstance();
    nextTimeExecution.setTime(new Date(scheduledExecutionTime()));
    nextTimeExecution.add(5, this.recurrenceTime);
    return nextTimeExecution.getTime();
  }
}
