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
package com.viettel.bccsgw.logging;

/**
 *
 * @author olivier.tatsinkou
 */
import com.viettel.bccsgw.utils.BlockQueue;
import com.viettel.bccsgw.utils.Queue;
import com.viettel.mmserver.base.ProcessThreadMX;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Properties;
import java.util.Vector;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;

public class LogWriter
  extends ProcessThreadMX
{
  private String CONFIG_FILE_NAME;
  private long maxCacheSize;
  private int maxTimeToFlush;
  private long maxMsgLine;
  public static String seperator;
  private String logPath;
  private String prefix;
  private String subPath;
  private static int threadIndex = 0;
  private File file;
  private FileOutputStream fos;
  private String orgFileName;
  private String fileName;
  private long currentLine;
  private long currentSize;
  private long intervalTime;
  private long startTime;
  private String lastScanTime;
  private Vector<byte[]> blockCache;
  private String logInfo = null;
  boolean isAbort = false;
  boolean isActive = true;
  private Properties properties;
  private Queue bufferQueue;
  private String replacedStringForEndLine;
  private static final String END_LINE_REPRESENTATIVE = " ";
  private String replacedStringForTab;
  private static final String TAB_REPRESENTATIVE = " ";
  
  public LogWriter(String configFileName, String subPath, String prefix)
  {
    super("LogWriter[" + threadIndex + "]", "Thread write log to folder \".." + subPath + "\", log file prefix is \"" + prefix + "\"");
    

    this.CONFIG_FILE_NAME = configFileName;
    this.subPath = subPath;
    this.prefix = prefix;
    
    init();
    try
    {
      registerAgent("logging:type=LogWriter, name=" + this.threadName);
    }
    catch (Exception ex)
    {
      this.logger.error("Register JMX agent error: " + ex);
    }
    LogWriterThreadMrg.getInstance().addThread(this);
    

    start();
    
    threadIndex += 1;
  }
  
  private void init()
  {
    this.maxCacheSize = 100000000L;
    
    this.maxTimeToFlush = 30000;
    this.maxMsgLine = 10000L;
    seperator = "@@@";
    
    this.logPath = ("log/db_log" + this.subPath);
    this.orgFileName = (this.logPath + File.separator + "prefix");
    
    this.currentLine = 0L;
    this.currentSize = 0L;
    this.intervalTime = 0L;
    this.startTime = 0L;
    this.lastScanTime = getCurrentTime();
    

    this.blockCache = new Vector();
    


    this.bufferQueue = new BlockQueue(1000, 100000);
    try
    {
      loadConfigFile();
      
      this.logPath += this.subPath;
      this.orgFileName = (this.logPath + File.separator + this.prefix);
      File dir = new File(this.logPath);
      if (!dir.exists()) {
        dir.mkdirs();
      }
      this.startTime = System.currentTimeMillis();
    }
    catch (Throwable e)
    {
      this.logger.warn(e);
    }
  }
  
  public synchronized void writeLn(String sep, String data)
  {
    String formatData = "";
    String[] datas = data.split(",");
    for (int i = 0; i < datas.length; i++)
    {
      String aData = datas[i].trim();
      if (i == 0) {
        formatData = formatData + aData;
      } else {
        formatData = formatData + sep + aData;
      }
    }
    writeLn(formatData);
  }
  
  public void writeLn(String data)
  {
    if (!this.isActive) {
      return;
    }
    try
    {
      this.bufferQueue.enqueue(data);
    }
    catch (Exception ex)
    {
      this.logger.error("queue is full " + ex);
      return;
    }
  }
  
  public void writeLn(StringBuffer data)
  {
    writeLn(data.toString());
  }
  
  public static synchronized void writeLn(String dir, String fileName, String content, boolean append)
  {
    SecurityFileChannel sfchannel = SecurityFileChannel.getInstance();
    try
    {
      sfchannel.writeLn(dir, fileName, content);
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }
  
  private boolean isCacheFull()
  {
    if (this.currentLine >= this.maxMsgLine) {
      return true;
    }
    if ((this.currentLine > 0L) && (this.currentSize > this.maxCacheSize)) {
      return true;
    }
    return false;
  }
  
  private boolean hasBlockCache()
  {
    return !this.blockCache.isEmpty();
  }
  
  private synchronized void flush()
    throws IOException
  {
    if (this.blockCache.isEmpty()) {
      return;
    }
    try
    {
      createNewFile();
      
      long st = System.currentTimeMillis();
      
      FileChannel fchannel = new RandomAccessFile(this.file, "rws").getChannel();
      
      FileLock flock = fchannel.lock();
      for (int i = 0; i < this.blockCache.size(); i++)
      {
        ByteBuffer bf = ByteBuffer.wrap((byte[])this.blockCache.elementAt(i));
        fchannel.write(bf, this.file.length());
      }
      this.blockCache.clear();
      this.logger.info("created a log file: " + this.fileName);
      flock.release();
      fchannel.close();
      long et = System.currentTimeMillis();
      this.logger.debug("time for write data from cache to file: " + (et - st) + " miliseconds");
    }
    catch (IOException ex)
    {
      this.logger.error("Error when flush data from cache to file: " + ex);
      
      File dir = new File(this.logPath);
      if (!dir.exists()) {
        dir.mkdirs();
      }
      throw ex;
    }
    finally
    {
      if (this.fos != null)
      {
        this.fos.close();
        this.fos = null;
      }
      try
      {
        this.logger.info("rename to " + this.fileName + ".log");
        File logFile = new File(this.fileName + ".log");
        boolean isRenamed = this.file.renameTo(logFile);
        if (!isRenamed) {
          this.logger.error("Can't rename file " + this.fileName);
        }
      }
      catch (Exception ex)
      {
        this.logger.error("Error when rename file " + this.fileName + ". " + ex.getMessage());
      }
    }
  }
  
  private void createNewFile()
  {
    File dir = new File(this.logPath);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    String ext = getTimeString();
    this.fileName = (this.orgFileName + "." + ext);
    if ((this.orgFileName.equalsIgnoreCase("")) || (this.orgFileName == null)) {
      this.fileName = ext;
    }
    this.file = new File(this.fileName);
  }
  
  private String getTimeString()
  {
    Calendar cal = Calendar.getInstance();
    String year = new Formatter().format("%04d", new Object[] { Integer.valueOf(cal.get(1)) }).toString();
    
    String month = new Formatter().format("%02d", new Object[] { Integer.valueOf(cal.get(2) + 1) }).toString();
    
    String day = new Formatter().format("%02d", new Object[] { Integer.valueOf(cal.get(5)) }).toString();
    
    String hour = new Formatter().format("%02d", new Object[] { Integer.valueOf(cal.get(11)) }).toString();
    
    String minute = new Formatter().format("%02d", new Object[] { Integer.valueOf(cal.get(12)) }).toString();
    
    String second = new Formatter().format("%02d", new Object[] { Integer.valueOf(cal.get(13)) }).toString();
    
    String milisecond = new Formatter().format("%03d", new Object[] { Integer.valueOf(cal.get(14)) }).toString();
    
    String timeString = year + month + day + "." + hour + minute + second.toString() + milisecond;
    

    return timeString;
  }
  
  private String getCurrentTime()
  {
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
    Date date = new Date();
    return dateFormat.format(date);
  }
  
  public String getProperty(String key)
  {
    if (this.properties != null) {
      return this.properties.getProperty(key);
    }
    return null;
  }
  
  private void flushImmediately()
  {
    if (this.blockCache.size() == 0) {
      return;
    }
    try
    {
      flush();
      

      resetCacheStatus();
      this.logger.info("Forced to write all remain log to file");
    }
    catch (Exception ex)
    {
      this.logger.warn("flush log get error " + ex);
    }
  }
  
  private void loadConfigFile()
  {
    try
    {
      File f = new File(this.CONFIG_FILE_NAME);
      String path = f.getAbsolutePath();
      String parentpath = f.getParent();
      FileInputStream fis = new FileInputStream(this.CONFIG_FILE_NAME);
      
      this.properties = new Properties();
      this.properties.load(fis);
      fis.close();
      
      String mcs = this.properties.getProperty("max_cache_size");
      
      String ml = this.properties.getProperty("max_lines");
      String fc = this.properties.getProperty("flush_cycle");
      this.logPath = this.properties.getProperty("logpath");
      seperator = this.properties.getProperty("seperator");
      this.replacedStringForEndLine = this.properties.getProperty("replaced_string_for_endline", " ");
      this.replacedStringForTab = this.properties.getProperty("replaced_string_for_tab", " ");
      String iActive = this.properties.getProperty("is_active");
      try
      {
        int value = Integer.parseInt(seperator);
        seperator = String.valueOf((char)value);
      }
      catch (NumberFormatException ex) {}
      try
      {
        this.maxCacheSize = Long.parseLong(mcs);
      }
      catch (NumberFormatException e)
      {
        this.logger.warn("Can't parse parameter \"max_cache_size\", used default values: " + this.maxCacheSize);
      }
      try
      {
        this.maxMsgLine = Integer.parseInt(ml);
      }
      catch (NumberFormatException e)
      {
        this.logger.warn("Can't parse parameter \"max_msg_line\", used default values: " + this.maxMsgLine);
      }
      try
      {
        this.maxTimeToFlush = Integer.parseInt(fc);
      }
      catch (NumberFormatException e)
      {
        this.logger.warn("Can't parse parameter \"max_time_to_flush\", used default values: " + this.maxTimeToFlush);
      }
      if ((iActive != null) && (!iActive.equalsIgnoreCase("true")))
      {
        this.isActive = false;
        if (!this.isActive) {
          this.logger.info("LogWriter is not active, all writen operation will be ignored");
        }
      }
      else
      {
        this.isActive = true;
        this.logger.info("LogWriter is actived");
      }
    }
    catch (IOException ioe)
    {
      this.logger.warn("Can'n read config file, used default values");
    }
  }
  
  public void reloadConfig()
  {
    loadConfigFile();
    
    this.logPath += this.subPath;
    this.orgFileName = (this.logPath + File.separator + this.prefix);
    File dir = new File(this.logPath);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    this.logger.info("reloaded config");
  }
  
  public boolean isACtive()
  {
    return this.isActive;
  }
  
  public String getInfor()
  {
    String superInfo = super.getInfor();
    StringBuffer buff = new StringBuffer();
    buff.append("\nEssential Infomation:");
    buff.append("\n\tis_active: " + this.isActive);
    buff.append("\n\tmax_cache_size_to_flush: " + this.maxCacheSize + " [MB]");
    

    buff.append("\n\tmax_time_to_flush: " + this.maxTimeToFlush + " [milisecond]");
    buff.append("\n\tmax_line_to_flush: " + this.maxMsgLine);
    buff.append("\n\tseperator: " + seperator);
    buff.append("\n\tlog_path: " + this.logPath);
    buff.append("\n\tlog_file_prefix: " + this.prefix);
    buff.append("\n\tlastest_file: " + this.fileName);
    buff.append("\n\tcurrent_line: " + this.currentLine);
    buff.append("\n\tcurrent_size: " + this.currentSize + " [byte]");
    buff.append("\n\tlastest_scan_time: " + this.lastScanTime);
    
    buff.append("\n\tpass_time: " + this.intervalTime + " [milisecond]");
    
    buff.append("\n\telements_on_blockcache: " + this.blockCache.size());
    buff.append("\n\t endline representative: " + this.replacedStringForEndLine);
    buff.append("\n\t tab representative: " + this.replacedStringForTab);
    

    return superInfo + buff.toString();
  }
  
  public void start()
  {
    super.start();
    this.logger.info("LogWriter[" + threadIndex + "] is started");
  }
  
  public void stop()
  {
    flushImmediately();
    super.stop();
    this.logger.info("LogWriter[" + threadIndex + "] is stoped");
  }
  
  private void pushDataToCache(String data)
  {
    if (data == null) {
      this.logger.warn("pushDataToCache, data is null ");
    }
    data = data.replaceAll("\n", this.replacedStringForEndLine).replaceAll("\r", this.replacedStringForTab);
    
    data = data + "\n";
    byte[] bytes = data.getBytes();
    this.blockCache.add(bytes);
    this.currentLine += 1L;
    this.currentSize += bytes.length;
    this.logger.debug("Added a message log to buffer");
  }
  
  private void resetCacheStatus()
  {
    this.currentLine = 0L;
    this.currentSize = 0L;
  }
  
  protected void process()
  {
    Object ob = this.bufferQueue.dequeue();
    if (ob == null)
    {
      try
      {
        long currentTime = System.currentTimeMillis();
        this.intervalTime = (currentTime - this.startTime);
        if ((hasBlockCache()) && (
          (this.intervalTime >= this.maxTimeToFlush) || (isCacheFull()))) {
          try
          {
            flush();
            this.startTime = System.currentTimeMillis();
            this.lastScanTime = getCurrentTime();
            resetCacheStatus();
          }
          catch (IOException ex)
          {
            this.logger.error(ex);
            
            Thread.sleep(5000L);
          }
        }
      }
      catch (InterruptedException ex)
      {
        flushImmediately();
        this.logger.warn("InterruptedException ", ex);
      }
    }
    else if ((ob instanceof String))
    {
      pushDataToCache((String)ob);
      try
      {
        long currentTime = System.currentTimeMillis();
        
        this.intervalTime = (currentTime - this.startTime);
        if ((hasBlockCache()) && (
          (this.intervalTime >= this.maxTimeToFlush) || (isCacheFull()))) {
          try
          {
            flush();
            this.startTime = System.currentTimeMillis();
            this.lastScanTime = getCurrentTime();
            resetCacheStatus();
          }
          catch (IOException ex)
          {
            this.logger.error(ex);
            
            Thread.sleep(5000L);
          }
        }
      }
      catch (InterruptedException ex)
      {
        flushImmediately();
        this.logger.warn("InterruptedException ", ex);
      }
    }
  }
  
  public Object invoke(String operationName, Object[] params, String[] signature)
    throws MBeanException, ReflectionException
  {
    if (operationName == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"), "Cannot invoke a null operation in " + this.dClassName);
    }
    if (operationName.equals("reloadConfig")) {
      reloadConfig();
    } else if (operationName.equals("flushImmediately")) {
      flushImmediately();
    } else {
      return super.invoke(operationName, params, signature);
    }
    return null;
  }
  
  protected MBeanOperationInfo[] buildOperations()
  {
    MBeanOperationInfo[] mbInfors = super.buildOperations();
    MBeanOperationInfo[] mbNewInfors = new MBeanOperationInfo[mbInfors.length + 3];
    System.arraycopy(mbInfors, 0, mbNewInfors, 0, mbInfors.length);
    MBeanParameterInfo[] none = new MBeanParameterInfo[0];
    mbNewInfors[mbInfors.length] = new MBeanOperationInfo("reloadConfig", "reload config for Logwriter", none, "void", 0);
    mbNewInfors[(mbInfors.length + 1)] = new MBeanOperationInfo("flushImmediately", "flush all log in buffer to file immediately", none, "void", 0);
    mbNewInfors[(mbInfors.length + 2)] = new MBeanOperationInfo("getCacheLog", "get all log on cache", none, "String", 0);
    return mbNewInfors;
  }
}
