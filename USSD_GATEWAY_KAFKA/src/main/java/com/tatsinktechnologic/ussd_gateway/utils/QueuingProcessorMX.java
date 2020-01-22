 package com.tatsinktechnologic.ussd_gateway.utils;
 
 import javax.management.MBeanException;
 import javax.management.MBeanOperationInfo;
 import javax.management.MBeanParameterInfo;
 import javax.management.ReflectionException;
 import javax.management.RuntimeOperationsException;
 import org.apache.log4j.Logger;
 
 public abstract class QueuingProcessorMX
   extends ProcessorMX
 {
   protected Queue queue;
   
   public QueuingProcessorMX(String threadName)
   {
     super(threadName);
   }
   
   public int queueSize()
   {
     return this.queue != null ? this.queue.size() : 0;
   }
   
   protected void dumpQueue(String queueName, Queue queue)
   {
     this.logger.info("queue size [" + queueName + "]");
     if (queue == null)
     {
       this.logger.info("queue is null");
     }
     else if (queue.isEmpty())
     {
       this.logger.info("queue is empty");
     }
     else
     {
       Object[] obs = queue.toArray();
       for (Object ob : obs) {
         this.logger.info(ob.toString());
       }
     }
   }
   
   public void dumpQueue()
   {
     dumpQueue(this.name, this.queue);
   }
   
   public Object invoke(String operationName, Object[] params, String[] signature)
     throws MBeanException, ReflectionException
   {
     if (operationName == null) {
       throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"), "Cannot invoke a null operation in " + this.dClassName);
     }
     if (operationName.equals("queueSize")) {
       return Integer.valueOf(queueSize());
     }
     if (operationName.equals("dumpQueue"))
     {
       dumpQueue();
       return null;
     }
     return super.invoke(operationName, params, signature);
   }
   
   protected MBeanOperationInfo[] buildOperations()
   {
     MBeanOperationInfo[] mbInfors = super.buildOperations();
     MBeanOperationInfo[] mbNewInfors = new MBeanOperationInfo[mbInfors.length + 2];
     System.arraycopy(mbInfors, 0, mbNewInfors, 0, mbInfors.length);
     MBeanParameterInfo[] params = new MBeanParameterInfo[0];
     mbNewInfors[mbInfors.length] = new MBeanOperationInfo("queueSize", "the the current message in queue", params, "java.lang.Integer", 1);
     
 
 
 
 
     mbNewInfors[(mbInfors.length + 1)] = new MBeanOperationInfo("dumpQueue", "dump queue status", params, "void", 1);
     
 
 
 
 
     return mbNewInfors;
   }
 }

