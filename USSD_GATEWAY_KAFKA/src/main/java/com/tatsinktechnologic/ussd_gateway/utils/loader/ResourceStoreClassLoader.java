 package com.tatsinktechnologic.ussd_gateway.utils.loader;
 
 import org.apache.log4j.Logger;
 
 public class ResourceStoreClassLoader
   extends ClassLoader
 {
   private Logger log = Logger.getLogger("Loader");
   private ResourceStore stores;
   
   public ResourceStoreClassLoader(ClassLoader pParent, ResourceStore pStores)
   {
     super(pParent);
     this.stores = pStores;
   }
   
   private Class fastFindClass(String name)
   {
     if (this.stores != null)
     {
       byte[] clazzBytes = this.stores.read(convertClassToResourcePath(name));
       if (clazzBytes != null)
       {
         this.log.debug(getId() + " found class: " + name + " (" + clazzBytes.length + " bytes)");
         return defineClass(name, clazzBytes, 0, clazzBytes.length);
       }
     }
     return null;
   }
   
   protected synchronized Class loadClass(String name, boolean resolve)
     throws ClassNotFoundException
   {
     this.log.debug(getId() + " looking for: " + name);
     Class clazz = findLoadedClass(name);
     if (clazz == null)
     {
       ClassLoader parent = getParent();
       if (parent != null) {
         try
         {
           this.log.debug(getId() + " delegating loading to parent");
           this.log.debug(parent.getClass().getName() + " loading class " + name);
           
           clazz = parent.loadClass(name);
           this.log.debug("loaded class " + name + " from " + parent.getClass().getName());
         }
         catch (ClassNotFoundException ex) {}
       }
       if (clazz == null)
       {
         clazz = fastFindClass(name);
         if (resolve) {
           resolveClass(clazz);
         }
         if (clazz == null) {
           throw new ClassNotFoundException(name);
         }
         if (this.log.isDebugEnabled()) {
           this.log.debug(getId() + " loaded from store: " + name);
         }
       }
     }
     return clazz;
   }
   
   protected Class findClass(String name)
     throws ClassNotFoundException
   {
     Class clazz = fastFindClass(name);
     if (clazz == null) {
       throw new ClassNotFoundException(name);
     }
     return clazz;
   }
   
   private String getId()
   {
     return getClass().getName() + "[" + Thread.currentThread().getContextClassLoader().getClass().getName() + "]";
   }
   
   public static String convertClassToResourcePath(String pName)
   {
     return pName.replace('.', '/') + ".class";
   }
 }



