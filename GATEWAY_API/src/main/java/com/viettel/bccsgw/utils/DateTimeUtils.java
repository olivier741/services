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
package com.viettel.bccsgw.utils;

/**
 *
 * @author olivier.tatsinkou
 */
import java.io.PrintStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils
{
  public static long minusDate(Date date1, Date date2)
  {
    return date1.getTime() - date2.getTime();
  }
  
  public static String date2MMyyString(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat date = new SimpleDateFormat("MM/yyyy");
      return date.format(value);
    }
    return "";
  }
  
  public static String date2ddMMyyNoSlashString(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat date = new SimpleDateFormat("ddMMyyyy");
      return date.format(value);
    }
    return "";
  }
  
  public static String date2yyyyMMddHHString(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHH");
      return date.format(value);
    }
    return "";
  }
  
  public static Date stringYYYYmmDDhhMMssToDate(String value)
  {
    try
    {
      SimpleDateFormat dbUpdateDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      return dbUpdateDateTime.parse(value);
    }
    catch (ParseException ex)
    {
      ex.printStackTrace();
    }
    return null;
  }
  
  public static String date2String(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
      return date.format(value);
    }
    return "";
  }
  
  public static String date2StringNoSlash(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat dateNoSlash = new SimpleDateFormat("yyyyMMdd");
      return dateNoSlash.format(value);
    }
    return "";
  }
  
  public static String dateH2StringNoSlash(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat dateNoSlash = new SimpleDateFormat("yyyyMMddHH");
      return dateNoSlash.format(value);
    }
    return "";
  }
  
  public static String dateTime2StringNoSlash(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat dateTimeNoSlash = new SimpleDateFormat("yyyyMMddHHmmss");
      
      return dateTimeNoSlash.format(value);
    }
    return "";
  }
  
  public static String dateTime2String(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat dateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      
      return dateTime.format(value);
    }
    return "";
  }
  
  public static String dbUpdateDateTime2String(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat dbUpdateDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      
      return dbUpdateDateTime.format(value);
    }
    return "";
  }
  
  public static Timestamp date2Timestamp(Date value)
  {
    if (value != null) {
      return new Timestamp(value.getTime());
    }
    return null;
  }
  
  public static Date sysDate()
  {
    return new Date();
  }
  
  public static Date sysdateYmd()
  {
    return nextdate(0);
  }
  
  public static Date nextdate(int day)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.set(calendar.get(1), calendar.get(2), calendar.get(5) + day, 0, 0, 0);
    



    calendar.clear(14);
    return calendar.getTime();
  }
  
  public static Date nextdate(Date date, int day)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(calendar.get(1), calendar.get(2), calendar.get(5) + day, 0, 0, 0);
    



    calendar.clear(14);
    return calendar.getTime();
  }
  
  public static Date nextMonth(Date date, int month)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(calendar.get(1), calendar.get(2) + month, calendar.get(5), 0, 0, 0);
    





    calendar.clear(14);
    return calendar.getTime();
  }
  
  public static Date getPreMonthDate(Date date, int month)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(calendar.get(1), calendar.get(2) - month, calendar.get(5), 0, 0, 0);
    





    calendar.clear(14);
    return calendar.getTime();
  }
  
  public static String sysdateString()
  {
    SimpleDateFormat dbUpdateDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    return dbUpdateDateTime.format(new Date());
  }
  
  public static SimpleDateFormat getDate()
  {
    SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
    return date;
  }
  
  public static SimpleDateFormat getDateTime()
  {
    SimpleDateFormat dateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    return dateTime;
  }
  
  public static SimpleDateFormat getDateTimeMinute()
  {
    SimpleDateFormat dateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    return dateTime;
  }
  
  public static String timestampToStringFF(Timestamp date)
  {
    if (date != null)
    {
      SimpleDateFormat dbDateTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
      
      return dbDateTimeString.format(date);
    }
    return "";
  }
  
  public static SimpleDateFormat getDbUpdateDateTime()
  {
    SimpleDateFormat dbUpdateDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    return dbUpdateDateTime;
  }
  
  public static SimpleDateFormat getYYYYMM()
  {
    SimpleDateFormat yyyymm = new SimpleDateFormat("yyyyMM");
    return yyyymm;
  }
  
  public static SimpleDateFormat getMMdd()
  {
    SimpleDateFormat mmdd = new SimpleDateFormat("MM/dd");
    return mmdd;
  }
  
  public static String date2ddMMyyyyString(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat ddMMyyyy = new SimpleDateFormat("dd/MM/yyyy");
      return ddMMyyyy.format(value);
    }
    return "";
  }
  
  public static String date2MMyyyyString(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat yyyyMM = new SimpleDateFormat("MM/yyyy");
      return yyyyMM.format(value);
    }
    return "";
  }
  
  public static String date2yyMMddString(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat yyMMdd = new SimpleDateFormat("yy/MM/dd");
      return yyMMdd.format(value);
    }
    return "";
  }
  
  public static String date2yyMMddStringNoSlash(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat yyMMdd = new SimpleDateFormat("yyMMdd");
      return yyMMdd.format(value);
    }
    return "";
  }
  
  public static String date2yyyyMMStringNoSlash(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat yyyymm = new SimpleDateFormat("yyyyMM");
      return yyyymm.format(value);
    }
    return "";
  }
  
  public static String date2yyMMStringNoSlash(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat yymm = new SimpleDateFormat("yyMM");
      return yymm.format(value);
    }
    return "";
  }
  
  public static String date2MMddString(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat mmdd = new SimpleDateFormat("MM/dd");
      return mmdd.format(value);
    }
    return "";
  }
  
  public static String second2String(Date value)
  {
    if (value != null) {
      return SimpleDateFormat.getTimeInstance(2).format(value);
    }
    return "";
  }
  
  public static String[] getSplitDate(Date value)
  {
    if (value != null)
    {
      DecimalFormat df = new DecimalFormat("00");
      String[] dateTime = dateTime2String(value).split(" ");
      String[] date = new String[6];
      String[] tmpDate = dateTime[0].split("/");
      date[0] = df.format(Integer.parseInt(tmpDate[0]));
      date[1] = df.format(Integer.parseInt(tmpDate[1]));
      date[2] = df.format(Integer.parseInt(tmpDate[2]));
      tmpDate = dateTime[1].split(":");
      date[3] = df.format(Integer.parseInt(tmpDate[0]));
      date[4] = df.format(Integer.parseInt(tmpDate[1]));
      date[5] = df.format(Integer.parseInt(tmpDate[2]));
      return date;
    }
    return new String[6];
  }
  
  public static String date2MMddTime(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat mmdd = new SimpleDateFormat("MM/dd HH:mm:ss");
      return mmdd.format(value);
    }
    return "";
  }
  
  public static String date2YYYYMMddTime(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      return yyyyMMdd.format(value);
    }
    return "";
  }
  
  public static String date2HHMMssNoSlash(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat dateTimeNoSlash = new SimpleDateFormat("HHmmss");
      return dateTimeNoSlash.format(value);
    }
    return "";
  }
  
  public static String date2ddMMyyyyHHMMssNoSlash(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat dateTimeNoSlash = new SimpleDateFormat("ddMMyyyyHHmmss");
      return dateTimeNoSlash.format(value);
    }
    return "";
  }
  
  public static String date2ddMMyyyyHHMMss(Date value)
  {
    if (value != null)
    {
      SimpleDateFormat dateTimeNoSlash = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
      return dateTimeNoSlash.format(value);
    }
    return "";
  }
  
  public static Timestamp nowDateMilli()
  {
    return new Timestamp(sysDate().getTime());
  }
  
  public static int getYY(Date date)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(1) % 100;
  }
  
  public static int getMonth(Date nowDate)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(nowDate);
    return calendar.get(2) + 1;
  }
  
  public static int getDay(Date nowDate)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(nowDate);
    return calendar.get(7);
  }
  
  public static Timestamp addMilli(Timestamp nowDate, int period)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(nowDate);
    calendar.add(14, period);
    
    Timestamp stopTerm = date2Timestamp(calendar.getTime());
    
    return stopTerm;
  }
  
  public static Date addMinute(Date nowDate, int period)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(nowDate);
    calendar.add(12, period);
    
    return calendar.getTime();
  }
  
  public static String convertDateToString(Date date, String pattern)
  {
    SimpleDateFormat formatter = new SimpleDateFormat(pattern);
    return formatter.format(date);
  }
  
  public static boolean after(Date firstDate, Date secondDate)
  {
    if (firstDate == null) {
      return false;
    }
    if (secondDate == null) {
      return true;
    }
    Calendar fisrtCal = Calendar.getInstance();
    Calendar secondCal = Calendar.getInstance();
    fisrtCal.setTime(firstDate);
    secondCal.setTime(secondDate);
    return fisrtCal.after(secondCal);
  }
  
  public static Date convertStringToTime(String date, String pattern)
  {
    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
    try
    {
      return dateFormat.parse(date);
    }
    catch (Exception e)
    {
      System.out.println("Date ParseException, string value:" + date);
    }
    return null;
  }
}