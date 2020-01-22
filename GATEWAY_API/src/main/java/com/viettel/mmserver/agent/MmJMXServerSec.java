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
import com.viettel.mmserver.authenticator.ActionLogDbUtils;
import com.viettel.mmserver.authenticator.MmAuthenticator;
import com.viettel.mmserver.authenticator.MmUser;
import com.viettel.mmserver.base.ConfigParam;
import com.viettel.mmserver.base.Log;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Principal;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXAuthenticator;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXPrincipal;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.MBeanServerForwarder;
import javax.security.auth.Subject;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class MmJMXServerSec
  implements MmJMXServerSecMBean, NotificationEmitter
{
  private static MmJMXServerSec instance;
  private static String appId;
  private ActionLogDbUtils actionLogDbUtils;
  protected NotificationBroadcasterSupport notificationHandler;
  private static String ip;
  private static int port = 9000;
  private static Logger log = Logger.getLogger("mbeanserver");
  private static Hashtable<String, MmUser> userMap = new Hashtable();
  private JMXConnectorServer cs;
  
  public static MmJMXServerSec getInstance()
  {
    if (instance == null) {
      instance = new MmJMXServerSec();
    }
    return instance;
  }
  
  public void reload() {}
  
  public static String getIp()
  {
    return ip;
  }
  
  public static void setIp(String ip)
  {
    ip = ip;
  }
  
  public static int getPort()
  {
    return port;
  }
  
  public static void setPort(int port)
  {
    port = port;
  }
  
  private MmJMXServerSec()
  {
    if (this.notificationHandler == null) {
      this.notificationHandler = new NotificationBroadcasterSupport();
    }
    try
    {
      MMbeanServer.getInstance().registerMBean(this, new ObjectName("Tools:name=JMXConnector"));
    }
    catch (Exception ex)
    {
      log.error(ex);
    }
    appId = ConfigParam.getInstance().getAppID();
  }
  
  public void start()
    throws MalformedURLException, IOException
  {
    String actionLog = System.getProperty("com.viettel.mmserver.actionLog");
    if (((actionLog == null) || (actionLog.trim().equals("1"))) && 
      (this.actionLogDbUtils == null)) {
      this.actionLogDbUtils = new ActionLogDbUtils("actionlog");
    }
    if (this.cs != null) {
      throw new IllegalStateException("start must be call only one");
    }
    String authenticate = "on";
    authenticate = System.getProperty("com.viettel.mmserver.agent.authenticate", "on").trim();
    
    HashMap<String, Object> env = new HashMap();
    if (!ConfigParam.getInstance().getAppID().equals(""))
    {
      if (authenticate.equals("off"))
      {
        log.info("Authenticating was off, so no authenticator and authorization");
      }
      else
      {
        log.info("Init Authenticator");
        env.put("jmx.remote.authenticator", new RealmJMXAuthenticator());
      }
    }
    else {
      log.info("No appID, so no authenticator and authorization!");
    }
    if (ip == null) {
      ip = "0.0.0.0";
    }
    System.setProperty("java.rmi.server.randomIDs", "true");
    

    LocateRegistry.createRegistry(port);
    

    JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://" + ip + ":" + (port + 1) + "/jndi/rmi://" + ip + ":" + port + "/jmxrmi");
    

    MBeanServer mbs = MMbeanServer.getInstance();
    
    log.info("Creating JMX Url connection");
    this.cs = JMXConnectorServerFactory.newJMXConnectorServer(url, env, mbs);
    if (!ConfigParam.getInstance().getAppID().equals(""))
    {
      log.info("Creating MBean Server Forwarder");
      MBeanServerForwarder mbsf = MBSFInvocationHandler.newProxyInstance();
      this.cs.setMBeanServerForwarder(mbsf);
    }
    try
    {
      if (this.actionLogDbUtils != null) {
        addNotificationListener(this.actionLogDbUtils, null, null);
      }
    }
    catch (Exception ex)
    {
      log.info(ex);
    }
    this.cs.start();
    log.info("Started succesfully");
  }
  
  public void stop()
  {
    if (this.cs != null) {
      try
      {
        this.cs.stop();
      }
      catch (IOException e) {}
    }
    LogManager.shutdown();
  }
  
  public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
    throws ListenerNotFoundException
  {
    this.notificationHandler.removeNotificationListener(listener, filter, handback);
  }
  
  public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
    throws IllegalArgumentException
  {
    this.notificationHandler.addNotificationListener(listener, filter, handback);
  }
  
  public void removeNotificationListener(NotificationListener listener)
    throws ListenerNotFoundException
  {
    this.notificationHandler.removeNotificationListener(listener);
  }
  
  public MBeanNotificationInfo[] getNotificationInfo()
  {
    MBeanNotificationInfo[] dNotifications = new MBeanNotificationInfo[1];
    dNotifications[0] = new MBeanNotificationInfo(new String[] { "Method Invoke" }, Notification.class.getName(), "notification when method invoked remotely");
    





    return dNotifications;
  }
  
  private static String getLogMsg(String msg, Object[] args)
  {
    StringBuilder sb = new StringBuilder(msg);
    String indent = "\t\t\t\t";
    if (args != null)
    {
      sb.append("\r\n");
      sb.append(indent + "<args>\r\n");
      for (int i = 0; i < args.length; i++) {
        if (args[i] != null) {
          sb.append(indent + "   <" + args[i].getClass().getName() + ">" + getObString(args[i]) + "</>\r\n");
        }
      }
      sb.append(indent + "</args>");
    }
    sb.append("\r\n");
    return sb.toString();
  }
  
  private static String getObString(Object o)
  {
    if (o != null)
    {
      if ((o instanceof String)) {
        return (String)o;
      }
      if ((o instanceof String[]))
      {
        String[] tmp = (String[])o;
        StringBuilder sb = new StringBuilder();
        for (String s : tmp) {
          sb.append(s + "   ");
        }
        return sb.toString();
      }
      return o.toString();
    }
    return null;
  }
  
  public static class RealmJMXAuthenticator
    implements JMXAuthenticator
  {
    public Subject authenticate(Object credentials)
    {
      MmJMXServerSec.log.info("Authenticating!!!");
      if (credentials == null)
      {
        MmJMXServerSec.log.warn("Credentials required");
        throw new SecurityException("Credentials required");
      }
      if (!(credentials instanceof String[]))
      {
        MmJMXServerSec.log.warn("Credentials should be String[]");
        throw new SecurityException("Credentials should be String[]");
      }
      String[] aCredentials = (String[])credentials;
      if (aCredentials.length < 2)
      {
        MmJMXServerSec.log.warn("Credentials should have username/password");
        throw new SecurityException("Credentials should have username/password");
      }
      String username = aCredentials[0];
      String password = aCredentials[1];
      MmUser mmUser = null;
      try
      {
        mmUser = MmAuthenticator.getInstance().validate(username, password, MmJMXServerSec.appId);
        MmJMXServerSec.log.info("After validate user");
      }
      catch (Exception ex)
      {
        MmJMXServerSec.log.error("Error in validate user", ex);
        MmJMXServerSec.log.error(ex.getMessage(), ex);
        ex.printStackTrace();
      }
      catch (Throwable ex)
      {
        Log.error("Error in validate user", ex);
        ex.printStackTrace();
        Log.error(ex);
        throw new SecurityException(ex);
      }
      try
      {
        if (mmUser != null)
        {
          Log.info(Collections.EMPTY_SET);
          MmJMXServerSec.userMap.put(username, mmUser);
          MmJMXServerSec.log.info(username + " authenticated");
          return new Subject(true, Collections.singleton(new JMXPrincipal(aCredentials[0])), Collections.EMPTY_SET, Collections.EMPTY_SET);
        }
        MmJMXServerSec.log.warn(username + " invalid");
        throw new SecurityException("Invalid credentials");
      }
      catch (Exception e)
      {
        MmJMXServerSec.log.error("exception detail ", e);
        MmJMXServerSec.log.warn("user " + username + " authentiacte error");
        throw new SecurityException("Authenticate error");
      }
    }
  }
  
  public static class MBSFInvocationHandler
    implements InvocationHandler
  {
    public static final String TYPE = "method.invoke";
    private MBeanServer mbs;
    
    public static MBeanServerForwarder newProxyInstance()
    {
      InvocationHandler handler = new MBSFInvocationHandler();
      
      Class[] interfaces = { MBeanServerForwarder.class };
      

      Object proxy = Proxy.newProxyInstance(MBeanServerForwarder.class.getClassLoader(), interfaces, handler);
      



      return (MBeanServerForwarder)MBeanServerForwarder.class.cast(proxy);
    }
    
    public Object invoke(Object proxy, Method method, Object[] args)
      throws Throwable
    {
      try
      {
        String methodName = method.getName();
        if (methodName.equals("getMBeanServer"))
        {
          MmJMXServerSec.log.debug("getMBeanServer return");
          return this.mbs;
        }
        if (methodName.equals("setMBeanServer"))
        {
          if (args[0] == null)
          {
            MmJMXServerSec.log.warn("setMBeanServer return by null");
            throw new IllegalArgumentException("Null MBeanServer");
          }
          if (this.mbs != null)
          {
            MmJMXServerSec.log.warn("already initialized");
            throw new IllegalArgumentException("MBeanServer object already initialized");
          }
          this.mbs = ((MBeanServer)args[0]);
          MmJMXServerSec.log.debug("setMBeanServer return");
          return null;
        }
        AccessControlContext acc = AccessController.getContext();
        Subject subject = Subject.getSubject(acc);
        if (subject == null) {
          return method.invoke(this.mbs, args);
        }
        if ((methodName.equals("createMBean")) || (methodName.equals("unregisterMBean")))
        {
          MmJMXServerSec.log.warn("Access to \"createMBean\" and \"unregisterMBean\" restricted");
          throw new SecurityException("Access denied");
        }
        if ((args != null) && 
          ((args[0] instanceof ObjectName)))
        {
          ObjectName o = (ObjectName)args[0];
          if ((o.getDomain().equals("java.lang")) || (o.getDomain().equals("com.sun.management")) || (o.getDomain().equals("JMImplementation")) || (o.getDomain().equals("java.util.logging")) || (o.getDomain().equals("Log"))) {
            return method.invoke(this.mbs, args);
          }
          if ((o.toString().equals("Tools:name=MmAuthenticator")) || (o.toString().equals("Tools:name=BlockedDetector")) || (o.toString().equals("Tools:name=StatusCollector"))) {
            return method.invoke(this.mbs, args);
          }
        }
        Set<JMXPrincipal> principals = subject.getPrincipals(JMXPrincipal.class);
        if ((principals == null) || (principals.isEmpty()))
        {
          MmJMXServerSec.log.warn("No principal, access denied");
          throw new SecurityException("Access denied");
        }
        Principal principal = (Principal)principals.iterator().next();
        String identity = principal.getName();
        if ((args != null) && (args.length == 4) && 
          (args[0] != null) && ((args[0] instanceof ObjectName)) && (args[1] != null) && ((args[1] instanceof String)))
        {
          ObjectName o = (ObjectName)args[0];
          
          String strObjectName = o.toString();
          String strMethod = (String)args[1];
          MmJMXServerSec.log.debug(identity + " invokes method " + strMethod + " of " + o.toString());
          String param = "";
          if (args[2] != null)
          {
            Object[] params = (Object[])args[2];
            for (int i = 0; i < params.length; i++) {
              param = param + params[i].toString() + ",";
            }
            if (!param.equals("")) {
              param = param.substring(0, param.length() - 1);
            }
          }
          if ((strObjectName != null) && (!strObjectName.equals("")) && (strMethod != null) && (!strMethod.equals("")))
          {
            MmUser mmUser = (MmUser)MmJMXServerSec.userMap.get(identity);
            MmJMXServerSec.log.info("************" + identity);
            if (mmUser != null)
            {
              MmJMXServerSec.log.info("******mmUser != null******");
              Hashtable<String, List<String>> objectList = mmUser.getRoleMap();
              List<String> methodList = (List)objectList.get(strObjectName);
              boolean hasPrivilege = false;
              if (methodList != null)
              {
                MmJMXServerSec.log.info("******methodList != null******" + strObjectName);
                for (int i = 0; i < methodList.size(); i++)
                {
                  MmJMXServerSec.log.info("________" + (String)methodList.get(i) + "+++++" + strMethod);
                  if (((String)methodList.get(i)).equals(strMethod))
                  {
                    hasPrivilege = true;
                    break;
                  }
                }
              }
              else
              {
                MmJMXServerSec.log.info("******methodList == null******" + strObjectName);
                


                Set<Map.Entry<String, List<String>>> roleSet = objectList.entrySet();
                Iterator<Map.Entry<String, List<String>>> iterator = roleSet.iterator();
                while (iterator.hasNext())
                {
                  Map.Entry<String, List<String>> entry = (Map.Entry)iterator.next();
                  if (MmJMXServerSec.matchRule(strObjectName, (String)entry.getKey()))
                  {
                    List<String> methodList_ = (List)entry.getValue();
                    if (methodList_ != null) {
                      for (int i = 0; i < methodList_.size(); i++)
                      {
                        MmJMXServerSec.log.info("||||||||||||||||" + (String)methodList.get(i) + "+++++" + strMethod);
                        if (((String)methodList_.get(i)).equals(strMethod))
                        {
                          hasPrivilege = true;
                          break;
                        }
                      }
                    }
                  }
                  if (hasPrivilege) {
                    break;
                  }
                }
              }
              if (hasPrivilege) {
                try
                {
                  Object result = method.invoke(this.mbs, args);
                  
                  Notification notification = new Notification("method.invoke", identity, 0L, MmJMXServerSec.appId + "\t" + strMethod + "\t" + param + "\t" + strObjectName + "\t" + "success" + "\t" + Long.toString(Calendar.getInstance().getTimeInMillis()));
                  


                  MmJMXServerSec.getInstance().notificationHandler.sendNotification(notification);
                  return result;
                }
                catch (Exception ex)
                {
                  Notification notification = new Notification("method.invoke", identity, 0L, MmJMXServerSec.appId + "\t" + strMethod + "\t" + param + "\t" + strObjectName + "\t" + "unsuccess" + "\t" + Long.toString(Calendar.getInstance().getTimeInMillis()));
                  


                  MmJMXServerSec.getInstance().notificationHandler.sendNotification(notification);
                  throw ex;
                }
              }
              MmJMXServerSec.log.warn("No permission, access denied to method " + args[1] + " of mbean " + args[0]);
              
              throw new SecurityException("Access denied");
            }
            MmJMXServerSec.log.warn("No permission, access denied");
            throw new SecurityException("Access denied");
          }
        }
        MmJMXServerSec.log.debug(identity + MmJMXServerSec.getLogMsg(new StringBuilder().append(" invoke ").append(methodName).toString(), args));
        return method.invoke(this.mbs, args);
      }
      catch (Throwable e)
      {
        MmJMXServerSec.log.warn("more error", e);
        throw e;
      }
    }
  }
  
  public static boolean matchRule(String mbeanName, String rule)
  {
    if (rule == null) {
      return false;
    }
    rule = rule.trim();
    if ((rule.endsWith("*")) && (rule.length() > 1)) {
      return mbeanName.startsWith(rule.substring(0, rule.length() - 1));
    }
    return false;
  }
  
  public void sendNotification(Notification notification)
  {
    this.notificationHandler.sendNotification(notification);
  }
}

