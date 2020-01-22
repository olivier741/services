package com.viettel.bccsgw.utils;
 
 import javax.management.MBeanException;
 import javax.management.MBeanOperationInfo;
 import javax.management.MBeanParameterInfo;
 import javax.management.ReflectionException;
 
 public abstract class LoadableProcessorMX
   extends ProcessorMX
 {
   public LoadableProcessorMX(String name)
   {
     super(name);
   }
   
   public Object invoke(String operationName, Object[] params, String[] signature)
     throws MBeanException, ReflectionException
   {
     if (operationName.equals("reload"))
     {
       reload();
       return null;
     }
     return super.invoke(operationName, params, signature);
   }
   
   protected MBeanOperationInfo[] buildOperations()
   {
     MBeanOperationInfo[] mbInfors = super.buildOperations();
     MBeanOperationInfo[] mbNewInfors = new MBeanOperationInfo[mbInfors.length + 1];
     System.arraycopy(mbInfors, 0, mbNewInfors, 0, mbInfors.length);
     MBeanParameterInfo[] params = new MBeanParameterInfo[0];
     mbNewInfors[mbInfors.length] = new MBeanOperationInfo("reload", "reload current processor", params, "void", 1);
     
 
 
 
 
     return mbNewInfors;
   }
   
   public abstract void reload();
 }

