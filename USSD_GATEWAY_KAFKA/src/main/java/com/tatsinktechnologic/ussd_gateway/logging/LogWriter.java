/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.logging;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.mmserver.base.ProcessThreadMX;
import com.tatsinktechnologic.ussd_gateway.utils.BlockQueue;
import java.util.Vector;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;


public class LogWriter
  extends ProcessThreadMX
{
  private String configFile;
  private int maxCacheSize;
  private int maxTimeToFlush;
  private int maxMsgLine;
  private String seperator;
  private String logPath;
  private String prefix;
  private String subPath;
  private int currentLine;
  private int currentSize;
  private volatile long startTime;
  private Vector<String> cache;
  private volatile boolean isActive;
  private PropertyConfigFile properties;
  private BlockQueue cacheQueue;
  private int maxNumCaches;
  private FileWrittingThread[] writeFileThreads;
  
  public LogWriter(String configFileName, String subPath, String prefix)
  {
    super("LogWriter-" + prefix, "Thread write log to folder \".." + subPath + "\", log file prefix is \"" + prefix + "\"");
    

    this.configFile = configFileName;
    this.subPath = subPath;
    this.prefix = prefix;
    

    this.logger.info("init logwriter");
    init();
    
    this.logger.info("regist agent");
    try
    {
      registerAgent("logging:type=LogWriter, name=" + this.threadName);
    }
    catch (Exception ex)
    {
      this.logger.error("Register JMX agent error: " + ex.getMessage());
    }
  }
  
  public String getSeperator()
  {
    return this.seperator;
  }
  
  private void init()
  {
    this.logger.info("load params from config file");
    loadConfigFile();
    
    this.cacheQueue = new BlockQueue(0, this.maxNumCaches);
    

    this.logger.info("make threads to write log to file");
    int numThread = Runtime.getRuntime().availableProcessors();
    this.logger.info("num threads: " + numThread);
    this.writeFileThreads = new FileWrittingThread[numThread];
    for (int i = 0; i < numThread; i++)
    {
      this.writeFileThreads[i] = new FileWrittingThread(this.prefix + i, this.logPath + this.subPath, this.prefix);
      this.writeFileThreads[i].setQueue(this.cacheQueue);
    }
    this.logger.info("make new cache");
    initNewCache();
  }
  
  private void initNewCache()
  {
    this.cache = new Vector(this.maxMsgLine);
    this.currentLine = 0;
    this.currentSize = 0;
    this.startTime = System.currentTimeMillis();
  }
  
  public synchronized void writeLn(String data)
  {
    if (!this.isActive) {
      return;
    }
    this.cache.add(data);
    

    this.currentLine += 1;
    this.currentSize += data.length();
    if ((this.currentLine >= this.maxMsgLine) || (this.currentSize >= this.maxCacheSize))
    {
      pushCacheToQueue();
      
      initNewCache();
    }
  }
  
  private void pushCacheToQueue()
  {
    try
    {
      this.cacheQueue.enqueue(this.cache);
    }
    catch (IndexOutOfBoundsException ex)
    {
      this.logger.error("cache queue is full. Missing one block log");
    }
  }
  
  private synchronized void flushImmediately()
  {
    if (this.currentLine > 0)
    {
      pushCacheToQueue();
      initNewCache();
    }
    else
    {
      this.startTime = System.currentTimeMillis();
    }
  }
  
  private void initDefault()
  {
    this.maxCacheSize = 10000000;
    this.logger.info("max cache size(Byte) = " + this.maxCacheSize);
    this.maxNumCaches = 1000;
    this.logger.info("max num cache = " + this.maxNumCaches);
    this.maxMsgLine = 1000;
    this.logger.info("max message line = " + this.maxMsgLine);
    this.maxTimeToFlush = 600000;
    this.logger.info("time to flush(ms) = " + this.maxTimeToFlush);
    this.seperator = "@@@";
    this.logger.info("separator = " + this.seperator);
    this.logPath = "../log/db_log";
    this.logger.info("log path = " + this.logPath);
    this.isActive = true;
    this.logger.info("logwriter is active mode");
  }
  
  private void loadConfigFile()
  {
    this.logger.info("config file: " + this.configFile);
    try
    {
      this.properties = new PropertyConfigFile(this.configFile);
    }
    catch (Exception ex)
    {
      this.logger.error("load config file error, use default setting: " + ex.getMessage());
      initDefault();
      return;
    }
    this.logger.info("get max cache size");
    String mcs = this.properties.getParam("max_cache_size", "10000000");
    this.maxCacheSize = Integer.parseInt(mcs);
    this.logger.info("max cache size(Byte) = " + this.maxCacheSize);
    
    this.logger.info("get max num caches");
    this.maxNumCaches = Integer.parseInt(this.properties.getParam("max_num_cache", "1000"));
    this.logger.info("max num cache = " + this.maxNumCaches);
    

    this.logger.info("get max line");
    String ml = this.properties.getParam("max_lines", "1000");
    this.maxMsgLine = Integer.parseInt(ml);
    this.logger.info("max message line = " + this.maxMsgLine);
    
    this.logger.info("get timeout to flush");
    String fc = this.properties.getParam("flush_cycle", "10");
    this.maxTimeToFlush = (Integer.parseInt(fc) * 60000);
    this.logger.info("time to flush(ms) = " + this.maxTimeToFlush);
    
    this.logger.info("get log path");
    this.logPath = this.properties.getParam("logpath", "../log/db_log");
    this.logger.info("log path = " + this.logPath);
    
    this.logger.info("get separator");
    this.seperator = this.properties.getParam("seperator", "@@@");
    this.logger.info("separator = " + this.seperator);
    
    this.logger.info("check logger active mode");
    String iActive = this.properties.getParam("is_active", "true");
    if (iActive.equalsIgnoreCase("true"))
    {
      this.isActive = true;
      this.logger.info("LogWriter is actived");
    }
    else
    {
      this.isActive = false;
      this.logger.info("LogWriter is not active, all writen operation will be ignored");
    }
  }
  
  public void start()
  {
    this.logger.info("start thread timing");
    super.start();
    
    this.logger.info("start thread write log to file");
    for (FileWrittingThread threads : this.writeFileThreads) {
      threads.start();
    }
  }
  
  public void stop()
  {
    this.logger.info("flush current log to file");
    flushWhenStop();
    this.logger.info("stop thread timing");
    super.stop();
    this.logger.info("wait for log blocks write file complete");
    while (this.cacheQueue.size() > 0) {
      try
      {
        Thread.sleep(100L);
      }
      catch (InterruptedException ex) {}
    }
    this.logger.info("stop thread write log to file");
    for (FileWrittingThread threads : this.writeFileThreads) {
      threads.stop();
    }
  }
  
  protected void process()
  {
    try
    {
      long intervalTime = System.currentTimeMillis() - this.startTime;
      if (intervalTime >= this.maxTimeToFlush) {
        flushWhenTimeout();
      } else {
        Thread.sleep(this.maxTimeToFlush - intervalTime + 500L);
      }
    }
    catch (InterruptedException ex) {}
  }
  
  private synchronized void flushWhenTimeout()
  {
    long intervalTime = System.currentTimeMillis() - this.startTime;
    if (intervalTime >= this.maxTimeToFlush) {
      flushImmediately();
    }
  }
  
  private synchronized void flushWhenStop()
  {
    this.isActive = false;
    flushImmediately();
  }
  
  public Object invoke(String operationName, Object[] params, String[] signature)
    throws MBeanException, ReflectionException
  {
    if (operationName == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"), "Cannot invoke a null operation in " + this.dClassName);
    }
    if (operationName.equals("flushImmediately")) {
      flushImmediately();
    } else {
      return super.invoke(operationName, params, signature);
    }
    return null;
  }
  
  protected MBeanOperationInfo[] buildOperations()
  {
    MBeanOperationInfo[] mbInfors = super.buildOperations();
    MBeanOperationInfo[] mbNewInfors = new MBeanOperationInfo[mbInfors.length + 1];
    System.arraycopy(mbInfors, 0, mbNewInfors, 0, mbInfors.length);
    
    MBeanParameterInfo[] none = new MBeanParameterInfo[0];

    mbNewInfors[mbInfors.length] = new MBeanOperationInfo("flushImmediately", "flush all log in buffer to file immediately", none, "void", 0);
 
    return mbNewInfors;
  }
  
  public synchronized String getLoadInfo()
  {
    StringBuilder buf = new StringBuilder();
    buf.append("line in cache: ");
    buf.append(this.currentLine);
    buf.append("\r\nSize in cache (byte): ");
    buf.append(this.currentSize);
    buf.append("\r\nNum block in queue: ");
    buf.append(this.cacheQueue.size());
    
    return buf.toString();
  }
}

