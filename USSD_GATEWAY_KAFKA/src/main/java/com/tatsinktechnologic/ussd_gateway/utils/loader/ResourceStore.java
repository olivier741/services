 package com.tatsinktechnologic.ussd_gateway.utils.loader;
 
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.Enumeration;
 import java.util.jar.JarEntry;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 import org.apache.log4j.Logger;
 
 public class ResourceStore
 {
   private Logger log = Logger.getLogger("Loader");
   private ClassPath[] classPaths;
   private boolean modified;
   private String dir;
   
   public ResourceStore() {}
   
   public ResourceStore(String dir)
   {
     setDir(dir);
   }
   
   private void addDir(String dir)
   {
     File f = new File(dir);
     if (!f.exists()) {
       return;
     }
     if (f.isFile())
     {
       if (isJarFile(dir)) {
         addClassPath(f);
       }
       return;
     }
     if (f.isDirectory())
     {
       File[] childs = f.listFiles();
       if ((childs == null) || (childs.length == 0)) {
         return;
       }
       for (File child : childs) {
         addDir(child.getPath());
       }
     }
   }
   
   public boolean updateDir()
   {
     if (this.dir == null) {
       return false;
     }
     this.modified = false;
     addDir(this.dir);
     return this.modified;
   }
   
   public void setDir(String dir)
   {
     removeDir();
     addDir(dir);
     this.dir = dir;
     this.modified = true;
   }
   
   public String getDir()
   {
     return this.dir;
   }
   
   public void removeDir()
   {
     this.classPaths = null;
     this.modified = true;
     this.dir = "";
   }
   
   public boolean isModified()
   {
     return this.modified;
   }
   
   public byte[] read(String pResourceName)
   {
     this.log.debug("reading resource " + pResourceName);
     return readClassData(pResourceName);
   }
   
   public void addClassPath(File classPath)
   {
     int i = indexClassPath(classPath);
     if (i == -1)
     {
       this.log.info("add classpath to resource " + classPath.getPath());
       if (this.classPaths == null)
       {
         this.classPaths = new ClassPath[] { new ClassPath(classPath, classPath.lastModified()) };
       }
       else
       {
         ClassPath[] newClassPaths = new ClassPath[this.classPaths.length + 1];
         System.arraycopy(this.classPaths, 0, newClassPaths, 0, this.classPaths.length);
         newClassPaths[this.classPaths.length] = new ClassPath(classPath, classPath.lastModified());
         this.classPaths = newClassPaths;
       }
       this.modified = true;
     }
     else if (classPath.lastModified() != this.classPaths[i].lastModified)
     {
       this.log.info("update classpath " + classPath.getPath());
       this.classPaths[i] = new ClassPath(classPath, classPath.lastModified());
       this.modified = true;
     }
   }
   
   public void removeClassPath(File classPath)
   {
     int i = indexClassPath(classPath);
     if (i != -1)
     {
       this.log.info("remove classpath from resource " + classPath.getPath());
       ClassPath[] newClassPaths = new ClassPath[this.classPaths.length - 1];
       if (i > 0) {
         System.arraycopy(this.classPaths, 0, newClassPaths, 0, i);
       }
       if (i < this.classPaths.length - 1) {
         System.arraycopy(this.classPaths, i + 1, newClassPaths, i, this.classPaths.length - i - 1);
       }
       this.classPaths = newClassPaths;
       this.modified = true;
     }
   }
   
   private int indexClassPath(File classPath)
   {
     if (this.classPaths == null) {
       return -1;
     }
     for (int i = 0; i < this.classPaths.length; i++) {
       if (this.classPaths[i].path.getAbsolutePath().equals(classPath.getAbsolutePath())) {
         return i;
       }
     }
     return -1;
   }
   
   private byte[] readClassData(String classFile)
   {
     try
     {
       String classPath = searchClassPath(classFile);
       if (classPath != null) {
         return readClassData(classPath, classFile);
       }
       if (this.log.isDebugEnabled()) {
         this.log.debug("class " + classFile + " not found in local class loader");
       }
       return new byte[0];
     }
     catch (IOException ex)
     {
       this.log.error("read file exception:" + ex);
       this.log.error(ex.getMessage(), ex);
     }
     return new byte[0];
   }
   
   private byte[] readClassData(InputStream in)
     throws IOException
   {
     byte[] b = new byte[2048];
     ByteArrayOutputStream out = new ByteArrayOutputStream();
     int len = 0;
     while ((len = in.read(b)) > 0) {
       out.write(b, 0, len);
     }
     return out.toByteArray();
   }
   
   private byte[] readClassData(String classPath, String classFile)
     throws IOException
   {
     if (isJarFile(classPath))
     {
       JarFile jar = new JarFile(classPath);
       ZipEntry entry = jar.getEntry(classFile);
       InputStream in = jar.getInputStream(entry);
       return readClassData(in);
     }
     String path = classPath + "/" + classFile;
     InputStream in = new FileInputStream(path);
     return readClassData(in);
   }
   
   private String searchClassPath(String classFile)
     throws IOException
   {
     if ((this.classPaths == null) || (this.classPaths.length == 0)) {
       return null;
     }
     String path;
     Enumeration e;
     for (ClassPath classPath : this.classPaths)
     {
       path = classPath.path.getPath();
       if (!isJarFile(path))
       {
         String fullPath = path + "/" + classFile;
         if (new File(fullPath).exists()) {
           return path;
         }
       }
       else
       {
         JarFile jar = new JarFile(path);
         for (e = jar.entries(); e.hasMoreElements();)
         {
           JarEntry entry = (JarEntry)e.nextElement();
           if (entry.getName().equals(classFile)) {
             return path;
           }
         }
       }
     }
     return null;
   }
   
   private boolean isJarFile(String path)
   {
     return (path != null) && ((path.endsWith(".jar")) || (path.endsWith(".zip")) || (path.endsWith(".JAR")) || (path.endsWith(".ZIP")));
   }
   
   public ClassPath[] getClassPaths()
   {
     if (this.classPaths == null) {
       return new ClassPath[0];
     }
     ClassPath[] c = new ClassPath[this.classPaths.length];
     System.arraycopy(this.classPaths, 0, c, 0, this.classPaths.length);
     return c;
   }
   
   static class ClassPath
   {
     File path;
     long lastModified;
     
     public ClassPath(File path, long lastModified)
     {
       this.path = path;
       this.lastModified = lastModified;
     }
     
     public String toString()
     {
       return this.path.getPath() + "(" + this.lastModified + ")";
     }
   }
 }


