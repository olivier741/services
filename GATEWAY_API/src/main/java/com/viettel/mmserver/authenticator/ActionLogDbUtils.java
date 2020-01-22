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
package com.viettel.mmserver.authenticator;

/**
 *
 * @author olivier.tatsinkou
 */
import com.viettel.mmserver.base.ProcessThread;
import com.viettel.mmserver.database.ConnectionException;
import com.viettel.mmserver.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.management.Notification;
import javax.management.NotificationListener;
import org.apache.log4j.Logger;

public class ActionLogDbUtils
  implements NotificationListener
{
  private Logger logger;
  private Connection connection;
  private PreparedStatement insertLogReqStmt;
  private DbInstance dbInstance;
  private boolean sqlException;
  private static String actionLogId = "";
  private static String insertLogSql = "";
  private String schema = "";
  
  public ActionLogDbUtils(String logName)
  {
    try
    {
      this.logger = Logger.getLogger(logName);
      if (this.dbInstance == null) {
        this.dbInstance = new DbInstance("Action Log Thread");
      }
      this.dbInstance.start();
      this.schema = DatabaseConnection.shareInstance().getSchema();
      actionLogId = this.schema + ".ACTIONLOGID_SEQ.nextval";
      insertLogSql = "insert into " + this.schema + ".ACTIONLOG (LOGID" + ",USERNAME, APPLICATIONID, OPERATIONNAME, PARASTRING" + ", MBEANOBJECTNAME, RESULTSTRING, OPERATIONTIME" + ",OPERATIONYEAR, OPERATIONMONTH, OPERATIONDAY) VALUES (" + actionLogId + ",?,?,?,?,?,?,to_date(?,'yyyy/mm/dd/hh24/mi/ss'),?,?,?)";
      



      this.connection = DatabaseConnection.shareInstance().getConnection();
      this.connection.setAutoCommit(true);
      this.insertLogReqStmt = this.connection.prepareStatement(insertLogSql);
    }
    catch (Exception ex)
    {
      this.logger.error("Error in start ActionLogDbUtils");
      this.logger.error(ex.toString());
    }
  }
  
  public int insertLog(String userName, String applicationId, String operationName, String paraString, String mBeanObjectName, String resultString, Calendar operationTime)
  {
    try
    {
      return iinsertLog(userName, applicationId, operationName, paraString, mBeanObjectName, resultString, operationTime);
    }
    catch (ConnectionException ce)
    {
      this.logger.info("connection exception:" + ce);
      this.logger.info("previous connection is closed, reconnect and retry again");
      try
      {
        return iinsertLog(userName, applicationId, operationName, paraString, mBeanObjectName, resultString, operationTime);
      }
      catch (ConnectionException e)
      {
        this.logger.info("connection exception:" + e);
        this.logger.info("can't create connection");
      }
    }
    return -1;
  }
  
  private int iinsertLog(String userName, String applicationId, String operationName, String paraString, String mBeanObjectName, String resultString, Calendar operationTime)
    throws ConnectionException
  {
    if (this.sqlException) {
      updateConnection();
    }
    try
    {
      this.insertLogReqStmt.clearParameters();
      this.insertLogReqStmt.setString(1, userName);
      this.insertLogReqStmt.setString(2, applicationId);
      this.insertLogReqStmt.setString(3, operationName);
      this.insertLogReqStmt.setString(4, paraString);
      this.insertLogReqStmt.setString(5, mBeanObjectName);
      this.insertLogReqStmt.setString(6, resultString);
      this.insertLogReqStmt.setString(7, new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(new java.sql.Date(operationTime.getTime().getTime())));
      
      this.insertLogReqStmt.setInt(8, operationTime.get(1));
      this.insertLogReqStmt.setInt(9, operationTime.get(2) + 1);
      this.insertLogReqStmt.setInt(10, operationTime.get(5));
      this.insertLogReqStmt.executeUpdate();
      return 0;
    }
    catch (SQLException ex)
    {
      if (DatabaseConnection.shareInstance().checkConnection())
      {
        this.logger.debug("insert actionLog exception:" + ex);
        return ex.getErrorCode();
      }
      this.sqlException = true;
      throw new ConnectionException(ex.getMessage(), "");
    }
  }
  
  protected void updateConnection()
  {
    try
    {
      this.connection = DatabaseConnection.shareInstance().updateConnection();
      this.insertLogReqStmt = this.connection.prepareStatement(insertLogSql);
      this.sqlException = false;
    }
    catch (Exception ex)
    {
      this.logger.warn("connection error:" + ex);
      this.logger.warn(ex.getMessage(), ex);
    }
  }
  
  public void setLogger(Logger logger)
  {
    this.logger = logger;
  }
  
  public void handleNotification(Notification notification, Object handback)
  {
    this.dbInstance.add(notification);
  }
  
  class DbInstance
    extends ProcessThread
  {
    private List<Notification> waitQueue = new ArrayList();
    
    public DbInstance(String threadName)
    {
      super();
    }
    
    protected void process()
    {
      if (this.waitQueue.size() > 0)
      {
        Notification notification = (Notification)this.waitQueue.remove(0);
        if ((notification.getType() != null) && (notification.getType().equals("method.invoke")))
        {
          String identity = notification.getSource().toString();
          String message = notification.getMessage();
          if ((message != null) && (!message.equals("")))
          {
            String[] params = message.split("\\t");
            if (params.length >= 6)
            {
              Calendar calendar = Calendar.getInstance();
              calendar.setTimeInMillis(Long.parseLong(params[5]));
              ActionLogDbUtils.this.insertLog(identity, params[0], params[1], params[2], params[3], params[4], calendar);
            }
          }
        }
      }
      try
      {
        Thread.sleep(1000L);
      }
      catch (InterruptedException ex)
      {
        this.logger.info(ex.toString());
      }
    }
    
    public void add(Notification notification)
    {
      if (notification != null) {
        this.waitQueue.add(notification);
      }
    }
  }
}

