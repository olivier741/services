/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.warnning;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.mmserver.agent.MMbeanServer;
import com.tatsinktechnologic.ussd_gateway.mmserver.base.Log;
import com.tatsinktechnologic.ussd_gateway.mmserver.base.ProcessThreadMX;
import com.tatsinktechnologic.ussd_gateway.mmserver.database.DatabaseAccessor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServerNotification;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;

public class BlockedThreadDetector
  extends ProcessThreadMX
  implements NotificationListener
{
  public static final int DEFAULTFOOLINGMINUTE = 5;
  public static final int DEFAULTIMEOUTMINUTE = 5;
  public static final int MINUTE20 = 20;
  public static final String OBJECT_NAME = "Tools:name=BlockedDetector";
  private static BlockedThreadDetector instance = null;
  private ArrayList<ObjectName> threadList = new ArrayList();
  private ArrayList<ObjectName> blockedThreadList = new ArrayList();
  private ArrayList<String> blockedSMS = new ArrayList();
  private String applicationId = "";
  private HashMap<String, TimeoutInfo> threadTimeout = null;
  private static final long MILISECONDOFMINUTE = 60000L;
  private long defaultTimeout = 300000L;
  private long defaultPoolingPeriod = 300000L;
  private boolean sendUnblockedSMS = false;
  private boolean defaultSendSMS = false;
  
  private BlockedThreadDetector()
  {
    super("Blocked Detecting Thread", " A thread of mmserver that detects blocked thread");
    try
    {
      registerAgent("Tools:name=BlockedDetector");
    }
    catch (MalformedObjectNameException ex)
    {
      Log.info(ex);
    }
    catch (InstanceAlreadyExistsException ex)
    {
      Log.info(ex);
    }
    catch (MBeanRegistrationException ex)
    {
      Log.info(ex);
    }
    catch (NotCompliantMBeanException ex)
    {
      Log.info(ex);
    }
    ObjectName mbeanServerDelegate = null;
    try
    {
      mbeanServerDelegate = new ObjectName("JMImplementation:type=MBeanServerDelegate");
    }
    catch (MalformedObjectNameException ex)
    {
      Log.info("Can not find MbeanServerDelegate: " + ex);
    }
    catch (NullPointerException ex)
    {
      Log.info("Can not find MbeanServerDelegate: " + ex);
    }
    try
    {
      if (mbeanServerDelegate != null) {
        MMbeanServer.getInstance().addNotificationListener(mbeanServerDelegate, this, null, null);
      }
    }
    catch (InstanceNotFoundException ex)
    {
      Log.info("Can not registry listener to MbeanServerDelegate: " + ex);
    }
    this.applicationId = System.getProperty("com.viettel.mmserver.appid", "");
    if ((this.applicationId != null) && (!this.applicationId.equals(""))) {
      this.threadTimeout = DatabaseAccessor.shareInstance().selectThreadTimeout(this.applicationId);
    }
    String strDefaultTimeout = System.getProperty("com.viettel.mmserver.blockedDetect.defaultTimeout");
    if (strDefaultTimeout != null)
    {
      long iTimeout = this.defaultTimeout;
      try
      {
        iTimeout = Integer.valueOf(strDefaultTimeout).intValue() * 60000L;
        this.defaultTimeout = iTimeout;
      }
      catch (NumberFormatException e)
      {
        Log.error("Can not read default timeout");
      }
    }
    String strDefaultPooling = System.getProperty("com.viettel.mmserver.blockedDetect.defaultPoolingPeriod");
    if (strDefaultPooling != null)
    {
      long ipooling = this.defaultPoolingPeriod;
      try
      {
        ipooling = Integer.valueOf(strDefaultPooling).intValue() * 60000L;
        this.defaultPoolingPeriod = ipooling;
      }
      catch (NumberFormatException e)
      {
        Log.error("Can not read default pooling period");
      }
    }
    String strSendUnblockedSMS = System.getProperty("com.viettel.mmserver.blockedDetect.unblockedSMS");
    if ((strSendUnblockedSMS != null) && (strSendUnblockedSMS.equals("1"))) {
      this.sendUnblockedSMS = true;
    }
    String strSendSMS = System.getProperty("com.viettel.mmserver.blockedDetect.defaultBlockedSMS");
    if ((strSendSMS != null) && (strSendSMS.equals("1"))) {
      this.defaultSendSMS = true;
    }
  }
  
  public static synchronized BlockedThreadDetector getInstance()
  {
    if (instance == null) {
      instance = new BlockedThreadDetector();
    }
    return instance;
  }
  
  public void handleNotification(Notification notification, Object handback)
  {
    if (!(notification instanceof MBeanServerNotification)) {
      return;
    }
    MBeanServerNotification mbsn = (MBeanServerNotification)notification;
    
    ObjectName mbean = mbsn.getMBeanName();
    if (notification.getType().equals("JMX.mbean.registered"))
    {
      if (pingable(mbean)) {
        synchronized (this.threadListLock)
        {
          this.threadList.add(mbean);
        }
      }
    }
    else if (notification.getType().equals("JMX.mbean.unregistered")) {
      synchronized (this.threadListLock)
      {
        this.threadList.remove(mbean);
      }
    }
  }
  
  private final Object threadListLock = new Object();
  private final Object threadTimeoutLock = new Object();
  private static final String BLOCKEDNOTIFYTYPE = "process.blocked";
  
  private boolean pingable(ObjectName mbean)
  {
    try
    {
      MBeanAttributeInfo[] atts = MMbeanServer.getInstance().getMBeanInfo(mbean).getAttributes();
      for (MBeanAttributeInfo att : atts) {
        if (att.getName().equals("pingable")) {
          return true;
        }
      }
      return false;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return false;
  }
  
  protected void process()
  {
    try
    {
      long itimeout;
      boolean sendSMS;
      TimeoutInfo timeoutInfo;
      synchronized (this.threadListLock)
      {
        this.logger.debug("There are " + this.threadList.size() + " threads to be checked for blocking");
        itimeout = this.defaultTimeout;
        sendSMS = this.defaultSendSMS;
        timeoutInfo = null;
        for (ObjectName thread : this.threadList)
        {
          itimeout = this.defaultTimeout;
          sendSMS = this.defaultSendSMS;
          Long difTime = Long.valueOf(0L);
          try
          {
            difTime = (Long)MMbeanServer.getInstance().invoke(thread, "ping", null, null);
            synchronized (this.threadTimeoutLock)
            {
              if ((this.threadTimeout != null) && 
                (this.threadTimeout.containsKey(thread.toString())))
              {
                timeoutInfo = (TimeoutInfo)this.threadTimeout.get(thread.toString());
                itimeout = timeoutInfo.getTimeout() * 60000L;
                sendSMS = timeoutInfo.isSendSMS();
              }
            }
            if (difTime.longValue() > itimeout)
            {
              if (!this.blockedThreadList.contains(thread)) {
                this.blockedThreadList.add(thread);
              }
              Notification notification = new Notification("process.blocked", thread.toString(), 0L, "1");
              this.notificationHandler.sendNotification(notification);
              if (sendSMS) {
                filterNotification(thread.toString());
              }
            }
            else if (this.blockedThreadList.contains(thread))
            {
              this.blockedThreadList.remove(thread);
              
              Notification notification = new Notification("process.blocked", thread.toString(), 0L, "0");
              
              this.notificationHandler.sendNotification(notification);
              if ((this.sendUnblockedSMS) && (sendSMS) && (this.blockedSMS.contains(thread.toString())))
              {
                Date now = new Date();
                String sms = "Chuong trinh " + this.applicationId + ": tien trinh " + thread.toString() + " da het block khi check luc " + this.dateFormat.format(now);
                

                this.logger.info(sms);
                DatabaseAccessor.shareInstance().requestSendSMS(sms, this.applicationId);
                this.blockedSMS.remove(thread.toString());
              }
            }
          }
          catch (Exception e)
          {
            this.logger.error(e);
          }
        }
      }
      Thread.sleep(this.defaultPoolingPeriod);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public int insertThreadTimeout(String mbean, int timeout, boolean sendSMS)
  {
    if (this.applicationId.equals("")) {
      return -1;
    }
    int result = DatabaseAccessor.shareInstance().insertThreadTimeout(this.applicationId, mbean, timeout, sendSMS);
    if (result > 0) {
      synchronized (this.threadListLock)
      {
        this.threadTimeout.put(mbean, new TimeoutInfo(timeout, sendSMS));
      }
    }
    return result;
  }
  
  public int deleteThreadTimeout(String mbean)
  {
    if (this.applicationId.equals("")) {
      return -1;
    }
    int result = DatabaseAccessor.shareInstance().deleteThreadTimeout(this.applicationId, mbean);
    if (result > 0) {
      synchronized (this.threadListLock)
      {
        this.threadTimeout.remove(mbean);
      }
    }
    return result;
  }
  
  public int editThreadTimeout(String mbean, int timeout, boolean sendSMS)
  {
    if (this.applicationId.equals("")) {
      return -1;
    }
    TimeoutInfo old = (TimeoutInfo)this.threadTimeout.get(mbean);
    if (old == null) {
      return -1;
    }
    if ((old.getTimeout() == timeout) && (old.isSendSMS() == sendSMS)) {
      return 1;
    }
    int result = DatabaseAccessor.shareInstance().editThreadTimeout(this.applicationId, mbean, timeout, sendSMS);
    if (result > 0) {
      synchronized (this.threadListLock)
      {
        this.threadTimeout.put(mbean, new TimeoutInfo(timeout, sendSMS));
      }
    }
    return result;
  }
  
  private boolean setDefaultTimeout(int defaultTimeoutInMinutes)
  {
    if (defaultTimeoutInMinutes < 1) {
      return false;
    }
    this.defaultTimeout = (defaultTimeoutInMinutes * 60000L);
    return true;
  }
  
  private boolean setPoolingPeriod(int poolingPeriodInMinutes)
  {
    if (poolingPeriodInMinutes < 1) {
      return false;
    }
    this.defaultPoolingPeriod = (poolingPeriodInMinutes * 60000L);
    return true;
  }
  
  public Object invoke(String operationName, Object[] params, String[] signature)
    throws MBeanException, ReflectionException
  {
    if (operationName == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"), "Cannot invoke a null operation in " + this.dClassName);
    }
    if (operationName.equals("start"))
    {
      start();
      return null;
    }
    if (operationName.equals("stop"))
    {
      stop();
      return null;
    }
    if (operationName.equals("restart"))
    {
      restart();
      return null;
    }
    if (operationName.equals("readConfig")) {
      return readConfig();
    }
    if (operationName.equals("addChild"))
    {
      addChild((String)params[0], (String)params[1], (String)params[2], (String)params[3], (String)params[4]);
      
      return null;
    }
    if (operationName.equals("getInfor")) {
      return getInfor();
    }
    if (operationName.equals("ping")) {
      return Long.valueOf(ping());
    }
    if (operationName.equals("loadParams")) {
      return loadParams();
    }
    if (operationName.equals("saveParams"))
    {
      String newConfig = (String)params[0];
      return saveParams(newConfig);
    }
    if (operationName.equals("setDump"))
    {
      Boolean b = (Boolean)params[0];
      setDumpThread(b.booleanValue());
      return null;
    }
    if (operationName.equals("loadLoggerName")) {
      return loadLoggerName();
    }
    if (operationName.equals("insertThreadTimeout"))
    {
      String mbean = (String)params[0];
      int timeout = ((Integer)params[1]).intValue();
      boolean sendSMS = ((Boolean)params[2]).booleanValue();
      return Integer.valueOf(insertThreadTimeout(mbean, timeout, sendSMS));
    }
    if (operationName.equals("editThreadTimeout"))
    {
      String mbean = (String)params[0];
      int timeout = ((Integer)params[1]).intValue();
      boolean sendSMS = ((Boolean)params[2]).booleanValue();
      return Integer.valueOf(editThreadTimeout(mbean, timeout, sendSMS));
    }
    if (operationName.equals("deleteThreadTimeout"))
    {
      String mbean = (String)params[0];
      return Integer.valueOf(deleteThreadTimeout(mbean));
    }
    if (operationName.equals("setDefaultTimeout"))
    {
      Integer defaultTimeoutTmp = (Integer)params[0];
      return Boolean.valueOf(setDefaultTimeout(defaultTimeoutTmp.intValue()));
    }
    if (operationName.equals("setPoolingPeriod"))
    {
      Integer pooling = (Integer)params[0];
      return Boolean.valueOf(setPoolingPeriod(pooling.intValue()));
    }
    if (operationName.equals("reloadDatabase")) {
      return Boolean.valueOf(reloadDatabase());
    }
    if (operationName.equals("setTimeBetweenTwoSMS"))
    {
      Integer time = (Integer)params[0];
      return Boolean.valueOf(setTimeBetweenTwoSMS(time));
    }
    throw new ReflectionException(new NoSuchMethodException(operationName), "Cannot find the operation " + operationName + " in " + this.dClassName);
  }
  
  protected MBeanOperationInfo[] buildOperations()
  {
    ArrayList<MBeanOperationInfo> v = new ArrayList();
    MBeanParameterInfo[] params = new MBeanParameterInfo[0];
    v.add(new MBeanOperationInfo("start", "start service", params, "void", 1));
    
    v.add(new MBeanOperationInfo("stop", "stop service", params, "void", 1));
    
    v.add(new MBeanOperationInfo("restart", "stop service", params, "void", 1));
    
    v.add(new MBeanOperationInfo("getInfor", "get configuration information and runtime state of this service", params, "java.lang.String", 1));
  
    v.add(new MBeanOperationInfo("ping", "get the period between the time ping() called and the lastest transation start time", params, "java.lang.Long", 1));
    
    v.add(new MBeanOperationInfo("loadLoggerName", "get logger name of services", params, "java.lang.String", 1));
    
    v.add(new MBeanOperationInfo("readConfig", "get Document represent the config file for create new child Thread", params, "org.w3c.dom.Document", 1));

    params = new MBeanParameterInfo[5];
    params[0] = new MBeanParameterInfo("childName", "java.lang.String", "Child's name that in form of key=value");
    params[1] = new MBeanParameterInfo("className", "java.lang.String", "An extend class of ProcessThreadMX class that is used to create an instance");
    
    params[2] = new MBeanParameterInfo("constructParam", "java.lang.String", "The String that is used to provide parammeters for constructing new instance of className");
    
    params[3] = new MBeanParameterInfo("variable", "java.lang.String", "variable assigned to new instance");
    
    params[4] = new MBeanParameterInfo("followingThread", "java.lang.String", "Name of thread that run following this child");
    
    v.add(new MBeanOperationInfo("addChild", "create a ProcessThreadMX thread that is assigned to be a child of this thread", params, "void", 1));
 
    params = new MBeanParameterInfo[1];
    params[0] = new MBeanParameterInfo("dump", "java.lang.Boolean", "boolean value indicate track thread or not");
    v.add(new MBeanOperationInfo("setDump", "set dumping status of current thread", params, "void", 1));

    params = new MBeanParameterInfo[0];
    v.add(new MBeanOperationInfo("loadParams", "load params", params, "java.lang.String", 1));

    params = new MBeanParameterInfo[1];
    params[0] = new MBeanParameterInfo("newConfig", "java.lang.String", "The String express new config");
    v.add(new MBeanOperationInfo("saveParams", "save params", params, "java.lang.String", 1));

    params = new MBeanParameterInfo[3];
    params[0] = new MBeanParameterInfo("mbean", "java.lang.String", "mbean's object name");
    params[1] = new MBeanParameterInfo("timeout_inMinutes", "java.lang.Integer", "thread timeout");
    params[2] = new MBeanParameterInfo("sendSMS", "java.lang.Boolean", "sendSMS");
    v.add(new MBeanOperationInfo("insertThreadTimeout", "insert thread timeout", params, "java.lang.Integer", 1));

    params = new MBeanParameterInfo[3];
    params[0] = new MBeanParameterInfo("mbean", "java.lang.String", "mbean's object name");
    params[1] = new MBeanParameterInfo("timeout_inMinutes", "java.lang.Integer", "thread timeout");
    params[2] = new MBeanParameterInfo("sendSMS", "java.lang.Boolean", "sendSMS");
    v.add(new MBeanOperationInfo("editThreadTimeout", "edit thread timeout", params, "java.lang.Integer", 1));

    params = new MBeanParameterInfo[1];
    params[0] = new MBeanParameterInfo("mbean", "java.lang.String", "mbean's object name");
    
    v.add(new MBeanOperationInfo("deleteThreadTimeout", "delete thread timeout", params, "java.lang.Integer", 1));

    params = new MBeanParameterInfo[1];
    params[0] = new MBeanParameterInfo("defaultTimeout", "java.lang.Integer", "default timeout (in minutes)");
    
    v.add(new MBeanOperationInfo("setDefaultTimeout", "set deafult tiemout", params, "void", 1));

    params = new MBeanParameterInfo[1];
    params[0] = new MBeanParameterInfo("defaultPoolingPeriod", "java.lang.Integer", "default pooling Period (in minutes)");
   
    v.add(new MBeanOperationInfo("setPoolingPeriod", "set pooling period", params, "void", 1));

    params = new MBeanParameterInfo[0];
    v.add(new MBeanOperationInfo("reloadDatabase", "reload timeout infos from database", params, "boolean", 1));

    params = new MBeanParameterInfo[1];
    params[0] = new MBeanParameterInfo("time_between_two_SMS", "java.lang.Integer", "time between two SMS (in minutes)");
    
    v.add(new MBeanOperationInfo("setTimeBetweenTwoSMS", "set time between two SMS", params, "boolean", 1));
    return (MBeanOperationInfo[])v.toArray(new MBeanOperationInfo[v.size()]);
  }
  
  public boolean reloadDatabase()
  {
    if ((this.applicationId != null) && (!this.applicationId.equals("")))
    {
      HashMap<String, TimeoutInfo> temp = DatabaseAccessor.shareInstance().selectThreadTimeout(this.applicationId);
      if (temp != null) {
        synchronized (this.threadTimeoutLock)
        {
          this.threadTimeout = temp;
          return true;
        }
      }
    }
    return false;
  }
  
  public String getInfor()
  {
    StringBuilder str = new StringBuilder(super.getInfor());
    str.append(System.getProperty("line.separator"));
    str.append("Recent blocked thread: ");
    str.append(System.getProperty("line.separator"));
    for (ObjectName thread : this.blockedThreadList)
    {
      str.append("\t");
      str.append(thread.toString());
      str.append(System.getProperty("line.separator"));
    }
    str.append("Time between two SMS: ");
    str.append(this.smsThreshold / 60000L);
    str.append(" minutes");
    str.append(System.getProperty("line.separator"));
    
    str.append("Pooling period: ");
    str.append(this.defaultPoolingPeriod);
    str.append(" miliseconds");
    str.append(System.getProperty("line.separator"));
    
    str.append("Default timeout: ");
    str.append(this.defaultTimeout);
    str.append(" miliseconds");
    str.append(System.getProperty("line.separator"));
    
    str.append("Timeout infos: \tthread'name - timeout(minutes) - sendSMS");
    str.append(System.getProperty("line.separator"));
    synchronized (this.threadTimeoutLock)
    {
      Set<Map.Entry<String, TimeoutInfo>> s = this.threadTimeout.entrySet();
      Iterator<Map.Entry<String, TimeoutInfo>> i = s.iterator();
      while (i.hasNext())
      {
        Map.Entry<String, TimeoutInfo> entry = (Map.Entry)i.next();
        str.append("\t");
        str.append((String)entry.getKey());
        str.append(" - ");
        str.append(((TimeoutInfo)entry.getValue()).getTimeout());
        str.append(" - ");
        str.append(((TimeoutInfo)entry.getValue()).isSendSMS() ? "Yes" : "No");
        str.append(System.getProperty("line.separator"));
      }
    }
    return str.toString();
  }
  
  private long smsThreshold = 1200000L;
  private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
  private Date lastSend = null;
  
  private void filterNotification(String threadName)
  {
    Date now = new Date();
    String sms = "Chuong trinh " + this.applicationId + ": tien trinh " + threadName + " bi block luc " + this.dateFormat.format(now);
    
    this.logger.info(sms);
    if ((this.lastSend == null) || (now.getTime() - this.lastSend.getTime() > this.smsThreshold))
    {
      DatabaseAccessor.shareInstance().requestSendSMS(sms, this.applicationId);
      this.lastSend = now;
      if (!this.blockedSMS.contains(threadName)) {
        this.blockedSMS.add(threadName);
      }
    }
  }
  
  private boolean setTimeBetweenTwoSMS(Integer time)
  {
    if (time.intValue() >= 1)
    {
      this.smsThreshold = (time.intValue() * 60000L);
      return true;
    }
    return false;
  }
}

