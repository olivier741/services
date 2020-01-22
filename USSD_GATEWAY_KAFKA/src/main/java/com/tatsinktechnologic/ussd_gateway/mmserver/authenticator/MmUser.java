/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.authenticator;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.mmserver.agent.MmJMXServerSec;
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