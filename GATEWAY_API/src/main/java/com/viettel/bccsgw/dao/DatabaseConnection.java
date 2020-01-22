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
import com.viettel.bccsgw.utils.ResourceBundleUtils;
import desktopapplication1.EncryptDecryptUtils;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnection
{
  private static final String ORACLE_CONFIG_FILE = "../conf/OracleDB.properties";
  public static final String DB_LOGGING_FILE = "../conf/dbLog.properties";
  private static final String ORACLE_DRIVER = "oracle.jdbc.OracleDriver";
  
  public static void main(String[] argsp)
  {
    String decryptString = EncryptDecryptUtils.decryptFile("D:\\BU_BSS\\INVENTORY\\SM_MZB_ATTT\\src\\java\\com\\viettel\\im\\database\\config\\default session");
    System.out.println(decryptString);
  }
  
  public static Connection openConnection()
    throws Exception
  {
    Connection connection = null;
    String userName = null;
    String password = null;
    String url = "";
    String decryptString = EncryptDecryptUtils.decryptFile("../conf/OracleDB.properties");
    String[] appProperties = decryptString.split("\r\n");
    url = appProperties[0].split("=", 2)[1].trim();
    

    userName = appProperties[1].split("=", 2)[1].trim();
    password = appProperties[2].split("=", 2)[1].trim();
    


    String driver = ResourceBundleUtils.getResource("databaseDriver");
    Class.forName((driver != null) && (driver.trim().length() > 0) ? driver.trim() : "oracle.jdbc.OracleDriver");
    connection = DriverManager.getConnection(url, userName, password);
    return connection;
  }
  
  public static Connection openConnection(String encryptFilePath)
    throws Exception
  {
    Connection connection = null;
    String userName = null;
    String password = null;
    String url = "";
    String decryptString = EncryptDecryptUtils.decryptFile(encryptFilePath);
    String[] appProperties = decryptString.split("\r\n");
    url = appProperties[0].split("=", 2)[1].trim();
    

    userName = appProperties[1].split("=", 2)[1].trim();
    password = appProperties[2].split("=", 2)[1].trim();
    


    String driver = ResourceBundleUtils.getResource("databaseDriver");
    Class.forName((driver != null) && (driver.trim().length() > 0) ? driver.trim() : "oracle.jdbc.OracleDriver");
    connection = DriverManager.getConnection(url, userName, password);
    return connection;
  }
  
  public static void closeConnection(Connection connection, ResultSet rs, Statement stmt)
    throws Exception
  {
    if (rs != null)
    {
      rs.close();
      rs = null;
    }
    if (stmt != null)
    {
      stmt.close();
      stmt = null;
    }
    if (connection != null)
    {
      if (!connection.isClosed()) {
        connection.close();
      }
      connection = null;
    }
  }
  
  public static void closeConnection(Connection connection, ResultSet rs, PreparedStatement prstmt)
    throws Exception
  {
    if (rs != null)
    {
      rs.close();
      rs = null;
    }
    if (prstmt != null)
    {
      prstmt.close();
      prstmt = null;
    }
    if (connection != null)
    {
      if (!connection.isClosed()) {
        connection.close();
      }
      connection = null;
    }
  }
}
