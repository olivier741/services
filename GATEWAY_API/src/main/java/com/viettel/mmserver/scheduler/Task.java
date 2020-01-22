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
import com.viettel.mmserver.database.DatabaseAccessor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import org.apache.log4j.Logger;

public abstract class Task
  extends TimerTask
{
  public static final int HOUR_PER_DAY = 23;
  public static final int MINUTE_PER_HOUR = 59;
  protected Timer timer;
  protected Scheduler scheduler;
  protected int taskId;
  protected String strAppId;
  protected String strObjectName;
  protected String strMethodName;
  protected Scheduler.TaskType taskType;
  protected Date startTime;
  protected Date startDate;
  protected Date realStartDate;
  protected Date endDate;
  protected int intStatus;
  protected String strUserName;
  protected RepeatedTask repeatedTask;
  protected Date firstScheduledRunTime;
  protected Object[] parameters = null;
  protected String[] signature = null;
  protected Logger logger = null;
  public static final int INACTIVE = 0;
  public static final int ACTIVE = 1;
  public static final int EXPRIRE = 2;
  private static final int MILISECOND_PER_MINUTE = 60000;
  
  public void parseMethod()
  {
    if (this.strMethodName == null) {
      return;
    }
    int i = this.strMethodName.indexOf("(");
    if (i == -1) {
      return;
    }
    String s = this.strMethodName.substring(i + 1);
    this.strMethodName = this.strMethodName.substring(0, i);
    s = s.substring(0, s.length() - 1);
    
    String[] typeValuePair = s.split(",");
    int length = typeValuePair.length;
    this.parameters = new Object[length];
    this.signature = new String[length];
    for (i = 0; i < length; i++)
    {
      int j = typeValuePair[i].indexOf(":");
      this.signature[i] = typeValuePair[i].substring(0, j);
      try
      {
        this.parameters[i] = Utils.createObjectFromString(this.signature[i], typeValuePair[i].substring(j + 1));
      }
      catch (Exception ex)
      {
        this.logger.error("Error when parsing the method to get parameters of task " + this.taskId + "\n" + ex);
        this.parameters[i] = typeValuePair[i].substring(j + 1);
      }
    }
  }
  
  public void setScheduler(Scheduler scheduler)
  {
    this.scheduler = scheduler;
  }
  
  public Scheduler getScheduler()
  {
    return this.scheduler;
  }
  
  public void setFirstScheduledRunTime(Date firstScheduledRunTime)
  {
    this.firstScheduledRunTime = firstScheduledRunTime;
  }
  
  public Date getFirstScheduledRunTime()
  {
    return this.firstScheduledRunTime;
  }
  
  public int getTaskId()
  {
    return this.taskId;
  }
  
  public void setTaskId(int taskId)
  {
    this.taskId = taskId;
  }
  
  public Task() {}
  
  public Task(int taskId, String strAppId, String strObjectName, String strMethodName, Scheduler.TaskType taskType, Date startTime, Date startDate, Date endDate, int intStatus, String strUserName)
  {
    this.logger = Logger.getLogger(Scheduler.getMBeanName());
    this.taskId = taskId;
    this.strAppId = strAppId;
    this.strObjectName = strObjectName;
    this.strMethodName = strMethodName;
    this.taskType = taskType;
    this.startTime = startTime;
    this.startDate = startDate;
    Date today = new Date();
    if (startDate != null)
    {
      if (taskType == Scheduler.TaskType.ONCE) {
        this.realStartDate = startDate;
      } else if (startDate.getTime() < today.getTime()) {
        this.realStartDate = today;
      } else {
        this.realStartDate = startDate;
      }
    }
    else {
      this.realStartDate = null;
    }
    if (endDate != null)
    {
      Calendar endDateCalendar = Calendar.getInstance();
      endDateCalendar.setTime(endDate);
      endDateCalendar.set(11, 22);
      endDateCalendar.set(12, 58);
      endDateCalendar.set(13, 58);
      this.endDate = endDateCalendar.getTime();
    }
    this.intStatus = intStatus;
    this.strUserName = strUserName;
    parseMethod();
  }
  
  public Date getStartRunTime()
  {
    Calendar startTimeCalendar = Calendar.getInstance();
    startTimeCalendar.setTime(this.startTime);
    Calendar startDateCalendar = Calendar.getInstance();
    startDateCalendar.setTime(this.realStartDate);
    
    Calendar startRunTimeCalendar = Calendar.getInstance();
    startRunTimeCalendar.set(1, startDateCalendar.get(1));
    startRunTimeCalendar.set(2, startDateCalendar.get(2));
    startRunTimeCalendar.set(5, startDateCalendar.get(5));
    
    startRunTimeCalendar.set(11, startTimeCalendar.get(11));
    startRunTimeCalendar.set(12, startTimeCalendar.get(12));
    startRunTimeCalendar.set(13, startTimeCalendar.get(13));
    
    return startRunTimeCalendar.getTime();
  }
  
  public abstract long getRecurrencePeriod();
  
  public abstract Date getNextTimeExecution();
  
  public abstract boolean schedule();
  
  public abstract Task getSibbling();
  
  public String getType()
  {
    switch (this.taskType.ordinal())
    {
    case 1: 
      return "Once";
    case 2: 
      return "Daily";
    case 3: 
      return "Weekly";
    }
    return "Monthly";
  }
  
  public void run()
  {
    try
    {
      final DatabaseAccessor schedulerDB = this.scheduler.getScheduleDB();
      Date now = new Date();
      final SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
      if (this.endDate != null) {
        if (now.getTime() > this.endDate.getTime())
        {
          cancel();
          this.logger.info("Task " + this.taskId + " " + this.taskType + " is out of date. It has been Canceled");
          if (this.taskType != Scheduler.TaskType.REPEATED) {
            schedulerDB.setStatus(this.taskId, 2);
          }
          return;
        }
      }
      final Task parent = this;
      
      Thread executeTaskThread = new Thread()
      {
        public void run()
        {
          try
          {
            Object result = Scheduler.getMbeanServer().invoke(new ObjectName(Task.this.strObjectName), Task.this.strMethodName, Task.this.parameters, Task.this.signature);
            
            Date d = new Date();
            Task.this.logger.info("<RUNNING_TASK END id=" + Task.this.taskId + " type=" + Task.this.taskType + ">");
            Task.this.logger.info("Task: " + Task.this.taskId + " end running at : " + f.format(d));
            Task.this.logger.warn("<Insert log to DB>");
            Task.this.logger.warn(Task.this.taskId + " END INVOKING " + f.format(d) + parent.toString() + " YES");
            schedulerDB.insertSchedulingLog(Task.this.taskId, "END INVOKING", d, parent.toString(), parent.toString(), "YES");
            
            Task.this.logger.warn("</Insert log to DB>");
            if (Task.this.taskType == Scheduler.TaskType.ONCE)
            {
              Task.this.logger.warn("Task ONCE" + Task.this.taskId + " has been run. It has been Canceled");
              Task.this.cancel();
              schedulerDB.setStatus(Task.this.taskId, 2);
            }
            else if (Task.this.taskType == Scheduler.TaskType.REPEATED)
            {
              Calendar nextTimeExec = Calendar.getInstance();
              nextTimeExec.setTime(new Date(Task.this.scheduledExecutionTime()));
              int minute = (int)Task.this.getRecurrencePeriod() / 60000;
              nextTimeExec.add(12, minute);
              if (nextTimeExec.getTime().after(Task.this.endDate)) {
                Task.this.logger.warn("Task " + Task.this.taskType + " " + Task.this.taskId + " will not run at " + f.format(nextTimeExec.getTime()) + " due to after endDate " + f.format(Task.this.endDate));
              } else {
                Task.this.logger.warn("Task " + Task.this.taskType + " " + Task.this.taskId + " will run at " + f.format(nextTimeExec.getTime()));
              }
            }
            else if (Task.this.getNextTimeExecution().after(Task.this.endDate))
            {
              Task.this.logger.warn("Task " + Task.this.taskType + " " + Task.this.taskId + " will not run at " + f.format(Task.this.getNextTimeExecution()) + " due to after endDate " + f.format(Task.this.endDate));
            }
            else
            {
              Task.this.logger.warn("Task " + Task.this.taskType + " " + Task.this.taskId + " will run at " + f.format(Task.this.getNextTimeExecution()));
            }
            Task.this.logger.info("</RUNNING_TASK END id=" + Task.this.taskId + " type=" + Task.this.taskType + ">");
          }
          catch (InstanceNotFoundException ife)
          {
            Date d = new Date();
            Task.this.logger.error("Task: " + Task.this.taskId + " end running at : " + f.format(d) + "Could not find the MBean: " + Task.this.strObjectName);
            
            Task.this.logger.warn("<Insert log to DB>");
            Task.this.logger.warn(Task.this.taskId + " END INVOKING " + d + parent.toString() + ife.toString() + " NO");
            schedulerDB.insertSchedulingLog(Task.this.taskId, "END INVOKING", d, parent.toString(), ife.toString(), "NO");
            
            Task.this.logger.warn("</Insert log to DB>");
            
            Task.this.logger.info("</RUNNING_TASK END id=" + Task.this.taskId + " type=" + Task.this.taskType + ">");
          }
          catch (Exception e)
          {
            Date d = new Date();
            Task.this.logger.error("Task: " + Task.this.taskId + " end running at : " + f.format(d), e);
            
            Task.this.logger.warn("<Insert log to DB>");
            Task.this.logger.warn(Task.this.taskId + " END INVOKING " + f.format(d) + parent.toString() + e.toString() + " NO");
            
            schedulerDB.insertSchedulingLog(Task.this.taskId, "END INVOKING", d, parent.toString(), e.toString(), "NO");
            
            Task.this.logger.warn("</Insert log to DB>");
            Task.this.logger.info("</RUNNING_TASK END id=" + Task.this.taskId + " type=" + Task.this.taskType + ">");
          }
        }
      };
      this.logger.info("<RUNNING_TASK START id=" + this.taskId + " type=" + this.taskType + ">");
      this.logger.info("Task: " + this.taskId + " start running at : " + f.format(now));
      this.logger.info(toString());
      try
      {
        executeTaskThread.start();
        this.logger.warn("<Insert log to DB>");
        this.logger.warn(this.taskId + " START INVOKING " + f.format(now) + toString() + " YES");
        schedulerDB.insertSchedulingLog(this.taskId, "START INVOKING", now, toString(), toString(), "YES");
        this.logger.warn("</Insert log to DB>");
      }
      catch (Exception e)
      {
        this.logger.warn("<Insert log to DB>");
        this.logger.warn(this.taskId + " START INVOKING " + f.format(now) + toString() + e.toString() + " NO");
        schedulerDB.insertSchedulingLog(this.taskId, "START INVOKING", now, toString(), e.toString(), "NO");
        this.logger.warn("</Insert log to DB>");
      }
      if (this.repeatedTask != null)
      {
        Task repeatedtask = getSibbling();
        repeatedtask.setRepeatedTask(null);
        repeatedtask.setTaskType(Scheduler.TaskType.REPEATED);
        
        Calendar repeatedStartTimeCalendar = Calendar.getInstance();
        repeatedStartTimeCalendar.setTime(new Date(scheduledExecutionTime()));
        repeatedStartTimeCalendar.add(12, this.repeatedTask.getRepeatedRecurrencePeriod());
        
        Calendar repeatedEndDateCalendar = Calendar.getInstance();
        repeatedEndDateCalendar.setTime(new Date(scheduledExecutionTime()));
        repeatedEndDateCalendar.add(12, this.repeatedTask.getRepeatedRecurrencePeriod() * (this.repeatedTask.getRepeatedTimes() + 1));
        
        repeatedEndDateCalendar.add(13, -1);
        if (repeatedEndDateCalendar.getTime().before(repeatedtask.getEndDate())) {
          repeatedtask.setEndDate(repeatedEndDateCalendar.getTime());
        }
        repeatedtask.setRecurrencePeriod(this.repeatedTask.getRepeatedRecurrencePeriodInMilisecond());
        this.logger.info("Schedule a repeated task of task " + this.taskId + ". This repeated task will excecute task " + this.taskId + " every " + this.repeatedTask.getRepeatedRecurrencePeriod() + " minutes util " + this.repeatedTask.getRepeatedTimes());
        



        ((ArrayList)getScheduler().getTaskList()).add(repeatedtask);
        this.timer.scheduleAtFixedRate(repeatedtask, repeatedStartTimeCalendar.getTime(), repeatedtask.getRecurrencePeriod());
      }
      this.logger.info("</RUNNING_TASK START id=" + this.taskId + " type=" + this.taskType + ">");
    }
    catch (Exception ex)
    {
      this.logger.error("Exception when run task " + this.taskId, ex);
      this.logger.info("</RUNNING_TASK START id=" + this.taskId + " type=" + this.taskType + ">");
    }
  }
  
  public void setEndDate(Date endDate)
  {
    this.endDate = endDate;
  }
  
  public void setIntStatus(int intStatus)
  {
    this.intStatus = intStatus;
  }
  
  public void setRepeatedTask(RepeatedTask repeatedTask)
  {
    this.repeatedTask = repeatedTask;
  }
  
  public abstract void setRecurrencePeriod(long paramLong);
  
  public void setStartDate(Date startDate)
  {
    this.realStartDate = startDate;
  }
  
  public void setStrAppId(String strAppId)
  {
    this.strAppId = strAppId;
  }
  
  public void setStrMethodName(String strMethodName)
  {
    this.strMethodName = strMethodName;
  }
  
  public void setStrObjectName(String strObjectName)
  {
    this.strObjectName = strObjectName;
  }
  
  public void setStrUserName(String strUserName)
  {
    this.strUserName = strUserName;
  }
  
  public void setTaskType(Scheduler.TaskType taskType)
  {
    this.taskType = taskType;
  }
  
  public Date getEndDate()
  {
    return this.endDate;
  }
  
  public int getIntStatus()
  {
    return this.intStatus;
  }
  
  public RepeatedTask getRepeatedTask()
  {
    return this.repeatedTask;
  }
  
  public Date getStartDate()
  {
    return this.realStartDate;
  }
  
  public String getStrAppId()
  {
    return this.strAppId;
  }
  
  public String getStrMethodName()
  {
    return this.strMethodName;
  }
  
  public String getStrObjectName()
  {
    return this.strObjectName;
  }
  
  public String getStrUserName()
  {
    return this.strUserName;
  }
  
  public Scheduler.TaskType getTaskType()
  {
    return this.taskType;
  }
  
  public Timer getTimer()
  {
    return this.timer;
  }
  
  public void setTimer(Timer timer)
  {
    this.timer = timer;
  }
  
  public void setStartTime(Date startTime)
  {
    this.startTime = startTime;
  }
  
  public Date getStartTime()
  {
    return this.startTime;
  }
  
  public boolean isLegible()
  {
    return (this.strObjectName != null) && (this.strMethodName != null) && (this.startTime != null) && (this.realStartDate != null) && ((this.taskType.equals(Scheduler.TaskType.ONCE)) || (this.endDate != null)) && (this.intStatus == 1);
  }
  
  public String toString()
  {
    SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    return "TaskId: \t" + this.taskId + " \n" + "Type: \t" + (this.taskType == null ? "REPEATED" : this.taskType) + " \n" + "ObjectName: \t" + this.strObjectName + " \n" + "MethodName: \t" + this.strMethodName + " \n" + "UserName: \t" + this.strUserName + " \n" + "StartTime: \t" + (this.startTime != null ? f.format(this.startTime) : "NULL") + " \n" + "StartDate: \t" + (this.startDate != null ? f.format(this.startDate) : "NULL") + " \n" + "EndDate: \t" + (this.endDate != null ? f.format(this.endDate) : "NULL") + " \n" + "Repeated Task: \t" + (this.repeatedTask != null ? "Yes" : "No") + " \n" + "Status: \t" + this.intStatus;
  }
}
