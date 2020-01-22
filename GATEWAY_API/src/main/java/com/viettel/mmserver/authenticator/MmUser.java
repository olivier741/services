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
import com.viettel.mmserver.agent.MmJMXServerSec;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MmUser
  implements Serializable
{
  private final String username;
  private final String password;
  private final String appID;
  private final Hashtable<String, List<String>> roleMap;
  private static final String CRLF = "\r\n";
  
  public MmUser(String username, String password, String appID, Hashtable<String, List<String>> roleMap)
  {
    this.username = username;
    this.password = password;
    this.appID = appID;
    this.roleMap = roleMap;
  }
  
  public String getPassword()
  {
    return this.password;
  }
  
  public Hashtable<String, List<String>> getRoleMap()
  {
    return this.roleMap;
  }
  
  public String getUsername()
  {
    return this.username;
  }
  
  public String getAppID()
  {
    return this.appID;
  }
  
  public String toString()
  {
    return toString("");
  }
  
  public String toString(String indent)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(indent + "<user name=" + getUsername() + "\r\n");
    sb.append(indent + "<password>" + getPassword() + "</password>" + "\r\n");
    sb.append(indent + "</user>");
    return sb.toString();
  }
  
  public LinkedList<String> getMethods(String objectName)
  {
    LinkedList method = (LinkedList)this.roleMap.get(objectName);
    if (method != null) {
      return method;
    }
    Set<Map.Entry<String, List<String>>> entrySet = this.roleMap.entrySet();
    Iterator<Map.Entry<String, List<String>>> i = entrySet.iterator();
    while (i.hasNext())
    {
      Map.Entry<String, List<String>> entry = (Map.Entry)i.next();
      if (MmJMXServerSec.matchRule(objectName, (String)entry.getKey())) {
        return (LinkedList)entry.getValue();
      }
    }
    return null;
  }
}

