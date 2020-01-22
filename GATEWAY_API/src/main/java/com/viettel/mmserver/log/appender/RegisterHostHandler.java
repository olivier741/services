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
package com.viettel.mmserver.log.appender;

/**
 *
 * @author olivier.tatsinkou
 */
import com.viettel.mmserver.base.ConfigParam;
import com.viettel.mmserver.base.Log;
import com.viettel.mmserver.base.ProcessThreadMX;
import com.viettel.mmserver.database.DatabaseAccessor;
import java.util.ArrayList;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;

public class RegisterHostHandler
  extends ProcessThreadMX
{
  private static final int SLEEP_TIME = 1000;
  private DatabaseAccessor dbAccessor = null;
  private static RegisterHostHandler sharedInstance = null;
  private static String mBeanName = "Tools:name=RegisterHostHandler";
  private String appId = "";
  public static final String NOTIFICATION_TYPE = "errorDefiniton.change";
  
  public static synchronized RegisterHostHandler getInstance()
  {
    if (sharedInstance == null) {
      try
      {
        sharedInstance = new RegisterHostHandler();
      }
      catch (Exception ex)
      {
        Log.error("Critical error when init an instance of ErrorDefinitionHandler" + ex.getMessage());
        throw new RuntimeException("Critical error when init an instance of ErrorDefinitionHandler!");
      }
    }
    return sharedInstance;
  }
  
  private RegisterHostHandler()
    throws NotCompliantMBeanException, MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException
  {
    super("Register Host Handler");
    registerAgent(mBeanName);
    this.appId = ConfigParam.getInstance().loadAppId();
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
  
  public int registerHost(String hostId)
  {
    RegisterHostId.getInstance().addHost(hostId);
    
    return 0;
  }
  
  public Object invoke(String operationName, Object[] params, String[] signature)
    throws MBeanException, ReflectionException
  {
    if (operationName == null) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"), "Cannot invoke a null operation in " + this.dClassName);
    }
    if (operationName.equals("registerHost")) {
      return Integer.valueOf(registerHost((String)params[0]));
    }
    return super.invoke(operationName, params, signature);
  }
  
  protected MBeanOperationInfo[] buildOperations()
  {
    ArrayList<MBeanOperationInfo> v = new ArrayList();
    MBeanParameterInfo[] params = new MBeanParameterInfo[1];
    params[0] = new MBeanParameterInfo("hostId", "java.lang.String", "");
    
    v.add(new MBeanOperationInfo("registerHost", "register host", params, "String", 1));
    



    MBeanOperationInfo[] old = super.buildOperations();
    for (int i = 0; i < old.length; i++) {
      v.add(old[i]);
    }
    return (MBeanOperationInfo[])v.toArray(new MBeanOperationInfo[v.size()]);
  }
}
