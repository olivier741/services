package com.viettel.bccsgw.utils;
 
 import org.apache.log4j.Logger;
 
 public class CharacterConvert
 {
   private static Logger logger;
   public static char[][] signChars = { { 'a', 'à', 'á', 'â', 'ã', 'ă', 'ạ', 'ả', 'ấ', 'ầ', 'ẩ', 'ẫ', 'ậ', 'ắ', 'ằ', 'ẳ', 'ẵ', 'ặ' }, { 'A', 'À', 'Á', 'Â', 'Ã', 'Ă', 'Ạ', 'Ả', 'Ấ', 'Ầ', 'Ẩ', 'Ẫ', 'Ậ', 'Ắ', 'Ằ', 'Ẳ', 'Ẵ', 'Ặ' }, { 'o', 'ò', 'ó', 'ô', 'õ', 'ơ', 'ọ', 'ỏ', 'ố', 'ồ', 'ổ', 'ỗ', 'ộ', 'ờ', 'ớ', 'ở', 'ỡ', 'ợ' }, { 'O', 'Ò', 'Ó', 'Ô', 'Õ', 'Ơ', 'Ọ', 'Ỏ', 'Ố', 'Ồ', 'Ổ', 'Ỗ', 'Ộ', 'Ớ', 'Ờ', 'Ở', 'Ỡ', 'Ợ' }, { 'e', 'è', 'é', 'ê', 'ẹ', 'ẻ', 'ẽ', 'ế', 'ề', 'ể', 'ễ', 'ệ' }, { 'E', 'È', 'É', 'Ê', 'Ẹ', 'Ẻ', 'Ẽ', 'Ế', 'Ề', 'Ể', 'Ễ', 'Ệ' }, { 'u', 'ù', 'ú', 'ũ', 'ư', 'ụ', 'ủ', 'ứ', 'ừ', 'ử', 'ữ', 'ự' }, { 'U', 'Ù', 'Ú', 'Ũ', 'Ư', 'Ụ', 'Ủ', 'Ứ', 'Ừ', 'Ử', 'Ữ', 'Ự' }, { 'i', '?', 'ì', 'í', 'ĩ', 'ỉ', 'ị' }, { 'I', 'Ì', 'Í', 'Ī', 'Ỉ', 'Ị' }, { 'y', 'ý', 'ỳ', 'ỵ', 'ỷ', 'ỹ' }, { 'Y', 'ü', 'Ỳ', 'Ỵ', 'Ỷ', 'Ỹ' }, { 'd', 'đ' }, { 'D', 'Đ' } };
   
   public static final String toUnSign(String orgStr)
   {
     if ((orgStr == null) || (orgStr.length() == 0)) {
       return null;
     }
     StringBuffer buf = new StringBuffer();
     for (int i = 0; i < orgStr.length(); i++)
     {
       char c = toUnsign(orgStr.charAt(i));
       buf.append(c);
     }
     return buf.toString();
   }
   
   public static final String toSmsText(String orgStr)
   {
     if ((orgStr == null) || (orgStr.length() == 0)) {
       return "";
     }
     StringBuffer buf = new StringBuffer();
     orgStr = orgStr.trim();
     int max = orgStr.length() > 300 ? 300 : orgStr.length();
     for (int i = 0; i < max; i++)
     {
       char c = toUnsign(orgStr.charAt(i));
       if ((isSmsChar(c)) || (c == ' ')) {
         buf.append(c);
       }
     }
     return buf.toString();
   }
   
   public static final boolean isSmsChar(char c)
   {
     return ((c >= '0') && (c <= '9')) || ((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z'));
   }
   
   public static final char toUnsign(char c)
   {
     for (char[] signChar : signChars) {
       for (char aSignChar : signChar) {
         if (aSignChar == c) {
           return signChar[0];
         }
       }
     }
     return c;
   }
   
   public static final String n2aEncode(String str)
   {
     if ((str == null) || (str.length() == 0)) {
       return null;
     }
     char[] buf = str.toCharArray();
     int len = buf.length;
     StringBuffer out = new StringBuffer();
     for (int i = 0; i < len; i++) {
       if (buf[i] > '')
       {
         out.append('\\');
         out.append('u');
         String hex = Integer.toHexString(buf[i]);
         int length = 4 - hex.length();
         for (int j = 0; j < length; j++) {
           out.append('0');
         }
         out.append(hex);
       }
       else
       {
         out.append(buf[i]);
       }
     }
     return out.toString();
   }
   
   public static final String n2aDecode(String str)
   {
     if ((str == null) || (str.length() == 0)) {
       return null;
     }
     char[] buf = str.toCharArray();
     int len = buf.length;
     int index = 0;
     StringBuffer out = new StringBuffer();
     while (index < len) {
       if ((index < len - 6) && (buf[index] == '\\') && (buf[(index + 1)] == 'u'))
       {
         String s = new String(buf, index + 2, 4);
         try
         {
           char c = (char)Integer.parseInt(s, 16);
           out.append(c);
           index += 6;
         }
         catch (NumberFormatException e)
         {
           if (logger != null) {
             logger.warn(e.getMessage() + "->partern:" + s);
           }
           out.append(buf[index]);
           out.append(buf[(index + 1)]);
           index += 2;
         }
       }
       else
       {
         out.append(buf[index]);
         index++;
       }
     }
     return out.toString();
   }
   
   public static void setLogger(Logger logger)
   {
     logger = logger;
   }
 }


