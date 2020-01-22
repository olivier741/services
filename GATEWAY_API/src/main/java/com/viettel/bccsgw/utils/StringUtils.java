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
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils
{
  public static boolean validateIP(String ip, String allowIP)
  {
    String[] listIP = allowIP.split("\\|");
    Pattern singleIp = Pattern.compile("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$");
    Pattern rangeDIp = Pattern.compile("^(?:[0-9]{1,3}\\.){3}\\*$");
    for (int i = 0; i < listIP.length; i++) {
      if (singleIp.matcher(listIP[i]).matches())
      {
        if (ip.equals(listIP[i])) {
          return true;
        }
      }
      else if ((rangeDIp.matcher(listIP[i]).matches()) && 
        (ip.substring(0, ip.lastIndexOf(".")).equals(listIP[i].substring(0, listIP[i].lastIndexOf("."))))) {
        return true;
      }
    }
    return false;
  }
  
  public static String getTraceException(Exception ex)
  {
    if (ex == null) {
      return "";
    }
    StringBuffer strReturn = new StringBuffer();
    strReturn.append(ex.toString());
    strReturn.append("\n");
    StackTraceElement[] element = ex.getStackTrace();
    if ((element != null) && (element.length > 0)) {
      for (int i = 0; i < element.length; i++)
      {
        strReturn.append("\t");
        String className = element[i].getClassName();
        strReturn.append("at ");
        if ((className != null) && (!className.equals("")))
        {
          strReturn.append(className);
          strReturn.append(".");
        }
        String methodName = element[i].getMethodName();
        if ((methodName != null) && (!methodName.equals(""))) {
          strReturn.append(methodName);
        }
        String fileName = element[i].getFileName();
        if ((fileName != null) && (!fileName.equals("")))
        {
          strReturn.append("(");
          strReturn.append(fileName);
          int lineNumber = element[i].getLineNumber();
          strReturn.append(":");
          strReturn.append(lineNumber);
          strReturn.append(")");
        }
        strReturn.append("\n");
      }
    }
    return strReturn.toString();
  }
  
  public static Vector toStringVetor(String str, String regex)
  {
    Vector vtReturn = new Vector();
    if ((str != null) && (!str.equals("")))
    {
      String[] array = str.split(regex);
      for (int i = 0; i < array.length; i++) {
        vtReturn.add(array[i]);
      }
    }
    return vtReturn;
  }
}
