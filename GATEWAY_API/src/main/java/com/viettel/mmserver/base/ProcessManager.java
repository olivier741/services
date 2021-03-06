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
package com.viettel.mmserver.base;

/**
 *
 * @author olivier.tatsinkou
 */
import com.viettel.mmserver.agent.MMbeanServer;
import java.util.Hashtable;
import java.util.Set;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

public class ProcessManager
  extends StandardMBean
  implements ProcessManagerMBean
{
  private static ProcessManager instance;
  private final Hashtable<Integer, ProcessThread> mmProcesses;
  
  private ProcessManager()
    throws NotCompliantMBeanException
  {
    super(ProcessManagerMBean.class);
    this.mmProcesses = new Hashtable();
    try
    {
      MMbeanServer.getInstance().registerMBean(this, new ObjectName("Tools:name=ProcessManager"));
    }
    catch (Exception ex)
    {
      Log.warn("Register JMX error", ex);
    }
  }
  
  public static synchronized ProcessManager getInstance()
  {
    try
    {
      if (instance == null) {
        instance = new ProcessManager();
      }
      return instance;
    }
    catch (NotCompliantMBeanException e)
    {
      throw new RuntimeException("critical error when init ProcessManager!");
    }
  }
  
  public void addMmProcess(ProcessThread process)
  {
    if (process != null) {
      synchronized (this.mmProcesses)
      {
        this.mmProcesses.put(process.getId(), process);
      }
    }
  }
  
  public ProcessThread getMmProcess(Integer pid)
  {
    synchronized (this.mmProcesses)
    {
      return (ProcessThread)this.mmProcesses.get(pid);
    }
  }
  
  public void kill(Integer pid)
  {
    synchronized (this.mmProcesses)
    {
      ProcessThread process = (ProcessThread)this.mmProcesses.get(pid);
      if (process != null)
      {
        process.stop();
        process = null;
      }
      this.mmProcesses.remove(pid);
    }
  }
  
  public void cleanup()
  {
    for (ProcessThread process : this.mmProcesses.values()) {
      if (process.getState() == Thread.State.TERMINATED) {
        stop(process.getId());
      }
    }
    this.mmProcesses.clear();
  }
  
  public Hashtable<Integer, ProcessThread> getMmProcess()
  {
    return this.mmProcesses;
  }
  
  public String getProcessState(Integer pid)
  {
    ProcessThread process = getMmProcess(pid);
    if (process != null) {
      return process.toString();
    }
    return "process with pid " + pid + " not exist";
  }
  
  public String listProcess()
  {
    StringBuilder sb = new StringBuilder("\n[MmProcess List]");
    for (ProcessThread process : this.mmProcesses.values()) {
      sb.append("\n" + process.toString());
    }
    sb.append("\n[End List]");
    
    return sb.toString();
  }
  
  public void unManageProcess(Integer pid)
  {
    synchronized (this.mmProcesses)
    {
      this.mmProcesses.remove(pid);
    }
  }
  
  public void stop(Integer pid)
  {
    synchronized (this.mmProcesses)
    {
      ProcessThread process = (ProcessThread)this.mmProcesses.get(pid);
      if (process != null)
      {
        process.stop();
        process = null;
      }
    }
  }
  
  public void stopAll()
  {
    Integer[] ids = (Integer[])this.mmProcesses.keySet().toArray(new Integer[this.mmProcesses.size()]);
    for (Integer i : ids)
    {
      ProcessThread process = (ProcessThread)this.mmProcesses.get(i);
      if (process != null)
      {
        process.stop();
        process = null;
      }
    }
  }
  
  protected String getDescription(MBeanInfo info)
  {
    return "Tiến trình quản lý các Mm Process";
  }
  
  protected String getDescription(MBeanOperationInfo info)
  {
    MBeanParameterInfo[] params = info.getSignature();
    String[] signature = new String[params.length];
    for (int i = 0; i < params.length; i++) {
      signature[i] = params[i].getType();
    }
    if (info.getName().equals("cleanup")) {
      return "cleanup deadth process on system";
    }
    if (info.getName().equals("getProcessState")) {
      return "Trạng thái của internal thread";
    }
    if (info.getName().equals("kill")) {
      return "Hủy tiến trình MmProcess, yêu cần tiến trình dừng, nếu hết thời gian timeout, sẽ kill";
    }
    if (info.getName().equals("stop")) {
      return "Stop tiến trình MmProcess";
    }
    if (info.getName().equals("stopAll")) {
      return "Dừng tất cả các tiến trình xử lý nghiệp vụ";
    }
    if (info.getName().equals("resume")) {
      return "Tiếp tục chạy tiến trình nếu đang suspend";
    }
    if (info.getName().equals("suspend")) {
      return "Suspend tiến trình";
    }
    if (info.getName().equals("listProcess")) {
      return "Liệt kê trạng thái các tiến trình MmProcess trong hệ thống";
    }
    return null;
  }
  
  protected String getDescription(MBeanOperationInfo op, MBeanParameterInfo param, int sequence)
  {
    if ((op.getName().equals("getProcessState")) || (op.getName().equals("kill")) || (op.getName().equals("listProcess")) || (op.getName().equals("resume")) || (op.getName().equals("stop")) || (op.getName().equals("suspend")) || (op.getName().equals("start")))
    {
      switch (sequence)
      {
      case 0: 
        return "Pid của MmProcess cần thao tác";
      }
      return null;
    }
    return null;
  }
  
  protected String getParameterName(MBeanOperationInfo op, MBeanParameterInfo param, int sequence)
  {
    if ((op.getName().equals("getProcessState")) || (op.getName().equals("kill")) || (op.getName().equals("listProcess")) || (op.getName().equals("resume")) || (op.getName().equals("stop")) || (op.getName().equals("suspend")) || (op.getName().equals("start")))
    {
      switch (sequence)
      {
      case 0: 
        return "process id";
      }
      return null;
    }
    return null;
  }
}

