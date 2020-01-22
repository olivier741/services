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
import com.viettel.mmserver.agent.MMbeanServer;
import com.viettel.mmserver.base.ConfigParam;
import com.viettel.mmserver.base.Log;
import com.viettel.mmserver.base.ProcessThreadMX;
import com.viettel.mmserver.database.DatabaseAccessor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import org.apache.log4j.Logger;

public class Scheduler
  extends ProcessThreadMX
{
  public static final int SLEEP_TIME = 1000;
  private static final String SCHEDULING_NOTIFICATION = "scheduling";
  
  public void process()
  {
    try
    {
      Thread.sleep(1000L);
    }
    catch (InterruptedException ex)
    {
      this.logger.error(ex.getMessage(), ex);
    }
  }
  
  public static enum TaskType
  {
    ONCE,  DAILY,  WEEKLY,  MONTHLY,  REPEATED;
    
    private TaskType() {}
  }
  
  private String appId = "";
  private DatabaseAccessor schedulerDB;
  private List<Task> taskList = Collections.synchronizedList(new ArrayList());
  private ArrayList<Thread> taskThreads = new ArrayList();
  private static MBeanServer mbeanServer;
  private Timer timer;
  private static Scheduler scheduler;
  private static String mBeanName = "Tools:name=Scheduler";
  
  public static synchronized MBeanServer getMbeanServer()
  {
    if (mbeanServer == null) {
      mbeanServer = MMbeanServer.getInstance();
    }
    return mbeanServer;
  }
  
  public static synchronized Scheduler getInstance(String appId)
  {
    if (scheduler == null) {
      try
      {
        scheduler = new Scheduler(appId);
      }
      catch (Exception ex)
      {
        Log.error("Critical error when init Scheduler" + ex.getMessage());
        ex.printStackTrace();
        throw new RuntimeException("Critical error when init Scheduler!");
      }
    }
    return scheduler;
  }
  
  private Scheduler(String threadName)
    throws NotCompliantMBeanException, MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException
  {
    super(threadName);
    registerAgent(mBeanName);
    this.appId = ConfigParam.getInstance().loadAppId();
    this.schedulerDB = DatabaseAccessor.shareInstance();
    this.timer = new Timer();
  }
  
  public DatabaseAccessor getScheduleDB()
  {
    return this.schedulerDB;
  }
  
  public static String getMBeanName()
  {
    return mBeanName;
  }
  
  public void prepareSchedule()
  {
    try
    {
      TimerTask testTask = new TimerTask()
      {
        public void run()
        {
          Scheduler.this.logger.info("Test if the timer is cancel or not ");
        }
      };
      this.timer.schedule(testTask, new Date());
      this.logger.info("Timer is OK!");
    }
    catch (IllegalStateException ise)
    {
      this.timer = new Timer();
      this.logger.info(ise);
      this.logger.info("Timer has been Cancel! Create a new Timer");
    }
  }
  
  protected void prepareStart()
  {
    prepareSchedule();
    this.taskList = this.schedulerDB.loadTaskList(this.appId);
    if (this.taskList == null) {
      return;
    }
    this.logger.info(this.taskList.size() + " tasks are loaded from database.");
    for (Task task : this.taskList) {
      schedule(task);
    }
  }
  
  private void schedule(Task task)
  {
    if (getProcessStatus() == 0) {
      try
      {
        if (task.isLegible())
        {
          task.setTimer(this.timer);
          task.setScheduler(this);
          try
          {
            if (task.schedule()) {
              this.logger.info("Task " + task.getTaskId() + " has been scheduled.");
            }
          }
          catch (IllegalStateException ise)
          {
            this.logger.info(ise);
            prepareSchedule();
            task.setTimer(this.timer);
            task.setScheduler(this);
            if (task.schedule()) {
              this.logger.info("Task " + task.getTaskId() + " has been scheduled.");
            }
          }
        }
        else
        {
          this.logger.info("Task " + task.getTaskId() + " is not legible to be scheduled");
        }
      }
      catch (Exception ex)
      {
        this.logger.error("Error occurs when schedule task: " + task, ex);
      }
    } else {
      this.logger.warn("Scheduler is not running. Task will not be scheduled util Scheduler start again");
    }
  }
  
  public void stop()
  {
    this.logger.warn("Stop Scheduler. All running task will be canceld!");
    if (this.taskList != null)
    {
      try
      {
        for (Task task : this.taskList)
        {
          task.cancel();
          this.logger.warn("Cancel task " + task.getTaskId());
        }
      }
      catch (Exception e)
      {
        this.logger.error(e);
      }
      try
      {
        this.timer.cancel();
        this.logger.warn("Cancel Timer");
      }
      catch (Exception e)
      {
        this.logger.error(e);
      }
    }
    super.stop();
  }
  
  public void print()
  {
    this.logger.info("Scheduler running at " + new SimpleDateFormat().format(new Date()));
  }
  
  public String addTask(String strObjectName, String strMethodName, String strTaskType, String tstartTime, String tstartDate, String tendDate, String strStatus, String strUserName, String strRecurrenceTime, String strTaskMode, String strDayList, String strMonthList, String strHasRepeatedTask, String strRepeatedRecurrenceTime, String strRepeatedTimes)
  {
    this.logger.warn("<ADD_TASK>");
    Task newTask = null;
    int taskId = -1;
    Date startTime = new Date(Long.parseLong(tstartTime));
    Log.info("Start Time: " + startTime + new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(startTime));
    
    Date startDate = new Date(Long.parseLong(tstartDate));
    Date endDate = new Date(Long.parseLong(tendDate));
    int intStatus = Integer.valueOf(strStatus).intValue();
    Log.info("Status: " + intStatus);
    int intRecurrenceTime = 0;
    boolean hasRepeatedTask = strHasRepeatedTask.equalsIgnoreCase("y");
    int repeatedRecurrenceTime = 0;
    int repeatedTimes = 0;
    if (hasRepeatedTask)
    {
      repeatedRecurrenceTime = Integer.parseInt(strRepeatedRecurrenceTime);
      repeatedTimes = Integer.parseInt(strRepeatedTimes);
    }
    if (strTaskType.equalsIgnoreCase("Once"))
    {
      taskId = this.schedulerDB.addOnceTask(this.appId, strObjectName, strMethodName, startTime, startDate, intStatus, strUserName);
      
      newTask = new OnceTask(taskId, this.appId, strObjectName, strMethodName, TaskType.ONCE, startTime, startDate, null, intStatus, strUserName);
    }
    if (strTaskType.equalsIgnoreCase("Daily"))
    {
      intRecurrenceTime = Integer.parseInt(strRecurrenceTime);
      if (!hasRepeatedTask)
      {
        taskId = this.schedulerDB.addNonRepeatedDailyTask(this.appId, strObjectName, strMethodName, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime);
        
        newTask = new DailyTask(taskId, this.appId, strObjectName, strMethodName, TaskType.DAILY, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime);
      }
      else
      {
        taskId = this.schedulerDB.addRepeatedDailyTask(this.appId, strObjectName, strMethodName, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime, repeatedRecurrenceTime, repeatedTimes);
        
        newTask = new DailyTask(taskId, this.appId, strObjectName, strMethodName, TaskType.DAILY, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime);
        
        newTask.setRepeatedTask(new RepeatedTask(repeatedRecurrenceTime, repeatedTimes));
      }
    }
    if (strTaskType.equalsIgnoreCase("Weekly"))
    {
      intRecurrenceTime = Integer.parseInt(strRecurrenceTime);
      if (!hasRepeatedTask)
      {
        taskId = this.schedulerDB.addNonRepeatedWeeklyTask(this.appId, strObjectName, strMethodName, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime, strDayList);
        
        newTask = new WeeklyTask(taskId, this.appId, strObjectName, strMethodName, TaskType.WEEKLY, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime, strDayList);
      }
      else
      {
        taskId = this.schedulerDB.addRepeatedWeeklyTask(this.appId, strObjectName, strMethodName, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime, strDayList, repeatedRecurrenceTime, repeatedTimes);
        

        newTask = new WeeklyTask(taskId, this.appId, strObjectName, strMethodName, TaskType.WEEKLY, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime, strDayList);
        
        newTask.setRepeatedTask(new RepeatedTask(repeatedRecurrenceTime, repeatedTimes));
      }
    }
    if (strTaskType.equalsIgnoreCase("Monthly")) {
      if (!hasRepeatedTask)
      {
        taskId = this.schedulerDB.addNonRepeatedMonthlyTask(this.appId, strObjectName, strMethodName, startTime, startDate, endDate, intStatus, strUserName, strTaskMode, strDayList, strMonthList);
        
        newTask = new MonthlyTask(taskId, this.appId, strObjectName, strMethodName, TaskType.MONTHLY, startTime, startDate, endDate, intStatus, strUserName, strTaskMode, strDayList, strMonthList);
      }
      else
      {
        taskId = this.schedulerDB.addRepeatedMonthlyTask(this.appId, strObjectName, strMethodName, startTime, startDate, endDate, intStatus, strUserName, strTaskMode, strDayList, strMonthList, repeatedRecurrenceTime, repeatedTimes);
        

        newTask = new MonthlyTask(taskId, this.appId, strObjectName, strMethodName, TaskType.MONTHLY, startTime, startDate, endDate, intStatus, strUserName, strTaskMode, strDayList, strMonthList);
        
        newTask.setRepeatedTask(new RepeatedTask(repeatedRecurrenceTime, repeatedTimes));
      }
    }
    if (taskId <= 0)
    {
      this.logger.warn("\tCan not Insert new task into database. Add failed");
      return "0";
    }
    this.logger.warn("\tInsert new task " + taskId + " into database successfully");
    this.taskList.add(newTask);
    schedule(newTask);
    this.logger.warn("</ADD_TASK>");
    return Integer.toString(taskId);
  }
  
  public ArrayList<Integer> inverseStatus(String[] strTaskId)
  {
    ArrayList<Integer> returns = new ArrayList();
    if (strTaskId == null) {
      return null;
    }
    for (String taskId : strTaskId)
    {
      int i = inverseStatus(taskId);
      returns.add(Integer.valueOf(i));
    }
    return returns;
  }
  
  public int inverseStatus(String strTaskId)
  {
    this.logger.warn("<INVERT_TASK_STATUS taskID=" + strTaskId + ">");
    int taskId = Integer.parseInt(strTaskId);
    
    int newStatus = this.schedulerDB.inverseStatus(taskId);
    if (newStatus >= 0) {
      for (int i = 0; i < this.taskList.size(); i++)
      {
        Task task = (Task)this.taskList.get(i);
        if (task.getTaskId() == taskId) {
          if (task.getIntStatus() == 1)
          {
            task.setIntStatus(0);
            task.cancel();
            this.logger.warn("\tCancel task " + taskId);
          }
          else if (task.getIntStatus() == 0)
          {
            task.setIntStatus(1);
            try
            {
              schedule(task);
              this.logger.info("\tSchedule task " + taskId);
            }
            catch (IllegalStateException ise)
            {
              this.logger.info(ise);
              this.logger.info("\tSchedule again task " + taskId);
              Task sibbling = task.getSibbling();
              schedule(sibbling);
            }
          }
        }
      }
    }
    this.logger.info("Task " + taskId + "'s status now is " + newStatus);
    this.logger.warn("</INVERT_TASK_STATUS taskID=" + strTaskId + ">");
    
    return newStatus;
  }
  
  private void sendNotification(String message)
  {
    Notification notification = new Notification("scheduling", this, 0L, message);
    
    this.notificationHandler.sendNotification(notification);
  }
  
  public ArrayList<Integer> removeTask(String[] strTaskId)
  {
    ArrayList<Integer> returns = new ArrayList();
    if (strTaskId == null) {
      return null;
    }
    for (String taskId : strTaskId)
    {
      int i = removeTask(taskId);
      returns.add(Integer.valueOf(i));
    }
    return returns;
  }
  
  public int removeTask(String strTaskId)
  {
    this.logger.warn("<REMOVE_TASK taskID=" + strTaskId + ">");
    int taskId = Integer.parseInt(strTaskId);
    try
    {
      for (int i = 0; i < this.taskList.size(); i++)
      {
        Task task = (Task)this.taskList.get(i);
        if (task.getTaskId() == taskId)
        {
          task.cancel();
          this.taskList.remove(i);
          i--;
        }
      }
      this.logger.warn("\tCancel task " + taskId);
    }
    catch (Exception e)
    {
      this.logger.error("\tCan not Cancel task " + taskId + ". " + e);
    }
    int i = this.schedulerDB.removeTask(taskId);
    if (i > 0) {
      this.logger.warn("\tDelete task " + taskId + " from database");
    } else {
      this.logger.warn("\tCan not delete task " + taskId + " from database");
    }
    this.logger.warn("</REMOVE_TASK taskID=" + strTaskId + ">");
    return i;
  }
  
  public int editTask(String strTaskId, String strObjectName, String strMethodName, String strTaskType, String strStartTime, String strStartDate, String strEndDate, String strStatus, String strUserName, String strRecurrenceTime, String strTaskMode, String strDayList, String strMonthList, String strHasRepeatedTask, String strRepeatedTaskId, String strRepeatedRecurrenceTime, String strRepeatedTimes)
  {
    this.logger.warn("<EDIT_TASK taskID=" + strTaskId + ">");
    int taskId = Integer.parseInt(strTaskId);
    
    Date startTime = new Date(Long.parseLong(strStartTime));
    Date startDate = new Date(Long.parseLong(strStartDate));
    Date endDate = new Date(Long.parseLong(strEndDate));
    int intStatus = Integer.valueOf(strStatus).intValue();
    int intRecurrenceTime = 0;
    boolean hasRepeatedTask = strHasRepeatedTask.equalsIgnoreCase("y");
    int repeatedTaskId = 0;
    int repeatedRecurrenceTime = 0;
    int repeatedTimes = 0;
    if (hasRepeatedTask)
    {
      repeatedTaskId = Integer.parseInt(strRepeatedTaskId);
      repeatedRecurrenceTime = Integer.parseInt(strRepeatedRecurrenceTime);
      repeatedTimes = Integer.parseInt(strRepeatedTimes);
    }
    if ((!strTaskType.equalsIgnoreCase("Once")) && (!strTaskType.equalsIgnoreCase("Monthly"))) {
      intRecurrenceTime = Integer.parseInt(strRecurrenceTime);
    }
    int success = this.schedulerDB.editTask(taskId, strObjectName, strMethodName, strTaskType, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime, strTaskMode, strDayList, strMonthList, hasRepeatedTask, repeatedTaskId, repeatedRecurrenceTime, repeatedTimes);
    if ((success == -1) || (success == 0))
    {
      this.logger.warn("\tCan not update task " + taskId + " into database. Edit failed");
      return success;
    }
    this.logger.warn("\tUpdate task " + taskId + " into database successfully");
    for (int i = 0; i < this.taskList.size(); i++)
    {
      Task oldTask = (Task)this.taskList.get(i);
      if (oldTask.getTaskId() == taskId)
      {
        oldTask.cancel();
        this.taskList.remove(i);
        i--;
      }
    }
    this.logger.warn("\tTask " + taskId + "has been canceled");
    Task newTask = null;
    if (strTaskType.equalsIgnoreCase("Once")) {
      newTask = new OnceTask(taskId, this.appId, strObjectName, strMethodName, TaskType.ONCE, startTime, startDate, null, intStatus, strUserName);
    }
    if (strTaskType.equalsIgnoreCase("Daily")) {
      newTask = new DailyTask(taskId, this.appId, strObjectName, strMethodName, TaskType.DAILY, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime);
    }
    if (strTaskType.equalsIgnoreCase("Weekly")) {
      newTask = new WeeklyTask(taskId, this.appId, strObjectName, strMethodName, TaskType.WEEKLY, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime, strDayList);
    }
    if (strTaskType.equalsIgnoreCase("Monthly")) {
      newTask = new MonthlyTask(taskId, this.appId, strObjectName, strMethodName, TaskType.MONTHLY, startTime, startDate, endDate, intStatus, strUserName, strTaskMode, strDayList, strMonthList);
    }
    if (hasRepeatedTask) {
      newTask.setRepeatedTask(new RepeatedTask(repeatedRecurrenceTime, repeatedTimes));
    }
    this.taskList.add(newTask);
    this.logger.info("\tSchedule again task " + taskId);
    this.logger.info(newTask);
    schedule(newTask);
    
    this.logger.warn("</EDIT_TASK taskID=" + strTaskId + ">");
    
    return taskId;
  }
  
  public List<Task> getTaskList()
  {
    return this.taskList;
  }
  
  public Object invoke(String operationName, Object[] params, String[] signature)
    throws MBeanException, ReflectionException
  {
    if (operationName == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"), "Cannot invoke a null operation in " + this.dClassName);
    }
    if (operationName.equals("editTask")) {
      return Integer.valueOf(editTask((String)params[0], (String)params[1], (String)params[2], (String)params[3], (String)params[4], (String)params[5], (String)params[6], (String)params[7], (String)params[8], (String)params[9], (String)params[10], (String)params[11], (String)params[12], (String)params[13], (String)params[14], (String)params[15], (String)params[16]));
    }
    if (operationName.equals("addTask")) {
      return addTask((String)params[0], (String)params[1], (String)params[2], (String)params[3], (String)params[4], (String)params[5], (String)params[6], (String)params[7], (String)params[8], (String)params[9], (String)params[10], (String)params[11], (String)params[12], (String)params[13], (String)params[14]);
    }
    if (operationName.equals("removeTask"))
    {
      String[] strParams = new String[params.length];
      int i = 0;
      for (Object o : params) {
        strParams[(i++)] = ((String)o);
      }
      return removeTask(strParams);
    }
    if (operationName.equals("getInfor")) {
      return getInfor();
    }
    if (operationName.equals("setDump"))
    {
      Boolean b = (Boolean)params[0];
      setDumpThread(b.booleanValue());
      return null;
    }
    if (operationName.equals("inverseStatus"))
    {
      String[] strParams = new String[params.length];
      int i = 0;
      for (Object o : params) {
        strParams[(i++)] = ((String)o);
      }
      return inverseStatus(strParams);
    }
    return super.invoke(operationName, params, signature);
  }
  
  protected MBeanOperationInfo[] buildOperations()
  {
    ArrayList<MBeanOperationInfo> v = new ArrayList();
    MBeanParameterInfo[] params = new MBeanParameterInfo[17];
    params[0] = new MBeanParameterInfo("TaskID", "java.lang.String", "");
    params[1] = new MBeanParameterInfo("ObjectName", "java.lang.String", "MBean Object Name , subject task");
    params[2] = new MBeanParameterInfo("MethodName", "java.lang.String", "Method will invoked by task");
    params[3] = new MBeanParameterInfo("TaskType", "java.lang.String", "");
    params[4] = new MBeanParameterInfo("StartTime", "java.lang.String", "");
    params[5] = new MBeanParameterInfo("StartDate", "java.lang.String", "");
    params[6] = new MBeanParameterInfo("EndDate", "java.lang.String", "");
    params[7] = new MBeanParameterInfo("Status", "java.lang.String", "");
    params[8] = new MBeanParameterInfo("Username", "java.lang.String", "");
    params[9] = new MBeanParameterInfo("RecurrenceTime", "java.lang.String", "");
    params[10] = new MBeanParameterInfo("TaskMode", "java.lang.String", "");
    params[11] = new MBeanParameterInfo("DayList", "java.lang.String", "");
    params[12] = new MBeanParameterInfo("MonthList", "java.lang.String", "");
    params[13] = new MBeanParameterInfo("HasRepeatedTask", "java.lang.String", "");
    params[14] = new MBeanParameterInfo("RepeatedTaskID", "java.lang.String", "");
    params[15] = new MBeanParameterInfo("RepeatedRecurrenceTime", "java.lang.String", "");
    params[16] = new MBeanParameterInfo("RepeatedTimes", "java.lang.String", "");
    v.add(new MBeanOperationInfo("editTask", "edit a task in scheduler", params, "int", 1));
    




    params = new MBeanParameterInfo[15];
    params[0] = new MBeanParameterInfo("ObjectName", "java.lang.String", "MBean Object Name , subject task");
    params[1] = new MBeanParameterInfo("MethodName", "java.lang.String", "Method will invoked by task");
    params[2] = new MBeanParameterInfo("TaskType", "java.lang.String", "");
    params[3] = new MBeanParameterInfo("StartTime", "java.lang.String", "");
    params[4] = new MBeanParameterInfo("StartDate", "java.lang.String", "");
    params[5] = new MBeanParameterInfo("EndDate", "java.lang.String", "");
    params[6] = new MBeanParameterInfo("Status", "java.lang.String", "");
    params[7] = new MBeanParameterInfo("Username", "java.lang.String", "");
    params[8] = new MBeanParameterInfo("RecurrenceTime", "java.lang.String", "");
    params[9] = new MBeanParameterInfo("TaskMode", "java.lang.String", "");
    params[10] = new MBeanParameterInfo("DayList", "java.lang.String", "");
    params[11] = new MBeanParameterInfo("MonthList", "java.lang.String", "");
    params[12] = new MBeanParameterInfo("HasRepeatedTask", "java.lang.String", "");
    params[13] = new MBeanParameterInfo("RepeatedRecurrenceTime", "java.lang.String", "");
    params[14] = new MBeanParameterInfo("RepeatedTimes", "java.lang.String", "");
    v.add(new MBeanOperationInfo("addTask", "add a task into scheduler", params, "java.lang.String", 1));
    




    params = new MBeanParameterInfo[1];
    params[0] = new MBeanParameterInfo("TaskID", "java.lang.String", "TaskID");
    v.add(new MBeanOperationInfo("removeTask", "remove a task in scheduler", params, "int", 1));
    




    MBeanOperationInfo[] old = super.buildOperations();
    for (int i = 0; i < old.length; i++) {
      v.add(old[i]);
    }
    return (MBeanOperationInfo[])v.toArray(new MBeanOperationInfo[v.size()]);
  }
  
  public String getInfor()
  {
    StringBuilder info = new StringBuilder(super.getInfor());
    info.append(System.getProperty("line.separator"));
    if (this.taskList != null)
    {
      info.append("Current scheduling task list:" + this.taskList.size() + "task(s)");
      for (Task task : this.taskList)
      {
        info.append(System.getProperty("line.separator"));
        info.append("\t" + task.getTaskId() + " " + task.getTaskType());
      }
    }
    return info.toString();
  }
}