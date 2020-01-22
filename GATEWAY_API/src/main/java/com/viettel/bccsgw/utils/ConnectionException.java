package com.viettel.bccsgw.utils;
 
 import java.sql.SQLException;
 
 public class ConnectionException
   extends SQLException
 {
   protected String connUrl;
   
   public ConnectionException(String message, String connUrl)
   {
     super(message);
     this.connUrl = connUrl;
   }
   
   public String getConnUrl()
   {
     return this.connUrl;
   }
   
   public void setConnUrl(String connUrl)
   {
     this.connUrl = connUrl;
   }
 }


