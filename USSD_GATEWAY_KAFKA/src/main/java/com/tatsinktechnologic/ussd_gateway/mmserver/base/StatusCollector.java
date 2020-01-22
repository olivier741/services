/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.base;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.mmserver.agent.MMbeanServer;
import java.util.HashMap;
import java.util.Set;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
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
