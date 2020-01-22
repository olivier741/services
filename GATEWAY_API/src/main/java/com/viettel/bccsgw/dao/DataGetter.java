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
package com.viettel.bccsgw.dao;

/**
 *
 * @author olivier.tatsinkou
 */
import com.viettel.bccsgw.bo.AccessManegement;
import com.viettel.bccsgw.bo.Client;
import com.viettel.bccsgw.bo.ErrorCode;
import com.viettel.bccsgw.bo.Webservice;
import com.viettel.bccsgw.utils.ResourceBundleUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DataGetter
{
  public static ArrayList<Client> getAllClient(Connection cn)
    throws Exception
  {
    ArrayList<Client> result = null;
    Client clientBO = null;
    PreparedStatement prstmt = null;
    ResultSet rs = null;
    try
    {
      if (cn.isClosed()) {
        cn = null;
      }
      if (cn == null) {
        cn = DatabaseConnection.openConnection();
      }
      String sql = "select * from " + ResourceBundleUtils.getResource("schema") + ".client where status = ?";
      prstmt = cn.prepareStatement(sql);
      prstmt.setInt(1, 1);
      rs = prstmt.executeQuery();
      while (rs.next())
      {
        if (result == null) {
          result = new ArrayList();
        }
        clientBO = new Client();
        clientBO.setClientId(Long.valueOf(rs.getLong("client_id")));
        clientBO.setClientName(rs.getString("client_name"));
        clientBO.setUsername(rs.getString("username").trim());
        clientBO.setDescription(rs.getString("description"));
        clientBO.setMaxRequest(rs.getLong("max_request"));
        clientBO.setStatus(rs.getInt("status"));
        clientBO.setPassword(rs.getString("password") != null ? rs.getString("password").trim() : "");
        clientBO.setIpAddress(rs.getString("ip_address") != null ? rs.getString("ip_address").trim() : "");
        clientBO.setClientType(rs.getInt("client_type"));
        result.add(clientBO);
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      throw ex;
    }
    finally
    {
      DatabaseConnection.closeConnection(null, rs, prstmt);
    }
    return result;
  }
  
  public static ArrayList<Webservice> getAllWebservice(Connection cn)
    throws Exception
  {
    ArrayList<Webservice> result = null;
    Webservice webservice = null;
    PreparedStatement prstmt = null;
    ResultSet rs = null;
    try
    {
      if (cn.isClosed()) {
        cn = null;
      }
      if (cn == null) {
        cn = DatabaseConnection.openConnection();
      }
      String sql = "select * from " + ResourceBundleUtils.getResource("schema") + ".webservice where status = ?";
      
      prstmt = cn.prepareStatement(sql);
      prstmt.setInt(1, 1);
      rs = prstmt.executeQuery();
      while (rs.next())
      {
        if (result == null) {
          result = new ArrayList();
        }
        webservice = new Webservice();
        webservice.setWsId(Long.valueOf(rs.getLong("ws_id")));
        webservice.setWsName(rs.getString("ws_name").trim());
        webservice.setMaxConnection(rs.getLong("max_connection"));
        webservice.setWsCode(rs.getString("ws_code").trim());
        webservice.setStatus(rs.getInt("status"));
        webservice.setWsdl(rs.getString("wsdl"));
        webservice.setDescription(rs.getString("description"));
        webservice.setMsgTemplate(rs.getString("msg_template"));
        webservice.setWsUsernameTag(rs.getString("ws_username_tag"));
        webservice.setWsPasswordTag(rs.getString("ws_password_tag"));
        webservice.setWsUsername(rs.getString("ws_username"));
        webservice.setWsPassword(rs.getString("ws_password"));
        webservice.setTimeout(Integer.valueOf(rs.getInt("timeout")));
        webservice.setWarningTimeLevel1(Long.valueOf(rs.getLong("warning_time_level1")));
        webservice.setWarningTimeLevel2(Long.valueOf(rs.getLong("warning_time_level2")));
        webservice.setWarningTimeLevel3(Long.valueOf(rs.getLong("warning_time_level3")));
        result.add(webservice);
      }
    }
    catch (Exception ex)
    {
      throw ex;
    }
    finally
    {
      DatabaseConnection.closeConnection(null, rs, prstmt);
    }
    return result;
  }
  
  public static ArrayList<AccessManegement> getAllAccessManegements(Connection cn)
    throws Exception
  {
    ArrayList<AccessManegement> result = null;
    AccessManegement accessManegement = null;
    PreparedStatement prstmt = null;
    ResultSet rs = null;
    Long clientId = null;
    Long wsId = null;
    try
    {
      if (cn.isClosed()) {
        cn = null;
      }
      if (cn == null) {
        cn = DatabaseConnection.openConnection();
      }
      String sql = "select * from " + ResourceBundleUtils.getResource("schema") + ".access_manegement where status = ?";
      prstmt = cn.prepareStatement(sql);
      prstmt.setInt(1, 1);
      rs = prstmt.executeQuery();
      while (rs.next())
      {
        if (result == null) {
          result = new ArrayList();
        }
        accessManegement = new AccessManegement();
        accessManegement.setAmId(Long.valueOf(rs.getLong("am_id")));
        clientId = Long.valueOf(rs.getLong("client_id"));
        if (clientId != null) {
          accessManegement.setClient(getClientById(clientId, cn));
        }
        wsId = Long.valueOf(rs.getLong("ws_id"));
        if (wsId != null) {
          accessManegement.setWebservice(getWebserviceById(wsId, cn));
        }
        accessManegement.setMaxConnection(rs.getLong("max_connection"));
        accessManegement.setStatus(Integer.valueOf(rs.getInt("status")));
        result.add(accessManegement);
      }
    }
    catch (Exception ex)
    {
      throw ex;
    }
    finally
    {
      DatabaseConnection.closeConnection(null, rs, prstmt);
    }
    return result;
  }
  
  public static Client getClientById(Long clientId, Connection cn)
    throws Exception
  {
    Client result = null;
    PreparedStatement prstmt = null;
    ResultSet rs = null;
    try
    {
      if (cn.isClosed()) {
        cn = null;
      }
      if (cn == null) {
        cn = DatabaseConnection.openConnection();
      }
      String sql = "select * from " + ResourceBundleUtils.getResource("schema") + ".client where client_id = ? and status = ?";
      prstmt = cn.prepareStatement(sql);
      prstmt.setLong(1, clientId.longValue());
      prstmt.setInt(2, 1);
      rs = prstmt.executeQuery();
      boolean hasData = rs.next();
      if (hasData)
      {
        result = new Client();
        result.setClientId(Long.valueOf(rs.getLong("client_id")));
        result.setClientName(rs.getString("client_name"));
        result.setUsername(rs.getString("username").trim());
        result.setDescription(rs.getString("description"));
        result.setMaxRequest(rs.getLong("max_request"));
        result.setStatus(rs.getInt("status"));
        result.setClientType(rs.getInt("client_type"));
        result.setPassword(rs.getString("password") != null ? rs.getString("password").trim() : "");
      }
    }
    catch (Exception ex)
    {
      throw ex;
    }
    finally
    {
      DatabaseConnection.closeConnection(null, rs, prstmt);
    }
    return result;
  }
  
  public static Webservice getWebserviceById(Long wsId, Connection cn)
    throws Exception
  {
    Webservice result = null;
    PreparedStatement prstmt = null;
    ResultSet rs = null;
    try
    {
      if (cn.isClosed()) {
        cn = null;
      }
      if (cn == null) {
        cn = DatabaseConnection.openConnection();
      }
      String sql = "select * from " + ResourceBundleUtils.getResource("schema") + ".webservice where ws_id = ? and status = ?";
      
      prstmt = cn.prepareStatement(sql);
      prstmt.setLong(1, wsId.longValue());
      prstmt.setInt(2, 1);
      rs = prstmt.executeQuery();
      boolean hasData = rs.next();
      if (hasData)
      {
        result = new Webservice();
        result.setWsId(Long.valueOf(rs.getLong("ws_id")));
        result.setWsName(rs.getString("ws_name").trim());
        result.setMaxConnection(rs.getLong("max_connection"));
        result.setWsdl(rs.getString("wsdl"));
        result.setDescription(rs.getString("description"));
        result.setMsgTemplate(rs.getString("msg_template"));
        result.setWsCode(rs.getString("ws_code").trim());
        result.setStatus(rs.getInt("status"));
      }
    }
    catch (Exception ex)
    {
      throw ex;
    }
    finally
    {
      DatabaseConnection.closeConnection(null, rs, prstmt);
    }
    return result;
  }
  
  public static ArrayList<ErrorCode> getAllErrorCode(Connection cn)
    throws Exception
  {
    ArrayList<ErrorCode> result = null;
    ErrorCode errorCode = null;
    PreparedStatement prstmt = null;
    ResultSet rs = null;
    try
    {
      if (cn.isClosed()) {
        cn = null;
      }
      if (cn == null) {
        cn = DatabaseConnection.openConnection();
      }
      String sql = "select * from " + ResourceBundleUtils.getResource("schema") + ".error_code";
      
      prstmt = cn.prepareStatement(sql);
      rs = prstmt.executeQuery();
      while (rs.next())
      {
        if (result == null) {
          result = new ArrayList();
        }
        errorCode = new ErrorCode();
        errorCode.setErrorCodeId(Long.valueOf(rs.getLong("error_code_id")));
        errorCode.setErrorCode(rs.getString("error_code"));
        errorCode.setDescription(rs.getString("description"));
        result.add(errorCode);
      }
    }
    catch (Exception ex)
    {
      throw ex;
    }
    finally
    {
      DatabaseConnection.closeConnection(null, rs, prstmt);
    }
    return result;
  }
}