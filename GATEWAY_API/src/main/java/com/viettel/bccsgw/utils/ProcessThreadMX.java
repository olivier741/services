package com.viettel.bccsgw.utils;
 
 import java.lang.management.ManagementFactory;
 import java.lang.reflect.Constructor;
 import java.util.ArrayList;
 import java.util.Iterator;
 import javax.management.Attribute;
 import javax.management.AttributeList;
 import javax.management.AttributeNotFoundException;
 import javax.management.DynamicMBean;
 import javax.management.InstanceAlreadyExistsException;
 import javax.management.InstanceNotFoundException;
 import javax.management.InvalidAttributeValueException;
 import javax.management.ListenerNotFoundException;
 import javax.management.MBeanAttributeInfo;
 import javax.management.MBeanConstructorInfo;
 import javax.management.MBeanException;
 import javax.management.MBeanInfo;
 import javax.management.MBeanNotificationInfo;
 import javax.management.MBeanOperationInfo;
 import javax.management.MBeanParameterInfo;
 import javax.management.MBeanRegistrationException;
 import javax.management.MBeanServer;
 import javax.management.MalformedObjectNameException;
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
 
 public abstract class ProcessThreadMX
   extends ProcessThread
   implements DynamicMBean, NotificationEmitter
 {
   public static final String TYPE = "process.status";
   protected String dClassName;
   protected String dDescription;
   protected MBeanAttributeInfo[] dAttributes;
   protected MBeanConstructorInfo[] dConstructors;
   protected MBeanOperationInfo[] dOperations;
   protected MBeanNotificationInfo[] dNotifications;
   protected MBeanInfo dMBeanInfo;
   protected NotificationBroadcasterSupport notificationHandler;
   
   public ProcessThreadMX(String threadName)
   {
     super(threadName);
     this.dClassName = getClass().getName();
     buildDynamicMBeanInfo();
     this.notificationHandler = new NotificationBroadcasterSupport();
   }
   
   public void start()
   {
     super.start();
     Notification notification = new Notification("process.status", Integer.valueOf(getProcessStatus()), 0L, getThreadName() + " started");
     this.notificationHandler.sendNotification(notification);
   }
   
   public void stop()
   {
     super.stop();
     Notification notification = new Notification("process.status", Integer.valueOf(getProcessStatus()), 0L, getThreadName() + " stopped");
     this.notificationHandler.sendNotification(notification);
   }
   
   public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
     throws ListenerNotFoundException
   {
     this.notificationHandler.removeNotificationListener(listener, filter, handback);
   }
   
   public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
     throws IllegalArgumentException
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
   
   protected void registerAgent(String objName)
     throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException
   {
     this.logger.info("register MBeanServer:" + objName);
     
     MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
     
     ObjectName mbeanName = new ObjectName(objName);
     
     mbs.registerMBean(this, mbeanName);
   }
   
   protected void unregisterAgent(String objName)
     throws MalformedObjectNameException, InstanceNotFoundException, MBeanRegistrationException
   {
     this.logger.info("unregister MBeanServer:" + objName);
     
     MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
     
     ObjectName mbeanName = new ObjectName(objName);
     mbs.unregisterMBean(mbeanName);
   }
   
   public Object getAttribute(String attribute_name)
     throws AttributeNotFoundException, MBeanException, ReflectionException
   {
     if (attribute_name == null) {
       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke a getter of " + this.dClassName + " with null attribute name");
     }
     if (attribute_name.equals("ThreadName")) {
       return getThreadName();
     }
     if (attribute_name.equals("Running")) {
       return Boolean.valueOf(isRunning());
     }
     if (attribute_name.equals("Status")) {
       return Integer.valueOf(getProcessStatus());
     }
     if (attribute_name.equals("Priority")) {
       return Integer.valueOf(getPriority());
     }
     throw new AttributeNotFoundException("Cannot find " + attribute_name + " attribute in " + this.dClassName);
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
     if (name.equals("Priority"))
     {
       try
       {
         if (Class.forName("java.lang.Integer").isAssignableFrom(value.getClass())) {
           setPriority(((Integer)value).intValue());
         } else {
           throw new InvalidAttributeValueException("Cannot set attribute " + name + " to a " + value.getClass().getName() + " object, String expected");
         }
       }
       catch (ClassNotFoundException e)
       {
         e.printStackTrace();
       }
     }
     else if (name.equals("ThreadName"))
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
         e.printStackTrace();
       }
     }
     else
     {
       if (name.equals("Running")) {
         throw new AttributeNotFoundException("Cannot set attribute " + name + " because it is read-only");
       }
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
         e.printStackTrace();
       }
       catch (MBeanException e)
       {
         e.printStackTrace();
       }
       catch (ReflectionException e)
       {
         e.printStackTrace();
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
         e.printStackTrace();
       }
       catch (InvalidAttributeValueException e)
       {
         e.printStackTrace();
       }
       catch (MBeanException e)
       {
         e.printStackTrace();
       }
       catch (ReflectionException e)
       {
         e.printStackTrace();
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
     if (operationName.equals("setDump"))
     {
       Boolean b = (Boolean)params[0];
       setDumpThread(b.booleanValue());
       return null;
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
   
   protected MBeanNotificationInfo[] buildNotifications()
   {
     this.dNotifications = new MBeanNotificationInfo[1];
     this.dNotifications[0] = new MBeanNotificationInfo(new String[] { "Process Status" }, Notification.class.getName(), "notification when the status of process changed");
     
 
 
 
 
 
     return this.dNotifications;
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


