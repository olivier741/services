/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.agent;

/**
 *
 * @author olivier.tatsinkou
 */
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;

public class MMbeanServer
{
  public static final String MM_MBEAN_SERVER_FACTORY_CLASS = "com.viettel.mmserver.mbeanserver.factoryclass";
  public static final String MM_MBEAN_SERVER_FACTORY_METHOD = "com.viettel.mmserver.mbeanserver.factorymethod";
  private static MBeanServer mBeanServer = null;
  
  public static synchronized MBeanServer getInstance()
  {
    return ManagementFactory.getPlatformMBeanServer();
  }
}
