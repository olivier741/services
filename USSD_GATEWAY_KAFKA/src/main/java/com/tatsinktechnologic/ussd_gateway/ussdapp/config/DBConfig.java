/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.config;

/**
 *
 * @author olivier.tatsinkou
 */
import com.tatsinktechnologic.ussd_gateway.utils.EncryptDecryptUtils;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.apache.log4j.Logger;

public class DBConfig
{
  private String driver;
  private String url;
  private Logger logger;
  private Connection conn;
  private Statement statement;
  private final Object lock = new Object();
  private int dbLoginTimeout;
  private int dbQueryTimeout;
  private Properties connectionParams;
  
  public DBConfig(UssdAppConfig config)
    throws Exception
  {
    this.logger = Logger.getLogger(DBConfig.class);
    

    this.logger.info("get db login timeout");
    this.dbLoginTimeout = (config.getDbLoginTimeout() * 1000);
    this.logger.info("login timeout (ms): " + this.dbLoginTimeout);
    

    this.logger.info("get db query timeout");
    this.dbQueryTimeout = (config.getDbQueryTimeout() * 1000);
    this.logger.info("query timeout (ms): " + this.dbQueryTimeout);
    

    this.logger.info("get db driver");
    this.driver = config.getDBDriver();
    if (this.driver == null) {
      throw new Exception("missing database driver");
    }
    this.logger.info("driver: " + this.driver);
    this.logger.info("regist driver");
    System.setProperty("jdbc.drivers", this.driver);
    

    this.logger.info("get db encrypt file");
    String encryptFile = config.getDBEncryptFile();
    if (encryptFile == null) {
      throw new Exception("missing db encrypt file");
    }
    this.logger.info("encrypt file: " + encryptFile);
    

    this.logger.info("decode db encrypt file");
    String decryptString = EncryptDecryptUtils.decryptFile(URLDecoder.decode(encryptFile, "UTF-8"));
    String[] encryptProp = decryptString.split("\r\n");
    String pass;
    String user = null;
    if (encryptProp.length >= 3)
    {
      this.url = encryptProp[0].split("=", 2)[1].trim();
      user = encryptProp[1].split("=", 2)[1].trim();
      pass = encryptProp[2].split("=", 2)[1].trim();
    }
    else
    {
      throw new Exception("missing connection param in encrypted file");
    }

    this.logger.info("decode success");
    this.logger.info("make connection params object");
    this.connectionParams = new Properties();
    this.connectionParams.put("user", user);
    this.connectionParams.put("password", pass);
    this.connectionParams.put("oracle.net.READ_TIMEOUT", "" + this.dbQueryTimeout);
    this.connectionParams.put("oracle.net.CONNECT_TIMEOUT", "" + this.dbLoginTimeout);
    

    this.logger.info("connect to DB ...");
    connectToDB();
    this.logger.info("connect success");
  }
  
  private void connectToDB()
    throws SQLException
  {
    this.conn = DriverManager.getConnection(this.url, this.connectionParams);
    
    this.statement = this.conn.createStatement(1003, 1007);
  }
  
  public void disconnect()
  {
    try
    {
      this.statement.close();
    }
    catch (SQLException ex)
    {
      this.logger.warn("close statement fail: " + ex.getMessage());
    }
    try
    {
      this.conn.close();
    }
    catch (SQLException ex)
    {
      this.logger.warn("close db connection fail: " + ex.getMessage());
    }
  }
  
  public Object getLock()
  {
    return this.lock;
  }
  
  public ResultSet getData(String sql)
    throws Exception
  {
    ResultSet result;
    try
    {
      this.logger.info("excute sql: " + sql);
      result = this.statement.executeQuery(sql);
    }
    catch (SQLException ex)
    {
      this.logger.error("get data fail: " + ex.getMessage() + ". Retry!");
      this.logger.info("reconnect DB");
      disconnect();
      connectToDB();
      
      this.logger.info("excute sql again");
      result = this.statement.executeQuery(sql);
    }
    this.logger.info("excute sql success");
    return result;
  }
  
  public void reconnectDb()
    throws SQLException
  {
    disconnect();
    
    connectToDB();
  }
}

