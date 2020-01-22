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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import org.apache.log4j.Logger;

public class MonthlyTask  extends Task
{
  private long recurrencePeriod;
  private ArrayList<Calendar> startRunTimeList = new ArrayList();
  private ArrayList<MonthlyTask> sibblingTasks = new ArrayList();
  private String dayList = "";
  private String monthList = "";
  private String taskMode = "";
  private static final long MILISECOND_OF_YEAR = 31536000000L;
  
  public void setDayList(String dayList)
  {
    this.dayList = dayList;
  }
  
  public void setMonthList(String monthList)
  {
    this.monthList = monthList;
  }
  
  public void setTaskMode(String taskMode)
  {
    this.taskMode = taskMode;
  }
  
  public String getDayList()
  {
    return this.dayList;
  }
  
  public String getMonthList()
  {
    return this.monthList;
  }
  
  public String getTaskMode()
  {
    return this.taskMode;
  }
  
  public MonthlyTask() {}
  
  public MonthlyTask(int taskId, String strAppId, String strObjectName, String strMethodName, Scheduler.TaskType taskType, Date startTime, Date startDate, Date endDate, int intStatus, String strUserName, String taskMode, String dayList, String monthList)
  {
    super(taskId, strAppId, strObjectName, strMethodName, taskType, startTime, startDate, endDate, intStatus, strUserName);
    
    this.dayList = dayList;this.monthList = monthList;
    this.taskMode = taskMode;
    this.recurrencePeriod = 31536000000L;
    Date now = new Date();
    String[] listDay = dayList.split(",");
    String[] listMonth = monthList.split(",");
    Calendar startDateCalendar = Calendar.getInstance();
    startDateCalendar.setTime(getStartDate());
    Calendar startTimeCalendar = Calendar.getInstance();
    startTimeCalendar.setTime(startTime);
    if (taskMode.equalsIgnoreCase("DAY_OF_MONTH")) {
      for (int i = 0; i < listDay.length; i++)
      {
        int intDay = Integer.parseInt(listDay[i]);
        for (int j = 0; j < listMonth.length; j++)
        {
          int intMonth = Integer.parseInt(listMonth[j]);
          Calendar startRunTimeCalendar = Calendar.getInstance();
          startRunTimeCalendar.set(1, startDateCalendar.get(1));
          startRunTimeCalendar.set(2, intMonth - 1);
          startRunTimeCalendar.set(5, intDay);
          startRunTimeCalendar.set(11, startTimeCalendar.get(11));
          startRunTimeCalendar.set(12, startTimeCalendar.get(12));
          startRunTimeCalendar.set(13, startTimeCalendar.get(13));
          this.startRunTimeList.add(startRunTimeCalendar);
        }
      }
    } else if (taskMode.equalsIgnoreCase("DAY_OF_WEEK")) {
      for (int i = 0; i < listDay.length; i++)
      {
        String[] dayInfos = listDay[i].split(":");
        String iweekOfMonth = dayInfos[0].trim();
        String idayOfWeek = dayInfos[1].trim().toUpperCase();
        for (int j = 0; j < listMonth.length; j++)
        {
          int intMonth = Integer.parseInt(listMonth[j]);
          Calendar startRunTimeCalendar = Calendar.getInstance();
          int iintDayOfWeek = 0;
          if (idayOfWeek.equalsIgnoreCase("SUNDAY"))
          {
            iintDayOfWeek = 1;
            startRunTimeCalendar.set(7, 1);
          }
          else if (idayOfWeek.equalsIgnoreCase("MONDAY"))
          {
            iintDayOfWeek = 2;
            startRunTimeCalendar.set(7, 2);
          }
          else if (idayOfWeek.equalsIgnoreCase("TUESDAY"))
          {
            iintDayOfWeek = 3;
            startRunTimeCalendar.set(7, 3);
          }
          else if (idayOfWeek.equalsIgnoreCase("WEDNESDAY"))
          {
            iintDayOfWeek = 4;
            startRunTimeCalendar.set(7, 4);
          }
          else if (idayOfWeek.equalsIgnoreCase("THURSDAY"))
          {
            iintDayOfWeek = 5;
            startRunTimeCalendar.set(7, 5);
          }
          else if (idayOfWeek.equalsIgnoreCase("FRIDAY"))
          {
            iintDayOfWeek = 6;
            startRunTimeCalendar.set(7, 6);
          }
          else if (idayOfWeek.equalsIgnoreCase("SATURDAY"))
          {
            iintDayOfWeek = 7;
            startRunTimeCalendar.set(7, 7);
          }
          Calendar firstWeekOfMonth = Calendar.getInstance();
          firstWeekOfMonth.set(5, 1);
          firstWeekOfMonth.set(2, intMonth - 1);
          int idayOfFirstWeek = firstWeekOfMonth.get(7);
          if (iintDayOfWeek < idayOfFirstWeek) {
            startRunTimeCalendar.set(3, firstWeekOfMonth.get(3) + 1);
          } else {
            startRunTimeCalendar.set(3, firstWeekOfMonth.get(3));
          }
          if (startRunTimeCalendar.get(2) < intMonth - 1) {
            startRunTimeCalendar.set(3, firstWeekOfMonth.get(3) + 1);
          }
          if (iweekOfMonth.equalsIgnoreCase("second"))
          {
            startRunTimeCalendar.set(3, startRunTimeCalendar.get(3) + 1);
          }
          else if (iweekOfMonth.equalsIgnoreCase("third"))
          {
            startRunTimeCalendar.set(3, startRunTimeCalendar.get(3) + 2);
          }
          else if (iweekOfMonth.equalsIgnoreCase("fourth"))
          {
            startRunTimeCalendar.set(3, startRunTimeCalendar.get(3) + 3);
          }
          else if (iweekOfMonth.equalsIgnoreCase("last"))
          {
            startRunTimeCalendar.set(3, startRunTimeCalendar.get(3) + 3);
            
            Calendar lastWeek = Calendar.getInstance();
            lastWeek.setTime(startRunTimeCalendar.getTime());
            lastWeek.set(3, lastWeek.get(3) + 1);
            if (lastWeek.get(2) == startRunTimeCalendar.get(2)) {
              startRunTimeCalendar = lastWeek;
            }
          }
          startRunTimeCalendar.set(1, startDateCalendar.get(1));
          startRunTimeCalendar.set(11, startTimeCalendar.get(11));
          startRunTimeCalendar.set(12, startTimeCalendar.get(12));
          startRunTimeCalendar.set(13, startTimeCalendar.get(13));
          if (startRunTimeCalendar.getTime().before(now))
          {
            firstWeekOfMonth = Calendar.getInstance();
            firstWeekOfMonth.set(5, 1);
            firstWeekOfMonth.set(2, intMonth - 1);
            firstWeekOfMonth.add(1, 1);
            startRunTimeCalendar.set(1, firstWeekOfMonth.get(1));
            startRunTimeCalendar.set(11, startTimeCalendar.get(11));
            startRunTimeCalendar.set(12, startTimeCalendar.get(12));
            startRunTimeCalendar.set(13, startTimeCalendar.get(13));
            idayOfFirstWeek = firstWeekOfMonth.get(7);
            if (iintDayOfWeek < idayOfFirstWeek) {
              startRunTimeCalendar.set(3, firstWeekOfMonth.get(3) + 1);
            } else {
              startRunTimeCalendar.set(3, firstWeekOfMonth.get(3));
            }
            if (startRunTimeCalendar.get(2) < intMonth - 1) {
              startRunTimeCalendar.set(3, firstWeekOfMonth.get(3) + 1);
            }
            if (iweekOfMonth.equalsIgnoreCase("second"))
            {
              startRunTimeCalendar.set(3, startRunTimeCalendar.get(3) + 1);
            }
            else if (iweekOfMonth.equalsIgnoreCase("third"))
            {
              startRunTimeCalendar.set(3, startRunTimeCalendar.get(3) + 2);
            }
            else if (iweekOfMonth.equalsIgnoreCase("fourth"))
            {
              startRunTimeCalendar.set(3, startRunTimeCalendar.get(3) + 3);
            }
            else if (iweekOfMonth.equalsIgnoreCase("last"))
            {
              startRunTimeCalendar.set(3, startRunTimeCalendar.get(3) + 3);
              
              Calendar lastWeek = Calendar.getInstance();
              lastWeek.setTime(startRunTimeCalendar.getTime());
              lastWeek.set(3, lastWeek.get(3) + 1);
              if (lastWeek.get(2) == startRunTimeCalendar.get(2)) {
                startRunTimeCalendar = lastWeek;
              }
            }
          }
          this.startRunTimeList.add(startRunTimeCalendar);
        }
      }
    }
  }
  
  public boolean schedule()
  {
    SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH-mm-ss");
    for (Calendar calendar : this.startRunTimeList)
    {
      MonthlyTask newTask = getSibbling();
      
      this.sibblingTasks.add(newTask);
      Date now = new Date();
      if (calendar.getTime().getTime() < now.getTime()) {
        calendar.add(1, 1);
      }
      newTask.setFirstScheduledRunTime(calendar.getTime());
      if (newTask.getFirstScheduledRunTime().after(this.endDate))
      {
        this.logger.warn("Task " + this.taskId + " will not run at " + f.format(calendar.getTime()) + " due to after endDate " + f.format(this.endDate));
      }
      else
      {
        this.logger.info("Task " + this.taskId + " will run at " + f.format(calendar.getTime()));
        this.timer.schedule(newTask, newTask.getFirstScheduledRunTime(), getRecurrencePeriod());
      }
    }
    return true;
  }
  
  public boolean cancel()
  {
    if (this.sibblingTasks != null) {
      for (MonthlyTask mt : this.sibblingTasks) {
        mt.cancel();
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
  
  public Date getNextTimeExecution()
  {
    Calendar nextTimeExecution = Calendar.getInstance();
    nextTimeExecution.setTime(new Date(scheduledExecutionTime()));
    nextTimeExecution.add(1, 1);
    return nextTimeExecution.getTime();
  }
  
  public MonthlyTask getSibbling()
  {
    MonthlyTask sibbling = new MonthlyTask();
    sibbling.taskId = this.taskId;
    sibbling.endDate = new Date(this.endDate.getTime());
    sibbling.intStatus = this.intStatus;
    sibbling.recurrencePeriod = this.recurrencePeriod;
    sibbling.startDate = new Date(this.startDate.getTime());
    sibbling.realStartDate = new Date(this.realStartDate.getTime());
    sibbling.startTime = new Date(this.startTime.getTime());
    sibbling.strMethodName = this.strMethodName;
    sibbling.strObjectName = this.strObjectName;
    sibbling.strUserName = this.strUserName;
    sibbling.timer = this.timer;
    sibbling.startRunTimeList = null;
    sibbling.repeatedTask = this.repeatedTask;
    sibbling.parameters = this.parameters;
    sibbling.signature = this.signature;
    sibbling.sibblingTasks = null;
    sibbling.logger = this.logger;
    sibbling.scheduler = this.scheduler;
    sibbling.taskType = this.taskType;
    if (getFirstScheduledRunTime() != null) {
      sibbling.firstScheduledRunTime = new Date(getFirstScheduledRunTime().getTime());
    }
    return sibbling;
  }
}