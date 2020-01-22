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
package com.viettel.mmserver.agent;

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

