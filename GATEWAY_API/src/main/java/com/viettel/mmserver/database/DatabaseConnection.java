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
package com.viettel.mmserver.database;

/**
 *
 * @author olivier.tatsinkou
 */
import com.viettel.bccsgw.security.PassTranformer;
import com.viettel.mmserver.base.Log;
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
      this.driverName = PassTranformer.decrypt(this.properties.getProperty("driver"));
      this.connectionUri = PassTranformer.decrypt(this.properties.getProperty("connection"));
      this.schema = PassTranformer.decrypt(this.properties.getProperty("schema"));
      this.username = PassTranformer.decrypt(this.properties.getProperty("username"));
      this.password = PassTranformer.decrypt(this.properties.getProperty("password"));
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

