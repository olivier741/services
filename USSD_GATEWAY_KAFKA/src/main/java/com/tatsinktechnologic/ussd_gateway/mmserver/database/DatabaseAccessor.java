/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.database;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.mmserver.base.Log;
import com.tatsinktechnologic.ussd_gateway.mmserver.scheduler.DailyTask;
import com.tatsinktechnologic.ussd_gateway.mmserver.scheduler.MonthlyTask;
import com.tatsinktechnologic.ussd_gateway.mmserver.scheduler.OnceTask;
import com.tatsinktechnologic.ussd_gateway.mmserver.scheduler.RepeatedTask;
import com.tatsinktechnologic.ussd_gateway.mmserver.scheduler.Scheduler;
import com.tatsinktechnologic.ussd_gateway.mmserver.scheduler.Task;
import com.tatsinktechnologic.ussd_gateway.mmserver.scheduler.WeeklyTask;
import com.tatsinktechnologic.ussd_gateway.mmserver.warnning.ErrorCondition;
import com.tatsinktechnologic.ussd_gateway.mmserver.warnning.ErrorDefinition;
import com.tatsinktechnologic.ussd_gateway.mmserver.warnning.TimeoutInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;
import static scala.concurrent.Await.result;
import static scala.util.Properties.userName;

public class DatabaseAccessor
{
  private Connection connection;
  private boolean sqlException;
  private Logger log;
  private PreparedStatement insertSchedulingLogPS = null;
  private PreparedStatement selectErrorDefinitionPS = null;
  private PreparedStatement insertErrorDefinitionPS = null;
  private PreparedStatement setErrorDefinitionUseFlagPS = null;
  private PreparedStatement deleteErrorDefinitionPS = null;
  private PreparedStatement selectApplicationTelephone = null;
  private PreparedStatement insertApplicationTelephone = null;
  private PreparedStatement updateApplicationTelephonePS = null;
  private PreparedStatement deleteApplicationReceiverPS = null;
  private PreparedStatement errorIdSeqPS = null;
  private PreparedStatement requestSendSMS = null;
  private PreparedStatement selectThreadTimeoutPS = null;
  private PreparedStatement deleteThreadTimeoutPS = null;
  private PreparedStatement editThreadTimeoutPS = null;
  private PreparedStatement insertThreadTimeoutPS = null;
  private PreparedStatement selectMaxAttemptPS = null;
  private static String selectErrorDefinitionSQL = "";
  private static String insertErrorDefinitionSQL = "";
  private static String setErrorDefinitionUseFlagSQL = "";
  private static String deleteErrorDefinitionSQL = "";
  private static String selectAppTelephoneSQL = "";
  private static String insertAppTelephoneSQL = "";
  private static String updateAppTelephone = "";
  private static String deleteAppTelephone = "";
  private static String selectErrorIDSeqSQL = "";
  private static String selectSMSIdSeqSQL = "";
  private static String insertMMSMSSQL = "";
  private static String requestSendSMSSQL = "";
  private static String selectThreadTimeoutSQL = "";
  private static String deleteThreadTimeoutSQL = "";
  private static String editThreadTimeoutSQL = "";
  private static String insertThreadTimeoutSQL = "";
  private String schema = "";
  
  private DatabaseAccessor()
  {
    this.log = Logger.getLogger("Tools:mmserver");
    try
    {
      this.schema = DatabaseConnection.shareInstance().getSchema();
      selectErrorDefinitionSQL = "SELECT * FROM " + this.schema + ".ERROR_DEFINITION " + "where applicationID = ? AND useFlag = ?";
      
      insertErrorDefinitionSQL = "INSERT INTO " + this.schema + ".ERROR_DEFINITION(errorId,applicationId,userName,createTime, useFlag,loglevel," + "loggerName,message,threadName,smsFormat,smsContent,sendSMS,error_frequence)  " + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      


      setErrorDefinitionUseFlagSQL = "UPDATE " + this.schema + ".ERROR_DEFINITION SET useFlag=? where errorId=?";
      deleteErrorDefinitionSQL = "DELETE FROM " + this.schema + ".ERROR_DEFINITION where errorId = ?";
      selectAppTelephoneSQL = "SELECT * FROM " + this.schema + ".APPLICATION_TELEPHONE where application = ?";
      insertAppTelephoneSQL = "INSERT INTO " + this.schema + ".APPLICATION_TELEPHONE(application,phone_number_list) VALUES(?,?)";
      
      updateAppTelephone = "UPDATE " + this.schema + ".APPLICATION_TELEPHONE set phone_number_list = ? where application = ?";
      
      deleteAppTelephone = "DELETE " + this.schema + ".APP_RECEIVER WHERE APPLICATION_ID = ?";
      selectErrorIDSeqSQL = "SELECT " + this.schema + ".errorDefId_seq.nextVal from dual";
      selectSMSIdSeqSQL = "SELECT " + this.schema + ".smsId_seq.nextVal from dual";
      insertMMSMSSQL = "INSERT INTO " + this.schema + ".WAIT";
      requestSendSMSSQL = "INSERT INTO " + this.schema + ".wait_msg(msg_id,msg_content,application_id,remain_retry, arrived_time)" + "  VALUES(" + this.schema + ".WAIT_MSG_SEQ.nextval, ?,?,?,sysdate) ";
      

      selectThreadTimeoutSQL = "SELECT APPLICATION_ID, MBEANNAME, TIMEOUT, SENDSMS FROM " + this.schema + ".THREAD_TIMEOUT where APPLICATION_ID = ?";
      
      deleteThreadTimeoutSQL = "DELETE FROM " + this.schema + ".THREAD_TIMEOUT WHERE APPLICATION_ID = ? AND MBEANNAME=? ";
      
      editThreadTimeoutSQL = "UPDATE " + this.schema + ".THREAD_TIMEOUT SET TIMEOUT=?, SENDSMS=? WHERE APPLICATION_ID = ? AND MBEANNAME=? ";
      
      insertThreadTimeoutSQL = "INSERT INTO " + this.schema + ".THREAD_TIMEOUT(APPLICATION_ID, MBEANNAME,TIMEOUT, SENDSMS) VALUES(?,?,?,?) ";
      

      this.connection = DatabaseConnection.shareInstance().getConnection();
      
      this.insertSchedulingLogPS = this.connection.prepareStatement("INSERT INTO " + this.schema + ".Scheduling VALUES(?,?,to_date(?,'yyyy/mm/dd/hh24/mi/ss'),?,?,?)");
      
      this.selectErrorDefinitionPS = this.connection.prepareStatement(selectErrorDefinitionSQL);
      this.insertErrorDefinitionPS = this.connection.prepareStatement(insertErrorDefinitionSQL);
      this.setErrorDefinitionUseFlagPS = this.connection.prepareStatement(setErrorDefinitionUseFlagSQL);
      
      this.deleteErrorDefinitionPS = this.connection.prepareStatement(deleteErrorDefinitionSQL);
      this.selectApplicationTelephone = this.connection.prepareStatement(selectAppTelephoneSQL);
      this.insertApplicationTelephone = this.connection.prepareStatement(insertAppTelephoneSQL);
      this.updateApplicationTelephonePS = this.connection.prepareStatement(updateAppTelephone);
      this.errorIdSeqPS = this.connection.prepareStatement(selectErrorIDSeqSQL);
      this.requestSendSMS = this.connection.prepareStatement(requestSendSMSSQL);
      

      this.selectMaxAttemptPS = this.connection.prepareStatement("SELECT avg(max_attemp) FROM " + this.schema + ".application WHERE application_id=?");
      
      this.deleteApplicationReceiverPS = this.connection.prepareStatement(deleteAppTelephone);
      this.selectThreadTimeoutPS = this.connection.prepareStatement(selectThreadTimeoutSQL);
      this.deleteThreadTimeoutPS = this.connection.prepareStatement(deleteThreadTimeoutSQL);
      this.editThreadTimeoutPS = this.connection.prepareStatement(editThreadTimeoutSQL);
      this.insertThreadTimeoutPS = this.connection.prepareStatement(insertThreadTimeoutSQL);
      

      this.connection.setAutoCommit(true);
    }
    catch (Exception ex)
    {
      this.log.error(selectErrorDefinitionSQL);
      this.sqlException = true;
      this.log.error(ex.toString());
    }
  }
  
  private static DatabaseAccessor shareInstance = null;
  
  public static synchronized DatabaseAccessor shareInstance()
  {
    if (shareInstance == null) {
      shareInstance = new DatabaseAccessor();
    }
    return shareInstance;
  }
  
  private void updateConnection()
  {
    try
    {
      this.connection = DatabaseConnection.shareInstance().updateConnection();
      this.insertSchedulingLogPS = this.connection.prepareStatement("INSERT INTO " + this.schema + ".Scheduling VALUES(?,?,to_date(?,'yyyy/mm/dd/hh24/mi/ss'),?,?,?)");
      
      this.selectErrorDefinitionPS = this.connection.prepareStatement(selectErrorDefinitionSQL);
      this.insertErrorDefinitionPS = this.connection.prepareStatement(insertErrorDefinitionSQL);
      this.setErrorDefinitionUseFlagPS = this.connection.prepareStatement(setErrorDefinitionUseFlagSQL);
      this.deleteErrorDefinitionPS = this.connection.prepareStatement(deleteErrorDefinitionSQL);
      this.selectApplicationTelephone = this.connection.prepareStatement(selectAppTelephoneSQL);
      this.insertApplicationTelephone = this.connection.prepareStatement(insertAppTelephoneSQL);
      this.updateApplicationTelephonePS = this.connection.prepareStatement(updateAppTelephone);
      this.errorIdSeqPS = this.connection.prepareStatement(selectErrorIDSeqSQL);
      this.requestSendSMS = this.connection.prepareStatement(requestSendSMSSQL);
      

      this.selectMaxAttemptPS = this.connection.prepareStatement("SELECT avg(max_attemp) FROM " + this.schema + ".application WHERE application_id=?");
      
      this.deleteApplicationReceiverPS = this.connection.prepareStatement(deleteAppTelephone);
      this.selectThreadTimeoutPS = this.connection.prepareStatement(selectThreadTimeoutSQL);
      this.deleteThreadTimeoutPS = this.connection.prepareStatement(deleteThreadTimeoutSQL);
      this.editThreadTimeoutPS = this.connection.prepareStatement(editThreadTimeoutSQL);
      this.insertThreadTimeoutPS = this.connection.prepareStatement(insertThreadTimeoutSQL);
      this.sqlException = false;
      this.connection.setAutoCommit(true);
    }
    catch (Exception ex)
    {
      Log.warn(ex);
    }
  }
  
  private int iRemoveTask(int taskId)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    PreparedStatement ps = null;
    ResultSet rs = null;
    try
    {
      ps = this.connection.prepareStatement("SELECT taskType, repeatedTaskID FROM " + this.schema + ".task WHERE taskId = ?");
      
      ps.setInt(1, taskId);
      rs = ps.executeQuery();
      rs.next();
      String taskType = rs.getString("taskType");
      int repeatedTaskId = rs.getInt("repeatedTaskId");
      String tableName;
      if (taskType.equalsIgnoreCase("Once"))
      {
        ps = this.connection.prepareStatement("DELETE FROM " + this.schema + ".task WHERE taskId = ?");
        ps.setInt(1, taskId);
        if (ps.executeUpdate() < 0) {
          return 0;
        }
      }
      else
      {
        tableName = this.schema + "." + taskType + "Task";
        
        ps = this.connection.prepareStatement("DELETE FROM " + tableName + " WHERE taskId = ?");
        ps.setInt(1, taskId);
        if (ps.executeUpdate() < 0) {
          return 0;
        }
        ps = this.connection.prepareStatement("DELETE FROM " + this.schema + ".task WHERE taskId = ?");
        ps.setInt(1, taskId);
        if (ps.executeUpdate() < 0) {
          return 0;
        }
      }
      int j;
      if (repeatedTaskId != 0)
      {
        ps = this.connection.prepareStatement("DELETE FROM " + this.schema + ".repeatedTask WHERE repeatedTaskId = ?");
        ps.setInt(1, repeatedTaskId);
        if (ps.executeUpdate() < 0) {
          return 0;
        }
      }
      return 1;
    }
    catch (SQLException ex)
    {
      int repeatedTaskId;
      this.log.error("SQL exception:" + ex);
      if (DatabaseConnection.shareInstance().checkConnection()) {
        return -1;
      }
      this.sqlException = true;
      throw new ConnectionException(ex.getMessage(), "");
    }
    finally
    {
      try
      {
        if (rs != null) {
          rs.close();
        }
        if (ps != null) {
          ps.close();
        }
      }
      catch (Exception e)
      {
        this.log.debug(e);
      }
    }
  }
  
  public int removeTask(int taskId)
  {
    try
    {
      return iRemoveTask(taskId);
    }
    catch (ConnectionException ce)
    {
      this.log.error(ce.getMessage());
      try
      {
        return iRemoveTask(taskId);
      }
      catch (ConnectionException e)
      {
        this.log.error(e.getMessage());
      }
    }
    return -1;
  }
  
  public int iaddOnceTask(String strAppId, String strObjectName, String strMethodName, java.util.Date startTime, java.util.Date startDate, int intStatus, String strUserName)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    int taskId = -1;
    PreparedStatement taskIdPS = null;
    ResultSet taskIdRS = null;
    PreparedStatement ps = null;
    PreparedStatement ps2 = null;
    ResultSet rs2 = null;
    try
    {
      taskIdPS = this.connection.prepareStatement("SELECT " + this.schema + ".taskid_seq.nextVal from dual");
      taskIdRS = taskIdPS.executeQuery();
      taskIdRS.next();
      taskId = taskIdRS.getInt(1);
      
      ps = this.connection.prepareStatement("INSERT INTO " + this.schema + ".task(taskid, taskType, appId, objectName, methodName, startTime, startDate, status, userName)" + " VALUES (?, 'ONCE', ?, ?, ?, to_date(?,'yyyy/mm/dd/hh24/mi/ss'), " + "to_date(?,'yyyy/mm/dd/hh24/mi/ss'), ?, ?)");
      


      ps.setInt(1, taskId);
      ps.setString(2, strAppId);
      ps.setString(3, strObjectName);
      ps.setString(4, strMethodName);
      ps.setString(5, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(startTime));
      ps.setString(6, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(startDate));
      ps.setString(7, "" + intStatus);
      ps.setString(8, strUserName);
      int i;
      if (ps.executeUpdate() < 0) {
        return 0;
      }
      return taskId;
    }
    catch (SQLException ex)
    {
      this.log.error("SQL exception:" + ex);
      if (DatabaseConnection.shareInstance().checkConnection()) {
        return -1;
      }
      this.sqlException = true;
      throw new ConnectionException(ex.getMessage(), "");
    }
    finally
    {
      try
      {
        if (ps != null) {
          ps.close();
        }
        if (rs2 != null) {
          rs2.close();
        }
        if (ps2 != null) {
          ps2.close();
        }
      }
      catch (Exception e)
      {
        this.log.debug("close ps " + e.getMessage());
      }
    }
  }
  
  public int addOnceTask(String strAppId, String strObjectName, String strMethodName, java.util.Date startTime, java.util.Date startDate, int intStatus, String strUserName)
  {
    try
    {
      return iaddOnceTask(strAppId, strObjectName, strMethodName, startTime, startDate, intStatus, strUserName);
    }
    catch (ConnectionException ce)
    {
      this.log.error(ce.getMessage());
      try
      {
        return iaddOnceTask(strAppId, strObjectName, strMethodName, startTime, startDate, intStatus, strUserName);
      }
      catch (ConnectionException e)
      {
        this.log.error(ce.getMessage());
      }
    }
    return -1;
  }
  
  public int iaddNonRepeatedDailyTask(String strAppId, String strObjectName, String strMethodName, java.util.Date startTime, java.util.Date startDate, java.util.Date endDate, int intStatus, String strUserName, int intRecurrenceTime)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    int taskId = -1;
    PreparedStatement taskIdPS = null;
    ResultSet taskIdRS = null;
    PreparedStatement ps = null;
    try
    {
      taskIdPS = this.connection.prepareStatement("SELECT " + this.schema + ".taskid_seq.nextVal from dual");
      taskIdRS = taskIdPS.executeQuery();
      taskIdRS.next();
      taskId = taskIdRS.getInt(1);
      ps = this.connection.prepareStatement("INSERT INTO " + this.schema + ".task(taskid, taskType, appId, objectName, methodName," + " startTime, startDate, endDate, status, userName)" + " VALUES (?, 'DAILY', ?, ?, ?, to_date(?,'yyyy/mm/dd/hh24/mi/ss'), " + "to_date(?,'yyyy/mm/dd/hh24/mi/ss'), to_date(?,'yyyy/mm/dd/hh24/mi/ss'), ?, ?)");
      



      ps.setInt(1, taskId);
      ps.setString(2, strAppId);
      ps.setString(3, strObjectName);
      ps.setString(4, strMethodName);
      ps.setString(5, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(startTime));
      ps.setString(6, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(startDate));
      ps.setString(7, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(endDate));
      ps.setString(8, "" + intStatus);
      ps.setString(9, strUserName);
      int i;
      if (ps.executeUpdate() < 0) {
        return 0;
      }
      ps.close();
      ps = this.connection.prepareStatement("INSERT INTO " + this.schema + ".DailyTask(Taskid, RECURRENCETIME) VALUES(?, ?)");
      
      ps.setInt(1, taskId);
      ps.setInt(2, intRecurrenceTime);
      if (ps.executeUpdate() < 0) {
        return 0;
      }
      return taskId;
    }
    catch (SQLException ex)
    {
      this.log.error("SQL exception:" + ex);
      if (DatabaseConnection.shareInstance().checkConnection()) {
        return -1;
      }
      this.sqlException = true;
      throw new ConnectionException(ex.getMessage(), "");
    }
    finally
    {
      try
      {
        if (ps != null) {
          ps.close();
        }
      }
      catch (Exception e)
      {
        this.log.debug("close ps " + e.getMessage());
      }
    }
  }
  
  public int addNonRepeatedDailyTask(String strAppId, String strObjectName, String strMethodName, java.util.Date startTime, java.util.Date startDate, java.util.Date endDate, int intStatus, String strUserName, int intRecurrenceTime)
  {
    try
    {
      return iaddNonRepeatedDailyTask(strAppId, strObjectName, strMethodName, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime);
    }
    catch (ConnectionException ce)
    {
      this.log.error(ce.getMessage());
      try
      {
        return iaddNonRepeatedDailyTask(strAppId, strObjectName, strMethodName, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime);
      }
      catch (ConnectionException e)
      {
        this.log.error(e.getMessage());
      }
    }
    return -1;
  }
  
  public int iaddRepeatedDailyTask(String strAppId, String strObjectName, String strMethodName, java.util.Date startTime, java.util.Date startDate, java.util.Date endDate, int intStatus, String strUserName, int intRecurrenceTime, int intRepeatedRecurrenceTime, int intRepeatedTime)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    int taskId = -1;
    int repeatedTaskId = -1;
    PreparedStatement repeatedTaskIdPS = null;
    ResultSet repeatedTaskIdRS = null;
    PreparedStatement taskIdPS = null;
    ResultSet taskIdRS = null;
    PreparedStatement ps = null;
    try
    {
      taskIdPS = this.connection.prepareStatement("SELECT " + this.schema + ".taskid_seq.nextVal from dual");
      taskIdRS = taskIdPS.executeQuery();
      taskIdRS.next();
      taskId = taskIdRS.getInt(1);
      
      repeatedTaskIdPS = this.connection.prepareStatement("SELECT " + this.schema + ".repeatedTaskid_seq.nextVal from dual");
      
      repeatedTaskIdRS = repeatedTaskIdPS.executeQuery();
      repeatedTaskIdRS.next();
      repeatedTaskId = repeatedTaskIdRS.getInt(1);
      
      ps = this.connection.prepareStatement("INSERT INTO " + this.schema + ".RepeatedTask(REPEATEDTASKID,REPEATEDRECURRENCETIME,REPEATEDTIMES) VALUES(?, ?, ?)");
      
      ps.setInt(1, repeatedTaskId);
      ps.setInt(2, intRepeatedRecurrenceTime);
      ps.setInt(3, intRepeatedTime);
      int i;
      if (ps.executeUpdate() < 0) {
        return 0;
      }
      ps = this.connection.prepareStatement("INSERT INTO " + this.schema + ".task(taskid, taskType, appId, objectName, methodName, startTime, " + "startDate, endDate, status, userName, repeatedTaskId)" + " VALUES (?, 'DAILY', ?, ?, ?, to_date(?,'yyyy/mm/dd/hh24/mi/ss')," + " to_date(?,'yyyy/mm/dd/hh24/mi/ss'), to_date(?,'yyyy/mm/dd/hh24/mi/ss'), ?, ?, ?)");
      



      ps.setInt(1, taskId);
      ps.setString(2, strAppId);
      ps.setString(3, strObjectName);
      ps.setString(4, strMethodName);
      ps.setString(5, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(startTime));
      ps.setString(6, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(startDate));
      ps.setString(7, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(endDate));
      ps.setString(8, "" + intStatus);
      ps.setString(9, strUserName);
      ps.setInt(10, repeatedTaskId);
      if (ps.executeUpdate() < 0) {
        return 0;
      }
      ps.close();
      ps = this.connection.prepareStatement("INSERT INTO " + this.schema + ".DailyTask(Taskid, RECURRENCETIME) VALUES(?, ?)");
      
      ps.setInt(1, taskId);
      ps.setInt(2, intRecurrenceTime);
      if (ps.executeUpdate() < 0) {
        return 0;
      }
      return taskId;
    }
    catch (SQLException ex)
    {
      this.log.error("SQL exception:" + ex);
      if (DatabaseConnection.shareInstance().checkConnection()) {
        return -1;
      }
      this.sqlException = true;
      throw new ConnectionException(ex.getMessage(), "");
    }
    finally
    {
      try
      {
        if (ps != null) {
          ps.close();
        }
        if (taskIdRS != null) {
          taskIdRS.close();
        }
        if (taskIdPS != null) {
          taskIdPS.close();
        }
        if (repeatedTaskIdRS != null) {
          repeatedTaskIdRS.close();
        }
        if (repeatedTaskIdPS != null) {
          repeatedTaskIdPS.close();
        }
      }
      catch (Exception e)
      {
        this.log.debug("close ps " + e.getMessage());
      }
    }
  }
  
  public int addRepeatedDailyTask(String strAppId, String strObjectName, String strMethodName, java.util.Date startTime, java.util.Date startDate, java.util.Date endDate, int intStatus, String strUserName, int intRecurrenceTime, int intRepeatedRecurrenceTime, int intRepeatedTime)
  {
    try
    {
      return iaddRepeatedDailyTask(strAppId, strObjectName, strMethodName, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime, intRepeatedRecurrenceTime, intRepeatedTime);
    }
    catch (ConnectionException ce)
    {
      this.log.error(ce.getMessage());
      try
      {
        return iaddRepeatedDailyTask(strAppId, strObjectName, strMethodName, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime, intRepeatedRecurrenceTime, intRepeatedTime);
      }
      catch (ConnectionException e)
      {
        this.log.error(ce.getMessage());
      }
    }
    return -1;
  }
  
  public int iaddNonRepeatedWeeklyTask(String strAppId, String strObjectName, String strMethodName, java.util.Date startTime, java.util.Date startDate, java.util.Date endDate, int intStatus, String strUserName, int intRecurrenceTime, String strDays)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    int taskId = -1;
    PreparedStatement taskIdPS = null;
    ResultSet taskIdRS = null;
    PreparedStatement ps = null;
    try
    {
      taskIdPS = this.connection.prepareStatement("SELECT " + this.schema + ".taskid_seq.nextVal from dual");
      taskIdRS = taskIdPS.executeQuery();
      taskIdRS.next();
      taskId = taskIdRS.getInt(1);
      
      ps = this.connection.prepareStatement("INSERT INTO " + this.schema + ".task(taskid, taskType, appId, objectName, methodName, startTime," + " startDate, endDate, status, userName)" + " VALUES (?, 'WEEKLY', ?, ?, ?, to_date(?,'yyyy/mm/dd/hh24/mi/ss'), " + "to_date(?,'yyyy/mm/dd/hh24/mi/ss'), to_date(?,'yyyy/mm/dd/hh24/mi/ss'), ?, ?)");
      



      ps.setInt(1, taskId);
      ps.setString(2, strAppId);
      ps.setString(3, strObjectName);
      ps.setString(4, strMethodName);
      ps.setString(5, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(startTime));
      ps.setString(6, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(startDate));
      ps.setString(7, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(endDate));
      ps.setString(8, "" + intStatus);
      ps.setString(9, strUserName);
      int i;
      if (ps.executeUpdate() < 0) {
        return 0;
      }
      ps.close();
      ps = this.connection.prepareStatement("INSERT INTO " + this.schema + ".WeeklyTask(Taskid, RECURRENCETIME, DAYS) VALUES(?, ?, ?)");
      
      ps.setInt(1, taskId);
      ps.setInt(2, intRecurrenceTime);
      ps.setString(3, strDays);
      if (ps.executeUpdate() < 0) {
        return 0;
      }
      return taskId;
    }
    catch (SQLException ex)
    {
      this.log.error("SQL exception:" + ex);
      if (DatabaseConnection.shareInstance().checkConnection()) {
        return -1;
      }
      this.sqlException = true;
      throw new ConnectionException(ex.getMessage(), "");
    }
    finally
    {
      try
      {
        if (ps != null) {
          ps.close();
        }
      }
      catch (Exception e)
      {
        this.log.debug("close ps " + e.getMessage());
      }
    }
  }
  
  public int addNonRepeatedWeeklyTask(String strAppId, String strObjectName, String strMethodName, java.util.Date startTime, java.util.Date startDate, java.util.Date endDate, int intStatus, String strUserName, int intRecurrenceTime, String strDays)
  {
    try
    {
      return iaddNonRepeatedWeeklyTask(strAppId, strObjectName, strMethodName, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime, strDays);
    }
    catch (ConnectionException ce)
    {
      this.log.error(ce.getMessage());
      try
      {
        return iaddNonRepeatedWeeklyTask(strAppId, strObjectName, strMethodName, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime, strDays);
      }
      catch (ConnectionException e)
      {
        this.log.error(e.getMessage());
      }
    }
    return -1;
  }
  
  private int iaddRepeatedWeeklyTask(String strAppId, String strObjectName, String strMethodName, java.util.Date startTime, java.util.Date startDate, java.util.Date endDate, int intStatus, String strUserName, int intRecurrenceTime, String strDays, int intRepeatedRecurrenceTime, int intRepeatedTime)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    int taskId = -1;
    int repeatedTaskId = -1;
    
    PreparedStatement repeatedTaskIdPS = null;
    ResultSet repeatedTaskIdRS = null;
    PreparedStatement taskIdPS = null;
    ResultSet taskIdRS = null;
    PreparedStatement ps = null;
    try
    {
      taskIdPS = this.connection.prepareStatement("SELECT " + this.schema + ".taskid_seq.nextVal from dual");
      taskIdRS = taskIdPS.executeQuery();
      taskIdRS.next();
      taskId = taskIdRS.getInt(1);
      
      repeatedTaskIdPS = this.connection.prepareStatement("SELECT " + this.schema + ".repeatedTaskid_seq.nextVal from dual");
      
      repeatedTaskIdRS = repeatedTaskIdPS.executeQuery();
      repeatedTaskIdRS.next();
      repeatedTaskId = repeatedTaskIdRS.getInt(1);
      

      ps = this.connection.prepareStatement("INSERT INTO " + this.schema + ".RepeatedTask(REPEATEDTASKID,RepeatedRECURRENCETIME,REPEATEDTIMES) VALUES(?, ?, ?)");
      
      ps.setInt(1, repeatedTaskId);
      ps.setInt(2, intRepeatedRecurrenceTime);
      ps.setInt(3, intRepeatedTime);
      int i;
      if (ps.executeUpdate() < 0) {
        return 0;
      }
      ps = this.connection.prepareStatement("INSERT INTO " + this.schema + ".task(taskid, taskType, appId, objectName, methodName, startTime, startDate, endDate," + " status, userName, repeatedTaskID)" + " VALUES (?, 'WEEKLY', ?, ?, ?, to_date(?,'yyyy/mm/dd/hh24/mi/ss')," + " to_date(?,'yyyy/mm/dd/hh24/mi/ss'), to_date(?,'yyyy/mm/dd/hh24/mi/ss'), ?, ?, ?)");
      



      ps.setInt(1, taskId);
      ps.setString(2, strAppId);
      ps.setString(3, strObjectName);
      ps.setString(4, strMethodName);
      ps.setString(5, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(startTime));
      ps.setString(6, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(startDate));
      ps.setString(7, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(endDate));
      ps.setString(8, "" + intStatus);
      ps.setString(9, strUserName);
      ps.setInt(10, repeatedTaskId);
      if (ps.executeUpdate() < 0) {
        return 0;
      }
      ps.close();
      ps = this.connection.prepareStatement("INSERT INTO " + this.schema + ".WeeklyTask(Taskid, RECURRENCETIME, DAYS) VALUES(?, ?, ?)");
      
      ps.setInt(1, taskId);
      ps.setInt(2, intRecurrenceTime);
      ps.setString(3, strDays);
      if (ps.executeUpdate() < 0) {
        return 0;
      }
      return taskId;
    }
    catch (SQLException ex)
    {
      this.log.error("SQL exception:" + ex);
      if (DatabaseConnection.shareInstance().checkConnection()) {
        return -1;
      }
      this.sqlException = true;
      throw new ConnectionException(ex.getMessage(), "");
    }
    finally
    {
      try
      {
        if (ps != null) {
          ps.close();
        }
        if (taskIdRS != null) {
          taskIdRS.close();
        }
        if (taskIdPS != null) {
          taskIdPS.close();
        }
        if (repeatedTaskIdRS != null) {
          repeatedTaskIdRS.close();
        }
        if (repeatedTaskIdPS != null) {
          repeatedTaskIdPS.close();
        }
      }
      catch (Exception e)
      {
        this.log.debug("close ps " + e.getMessage());
      }
    }
  }
  
  public int addRepeatedWeeklyTask(String strAppId, String strObjectName, String strMethodName, java.util.Date startTime, java.util.Date startDate, java.util.Date endDate, int intStatus, String strUserName, int intRecurrenceTime, String strDays, int intRepeatedRecurrenceTime, int intRepeatedTime)
  {
    try
    {
      return iaddRepeatedWeeklyTask(strAppId, strObjectName, strMethodName, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime, strDays, intRepeatedRecurrenceTime, intRepeatedTime);
    }
    catch (ConnectionException ce)
    {
      this.log.error(ce.getMessage());
      try
      {
        return iaddRepeatedWeeklyTask(strAppId, strObjectName, strMethodName, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime, strDays, intRepeatedRecurrenceTime, intRepeatedTime);
      }
      catch (ConnectionException e)
      {
        this.log.error(e.getMessage());
      }
    }
    return -1;
  }
  
  public int iaddNonRepeatedMonthlyTask(String strAppId, String strObjectName, String strMethodName, java.util.Date startTime, java.util.Date startDate, java.util.Date endDate, int intStatus, String strUserName, String strTaskMode, String strDays, String strMonths)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    int taskId = -1;
    PreparedStatement taskIdPS = null;
    ResultSet taskIdRS = null;
    PreparedStatement ps = null;
    try
    {
      taskIdPS = this.connection.prepareStatement("SELECT " + this.schema + ".taskid_seq.nextVal from dual");
      taskIdRS = taskIdPS.executeQuery();
      taskIdRS.next();
      taskId = taskIdRS.getInt(1);
      
      ps = this.connection.prepareStatement("INSERT INTO " + this.schema + ".task(taskid, taskType, appId, objectName, methodName, " + "startTime, startDate, endDate, status, userName)" + " VALUES (?, 'MONTHLY', ?, ?, ?, to_date(?,'yyyy/mm/dd/hh24/mi/ss')," + " to_date(?,'yyyy/mm/dd/hh24/mi/ss'), to_date(?,'yyyy/mm/dd/hh24/mi/ss'), ?, ?)");
      



      ps.setInt(1, taskId);
      ps.setString(2, strAppId);
      ps.setString(3, strObjectName);
      ps.setString(4, strMethodName);
      ps.setString(5, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(startTime));
      ps.setString(6, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(startDate));
      ps.setString(7, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(endDate));
      ps.setString(8, "" + intStatus);
      ps.setString(9, strUserName);
      int i;
      if (ps.executeUpdate() < 0) {
        return 0;
      }
      ps.close();
      ps = this.connection.prepareStatement("INSERT INTO " + this.schema + ".MonthlyTask(Taskid,  TASKMODE, DAYS, MONTHS) VALUES(?, ?, ?, ?)");
      
      ps.setInt(1, taskId);
      ps.setString(2, strTaskMode);
      ps.setString(3, strDays);
      ps.setString(4, strMonths);
      if (ps.executeUpdate() < 0) {
        return 0;
      }
      return taskId;
    }
    catch (SQLException ex)
    {
      this.log.error("SQL exception:" + ex);
      if (DatabaseConnection.shareInstance().checkConnection()) {
        return -1;
      }
      this.sqlException = true;
      throw new ConnectionException(ex.getMessage(), "");
    }
    finally
    {
      try
      {
        if (ps != null) {
          ps.close();
        }
      }
      catch (Exception e)
      {
        this.log.debug("close ps " + e.getMessage());
      }
    }
  }
  
  public int addNonRepeatedMonthlyTask(String strAppId, String strObjectName, String strMethodName, java.util.Date startTime, java.util.Date startDate, java.util.Date endDate, int intStatus, String strUserName, String strTaskMode, String strDays, String strMonths)
  {
    try
    {
      return iaddNonRepeatedMonthlyTask(strAppId, strObjectName, strMethodName, startTime, startDate, endDate, intStatus, strUserName, strTaskMode, strDays, strMonths);
    }
    catch (ConnectionException ce)
    {
      this.log.error(ce.getMessage());
      try
      {
        return iaddNonRepeatedMonthlyTask(strAppId, strObjectName, strMethodName, startTime, startDate, endDate, intStatus, strUserName, strTaskMode, strDays, strMonths);
      }
      catch (ConnectionException e)
      {
        this.log.error(e.getMessage());
      }
    }
    return -1;
  }
  
  private int iaddRepeatedMonthlyTask(String strAppId, String strObjectName, String strMethodName, java.util.Date startTime, java.util.Date startDate, java.util.Date endDate, int intStatus, String strUserName, String strTaskMode, String strDays, String strMonths, int intRepeatedRecurrenceTime, int intRepeatedTime)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    int taskId = -1;
    int repeatedTaskId = -1;
    
    PreparedStatement repeatedTaskIdPS = null;
    ResultSet repeatedTaskIdRS = null;
    PreparedStatement taskIdPS = null;
    ResultSet taskIdRS = null;
    PreparedStatement ps = null;
    try
    {
      taskIdPS = this.connection.prepareStatement("SELECT " + this.schema + ".taskid_seq.nextVal from dual");
      taskIdRS = taskIdPS.executeQuery();
      taskIdRS.next();
      taskId = taskIdRS.getInt(1);
      
      repeatedTaskIdPS = this.connection.prepareStatement("SELECT " + this.schema + ".repeatedTaskid_seq.nextVal from dual");
      
      repeatedTaskIdRS = repeatedTaskIdPS.executeQuery();
      repeatedTaskIdRS.next();
      repeatedTaskId = repeatedTaskIdRS.getInt(1);
      

      ps = this.connection.prepareStatement("INSERT INTO " + this.schema + ".RepeatedTask(REPEATEDTASKID,RepeatedRECURRENCETIME,REPEATEDTIMES) VALUES(?, ?, ?)");
      
      ps.setInt(1, repeatedTaskId);
      ps.setInt(2, intRepeatedRecurrenceTime);
      ps.setInt(3, intRepeatedTime);
      int i;
      if (ps.executeUpdate() < 0) {
        return 0;
      }
      ps = this.connection.prepareStatement("INSERT INTO " + this.schema + ".task(taskid, taskType, appId, objectName, methodName, startTime, startDate, endDate," + " status, userName, repeatedTaskId)" + " VALUES (?, 'MONTHLY', ?, ?, ?, to_date(?,'yyyy/mm/dd/hh24/mi/ss')," + " to_date(?,'yyyy/mm/dd/hh24/mi/ss'), to_date(?,'yyyy/mm/dd/hh24/mi/ss'), ?, ?, ?)");
      



      ps.setInt(1, taskId);
      ps.setString(2, strAppId);
      ps.setString(3, strObjectName);
      ps.setString(4, strMethodName);
      ps.setString(5, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(startTime));
      ps.setString(6, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(startDate));
      ps.setString(7, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(endDate));
      ps.setString(8, "" + intStatus);
      ps.setString(9, strUserName);
      ps.setInt(10, repeatedTaskId);
      if (ps.executeUpdate() < 0) {
        return 0;
      }
      ps.close();
      ps = this.connection.prepareStatement("INSERT INTO " + this.schema + ".MonthlyTask(Taskid, TASKMODE, DAYS, MONTHS) VALUES(?,  ?, ?, ?)");
      
      ps.setInt(1, taskId);
      ps.setString(2, strTaskMode);
      ps.setString(3, strDays);
      ps.setString(4, strMonths);
      if (ps.executeUpdate() < 0) {
        return 0;
      }
      return taskId;
    }
    catch (SQLException ex)
    {
      this.log.error("SQL exception:" + ex);
      if (DatabaseConnection.shareInstance().checkConnection()) {
        return -1;
      }
      this.sqlException = true;
      throw new ConnectionException(ex.getMessage(), "");
    }
    finally
    {
      try
      {
        if (ps != null) {
          ps.close();
        }
        if (taskIdRS != null) {
          taskIdRS.close();
        }
        if (taskIdPS != null) {
          taskIdPS.close();
        }
        if (repeatedTaskIdRS != null) {
          repeatedTaskIdRS.close();
        }
        if (repeatedTaskIdPS != null) {
          repeatedTaskIdPS.close();
        }
      }
      catch (Exception e)
      {
        this.log.debug("close ps " + e.getMessage());
      }
    }
  }
  
  public int addRepeatedMonthlyTask(String strAppId, String strObjectName, String strMethodName, java.util.Date startTime, java.util.Date startDate, java.util.Date endDate, int intStatus, String strUserName, String strTaskMode, String strDays, String strMonths, int intRepeatedRecurrenceTime, int intRepeatedTime)
  {
    try
    {
      return iaddRepeatedMonthlyTask(strAppId, strObjectName, strMethodName, startTime, startDate, endDate, intStatus, strUserName, strTaskMode, strDays, strMonths, intRepeatedRecurrenceTime, intRepeatedTime);
    }
    catch (ConnectionException ce)
    {
      this.log.error(ce.getMessage());
      try
      {
        return iaddRepeatedMonthlyTask(strAppId, strObjectName, strMethodName, startTime, startDate, endDate, intStatus, strUserName, strTaskMode, strDays, strMonths, intRepeatedRecurrenceTime, intRepeatedTime);
      }
      catch (ConnectionException e)
      {
        this.log.error(e.getMessage());
      }
    }
    return -1;
  }
  
  private int ieditTask(int taskId, String strObjectName, String strMethodName, String strTaskType, java.util.Date startTime, java.util.Date startDate, java.util.Date endDate, int intStatus, String strUserName, int intRecurrenceTime, String strTaskMode, String strDayList, String strMonthList, boolean hasRepeatedTask, int repeatedTaskId, int repeatedRecurrenceTime, int repeatedTimes)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    PreparedStatement ps = null;
    PreparedStatement ps2 = null;
    PreparedStatement ps3 = null;
    String oldTaskType = "";
    int oldRepeatedTaskId = 0;
    try
    {
      ps = this.connection.prepareStatement("select TASKTYPE, REPEATEDTASKID from " + this.schema + ".TASK where TASKID=?");
      ps.setInt(1, taskId);
      ResultSet rs = ps.executeQuery();
      if (rs.next())
      {
        oldTaskType = rs.getString(1);
        oldRepeatedTaskId = rs.getInt(2);
        rs.close();
      }
      else
      {
        return 0;
      }
      ps = this.connection.prepareStatement("UPDATE " + this.schema + ".task " + "SET  objectName = ? , " + " methodName = ? , " + " taskType = ? , " + " startTime = to_date(?,'yyyy/mm/dd/hh24/mi/ss') , " + " startDate = to_date(?,'yyyy/mm/dd/hh24/mi/ss') , " + " endDate = to_date(?,'yyyy/mm/dd/hh24/mi/ss') , " + " status = ? , " + " userName = ?, " + " repeatedTaskId = ?" + " WHERE taskId = ?");
      









      ps.setString(1, strObjectName);
      ps.setString(2, strMethodName);
      ps.setString(3, strTaskType);
      ps.setString(4, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(startTime));
      ps.setString(5, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(startDate));
      ps.setString(6, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(endDate));
      ps.setString(7, "" + intStatus);
      ps.setString(8, strUserName);
      ps.setInt(10, taskId);
      ResultSet repeatedTaskIdRS;
      if (hasRepeatedTask)
      {
        if (oldRepeatedTaskId == 0)
        {
          PreparedStatement repeatedTaskIdPS = this.connection.prepareStatement("SELECT " + this.schema + ".repeatedTaskid_seq.nextVal from dual");
          
          repeatedTaskIdRS = repeatedTaskIdPS.executeQuery();
          int newRepeatedTaskId;
          if (repeatedTaskIdRS.next()) {
            newRepeatedTaskId = repeatedTaskIdRS.getInt(1);
          } else {
            return 0;
          }

          PreparedStatement insertRepeatedTaskPS = this.connection.prepareStatement("INSERT INTO " + this.schema + ".RepeatedTask(REPEATEDTASKID,RepeatedRECURRENCETIME,REPEATEDTIMES) VALUES(?, ?, ?)");
          
          insertRepeatedTaskPS.setInt(1, newRepeatedTaskId);
          insertRepeatedTaskPS.setInt(2, repeatedRecurrenceTime);
          insertRepeatedTaskPS.setInt(3, repeatedTimes);
          if (insertRepeatedTaskPS.executeUpdate() < 0) {
            return 0;
          }
          ps.setInt(9, newRepeatedTaskId);
        }
        else
        {
          ps2 = this.connection.prepareStatement("UPDATE " + this.schema + ".repeatedTask " + "SET repeatedRecurrenceTime = ? , " + " repeatedTimes = ? WHERE repeatedTaskId = ? ");
          

          ps2.setInt(1, repeatedRecurrenceTime);
          ps2.setInt(2, repeatedTimes);
          ps2.setInt(3, oldRepeatedTaskId);
          ps.setInt(9, oldRepeatedTaskId);
        }
      }
      else
      {
        ps.setNull(9, 4);
        if (oldRepeatedTaskId != 0)
        {
          ps2 = this.connection.prepareStatement("DELETE FROM " + this.schema + ".repeatedTask WHERE repeatedTaskId = ? ");
          
          ps2.setInt(1, oldRepeatedTaskId);
        }
      }
      if (oldTaskType.equalsIgnoreCase(strTaskType))
      {
        if (strTaskType.equalsIgnoreCase("Daily"))
        {
          ps3 = this.connection.prepareStatement("UPDATE " + this.schema + ".dailyTask SET recurrenceTime = ? WHERE taskId = ? ");
          
          ps3.setInt(1, intRecurrenceTime);
          ps3.setInt(2, taskId);
        }
        if (strTaskType.equalsIgnoreCase("Weekly"))
        {
          ps3 = this.connection.prepareStatement("UPDATE " + this.schema + ".weeklyTask SET recurrenceTime = ? , days = ? WHERE taskId = ? ");
          
          ps3.setInt(1, intRecurrenceTime);
          ps3.setString(2, strDayList);
          ps3.setInt(3, taskId);
        }
        if (strTaskType.equalsIgnoreCase("Monthly"))
        {
          ps3 = this.connection.prepareStatement("UPDATE " + this.schema + ".monthlyTask SET taskMode = ? , days = ? , months = ? WHERE taskId = ? ");
          
          ps3.setString(1, strTaskMode);
          ps3.setString(2, strDayList);
          ps3.setString(3, strMonthList);
          ps3.setInt(4, taskId);
        }
      }
      else
      {
        if (!oldTaskType.equalsIgnoreCase("Once"))
        {
          PreparedStatement deletePS = this.connection.prepareStatement("delete FROM " + this.schema + "." + oldTaskType + "task where taskId = ?");
          
          deletePS.setInt(1, taskId);
          deletePS.executeUpdate();
        }
        PreparedStatement insertPS = null;
        if (strTaskType.equalsIgnoreCase("Daily"))
        {
          insertPS = this.connection.prepareStatement("INSERT into " + this.schema + ".DAILYTASK(taskID,recurrenceTime) values(?,?)");
          
          insertPS.setInt(1, taskId);
          insertPS.setInt(2, intRecurrenceTime);
        }
        if (strTaskType.equalsIgnoreCase("Weekly"))
        {
          insertPS = this.connection.prepareStatement("INSERT into " + this.schema + ".WEEKLYTASK(taskID,recurrenceTime,days) values(?,?,?) ");
          
          insertPS.setInt(1, taskId);
          insertPS.setInt(2, intRecurrenceTime);
          insertPS.setString(3, strDayList);
        }
        if (strTaskType.equalsIgnoreCase("Monthly"))
        {
          insertPS = this.connection.prepareStatement("INSERT into " + this.schema + ".MONTHLYTASK(taskID,taskMode, days, months) values(?,?,?,?)");
          
          insertPS.setInt(1, taskId);
          insertPS.setString(2, strTaskMode);
          insertPS.setString(3, strDayList);
          insertPS.setString(4, strMonthList);
        }
        if ((insertPS != null) && 
          (insertPS.executeUpdate() < 0)) {
          return 0;
        }
      }
      if (ps.executeUpdate() < 0) {
        return 0;
      }
      if ((ps2 != null) && 
        (ps2.executeUpdate() < 0)) {
        return 0;
      }
      if ((ps3 != null) && 
        (ps3.executeUpdate() < 0)) {
        return 0;
      }
      return taskId;
    }
    catch (SQLException ex)
    {
      PreparedStatement insertPS;
      int j;
      this.log.error("SQL exception:" + ex);
      if (DatabaseConnection.shareInstance().checkConnection()) {
        return -1;
      }
      this.sqlException = true;
      throw new ConnectionException(ex.getMessage(), "");
    }
    finally
    {
      try
      {
        if (ps != null) {
          ps.close();
        }
        if (ps2 != null) {
          ps2.close();
        }
        if (ps3 != null) {
          ps3.close();
        }
      }
      catch (SQLException e)
      {
        Log.warn(e);
      }
    }
  }
  
  public int editTask(int taskId, String strObjectName, String strMethodName, String strTaskType, java.util.Date startTime, java.util.Date startDate, java.util.Date endDate, int intStatus, String strUserName, int intRecurrenceTime, String strTaskMode, String strDayList, String strMonthList, boolean hasRepeatedTask, int repeatedTaskId, int repeatedRecurrenceTime, int repeatedTimes)
  {
    try
    {
      return ieditTask(taskId, strObjectName, strMethodName, strTaskType, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime, strTaskMode, strDayList, strMonthList, hasRepeatedTask, repeatedTaskId, repeatedRecurrenceTime, repeatedTimes);
    }
    catch (ConnectionException ce)
    {
      this.log.error(ce.getMessage());
      try
      {
        return ieditTask(taskId, strObjectName, strMethodName, strTaskType, startTime, startDate, endDate, intStatus, strUserName, intRecurrenceTime, strTaskMode, strDayList, strMonthList, hasRepeatedTask, repeatedTaskId, repeatedRecurrenceTime, repeatedTimes);
      }
      catch (ConnectionException e)
      {
        this.log.error(e.getMessage());
      }
    }
    return -1;
  }
  
  private int iinverseStatus(int taskId)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    PreparedStatement PS1 = null;PreparedStatement PS2 = null;
    ResultSet RS1 = null;
    try
    {
      PS1 = this.connection.prepareStatement("select STATUS from " + this.schema + ".task where taskId = ? ");
      PS1.setInt(1, taskId);
      RS1 = PS1.executeQuery();
      RS1.next();
      int status = Integer.valueOf(RS1.getString("STATUS")).intValue();
      if (status == 1)
      {
        PS2 = this.connection.prepareStatement("update " + this.schema + ".TASK set STATUS = 0 where taskId = ?");
        PS2.setInt(1, taskId);
      }
      else if (status == 0)
      {
        PS2 = this.connection.prepareStatement("update " + this.schema + ".TASK set STATUS = 1 where taskId = ?");
        PS2.setInt(1, taskId);
      }
      else
      {
        return -1;
      }
      PS2.executeUpdate();
      return status == 1 ? 0 : 1;
    }
    catch (SQLException ex)
    {
      int i;
      this.log.error("SQL exception:" + ex);
      if (DatabaseConnection.shareInstance().checkConnection()) {
        return -1;
      }
      this.sqlException = true;
      throw new ConnectionException(ex.getMessage(), "");
    }
    finally
    {
      try
      {
        if (RS1 != null) {
          RS1.close();
        }
        if (PS1 != null) {
          PS1.close();
        }
        if (PS2 != null) {
          PS2.close();
        }
      }
      catch (SQLException e) {}
    }
  }
  
  public int inverseStatus(int taskId)
  {
    try
    {
      return iinverseStatus(taskId);
    }
    catch (ConnectionException ce)
    {
      this.log.error(ce.getMessage());
      try
      {
        return iinverseStatus(taskId);
      }
      catch (ConnectionException e)
      {
        this.log.error(e.getMessage());
      }
    }
    return -1;
  }
  
  private int isetStatus(int taskId, int status)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    PreparedStatement PS = null;
    try
    {
      PS = this.connection.prepareStatement("update " + this.schema + ".TASK set STATUS = ? where taskId = ?");
      PS.setString(1, "" + status);
      PS.setInt(2, taskId);
      
      PS.executeUpdate();
      return status;
    }
    catch (SQLException ex)
    {
      this.log.error("SQL exception:" + ex);
      if (DatabaseConnection.shareInstance().checkConnection()) {
        return -1;
      }
      this.sqlException = true;
      throw new ConnectionException(ex.getMessage(), "");
    }
    finally
    {
      try
      {
        if (PS != null) {
          PS.close();
        }
      }
      catch (SQLException e)
      {
        Log.error(e);
      }
    }
  }
  
  public int setStatus(int taskId, int status)
  {
    try
    {
      return isetStatus(taskId, status);
    }
    catch (ConnectionException ce)
    {
      this.log.error(ce.getMessage());
      try
      {
        return isetStatus(taskId, status);
      }
      catch (ConnectionException e)
      {
        this.log.error(e.getMessage());
      }
    }
    return -1;
  }
  
  private ArrayList<Task> iloadTaskList(String appId)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    PreparedStatement onceTaskPS = null;
    ResultSet onceTaskRS = null;
    
    PreparedStatement dailyTaskPS = null;
    ResultSet dailyTaskRS = null;
    
    PreparedStatement weeklyTaskPS = null;
    ResultSet weeklyTaskRS = null;
    
    PreparedStatement monthlyTaskPS = null;
    ResultSet monthlyTaskRS = null;
    
    ArrayList<Task> taskList = new ArrayList();
    if (this.connection == null) {
      return null;
    }
    try
    {
      onceTaskPS = this.connection.prepareStatement("SELECT * FROM " + this.schema + ".task, " + this.schema + ".repeatedtask " + "WHERE  ((endDate is NULL) OR (sysdate <= endDate)) AND (" + this.schema + ".task.appId = ? )  AND (" + this.schema + ".task.taskType = 'ONCE') " + "AND " + this.schema + ".task.repeatedTaskId = " + this.schema + ".repeatedTask.repeatedTaskId(+) ");
      


      dailyTaskPS = this.connection.prepareStatement("SELECT * FROM " + this.schema + ".task," + this.schema + ".dailyTask, " + this.schema + ".repeatedtask " + "WHERE ((endDate is NULL) OR (sysdate <= endDate)) AND (" + this.schema + ".task.appId = ? )  AND (" + this.schema + ".task.taskId = " + this.schema + ".dailyTask.taskId) " + "AND " + this.schema + ".task.repeatedTaskId = " + this.schema + ".repeatedTask.repeatedTaskId(+) ");
      



      weeklyTaskPS = this.connection.prepareStatement("SELECT * FROM " + this.schema + ".task," + this.schema + ".weeklyTask, " + this.schema + ".repeatedtask " + "WHERE ((endDate is NULL) OR (sysdate <= endDate)) AND (" + this.schema + ".task.appId = ? )  AND (" + this.schema + ".task.taskId = " + this.schema + ".weeklyTask.taskId) " + "AND " + this.schema + ".task.repeatedTaskId = " + this.schema + ".repeatedTask.repeatedTaskId(+) ");
      



      monthlyTaskPS = this.connection.prepareStatement("SELECT * FROM " + this.schema + ".task," + this.schema + ".monthlyTask, " + this.schema + ".repeatedtask " + "WHERE ((endDate is NULL) OR (sysdate <= endDate)) AND (" + this.schema + ".task.appId = ? )  AND (" + this.schema + ".task.taskId = " + this.schema + ".monthlyTask.taskId) " + "AND " + this.schema + ".task.repeatedTaskId = " + this.schema + ".repeatedTask.repeatedTaskId(+) ");
      



      onceTaskPS.setString(1, appId);
      dailyTaskPS.setString(1, appId);
      weeklyTaskPS.setString(1, appId);
      monthlyTaskPS.setString(1, appId);
      onceTaskRS = onceTaskPS.executeQuery();
      dailyTaskRS = dailyTaskPS.executeQuery();
      weeklyTaskRS = weeklyTaskPS.executeQuery();
      monthlyTaskRS = monthlyTaskPS.executeQuery();
      while (onceTaskRS.next())
      {
        int taskId = onceTaskRS.getInt("taskId");
        String strObjectName = onceTaskRS.getString("objectName");
        String strMethod = onceTaskRS.getString("methodName");
        java.util.Date startTime = new java.util.Date(onceTaskRS.getTimestamp("StartTime").getTime());
        java.util.Date startDate = onceTaskRS.getDate("startDate");
        java.util.Date endDate = onceTaskRS.getDate("EndDate");
        String userName = onceTaskRS.getString("userName");
        int intStatus = Integer.valueOf(onceTaskRS.getString("status")).intValue();
        taskList.add(new OnceTask(taskId, appId, strObjectName, strMethod, Scheduler.TaskType.ONCE, startTime, startDate, endDate, intStatus, userName));
      }
      while (dailyTaskRS.next())
      {
        int taskId = dailyTaskRS.getInt("taskId");
        String strObjectName = dailyTaskRS.getString("objectName");
        String strMethod = dailyTaskRS.getString("methodName");
        java.util.Date startTime = new java.util.Date(dailyTaskRS.getTimestamp("StartTime").getTime());
        java.util.Date startDate = dailyTaskRS.getDate("startDate");
        java.util.Date endDate = dailyTaskRS.getDate("EndDate");
        String userName = dailyTaskRS.getString("userName");
        int recurrenceTime = dailyTaskRS.getInt("recurrenceTime");
        
        int intStatus = Integer.valueOf(dailyTaskRS.getString("status")).intValue();
        DailyTask dailyTask = new DailyTask(taskId, appId, strObjectName, strMethod, Scheduler.TaskType.DAILY, startTime, startDate, endDate, intStatus, userName, recurrenceTime);
        
        int repeatedTaskId = dailyTaskRS.getInt("repeatedTaskId");
        if (repeatedTaskId > 0) {
          dailyTask.setRepeatedTask(new RepeatedTask(dailyTaskRS.getInt("repeatedRecurrenceTime"), dailyTaskRS.getInt("repeatedTimes")));
        }
        taskList.add(dailyTask);
      }
      while (weeklyTaskRS.next())
      {
        int taskId = weeklyTaskRS.getInt("taskId");
        String strObjectName = weeklyTaskRS.getString("objectName");
        String strMethod = weeklyTaskRS.getString("methodName");
        java.util.Date startTime = new java.util.Date(weeklyTaskRS.getTimestamp("StartTime").getTime());
        java.util.Date startDate = weeklyTaskRS.getDate("startDate");
        java.util.Date endDate = weeklyTaskRS.getDate("EndDate");
        String userName = weeklyTaskRS.getString("userName");
        int recurrenceTime = weeklyTaskRS.getInt("recurrenceTime");
        String dayList = weeklyTaskRS.getString("days");
        int intStatus = Integer.valueOf(weeklyTaskRS.getString("status")).intValue();
        WeeklyTask weeklyTask = new WeeklyTask(taskId, appId, strObjectName, strMethod, Scheduler.TaskType.WEEKLY, startTime, startDate, endDate, intStatus, userName, recurrenceTime, dayList);
        
        int repeatedTaskId = weeklyTaskRS.getInt("repeatedTaskId");
        if (repeatedTaskId > 0) {
          weeklyTask.setRepeatedTask(new RepeatedTask(weeklyTaskRS.getInt("repeatedRecurrenceTime"), weeklyTaskRS.getInt("repeatedTimes")));
        }
        taskList.add(weeklyTask);
      }
      while (monthlyTaskRS.next())
      {
        int taskId = monthlyTaskRS.getInt("taskId");
        String strObjectName = monthlyTaskRS.getString("objectName");
        String strMethod = monthlyTaskRS.getString("methodName");
        java.util.Date startTime = new java.util.Date(monthlyTaskRS.getTimestamp("StartTime").getTime());
        java.util.Date startDate = monthlyTaskRS.getDate("startDate");
        java.util.Date endDate = monthlyTaskRS.getDate("EndDate");
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
        
        String userName = monthlyTaskRS.getString("userName");
        String taskMode = monthlyTaskRS.getString("taskMode");
        String dayList = monthlyTaskRS.getString("days");
        String monthList = monthlyTaskRS.getString("months");
        int intStatus = Integer.valueOf(monthlyTaskRS.getString("status")).intValue();
        MonthlyTask monthlyTask = new MonthlyTask(taskId, appId, strObjectName, strMethod, Scheduler.TaskType.MONTHLY, startTime, startDate, endDate, intStatus, userName, taskMode, dayList, monthList);
        
        int repeatedTaskId = monthlyTaskRS.getInt("repeatedTaskId");
        if (repeatedTaskId > 0) {
          monthlyTask.setRepeatedTask(new RepeatedTask(monthlyTaskRS.getInt("repeatedRecurrenceTime"), monthlyTaskRS.getInt("repeatedTimes")));
        }
        taskList.add(monthlyTask);
      }
    }
    catch (SQLException ex)
    {
      String strObjectName;
      ex.printStackTrace();
      this.log.error("SQL exception:" + ex);
      if (DatabaseConnection.shareInstance().checkConnection()) {
        return taskList;
      }
      this.sqlException = true;
      throw new ConnectionException(ex.getMessage(), "");
    }
    finally
    {
      try
      {
        if (onceTaskRS != null) {
          onceTaskRS.close();
        }
        if (onceTaskPS != null) {
          onceTaskPS.close();
        }
        if (dailyTaskRS != null) {
          dailyTaskRS.close();
        }
        if (dailyTaskPS != null) {
          dailyTaskPS.close();
        }
        if (weeklyTaskRS != null) {
          weeklyTaskRS.close();
        }
        if (weeklyTaskPS != null) {
          weeklyTaskPS.close();
        }
        if (monthlyTaskRS != null) {
          monthlyTaskRS.close();
        }
        if (monthlyTaskPS != null) {
          monthlyTaskPS.close();
        }
      }
      catch (Exception e)
      {
        this.log.debug("close ps " + e.getMessage());
      }
    }
    return taskList;
  }
  
  private boolean iinsertSchedulingLog(int taskId, String action, java.util.Date actionTime, String preCondition, String postCondition, String success)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    if (this.insertSchedulingLogPS != null)
    {
      try
      {
        this.insertSchedulingLogPS.setInt(1, taskId);
        this.insertSchedulingLogPS.setString(2, action);
        this.insertSchedulingLogPS.setString(3, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(actionTime));
        this.insertSchedulingLogPS.setString(4, preCondition);
        this.insertSchedulingLogPS.setString(5, postCondition);
        this.insertSchedulingLogPS.setString(6, success);
        this.insertSchedulingLogPS.executeUpdate();
        return true;
      }
      catch (Exception e)
      {
        if (DatabaseConnection.shareInstance().checkConnection())
        {
          this.log.error("Exception when insert scheduling log ", e);
        }
        else
        {
          this.sqlException = true;
          this.log.error("Problem with database. Exception when insert scheduling log ", e);
          throw new ConnectionException(e.getMessage(), "");
        }
      }
      return false;
    }
    this.log.error("Problem with database.Could not insert scheduling log into Database.");
    return false;
  }
  
  public synchronized boolean insertSchedulingLog(int taskId, String action, java.util.Date actionTime, String preCondition, String postCondition, String success)
  {
    try
    {
      return iinsertSchedulingLog(taskId, action, actionTime, preCondition, postCondition, success);
    }
    catch (ConnectionException ce)
    {
      this.log.error("insert Scheduling Log failed." + ce.getMessage());
      try
      {
        return iinsertSchedulingLog(taskId, action, actionTime, preCondition, postCondition, success);
      }
      catch (ConnectionException e)
      {
        this.log.error("insert Scheduling Log failed againt." + e.getMessage());
      }
    }
    return false;
  }
  
  public ArrayList<Task> loadTaskList(String appId)
  {
    try
    {
      return iloadTaskList(appId);
    }
    catch (ConnectionException ce)
    {
      this.log.error(ce.getMessage());
      try
      {
        return iloadTaskList(appId);
      }
      catch (ConnectionException e)
      {
        this.log.error(e.getMessage());
      }
    }
    return new ArrayList();
  }
  
  private HashMap<Integer, ErrorDefinition> igetErrorDefinition(String appId)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    HashMap<Integer, ErrorDefinition> errorDefList = new HashMap();
    ResultSet errorDefRS = null;
    try
    {
      this.selectErrorDefinitionPS.setString(1, appId);
      this.selectErrorDefinitionPS.setInt(2, 1);
      
      errorDefRS = this.selectErrorDefinitionPS.executeQuery();
      int errorId;
      while (errorDefRS.next())
      {
        errorId = errorDefRS.getInt("errorId");
        String userName = errorDefRS.getString("username");
        java.util.Date createTime = null;
        if (errorDefRS.getTimestamp("createTime") != null) {
          createTime = new java.util.Date(errorDefRS.getTimestamp("createTime").getTime());
        }
        boolean useFlag = errorDefRS.getInt("useFlag") != 0;
        
        String logLevel = errorDefRS.getString("logLevel");
        ArrayList<ErrorCondition> conditions = new ArrayList();
        try
        {
          if ((logLevel != null) && (!logLevel.equals(""))) {
            conditions.add(new ErrorCondition("LOGLEVEL", logLevel));
          }
          String loggerName = errorDefRS.getString("loggerName");
          if ((loggerName != null) && (!loggerName.equals(""))) {
            conditions.add(new ErrorCondition("LOGGERNAME", loggerName));
          }
          String message = errorDefRS.getString("message");
          if ((message != null) && (!message.equals(""))) {
            conditions.add(new ErrorCondition("MESSAGE", message));
          }
          String threadName = errorDefRS.getString("threadName");
          if ((threadName != null) && (!threadName.equals(""))) {
            conditions.add(new ErrorCondition("THREADNAME", threadName));
          }
          String smsFormat = errorDefRS.getString("smsFormat");
          String smsContent = errorDefRS.getString("smsContent");
          boolean sendSMS = errorDefRS.getBoolean("sendSMS");
          String errorFrequence = errorDefRS.getString("error_frequence");
          
          ErrorDefinition ed = new ErrorDefinition(errorId, appId, userName, createTime, useFlag, conditions, smsFormat, smsContent, sendSMS);
          if ((errorFrequence != null) && (!errorFrequence.equals(""))) {
            try
            {
              String[] temp = errorFrequence.split("-");
              if (temp.length == 2)
              {
                int numOfError_EF = Integer.valueOf(temp[0]).intValue();
                int minute_EF = Integer.valueOf(temp[1]).intValue();
                if ((numOfError_EF > 1) && (minute_EF > 0)) {
                  ed.setNumOfErrorOfErrorFrequence(numOfError_EF);
                }
                ed.setMinutesOfErrorFrequence(minute_EF);
              }
            }
            catch (Exception e)
            {
              Log.warn("Exception when read error definition" + e);
            }
          }
          errorDefList.put(Integer.valueOf(errorId), ed);
        }
        catch (IllegalArgumentException iae)
        {
          Log.warn("Exception " + iae);
        }
      }
      return errorDefList;
    }
    catch (SQLException ex)
    {
      Log.warn("Exception when select error definition:" + ex);
      if (!DatabaseConnection.shareInstance().checkConnection())
      {
        this.sqlException = true;
        throw new ConnectionException(ex.getMessage(), "");
      }
      return null;
    }
    catch (Exception e)
    {
      String userName;
      Log.warn(e);
      return null;
    }
    finally
    {
      try
      {
        if (errorDefRS != null) {
          errorDefRS.close();
        }
      }
      catch (Exception e)
      {
        Log.warn(e.toString());
      }
    }
  }
  
  public synchronized HashMap<Integer, ErrorDefinition> getErrorDefinition(String appId)
  {
    try
    {
      return igetErrorDefinition(appId);
    }
    catch (ConnectionException ex)
    {
      Log.info("connection exception:" + ex);
      Log.info("connection is closed before, retry again");
      try
      {
        return igetErrorDefinition(appId);
      }
      catch (ConnectionException e)
      {
        Log.info("connection exception:" + e);
        Log.info("can't create connection");
      }
    }
    return null;
  }
  
  private int iinsertErrorDefinition(String appId, String userName, java.util.Date createTime, boolean useFlag, String logLevel, String loggerName, String message, String threadName, String smsFormat, String smsContent, boolean sendSMS, String error_Frequence)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    ResultSet errorIdRS = null;
    int errorId = 0;
    try
    {
      errorIdRS = this.errorIdSeqPS.executeQuery();
      
      errorIdRS.next();
      errorId = errorIdRS.getInt(1);
      this.insertErrorDefinitionPS.setInt(1, errorId);
      this.insertErrorDefinitionPS.setString(2, appId);
      this.insertErrorDefinitionPS.setString(3, userName);
      this.insertErrorDefinitionPS.setDate(4, new java.sql.Date(createTime.getTime()));
      if (useFlag == true) {
        this.insertErrorDefinitionPS.setInt(5, 1);
      } else {
        this.insertErrorDefinitionPS.setInt(5, 0);
      }
      if ((logLevel != null) && (!logLevel.equals(""))) {
        this.insertErrorDefinitionPS.setString(6, logLevel);
      } else {
        this.insertErrorDefinitionPS.setString(6, "");
      }
      if ((loggerName != null) && (!loggerName.equals(""))) {
        this.insertErrorDefinitionPS.setString(7, loggerName);
      } else {
        this.insertErrorDefinitionPS.setString(7, "");
      }
      if ((message != null) && (!message.equals(""))) {
        this.insertErrorDefinitionPS.setString(8, message);
      } else {
        this.insertErrorDefinitionPS.setString(8, "");
      }
      if ((threadName != null) && (!threadName.equals(""))) {
        this.insertErrorDefinitionPS.setString(9, threadName);
      } else {
        this.insertErrorDefinitionPS.setString(9, "");
      }
      this.insertErrorDefinitionPS.setString(10, smsFormat);
      this.insertErrorDefinitionPS.setString(11, smsContent);
      
      this.insertErrorDefinitionPS.setBoolean(12, sendSMS);
      if (error_Frequence != null) {
        this.insertErrorDefinitionPS.setString(13, error_Frequence);
      } else {
        this.insertErrorDefinitionPS.setString(13, "");
      }
      if (this.insertErrorDefinitionPS.executeUpdate() < 0) {
        errorId = 0;
      }
      return errorId;
    }
    catch (SQLException ex)
    {
      Log.warn("Exception when insert definition:" + ex);
      if (!DatabaseConnection.shareInstance().checkConnection())
      {
        this.sqlException = true;
        throw new ConnectionException(ex.getMessage(), "");
      }
      return -1;
    }
    catch (Exception e)
    {
      int j;
      Log.warn(e);
      return -1;
    }
    finally
    {
      try
      {
        if (errorIdRS != null) {
          errorIdRS.close();
        }
      }
      catch (Exception e)
      {
        Log.warn(e.toString());
      }
    }
  }
  
  public synchronized int insertErrorDefinition(String appId, String userName, java.util.Date createTime, boolean useFlag, String logLevel, String loggerName, String message, String threadName, String smsFormat, String smsContent, boolean sendSMS, String error_Frequence)
  {
    try
    {
      return iinsertErrorDefinition(appId, userName, createTime, useFlag, logLevel, loggerName, message, threadName, smsFormat, smsContent, sendSMS, error_Frequence);
    }
    catch (ConnectionException ex)
    {
      Log.info("connection exception:" + ex);
      Log.info("connection is closed before, retry again");
      try
      {
        return iinsertErrorDefinition(appId, userName, createTime, useFlag, logLevel, loggerName, message, threadName, smsFormat, smsContent, sendSMS, error_Frequence);
      }
      catch (ConnectionException e)
      {
        Log.info("connection exception:" + e);
        Log.info("can't create connection");
      }
    }
    return -1;
  }
  
  private int ideleteErrorDefinition(int errorId)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    try
    {
      this.deleteErrorDefinitionPS.setInt(1, errorId);
      if (this.deleteErrorDefinitionPS.executeUpdate() == 0) {
        return 0;
      }
      return errorId;
    }
    catch (SQLException ex)
    {
      Log.warn("Exception when delete error definition:" + ex);
      if (!DatabaseConnection.shareInstance().checkConnection())
      {
        this.sqlException = true;
        throw new ConnectionException(ex.getMessage(), "");
      }
      return -1;
    }
    catch (Exception e)
    {
      Log.warn(e);
    }
    return -1;
  }
  
  public synchronized int deleteErrorDefinition(int errorId)
  {
    try
    {
      return ideleteErrorDefinition(errorId);
    }
    catch (ConnectionException ex)
    {
      Log.info("connection exception:" + ex);
      Log.info("connection is closed before, retry again");
      try
      {
        return ideleteErrorDefinition(errorId);
      }
      catch (ConnectionException e)
      {
        Log.info("connection exception:" + e);
        Log.info("can't create connection");
      }
    }
    return -1;
  }
  
  private int isetErrorDefinitionUseFlag(int errorId, boolean useFlag)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    try
    {
      this.setErrorDefinitionUseFlagPS.setInt(1, errorId);
      this.setErrorDefinitionUseFlagPS.setInt(2, useFlag == true ? 1 : 0);
      if (this.setErrorDefinitionUseFlagPS.executeUpdate() == 0) {
        return -1;
      }
      return useFlag == true ? 1 : 0;
    }
    catch (SQLException ex)
    {
      Log.warn("Exception when set error definition use Flag:" + ex);
      if (!DatabaseConnection.shareInstance().checkConnection())
      {
        this.sqlException = true;
        throw new ConnectionException(ex.getMessage(), "");
      }
      return -1;
    }
    catch (Exception e)
    {
      Log.warn(e);
    }
    return -1;
  }
  
  public synchronized int setErrorDefinitionUseFlag(int errorId, boolean useFlag)
  {
    try
    {
      return isetErrorDefinitionUseFlag(errorId, useFlag);
    }
    catch (ConnectionException ex)
    {
      Log.info("connection exception:" + ex);
      Log.info("connection is closed before, retry again");
      try
      {
        return isetErrorDefinitionUseFlag(errorId, useFlag);
      }
      catch (ConnectionException e)
      {
        Log.info("connection exception:" + e);
        Log.info("can't create connection");
      }
    }
    return -1;
  }
  
  private String iselectApplicationTelephone(String appId)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    ResultSet appTelephoneRS = null;
    String telephoneList = "";
    try
    {
      this.selectApplicationTelephone.setString(1, appId);
      
      appTelephoneRS = this.selectApplicationTelephone.executeQuery();
      while (appTelephoneRS.next()) {
        telephoneList = appTelephoneRS.getString("phone_number_list");
      }
      return telephoneList;
    }
    catch (SQLException ex)
    {
      Log.warn("Exception when select error definition:" + ex);
      if (!DatabaseConnection.shareInstance().checkConnection())
      {
        this.sqlException = true;
        throw new ConnectionException(ex.getMessage(), "");
      }
      return "";
    }
    catch (Exception e)
    {
      Log.warn(e);
    }
    return "";
  }
  
  public synchronized String selectApplicationTelephone(String appId)
  {
    try
    {
      return iselectApplicationTelephone(appId);
    }
    catch (ConnectionException ex)
    {
      Log.info("connection exception:" + ex);
      Log.info("connection is closed before, retry again");
      try
      {
        return iselectApplicationTelephone(appId);
      }
      catch (ConnectionException e)
      {
        Log.info("connection exception:" + e);
        Log.info("can't create connection");
      }
    }
    return "";
  }
  
  private int iinsertApplicationTelephone(String appId, String phone_number_list)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    try
    {
      this.insertApplicationTelephone.setString(1, appId);
      this.insertApplicationTelephone.setString(2, phone_number_list);
      if (this.insertApplicationTelephone.executeUpdate() == 0) {
        return 0;
      }
      return 1;
    }
    catch (SQLException ex)
    {
      Log.warn("Exception when insert Telephone:" + ex);
      if (!DatabaseConnection.shareInstance().checkConnection())
      {
        this.sqlException = true;
        throw new ConnectionException(ex.getMessage(), "");
      }
      return -1;
    }
    catch (Exception e)
    {
      Log.warn(e);
    }
    return -1;
  }
  
  public synchronized int insertApplicationTelephone(String appId, String phone_number_list)
  {
    try
    {
      return iinsertApplicationTelephone(appId, phone_number_list);
    }
    catch (ConnectionException ex)
    {
      Log.info("connection exception:" + ex);
      Log.info("connection is closed before, retry again");
      try
      {
        return iinsertApplicationTelephone(appId, phone_number_list);
      }
      catch (ConnectionException e)
      {
        Log.info("connection exception:" + e);
        Log.info("can't create connection");
      }
    }
    return -1;
  }
  
  private int iupdateApplicationTelephone(String appId, String phone_number_list)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    try
    {
      this.updateApplicationTelephonePS.setString(1, appId);
      this.updateApplicationTelephonePS.setString(2, phone_number_list);
      
      int i = this.updateApplicationTelephonePS.executeUpdate();
      if (i == 0) {
        return 0;
      }
      return 1;
    }
    catch (SQLException ex)
    {
      Log.warn("Exception when update telephone:" + ex);
      if (!DatabaseConnection.shareInstance().checkConnection())
      {
        this.sqlException = true;
        throw new ConnectionException(ex.getMessage(), "");
      }
      return -1;
    }
    catch (Exception e)
    {
      Log.warn(e);
    }
    return -1;
  }
  
  public synchronized int updateApplicationTelephone(String appId, String phone_number_list)
  {
    try
    {
      return iupdateApplicationTelephone(appId, phone_number_list);
    }
    catch (ConnectionException ex)
    {
      Log.info("connection exception:" + ex);
      Log.info("connection is closed before, retry again");
      try
      {
        return iupdateApplicationTelephone(appId, phone_number_list);
      }
      catch (ConnectionException e)
      {
        Log.info("connection exception:" + e);
        Log.info("can't create connection");
      }
    }
    return -1;
  }
  
  private int irequestSendSMS(String message, String applicationId)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    try
    {
      int maxAtempt = 0;
      this.selectMaxAttemptPS.setString(1, applicationId);
      ResultSet rs = this.selectMaxAttemptPS.executeQuery();
      if (rs != null)
      {
        while (rs.next()) {
          maxAtempt = rs.getInt(1);
        }
        try
        {
          rs.close();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
      if (maxAtempt == 0) {
        maxAtempt = 5;
      }
      this.requestSendSMS.setString(1, message);
      this.requestSendSMS.setString(2, applicationId);
      this.requestSendSMS.setInt(3, maxAtempt);
      
      return this.requestSendSMS.executeUpdate();
    }
    catch (SQLException ex)
    {
      Log.warn("Exception when request send SMS: " + ex);
      if (!DatabaseConnection.shareInstance().checkConnection())
      {
        this.sqlException = true;
        throw new ConnectionException(ex.getMessage(), "");
      }
      return -1;
    }
    catch (Exception e)
    {
      Log.warn(e);
    }
    return -1;
  }
  
  public synchronized int requestSendSMS(String message, String applicationId)
  {
    try
    {
      return irequestSendSMS(message, applicationId);
    }
    catch (ConnectionException ex)
    {
      Log.info("connection exception:" + ex);
      Log.info("connection is closed, retry again");
      try
      {
        return irequestSendSMS(message, applicationId);
      }
      catch (ConnectionException e)
      {
        Log.info("connection exception:" + e);
        Log.info("can't create connection");
      }
    }
    return -1;
  }
  
  public synchronized int insertApplication(String appID, int maxAttemp, String desc)
  {
    try
    {
      return iinsertApplication(appID, maxAttemp, desc);
    }
    catch (ConnectionException ex)
    {
      Log.info("connection exception:" + ex);
      Log.info("connection is closed, retry again");
      try
      {
        return iinsertApplication(appID, maxAttemp, desc);
      }
      catch (ConnectionException e)
      {
        Log.info("connection exception:" + e);
        Log.info("can't create connection");
      }
    }
    return -1;
  }
  
  private int iinsertApplication(String appId, int maxAttempt, String desc)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    try
    {
      Statement stm = this.connection.createStatement();
      return stm.executeUpdate("INSERT INTO " + this.schema + ".APPLICATION(APPLICATION_ID,MAX_ATTEMP) VALUES('" + appId + "'," + maxAttempt + ",'" + desc + "')");
    }
    catch (SQLException ex)
    {
      Log.warn("Exception when insert Application:" + ex);
      if (!DatabaseConnection.shareInstance().checkConnection())
      {
        this.sqlException = true;
        throw new ConnectionException(ex.getMessage(), "");
      }
      return -1;
    }
    catch (Exception e)
    {
      Log.warn(e);
    }
    return -1;
  }
  
  private int ideleteReceiver(String appID)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    Statement stm = null;
    try
    {
      stm = this.connection.createStatement();
      return stm.executeUpdate("DELETE FROM " + this.schema + ".APP_RECEIVER WHERE application_id = '" + appID + "'");
    }
    catch (SQLException ex)
    {
      Log.warn("Exception when delete Receiver:" + ex);
      if (!DatabaseConnection.shareInstance().checkConnection())
      {
        this.sqlException = true;
        throw new ConnectionException(ex.getMessage(), "");
      }
      return -1;
    }
    catch (Exception e)
    {
      int j;
      Log.warn(e);
      return -1;
    }
    finally
    {
      if (stm != null) {
        try
        {
          stm.close();
        }
        catch (SQLException ex) {}
      }
    }
  }
  
  private int ideleteApplicationReceiver(String appID)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    try
    {
      this.deleteApplicationReceiverPS.setString(1, appID);
      return this.deleteApplicationReceiverPS.executeUpdate();
    }
    catch (SQLException ex)
    {
      Log.warn("Exception when delete Application_Receiver:" + ex);
      if (!DatabaseConnection.shareInstance().checkConnection())
      {
        this.sqlException = true;
        throw new ConnectionException(ex.getMessage(), "");
      }
      return -1;
    }
    catch (Exception e)
    {
      Log.warn(e);
    }
    return -1;
  }
  
  public int deleteApplicationReceiver(String appID)
  {
    try
    {
      return ideleteApplicationReceiver(appID);
    }
    catch (ConnectionException ex)
    {
      Log.info("connection exception:" + ex);
      Log.info("connection is closed, retry again");
      try
      {
        return ideleteApplicationReceiver(appID);
      }
      catch (ConnectionException e)
      {
        Log.info("connection exception:" + e);
        Log.info("can't create connection");
      }
    }
    return -1;
  }
  
  public int deleleReceiver(String appID)
  {
    try
    {
      return ideleteReceiver(appID);
    }
    catch (ConnectionException ex)
    {
      Log.info("connection exception:" + ex);
      Log.info("connection is closed, retry again");
      try
      {
        return ideleteReceiver(appID);
      }
      catch (ConnectionException e)
      {
        Log.info("connection exception:" + e);
        Log.info("can't create connection");
      }
    }
    return -1;
  }
  
  private int iinsertReceiver(String appID, String phone_num_list)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    ResultSet rs = null;
    PreparedStatement receiverIdPS = null;
    PreparedStatement insertPS = null;
    PreparedStatement insertApp_ReceiverPS = null;
    try
    {
      receiverIdPS = this.connection.prepareStatement("SELECT " + this.schema + ".Receiver_ID_seq.nextVal from dual");
      insertPS = this.connection.prepareStatement("INSERT INTO " + this.schema + ".RECEIVER(Receiver_ID,Receiver_Name,MSISDN) VALUES(?,?,?)");
      
      insertApp_ReceiverPS = this.connection.prepareStatement("INSERT INTO " + this.schema + ".APP_RECEIVER(APPLICATION_ID, RECEIVER_ID, RECEIVE_METHOD) VALUES (?,?,?)");
      
      insertApp_ReceiverPS.setString(1, appID);
      insertApp_ReceiverPS.setInt(3, 1);
      if ((phone_num_list == null) || (phone_num_list.equals(""))) {
        return 0;
      }
      String[] phones = phone_num_list.split(" ");
      int result = 0;
      for (String phone : phones)
      {
        rs = receiverIdPS.executeQuery();
        rs.next();
        int receiverId = rs.getInt(1);
        insertPS.setInt(1, receiverId);
        insertPS.setString(2, "MMClient");
        insertPS.setString(3, phone);
        if (insertPS.executeUpdate() > 0)
        {
          insertApp_ReceiverPS.setInt(2, receiverId);
          result = insertApp_ReceiverPS.executeUpdate();
        }
      }
      return result;
    }
    catch (SQLException ex)
    {
      Log.warn("Exception when insert Reciver:" + ex);
      if (!DatabaseConnection.shareInstance().checkConnection())
      {
        this.sqlException = true;
        throw new ConnectionException(ex.getMessage(), "");
      }
      return -1;
    }
    catch (Exception e)
    {
      int result;
      Log.warn(e);
      return -1;
    }
    finally
    {
      try
      {
        if (rs != null) {
          rs.close();
        }
        if (receiverIdPS != null) {
          receiverIdPS.close();
        }
        if (insertPS != null) {
          insertPS.close();
        }
        if (insertApp_ReceiverPS != null) {
          insertApp_ReceiverPS.close();
        }
      }
      catch (Exception e)
      {
        Log.warn(e);
      }
    }
  }
  
  public synchronized int insertReceiver(String appID, String phone_num_list)
  {
    try
    {
      return iinsertReceiver(appID, phone_num_list);
    }
    catch (ConnectionException ex)
    {
      Log.info("connection exception:" + ex);
      Log.info("connection is closed, retry again");
      try
      {
        return iinsertReceiver(appID, phone_num_list);
      }
      catch (ConnectionException e)
      {
        Log.info("connection exception:" + e);
        Log.info("can't create connection");
      }
    }
    return -1;
  }
  
  private String iselectPhoneNumList(String appId)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    Statement stm = null;
    ResultSet rs = null;
    try
    {
      stm = this.connection.createStatement();
      rs = stm.executeQuery("SELECT MSISDN FROM " + this.schema + ".RECEIVER R , " + this.schema + ".APP_RECEIVER A WHERE " + "A.APPLICATION_ID = '" + appId + "'  AND R.RECEIVER_ID = A.RECIVER_ID ");
      
      StringBuilder str = new StringBuilder();
      if (rs != null) {
        while (rs.next())
        {
          str.append(rs.getString("MSISDN"));
          str.append(" ");
        }
      }
      return str.toString().trim();
    }
    catch (SQLException ex)
    {
      Log.warn("Exception when update telephone:" + ex);
      if (!DatabaseConnection.shareInstance().checkConnection())
      {
        this.sqlException = true;
        throw new ConnectionException(ex.getMessage(), "");
      }
      return "";
    }
    catch (Exception e)
    {
      String str;
      Log.warn(e);
      return "";
    }
    finally {}
  }
  
  public synchronized String selectTelephone(String appID)
  {
    try
    {
      return iselectPhoneNumList(appID);
    }
    catch (ConnectionException ex)
    {
      Log.info("connection exception:" + ex);
      Log.info("connection is closed, retry again");
      try
      {
        return iselectPhoneNumList(appID);
      }
      catch (ConnectionException e)
      {
        Log.info("connection exception:" + e);
        Log.info("can't create connection");
      }
    }
    return "";
  }
  
  private HashMap<String, TimeoutInfo> iselectThreadTimeout(String applicationID)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    HashMap<String, TimeoutInfo> result = new HashMap();
    ResultSet rs = null;
    try
    {
      this.selectThreadTimeoutPS.setString(1, applicationID);
      rs = this.selectThreadTimeoutPS.executeQuery();
      while (rs.next()) {
        result.put(rs.getString("MBEANNAME"), new TimeoutInfo(rs.getInt("TIMEOUT"), rs.getBoolean("SENDSMS")));
      }
      return result;
    }
    catch (SQLException ex)
    {
      Log.warn("Exception when select Thread timeout:" + ex);
      if (!DatabaseConnection.shareInstance().checkConnection())
      {
        this.sqlException = true;
        throw new ConnectionException(ex.getMessage(), "");
      }
      return null;
    }
    catch (Exception e)
    {
      HashMap<String, TimeoutInfo> localHashMap2;
      Log.warn(e);
      return null;
    }
    finally
    {
      try
      {
        if (rs != null) {
          rs.close();
        }
      }
      catch (SQLException ex)
      {
        Log.warn(ex);
      }
    }
  }
  
  public synchronized HashMap<String, TimeoutInfo> selectThreadTimeout(String applicationID)
  {
    try
    {
      return iselectThreadTimeout(applicationID);
    }
    catch (ConnectionException ex)
    {
      Log.info("connection exception:" + ex);
      Log.info("connection is closed before, retry again");
      try
      {
        return iselectThreadTimeout(applicationID);
      }
      catch (ConnectionException e)
      {
        Log.info("connection exception:" + e);
        Log.info("can't create connection");
      }
    }
    return null;
  }
  
  private int ideleteThreadTimeout(String appID, String mbeanName)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    try
    {
      this.deleteThreadTimeoutPS.setString(1, appID);
      this.deleteThreadTimeoutPS.setString(2, mbeanName);
      return this.deleteThreadTimeoutPS.executeUpdate();
    }
    catch (SQLException ex)
    {
      Log.warn("Exception when delete Thread timeout:" + ex);
      if (!DatabaseConnection.shareInstance().checkConnection())
      {
        this.sqlException = true;
        throw new ConnectionException(ex.getMessage(), "");
      }
      return -1;
    }
    catch (Exception e)
    {
      Log.warn(e);
    }
    return -1;
  }
  
  public synchronized int deleteThreadTimeout(String appID, String mbeanName)
  {
    try
    {
      return ideleteThreadTimeout(appID, mbeanName);
    }
    catch (ConnectionException ex)
    {
      Log.info("connection exception:" + ex);
      Log.info("connection is closed before, retry again");
      try
      {
        return ideleteThreadTimeout(appID, mbeanName);
      }
      catch (ConnectionException e)
      {
        Log.info("connection exception:" + e);
        Log.info("can't create connection");
      }
    }
    return -1;
  }
  
  private int ieditThreadTimeout(String appID, String mbeanName, int timeout, boolean sendSMS)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    try
    {
      this.editThreadTimeoutPS.setInt(1, timeout);
      this.editThreadTimeoutPS.setBoolean(2, sendSMS);
      this.editThreadTimeoutPS.setString(3, appID);
      this.editThreadTimeoutPS.setString(4, mbeanName);
      return this.editThreadTimeoutPS.executeUpdate();
    }
    catch (SQLException ex)
    {
      Log.warn("Exception when edit Thread timeout:" + ex);
      if (!DatabaseConnection.shareInstance().checkConnection())
      {
        this.sqlException = true;
        throw new ConnectionException(ex.getMessage(), "");
      }
      return -1;
    }
    catch (Exception e)
    {
      Log.warn(e);
    }
    return -1;
  }
  
  public synchronized int editThreadTimeout(String appID, String mbeanName, int timeout, boolean sendSMS)
  {
    try
    {
      return ieditThreadTimeout(appID, mbeanName, timeout, sendSMS);
    }
    catch (ConnectionException ex)
    {
      Log.info("connection exception:" + ex);
      Log.info("connection is closed before, retry again");
      try
      {
        return ieditThreadTimeout(appID, mbeanName, timeout, sendSMS);
      }
      catch (ConnectionException e)
      {
        Log.info("connection exception:" + e);
        Log.info("can't create connection");
      }
    }
    return -1;
  }
  
  private int iinsertThreadTimeout(String appID, String mbeanName, int timeout, boolean sendSMS)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    try
    {
      this.insertThreadTimeoutPS.setString(1, appID);
      this.insertThreadTimeoutPS.setString(2, mbeanName);
      this.insertThreadTimeoutPS.setInt(3, timeout);
      this.insertThreadTimeoutPS.setBoolean(4, sendSMS);
      return this.insertThreadTimeoutPS.executeUpdate();
    }
    catch (SQLException ex)
    {
      Log.warn("Exception when insert Thread timeout:" + ex);
      if (!DatabaseConnection.shareInstance().checkConnection())
      {
        this.sqlException = true;
        throw new ConnectionException(ex.getMessage(), "");
      }
      return -1;
    }
    catch (Exception e)
    {
      Log.warn(e);
    }
    return -1;
  }
  
  public synchronized int insertThreadTimeout(String appID, String mbeanName, int timeout, boolean sendSMS)
  {
    try
    {
      return iinsertThreadTimeout(appID, mbeanName, timeout, sendSMS);
    }
    catch (ConnectionException ex)
    {
      Log.info("connection exception:" + ex);
      Log.info("connection is closed before, retry again");
      try
      {
        return iinsertThreadTimeout(appID, mbeanName, timeout, sendSMS);
      }
      catch (ConnectionException e)
      {
        Log.info("connection exception:" + e);
        Log.info("can't create connection");
      }
    }
    return -1;
  }
  
  public synchronized void close()
  {
    try
    {
      this.connection.close();
    }
    catch (SQLException ex)
    {
      Log.warn(ex);
    }
  }
}
