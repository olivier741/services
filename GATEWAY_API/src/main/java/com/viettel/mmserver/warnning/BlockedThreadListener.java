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
package com.viettel.mmserver.warnning;

/**
 *
 * @author olivier.tatsinkou
 */
import com.viettel.mmserver.agent.MMbeanServer;
import com.viettel.mmserver.base.Log;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

public class BlockedThreadListener
  implements NotificationListener
{
  public void handleNotification(Notification notification, Object handback) {}
  
  public void register()
  {
    try
    {
      MMbeanServer.getInstance().addNotificationListener(new ObjectName("Tools:name=BlockedDetector"), this, null, null);
    }
    catch (Exception e)
    {
      Log.warn("Exception when register to notificationlistener" + e);
    }
  }
}

