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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

public class DataStore
{
  public final int BUFSIZE = 1024;
  protected Logger logger;
  protected Connection connection;
  protected Statement stmt;
  protected boolean pooling;
  protected String cnString;
  protected String username;
  protected String password;
  protected String sqlcheck;
  protected final Object lock = new Object();
  
  public DataStore(String configFile)
    throws ClassNotFoundException, SQLException, FileNotFoundException, IOException
  {
    FileReader r = null;
    try
    {
      r = new FileReader(configFile);
      Properties properties = new Properties();
      properties.load(r);
      String dbdriver = properties.getProperty("driver");
      String dbconnStr = properties.getProperty("connection");
      String dbusername = properties.getProperty("username");
      String dbpassword = properties.getProperty("password");
      String dbsqlcheck = properties.getProperty("sqlcheck", "select 1 from dual");
      boolean dbpooling = "true".equals(properties.getProperty("pooling", "false"));
      init(dbdriver, dbconnStr, dbusername, dbpassword, dbsqlcheck, dbpooling);
    }
    finally
    {
      if (r != null) {
        try
        {
          r.close();
        }
        catch (IOException ex)
        {
          this.logger.error("close buffer reader exception:" + ex);
        }
      }
    }
  }
  
  public DataStore(Properties properties)
    throws ClassNotFoundException, SQLException
  {
    String dbdriver = properties.getProperty("driver");
    String dbconnStr = properties.getProperty("connection");
    String dbusername = properties.getProperty("username");
    String dbpassword = properties.getProperty("password");
    String dbsqlcheck = properties.getProperty("sqlcheck", "select 1 from dual");
    boolean dbpooling = "true".equals(properties.getProperty("pooling", "false"));
    init(dbdriver, dbconnStr, dbusername, dbpassword, dbsqlcheck, dbpooling);
  }
  
  public DataStore(String driver, String cnString, String username, String password, String sqlcheck)
    throws ClassNotFoundException, SQLException
  {
    init(driver, cnString, username, password, sqlcheck, false);
  }
  
  public DataStore(String driver, String cnString, String username, String password, String sqlcheck, boolean pooling)
    throws ClassNotFoundException, SQLException
  {
    init(driver, cnString, username, password, sqlcheck, pooling);
  }
  
  private void init(String driver, String cnString, String username, String password, String sqlcheck, boolean pooling)
    throws ClassNotFoundException, SQLException
  {
    this.logger = Logger.getLogger("datastore");
    if ((driver == null) || (cnString == null) || (username == null) || (password == null)) {
      throw new NullPointerException("One of mandatory parameter is null");
    }
    if (sqlcheck == null) {
      sqlcheck = "select 1 from dual";
    }
    try
    {
      this.logger.info("instance driver:" + driver);
      Class.forName(driver);
      this.pooling = pooling;
      this.cnString = cnString;
      this.username = username;
      this.password = password;
      this.sqlcheck = sqlcheck;
      if (pooling)
      {
        this.logger.info("instance pool");
        instancePool(cnString, username, password);
      }
    }
    catch (ClassNotFoundException e)
    {
      this.logger.error(e.getMessage(), e);
      throw e;
    }
    catch (SQLException e)
    {
      this.logger.error(e.getMessage(), e);
      throw e;
    }
  }
  
  public void reload(String cnString, String username, String password, boolean pooling)
  {
    synchronized (this.lock)
    {
      if ((!this.cnString.equals(cnString)) || (!this.username.equals(username)) || (!this.password.equals(password)))
      {
        this.cnString = cnString;
        this.username = username;
        this.password = password;
        if (this.connection != null) {
          try
          {
            this.connection.close();
          }
          catch (SQLException ex)
          {
            this.logger.error(ex.getMessage(), ex);
          }
          finally
          {
            this.connection = null;
          }
        }
        if (this.pooling) {
          try
          {
            PoolingDriver driver = (PoolingDriver)DriverManager.getDriver("jdbc:apache:commons:dbcp:");
            if (driver != null) {
              driver.closePool("pool");
            }
          }
          catch (SQLException ex)
          {
            this.logger.error(ex.getMessage(), ex);
          }
        }
      }
      this.pooling = pooling;
      if (pooling) {
        try
        {
          instancePool(cnString, username, password);
        }
        catch (ClassNotFoundException ex)
        {
          this.logger.error(ex.getMessage(), ex);
        }
        catch (SQLException ex)
        {
          this.logger.error(ex.getMessage(), ex);
        }
      }
    }
  }
  
  private void instancePool(String cnString, String username, String password)
    throws ClassNotFoundException, SQLException
  {
    ObjectPool connectionPool = new GenericObjectPool(null, 10);
    







    ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(cnString, username, password);
    





    new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);
    


    Class.forName("org.apache.commons.dbcp.PoolingDriver");
    PoolingDriver driver = (PoolingDriver)DriverManager.getDriver("jdbc:apache:commons:dbcp:");
    



    driver.registerPool("pool", connectionPool);
  }
  
  public void destroy()
  {
    synchronized (this.lock)
    {
      try
      {
        if (this.stmt != null)
        {
          this.logger.info("close statement");
          this.stmt.close();
        }
      }
      catch (SQLException ex)
      {
        this.logger.error("close statement error:" + ex.getMessage());
      }
      try
      {
        if (this.connection != null)
        {
          this.logger.info("close connection");
          this.connection.close();
        }
      }
      catch (SQLException ex)
      {
        this.logger.error(ex.getMessage(), ex);
      }
      try
      {
        if (this.pooling)
        {
          this.logger.info("close connection pool");
          PoolingDriver driver = (PoolingDriver)DriverManager.getDriver("jdbc:apache:commons:dbcp:");
          driver.closePool("pool");
        }
      }
      catch (SQLException ex)
      {
        this.logger.error("close pool exception: " + ex.getMessage());
      }
    }
  }
  
  public int doUpdate(String sql)
    throws SQLException, ConnectionException
  {
    synchronized (this.lock)
    {
      this.logger.debug(sql);
      PreparedStatement cstmt = null;
      if (this.connection == null)
      {
        this.logger.info("the connection is null, create connection");
        this.connection = connection();
      }
      try
      {
        cstmt = this.connection.prepareStatement(sql);
        int r = cstmt.executeUpdate();
        cstmt.close();
        return r;
      }
      catch (SQLException e)
      {
        this.logger.error("update database error");
        if (cstmt != null) {
          try
          {
            this.logger.info("close open statement");
            cstmt.close();
          }
          catch (SQLException ex)
          {
            this.logger.error("close statement error:" + ex);
          }
        }
        this.logger.info("validate the connection now, may be it has been closed");
        validate();
        throw e;
      }
    }
  }
  
  public ResultSet doQuery(String sql)
    throws SQLException, ConnectionException
  {
    synchronized (this.lock)
    {
      this.logger.debug(sql);
      if (this.connection == null)
      {
        this.logger.info("the connection is null, create connection");
        this.connection = connection();
      }
      try
      {
        return this.stmt.executeQuery(sql);
      }
      catch (SQLException e)
      {
        this.logger.error("execute query error");
        this.logger.info("validate the connection now, may be it has been closed");
        validate();
        throw e;
      }
    }
  }
  
//  public DataSource[] retrieveData(ParamList param, String table)   throws SQLException, IOException, ConnectionException
//  {
//      
//    DataSource[] datasources = null;  
//    
//    synchronized (this.lock)
//    {
//      if ((param == null) || (param.size() == 0)) {
//        return null;
//      }
//      Statement cstmt = null;
//      Exception exception = null;
//      try
//      {
//        if (this.connection == null)
//        {
//          this.logger.info("the connection is null, create connection");
//          this.connection = connection();
//        }
//        StringBuffer bufsql = new StringBuffer();
//        bufsql.append("SELECT ");
//        Param[] pin = param.paramIn();
//        Param[] pout = param.paramOut();
//        if (((pin == null) || (pin.length == 0)) && ((pout == null) || (pout.length == 0)))
//        {
//          DataSource[] arrayOfDataSource = null;
//          return arrayOfDataSource;
//        }
//        for (int i = 0; i < pout.length; i++)
//        {
//          bufsql.append(pout[i].getName());
//          if (i != pout.length - 1) {
//            bufsql.append(',');
//          }
//        }
//        bufsql.append(" FROM ");
//        bufsql.append(table);
//        if ((pin != null) && (pin.length > 0))
//        {
//          bufsql.append(" WHERE ");
//          for (int i = 0; i < pin.length; i++)
//          {
//            bufsql.append(pin[i].getName());
//            bufsql.append('=');
//            if (pin[i].getDatatype().equals("string"))
//            {
//              bufsql.append('\'');
//              bufsql.append(pin[i].getValue());
//              bufsql.append('\'');
//            }
//            else
//            {
//              bufsql.append(pin[i].getValue());
//            }
//            if (i != pin.length - 1) {
//              bufsql.append(" AND ");
//            }
//          }
//        }
//        String sql = bufsql.toString();
//        
//
//        this.logger.debug(sql);
//        ArrayList<DataSource> v = new ArrayList();
//        cstmt = this.connection.createStatement();
//        ResultSet rs = cstmt.executeQuery(sql);
//        while (rs.next())
//        {
//          DataSource datasource = new DataSource();
//          for (Param p : pout)
//          {
//            String type = p.getDatatype();
//            String name = p.getName();
//            Object value = null;
//            if (type.equals("byte"))
//            {
//              value = Byte.valueOf(rs.getByte(name));
//            }
//            else if (type.equals("short"))
//            {
//              value = Short.valueOf(rs.getShort(name));
//            }
//            else if (type.equals("int"))
//            {
//              value = Integer.valueOf(rs.getInt(name));
//            }
//            else if (type.equals("long"))
//            {
//              value = Long.valueOf(rs.getLong(name));
//            }
//            else if (type.equals("float"))
//            {
//              value = Float.valueOf(rs.getFloat(name));
//            }
//            else if (type.equals("double"))
//            {
//              value = Double.valueOf(rs.getDouble(name));
//            }
//            else if (type.equals("boolean"))
//            {
//              value = Boolean.valueOf(rs.getBoolean(name));
//            }
//            else if (type.equals("date"))
//            {
//              value = rs.getDate(name);
//            }
//            else if (type.equals("string"))
//            {
//              value = rs.getString(name);
//            }
//            else if (type.equals("blob"))
//            {
//              Blob blob = rs.getBlob(name);
//              if (blob == null)
//              {
//                this.logger.warn("Binary data is null");
//              }
//              else
//              {
//                InputStream in = blob.getBinaryStream();
//                ByteBuffer buffer = new ByteBuffer();
//                
//
//                byte[] b = new byte[1024];
//                int len;
//                while ((len = in.read(b)) != -1) {
//                  buffer.appendBytes(b, len);
//                }
//                value = buffer.getBuffer();
//                in.close();
//              }
//            }
//            else if (type.equals("clob"))
//            {
//              type = "string";
//              Clob clob = rs.getClob(name);
//              if (clob == null)
//              {
//                this.logger.warn("Binary data is null");
//              }
//              else
//              {
//                InputStream in = clob.getAsciiStream();
//                ByteBuffer buffer = new ByteBuffer();
//                
//
//                byte[] b = new byte[1024];
//                int len;
//                while ((len = in.read(b)) != -1) {
//                  buffer.appendBytes(b, len);
//                }
//                value = buffer.getBuffer();
//                in.close();
//              }
//            }
//            datasource.add(new Data(name, type, value));
//          }
//          v.add(datasource);
//        }
//        rs.close();
//        cstmt.close();
//         datasources = (DataSource[])v.toArray(new DataSource[v.size()]);
//        return datasources;
//      }
//      catch (SQLException e)
//      {
//        this.logger.error("retrieve data error");
//        exception = e;
//        throw e;
//      }
//      
//      
//      
//     
//    }
//     return datasources;
//  }
//  
  public int updateData(ParamList param, String table)
    throws SQLException, IOException, ConnectionException
  {
    synchronized (this.lock)
    {
      if ((param == null) || (param.size() == 0)) {
        return 0;
      }
      Param[] pin = param.paramIn();
      Param[] pout = param.paramOut();
      if ((pout == null) || (pout.length == 0)) {
        return 0;
      }
      StringBuffer bufsql = new StringBuffer();
      bufsql.append("UPDATE ");
      bufsql.append(table);
      bufsql.append(" SET ");
      for (int i = 0; i < pout.length; i++)
      {
        bufsql.append(pout[i].getName());
        bufsql.append('=');
        if (pout[i].getDatatype().equals("string"))
        {
          bufsql.append('\'');
          bufsql.append(pout[i].getValue());
          bufsql.append('\'');
        }
        else
        {
          bufsql.append(pout[i].getValue());
        }
        if (i != pout.length - 1) {
          bufsql.append(',');
        }
      }
      if ((pin != null) && (pin.length > 0))
      {
        bufsql.append(" WHERE ");
        for (int i = 0; i < pin.length; i++)
        {
          bufsql.append(pin[i].getName());
          bufsql.append('=');
          if (pin[i].getDatatype().equals("string"))
          {
            bufsql.append('\'');
            bufsql.append(pin[i].getValue());
            bufsql.append('\'');
          }
          else
          {
            bufsql.append(pin[i].getValue());
          }
          if (i != pin.length - 1) {
            bufsql.append(" AND ");
          }
        }
      }
      String sql = bufsql.toString();
      return doUpdate(sql);
    }
  }
  
  public int insertData(ParamList param, String table)
    throws SQLException, IOException, ConnectionException
  {
    synchronized (this.lock)
    {
      if ((param == null) || (param.size() == 0)) {
        return 0;
      }
      Param[] pout = param.paramOut();
      if ((pout == null) || (pout.length == 0)) {
        return 0;
      }
      StringBuffer bufsql = new StringBuffer();
      bufsql.append("INSERT INTO ");
      bufsql.append(table);
      bufsql.append('(');
      for (int i = 0; i < pout.length; i++)
      {
        bufsql.append(pout[i].getName());
        if (i != pout.length - 1) {
          bufsql.append(',');
        }
      }
      bufsql.append(") VALUES (");
      for (int i = 0; i < pout.length; i++)
      {
        if (pout[i].getDatatype().equals("string"))
        {
          bufsql.append('\'');
          bufsql.append(pout[i].getValue());
          bufsql.append('\'');
        }
        else
        {
          bufsql.append(pout[i].getValue());
        }
        if (i != pout.length - 1) {
          bufsql.append(',');
        }
      }
      bufsql.append(')');
      String sql = bufsql.toString();
      return doUpdate(sql);
    }
  }
  
  public int insertData(ParamList[] param, String table)
    throws SQLException, IOException, ConnectionException
  {
    synchronized (this.lock)
    {
      if ((param == null) || (param.length == 0)) {
        return 0;
      }
      if (this.connection == null)
      {
        this.logger.info("the connection is null, create connection");
        this.connection = connection();
      }
      PreparedStatement pstmt = null;
      Exception exception = null;
      try
      {
        StringBuffer bufsql = new StringBuffer();
        StringBuffer bufvl = new StringBuffer();
        bufsql.append("INSERT INTO ");
        bufsql.append(table);
        bufsql.append('(');
        bufvl.append(") VALUES (");
        for (int i = 0; i < param[0].size(); i++)
        {
          bufsql.append(param[0].allParam()[i].getName());
          bufvl.append('?');
          if (i != param[0].size() - 1)
          {
            bufsql.append(',');
            bufvl.append(',');
          }
        }
        bufvl.append(')');
        bufsql.append(bufvl);
        String sql = bufsql.toString();
        this.logger.debug(sql);
        pstmt = this.connection.prepareStatement(sql);
        for (ParamList p : param)
        {
          Param[] pout = p.paramOut();
          if ((pout != null) && (pout.length != 0))
          {
            for (int i = 1; i <= pout.length; i++)
            {
              String type = pout[(i - 1)].getDatatype();
              Object value = pout[(i - 1)].getValue();
              if (type.equals("byte")) {
                pstmt.setByte(i, ((Byte)value).byteValue());
              } else if (type.equals("short")) {
                pstmt.setShort(i, ((Short)value).shortValue());
              } else if (type.equals("int")) {
                pstmt.setInt(i, ((Integer)value).intValue());
              } else if (type.equals("long")) {
                pstmt.setLong(i, ((Long)value).longValue());
              } else if (type.equals("float")) {
                pstmt.setFloat(i, ((Float)value).floatValue());
              } else if (type.equals("double")) {
                pstmt.setDouble(i, ((Double)value).doubleValue());
              } else if (type.equals("boolean")) {
                pstmt.setBoolean(i, ((Boolean)value).booleanValue());
              } else if (type.equals("date")) {
                pstmt.setDate(i, (Date)value);
              } else if (type.equals("string")) {
                pstmt.setString(i, (String)value);
              }
            }
            pstmt.addBatch();
          }
        }
        int result = pstmt.executeBatch().length;
        pstmt.close();
        return result;
      }
      catch (SQLException e)
      {
        this.logger.error("insert data error");
        exception = e;
        throw e;
      }
      
    }
  }
  
  public Connection getConnection()
    throws SQLException, ConnectionException
  {
    synchronized (this.lock)
    {
      if (isPooling()) {
        return DriverManager.getConnection("jdbc:apache:commons:dbcp:pool");
      }
      return check(this.connection) ? this.connection : connection();
    }
  }
  
  public Connection connection()
    throws ConnectionException
  {
    try
    {
      this.logger.info("create new connetion");
      this.logger.info("connection url: " + this.cnString);
      this.logger.info("connecting ...");
      this.connection = DriverManager.getConnection(this.cnString, this.username, this.password);
      this.stmt = this.connection.createStatement();
      this.logger.info("connected");
      return this.connection;
    }
    catch (SQLException ex)
    {
      this.logger.error("create connection error");
      this.connection = null;
      throw new ConnectionException(ex.getMessage(), this.cnString);
    }
  }
  
  public boolean check(Connection conn)
  {
    if (conn != null)
    {
      PreparedStatement cstmt = null;
      ResultSet rs = null;
      try
      {
        this.logger.info("check connection");
        this.logger.debug(this.sqlcheck);
        cstmt = conn.prepareStatement(this.sqlcheck);
        rs = cstmt.executeQuery();
        this.logger.info("connection is ok");
        cstmt.close();
        cstmt = null;
        rs.close();
        rs = null;
        return true;
      }
      catch (SQLException ex)
      {
        this.logger.info("connection is broken down:" + ex.getMessage());
        return false;
      }
      finally
      {
        if (rs != null) {
          try
          {
            rs.close();
          }
          catch (SQLException ex)
          {
            this.logger.error("close result set error:" + ex);
          }
        }
        if (cstmt != null) {
          try
          {
            cstmt.close();
          }
          catch (SQLException ex)
          {
            this.logger.error("close statement error:" + ex);
          }
        }
      }
    }
    return false;
  }
  
  private void validate()
  {
    synchronized (this.lock)
    {
      if (this.connection != null) {
        try
        {
          this.logger.info("check connection");
          this.logger.debug(this.sqlcheck);
          this.stmt.executeQuery(this.sqlcheck);
          this.logger.info("connection is ok");
        }
        catch (SQLException ex)
        {
          this.logger.error("check connection error:" + ex.toString());
          this.logger.error("the connection has been broken down, set it to null to recreate");
          
          this.connection = null;
        }
      }
    }
  }
  
  public boolean isPooling()
  {
    return this.pooling;
  }
  
  public void setLogger(Logger logger)
  {
    this.logger = logger;
  }
  
  public String getInfor()
  {
    StringBuffer buff = new StringBuffer();
    boolean connected = this.connection != null;
    if (connected) {
      buff.append("Connection:" + this.connection.getClass().getName());
    }
    buff.append("\r\nUrl:" + this.cnString);
    buff.append("\r\nUsername:" + this.username);
    buff.append("\r\nPassword:" + this.password);
    buff.append("\r\nConnected:" + connected);
    return buff.toString();
  }
}
