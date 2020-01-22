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
import com.tatsinktechnologic.ussd_gateway.mmserver.base.ConfigParam;
import com.tatsinktechnologic.ussd_gateway.mmserver.base.Log;
import com.tatsinktechnologic.ussd_gateway.mmserver.base.ProcessThreadMX;
import com.tatsinktechnologic.ussd_gateway.mmserver.database.DatabaseAccessor;
import java.util.ArrayList;
import java.util.Date;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;

public class ErrorDefinitionHandler
  extends ProcessThreadMX
{
  private static final int SLEEP_TIME = 1000;
  private DatabaseAccessor dbAccessor = null;
  private static ErrorDefinitionHandler sharedInstance = null;
  private static String mBeanName = "Tools:name=ErrorDefinitionHandler";
  private String appId = "";
  public static final String NOTIFICATION_TYPE = "errorDefiniton.change";
  
  public static synchronized ErrorDefinitionHandler getInstance()
  {
    if (sharedInstance == null) {
      try
      {
        sharedInstance = new ErrorDefinitionHandler();
      }
      catch (Exception ex)
      {
        Log.error("Critical error when init an instance of ErrorDefinitionHandler" + ex.getMessage());
        throw new RuntimeException("Critical error when init an instance of ErrorDefinitionHandler!");
      }
    }
    return sharedInstance;
  }
  
  private ErrorDefinitionHandler()
    throws NotCompliantMBeanException, MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException
  {
    super("Error Definition Handler");
    registerAgent(mBeanName);
    this.appId = ConfigParam.getInstance().loadAppId();
    this.logger.info("Created an instance of ErrorDefinitionHandler");
  }
  
  protected void process()
  {
    try
    {
      Thread.sleep(1000L);
    }
    catch (InterruptedException ex)
    {
      ex.printStackTrace();
    }
  }
  
  public int addErrorDefinition(String userName, Boolean useFlag, String logLevel, String loggerName, String message, String threadName, String smsFormat, String smsContent, boolean sendSMS, String errorFrequence)
  {
    if (this.dbAccessor == null) {
      this.dbAccessor = DatabaseAccessor.shareInstance();
    }
    int errorID = this.dbAccessor.insertErrorDefinition(this.appId, userName, new Date(), useFlag.booleanValue(), logLevel, loggerName, message, threadName, smsFormat, smsContent, sendSMS, errorFrequence);
    if (errorID > 0)
    {
      Notification notification = new Notification("errorDefiniton.change", Integer.valueOf(errorID), 0L, "add");
      this.notificationHandler.sendNotification(notification);
    }
    return errorID;
  }
  
  public int sendSMSTo(String appID, String phoneNumList)
  {
    if (this.dbAccessor == null) {
      this.dbAccessor = DatabaseAccessor.shareInstance();
    }
    if (phoneNumList.equals(""))
    {
      this.dbAccessor.deleteApplicationReceiver(appID);
      this.dbAccessor.deleleReceiver(appID);
      return 1;
    }
    this.dbAccessor.deleleReceiver(appID);
    return this.dbAccessor.insertReceiver(appID, phoneNumList);
  }
  
  public int removeErrorDefiniton(int errorID)
  {
    if (this.dbAccessor == null) {
      this.dbAccessor = DatabaseAccessor.shareInstance();
    }
    int ierrorID = this.dbAccessor.deleteErrorDefinition(errorID);
    if (ierrorID > 0)
    {
      Notification notification = new Notification("errorDefiniton.change", Integer.valueOf(ierrorID), 0L, "remove");
      this.notificationHandler.sendNotification(notification);
    }
    return ierrorID;
  }
  
  public Object invoke(String operationName, Object[] params, String[] signature)
    throws MBeanException, ReflectionException
  {
    if (operationName == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"), "Cannot invoke a null operation in " + this.dClassName);
    }
    if (operationName.equals("addErrorDefinition")) {
      return Integer.valueOf(addErrorDefinition((String)params[0], (Boolean)params[1], (String)params[2], (String)params[3], (String)params[4], (String)params[5], (String)params[6], (String)params[7], ((Boolean)params[8]).booleanValue(), (String)params[9]));
    }
    if (operationName.equals("removeErrorDefinition")) {
      return Integer.valueOf(removeErrorDefiniton(((Integer)params[0]).intValue()));
    }
    if (operationName.equals("sendSMSTo")) {
      return Integer.valueOf(sendSMSTo((String)params[0], (String)params[1]));
    }
    return super.invoke(operationName, params, signature);
  }
  
  protected MBeanOperationInfo[] buildOperations()
  {
    ArrayList<MBeanOperationInfo> v = new ArrayList();
    MBeanParameterInfo[] params = new MBeanParameterInfo[10];
    params[0] = new MBeanParameterInfo("userName", "java.lang.String", "");
    params[1] = new MBeanParameterInfo("useFlag", "java.lang.Boolean", "");
    params[2] = new MBeanParameterInfo("logLevel", "java.lang.String", "");
    params[3] = new MBeanParameterInfo("loggerName", "java.lang.String", "");
    params[4] = new MBeanParameterInfo("message", "java.lang.String", "");
    params[5] = new MBeanParameterInfo("threadName", "java.lang.String", "");
    params[6] = new MBeanParameterInfo("smsFormat", "java.lang.String", "");
    params[7] = new MBeanParameterInfo("smsContent", "java.lang.String", "");
    params[8] = new MBeanParameterInfo("sendSMS", "java.lang.Boolean", "");
    params[9] = new MBeanParameterInfo("errorFrequence", "java.lang.String", "");
    v.add(new MBeanOperationInfo("addErrorDefinition", "add a new ErrorDefinition", params, "int", 1));
    





    params = new MBeanParameterInfo[1];
    params[0] = new MBeanParameterInfo("errorID", "java.lang.Integer", "");
    
    v.add(new MBeanOperationInfo("removeErrorDefinition", "remove an ErrorDefinition", params, "int", 1));
    




    v.add(new MBeanOperationInfo("sendSMSTo", "remove an ErrorDefinition", params, "int", 1));
    



    MBeanOperationInfo[] old = super.buildOperations();
    for (int i = 0; i < old.length; i++) {
      v.add(old[i]);
    }
    return (MBeanOperationInfo[])v.toArray(new MBeanOperationInfo[v.size()]);
  }
}
