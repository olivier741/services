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
import java.io.PrintStream;
import java.util.List;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class AdvanceSocketHubAppender
  extends AppenderSkeleton
{
  static final int DEFAULT_PORT = 4560;
  static final int DEFAULT_OFFLINE_MAX_QUEUE = 100;
  static final int DEFAULT_MAX_QUEUE = 1000;
  static final int DEFAULT_MAX_CONNECTION = 100;
  private int port = 4560;
  private int maxConnection = 100;
  private int maxQueue = 1000;
  private int offlineMaxQueue = 100;
  private ServerMonitor serverMonitor;
  private boolean locationInfo = false;
  private SocketWriterManager swm;
  private static final String INIT_MMSERVER_BY_LOG4J = "com.viettel.mmserver.mbeanserver.initbylog4j";
  
  public AdvanceSocketHubAppender() {}
  
  public AdvanceSocketHubAppender(int port)
  {
    this.port = port;
    start();
  }
  
  public void activateOptions()
  {
    start();
  }
  
  private void start()
  {
    this.serverMonitor = new ServerMonitor(this.port);
    this.serverMonitor.start();
    this.swm = SocketWriterManager.getInstance();
    this.serverMonitor.setSwm(this.swm);
    this.swm.setMaxConnection(this.maxConnection);
    this.swm.setMaxQueue(this.maxQueue);
    this.swm.setOfflineMaxQueue(this.offlineMaxQueue);
    this.swm.setLocationInfo(this.locationInfo);
    this.swm.start();
  }
  
  public synchronized void close()
  {
    if (this.closed) {
      return;
    }
    this.closed = true;
    cleanUp();
  }
  
  public void cleanUp()
  {
    try
    {
      System.out.println("Call clean up");
      System.out.println("Unregister Mbean");
      this.serverMonitor.unregisterAgent();
      this.serverMonitor.stop();
      this.serverMonitor = null;
      this.swm.stop();
      this.swm = null;
    }
    catch (MalformedObjectNameException ex)
    {
      ex.printStackTrace();
    }
    catch (InstanceNotFoundException ex)
    {
      ex.printStackTrace();
    }
    catch (MBeanRegistrationException ex)
    {
      ex.printStackTrace();
    }
  }
  
  public void addToOffilineQueue(LoggingEvent event)
  {
    synchronized (this.swm.getOfflineQueueLock())
    {
      if (this.swm.getOfflineLogQueue().size() < this.offlineMaxQueue)
      {
        this.swm.getOfflineLogQueue().add(event);
      }
      else
      {
        this.swm.getOfflineLogQueue().remove(0);
        this.swm.getOfflineLogQueue().add(event);
      }
    }
  }
  
  public void addToQueue(LoggingEvent event)
  {
    synchronized (this.swm.getQueueLock())
    {
      if (this.swm.getLogQueue().size() < this.maxQueue)
      {
        this.swm.getLogQueue().add(event);
      }
      else
      {
        this.swm.getLogQueue().remove(0);
        this.swm.getLogQueue().add(event);
      }
    }
  }
  
  public void append(LoggingEvent event)
  {
    if (event != null)
    {
      addToOffilineQueue(event);
      addToQueue(event);
    }
  }
  
  public boolean requiresLayout()
  {
    return false;
  }
  
  public int getOfflineMaxQueue()
  {
    return this.offlineMaxQueue;
  }
  
  public void setOfflineMaxQueue(int maxqueue)
  {
    this.offlineMaxQueue = maxqueue;
  }
  
  public int getMaxQueue()
  {
    return this.maxQueue;
  }
  
  public void setMaxQueue(int maxqueue)
  {
    this.maxQueue = maxqueue;
  }
  
  public void setPort(int port)
  {
    this.port = port;
  }
  
  public int getPort()
  {
    return this.port;
  }
  
  public int getMaxConnection()
  {
    return this.maxConnection;
  }
  
  public void setMaxConnection(int maxConnection)
  {
    this.maxConnection = maxConnection;
  }
  
  public boolean isLocationInfo()
  {
    return this.locationInfo;
  }
  
  public void setLocationInfo(boolean locationInfo)
  {
    this.locationInfo = locationInfo;
  }
}
