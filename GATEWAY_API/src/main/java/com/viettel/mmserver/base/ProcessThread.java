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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import org.apache.log4j.Logger;

public abstract class ProcessThread
  implements Runnable
{
  private static int allId = 0;
  public static final int RUNNING = 0;
  public static final int STOPPED = 1;
  public static final int STOPPING = 2;
  private LinkedList<Date> lastRunTimes = new LinkedList();
  protected Integer id;
  protected String threadName;
  protected boolean running;
  protected int status = 1;
  protected String lastTime;
  protected int priority;
  protected Thread t;
  protected final Object lock = new Object();
  protected Logger logger;
  protected boolean dumpStatus;
  private Date originalDate;
  protected Date buStartTime = null;
  public static final int NUM_OF_SAVE_RUNTIME = 3;
  
  protected ProcessThread()
  {
    this("Unknown");
  }
  
  public ProcessThread(String threadName)
  {
    this.threadName = threadName;
    allId += 1;
    this.id = Integer.valueOf(allId);
    ProcessManager.getInstance().addMmProcess(this);
    this.logger = Logger.getLogger(threadName);
    this.priority = 5;
    try
    {
      this.originalDate = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2000");
      this.buStartTime = this.originalDate;
    }
    catch (ParseException ex)
    {
      this.logger.info("Can not create original date");
    }
  }
  
  public void run()
  {
    setProcessStatus(0);
    this.logger.info(this.threadName + " is starting to run");
    prepareStart();
    while (this.running) {
      try
      {
        saveRunTime(new Date());
        process();
        if (this.dumpStatus) {
          this.logger.debug(this.threadName + "_" + Thread.currentThread().getName() + " is running");
        }
        Thread.yield();
      }
      catch (RuntimeException ex)
      {
        ex.printStackTrace();
        this.logger.error("catch runtime exception[" + ex.toString() + "]");
      }
    }
    this.logger.info(this.threadName + " is dead");
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
      this.logger.info("starting " + this.threadName + " process...");
      this.t = new Thread(this);
      this.t.setPriority(this.priority);
      this.t.setName(this.threadName);
      this.t.start();
      setProcessStatus(0);
      this.logger.info(this.threadName + " process  is started");
    }
    else
    {
      this.logger.info(this.threadName + " process  is started");
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
      if ((this.buStartTime != null) && (!this.buStartTime.equals(this.originalDate))) {
        this.buStartTime = null;
      }
      this.lastTime = getTime();
      prepareStop();
      this.logger.info("stop " + this.threadName + " process");
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
          this.logger.info("waiting " + this.threadName + " process stop...");
          this.t.join();
        }
      }
      catch (InterruptedException ex)
      {
        this.logger.error("stop process exception:" + ex);
      }
      finally
      {
        this.logger.info(this.threadName + " process is stopped");
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
    this.logger = Logger.getLogger(threadName);
  }
  
  protected void setProcessStatus(int status)
  {
    this.status = status;
  }
  
  protected int getProcessStatus()
  {
    return this.status;
  }
  
  protected String loadLoggerName()
  {
    String loggerName = "";
    if (this.logger != null) {
      loggerName = this.logger.getName();
    }
    return loggerName;
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
    this.logger.info("set dump thread is " + dumpStatus);
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
  
  public void setLogger(Logger logger)
  {
    this.logger = logger;
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
    Date temp = this.buStartTime;
    if (temp == null) {
      return 0L;
    }
    if (temp.equals(this.originalDate)) {
      return -1L;
    }
    return new Date().getTime() - temp.getTime();
  }
  
  public boolean pingable()
  {
    Date temp = this.buStartTime;
    return (temp == null) || (!temp.equals(this.originalDate));
  }
}
