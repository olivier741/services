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
package com.viettel.mmserver.authenticator;

/**
 *
 * @author olivier.tatsinkou
 */
import com.viettel.bccsgw.security.ObjectToken;
import com.viettel.bccsgw.security.VSAValidate;
import com.viettel.mmserver.agent.MMbeanServer;
import com.viettel.mmserver.base.Log;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.management.ObjectName;


public class MmAuthenticator
  implements MmAuthenticatorMBean
{
  public static final String PASSPORT_SERVICE_URL = "com.viettel.mmserver.passportServiceUrl";
  private VSAValidate vsa;
  private static MmAuthenticator mmAuthenticator;
  private final int timeOut = 30000;
  
  public static MmAuthenticator getInstance()
  {
    if (mmAuthenticator == null) {
      mmAuthenticator = new MmAuthenticator();
    }
    return mmAuthenticator;
  }
  
  private MmAuthenticator()
  {
    try
    {
      ObjectName name = new ObjectName("Tools:name=MmAuthenticator");
      MMbeanServer.getInstance().registerMBean(this, name);
      Log.info("Registered MmAuthenticator");
    }
    catch (Exception e)
    {
      Log.error("Can not register Mbean MmAuthenticator");
      Log.error(e);
    }
    Log.info("Creating VSAValidate...");
    this.vsa = new VSAValidate();
    String passportServiceUrl = System.getProperty("com.viettel.mmserver.passportServiceUrl");
    if (passportServiceUrl != null) {
      this.vsa.setCasValidateUrl(passportServiceUrl);
    }
    Log.info("Passport URL: " + this.vsa.getCasValidateUrl());
    this.vsa.setTimeOutVal(30000);
    
    Log.info("VSAValidate created!");
  }
  
  public MmUser validate(String username, String password, String appID)
  {
    Log.info("running validate");
    MmUser mmUser = null;
    this.vsa.setDomainCode(appID);
    this.vsa.setUser(username);
    this.vsa.setPassword(password);
    try
    {
      this.vsa.validate();
      Log.info("Validated");
      boolean result = this.vsa.isAuthenticationSuccesful();
      if (result)
      {
        Log.info("isAuthenticationSuccesful");
        Hashtable<String, List<String>> roleMap = new Hashtable();
        ArrayList<ObjectToken> objList = this.vsa.getUserToken().getParentMenu();
        for (Iterator parentObject = objList.iterator(); parentObject.hasNext();)
        {
          Object ob = parentObject.next();
          if ((ob instanceof ObjectToken))
          {
            LinkedList<String> temp = new LinkedList();
            ObjectToken element = (ObjectToken)ob;
            String objectName = element.getObjectName();
            ArrayList<ObjectToken> childs = element.getChildObjects();
            if (childs != null) {
              for (int i = 0; i < childs.size(); i++)
              {
                ObjectToken child = (ObjectToken)childs.get(i);
                if ("C".equalsIgnoreCase(child.getObjectType())) {
                  temp.add(child.getObjectName());
                }
              }
            }
            Log.info("$$$$$$$$$" + objectName + "............" + temp);
            roleMap.put(objectName, temp);
          }
        }
        Log.info("create mmUser");
        mmUser = new MmUser(username, password, appID, roleMap);
      }
      Log.info("isAuthenticationSuccesful");
    }
    catch (Exception e)
    {
      Log.error("Error in validate with Passport");
      Log.error(e);
      e.printStackTrace();
    }
    return mmUser;
  }
  
  private String formatCanonicalName(String name)
  {
    String formatedName = name;
    if (name == null) {
      formatedName = "";
    }
    int index = name.indexOf(":");
    if ((index != -1) && (index < name.length()))
    {
      formatedName = name.substring(0, index + 1);
      name = name.substring(index + 1);
      String[] names = name.split(",");
      if (names.length > 0)
      {
        for (int i = 0; i < names.length - 1; i++) {
          for (int j = i + 1; j < names.length; j++) {
            if (names[i].compareTo(names[j]) > 0)
            {
              String temp = names[i];
              names[i] = names[j];
              names[j] = temp;
            }
          }
        }
        for (int i = 0; i < names.length; i++) {
          formatedName = formatedName + names[i] + ",";
        }
        formatedName = formatedName.substring(0, formatedName.length() - 1);
      }
    }
    return formatedName;
  }
  
  public LinkedList<String> getAccessibleMethods(String appId, String userName, String password, String mBeanObjectName)
  {
    MmUser mmUser = validate(userName, password, appId);
    if (mmUser == null) {
      return null;
    }
    LinkedList<String> method = mmUser.getMethods(mBeanObjectName);
    if (method == null) {
      return new LinkedList();
    }
    return method;
  }
  
  public String vsaURL()
  {
    String s = "VSA's URL: ";
    if (this.vsa.getCasValidateUrl() != null) {
      s = s + this.vsa.getCasValidateUrl();
    } else {
      s = s + "Unknown";
    }
    return s;
  }
}