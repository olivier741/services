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

import com.tatsinktechnologic.ussd_gateway.mmserver.base.ConfigObject;
import com.tatsinktechnologic.ussd_gateway.mmserver.base.ConfigParam;
import com.tatsinktechnologic.ussd_gateway.mmserver.base.Log;
import com.tatsinktechnologic.ussd_gateway.mmserver.database.DatabaseAccessor;
import com.tatsinktechnologic.ussd_gateway.mmserver.warnning.ErrorDefinition;
import com.tatsinktechnologic.ussd_gateway.mmserver.warnning.ErrorDefinitionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ReflectionException;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class ErrorFilterJDBCAppender
  extends AppenderSkeleton
  implements NotificationListener
{
  static final int DEFAULT_MAX_QUEUE = 1000;
  static final int DEFAULT_MAX_ERROR_QUEUE = 100;
  private DatabaseAccessor dbAccessor = null;
  private List<LoggingEvent> logQueue = new LinkedList();
  private HashMap<Integer, Date> recentSendSMS = new HashMap();
  private final Object recentSendSMSLock = new Object();
  private int maxQueue = 1000;
  private int maxErrorStack = 100;
  private final Object queueLock = new Object();
  public static final String NOTIFICATION_TYPE = "errorFiltering";
  private ErrorFilterThread errorFilterThread = null;
  private String appId = "";
  static final int DEFAULT_SMS_INTERVAL = 1200;
  private long smsInterval = 1200L;
  
  public long getSmsInterval()
  {
    return this.smsInterval;
  }
  
  public void setSmsInterval(long smsInterval)
  {
    this.smsInterval = smsInterval;
  }
  
  public void activateOptions()
  {
    try
    {
      this.errorFilterThread = new ErrorFilterThread();
      ErrorDefinitionHandler.getInstance().addNotificationListener(this, null, null);
      this.errorFilterThread.start();
      this.dbAccessor = DatabaseAccessor.shareInstance();
    }
    catch (Exception e)
    {
      Log.warn(e);
    }
  }
  
  public void addToQueue(LoggingEvent event)
  {
    synchronized (this.queueLock)
    {
      if (this.logQueue.size() < this.maxQueue)
      {
        this.logQueue.add(event);
      }
      else
      {
        this.logQueue.remove(0);
        this.logQueue.add(event);
      }
    }
  }
  
  public void append(LoggingEvent event)
  {
    if (null != event) {
      addToQueue(event);
    }
  }
  
  public boolean requiresLayout()
  {
    return false;
  }
  
  public synchronized void close()
  {
    if (this.closed) {
      return;
    }
    cleanup();
    this.closed = true;
  }
  
  public synchronized void cleanup()
  {
    try
    {
      this.errorFilterThread.unregisterAgent();
      this.errorFilterThread.stop();
      ErrorDefinitionHandler.getInstance().removeNotificationListener(this);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void handleNotification(Notification notification, Object handback)
  {
    if (notification.getType().equals("errorDefiniton.change"))
    {
      boolean updated = this.errorFilterThread.reloadErrorDefinitions();
      if (updated) {
        System.out.println("Reload Error Definition List" + this.errorFilterThread.getErrorListSize());
      } else {
        System.out.println("Can not reload Error Definiton list");
      }
    }
  }
  
  public int getMaxQueue()
  {
    return this.maxQueue;
  }
  
  public void setMaxQueue(int maxQueue)
  {
    this.maxQueue = maxQueue;
  }
  
  public int getOfflineErrorMaxQueue()
  {
    return this.maxErrorStack;
  }
  
  public void setOfflineErrorQueue(int offlineErrorMaxQueue)
  {
    this.maxErrorStack = offlineErrorMaxQueue;
  }
  
  class ErrorFilterThread
    extends AppenderThreadMX
  {
    public static final int MILISECOND_OF_ONE_SECOND = 1000;
    public static final int SLEEP_TIME = 1000;
    public static final int MILISECOND_ON_MINUTE = 60000;
    private HashMap<Integer, ErrorDefinition> errorDefinitionList = null;
    private DatabaseAccessor databaseAccessor = null;
    private long sameErrorIDSendInterval = ErrorFilterJDBCAppender.this.smsInterval * 1000L;
    
    public ErrorFilterThread()
    {
      super("Thread that match log event against error Definition");
      this.databaseAccessor = DatabaseAccessor.shareInstance();
      try
      {
        registerAgent("Tools:type=errorFilter,name=FilterThread");
      }
      catch (MalformedObjectNameException ex)
      {
        ex.printStackTrace();
      }
      catch (InstanceAlreadyExistsException ex)
      {
        ex.printStackTrace();
      }
      catch (MBeanRegistrationException ex)
      {
        ex.printStackTrace();
      }
      catch (NotCompliantMBeanException ex)
      {
        ex.printStackTrace();
      }
    }
    
    public int getErrorListSize()
    {
      if (this.errorDefinitionList != null) {
        return this.errorDefinitionList.size();
      }
      return 0;
    }
    
    public boolean reloadErrorDefinitions()
    {
      HashMap<Integer, ErrorDefinition> temp = this.databaseAccessor.getErrorDefinition(ConfigParam.getInstance().getAppID());
      if (temp == null) {
        return false;
      }
      this.errorDefinitionList = temp;
      return true;
    }
    
    public void start()
    {
      ErrorFilterJDBCAppender.this.appId = ConfigParam.getInstance().getAppID();
      Log.info("ErrorFilterJDBCAppender is trying to get Error Definition from database ");
      
      HashMap h = this.databaseAccessor.getErrorDefinition(ErrorFilterJDBCAppender.this.appId);
      if (h != null) {
        this.errorDefinitionList = h;
      }
      if (this.errorDefinitionList == null)
      {
        Log.warn("ErrorFilterJDBCAppender can not load error Definition from database ");
        stop();
      }
      else
      {
        Log.info("Load " + this.errorDefinitionList.size() + " error definitions from database");
      }
      super.start();
    }
    
    private void filterNotification(ErrorFilterJDBCAppender.MMNotification mmNotification)
    {
      int errorID = mmNotification.getErrorId();
      Date lastSend = null;
      synchronized (ErrorFilterJDBCAppender.this.recentSendSMSLock)
      {
        lastSend = (Date)ErrorFilterJDBCAppender.this.recentSendSMS.get(Integer.valueOf(errorID));
      }
      Date now = new Date();
      if ((lastSend == null) || (now.getTime() - lastSend.getTime() > this.sameErrorIDSendInterval))
      {
        int result = ErrorFilterJDBCAppender.this.dbAccessor.requestSendSMS(mmNotification.getSMS(), ErrorFilterJDBCAppender.this.appId);
        if (result == -1) {
          Log.warn("ErrorFilter: Can not send SMS ");
        } else {
          synchronized (ErrorFilterJDBCAppender.this.recentSendSMSLock)
          {
            ErrorFilterJDBCAppender.this.recentSendSMS.put(Integer.valueOf(mmNotification.getErrorId()), now);
          }
        }
      }
    }
    
    protected void process()
    {
      LoggingEvent event = null;
      

      event = null;
      synchronized (ErrorFilterJDBCAppender.this.queueLock)
      {
        if (ErrorFilterJDBCAppender.this.logQueue.size() > 0) {
          event = (LoggingEvent)ErrorFilterJDBCAppender.this.logQueue.remove(0);
        }
      }
      if ((event != null) && (this.errorDefinitionList.size() != 0))
      {
        Set set = this.errorDefinitionList.entrySet();
        
        Iterator i = set.iterator();
        while (i.hasNext())
        {
          Map.Entry me = (Map.Entry)i.next();
          int errorID = ((Integer)me.getKey()).intValue();
          ErrorDefinition errorDefinition = (ErrorDefinition)me.getValue();
          if (errorDefinition.match(event))
          {
            Notification notification = new Notification("errorFiltering", event, 0L, "" + errorID);
            this.notificationHandler.sendNotification(notification);
            if (errorDefinition.isSendSMSFlag())
            {
              MMNotification mMNotification = new MMNotification( event, errorDefinition);
              filterNotification(mMNotification);
            }
            return;
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
          System.out.println("Stop mmserver's ErrorFilterThread " + ex);
        }
      }
    }
    
    public String loadParams()
    {
      ConfigObject configObject = new ConfigObject();
      
      configObject.addConfig("Time between send two SMS that have the same ErrorID (minute)", new String[] { "5", "10", "20", "30", "40", "50", "60", "120" });
      

      return configObject.getConfig();
    }
    
    public String saveParams(String newConfig)
    {
      ConfigObject configObject = new ConfigObject(newConfig);
      ArrayList<String> params = configObject.getListParams();
      String strSame = configObject.readConfig((String)params.get(0))[0];
      this.sameErrorIDSendInterval = (Integer.parseInt(strSame) * 60000);
      System.out.println("same: " + this.sameErrorIDSendInterval + " " + strSame);
      
      return "" + this.sameErrorIDSendInterval;
    }
    
    protected MBeanOperationInfo[] buildOperations()
    {
      MBeanOperationInfo[] mbeanOperationInfo = super.buildOperations();
      MBeanOperationInfo[] newMbeanOperationInfo = new MBeanOperationInfo[mbeanOperationInfo.length + 1];
      System.arraycopy(mbeanOperationInfo, 0, newMbeanOperationInfo, 0, mbeanOperationInfo.length);
      
      MBeanParameterInfo[] params = new MBeanParameterInfo[0];
      newMbeanOperationInfo[mbeanOperationInfo.length] = new MBeanOperationInfo("reloadErrorDefinitions", "reload error definitions from database", params, "java.lang.Boolean", 1);
      if (newMbeanOperationInfo[mbeanOperationInfo.length] == null) {}
      return newMbeanOperationInfo;
    }
    
    public Object invoke(String operationName, Object[] params, String[] signature)
      throws MBeanException, ReflectionException
    {
      if (operationName.equals("reloadErrorDefinitions")) {
        return Boolean.valueOf(reloadErrorDefinitions());
      }
      return super.invoke(operationName, params, signature);
    }
    
    public String getInfor()
    {
      StringBuilder sb = new StringBuilder();
      sb.append("\nNumber of error definitions: " + this.errorDefinitionList.size());
      sb.append("\nSame Error send Interval: " + this.sameErrorIDSendInterval);
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
      sb.append("\n Recent send SMS 's errorID: ");
      Set<Map.Entry<Integer, Date>> set = ErrorFilterJDBCAppender.this.recentSendSMS.entrySet();
      Iterator i = set.iterator();
      while (i.hasNext())
      {
        Map.Entry<Integer, Date> e = (Map.Entry)i.next();
        sb.append("\n\t");
        sb.append(e.getKey() + " at " + dateFormat.format((Date)e.getValue()));
      }
      return super.getInfor() + sb.toString();
    }
  }
  
  class MMNotification
  {
    private LoggingEvent loggingEvent;
    private ErrorDefinition errorDefinition;
    
    public MMNotification(LoggingEvent event, ErrorDefinition errorDefinition)
    {
      this.loggingEvent = event;
      this.errorDefinition = errorDefinition;
    }
    
    public String getSMS()
    {
      if ((this.errorDefinition != null) && (this.loggingEvent != null)) {
        return this.errorDefinition.getSMS(this.loggingEvent);
      }
      return "";
    }
    
    public int getErrorId()
    {
      return this.errorDefinition.getErrorId();
    }
  }
}
