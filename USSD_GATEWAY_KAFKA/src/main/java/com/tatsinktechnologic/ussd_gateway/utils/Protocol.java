 package com.tatsinktechnologic.ussd_gateway.utils;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.Serializable;
 import org.apache.log4j.Logger;
 
 public class Protocol
   implements Serializable
 {
   private static final long serialVersionUID = 20091803145200L;
   protected String url;
   private String protocol;
   private String ip;
   private String server;
   private int port;
   private transient Logger logger;
   
   public Protocol(String url)
   {
     init(url);
   }
   
   private void init(String url)
   {
     this.url = url;
     this.protocol = parseProtocol();
     this.ip = parseIp();
     this.port = parsePort();
     this.server = parseServer();
   }
   
   private String parseProtocol()
   {
     if ((this.url == null) || (this.url.length() == 0)) {
       return null;
     }
     int i = this.url.indexOf("://");
     if ((i != -1) && (i < this.url.length())) {
       return this.url.substring(0, i);
     }
     return null;
   }
   
   private String parseIp()
   {
     if ((this.url == null) || (this.url.length() == 0)) {
       return null;
     }
     int s = this.url.indexOf("://");
     if ((s != -1) && (s < this.url.length()))
     {
       int e = this.url.indexOf(":", s + 1);
       if (e == -1)
       {
         e = this.url.indexOf("/", s + 3);
         if (e == -1) {
           e = this.url.length();
         }
       }
       return this.url.substring(s + 3, e);
     }
     return null;
   }
   
   private int parsePort()
   {
     if ((this.url == null) || (this.url.length() == 0)) {
       return getDefaultPort();
     }
     int s = this.url.indexOf("://");
     if ((s != -1) && (s < this.url.length()))
     {
       s = this.url.indexOf(":", s + 1);
       if ((s != -1) && (s < this.url.length()))
       {
         int e = this.url.indexOf("/", s + 1);
         String parten;
  
         if (e == -1) {
           parten = this.url.substring(s + 1);
         } else {
           parten = this.url.substring(s + 1, e);
         }
         try
         {
           return Integer.parseInt(parten);
         }
         catch (NumberFormatException ex)
         {
           if (this.logger != null) {
             this.logger.error(ex.getMessage() + "->partern:" + parten);
           }
           return getDefaultPort();
         }
       }
     }
     return getDefaultPort();
   }
   
   private String parseServer()
   {
     if ((this.url == null) || (this.url.length() == 0)) {
       return null;
     }
     int s = this.url.indexOf("://");
     if ((s != -1) && (s < this.url.length()))
     {
       s = this.url.indexOf("/", s + 3) + 1;
       if ((s != 0) && (s < this.url.length())) {
         return this.url.substring(s);
       }
     }
     return null;
   }
   
   private int getDefaultPort()
   {
     if ("ftp".equals(this.protocol)) {
       return 21;
     }
     if ("smtp".equals(this.protocol)) {
       return 25;
     }
     if ("rmi".equals(this.protocol)) {
       return 1099;
     }
     if ("eic".equals(this.protocol)) {
       return 8001;
     }
     return 80;
   }
   
   public String getProtocol()
   {
     return this.protocol;
   }
   
   public String getIp()
   {
     return this.ip;
   }
   
   public int getPort()
   {
     return this.port;
   }
   
   public String getServer()
   {
     return this.server;
   }
   
   public String getUrl()
   {
     return this.url;
   }
   
   public void setUrl(String url)
   {
     init(url);
   }
   
   public void setLogger(Logger logger)
   {
     this.logger = logger;
   }
   
   private void readObject(ObjectInputStream in)
     throws ClassNotFoundException, IOException
   {
     in.defaultReadObject();
   }
 }

