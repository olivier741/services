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
package com.viettel.mmserver.base;

/**
 *
 * @author olivier.tatsinkou
 */
import com.viettel.mmserver.agent.MMbeanServer;
import java.util.HashMap;
import java.util.Set;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class StatusCollector
  implements StatusCollectorMBean
{
  public StatusCollector()
  {
    try
    {
      MMbeanServer.getInstance().registerMBean(this, new ObjectName("Tools:name=StatusCollector"));
    }
    catch (Exception ex)
    {
      Log.info(ex);
    }
  }
  
  public HashMap<String, Integer> collectStatus()
  {
    HashMap<String, Integer> statuses = new HashMap();
    Set<ObjectName> mbeans = MMbeanServer.getInstance().queryNames(null, null);
    for (ObjectName mbean : mbeans) {
      try
      {
        MBeanAttributeInfo[] mbeanInfor = MMbeanServer.getInstance().getMBeanInfo(mbean).getAttributes();
        for (MBeanAttributeInfo attInfo : mbeanInfor) {
          if (attInfo.getName().equals("Status")) {
            try
            {
              statuses.put(mbean.toString(), (Integer)MMbeanServer.getInstance().getAttribute(mbean, "Status"));
            }
            catch (MBeanException ex)
            {
              Log.info(ex);
            }
            catch (AttributeNotFoundException ex)
            {
              Log.info(ex);
            }
          }
        }
      }
      catch (InstanceNotFoundException ex)
      {
        Log.info(ex);
      }
      catch (IntrospectionException ex)
      {
        Log.info(ex);
      }
      catch (ReflectionException ex)
      {
        Log.info(ex);
      }
    }
    return statuses;
  }
}

