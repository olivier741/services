/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.basev1;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.mmserver.agent.MMbeanServer;
import com.tatsinktechnologic.ussd_gateway.mmserver.base.Log;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import org.apache.log4j.Logger;

public abstract class MmProcessAbstract
  implements Runnable, MmProcess, DynamicMBean, NotificationEmitter
{
  private static int allId = 0;
  private Integer id;
  private String dClassName;
  private Thread internalThread;
  private volatile boolean running;
  private int status = 1;
  private final BooleanLock suspendRequested;
  private final BooleanLock internalSuspended;
  private final String threadName;
  protected Logger logger;
  private TaskInfo taskinfo = null;
  public static final String TYPE = "process.status";
  private NotificationBroadcasterSupport notificationHandler;
  private String mbeanName = "";
  private String dDescription;
  private MBeanAttributeInfo[] dAttributes;
  private MBeanConstructorInfo[] dConstructors;
  private MBeanOperationInfo[] dOperations;
  private MBeanNotificationInfo[] dNotifications;
  private MBeanInfo dMBeanInfo;
  
  private MmProcessAbstract(boolean bstart, String aThreadName)
    throws NotCompliantMBeanException
  {
    this.dClassName = getClass().getName();
    buildDynamicMBeanInfo();
    this.notificationHandler = new NotificationBroadcasterSupport();
    
    allId += 1;
    this.id = Integer.valueOf(allId);
    
    this.threadName = (aThreadName + "." + this.id);
    
    this.suspendRequested = new BooleanLock(false);
    this.internalSuspended = new BooleanLock(false);
    

    registerMangement();
    

    this.internalThread = new Thread(new Runnable()
    {
      public void run()
      {
        Log.info("internal thread start");
        if (MmProcessAbstract.this.preProcess()) {
          MmProcessAbstract.this.performJob();
        }
        MmProcessAbstract.this.posProcess();
        
        Log.info("internal thread stopped");
      }
    }, this.threadName);
    if (bstart)
    {
      start();
      if (this.taskinfo != null) {
        this.taskinfo.setRunningState(TaskInfo.RunningState.RUNNING);
      }
    }
  }
  
  private MmProcessAbstract(boolean bstart, String aThreadName, String mbeanName)
    throws NotCompliantMBeanException
  {
    this.dClassName = getClass().getName();
    buildDynamicMBeanInfo();
    this.notificationHandler = new NotificationBroadcasterSupport();
    
    allId += 1;
    this.id = Integer.valueOf(allId);
    
    this.mbeanName = mbeanName;
    this.threadName = (aThreadName + "." + this.id);
    
    this.suspendRequested = new BooleanLock(false);
    this.internalSuspended = new BooleanLock(false);
    

    registerMangement();
    

    this.internalThread = new Thread(this, this.threadName);
    if (bstart)
    {
      start();
      if (this.taskinfo != null) {
        this.taskinfo.setRunningState(TaskInfo.RunningState.RUNNING);
      }
    }
  }
  
  private void registerMangement()
  {
    ProcessManagerv1.getInstance().addMmProcess(this);
    if (this.mbeanName.equals("")) {
      this.mbeanName = ("MMLib:type=ProcessManagerList,name=" + this.threadName);
    }
    try
    {
      MMbeanServer.getInstance().registerMBean(this, new ObjectName(this.mbeanName));
      

      this.logger = Logger.getLogger(this.mbeanName);
    }
    catch (Exception ex)
    {
      Log.warn("Register JMX error", ex);
    }
  }
  
  private void unRegisterMangement()
  {
    try
    {
      MMbeanServer.getInstance().unregisterMBean(new ObjectName(this.mbeanName));
    }
    catch (Exception ex)
    {
      Log.warn("Remove JMX error", ex);
    }
    ProcessManagerv1.getInstance().unManageProcess(getId());
    if (this.taskinfo != null) {
      this.taskinfo.setRunningState(TaskInfo.RunningState.READY);
    }
  }
  
  public MmProcessAbstract(String threadName)
    throws NotCompliantMBeanException
  {
    this(true, threadName);
  }
  
  public MmProcessAbstract(String threadName, String mbeanName)
    throws NotCompliantMBeanException
  {
    this(true, threadName, mbeanName);
  }
  
  public MmProcessAbstract(String name, boolean bstart)
    throws NotCompliantMBeanException
  {
    this(bstart, name);
  }
  
  public MmProcessAbstract(TaskInfo taskinfo)
    throws NotCompliantMBeanException
  {
    this(taskinfo.getName());
    

    this.taskinfo = taskinfo;
  }
  
  protected final void performJob()
  {
    while (this.running)
    {
      try
      {
        waitWhileSuspended();
      }
      catch (InterruptedException e)
      {
        Log.error("interrupted: " + e.getMessage());
      }
      if (!this.running) {
        try
        {
          elmProcess();
        }
        catch (InterruptedException e)
        {
          Thread.currentThread().interrupt();
          Log.error("interrupted: " + e.getMessage());
        }
      }
    }
    Log.info("internal thread leave performJob");
  }
  
  protected boolean preProcess()
  {
    return true;
  }
  
  protected void posProcess() {}
  
  protected abstract void elmProcess()
    throws InterruptedException;
  
  private void waitWhileSuspended()
    throws InterruptedException
  {
    synchronized (this.suspendRequested)
    {
      try
      {
        this.internalSuspended.setValue(true);
        this.suspendRequested.waitUntilFalse(0L);
      }
      finally
      {
        this.internalSuspended.setValue(false);
      }
    }
  }
  
  public final void setThreadName(String name)
  {
    this.internalThread.setName(name);
  }
  
  public final String getThreadName()
  {
    return this.internalThread.getName();
  }
  
  public final synchronized void start()
  {
    Log.info("requestStart");
    if (!this.running)
    {
      this.running = true;
      this.internalThread = new Thread(this, this.threadName);
      this.internalThread.start();
      setProcessStatus(0);
      this.logger.info(this.threadName + " process  is started");
      Notification notification = new Notification("process.status", Integer.valueOf(getProcessStatus()), 0L, getThreadName() + " started");
      this.notificationHandler.sendNotification(notification);
    }
    else
    {
      this.logger.info(this.threadName + " process  is started");
    }
  }
  
  public void restart()
  {
    stop();
    
    start();
  }
  
  protected int getProcessStatus()
  {
    return this.status;
  }
  
  protected void setProcessStatus(int status)
  {
    this.status = status;
  }
  
  public boolean isStopRequested()
  {
    return (this.status == 2) || (this.status == 1);
  }
  
  public final void stop()
  {
    if (this.running)
    {
      setProcessStatus(2);
      this.running = false;
      if (this.internalThread != null) {
        this.internalThread.interrupt();
      }
      try
      {
        if ((this.internalThread != null) && (this.internalThread.isAlive()))
        {
          this.logger.info("waiting " + this.threadName + " process stop...");
          this.internalThread.join();
        }
      }
      catch (InterruptedException ex)
      {
        this.logger.error("stop process exception:" + ex);
      }
      finally
      {
        this.logger.info(this.threadName + " process is stopped");
      }
      setProcessStatus(1);
      onRequestStop();
      Notification notification = new Notification("process.status", Integer.valueOf(getProcessStatus()), 0L, getThreadName() + " stopped");
      this.notificationHandler.sendNotification(notification);
    }
  }
  
  public String loadParams()
  {
    return "";
  }
  
  public String saveParams(String newConfig)
  {
    return "";
  }
  
  public void onRequestStop() {}
  
  public final void suspend()
  {
    Log.info("requestSuspend");
    this.suspendRequested.setValue(true);
  }
  
  public final void resume()
  {
    Log.info("requestResume");
    this.suspendRequested.setValue(false);
  }
  
  public final boolean isAlive()
  {
    return this.internalThread.isAlive();
  }
  
  public Thread.State getState()
  {
    return Thread.currentThread().getState();
  }
  
  public Integer getId()
  {
    return this.id;
  }
  
  public TaskInfo getTaskinfo()
  {
    return this.taskinfo;
  }
  
  public String toString()
  {
    StringBuilder sb = new StringBuilder("MmProcess: " + getThreadName());
    
    sb.append("\nprocess id:" + getId());
    sb.append("\nprocess state:" + getState().toString());
    
    return sb.toString();
  }
  
  public void run()
  {
    Log.info("Thread " + this.threadName + " start");
    if (preProcess()) {
      performJob();
    }
    posProcess();
    setProcessStatus(1);
    
    Log.info("internal thread stopped");
  }
  
  public String getInfor()
  {
    StringBuffer buff = new StringBuffer();
    buff.append("Thread Name:" + this.threadName);
    buff.append("\r\nStatus:" + getStatusDesc());
    return buff.toString();
  }
  
  public String getStatusDesc()
  {
    switch (this.status)
    {
    case 0: 
      return "running";
    case 1: 
      return "stopped";
    }
    return "stopping";
  }
  
  public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
    throws ListenerNotFoundException
  {
    this.notificationHandler.removeNotificationListener(listener, filter, handback);
  }
  
  public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
  {
    this.notificationHandler.addNotificationListener(listener, filter, handback);
  }
  
  public MBeanNotificationInfo[] getNotificationInfo()
  {
    return this.dNotifications;
  }
  
  public void removeNotificationListener(NotificationListener listener)
    throws ListenerNotFoundException
  {
    this.notificationHandler.removeNotificationListener(listener);
  }
  
  protected MBeanNotificationInfo[] buildNotifications()
  {
    this.dNotifications = new MBeanNotificationInfo[1];
    this.dNotifications[0] = new MBeanNotificationInfo(new String[] { "Process Status" }, Notification.class.getName(), "notification when the status of process changed");

    return this.dNotifications;
  }
  
  public Object getAttribute(String attributeNname)
    throws AttributeNotFoundException, MBeanException, ReflectionException
  {
    if (attributeNname == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke a getter of " + this.dClassName + " with null attribute name");
    }
    if (attributeNname.equals("ThreadName")) {
      return getThreadName();
    }
    if (attributeNname.equals("Status")) {
      return Integer.valueOf(getProcessStatus());
    }
    throw new AttributeNotFoundException("Cannot find " + attributeNname + " attribute in " + this.dClassName);
  }
  
  public void setAttribute(Attribute attribute)
    throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
  {
    if (attribute == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Cannot invoke a setter of " + this.dClassName + " with null attribute");
    }
    String name = attribute.getName();
    Object value = attribute.getValue();
    if (name == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke the setter of " + this.dClassName + " with null attribute name");
    }
    if (name.equals("ThreadName"))
    {
      try
      {
        if (Class.forName("java.lang.String").isAssignableFrom(value.getClass())) {
          setThreadName((String)value);
        } else {
          throw new InvalidAttributeValueException("Cannot set attribute " + name + " to a " + value.getClass().getName() + " object, String expected");
        }
      }
      catch (ClassNotFoundException e)
      {
        this.logger.error("Error in SetAttribue: " + e);
      }
    }
    else
    {
      if (name.equals("Status")) {
        throw new AttributeNotFoundException("Cannot set attribute " + name + " because it is read-only");
      }
      throw new AttributeNotFoundException("Attribute " + name + " not found in " + getClass().getName());
    }
  }
  
  public AttributeList getAttributes(String[] attributeNames)
  {
    if (attributeNames == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("attributeNames[] cannot be null"), "Cannot invoke a getter of " + this.dClassName);
    }
    AttributeList resultList = new AttributeList();
    if (attributeNames.length == 0) {
      return resultList;
    }
    for (int i = 0; i < attributeNames.length; i++) {
      try
      {
        Object value = getAttribute(attributeNames[i]);
        resultList.add(new Attribute(attributeNames[i], value));
      }
      catch (AttributeNotFoundException e)
      {
        this.logger.error("Error in Get Attributes");
        this.logger.error(e);
      }
      catch (MBeanException e)
      {
        this.logger.error("Error in Get Attributes");
        this.logger.error(e);
      }
      catch (ReflectionException e)
      {
        this.logger.error("Error in Get Attributes");
        this.logger.error(e);
      }
    }
    return resultList;
  }
  
  public AttributeList setAttributes(AttributeList attributes)
  {
    if (attributes == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("AttributeList attributes cannot be null"), "Cannot invoke a setter of " + this.dClassName);
    }
    AttributeList resultList = new AttributeList();
    if (attributes.isEmpty()) {
      return resultList;
    }
    for (Iterator i = attributes.iterator(); i.hasNext();)
    {
      Attribute attr = (Attribute)i.next();
      try
      {
        setAttribute(attr);
        String name = attr.getName();
        Object value = getAttribute(name);
        resultList.add(new Attribute(name, value));
      }
      catch (AttributeNotFoundException e)
      {
        this.logger.error("Error in SetAttributes");
        this.logger.error(e);
      }
      catch (InvalidAttributeValueException e)
      {
        this.logger.error("Error in SetAttributes");
        this.logger.error(e);
      }
      catch (MBeanException e)
      {
        this.logger.error("Error in SetAttributes");
        this.logger.error(e);
      }
      catch (ReflectionException e)
      {
        this.logger.error("Error in SetAttributes");
        this.logger.error(e);
      }
    }
    return resultList;
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
    if (operationName.equals("getInfor")) {
      return getInfor();
    }
    if (operationName.equals("loadParams")) {
      return loadParams();
    }
    if (operationName.equals("saveParams"))
    {
      String newConfig = (String)params[0];
      return saveParams(newConfig);
    }
    throw new ReflectionException(new NoSuchMethodException(operationName), "Cannot find the operation " + operationName + " in " + this.dClassName);
  }
  
  public MBeanInfo getMBeanInfo()
  {
    return this.dMBeanInfo;
  }
  
  protected MBeanAttributeInfo[] buildAttributes()
  {
    ArrayList<MBeanAttributeInfo> v = new ArrayList();
    v.add(new MBeanAttributeInfo("ThreadName", "java.lang.String", "The name of thread", true, true, false));
 
    v.add(new MBeanAttributeInfo("Status", "java.lang.Integer", "The status of thread, 0 - running 1-stopping 2- stopped", true, false, false));

    v.add(new MBeanAttributeInfo("Running", "java.lang.Boolean", "The running status", true, false, true));

    v.add(new MBeanAttributeInfo("Priority", "java.lang.Integer", "Priority of thread value range 1-10", true, true, false));

   return (MBeanAttributeInfo[])v.toArray(new MBeanAttributeInfo[v.size()]);
  }
  
  protected MBeanOperationInfo[] buildOperations()
  {
    ArrayList<MBeanOperationInfo> v = new ArrayList();
    MBeanParameterInfo[] params = new MBeanParameterInfo[0];
    v.add(new MBeanOperationInfo("start", "start service", params, "void", 1));

    v.add(new MBeanOperationInfo("stop", "stop service", params, "void", 1));

    v.add(new MBeanOperationInfo("restart", "stop service", params, "void", 1));

    v.add(new MBeanOperationInfo("getInfor", "get configuration information and runtime state of this service", params, "java.lang.String", 1));

    params = new MBeanParameterInfo[1];
    
    params[0] = new MBeanParameterInfo("dump", "java.lang.Boolean", "boolean value indicate track thread or not");
    v.add(new MBeanOperationInfo("setDump", "set dumping status of current thread", params, "void", 1));

    params = new MBeanParameterInfo[0];
    
    v.add(new MBeanOperationInfo("loadParams", "load params", params, "java.lang.String", 1));

    params = new MBeanParameterInfo[1];
    
    params[0] = new MBeanParameterInfo("newConfig", "java.lang.String", "The String express new config");
    v.add(new MBeanOperationInfo("saveParams", "save params", params, "java.lang.String", 1));

    return (MBeanOperationInfo[])v.toArray(new MBeanOperationInfo[v.size()]);
  }
  
  protected MBeanConstructorInfo[] buildConstructors()
  {
    ArrayList<MBeanConstructorInfo> v = new ArrayList();
    Constructor[] constructors = getClass().getConstructors();
    if ((constructors != null) && (constructors.length > 0)) {
      v.add(new MBeanConstructorInfo("Constructs a service object", constructors[0]));
    }
    return (MBeanConstructorInfo[])v.toArray(new MBeanConstructorInfo[v.size()]);
  }
  
  protected void buildDynamicMBeanInfo()
  {
    this.dDescription = "The process which run in a separator thread";
    this.dAttributes = buildAttributes();
    
    this.dConstructors = buildConstructors();
    
    this.dOperations = buildOperations();
    
    this.dNotifications = buildNotifications();
    
    this.dMBeanInfo = new MBeanInfo(this.dClassName, this.dDescription, this.dAttributes, this.dConstructors, this.dOperations, this.dNotifications);
  }
}