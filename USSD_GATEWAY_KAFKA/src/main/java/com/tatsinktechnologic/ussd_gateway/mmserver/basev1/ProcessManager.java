/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.basev1;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.mmserver.agent.MMbeanServer;
import com.tatsinktechnologic.ussd_gateway.mmserver.base.Log;
import java.util.Hashtable;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

public class ProcessManager
  extends StandardMBean
  implements ProcessManagerMBean
{
  private static ProcessManager instance;
  private final Hashtable<Integer, MmProcess> mMmProcesses;
  
  private ProcessManager()
    throws NotCompliantMBeanException
  {
    super(ProcessManagerMBean.class);
    this.mMmProcesses = new Hashtable();
    try
    {
      MMbeanServer.getInstance().registerMBean(this, new ObjectName("MMLib:name=ProcessManager"));
    }
    catch (Exception ex)
    {
      Log.warn("Register JMX error", ex);
    }
  }
  
  public static ProcessManager getInstance()
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
  
  public void addMmProcess(MmProcess process)
  {
    if (process != null) {
      synchronized (this.mMmProcesses)
      {
        this.mMmProcesses.put(process.getId(), process);
      }
    }
  }
  
  public MmProcess getMmProcess(Integer pid)
  {
    synchronized (this.mMmProcesses)
    {
      return (MmProcess)this.mMmProcesses.get(pid);
    }
  }
  
  public void kill(Integer pid)
  {
    MmProcess process = getMmProcess(pid);
    if (process != null)
    {
      process.stop();
      removeProcess(pid);
    }
  }
  
  public void cleanup()
  {
    for (MmProcess process : this.mMmProcesses.values()) {
      if (process.getState() == Thread.State.TERMINATED) {
        removeProcess(process.getId());
      }
    }
    this.mMmProcesses.clear();
  }
  
  public Hashtable<Integer, MmProcess> getMmProcess()
  {
    return this.mMmProcesses;
  }
  
  public String getProcessState(Integer pid)
  {
    MmProcess process = getMmProcess(pid);
    if (process != null) {
      return process.toString();
    }
    return "process with pid " + pid + " not exist";
  }
  
  public String listProcess()
  {
    StringBuilder sb = new StringBuilder("\n[MmProcess List]");
    for (MmProcess process : this.mMmProcesses.values()) {
      sb.append("\n" + process.toString());
    }
    sb.append("\n[End List]");
    
    return sb.toString();
  }
  
  public void suspend(Integer pid)
  {
    MmProcess process = getMmProcess(pid);
    if (process != null) {
      process.suspend();
    }
  }
  
  public void unManageProcess(Integer pid)
  {
    synchronized (this.mMmProcesses)
    {
      this.mMmProcesses.remove(pid);
    }
  }
  
  private void removeProcess(Integer pid)
  {
    synchronized (this.mMmProcesses)
    {
      MmProcess process = (MmProcess)this.mMmProcesses.get(pid);
      if (process != null)
      {
        process.stop();
        process = null;
      }
      this.mMmProcesses.remove(pid);
    }
  }
  
  public void resume(Integer pid)
  {
    MmProcess process = getMmProcess(pid);
    if (process != null) {
      process.suspend();
    }
  }
  
  public void stop(Integer pid)
  {
    removeProcess(pid);
  }
  
  public void stopAll()
  {
    Integer[] ids = (Integer[])this.mMmProcesses.keySet().toArray(new Integer[this.mMmProcesses.size()]);
    for (Integer i : ids)
    {
      MmProcess process = (MmProcess)this.mMmProcesses.get(i);
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
