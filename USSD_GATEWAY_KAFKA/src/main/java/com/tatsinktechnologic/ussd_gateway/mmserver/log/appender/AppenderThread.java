/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.log.appender;

/**
 *
 * @author olivier.tatsinkou
 */
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import org.apache.log4j.helpers.LogLog;

public abstract class AppenderThread
  implements Runnable
{
  public static final int JOINT_TIME = 100;
  public static final int RUNNING = 0;
  public static final int SAVE_RUN_TIME = 3;
  public static final int STOPPED = 1;
  public static final int STOPPING = 2;
  private LinkedList<Date> lastRunTimes = new LinkedList();
  private String threadName;
  private Integer id;
  protected boolean running;
  private int status;
  private String lastTime;
  private int priority;
  private Thread t;
  private final Object lock = new Object();
  private boolean dumpStatus;
  private Date buStartTime = null;
  
  protected AppenderThread()
  {
    this("Unknown");
  }
  
  public AppenderThread(String threadName)
  {
    this.threadName = threadName;
    this.priority = 5;
  }
  
  public void run()
  {
    setProcessStatus(0);
    LogLog.debug(this.threadName + " is starting to run");
    prepareStart();
    while (this.running) {
      try
      {
        saveRunTime(new Date());
        process();
        Thread.yield();
      }
      catch (RuntimeException ex)
      {
        LogLog.debug("catch runtime exception[" + ex.toString() + "]");
      }
    }
    LogLog.debug(this.threadName + " is dead");
    setProcessStatus(1);
  }
  
  public boolean isRunning()
  {
    return this.running;
  }
  
  public Thread.State getState()
  {
    return Thread.currentThread().getState();
  }
  
  public Integer getId()
  {
    return this.id;
  }
  
  public void setId(Integer id)
  {
    this.id = id;
  }
  
  public boolean isStopping()
  {
    return this.status == 2;
  }
  
  protected abstract void process();
  
  protected void prepareStart() {}
  
  protected void prepareStop() {}
  
  public void start()
  {
    if (!this.running)
    {
      this.lastTime = getTime();
      this.running = true;
      LogLog.debug("starting " + this.threadName + " process...");
      this.t = new Thread(this);
      this.t.setPriority(this.priority);
      this.t.setName(this.threadName);
      this.t.start();
      setProcessStatus(0);
      LogLog.debug(this.threadName + " process  is started");
    }
    else
    {
      LogLog.debug(this.threadName + " process  is started");
    }
  }
  
  private String getTime()
  {
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    return formatter.format(new Date());
  }
  
  public void stop()
  {
    if (this.running)
    {
      setProcessStatus(2);
      
      this.lastTime = getTime();
      
      prepareStop();
      LogLog.debug("stop " + this.threadName + " process");
      this.running = false;
      synchronized (this.lock)
      {
        this.lock.notifyAll();
      }
      if (this.t != null) {
        this.t.interrupt();
      }
      try
      {
        if ((this.t != null) && (this.t.isAlive()))
        {
          LogLog.debug("waiting " + this.threadName + " process stop...");
          
          this.t.join(100L);
        }
      }
      catch (InterruptedException ex)
      {
        LogLog.debug("stop process exception:" + ex);
      }
      finally
      {
        LogLog.debug(this.threadName + " process is stopped");
      }
      setProcessStatus(1);
    }
  }
  
  public void restart()
  {
    stop();
    
    start();
  }
  
  public void destroy()
  {
    stop();
  }
  
  public String getThreadName()
  {
    return this.threadName;
  }
  
  public void setThreadName(String threadName)
  {
    this.threadName = threadName;
  }
  
  protected void setProcessStatus(int status)
  {
    this.status = status;
  }
  
  protected int getProcessStatus()
  {
    return this.status;
  }
  
  public int getPriority()
  {
    return this.priority;
  }
  
  public void setPriority(int priority)
  {
    this.priority = priority;
  }
  
  public void setDumpThread(boolean dumpStatus)
  {
    this.dumpStatus = dumpStatus;
  }
  
  public String getInfor()
  {
    StringBuffer buff = new StringBuffer();
    buff.append("Thread Name:" + this.threadName);
    buff.append("\r\nLast Time:" + this.lastTime);
    buff.append("\r\nStatus:" + getStatusDesc());
    buff.append("\r\nPriority:" + this.priority);
    
    buff.append(System.getProperty("line.separator") + "Recently run process(): ");
    SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    for (Date d : this.lastRunTimes)
    {
      buff.append(System.getProperty("line.separator"));
      buff.append("     ");
      buff.append(f.format(d));
    }
    return buff.toString();
  }
  
  public String getStatusDesc()
  {
    switch (this.status)
    {
    case 0: 
      return "running";
    case 1: 
      return "stopped";
    }
    return "stopping";
  }
  
  public String[] getConfigs()
  {
    return new String[0];
  }
  
  private void saveRunTime(Date d)
  {
    if (this.lastRunTimes.size() < 3)
    {
      this.lastRunTimes.add(d);
    }
    else
    {
      this.lastRunTimes.removeFirst();
      this.lastRunTimes.add(d);
    }
  }
  
  public long ping()
  {
    if ((this.lastRunTimes == null) || (this.lastRunTimes.size() == 0)) {
      return 0L;
    }
    return new Date().getTime() - ((Date)this.lastRunTimes.getLast()).getTime();
  }
}
