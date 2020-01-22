/*
 * Copyright 2019 olivier.tatsinkou.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.viettel.bccsgw.main;

/**
 *
 * @author olivier.tatsinkou
 */
import com.viettel.bccsgw.logging.ImportActionLogWSThread;
import com.viettel.bccsgw.logging.ImportRequestLogThread;
import com.viettel.bccsgw.logging.SendSms;
import com.viettel.bccsgw.logging.UpdateExpire;
import com.viettel.bccsgw.utils.MemoryDataGateway;
import com.viettel.bccsgw.utils.RequestInfo;
import com.viettel.mmserver.base.ProcessThreadMX;
import com.vtc.service.Charging;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Properties;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.xml.ws.Endpoint;
import org.apache.log4j.Logger;

public class ServiceManagementThread   extends ProcessThreadMX {
  
  public static String schema;
  private static ImportRequestLogThread requestLogThread;
  private static ImportActionLogWSThread actionLogWsThread;
  private static UpdateExpire updateExpire;
  private static SendSms sendSms;
  private static final String REQUEST_LOG_THREAD = "etc"+File.separator+"log"+File.separator+"RequestLog.properties";
  private static final String ACTION_LOG_WS_THREAD = "etc"+File.separator+"log"+File.separator+"ActionLogWS.properties";
  private static final String UPDATE_EXPIRE_CONF = "etc"+File.separator+"updateExpire.properties";
  private static final String SEND_SMS_CONF = "etc"+File.separator+"sendSms.properties";
  
  public ServiceManagementThread(String threadName)
  {
    super(threadName);
    try
    {
      registerAgent("BCCSGWThread:type=BCCSGWThread");
      this.logger.info("Publishing service...");
      Properties props = new Properties();
      props.load(new FileInputStream( "etc"+File.separator+"system.properties"));
      String ip = props.getProperty("IP");
      String port = props.getProperty("PORT");
      String context = props.getProperty("CONTEXT");
      String appName = props.getProperty("APP_NAME");
      ServiceManagement service = new ServiceManagement(this.logger, appName);
      Endpoint endpoint = Endpoint.publish("http://" + ip + ":" + port + "/" + context, service);
      
      Charging serviceCharge = new Charging(this.logger, appName);
      Endpoint endpointCharge = Endpoint.publish("http://" + ip + ":" + port + "/" + Charging.class.getSimpleName(), serviceCharge);
      

      boolean publishStatus = false;
      
      publishStatus = endpoint.isPublished();
      String msg = publishStatus ? "Service is published!" : "Error while publishing service!";
      this.logger.info(msg);
      System.out.println(msg);
      
      publishStatus = endpointCharge.isPublished();
      msg = publishStatus ? "Service charging is published!" : "Error while publishing service charging!";
      this.logger.info(msg);
      System.out.println(msg);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      this.logger.error(e.toString(), e);
    }
  }
  
  protected void process()
  {
    try
    {
      Thread.currentThread().join();
    }
    catch (InterruptedException ex)
    {
      this.logger.error("Error process: " + ex.toString(), ex);
    }
  }
  
  public static void main(String[] args)
  {
    try
    {
      new ServiceManagementThread("ServiceManagementThread").start();

      FileInputStream fis = new FileInputStream("etc"+File.separator+"log_writer.cfg");
      Properties properties = new Properties();
      properties.load(fis);
      fis.close();
      String logpath = properties.getProperty("logpath");
      requestLogThread = new ImportRequestLogThread("ImportRequestLogThread", "etc"+File.separator+"log"+File.separator+"RequestLog.properties", logpath + "/bccsgw_log");
      requestLogThread.start();
      actionLogWsThread = new ImportActionLogWSThread("ImportActionLogWSThread", "etc"+File.separator+"log"+File.separator+"ActionLogWS.properties", logpath + "/action_log_ws");
      actionLogWsThread.start();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public Object invoke(String operationName, Object[] params, String[] signature)
    throws MBeanException, ReflectionException
  {
    if (operationName == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"), "Cannot invoke a null operation in " + this.dClassName);
    }
    if (operationName.equals("reloadData")) {
      try
      {
        reloadData();
      }
      catch (Exception ex)
      {
        return ex;
      }
    } else {
      return super.invoke(operationName, params, signature);
    }
    return null;
  }
  
  public String getInfor()
  {
    StringBuffer buff = new StringBuffer();
    buff.append("BCCSGW Detail");
    for (RequestInfo reqInfo : ServiceManagement.getReqManager().values())
    {
      buff.append(System.getProperty("line.separator"));
      buff.append("     ");
      buff.append(reqInfo.getUsername() + ": " + reqInfo.getCurrentNumRequest());
    }
    buff.append(System.getProperty("line.separator"));
    buff.append(super.getInfor());
    return buff.toString();
  }
  
  protected MBeanOperationInfo[] buildOperations()
  {
    MBeanOperationInfo[] mbInfors = super.buildOperations();
    MBeanOperationInfo[] mbNewInfors = new MBeanOperationInfo[mbInfors.length + 1];
    System.arraycopy(mbInfors, 0, mbNewInfors, 0, mbInfors.length);
    MBeanParameterInfo[] none = new MBeanParameterInfo[0];
    mbNewInfors[mbInfors.length] = new MBeanOperationInfo("reloadData", "reload data to memory", none, "void", 0);

    return mbNewInfors;
  }
  
  public void reloadData()
    throws Exception
  {}
}
