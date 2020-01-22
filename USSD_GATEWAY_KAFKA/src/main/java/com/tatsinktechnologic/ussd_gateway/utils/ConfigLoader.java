 package com.tatsinktechnologic.ussd_gateway.utils;
 
 import java.io.BufferedReader;
 import java.io.IOException;
 import org.apache.log4j.Logger;
 
 public class ConfigLoader
 {
   protected Logger logger;
   private static Logger slogger;
   
   public ConfigLoader(String name)
   {
     this.logger = Logger.getLogger(name);
   }
   
   public static int getValue(String s, int defaultValue)
   {
     String value = getValue(s, "=");
     return getInt(value, defaultValue);
   }
   
   public static String getValue(String s, String matcher)
   {
     int i = s.indexOf(matcher);
     if (i < 0) {
       return null;
     }
     s = s.substring(i + 1).trim();
     return format(s);
   }
   
   public static String format(String s)
   {
     s = s.trim();
     StringBuffer buf = new StringBuffer();
     for (int i = 0; i < s.length(); i++)
     {
       char c = s.charAt(i);
       if (c == '\\')
       {
         if ((i < s.length() - 1) && (s.charAt(i + 1) == 't') && ((i == 0) || ((i > 0) && (s.charAt(i - 1) != '\\'))))
         {
           buf.append('\t');
           i++;
         }
         else if ((i < s.length() - 1) && (s.charAt(i + 1) == 'r') && ((i == 0) || ((i > 0) && (s.charAt(i - 1) != '\\'))))
         {
           buf.append('\r');
           i++;
         }
         else if ((i < s.length() - 1) && (s.charAt(i + 1) == 'n') && ((i == 0) || ((i > 0) && (s.charAt(i - 1) != '\\'))))
         {
           buf.append('\n');
           i++;
         }
         else
         {
           buf.append(c);
         }
       }
       else {
         buf.append(c);
       }
     }
     return buf.toString();
   }
   
   public static boolean getBoolean(String str, boolean defaultValue)
   {
     if (str == null) {
       return defaultValue;
     }
     return "true".equalsIgnoreCase(str);
   }
   
   public static String getValue(String input, String key, String defaultValue)
   {
     String output = defaultValue;
     if ((input == null) || (input.length() == 0)) {
       return output;
     }
     int inputLen = input.length();
     int keyLen = key.length();
     String iKey = key + " ";
     int from = input.indexOf(iKey);
     if (from < 0) {
       return output;
     }
     int to = input.indexOf("-", from + 1);
     int dest;
     if (to < 0) {
       dest = inputLen;
     } else {
       dest = to;
     }
     if (from + keyLen > dest) {
       return output;
     }
     String value1 = input.substring(from + keyLen, dest);
     String value2 = value1.trim();
     if ((value2 == null) || (value2.length() == 0)) {
       return output;
     }
     output = value2;
     return output.trim();
   }
   
   public static int getInt(String key, int defaultValue)
   {
     int i;
     try
     {
       i = Integer.decode(key).intValue();
     }
     catch (NumberFormatException ex)
     {
       i = defaultValue;
       if (slogger != null) {
         slogger.error(ex.getMessage() + "-> parten:" + key);
       }
     }
     return i;
   }
   
   public static byte getByte(String s, byte defaultValue)
   {
     byte i;
     try
     {
       i = Byte.decode(s).byteValue();
     }
     catch (NumberFormatException ex)
     {
       i = defaultValue;
       if (slogger != null) {
         slogger.error(ex.getMessage() + "-> parten:" + s);
       }
     }
     return i;
   }
   
   public static String read(BufferedReader r)
     throws IOException
   {
     String str = r.readLine();
     if (str != null) {
       str = str.trim();
     }
     return str;
   }
   
   public static final String add(String[] listStr)
   {
     return add(listStr, ",");
   }
   
   public static final String add(String[] listStr, String separator)
   {
     StringBuffer buff = new StringBuffer();
     if ((listStr != null) && (listStr.length > 0)) {
       for (int i = 0; i < listStr.length; i++)
       {
         if (i > 0) {
           buff.append(separator);
         }
         buff.append(listStr[i]);
       }
     }
     return buff.toString();
   }
   
   public static final String join(String[] listStr, String separator)
   {
     return add(listStr, separator);
   }
   
   public void setLogger(Logger logger)
   {
     this.logger = logger;
   }
   
   public static void setSlogger(Logger slogger)
   {
     slogger = slogger;
   }
 }



