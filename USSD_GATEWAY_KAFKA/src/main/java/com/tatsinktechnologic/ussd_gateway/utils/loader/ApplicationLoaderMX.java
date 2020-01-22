 package com.tatsinktechnologic.ussd_gateway.utils.loader;
 
import com.tatsinktechnologic.ussd_gateway.utils.AgentMX;
 import javax.management.MBeanException;
 import javax.management.MBeanOperationInfo;
 import javax.management.MBeanParameterInfo;
 import javax.management.ReflectionException;
 import javax.management.RuntimeOperationsException;
 import org.apache.log4j.Logger;
 
 public abstract class ApplicationLoaderMX
   extends AgentMX
 {
   protected Logger logger;
   
   public abstract ReloadingClassLoader addLoader(String paramString);
   
   public abstract void removeLoader(String paramString);
   
   public abstract void updateLoader();
   
   public ApplicationLoaderMX()
   {
     this.logger = Logger.getLogger("Loader");
     this.dClassName = getClass().getName();
     buildDynamicMBeanInfo();
   }
   
   public Object invoke(String operationName, Object[] params, String[] signature)
     throws MBeanException, ReflectionException
   {
     if (operationName == null) {
       throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"), "Cannot invoke a null operation in " + this.dClassName);
     }
     if (operationName.equals("updateLoader"))
     {
       updateLoader();
       return null;
     }
     if (operationName.equals("addLoader"))
     {
       if ((params != null) && (params.length > 0)) {
         addLoader((String)params[0]);
       }
       return null;
     }
     if (operationName.equals("removeLoader"))
     {
       if ((params != null) && (params.length > 0)) {
         removeLoader((String)params[0]);
       }
       return null;
     }
     return super.invoke(operationName, params, signature);
   }
   
   protected MBeanOperationInfo[] buildOperations()
   {
     MBeanOperationInfo[] mbInfors = super.buildOperations();
     MBeanOperationInfo[] mbNewInfors = new MBeanOperationInfo[mbInfors.length + 3];
     System.arraycopy(mbInfors, 0, mbNewInfors, 0, mbInfors.length);
     MBeanParameterInfo[] params = { new MBeanParameterInfo("url", "java.lang.String", "the classpath as url") };

     mbNewInfors[mbInfors.length] = new MBeanOperationInfo("addLoader", "add new Class Loader for server", params, "void", 1);

     mbNewInfors[(mbInfors.length + 1)] = new MBeanOperationInfo("removeLoader", "remove a Class Loader from cache", params, "void", 1);
 
     mbNewInfors[(mbInfors.length + 2)] = new MBeanOperationInfo("updateLoader", "Update current Class Loader cache, detect resources changed", null, "void", 1);
    
     return mbNewInfors;
   }
 }


