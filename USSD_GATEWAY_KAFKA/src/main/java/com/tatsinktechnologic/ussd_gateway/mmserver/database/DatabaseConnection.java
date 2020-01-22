/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.database;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.mmserver.base.Log;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection
{
  private Properties properties = new Properties();
  private static DatabaseConnection instance = null;
  private String driverName = "";
  private String connectionUri = "";
  private String username = "";
  private String password = "";
  private String schema = "";
  private String sqlCheck = "";
  private Connection connection = null;
  private DataStore datastore = null;
  
  private DatabaseConnection(String config)
  {
    try
    {
      this.properties.load(new FileInputStream(config));
      this.driverName = this.properties.getProperty("driver");
      this.connectionUri =this.properties.getProperty("connection");
      this.schema = this.properties.getProperty("schema");
      this.username = this.properties.getProperty("username");
      this.password = this.properties.getProperty("password");
      this.sqlCheck = this.properties.getProperty("sqlcheck");
      this.datastore = new DataStore(this.driverName, this.connectionUri, this.username, this.password, this.sqlCheck, false);
      this.connection = this.datastore.getConnection();
    }
    catch (Exception ex)
    {
      Log.error("Can not create connection " + ex.toString());
    }
  }
  
  public static DatabaseConnection shareInstance()
  {
    String config = System.getProperty("com.viettel.mmserver.db.path");
    if (config == null) {
      return null;
    }
    if (instance == null) {
      instance = new DatabaseConnection(config);
    }
    return instance;
  }
  
  public Connection updateConnection()
  {
    try
    {
      this.connection = this.datastore.getConnection();
      Log.debug("create db connection");
    }
    catch (SQLException ex)
    {
      Log.error("Error when update connection " + ex);
    }
    return this.connection;
  }
  
  private final Object lock = new Object();
  
  public boolean checkConnection()
  {
    return false;
  }
  
  public String getSchema()
  {
    return this.schema;
  }
  
  public Connection getConnection()
  {
    try
    {
      if ((this.connection == null) || (this.connection.isClosed())) {
        this.connection = this.datastore.getConnection();
      }
    }
    catch (SQLException ex)
    {
      Log.error("exception when get database connection " + ex);
    }
    return this.connection;
  }
}
