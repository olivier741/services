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
import com.viettel.bccsgw.bo.AccessManegement;
import com.viettel.bccsgw.bo.Client;
import com.viettel.bccsgw.bo.ErrorCode;
import com.viettel.bccsgw.bo.Webservice;
import com.viettel.bccsgw.dao.DataGetter;
import com.viettel.bccsgw.dao.DatabaseConnection;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MemoryDataGateway
{
  private static Map<String, Client> clientMap;
  private static Map<String, Webservice> webserviceMap;
  private static Map<String, AccessManegement> accessManagementMap;
  private static Map<String, String> errorCodeMap;
  
  public static void loadDataIntoMemory()
    throws Exception
  {
    long time = System.currentTimeMillis();
    System.out.println("[BCCSGW]: Start load data into memory");
    Connection cn = null;
    if ((clientMap == null) || (webserviceMap == null) || (accessManagementMap == null))
    {
      System.out.println("[BCCSGW]: Open connection to database");
      cn = DatabaseConnection.openConnection();
    }
    if ((clientMap == null) || (clientMap.size() < 1)) {
      loadClient(cn);
    }
    if ((webserviceMap == null) || (webserviceMap.size() < 1)) {
      loadWebservice(cn);
    }
    if ((accessManagementMap == null) || (accessManagementMap.size() < 1)) {
      loadAccessManagement(cn);
    }
    DatabaseConnection.closeConnection(cn, null, null);
    System.out.println("[BCCSGW]: Closed connection to database");
    System.out.println("[BCCSGW]: finish loaded data into memory, total process time: " + (System.currentTimeMillis() - time));
  }
  
  public static void loadClient(Connection cn)
    throws Exception
  {
    long time = System.currentTimeMillis();
    System.out.println("[BCCSGW]: start load data client");
    boolean isCloseConn = false;
    if (cn == null)
    {
      isCloseConn = true;
      cn = DatabaseConnection.openConnection();
    }
    clientMap = new LinkedHashMap();
    ArrayList<Client> lstClient = DataGetter.getAllClient(cn);
    if ((lstClient != null) && (lstClient.size() > 0)) {
      for (int i = 0; i < lstClient.size(); i++)
      {
        Client client = (Client)lstClient.get(i);
        if (clientMap.get(client.getUsername()) == null) {
          clientMap.put(client.getUsername().toLowerCase(), client);
        }
      }
    }
    if ((isCloseConn) && (cn != null)) {
      cn.close();
    }
    System.out.println("[BCCSGW]: finish load data client, total process time: " + (System.currentTimeMillis() - time));
  }
  
  public static void loadWebservice(Connection cn)
    throws Exception
  {
    long time = System.currentTimeMillis();
    System.out.println("[BCCSGW]: start load data webservice");
    boolean isCloseConn = false;
    if (cn == null)
    {
      isCloseConn = true;
      cn = DatabaseConnection.openConnection();
    }
    webserviceMap = new LinkedHashMap();
    ArrayList<Webservice> lstWebservice = DataGetter.getAllWebservice(cn);
    if ((lstWebservice != null) && (lstWebservice.size() > 0)) {
      for (int i = 0; i < lstWebservice.size(); i++)
      {
        Webservice webservice = (Webservice)lstWebservice.get(i);
        if (webserviceMap.get(webservice.getWsCode()) == null) {
          webserviceMap.put(webservice.getWsCode().toLowerCase(), webservice);
        }
      }
    }
    if ((isCloseConn) && (cn != null)) {
      cn.close();
    }
    System.out.println("[BCCSGW]: finish load data webservice, total process time: " + (System.currentTimeMillis() - time));
  }
  
  public static void loadAccessManagement(Connection cn)
    throws Exception
  {
    long time = System.currentTimeMillis();
    System.out.println("[BCCSGW]: start load data AccessManagement");
    boolean isCloseConn = false;
    if (cn == null)
    {
      isCloseConn = true;
      cn = DatabaseConnection.openConnection();
    }
    accessManagementMap = new LinkedHashMap();
    ArrayList<AccessManegement> lstAccessManegement = DataGetter.getAllAccessManegements(cn);
    if ((lstAccessManegement != null) && (lstAccessManegement.size() > 0)) {
      for (int i = 0; i < lstAccessManegement.size(); i++)
      {
        AccessManegement accessManegement = (AccessManegement)lstAccessManegement.get(i);
        if (accessManegement.getClient() != null) {
          if (accessManegement.getWebservice() != null) {
            if (accessManagementMap.get(accessManegement.getClient().getUsername().toLowerCase() + "-" + accessManegement.getWebservice().getWsCode().toLowerCase()) == null) {
              accessManagementMap.put(accessManegement.getClient().getUsername().toLowerCase() + "-" + accessManegement.getWebservice().getWsCode().toLowerCase(), accessManegement);
            }
          }
        }
      }
    }
    if ((isCloseConn) && (cn != null)) {
      cn.close();
    }
    System.out.println("[BCCSGW]: finish load data webservice, total process time: " + (System.currentTimeMillis() - time));
  }
  
  public static void loadErrorCode(Connection cn)
    throws Exception
  {
    long time = System.currentTimeMillis();
    System.out.println("[BCCSGW]: start load data errorCode");
    boolean isCloseConn = false;
    if (cn == null)
    {
      isCloseConn = true;
      cn = DatabaseConnection.openConnection();
    }
    errorCodeMap = new LinkedHashMap();
    List<ErrorCode> lstErrorCode = DataGetter.getAllErrorCode(cn);
    if ((lstErrorCode != null) && (lstErrorCode.size() > 0)) {
      for (int i = 0; i < lstErrorCode.size(); i++)
      {
        ErrorCode errorCode = (ErrorCode)lstErrorCode.get(i);
        if (errorCodeMap.get(errorCode.getErrorCode()) == null) {
          errorCodeMap.put(errorCode.getErrorCode().toLowerCase(), errorCode.getDescription());
        }
      }
    }
    if ((isCloseConn) && (cn != null)) {
      cn.close();
    }
    System.out.println("[BCCSGW]: finish load data errorCode, total process time: " + (System.currentTimeMillis() - time));
  }
  
  public static synchronized Map<String, Client> getCientMap()
    throws Exception
  {
    if (clientMap == null)
    {
      System.out.println("[BCCSGW]: Client is null: Reload client");
      loadClient(null);
    }
    return clientMap;
  }
  
  public static synchronized Map<String, Webservice> getWebserviceMap()
    throws Exception
  {
    if (webserviceMap == null)
    {
      System.out.println("[BCCSGW]: Webservice is null: Reload Webservice");
      loadWebservice(null);
    }
    return webserviceMap;
  }
  
  public static synchronized Map<String, AccessManegement> getAccessManagementMap()
    throws Exception
  {
    if (accessManagementMap == null)
    {
      System.out.println("[BCCSGW]: AccessManagement is null: Reload AccessManagement");
      loadAccessManagement(null);
    }
    return accessManagementMap;
  }
  
  public static synchronized Map<String, String> getErrorCodeMap()
  {
    try
    {
      if (errorCodeMap == null)
      {
        System.out.println("[BCCSGW]: ErrorCode is null: Reload ErrorCode");
        loadErrorCode(null);
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return errorCodeMap;
  }
  
  public static synchronized void reloadDataMemory()
    throws Exception
  {
    System.out.println("[BCCSGW]: reload database in memory");
    Connection cn = DatabaseConnection.openConnection();
    if (cn != null)
    {
      loadClient(cn);
      loadWebservice(cn);
      loadAccessManagement(cn);
      loadErrorCode(cn);
      DatabaseConnection.closeConnection(cn, null, null);
    }
    System.out.println("[BCCSGW]: finish reload database in memory");
  }
}
