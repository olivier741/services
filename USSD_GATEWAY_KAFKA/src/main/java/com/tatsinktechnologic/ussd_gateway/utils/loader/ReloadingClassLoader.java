 package com.tatsinktechnologic.ussd_gateway.utils.loader;
 
 import java.io.File;
 import java.io.InputStream;
 import java.net.URL;
 import java.security.AccessController;
 import java.security.PrivilegedAction;
 import org.apache.log4j.Logger;
 
 public class ReloadingClassLoader
   extends ReloadingClassLoaderMX
 {
   private final Logger log = Logger.getLogger("Loader");
   private ClassLoader delegate;
   private final ClassLoader parent;
   private ResourceStore stores;
   
   public ReloadingClassLoader(ClassLoader pParent, String dir)
   {
     super(pParent);
     this.parent = pParent;
     this.stores = new ResourceStore(dir);
     this.delegate = ((ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
     {
       public Object run()
       {
         return new ResourceStoreClassLoader(ReloadingClassLoader.this.parent, ReloadingClassLoader.this.stores);
       }
     }));
   }
   
   public ReloadingClassLoader(ClassLoader parent, ResourceStore stores)
   {
     super(parent);
     this.stores = stores;
     this.parent = parent;
   }
   
   public void updateClassPath()
   {
     if (this.stores.updateDir())
     {
       this.log.debug("reloading");
       this.delegate = ((ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
       {
         public Object run()
         {
           return new ResourceStoreClassLoader(ReloadingClassLoader.this.parent, ReloadingClassLoader.this.stores);
         }
       }));
     }
   }
   
   public void setBaseDir(String dir)
   {
     this.stores.setDir(dir);
   }
   
   public String getBaseDir()
   {
     return this.stores.getDir();
   }
   
   public String[] getClassPath()
   {
     ResourceStore.ClassPath[] classPath = this.stores.getClassPaths();
     String[] sClassPath = new String[classPath.length];
     for (int i = 0; i < classPath.length; i++) {
       sClassPath[i] = classPath[i].path.getAbsolutePath();
     }
     return sClassPath;
   }
   
   public boolean isResourceChanged()
   {
     return this.stores.isModified();
   }
   
   public void addClassPath(String url)
   {
     File f = new File(url);
     if (f.exists()) {
       this.stores.addClassPath(f);
     } else {
       this.log.warn("file " + url + " doesn't exist");
     }
   }
   
   public void removeClassPath(String url)
   {
     File f = new File(url);
     if (f.exists()) {
       this.stores.removeClassPath(f);
     } else {
       this.log.warn("file " + url + " doesn't exist");
     }
   }
   
   public String getInfor()
   {
     StringBuffer buff = new StringBuffer();
     buff.append("Parent:" + this.parent.getClass().getName());
     buff.append("\r\nBaseDir:" + getBaseDir());
     buff.append("\r\nClassPath");
     String[] classPaths = getClassPath();
     if (classPaths != null) {
       for (String str : classPaths) {
         buff.append("\r\n" + str);
       }
     }
     return buff.toString();
   }
   
   public void clearAssertionStatus()
   {
     this.delegate.clearAssertionStatus();
   }
   
   public URL getResource(String name)
   {
     return this.delegate.getResource(name);
   }
   
   public InputStream getResourceAsStream(String name)
   {
     return this.delegate.getResourceAsStream(name);
   }
   
   public Class loadClass(String name)
     throws ClassNotFoundException
   {
     return this.delegate.loadClass(name);
   }
   
   public void setClassAssertionStatus(String className, boolean enabled)
   {
     this.delegate.setClassAssertionStatus(className, enabled);
   }
   
   public void setDefaultAssertionStatus(boolean enabled)
   {
     this.delegate.setDefaultAssertionStatus(enabled);
   }
   
   public void setPackageAssertionStatus(String packageName, boolean enabled)
   {
     this.delegate.setPackageAssertionStatus(packageName, enabled);
   }
 }



