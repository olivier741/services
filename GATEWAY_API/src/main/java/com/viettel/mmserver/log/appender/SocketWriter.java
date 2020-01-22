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
package com.viettel.mmserver.log.appender;

/**
 *
 * @author olivier.tatsinkou
 */
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.spi.LoggingEvent;

public class SocketWriter
  extends AppenderThreadMX
{
  public static final int SLEEP_TIME = 1000;
  private ObjectOutputStream oos;
  private int maxQueue = 1000;
  private List<LoggingEvent> logQueue = new LinkedList();
  private final Object queueLock = new Object();
  private String clientDescription = "";
  private long count = 0L;
  private SocketWriterManager swm;
  
  public void setSwm(SocketWriterManager swm)
  {
    this.swm = swm;
  }
  
  public SocketWriter(ObjectOutputStream oos, int maxQueue, int id, String clientDescription)
  {
    super("SocketWriter" + id);
    if ((oos != null) && (id >= 0))
    {
      this.oos = oos;
      setId(Integer.valueOf(id));
      if (maxQueue > 0) {
        this.maxQueue = maxQueue;
      }
      if ((clientDescription != null) && (!clientDescription.equals(""))) {
        this.clientDescription = clientDescription;
      }
    }
  }
  
  public void addToQueue(LoggingEvent event)
  {
    synchronized (this.queueLock)
    {
      if (this.logQueue.size() < this.maxQueue)
      {
        this.logQueue.add(event);
      }
      else
      {
        this.logQueue.remove(0);
        this.logQueue.add(event);
      }
    }
  }
  
  public String getInfor()
  {
    String info = "";
    info = "Client: " + this.clientDescription;
    synchronized (this.queueLock)
    {
      info = info + "\nCurrent queue size: " + this.logQueue.size();
    }
    info = info + "\nWriten logs: " + this.count;
    return info;
  }
  
  public void flushOfflineQueue(List<LoggingEvent> offlineLogQueue)
    throws IOException
  {
    if ((this.oos != null) && 
      (offlineLogQueue != null))
    {
      Iterator<LoggingEvent> it = offlineLogQueue.iterator();
      while (it.hasNext())
      {
        LoggingEvent event = (LoggingEvent)it.next();
        this.oos.writeObject(event);
      }
    }
  }
  
  protected void process()
  {
    LoggingEvent event = null;
    event = null;
    synchronized (this.queueLock)
    {
      if (this.logQueue.size() > 0) {
        event = (LoggingEvent)this.logQueue.remove(0);
      }
    }
    if ((event != null) && (this.oos != null)) {
      try
      {
        this.oos.writeObject(event);
        this.oos.flush();
        this.oos.reset();
        this.count = (++this.count % 9223372036854775807L);
      }
      catch (Throwable e)
      {
        this.swm.removeSocketWriter(getId().intValue());
        releaseResouce();
      }
    } else {
      try
      {
        Thread.sleep(1000L);
      }
      catch (Throwable ex)
      {
        System.out.println(ex);
      }
    }
  }
  
  public void releaseResouce()
  {
    this.running = false;
    this.logQueue = null;
    try
    {
      this.oos.close();
    }
    catch (Exception ex)
    {
      System.out.println(ex);
    }
  }
}

