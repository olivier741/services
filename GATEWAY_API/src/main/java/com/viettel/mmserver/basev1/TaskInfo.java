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
package com.viettel.mmserver.basev1;

/**
 *
 * @author olivier.tatsinkou
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

public class TaskInfo
  extends StandardMBean
  implements TaskInforMBean
{
  public static final int HOUR_TYPE = 1;
  public static final int DAILY_TYPE = 2;
  public static final int MONTHLY_TYPE = 3;
  public static final int ONCE_TYPE = 5;
  public static final int PERIOD_TYPE = 6;
  private Integer id;
  private String classPath;
  
  public static enum RunningState
  {
    READY,  RUNNING;
    
    private RunningState() {}
  }
  
  public static enum RepeatType
  {
    UNDEFINED,  ONCE,  HOUR,  DAILY,  MONTHLY,  PERIOD;
    
    private RepeatType() {}
  }
  
  private String jarPath = null;
  private MmProcess mmProcess;
  private String name;
  private String param;
  private String type;
  private RepeatType repeatType;
  private RunningState runningState;
  private boolean[] taskDone;
  private List<Calendar> taskTimes;
  private Calendar lastRun;
  private int interval = -1;
  
  public TaskInfo()
    throws NotCompliantMBeanException
  {
    super(TaskInforMBean.class);
    this.taskTimes = new ArrayList();
    this.runningState = RunningState.READY;
    this.lastRun = null;
    this.repeatType = RepeatType.UNDEFINED;
  }
  
  public String getJarPath()
  {
    return this.jarPath;
  }
  
  public void setJarPath(String jarPath)
  {
    this.jarPath = jarPath;
  }
  
  public Integer getId()
  {
    return this.id;
  }
  
  public void setId(Integer id)
  {
    this.id = id;
  }
  
  public MmProcess getMmProcess()
  {
    return this.mmProcess;
  }
  
  public void setMmProcess(MmProcess mmProcess)
  {
    this.mmProcess = mmProcess;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public void setType(String repeatType)
  {
    this.type = repeatType;
  }
  
  public void addTaskTime(String tasktime)
  {
    this.taskTimes.add(convertStringToDate(tasktime));
  }
  
  public synchronized void removeTaskTimeString(int i)
  {
    if ((i > -1) && (i < this.taskTimes.size()))
    {
      this.taskTimes.remove(i);
      if (this.repeatType == RepeatType.PERIOD)
      {
        this.runningState = RunningState.READY;
        this.lastRun = null;
      }
      else
      {
        boolean[] temp = new boolean[this.taskTimes.size()];
        for (int j = 0; j < i; j++) {
          temp[j] = this.taskDone[j];
        }
        for (int j = i + 1; j < this.taskTimes.size() + 1; j++) {
          temp[(j - 1)] = this.taskDone[j];
        }
        this.taskDone = temp;
      }
    }
  }
  
  public String[] getTaskTimeString()
  {
    String[] temp = new String[this.taskTimes.size()];
    for (int i = 0; i < temp.length; i++) {
      switch (this.repeatType.ordinal())
      {
      case 1: 
        temp[i] = (((Calendar)this.taskTimes.get(i)).get(12) + ":" + ((Calendar)this.taskTimes.get(i)).get(13));
        break;
      case 2: 
        temp[i] = (((Calendar)this.taskTimes.get(i)).get(11) + ":" + ((Calendar)this.taskTimes.get(i)).get(12) + ":" + ((Calendar)this.taskTimes.get(i)).get(13));
        
        break;
      case 3: 
        temp[i] = (((Calendar)this.taskTimes.get(i)).get(5) + ":" + ((Calendar)this.taskTimes.get(i)).get(11) + ":" + ((Calendar)this.taskTimes.get(i)).get(12) + ":" + ((Calendar)this.taskTimes.get(i)).get(13));
        


        break;
      case 4: 
        temp[i] = (((Calendar)this.taskTimes.get(i)).get(1) + ":" + ((Calendar)this.taskTimes.get(i)).get(2) + ":" + ((Calendar)this.taskTimes.get(i)).get(5) + ":" + ((Calendar)this.taskTimes.get(i)).get(11) + ":" + ((Calendar)this.taskTimes.get(i)).get(12) + ":" + ((Calendar)this.taskTimes.get(i)).get(13));
        




        break;
      case 5: 
        temp[i] = (((Calendar)this.taskTimes.get(i)).get(1) + ":" + ((Calendar)this.taskTimes.get(i)).get(2) + ":" + ((Calendar)this.taskTimes.get(i)).get(5) + ":" + ((Calendar)this.taskTimes.get(i)).get(11) + ":" + ((Calendar)this.taskTimes.get(i)).get(12) + ":" + ((Calendar)this.taskTimes.get(i)).get(13) + ":" + this.interval);
      }
    }
    return temp;
  }
  
  public synchronized String addTaskTimeString(String tasktime)
  {
    String re = "Wrong format";
    Calendar date = Calendar.getInstance();
    String[] result = tasktime.trim().split(":");
    switch (result.length - 1)
    {
    case 1: 
      if (this.repeatType != RepeatType.HOUR) {
        return re;
      }
      break;
    case 2: 
      if (this.repeatType != RepeatType.DAILY) {
        return re;
      }
      break;
    case 3: 
      if (this.repeatType != RepeatType.MONTHLY) {
        return re;
      }
      break;
    case 5: 
      if (this.repeatType != RepeatType.ONCE) {
        return re;
      }
      break;
    case 6: 
      if (this.repeatType != RepeatType.PERIOD) {
        return re;
      }
      break;
    case 4: 
    default: 
      return re;
    }
    if (this.repeatType != RepeatType.PERIOD)
    {
      this.taskTimes.add(convertStringToDate(tasktime));
      boolean[] temp = new boolean[this.taskTimes.size()];
      for (int i = 0; i < temp.length; i++) {
        temp[i] = false;
      }
      for (int i = 0; i < this.taskDone.length; i++) {
        temp[i] = this.taskDone[i];
      }
      this.taskDone = temp;
    }
    else if (this.taskTimes.size() == 0)
    {
      this.taskTimes.add(convertStringToDate(tasktime));
    }
    else
    {
      return "Can't add more schedule";
    }
    return "OK";
  }
  
  public String getParam()
  {
    return this.param;
  }
  
  public void setParam(String param)
  {
    this.param = param;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getClassPath()
  {
    return this.classPath;
  }
  
  public void setClassPath(String classPath)
  {
    this.classPath = classPath;
  }
  
  public String toString(String indent)
  {
    StringBuilder sb = new StringBuilder("\n" + indent + "<taskinfo id=\"" + getId() + "\" type=\"" + getType() + "\">\n");
    

    sb.append(indent + "   <name>" + getName() + "</name>\n");
    sb.append(indent + "   <classPath>" + getClassPath() + "</classPath>\n");
    sb.append(indent + "   <jarPath>" + getJarPath() + "</jarPath>\n");
    sb.append(indent + "   <param>" + getParam() + "</param>\n");
    for (Calendar time : this.taskTimes)
    {
      sb.append(indent + "   <time>" + time.get(11) + ":" + time.get(12) + "</time>\n");
      if (this.repeatType == RepeatType.PERIOD) {
        sb.append(indent + "   <interval>" + this.interval + "</interval>\n");
      }
    }
    sb.append(indent + "</taskinfo>\n");
    return sb.toString();
  }
  
  public String toString()
  {
    return toString("");
  }
  
  public Calendar convertStringToDate(String aStr)
  {
    Calendar date = Calendar.getInstance();
    String[] result = aStr.trim().split(":");
    switch (result.length - 1)
    {
    case 1: 
      date.set(12, Integer.parseInt(result[0]));
      date.set(13, Integer.parseInt(result[1]));
      this.repeatType = RepeatType.HOUR;
      return date;
    case 2: 
      date.set(11, Integer.parseInt(result[0]));
      date.set(12, Integer.parseInt(result[1]));
      date.set(13, Integer.parseInt(result[2]));
      this.repeatType = RepeatType.DAILY;
      return date;
    case 3: 
      date.set(5, Integer.parseInt(result[0]));
      date.set(11, Integer.parseInt(result[1]));
      date.set(12, Integer.parseInt(result[2]));
      date.set(13, Integer.parseInt(result[3]));
      this.repeatType = RepeatType.MONTHLY;
      return date;
    case 5: 
      date.set(1, Integer.parseInt(result[0]));
      date.set(2, Integer.parseInt(result[1]) - 1);
      date.set(5, Integer.parseInt(result[2]));
      date.set(11, Integer.parseInt(result[3]));
      date.set(12, Integer.parseInt(result[4]));
      date.set(13, Integer.parseInt(result[5]));
      this.repeatType = RepeatType.ONCE;
      return date;
    case 6: 
      date.set(1, Integer.parseInt(result[0]));
      date.set(2, Integer.parseInt(result[1]) - 1);
      date.set(5, Integer.parseInt(result[2]));
      date.set(11, Integer.parseInt(result[3]));
      date.set(12, Integer.parseInt(result[4]));
      date.set(13, Integer.parseInt(result[5]));
      this.repeatType = RepeatType.PERIOD;
      this.interval = Integer.parseInt(result[6]);
      return date;
    }
    return null;
  }
  
  public boolean isTimeToRun()
  {
    Calendar temp;
    switch (this.repeatType.ordinal())
    {
    case 5: 
      temp = Calendar.getInstance();
      if ((this.lastRun == null) && (this.taskTimes.size() > 0))
      {
        Calendar cal = (Calendar)this.taskTimes.get(0);
        if (cal.compareTo(temp) <= 0)
        {
          long lCal = cal.getTimeInMillis();
          lCal += this.interval * (int)((temp.getTimeInMillis() - lCal) / this.interval);
          cal.setTimeInMillis(lCal);
          this.lastRun = ((Calendar)cal.clone());
          if (cal.compareTo(temp) == 0) {
            return true;
          }
        }
      }
      else if (this.lastRun != null)
      {
        Calendar cal = (Calendar)this.lastRun.clone();
        cal.add(14, this.interval);
        if (cal.compareTo(temp) <= 0)
        {
          this.lastRun = ((Calendar)temp.clone());
          if (this.runningState == RunningState.READY) {
            return true;
          }
        }
        return false;
      }
      break;
    case 3: 
      temp = Calendar.getInstance();
      if ((this.lastRun == null) || (this.lastRun.get(2) != temp.get(2)))
      {
        this.taskDone = new boolean[this.taskTimes.size()];
        for (boolean b : this.taskDone) {
          b = false;
        }
        this.lastRun = ((Calendar)temp.clone());
      }
      for (int i = 0; i < this.taskTimes.size(); i++) {
        if (this.taskDone[i])
        {
          Calendar cal = (Calendar)this.taskTimes.get(i);
          if ((cal.get(12) == temp.get(12)) && (cal.get(11) == temp.get(11)) && (cal.get(5) == temp.get(5))) {
            if (this.runningState == RunningState.READY)
            {
              this.taskDone[i] = true;
              this.lastRun = ((Calendar)temp.clone());
              return true;
            }
          }
        }
      }
      break;
    case 2: 
      temp = Calendar.getInstance();
      if ((this.lastRun == null) || (this.lastRun.get(5) != temp.get(5)))
      {
        this.taskDone = new boolean[this.taskTimes.size()];
        for (boolean b : this.taskDone) {
          b = false;
        }
        this.lastRun = ((Calendar)temp.clone());
      }
      for (int i = 0; i < this.taskTimes.size(); i++) {
        if (this.taskDone[i])
        {
          Calendar cal = (Calendar)this.taskTimes.get(i);
          if ((cal.get(12) == temp.get(12)) && (cal.get(11) == temp.get(11))) {
            if (this.runningState == RunningState.READY)
            {
              this.taskDone[i] = true;
              this.lastRun = ((Calendar)temp.clone());
              return true;
            }
          }
        }
      }
      break;
    case 1: 
      temp = Calendar.getInstance();
      if ((this.lastRun == null) || (this.lastRun.get(11) != temp.get(11)))
      {
        this.taskDone = new boolean[this.taskTimes.size()];
        for (boolean b : this.taskDone) {
          b = false;
        }
        this.lastRun = ((Calendar)temp.clone());
      }
      for (int i = 0; i < this.taskTimes.size(); i++) {
        if (this.taskDone[i])
        {
          Calendar cal = (Calendar)this.taskTimes.get(i);
          if (cal.get(12) == temp.get(12)) {
            if (this.runningState == RunningState.READY)
            {
              this.taskDone[i] = true;
              this.lastRun = ((Calendar)temp.clone());
              return true;
            }
          }
        }
      }
      break;
    case 4: 
      temp = Calendar.getInstance();
      if (this.lastRun == null)
      {
        this.taskDone = new boolean[this.taskTimes.size()];
        for (boolean b : this.taskDone) {
          b = false;
        }
        this.lastRun = ((Calendar)temp.clone());
      }
      for (int i = 0; i < this.taskTimes.size(); i++) {
        if (this.taskDone[i])
        {
          Calendar cal = (Calendar)this.taskTimes.get(i);
          cal.set(14, temp.get(14));
          cal.set(13, temp.get(13));
          if (cal.compareTo(temp) == 0) {
            if (this.runningState == RunningState.READY)
            {
              this.taskDone[i] = true;
              this.lastRun = ((Calendar)temp.clone());
              return true;
            }
          }
        }
      }
      break;
    }
    return false;
  }
  
  public RunningState getRunningState()
  {
    return this.runningState;
  }
  
  public void setRunningState(RunningState runningState)
  {
    this.runningState = runningState;
  }
  
  protected String getDescription(MBeanInfo info)
  {
    return "Mô tả thông tin về một task bao gồm danh sách thời điểm chạy, kiểu lặp, classpath...";
  }
  
  protected String getDescription(MBeanOperationInfo info)
  {
    MBeanParameterInfo[] params = info.getSignature();
    String[] signature = new String[params.length];
    for (int i = 0; i < params.length; i++) {
      signature[i] = params[i].getType();
    }
    String[] methodSignature = { String.class.getName() };
    if ((info.getName().equals("removeTaskTimeString")) && (Arrays.equals(signature, methodSignature))) {
      return "Xóa một thời điểm chạy khỏi danh sách thời điểm chạy";
    }
    if ((info.getName().equals("addTaskTimeString")) && (Arrays.equals(signature, methodSignature))) {
      switch (this.repeatType.ordinal())
      {
      case 5: 
        return "Thêm một lịch chạy vào danh sách lịch chạy (ví dụ yyyy:mm:dd:hh:mm:ss:interval)";
      case 2: 
        return "Thêm một lịch chạy vào danh sách lịch chạy (ví dụ hh:mm:ss)";
      case 3: 
        return "Thêm một lịch chạy vào danh sách lịch chạy (ví dụ dd:hh:mm:ss)";
      case 1: 
        return "Thêm một lịch chạy vào danh sách lịch chạy (ví dụ mm:ss)";
      case 4: 
        return "Thêm một lịch chạy vào danh sách lịch chạy (ví dụ yyyy:mm:dd:hh:mm:ss)";
      }
    }
    if (info.getName().equals("toString")) {
      return "Nội dung của Task";
    }
    return null;
  }
  
  protected String getDescription(MBeanOperationInfo op, MBeanParameterInfo param, int sequence)
  {
    if (op.getName().equals("addTaskTimeString"))
    {
      switch (sequence)
      {
      case 0: 
        return "Chuỗi ký tự mô tả một thời điểm để thêm vào";
      }
      return null;
    }
    if (op.getName().equals("removeTaskTimeString"))
    {
      switch (sequence)
      {
      case 0: 
        return "ID của task time";
      }
      return null;
    }
    return null;
  }
  
  protected String getParameterName(MBeanOperationInfo op, MBeanParameterInfo param, int sequence)
  {
    if (op.getName().equals("addTaskTimeString"))
    {
      switch (sequence)
      {
      case 0: 
        return "tasktime";
      }
      return null;
    }
    if (op.getName().equals("removeTaskTimeString"))
    {
      switch (sequence)
      {
      case 0: 
        return "tasktimeID";
      }
      return null;
    }
    return null;
  }
}

