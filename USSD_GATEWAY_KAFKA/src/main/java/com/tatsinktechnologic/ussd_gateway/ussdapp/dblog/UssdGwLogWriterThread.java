/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.dblog;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.logging.LogWriter;
import com.tatsinktechnologic.ussd_gateway.mmserver.base.ProcessThreadMX;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.UssdMessage;
import com.tatsinktechnologic.ussd_gateway.utils.BlockQueue;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class UssdGwLogWriterThread
  extends ProcessThreadMX
{
  private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(3);
  private static final SimpleDateFormat HOUR_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
  private LogWriter ussdLogWriter;
  private BlockQueue ussdQueue;
  private DBLog dbLog;
  private int sid;
  
  public UssdGwLogWriterThread(String name, LogWriter ussdLogWriter, DBLog dbLog)
  {
    super(name, "write log ussd messages communicate with Ussd Gateway");
    this.ussdLogWriter = ussdLogWriter;
    this.dbLog = dbLog;
    try
    {
      registerAgent("DBLog:type=GwLog,name=" + name);
    }
    catch (Exception ex)
    {
      this.logger.error("regist mbean fail: " + ex.getMessage(), ex);
    }
  }
  
  public void setUssdQueue(BlockQueue ussdQueue)
  {
    this.ussdQueue = ussdQueue;
  }
  
  public void setSid(int sid)
  {
    this.sid = sid;
  }
  
  public void process()
  {
    this.logger.info("receiving a ussd message to write log ...");
    UssdMessage msg = (UssdMessage)this.ussdQueue.dequeue();
    if (msg == null)
    {
      this.logger.info("no ussd message to receive.");
      return;
    }
    this.logger.info("write log ussd message received");
    writeLog(msg);
    this.logger.info("write log complete");
  }
  
  private void writeLog(UssdMessage msg)
  {
    StringBuilder logInfo = new StringBuilder();
    
    String sep = this.ussdLogWriter.getSeperator();
    
    logInfo.append(this.sid).append(sep);
    

    Date date = new Date(msg.getSendRecvTime());
    String currentTime = DATE_FORMAT.format(date) + " " + HOUR_FORMAT.format(date);
    logInfo.append(currentTime).append(sep);
    logInfo.append(msg.getTransId()).append(sep);
    logInfo.append(msg.getType()).append(sep);
    logInfo.append(msg.getMsisdn()).append(sep);
    logInfo.append(msg.getImsi()).append(sep);

    String ussdString = msg.getLoggedString();
    if ((this.dbLog.isEncryptMode()) && (ussdString != null))
    {
      try
      {
        ussdString = this.dbLog.encryptString(ussdString.getBytes("UTF-16BE"));
      }
      catch (UnsupportedEncodingException ex)
      {
        this.logger.error("encode ussd string fail: " + ex.getMessage());
        ussdString = null;
      }
    }
    else if (ussdString != null)
    {
      ussdString = ussdString.replaceAll("\n", "(n)");
      ussdString = ussdString.replaceAll("\r", "(r)");
    }
    logInfo.append(ussdString).append(sep);
    


    logInfo.append(msg.getConnectorId()).append(sep);
    logInfo.append(msg.getTimeout());
    

    this.ussdLogWriter.writeLn(logInfo.toString());
  }
}