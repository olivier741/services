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
import com.viettel.bccsgw.bo.MessageOutQueue;
import com.viettel.bccsgw.dao.DatabaseConnection;
import com.viettel.bccsgw.sendmt.MtStub;
import com.viettel.mmserver.base.ProcessThreadMX;
import com.viettel.mmserver.config.Configuration;
import com.viettel.mmserver.config.NumberFormatter;
import com.viettel.mmserver.config.PasswordParam;
import com.viettel.mmserver.config.TextParam;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SendSms
  extends ProcessThreadMX
{
  protected static Connection mcnMain;
  private boolean isStop = true;
  protected String smsUrl = "http://10.58.3.53:8108/vasp/Service.asmx";
  protected String smsXmlns = "http://tempuri.org/";
  protected String smsUsername = "sms2008";
  protected String smsPassword = "sms2008";
  protected String smsNumberGateway = "155";
  protected String smsCountryCode = "84";
  protected long sleepThread = 600000L;
  protected long maxRetry = 3L;
  private static MtStub SMS_CONNECTION = null;
  private static String PATH_CONFIG_FILE = "";
  
  public SendSms()
  {
    super(SendSms.class.getSimpleName());
  }
  
  public SendSms(String theadName, String pathConfigFile)
  {
    super(theadName);
    try
    {
      registerAgent("Import:name=" + this.threadName);
      PATH_CONFIG_FILE = pathConfigFile;
      initConfigFile();
    }
    catch (Exception ex)
    {
      this.logger.error(ex.getMessage(), ex);
      ex.printStackTrace();
    }
  }
  
  private void initConfigFile()
  {
    try
    {
      Properties defaultProps = new Properties();
      FileInputStream in = new FileInputStream(PATH_CONFIG_FILE);
      defaultProps.load(in);
      in.close();
      
      this.smsUrl = defaultProps.getProperty("URL");
      this.smsXmlns = defaultProps.getProperty("xmlns");
      this.smsUsername = defaultProps.getProperty("username");
      this.smsPassword = defaultProps.getProperty("password");
      this.smsNumberGateway = defaultProps.getProperty("numberGateway");
      this.smsCountryCode = defaultProps.getProperty("countryCode");
      this.sleepThread = Long.valueOf(defaultProps.getProperty("ThreadSleep")).longValue();
      this.maxRetry = Long.valueOf(defaultProps.getProperty("maxRetrySender")).longValue();
    }
    catch (Exception ex)
    {
      this.sleepThread = 5000L;
      this.logger.error("Can not load file config.properties. " + ex.getMessage());
    }
  }
  
  protected void prepareStart()
  {
    super.prepareStart();
  }
  
  protected void process()
  {
    try
    {
      if (!this.isStop)
      {
        if ((mcnMain == null) || (mcnMain.isClosed()))
        {
          mcnMain = DatabaseConnection.openConnection();
          this.logger.info("Open connection to BCCS Gateway system");
        }
        sendSMS();
        
        Thread.sleep(this.sleepThread);
      }
      else
      {
        super.stop();
      }
    }
    catch (Exception ex)
    {
      this.logger.error(ex, ex);
      super.stop();
    }
  }
  
  protected void sendSMS()
  {
    List<MessageOutQueue> lstSend = getMessageOutQueue();
    if ((lstSend == null) || (lstSend.size() < 1)) {
      return;
    }
    if (SMS_CONNECTION == null) {
      SMS_CONNECTION = new MtStub(this.smsUrl, this.smsXmlns, this.smsUsername, this.smsPassword);
    }
    for (int i = 0; i < lstSend.size(); i++)
    {
      MessageOutQueue messageOutQueue = (MessageOutQueue)lstSend.get(i);
      int result = SMS_CONNECTION.send("0", "warning", this.smsNumberGateway, this.smsCountryCode + messageOutQueue.getReceiver(), "0", messageOutQueue.getMessage(), "0");
      if (result == 0)
      {
        this.logger.info("<html><font color = green>[BCCS_GW]  Send alert to " + messageOutQueue.getReceiver() + " successfully! </font></html>");
        updateMessageOutQueue(messageOutQueue, true);
      }
      else
      {
        this.logger.info("<html><font color = red>[BCCS_GW] Send alert to " + messageOutQueue.getReceiver() + " fail! </font></html>");
        updateMessageOutQueue(messageOutQueue, false);
      }
    }
  }
  
  private void updateMessageOutQueue(MessageOutQueue messageOutQueue, boolean isSendSuccess)
  {
    PreparedStatement stmt = null;
    try
    {
      mcnMain.setAutoCommit(false);
      if (isSendSuccess)
      {
        String sql = "update message_out_queue set status = 1, send_date = sysdate, sender =? where message_id = ?";
        stmt = mcnMain.prepareStatement(sql);
        stmt.setString(1, this.smsNumberGateway);
        stmt.setInt(2, messageOutQueue.getMessageId());
        stmt.execute();
      }
      else if ((messageOutQueue.getSendDate() != null) && (!messageOutQueue.getSendDate().equals("")))
      {
        int retry = messageOutQueue.getRetry();
        retry++;
        String sql = "update message_out_queue set retry_time = sysdate, retry =? where message_id = ?";
        stmt = mcnMain.prepareStatement(sql);
        stmt.setInt(1, retry);
        stmt.setInt(2, messageOutQueue.getMessageId());
        stmt.execute();
      }
      else
      {
        String sql = "update message_out_queue set send_date = sysdate, sender =? where message_id = ?";
        stmt = mcnMain.prepareStatement(sql);
        stmt.setString(1, this.smsNumberGateway);
        stmt.setInt(2, messageOutQueue.getMessageId());
        stmt.execute();
      }
      mcnMain.commit();
    }
    catch (Exception ex)
    {
      this.logger.error("Error when update message_out_queue table! " + ex.getMessage());
      try
      {
        mcnMain.rollback();
      }
      catch (SQLException ex1)
      {
        this.logger.error("rollback error!!!", ex1);
      }
      this.logger.error(ex, ex);
      stop();
    }
    finally
    {
      closeObject(stmt);
    }
  }
  
  private List<MessageOutQueue> getMessageOutQueue()
  {
    List<MessageOutQueue> lstReturn = new ArrayList();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try
    {
      String sql = "SELECT message_id, message, to_char(send_start_date,'dd/MM/yyyy HH24:mi:ss') send_start_date, status, to_char(send_date,'dd/MM/yyyy HH24:mi:ss') send_date, retry, to_char(retry_time,'dd/MM/yyyy HH24:mi:ss') retry_time, log_id, receiver, sender, cond_id, to_char(log_date_time,'dd/MM/yyyy HH24:mi:ss') log_date_time  FROM message_out_queue WHERE send_start_date <= sysdate  AND (send_date IS NULL OR (status = 0 AND (retry IS NULL OR retry < " + this.maxRetry + ")))";
      




      stmt = mcnMain.prepareStatement(sql);
      rs = stmt.executeQuery();
      MessageOutQueue messageOutQueue = null;
      while (rs.next())
      {
        messageOutQueue = new MessageOutQueue();
        int messageId = rs.getInt("message_id");
        String message = rs.getString("message") != null ? rs.getString("message") : "";
        String sendStartDate = rs.getString("send_start_date") != null ? rs.getString("send_start_date") : "";
        int sendStatus = rs.getInt("status");
        String sendDate = rs.getString("send_date") != null ? rs.getString("send_date") : "";
        int retry = rs.getInt("retry");
        String retryTime = rs.getString("retry_time") != null ? rs.getString("retry_time") : "";
        int logId = rs.getInt("log_id");
        String receiver = rs.getString("receiver") != null ? rs.getString("receiver") : "";
        String sender = rs.getString("sender") != null ? rs.getString("sender") : "";
        int condId = rs.getInt("cond_id");
        String logDateTime = rs.getString("log_date_time") != null ? rs.getString("log_date_time") : "";
        messageOutQueue.setMessageId(messageId);
        messageOutQueue.setMessage(message);
        messageOutQueue.setSendStartDate(sendStartDate);
        messageOutQueue.setStatus(sendStatus);
        messageOutQueue.setSendDate(sendDate);
        messageOutQueue.setRetry(retry);
        messageOutQueue.setRetryTime(retryTime);
        messageOutQueue.setLogId(logId);
        messageOutQueue.setReceiver(receiver);
        messageOutQueue.setSender(sender);
        messageOutQueue.setCondId(condId);
        messageOutQueue.setLogDateTime(logDateTime);
        lstReturn.add(messageOutQueue);
      }
    }
    catch (Exception ex)
    {
      this.logger.error("Error when query message_out_queue table! " + ex.getMessage(), ex);
    }
    finally
    {
      closeObject(rs);
      closeObject(stmt);
    }
    return lstReturn;
  }
  
  public void stop()
  {
    this.logger.info("INFO_SYSTEM_STOP:This thread is stopped ");
    this.isStop = true;
    try
    {
      if ((mcnMain != null) && (!mcnMain.isClosed()))
      {
        mcnMain.close();
        mcnMain = null;
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      this.logger.error("Error stop" + ex);
    }
    super.stop();
  }
  
  public void start()
  {
    this.logger.info("INFO_SYSTEM_START:This thread is started ");
    this.isStop = false;
    super.start();
  }
  
  public void restart()
  {
    stop();
    start();
  }
  
  public Configuration loadConfig()
  {
    Configuration c = new Configuration();
    initConfigFile();
    c.addParam(new TextParam("URL", this.smsUrl != null ? this.smsUrl : ""));
    c.addParam(new TextParam("xmlns", this.smsXmlns != null ? this.smsXmlns : ""));
    c.addParam(new TextParam("username", this.smsUsername != null ? this.smsUsername : ""));
    c.addParam(new PasswordParam("password", this.smsPassword != null ? this.smsPassword : ""));
    c.addParam(new TextParam("numberGateway", this.smsNumberGateway != null ? this.smsNumberGateway : ""));
    c.addParam(new TextParam("countryCode", this.smsCountryCode != null ? this.smsCountryCode : ""));
    c.addParam(new TextParam("ThreadSleep", String.valueOf(this.sleepThread), TextParam.TextType.TEXT_FIELD, new NumberFormatter(NumberFormatter.Type.Integer)));
    c.addParam(new TextParam("maxRetrySender", String.valueOf(this.maxRetry), TextParam.TextType.TEXT_FIELD, new NumberFormatter(NumberFormatter.Type.Integer)));
    return c;
  }
  
  public boolean saveConfig(Configuration newConfig)
  {
    Properties outProperties = new Properties();
    try
    {
      this.smsUrl = newConfig.getParamByName("URL").getValue();
      outProperties.setProperty("URL", this.smsUrl);
      
      this.smsXmlns = newConfig.getParamByName("xmlns").getValue();
      outProperties.setProperty("xmlns", this.smsXmlns);
      
      this.smsUsername = newConfig.getParamByName("username").getValue();
      outProperties.setProperty("username", this.smsUsername);
      
      this.smsPassword = newConfig.getParamByName("password").getValue();
      outProperties.setProperty("password", this.smsPassword);
      
      this.smsNumberGateway = newConfig.getParamByName("numberGateway").getValue();
      outProperties.setProperty("numberGateway", this.smsNumberGateway);
      
      this.smsCountryCode = newConfig.getParamByName("countryCode").getValue();
      outProperties.setProperty("countryCode", this.smsCountryCode);
      
      this.sleepThread = Long.valueOf(newConfig.getParamByName("ThreadSleep").getValue() != null ? newConfig.getParamByName("ThreadSleep").getValue() : "600000").longValue();
      outProperties.setProperty("ThreadSleep", String.valueOf(this.sleepThread));
      
      this.maxRetry = Long.valueOf(newConfig.getParamByName("maxRetrySender").getValue() != null ? newConfig.getParamByName("maxRetrySender").getValue() : "3").longValue();
      outProperties.setProperty("maxRetrySender", String.valueOf(this.maxRetry));
      
      FileOutputStream out = new FileOutputStream(PATH_CONFIG_FILE);
      outProperties.store(out, "Update config file");
      out.close();
    }
    catch (Exception ex)
    {
      this.logger.error(ex, ex);
      return false;
    }
    return true;
  }
  
  public static void closeObject(Connection obj)
  {
    try
    {
      if ((obj != null) && 
        (!obj.isClosed()))
      {
        if (!obj.getAutoCommit()) {
          obj.rollback();
        }
        obj.close();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static void closeObject(CallableStatement obj)
  {
    try
    {
      if (obj != null) {
        obj.close();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static void closeObject(Statement obj)
  {
    try
    {
      if (obj != null) {
        obj.close();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static void closeObject(ResultSet obj)
  {
    try
    {
      if (obj != null) {
        obj.close();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static void closeObject(PreparedStatement obj)
  {
    try
    {
      if (obj != null) {
        obj.close();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  protected static void safeClose(FileInputStream is)
  {
    try
    {
      is.close();
    }
    catch (Exception ex)
    {
      is = null;
    }
  }
  
  protected static void safeClose(PrintWriter is)
  {
    try
    {
      is.close();
    }
    catch (Exception ex)
    {
      is = null;
    }
  }
  
  protected static void safeClose(FileOutputStream is)
  {
    try
    {
      is.close();
    }
    catch (Exception ex)
    {
      is = null;
    }
  }
  
  protected static void safeClose(BufferedInputStream is)
  {
    try
    {
      is.close();
    }
    catch (Exception ex)
    {
      is = null;
    }
  }
}
