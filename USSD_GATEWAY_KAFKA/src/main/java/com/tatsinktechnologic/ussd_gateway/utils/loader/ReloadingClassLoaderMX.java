 package com.tatsinktechnologic.ussd_gateway.utils.loader;
 
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
 import javax.management.ObjectName;
 import javax.management.ReflectionException;
 import javax.management.RuntimeOperationsException;
 
 public abstract class ReloadingClassLoaderMX
   extends ClassLoader
   implements DynamicMBean
 {
   protected String dClassName;
   protected String dDescription;
   protected MBeanAttributeInfo[] dAttributes;
   protected MBeanConstructorInfo[] dConstructors;
   protected MBeanOperationInfo[] dOperations;
   protected MBeanNotificationInfo[] dNotifications;
   protected MBeanInfo dMBeanInfo;
   
   public ReloadingClassLoaderMX(ClassLoader parent)
   {
     super(parent);
     buildDynamicMBeanInfo();
   }
   
   public abstract void setBaseDir(String paramString);
   
   public abstract String getBaseDir();
   
   public abstract void addClassPath(String paramString);
   
   public abstract void removeClassPath(String paramString);
   
   public abstract void updateClassPath();
   
   public abstract String[] getClassPath();
   
   public abstract boolean isResourceChanged();
   
   public abstract String getInfor();
   
   public Object invoke(String operationName, Object[] params, String[] signature)
     throws MBeanException, ReflectionException
   {
     if (operationName == null) {
       throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"), "Cannot invoke a null operation in " + this.dClassName);
     }
     if (operationName.equals("updateClassPath"))
     {
       updateClassPath();
       return null;
     }
     if (operationName.equals("addClassPath"))
     {
       if ((params != null) && (params.length > 0)) {
         addClassPath(params[0].toString());
       }
       return null;
     }
     if (operationName.equals("removeClassPath"))
     {
       if ((params != null) && (params.length > 0)) {
         removeClassPath(params[0].toString());
       }
       return null;
     }
     if (operationName.equals("isResourceChanged")) {
       return Boolean.valueOf(isResourceChanged());
     }
     if (operationName.equals("getInfor")) {
       return getInfor();
     }
     throw new ReflectionException(new NoSuchMethodException(operationName), "Cannot find the operation " + operationName + " in " + this.dClassName);
   }
   
   protected MBeanOperationInfo[] buildOperations()
   {
     ArrayList<MBeanOperationInfo> v = new ArrayList();
     MBeanParameterInfo[] params = { new MBeanParameterInfo("url", "java.lang.String", "the classpath as url") };
     
 
 
     v.add(new MBeanOperationInfo("addClassPath", "add URL to classpath of loader", params, "void", 1));
     
 
 
 
 
     v.add(new MBeanOperationInfo("removeClassPath", "remove a classpath from loader classpath", params, "void", 1));
     
 
 
 
 
     v.add(new MBeanOperationInfo("updateClassPath", "update the classpath when resouces has changed", null, "void", 1));
     
 
 
 
 
     v.add(new MBeanOperationInfo("getInfor", "get loader information", null, "java.lang.String", 0));
     
 
 
 
 
     return (MBeanOperationInfo[])v.toArray(new MBeanOperationInfo[v.size()]);
   }
   
   protected MBeanAttributeInfo[] buildAttributes()
   {
     ArrayList<MBeanAttributeInfo> v = new ArrayList();
     v.add(new MBeanAttributeInfo("ClassPath", "java.lang.String[]", "the classpaths of loader", true, false, false));
     
 
 
 
 
 
     v.add(new MBeanAttributeInfo("BaseDir", "java.lang.String", "the root directory of loader", true, false, false));
     
 
 
 
 
 
     return (MBeanAttributeInfo[])v.toArray(new MBeanAttributeInfo[v.size()]);
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
     if (name.equals("BaseDir")) {
       try
       {
         if (Class.forName("java.lang.String").isAssignableFrom(value.getClass())) {
           setBaseDir((String)value);
         } else {
           throw new InvalidAttributeValueException("Cannot set attribute " + name + " to a " + value.getClass().getName() + " object, String expected");
         }
       }
       catch (ClassNotFoundException e)
       {
         e.printStackTrace();
       }
     } else {
       throw new AttributeNotFoundException("Attribute " + name + " not found in " + getClass().getName());
     }
   }
   
   public void registerAgent(String objName)
     throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException
   {
     MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
     
     ObjectName mbeanName = new ObjectName(objName);
     
     mbs.registerMBean(this, mbeanName);
   }
   
   public void unregisterAgent(String objName)
     throws MalformedObjectNameException, InstanceNotFoundException, MBeanRegistrationException
   {
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
     if (attribute_name.equals("BaseDir")) {
       return getBaseDir();
     }
     if (attribute_name.equals("ClassPath")) {
       return getClassPath();
     }
     throw new AttributeNotFoundException("Cannot find " + attribute_name + " attribute in " + this.dClassName);
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
       catch (MBeanException e)
       {
         e.printStackTrace();
       }
       catch (ReflectionException e)
       {
         e.printStackTrace();
       }
       catch (InvalidAttributeValueException e)
       {
         e.printStackTrace();
       }
     }
     return resultList;
   }
   
   public MBeanInfo getMBeanInfo()
   {
     return this.dMBeanInfo;
   }
   
   protected MBeanConstructorInfo[] buildConstructors()
   {
     ArrayList<MBeanConstructorInfo> v = new ArrayList();
     Constructor[] constructors = getClass().getConstructors();
     v.add(new MBeanConstructorInfo("Constructs a service object", constructors[0]));
     
 
 
     return (MBeanConstructorInfo[])v.toArray(new MBeanConstructorInfo[v.size()]);
   }
   
   protected MBeanNotificationInfo[] buildNotifications()
   {
     return new MBeanNotificationInfo[0];
   }
   
   protected void buildDynamicMBeanInfo()
   {
     this.dClassName = getClass().getName();
     this.dDescription = "The process which run in a separator thread";
     this.dAttributes = buildAttributes();
     this.dConstructors = buildConstructors();
     this.dOperations = buildOperations();
     this.dNotifications = buildNotifications();
     this.dMBeanInfo = new MBeanInfo(this.dClassName, this.dDescription, this.dAttributes, this.dConstructors, this.dOperations, this.dNotifications);
   }
 }


