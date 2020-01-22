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
import com.tatsinktechnologic.ussd_gateway.mmserver.agent.MMbeanServer;
import com.tatsinktechnologic.ussd_gateway.mmserver.base.Log;
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
