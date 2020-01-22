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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class FileUtils
{
  public static void copy(File source, File dest)
    throws IOException
  {
    InputStream in = null;
    OutputStream out = null;
    try
    {
      in = new FileInputStream(source);
      out = new FileOutputStream(dest);
      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      if (out != null)
      {
        out.close();
        out = null;
      }
      if (in != null)
      {
        in.close();
        in = null;
      }
    }
  }
  
  public static void copy(String sourceFileName, String destFileName)
    throws IOException
  {
    File source = new File(sourceFileName);
    File dest = new File(destFileName);
    copy(source, dest);
  }
  
  public static void copy(String sourceFileName, File dest)
    throws IOException
  {
    File source = new File(sourceFileName);
    copy(source, dest);
  }
  
  public static void copy(File source, String destFileName)
    throws IOException
  {
    File dest = new File(destFileName);
    copy(source, dest);
  }
  
  public static void backup(String sourceFolder, String destFolder, String sourceFileName, String destFileName, String backupStyle, String formatDatetime)
  {
    try
    {
      Date currentDate = new Date();
      String strDate = DateTimeUtils.convertDateToString(currentDate, formatDatetime);
      Date dt = DateTimeUtils.convertStringToTime(strDate, formatDatetime);
      if ((backupStyle != null) && (!backupStyle.equals("")) && (!backupStyle.equals("Directly")))
      {
        if (backupStyle.equals("Monthly")) {
          formatDatetime = "yyyyMM";
        } else if (backupStyle.equals("Yearly")) {
          formatDatetime = "yyyy";
        }
        String processDate = DateTimeUtils.convertDateToString(dt, formatDatetime);
        destFolder = destFolder + processDate + File.separatorChar;
      }
      File fileBackup = new File(destFolder);
      if ((fileBackup != null) && (!fileBackup.isDirectory())) {
        fileBackup.mkdirs();
      }
      copy(sourceFolder + sourceFileName, destFolder + destFileName);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}

