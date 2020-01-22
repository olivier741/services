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
import com.viettel.bccsgw.dao.DatabaseConnection;
import com.viettel.bccsgw.utils.MemoryDataGateway;
import com.viettel.bccsgw.utils.SMTPAlerter;
import com.viettel.bccsgw.utils.StringUtils;
import com.viettel.mmserver.base.ProcessThreadMX;
import com.viettel.mmserver.config.Configuration;
import com.viettel.mmserver.config.NumberFormatter;
import com.viettel.mmserver.config.NumberFormatter.Type;
import com.viettel.mmserver.config.Param;
import com.viettel.mmserver.config.PasswordParam;
import com.viettel.mmserver.config.TextParam;
import com.viettel.mmserver.config.TextParam.TextType;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.Vector;
import org.apache.log4j.Logger;

public class UpdateExpire
  extends ProcessThreadMX
{
  protected static Connection mcnMain;
  protected int numberDayAlertBeforeExpire = 2;
  private boolean isStop = true;
  protected long sleepThread = 6000000L;
  protected String mailSubject = "";
  protected String mailContent = "";
  protected String mailAddress = "";
  protected String mailHost = "";
  protected String mailSender = "";
  protected String mailPass = "";
  protected String smsReceiver = "";
  protected String smsContent = "";
  private static String PATH_CONFIG_FILE = "";
  
  public UpdateExpire()
  {
    super(UpdateExpire.class.getSimpleName());
  }
  
  public UpdateExpire(String theadName, String pathConfigFile)
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
  
  private boolean isSendMail()
  {
    if ((this.mailHost == null) || (this.mailHost.equals("")) || (this.mailSender == null) || (this.mailSender.equals("")) || (this.mailPass == null) || (this.mailPass.equals("")) || (this.mailContent == null) || (this.mailContent.equals(""))) {
      return false;
    }
    return true;
  }
  
  private void initConfigFile()
  {
    try
    {
      Properties defaultProps = new Properties();
      FileInputStream in = new FileInputStream(PATH_CONFIG_FILE);
      defaultProps.load(in);
      in.close();
      
      this.mailHost = defaultProps.getProperty("MailHost");
      this.mailSender = defaultProps.getProperty("MailSender");
      this.mailPass = defaultProps.getProperty("MailPass");
      this.mailSubject = defaultProps.getProperty("MailSubject");
      this.mailContent = defaultProps.getProperty("MailContent");
      this.mailAddress = defaultProps.getProperty("MailAddress");
      this.smsReceiver = defaultProps.getProperty("SmsReceiver");
      this.smsContent = defaultProps.getProperty("SmsContent");
      this.sleepThread = Long.valueOf(defaultProps.getProperty("ThreadSleep")).longValue();
      this.numberDayAlertBeforeExpire = Integer.parseInt(defaultProps.getProperty("NumberDayAlertBeforeExpire"));
    }
    catch (Exception ex)
    {
      this.sleepThread = 5000L;
      this.logger.error("Can not load file " + PATH_CONFIG_FILE + ex.getMessage());
    }
  }
  
  public Configuration loadConfig()
  {
    Configuration c = new Configuration();
    initConfigFile();
    c.addParam(new TextParam("MailHost", this.mailHost != null ? this.mailHost : ""));
    c.addParam(new TextParam("MailSender", this.mailSender != null ? this.mailSender : ""));
    c.addParam(new PasswordParam("MailPass", this.mailPass != null ? this.mailPass : ""));
    c.addParam(new TextParam("MailSubject", this.mailSubject != null ? this.mailSubject : ""));
    c.addParam(new TextParam("MailContent", this.mailContent != null ? this.mailContent : ""));
    c.addParam(new TextParam("MailAddress", this.mailAddress != null ? this.mailAddress : ""));
    c.addParam(new TextParam("SmsReceiver", this.smsReceiver != null ? this.smsReceiver : ""));
    c.addParam(new TextParam("SmsContent", this.smsContent != null ? this.smsContent : ""));
    c.addParam(new TextParam("NumberDayAlertBeforeExpire", String.valueOf(this.numberDayAlertBeforeExpire), TextParam.TextType.TEXT_FIELD, new NumberFormatter(NumberFormatter.Type.Integer)));
    c.addParam(new TextParam("ThreadSleep", String.valueOf(this.sleepThread), TextParam.TextType.TEXT_FIELD, new NumberFormatter(NumberFormatter.Type.Integer)));
    return c;
  }
  
  public boolean saveConfig(Configuration newConfig)
  {
    Properties outProperties = new Properties();
    try
    {
      this.mailHost = newConfig.getParamByName("MailHost").getValue();
      outProperties.setProperty("MailHost", this.mailHost);
      

      this.mailSender = newConfig.getParamByName("MailSender").getValue();
      outProperties.setProperty("MailSender", this.mailSender);
      

      this.mailPass = newConfig.getParamByName("MailPass").getValue();
      outProperties.setProperty("MailPass", this.mailPass);
      

      this.mailSubject = newConfig.getParamByName("MailSubject").getValue();
      outProperties.setProperty("MailSubject", this.mailSubject);
      

      this.mailContent = newConfig.getParamByName("MailContent").getValue();
      outProperties.setProperty("MailContent", this.mailContent);
      

      this.mailAddress = newConfig.getParamByName("MailAddress").getValue();
      outProperties.setProperty("MailAddress", this.mailAddress);
      

      this.smsReceiver = newConfig.getParamByName("SmsReceiver").getValue();
      outProperties.setProperty("SmsReceiver", this.smsReceiver);
      


      this.smsContent = newConfig.getParamByName("SmsContent").getValue();
      outProperties.setProperty("SmsContent", this.smsContent);
      
      this.numberDayAlertBeforeExpire = Integer.parseInt(newConfig.getParamByName("NumberDayAlertBeforeExpire").getValue() != null ? newConfig.getParamByName("NumberDayAlertBeforeExpire").getValue() : "5");
      outProperties.setProperty("NumberDayAlertBeforeExpire", String.valueOf(this.numberDayAlertBeforeExpire));
      
      this.sleepThread = Long.valueOf(newConfig.getParamByName("ThreadSleep").getValue() != null ? newConfig.getParamByName("ThreadSleep").getValue() : "600000").longValue();
      outProperties.setProperty("ThreadSleep", String.valueOf(this.sleepThread));
      
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
        this.logger.info("Processing update expire for Client, webservice, access management");
        updateClient();
        
        updateAccessManagement();
        boolean result = clientAlertList();
        if (result) {
          MemoryDataGateway.reloadDataMemory();
        }
        this.logger.info("Thread sleeping " + this.sleepThread + " miliseconds");
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
  
  private boolean clientAlertList()
  {
    String sql = "select client_id, username, to_char(expired_time, 'dd/MM/yyyy')  expired_time from client where expired_time <= trunc(sysdate) + " + this.numberDayAlertBeforeExpire + " and status = 1 and alert_date is null";
    

    PreparedStatement stmt = null;
    ResultSet rs = null;
    boolean result = false;
    try
    {
      stmt = mcnMain.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next())
      {
        Long clientId = Long.valueOf(rs.getLong("client_id"));
        String username = rs.getString("username");
        String exprireDate = rs.getString("expired_time");
        
        String message = "";
        if (isSendMail())
        {
          message = this.mailContent;
          message = message.replaceAll("[$]alertContent[$]", "Client: " + username).replaceAll("[$]expireDate[$]", exprireDate);
          Vector vtReceiver = StringUtils.toStringVetor(this.mailAddress, ";");
          String smtpsCertFile = "../conf/vtcacerts";
          boolean isSend = SMTPAlerter.sendMail(this.mailHost, 465, this.mailSender, this.mailPass, vtReceiver, this.mailSubject, message, null, smtpsCertFile);
          if (!isSend) {
            this.logger.warn("Send mail to " + this.mailAddress + " fail!");
          }
        }
        try
        {
          message = this.smsContent;
          message = message.replaceAll("[$]alertContent[$]", "Client: " + username).replaceAll("[$]expireDate[$]", exprireDate);
          if ((this.smsReceiver != null) && (this.smsReceiver.equals(""))) {
            continue;
          }
          Vector vtReceiver = StringUtils.toStringVetor(this.smsReceiver, ";");
          if ((vtReceiver != null) && (vtReceiver.size() > 0)) {
            for (int i = 0; i < vtReceiver.size(); i++)
            {
              String strReceiver = (String)vtReceiver.get(i);
              insertMessageOutQueue(message, strReceiver);
            }
          }
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
          this.logger.error(ex, ex);
        }
        PreparedStatement stmtUpdate = null;
        try
        {
          String sqlUpdate = "update client set alert_date = sysdate where client_id = ?";
          stmtUpdate = mcnMain.prepareStatement(sqlUpdate);
          stmtUpdate.setLong(1, clientId.longValue());
          stmtUpdate.executeUpdate();
          result = true;
          this.logger.info("Send alert to client " + username);
        }
        catch (Exception ex)
        {
          this.logger.error(ex, ex);
        }
        finally
        {
          closeObject(stmtUpdate);
        }
      }
    }
    catch (Exception ex)
    {
      this.logger.error(ex, ex);
    }
    finally
    {
      closeObject(rs);
      closeObject(stmt);
    }
    return result;
  }
  
  private void insertMessageOutQueue(String message, String receiver)
  {
    String sql = "insert into message_out_queue (message_id, message, send_start_date, status, receiver, cond_id)  values(message_out_queue_seq.nextval, ?, sysdate, 0, ?, 0)";
    
    PreparedStatement stmt = null;
    try
    {
      stmt = mcnMain.prepareStatement(sql);
      stmt.setString(1, message);
      stmt.setString(2, receiver);
      stmt.execute();
    }
    catch (Exception ex)
    {
      this.logger.error(ex, ex);
    }
    finally
    {
      closeObject(stmt);
    }
  }
  
  private void updateClient()
  {
    String sql = "select client_id, username from client where expired_time <= trunc(sysdate) and status = 1";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try
    {
      stmt = mcnMain.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next())
      {
        Long clientId = Long.valueOf(rs.getLong("client_id"));
        String username = rs.getString("username");
        String sqlUpdate = "update client set status = ? where client_id = ?";
        PreparedStatement stmtUpdate = null;
        try
        {
          stmtUpdate = mcnMain.prepareStatement(sqlUpdate);
          stmtUpdate.setInt(1, 0);
          stmtUpdate.setLong(2, clientId.longValue());
          stmtUpdate.executeUpdate();
          this.logger.info("Update client " + username + " is inactive!");
        }
        catch (Exception ex)
        {
          this.logger.error(ex, ex);
        }
        finally
        {
          closeObject(stmtUpdate);
        }
      }
    }
    catch (Exception ex)
    {
      this.logger.error(ex, ex);
    }
    finally
    {
      closeObject(rs);
      closeObject(stmt);
    }
  }
  
  private void updateWebservice()
  {
    String sql = "select ws_id, ws_code from webservice where expired_time <= trunc(sysdate) and status = 1";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try
    {
      stmt = mcnMain.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next())
      {
        Long wsId = Long.valueOf(rs.getLong("ws_id"));
        String wsCode = rs.getString("ws_code");
        String sqlUpdate = "update webservice set status = ? where ws_id = ?";
        PreparedStatement stmtUpdate = null;
        try
        {
          stmtUpdate = mcnMain.prepareStatement(sqlUpdate);
          stmtUpdate.setInt(1, 0);
          stmtUpdate.setLong(2, wsId.longValue());
          stmtUpdate.executeUpdate();
          this.logger.info("Update webservice " + wsCode + " is inactive!");
        }
        catch (Exception ex)
        {
          this.logger.error(ex, ex);
        }
        finally
        {
          closeObject(stmtUpdate);
        }
      }
    }
    catch (Exception ex)
    {
      this.logger.error(ex, ex);
    }
    finally
    {
      closeObject(rs);
      closeObject(stmt);
    }
  }
  
  private void updateAccessManagement()
  {
    String sql = "select am_id, ws_id, client_id from access_manegement where expired_time <= trunc(sysdate) and status = 1";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try
    {
      stmt = mcnMain.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next())
      {
        Long amId = Long.valueOf(rs.getLong("am_id"));
        Long wsId = Long.valueOf(rs.getLong("ws_id"));
        Long clientId = Long.valueOf(rs.getLong("client_id"));
        String sqlUpdate = "update access_manegement set status = ? where am_id = ?";
        PreparedStatement stmtUpdate = null;
        try
        {
          stmtUpdate = mcnMain.prepareStatement(sqlUpdate);
          stmtUpdate.setInt(1, 0);
          stmtUpdate.setLong(2, amId.longValue());
          stmtUpdate.executeUpdate();
          this.logger.info("Update client " + clientId + " call webservice " + wsId + " is inactive!");
        }
        catch (Exception ex)
        {
          this.logger.error(ex, ex);
        }
        finally
        {
          closeObject(stmtUpdate);
        }
      }
    }
    catch (Exception ex)
    {
      this.logger.error(ex, ex);
    }
    finally
    {
      closeObject(rs);
      closeObject(stmt);
    }
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
      this.logger.error("Error stop " + ex);
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
