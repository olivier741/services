 package com.tatsinktechnologic.ussd_gateway.utils;
 
 import java.util.Vector;
 import javax.management.MBeanException;
 import javax.management.MBeanOperationInfo;
 import javax.management.MBeanParameterInfo;
 import javax.management.ReflectionException;
 import javax.management.RuntimeOperationsException;
 import org.apache.log4j.Logger;
 
 public abstract class ProcessorMX
   extends AgentMX
 {
   protected Logger logger;
   protected String name;
   
   public abstract void start();
   
   public abstract void stop();
   
   public abstract void restart();
   
   public ProcessorMX()
   {
     this("Noname");
   }
   
   public ProcessorMX(String name)
   {
     this.name = name;
     this.logger = Logger.getLogger(name);
     this.dClassName = getClass().getName();
     buildDynamicMBeanInfo();
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
     return super.invoke(operationName, params, signature);
   }
   
   protected MBeanOperationInfo[] buildOperations()
   {
     Vector<MBeanOperationInfo> v = new Vector();
     MBeanParameterInfo[] params = new MBeanParameterInfo[0];
     v.add(new MBeanOperationInfo("start", "start processor", params, "void", 1));
     
 
 
 
 
     v.add(new MBeanOperationInfo("stop", "stop processor", params, "void", 1));
     
 
 
 
 
     v.add(new MBeanOperationInfo("restart", "stop processor", params, "void", 1));
     
 
 
 
 
     v.add(new MBeanOperationInfo("getInfor", "get configuration information and runtime state of this processor", params, "java.lang.String", 1));
     
 
 
 
 
     return (MBeanOperationInfo[])v.toArray(new MBeanOperationInfo[v.size()]);
   }
   
   public String getName()
   {
     return this.name;
   }
   
   public void setName(String name)
   {
     this.name = name;
   }
 }

