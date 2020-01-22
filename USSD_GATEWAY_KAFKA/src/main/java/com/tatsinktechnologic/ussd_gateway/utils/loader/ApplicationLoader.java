package com.tatsinktechnologic.ussd_gateway.utils.loader;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import org.apache.log4j.Logger;

public class ApplicationLoader
  extends ApplicationLoaderMX
{
  private static final String DIR = "../module/";
  private String dir;
  private Map<String, ReloadingClassLoader> classLoaders;
  
  public ApplicationLoader()
  {
    this("../module/");
  }
  
  public ApplicationLoader(String dir)
  {
    this.classLoaders = new HashMap();
    this.dir = dir;
    try
    {
      String mbeansName = "loader:type=Loaders";
      this.logger.info("register MBean:" + mbeansName);
      registerAgent(mbeansName);
    }
    catch (MalformedObjectNameException ex)
    {
      this.logger.error("register MBeanServer exception:" + ex);
      this.logger.error(ex.getMessage(), ex);
    }
    catch (InstanceAlreadyExistsException ex)
    {
      this.logger.error("register MBeanServer exception:" + ex);
      this.logger.error(ex.getMessage(), ex);
    }
    catch (MBeanRegistrationException ex)
    {
      this.logger.error("register MBeanServer exception:" + ex);
      this.logger.error(ex.getMessage(), ex);
    }
    catch (NotCompliantMBeanException ex)
    {
      this.logger.error("register MBeanServer exception:" + ex);
      this.logger.error(ex.getMessage(), ex);
    }
  }
  
  public void destroy()
  {
    try
    {
      String mbeansName = "loader:type=Loaders";
      this.logger.info("register MBean:" + mbeansName);
      unregisterAgent(mbeansName);
    }
    catch (MalformedObjectNameException ex)
    {
      this.logger.error("register MBeanServer exception:" + ex);
      this.logger.error(ex.getMessage(), ex);
    }
    catch (InstanceNotFoundException ex)
    {
      this.logger.error("register MBeanServer exception:" + ex);
      this.logger.error(ex.getMessage(), ex);
    }
    catch (MBeanRegistrationException ex)
    {
      this.logger.error("register MBeanServer exception:" + ex);
      this.logger.error(ex.getMessage(), ex);
    }
  }
  
  public ReloadingClassLoader addLoader(String appId)
  {
    if (this.classLoaders.containsKey(appId))
    {
      this.logger.info("loadder [" + appId + "] already added");
      return (ReloadingClassLoader)this.classLoaders.get(appId);
    }
    String appDir = this.dir + appId;
    ReloadingClassLoader loader = (ReloadingClassLoader)AccessController.doPrivileged(new LoaderPrivilegedAction(appDir));
    

    this.classLoaders.put(appId, loader);
    try
    {
      String mbeansName = "loader:type=Loaders,name=" + appId;
      this.logger.info("register MBean:" + mbeansName);
      loader.registerAgent(mbeansName);
    }
    catch (MalformedObjectNameException ex)
    {
      this.logger.error("register MBeanServer exception.", ex);
    }
    catch (InstanceAlreadyExistsException ex)
    {
      this.logger.error("register MBeanServer exception.", ex);
    }
    catch (MBeanRegistrationException ex)
    {
      this.logger.error("register MBeanServer exception.", ex);
    }
    catch (NotCompliantMBeanException ex)
    {
      this.logger.error("register MBeanServer exception.", ex);
    }
    return loader;
  }
  
  public void removeLoader(String appId)
  {
    if (this.classLoaders.containsKey(appId))
    {
      ReloadingClassLoader loader = (ReloadingClassLoader)this.classLoaders.get(appId);
      try
      {
        String mbeansName = "loader:type=Loaders,name=" + appId;
        this.logger.info("unregister MBean:" + mbeansName);
        loader.unregisterAgent(mbeansName);
      }
      catch (MalformedObjectNameException ex)
      {
        this.logger.error("register MBeanServer exception:" + ex);
        this.logger.error(ex.getMessage(), ex);
      }
      catch (InstanceNotFoundException ex)
      {
        this.logger.error("register MBeanServer exception:" + ex);
        this.logger.error(ex.getMessage(), ex);
      }
      catch (MBeanRegistrationException ex)
      {
        this.logger.error("register MBeanServer exception:" + ex);
        this.logger.error(ex.getMessage(), ex);
      }
      this.classLoaders.remove(appId);
    }
  }
  
  public ReloadingClassLoader getLoader(String appId)
  {
    if (this.classLoaders.containsKey(appId)) {
      return (ReloadingClassLoader)this.classLoaders.get(appId);
    }
    return addLoader(appId);
  }
  
  public void updateLoader()
  {
    for (ReloadingClassLoader loader : this.classLoaders.values()) {
      loader.updateClassPath();
    }
  }
  
  public String getInfor()
  {
    StringBuffer buff = new StringBuffer();
    buff.append("Loaders=" + this.classLoaders.size());
    buff.append("\r\nDirectory=" + this.dir);
    return buff.toString();
  }
  
  static class LoaderPrivilegedAction
    implements PrivilegedAction
  {
    private String appDir;
    
    public LoaderPrivilegedAction(String appDir)
    {
      this.appDir = appDir;
    }
    
    public Object run()
    {
      return new ReloadingClassLoader(ClassLoader.getSystemClassLoader(), this.appDir);
    }
  }
}
