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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import org.apache.log4j.Logger;

public class WeeklyTask
  extends Task
{
  public static final int DAY_PER_WEEK = 7;
  private int recurrenceTime;
  private long recurrencePeriod;
  private String dayList = "";
  private ArrayList<Calendar> startRunTimeList = new ArrayList();
  private ArrayList<WeeklyTask> sibblingTasks = new ArrayList();
  public static final long MINISECOND_OF_ONE_WEEK = 60480000L;
  
  public WeeklyTask() {}
  
  public WeeklyTask(int taskId, String strAppId, String strObjectName, String strMethodName, Scheduler.TaskType taskType, Date startTime, Date startDate, Date endDate, int intStatus, String strUserName, int recurrenceTime, String dayList)
  {
    super(taskId, strAppId, strObjectName, strMethodName, taskType, startTime, startDate, endDate, intStatus, strUserName);
    

    this.recurrenceTime = recurrenceTime;
    this.recurrencePeriod = (recurrenceTime * 60480000L);
    this.dayList = dayList;
    Calendar startDateCalendar = Calendar.getInstance();
    startDateCalendar.setTime(getStartDate());
    
    Calendar startTimeCalendar = Calendar.getInstance();
    startTimeCalendar.setTime(startTime);
    String[] listDay = dayList.split(",");
    for (int i = 0; i < listDay.length; i++)
    {
      int intDay = Integer.parseInt(listDay[i]);
      
      Calendar startRunTimeCalendar = Calendar.getInstance();
      
      startRunTimeCalendar.set(1, startDateCalendar.get(1));
      startRunTimeCalendar.set(3, startDateCalendar.get(3));
      startRunTimeCalendar.set(7, intDay);
      
      startRunTimeCalendar.set(11, startTimeCalendar.get(11));
      startRunTimeCalendar.set(12, startTimeCalendar.get(12));
      startRunTimeCalendar.set(13, startTimeCalendar.get(13));
      if (startRunTimeCalendar.before(startDateCalendar)) {
        startRunTimeCalendar.add(5, 7);
      }
      this.startRunTimeList.add(startRunTimeCalendar);
    }
  }
  
  public boolean schedule()
  {
    for (Calendar calendar : this.startRunTimeList)
    {
      WeeklyTask newTask = getSibbling();
      this.sibblingTasks.add(newTask);
      
      Date now = new Date();
      if (calendar.getTime().getTime() < now.getTime()) {
        calendar.add(5, this.recurrenceTime * 7);
      }
      newTask.setFirstScheduledRunTime(calendar.getTime());
      if (newTask.getFirstScheduledRunTime().after(this.endDate))
      {
        this.logger.warn("Task " + this.taskId + " will not run at " + new SimpleDateFormat("dd/MM/yyyy HH-mm-ss").format(newTask.getFirstScheduledRunTime()) + " due to after endDate " + new SimpleDateFormat("dd/MM/yyyy HH-mm-ss").format(this.endDate));
      }
      else
      {
        this.logger.info("Task " + this.taskId + " will run in " + new SimpleDateFormat("dd/MM/yyyy HH-mm-ss").format(newTask.getFirstScheduledRunTime()));
        
        this.timer.scheduleAtFixedRate(newTask, newTask.getFirstScheduledRunTime(), getRecurrencePeriod());
      }
    }
    return true;
  }
  
  public boolean cancel()
  {
    if (this.sibblingTasks != null) {
      for (WeeklyTask wt : this.sibblingTasks) {
        wt.cancel();
      }
    }
    super.cancel();
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
  
  public Date getNextTimeExecution()
  {
    Calendar nextTimeExecution = Calendar.getInstance();
    nextTimeExecution.setTime(new Date(scheduledExecutionTime()));
    nextTimeExecution.add(5, this.recurrenceTime * 7);
    return nextTimeExecution.getTime();
  }
  
  public WeeklyTask getSibbling()
  {
    WeeklyTask sibbling = new WeeklyTask();
    sibbling.setTaskId(this.taskId);
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
    sibbling.parameters = this.parameters;
    sibbling.signature = this.signature;
    sibbling.logger = this.logger;
    sibbling.scheduler = this.scheduler;
    sibbling.repeatedTask = this.repeatedTask;
    sibbling.taskType = this.taskType;
    if (getFirstScheduledRunTime() != null) {
      sibbling.firstScheduledRunTime = new Date(getFirstScheduledRunTime().getTime());
    }
    return sibbling;
  }
}
