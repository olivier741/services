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
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Vector;


public class FileWrittingThread
  extends ProcessThreadMX
{
  private static int fileIndex;
  private String orgFileName;
  private File logDir;
  private BlockQueue queue;
  
  public FileWrittingThread(String name, String logFolder, String prefixName)
  {
    super(name, "Thread write log to file");
    
    this.orgFileName = (logFolder + File.separator + prefixName);
    this.logDir = new File(logFolder);
    if (!this.logDir.exists()) {
      this.logDir.mkdirs();
    }
    this.logger.info("regist agent");
    try
    {
      registerAgent("logging:type=WriteFileThread, name=" + name);
    }
    catch (Exception ex)
    {
      this.logger.error("Register JMX agent error: " + ex.getMessage());
    }
  }
  
  public void setQueue(BlockQueue queue)
  {
    this.queue = queue;
  }
  
  public void process()
  {
    this.logger.info("get block to write file");
    Vector<String> block = (Vector)this.queue.dequeue();
    if (block == null)
    {
      this.logger.info("no block to write file");
      return;
    }
    this.logger.info("have new block need to write to file");
    


    String newFileName = getFileName();
    this.logger.info("write log to file: " + newFileName);
    createNewFile(newFileName, block);
    this.logger.info("write log to file complete");
  }
  
  private static synchronized int getIndexFile()
  {
    return ++fileIndex;
  }
  
  private void createNewFile(String filename, Vector<String> block)
  {
    if (!this.logDir.exists()) {
      this.logDir.mkdirs();
    }
    FileWriter out = null;
    try
    {
      out = new FileWriter(filename);
      for (String line : block)
      {
        out.write(line);
        out.write("\n");
      }
      return;
    }
    catch (Exception ex)
    {
      this.logger.error("make log file error: " + ex.getMessage(), ex);
    }
    finally
    {
      if (out != null) {
        try
        {
          out.close();
        }
        catch (Exception e)
        {
          this.logger.error("close file error: " + e.getMessage());
        }
      }
    }
  }
  
  private String getFileName()
  {
    Calendar cal = Calendar.getInstance();
    Formatter fm = new Formatter().format("%s.%04d%02d%02d.%02d%02d%02d.%d.log", new Object[] { this.orgFileName, Integer.valueOf(cal.get(1)), Integer.valueOf(cal.get(2) + 1), Integer.valueOf(cal.get(5)), Integer.valueOf(cal.get(11)), Integer.valueOf(cal.get(12)), Integer.valueOf(cal.get(13)), Integer.valueOf(getIndexFile()) });
    return fm.toString();
  }
}
