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
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

public class ResourceBundleUtils
{
  private static ResourceBundle rb = null;
  
  public static String getResource(String key)
  {
    if (rb == null) {
      rb = ResourceBundle.getBundle("bccsgw");
    }
    return rb.getString(key);
  }
  
  public static Map<String, String> responseCodeSuccess = null;
  private static Integer timeout;
  private static String useVsa;
  
  public static int getLoginTimeout()
  {
    if ((timeout != null) && (timeout.intValue() > 0)) {
      return timeout.intValue();
    }
    try
    {
      timeout = Integer.valueOf(Integer.parseInt(getResource("loginTimeout")));
    }
    catch (Exception ex)
    {
      timeout = Integer.valueOf(1800);
    }
    return timeout.intValue();
  }
  
  public static String getUseVsa()
  {
    if (useVsa != null) {
      return useVsa;
    }
    try
    {
      useVsa = getResource("isUseVsa");
    }
    catch (Exception ex)
    {
      useVsa = "yes";
    }
    return useVsa;
  }
  
  public static Map getResCodeSuccess()
  {
    if (responseCodeSuccess == null)
    {
      responseCodeSuccess = new HashMap();
      String str = getResource("RES_CODE_SUCCESS");
      Vector vt = StringUtils.toStringVetor(str, ",");
      if ((vt != null) && (vt.size() > 0)) {
        for (int i = 0; i < vt.size(); i++)
        {
          String response = (String)vt.get(i);
          responseCodeSuccess.put(response, response);
        }
      }
    }
    return responseCodeSuccess;
  }
}
