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
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.spi.LoggingEvent;

public class SocketWriterManager
  extends AppenderThreadMX
{
  public static final int SLEEP_TIME = 1000;
  private List<LoggingEvent> offlineLogQueue = new LinkedList();
  private List<LoggingEvent> logQueue = new LinkedList();
  private final Object offlineQueueLock = new Object();
  private final Object queueLock = new Object();
  private int maxQueue = 1000;
  private int offlineMaxQueue = 100;
  private int maxConnection = 100;
  private boolean locationInfo = false;
  private int currentId = 0;
  private final Map<Integer, SocketWriter> swMap = Collections.synchronizedMap(new HashMap());
  private static SocketWriterManager swm;
  
  public int getMaxConnection()
  {
    return this.maxConnection;
  }
  
  public int getMaxQueue()
  {
    return this.maxQueue;
  }
  
  public int getOfflineMaxQueue()
  {
    return this.offlineMaxQueue;
  }
  
  public void setOfflineMaxQueue(int offlineMaxQueue)
  {
    this.offlineMaxQueue = offlineMaxQueue;
  }
  
  public void setMaxConnection(int maxConnection)
  {
    this.maxConnection = maxConnection;
  }
  
  public void setMaxQueue(int maxQueue)
  {
    this.maxQueue = maxQueue;
  }
  
  public void setLocationInfo(boolean ilocationInfo)
  {
    this.locationInfo = ilocationInfo;
  }
  
  public boolean getLocationInfo()
  {
    return this.locationInfo;
  }
  
  public List<LoggingEvent> getLogQueue()
  {
    return this.logQueue;
  }
  
  public List<LoggingEvent> getOfflineLogQueue()
  {
    return this.offlineLogQueue;
  }
  
  public Object getOfflineQueueLock()
  {
    return this.offlineQueueLock;
  }
  
  public Object getQueueLock()
  {
    return this.queueLock;
  }
  
  public static synchronized SocketWriterManager getInstance()
  {
    if (swm == null) {
      swm = new SocketWriterManager();
    }
    return swm;
  }
  
  private SocketWriterManager()
  {
    super("SocketWriterManager");
    try
    {
      super.registerAgent("Tools:type=log,name=SocketWriterManager");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  protected void process()
  {
    LoggingEvent event = null;
    synchronized (this.queueLock)
    {
      if (this.logQueue.size() > 0) {
        event = (LoggingEvent)this.logQueue.remove(0);
      }
    }
    if ((event != null) && (this.swMap.size() > 0))
    {
      if (this.locationInfo) {
        event.getLocationInformation();
      }
      Iterator iter;
      synchronized (this.swMap)
      {
        for (iter = this.swMap.values().iterator(); iter.hasNext();)
        {
          SocketWriter sw = (SocketWriter)iter.next();
          sw.addToQueue(event);
        }
      }
    }
    else
    {
      try
      {
        Thread.sleep(1000L);
      }
      catch (InterruptedException ex)
      {
        ex.printStackTrace();
      }
    }
  }
  
  public void stop()
  {
    try
    {
      super.stop();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public String getInfor()
  {
    int queueSize = 0;
    synchronized (this.queueLock)
    {
      queueSize = this.logQueue.size();
    }
    String info = "";
    info = "MaxQueue: " + this.maxQueue + ", currentQueueSize: " + queueSize;
    info = info + "\nOfflineMaxQueue: " + this.offlineMaxQueue;
    info = info + "\nMaxConnection: " + this.maxConnection + ", the number of connection: " + this.swMap.size();
    Iterator iter;
    synchronized (this.swMap)
    {
      for (iter = this.swMap.values().iterator(); iter.hasNext();)
      {
        SocketWriter sw = (SocketWriter)iter.next();
        info = info + "\n" + sw.getThreadName();
        info = info + "\n" + sw.getInfor();
      }
    }
    return super.getInfor() + "\n" + info;
  }
  
  public void addSocketWriter(Socket socket)
  {
    ObjectOutputStream oos = null;
    if (socket != null) {
      try
      {
        oos = new ObjectOutputStream(socket.getOutputStream());
        if (this.swMap.size() < this.maxConnection)
        {
          InetAddress remoteAddress = socket.getInetAddress();
          String clientDescription = remoteAddress.getHostName() + " (" + remoteAddress.getHostAddress() + ")";
          
          SocketWriter sw = new SocketWriter(oos, this.maxQueue, this.currentId, clientDescription);
          if (sw != null)
          {
            synchronized (this.offlineQueueLock)
            {
              sw.flushOfflineQueue(this.offlineLogQueue);
            }
            this.swMap.put(new Integer(this.currentId), sw);
            sw.setSwm(swm);
            sw.start();
            this.currentId += 1;
          }
        }
        else
        {
          socket.close();
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
        try
        {
          if (socket != null) {
            socket.close();
          }
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }
  }
  
  public void removeSocketWriter(int id)
  {
    if (id >= 0)
    {
      SocketWriter sw = (SocketWriter)this.swMap.get(new Integer(id));
      if (sw != null) {
        this.swMap.remove(new Integer(id));
      }
    }
  }
}

