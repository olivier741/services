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
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.PairVasReqRsp;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasRequest;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasResponse;
import com.tatsinktechnologic.ussd_gateway.utils.BlockQueue;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VasLogWriterThread
  extends ProcessThreadMX
{
  private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(3);
  private static final SimpleDateFormat HOUR_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
  private LogWriter vasLogWriter;
  private BlockQueue vasQueue;
  private DBLog dbLog;
  private int sid;
  
  public VasLogWriterThread(String name, LogWriter vasLogWriter, DBLog dbLog)
  {
    super(name, "write log VAS messages communicate with CP");
    this.vasLogWriter = vasLogWriter;
    this.dbLog = dbLog;
    try
    {
      registerAgent("DBLog:type=VasLog,name=" + name);
    }
    catch (Exception ex)
    {
      this.logger.error("regist mbean fail: " + ex.getMessage(), ex);
    }
  }
  
  public void setVasQueue(BlockQueue vasQueue)
  {
    this.vasQueue = vasQueue;
  }
  
  public void setSid(int sid)
  {
    this.sid = sid;
  }
  
  public void process()
  {
    this.logger.info("receiving a vas message to write log ...");
    PairVasReqRsp msg = (PairVasReqRsp)this.vasQueue.dequeue();
    if (msg == null)
    {
      this.logger.info("no vas message to receive.");
      return;
    }
    this.logger.info("write log vas message received");
    writeLog(msg);
    this.logger.info("write log complete");
  }
  
  private void writeLog(PairVasReqRsp msg)
  {
    StringBuilder logInfo = new StringBuilder();
    
    String sep = this.vasLogWriter.getSeperator();
    
    VasRequest reqMsg = msg.getReq();
    VasResponse rspMsg = msg.getRsp();
    

    logInfo.append(this.sid).append(sep);
    logInfo.append(reqMsg.getId()).append(sep);
    logInfo.append(reqMsg.getMsisdn()).append(sep);
    logInfo.append(reqMsg.getImsi()).append(sep);
    logInfo.append(reqMsg.getBizId()).append(sep);
    











    String params = reqMsg.getLoggedParams();
    if ((this.dbLog.isEncryptMode()) && (params != null))
    {
      try
      {
        params = this.dbLog.encryptString(params.getBytes("UTF-16BE"));
      }
      catch (UnsupportedEncodingException ex)
      {
        this.logger.error("encode params fail: " + ex.getMessage());
        params = null;
      }
    }
    else if (params != null)
    {
      params = params.replaceAll("\n", "(n)");
      params = params.replaceAll("\r", "(r)");
    }
    String content = rspMsg.getLoggedContent();
    if ((this.dbLog.isEncryptMode()) && (content != null))
    {
      try
      {
        content = this.dbLog.encryptString(content.getBytes("UTF-16BE"));
      }
      catch (UnsupportedEncodingException ex)
      {
        this.logger.error("encode params fail: " + ex.getMessage());
        content = null;
      }
    }
    else if (content != null)
    {
      content = content.replaceAll("\n", "(n)");
      content = content.replaceAll("\r", "(r)");
    }
    logInfo.append(params).append(sep);
    logInfo.append(content).append(sep);
    logInfo.append(reqMsg.getConnectorId()).append(sep);
    Date date = new Date(reqMsg.getSendRecvTime());
    String sendTime = DATE_FORMAT.format(date) + " " + HOUR_FORMAT.format(date);
    logInfo.append(sendTime).append(sep);
    date = new Date(rspMsg.getSendRecvTime());
    String recvTime = DATE_FORMAT.format(date) + " " + HOUR_FORMAT.format(date);
    logInfo.append(recvTime);
    

    logInfo.append(sep);
    logInfo.append(reqMsg.getTransId());
    
    this.vasLogWriter.writeLn(logInfo.toString());
  }
}
