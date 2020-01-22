package com.viettel.bccsgw.utils;
 
 import javax.management.MBeanException;
 import javax.management.MBeanOperationInfo;
 import javax.management.ReflectionException;
 
 public abstract class LoadableAgentMX
   extends AgentMX
 {
   public abstract void reload();
   
   protected MBeanOperationInfo[] buildOperations()
   {
     MBeanOperationInfo[] mbOperations = super.buildOperations();
     MBeanOperationInfo[] mbNewOperations = new MBeanOperationInfo[mbOperations.length + 1];
     System.arraycopy(mbOperations, 0, mbNewOperations, 0, mbOperations.length);
     mbNewOperations[mbOperations.length] = new MBeanOperationInfo("reload", "reload module", null, "void", 1);
     
 
 
 
     return mbNewOperations;
   }
   
   public Object invoke(String operationName, Object[] params, String[] signature)
     throws MBeanException, ReflectionException
   {
     if ("reload".equals(operationName))
     {
       reload();
       return null;
     }
     return super.invoke(operationName, params, signature);
   }
 }


