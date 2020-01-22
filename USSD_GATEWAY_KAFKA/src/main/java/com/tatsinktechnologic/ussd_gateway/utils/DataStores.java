package com.tatsinktechnologic.ussd_gateway.utils;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
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

public class DataStores {

    public static final int BUFSIZE = 1024;
    static Logger logger = Logger.getLogger("DataStores");
    static Connection connection;
    static Statement stmt;
    static boolean pooling;
    static String driver;
    static String cnString;
    static String username;
    static String password;
    static String sqlcheck;
    static boolean instanced;
    static final Object lock = new Object();

    public static void init(String configFile) throws Exception {
        FileReader r = null;
        try {
            r = new FileReader(configFile);
            Properties properties = new Properties();
            properties.load(r);
            r.close();
            String dbdriver = properties.getProperty("driver");
            String dbconnStr = properties.getProperty("connection");
            String dbusername = properties.getProperty("username");
            String dbpassword = properties.getProperty("password");
            String dbsqlcheck = properties.getProperty("sqlcheck", "select 1 from dual");

            String encryptFilePath = properties.getProperty("encrypt_file");
            try {
                String decryptString = EncryptDecryptUtils.decryptFile(URLDecoder.decode(encryptFilePath, "UTF-8"));
                String[] encryptProp = decryptString.split("\r\n");
                if (encryptProp.length >= 3) {
                    dbconnStr = encryptProp[0].split("=")[1].trim();
                    dbusername = encryptProp[1].split("=")[1].trim();
                    dbpassword = encryptProp[2].split("=")[1].trim();
                }
            } catch (Exception ex) {
                logger.error("Can't read encrypt file" + ex);
            }
            boolean dbpooling = "true".equals(properties.getProperty("pooling", "false"));
            init(dbdriver, dbconnStr, dbusername, dbpassword, dbsqlcheck, dbpooling);
            return;
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException ex) {
                    logger.error("close buffer reader exception:" + ex);
                }
            }
        }
    }

    public static void init(Properties properties)  throws Exception {
        String dbdriver = properties.getProperty("driver");
        String dbconnStr = properties.getProperty("connection");
        String dbusername = properties.getProperty("username");
        String dbpassword = properties.getProperty("password");
        String dbsqlcheck = properties.getProperty("sqlcheck", "select 1 from dual");
        boolean dbpooling = "true".equals(properties.getProperty("pooling", "false"));
        init(dbdriver, dbconnStr, dbusername, dbpassword, dbsqlcheck, dbpooling);
    }

    public static void init(String driver, String cnString, String username, String password, String sqlcheck)
            throws Exception {
        init(driver, cnString, username, password, sqlcheck, false);
    }

    public static void init(String driver, String cnString, String username, String password, String sqlcheck, boolean pooling)
            throws Exception {
        if (instanced) {
            logger.info("the DataStore has been instanced");
            return;
        }
        try {
            driver = driver;
            pooling = pooling;
            cnString = cnString;
            username = username;
            password = password;
            sqlcheck = sqlcheck;
            logger.info("instance driver:" + driver);
            Class.forName(driver);
            if (pooling) {
                logger.info("instance pool");
                instancePool(cnString, username, password);
            }
            instanced = true;
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
            instanced = false;
            throw e;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            instanced = false;
            throw e;
        }
    }

    public static void reload(String cnString, String username, String password, boolean pooling) {
        synchronized (lock) {
            if ((!cnString.equals(cnString)) || (!username.equals(username)) || (!password.equals(password))) {
                cnString = cnString;
                username = username;
                password = password;
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException ex) {
                        logger.error(ex.getMessage(), ex);
                    } finally {
                        connection = null;
                    }
                }
                if (pooling) {
                    try {
                        PoolingDriver poolDriver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
                        if (poolDriver != null) {
                            poolDriver.closePool("pool");
                        }
                    } catch (SQLException ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            }
            pooling = pooling;
            if (pooling) {
                try {
                    instancePool(cnString, username, password);
                } catch (ClassNotFoundException ex) {
                    logger.error(ex.getMessage(), ex);
                } catch (SQLException ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }
    }

    private static void instancePool(String cnString, String username, String password)
            throws ClassNotFoundException, SQLException {
        ObjectPool connectionPool = new GenericObjectPool(null, 10);
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(cnString, username, password);
        new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);

        Class.forName("org.apache.commons.dbcp.PoolingDriver");
        PoolingDriver drivers = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");

        drivers.registerPool("pool", connectionPool);
    }

    public static void destroy() {
        synchronized (lock) {
            try {
                if (stmt != null) {
                    logger.info("close statement");
                    stmt.close();
                }
            } catch (SQLException ex) {
                logger.error("close statement error:" + ex.getMessage());
            }
            try {
                if (connection != null) {
                    logger.info("close connection");
                    connection.close();
                }
            } catch (SQLException ex) {
                logger.error(ex.getMessage(), ex);
            }
            try {
                if (pooling) {
                    logger.info("close connection pool");
                    PoolingDriver poolDriver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
                    poolDriver.closePool("pool");
                }
            } catch (SQLException ex) {
                logger.error("close pool exception: " + ex.getMessage());
            }
            instanced = false;
        }
    }

    public static int doUpdate(String sql)
            throws SQLException, ConnectionException {

        synchronized (lock) {
            logger.debug(sql);
            Statement cstmt = null;
            Exception exception = null;
            if (connection == null) {
                logger.info("the connection is null, create connection");
                connection = connection();
            }
            try {
                cstmt = connection.createStatement();
                int r = cstmt.executeUpdate(sql);
                cstmt.close();
                int i = r;
                if (cstmt != null) {
                    try {
                        logger.info("close open statement");
                        cstmt.close();
                    } catch (SQLException ex) {
                        logger.error("close statement error:" + ex);
                    }
                }
                if (exception != null) {
                    logger.info("validate the connection now, may be it has been closed");
                    validate();
                }
                return i;
            } catch (SQLException e) {
                logger.error("update database error");

                exception = e;
                throw e;
            } finally {
                if (cstmt != null) {
                    try {
                        logger.info("close open statement");
                        cstmt.close();
                    } catch (SQLException ex) {
                        logger.error("close statement error:" + ex);
                    }
                }
                if (exception != null) {
                    logger.info("validate the connection now, may be it has been closed");
                    validate();
                }
            }
        }
    }

    public static ResultSet doQuery(String sql)
            throws SQLException, ConnectionException {

        synchronized (lock) {
            logger.debug(sql);
            if (connection == null) {
                logger.info("the connection is null, create connection");
                connection = connection();
            }
            try {
                return stmt.executeQuery(sql);
            } catch (SQLException e) {
                logger.error("execute query error");
                logger.info("validate the connection now, may be it has been closed");
                validate();
                throw e;
            }
        }
    }

    public static DataSource[] retrieveData(ParamList param, String table) throws SQLException, IOException, ConnectionException {
        DataSource[] datasourcelist =null;
        synchronized (lock) {
            if ((param == null) || (param.size() == 0)) {
                return null;
            }
            Statement cstmt = null;
            ResultSet rs = null;
            Exception exception = null;
            try {
                if (connection == null) {
                    logger.info("the connection is null, create connection");
                    connection = connection();
                }
                StringBuffer bufsql = new StringBuffer();
                bufsql.append("SELECT ");
                Param[] pin = param.paramIn();
                Param[] pout = param.paramOut();
                if (((pin == null) || (pin.length == 0)) && ((pout == null) || (pout.length == 0))) {
                    DataSource[] arrayOfDataSource = null;

                    return arrayOfDataSource;
                }
                for (int i = 0; i < pout.length; i++) {
                    bufsql.append(pout[i].getName());
                    if (i != pout.length - 1) {
                        bufsql.append(',');
                    }
                }
                bufsql.append(" FROM ");
                bufsql.append(table);
                if ((pin != null) && (pin.length > 0)) {
                    bufsql.append(" WHERE ");
                    for (int i = 0; i < pin.length; i++) {
                        bufsql.append(pin[i].getName());
                        bufsql.append('=');
                        if (pin[i].getDatatype().equals("string")) {
                            bufsql.append('\'');
                            bufsql.append(pin[i].getValue());
                            bufsql.append('\'');
                        } else {
                            bufsql.append(pin[i].getValue());
                        }
                        if (i != pin.length - 1) {
                            bufsql.append(" AND ");
                        }
                    }
                }
                String sql = bufsql.toString();

                logger.debug(sql);
                ArrayList<DataSource> v = new ArrayList();
                cstmt = connection.createStatement();
                rs = cstmt.executeQuery(sql);
                while (rs.next()) {
                    DataSource datasource = new DataSource();
                    for (Param p : pout) {
                        String type = p.getDatatype();
                        String name = p.getName();
                        Object value = null;
                        if (type.equals("byte")) {
                            value = Byte.valueOf(rs.getByte(name));
                        } else if (type.equals("short")) {
                            value = Short.valueOf(rs.getShort(name));
                        } else if (type.equals("int")) {
                            value = Integer.valueOf(rs.getInt(name));
                        } else if (type.equals("long")) {
                            value = Long.valueOf(rs.getLong(name));
                        } else if (type.equals("float")) {
                            value = Float.valueOf(rs.getFloat(name));
                        } else if (type.equals("double")) {
                            value = Double.valueOf(rs.getDouble(name));
                        } else if (type.equals("boolean")) {
                            value = Boolean.valueOf(rs.getBoolean(name));
                        } else if (type.equals("date")) {
                            value = rs.getDate(name);
                        } else if (type.equals("string")) {
                            value = rs.getString(name);
                        } else if (type.equals("blob")) {
                            Blob blob = rs.getBlob(name);
                            if (blob == null) {
                                logger.warn("Binary data is null");
                            } else {
                                InputStream in = blob.getBinaryStream();
                                ByteBuffer buffer = new ByteBuffer();

                                byte[] b = new byte[1024];
                                int len;
                                while ((len = in.read(b)) != -1) {
                                    buffer.appendBytes(b, len);
                                }
                                value = buffer.getBuffer();
                                in.close();
                            }
                        } else if (type.equals("clob")) {
                            type = "string";
                            Clob clob = rs.getClob(name);
                            if (clob == null) {
                                logger.warn("Binary data is null");
                            } else {
                                InputStream in = clob.getAsciiStream();
                                ByteBuffer buffer = new ByteBuffer();

                                byte[] b = new byte[1024];
                                int len;
                                while ((len = in.read(b)) != -1) {
                                    buffer.appendBytes(b, len);
                                }
                                value = buffer.getBuffer();
                                in.close();
                            }
                        }
                        datasource.add(new Data(name, type, value));
                    }
                    v.add(datasource);
                }
                rs.close();
                cstmt.close();
                datasourcelist = (DataSource[]) v.toArray(new DataSource[v.size()]);

            } catch (SQLException e) {
                logger.error("retrieve data error");
                exception = e;
                throw e;
            } finally {
//         jsr 6;
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    logger.error("close result set error:" + ex);
                }
            }

            if (cstmt != null) {
                logger.info("close the open statement");
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    logger.error("close statement error:" + ex);
                }
            }
            if (exception != null) {
                logger.info("validate the connection now, may be it has been closed");
                validate();
            }
//       ret;
        }
        return datasourcelist;
    }

    public static int updateData(ParamList param, String table)  throws SQLException, IOException, ConnectionException {

        synchronized (lock) {
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
            for (int i = 0; i < pout.length; i++) {
                bufsql.append(pout[i].getName());
                bufsql.append('=');
                if (pout[i].getDatatype().equals("string")) {
                    bufsql.append('\'');
                    bufsql.append(pout[i].getValue());
                    bufsql.append('\'');
                } else {
                    bufsql.append(pout[i].getValue());
                }
                if (i != pout.length - 1) {
                    bufsql.append(',');
                }
            }
            if ((pin != null) && (pin.length > 0)) {
                bufsql.append(" WHERE ");
                for (int i = 0; i < pin.length; i++) {
                    bufsql.append(pin[i].getName());
                    bufsql.append('=');
                    if (pin[i].getDatatype().equals("string")) {
                        bufsql.append('\'');
                        bufsql.append(pin[i].getValue());
                        bufsql.append('\'');
                    } else {
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

    public static int insertData(ParamList param, String table)
            throws SQLException, IOException, ConnectionException {

        synchronized (lock) {
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
            for (int i = 0; i < pout.length; i++) {
                bufsql.append(pout[i].getName());
                if (i != pout.length - 1) {
                    bufsql.append(',');
                }
            }
            bufsql.append(") VALUES (");
            for (int i = 0; i < pout.length; i++) {
                if (pout[i].getDatatype().equals("string")) {
                    bufsql.append('\'');
                    bufsql.append(pout[i].getValue());
                    bufsql.append('\'');
                } else {
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

    public static int insertData(ParamList[] param, String table)
            throws SQLException, IOException, ConnectionException {

        synchronized (lock) {
            if ((param == null) || (param.length == 0)) {
                return 0;
            }
            if (connection == null) {
                logger.info("the connection is null, create connection");
                connection = connection();
            }
            PreparedStatement pstmt = null;
            Exception exception = null;
            try {
                StringBuffer bufsql = new StringBuffer();
                StringBuffer bufvl = new StringBuffer();
                bufsql.append("INSERT INTO ");
                bufsql.append(table);
                bufsql.append('(');
                bufvl.append(") VALUES (");
                for (int i = 0; i < param[0].size(); i++) {
                    bufsql.append(param[0].allParam()[i].getName());
                    bufvl.append('?');
                    if (i != param[0].size() - 1) {
                        bufsql.append(',');
                        bufvl.append(',');
                    }
                }
                bufvl.append(')');
                bufsql.append(bufvl);
                String sql = bufsql.toString();
                logger.debug(sql);
                pstmt = connection.prepareStatement(sql);
                for (ParamList p : param) {
                    Param[] pout = p.paramOut();
                    if ((pout != null) && (pout.length != 0)) {
                        for (int i = 1; i <= pout.length; i++) {
                            String type = pout[(i - 1)].getDatatype();
                            Object value = pout[(i - 1)].getValue();
                            if (type.equals("byte")) {
                                pstmt.setByte(i, ((Byte) value).byteValue());
                            } else if (type.equals("short")) {
                                pstmt.setShort(i, ((Short) value).shortValue());
                            } else if (type.equals("int")) {
                                pstmt.setInt(i, ((Integer) value).intValue());
                            } else if (type.equals("long")) {
                                pstmt.setLong(i, ((Long) value).longValue());
                            } else if (type.equals("float")) {
                                pstmt.setFloat(i, ((Float) value).floatValue());
                            } else if (type.equals("double")) {
                                pstmt.setDouble(i, ((Double) value).doubleValue());
                            } else if (type.equals("boolean")) {
                                pstmt.setBoolean(i, ((Boolean) value).booleanValue());
                            } else if (type.equals("date")) {
                                pstmt.setDate(i, (Date) value);
                            } else if (type.equals("string")) {
                                pstmt.setString(i, (String) value);
                            }
                        }
                        pstmt.addBatch();
                    }
                }
                int result = pstmt.executeBatch().length;
                pstmt.close();
                int value = result;
                if (pstmt != null) {
                    try {
                        logger.info("close open prepare statement");
                        pstmt.close();
                    } catch (SQLException ex) {
                        logger.error("close statement error:" + ex);
                    }
                }
                if (exception != null) {
                    logger.info("validate the connection now, may be it has been closed");
                    validate();
                }
                return value;
            } catch (SQLException e) {
                logger.error("insert data error");
                exception = e;
                throw e;
            } finally {
                if (pstmt != null) {
                    try {
                        logger.info("close open prepare statement");
                        pstmt.close();
                    } catch (SQLException ex) {
                        logger.error("close statement error:" + ex);
                    }
                }
                if (exception != null) {
                    logger.info("validate the connection now, may be it has been closed");
                    validate();
                }
            }
        }
    }

    public static Connection getConnection()
            throws SQLException, ConnectionException {
        logger.info("get connection from DataStore");
        check();
        synchronized (lock) {
            if (isPooling()) {
                logger.info("return connection from pool");
                return DriverManager.getConnection("jdbc:apache:commons:dbcp:pool");
            }
            return check(connection) ? connection : connection();
        }
    }

    public static void connect()
            throws ConnectionException {
        connection();
    }

    private static Connection connection()
            throws ConnectionException {
        try {
            logger.info("create new connetion");
            logger.info("connection url: " + cnString);
            logger.info("connecting ...");
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException ex) {
                logger.error("Register driver error: " + ex);
            }
            connection = DriverManager.getConnection(cnString, username, password);
            stmt = connection.createStatement();
            logger.info("connected");
            return connection;
        } catch (SQLException ex) {
            logger.error("create connection error");
            connection = null;
            throw new ConnectionException(ex.getMessage(), cnString);
        }
    }

    public static boolean check(Connection conn) {
        if (conn != null) {
            Statement cstmt = null;
            try {
                logger.info("check connection");
                logger.debug(sqlcheck);
                cstmt = conn.createStatement();
                cstmt.executeQuery(sqlcheck);
                logger.info("connection is ok");
                cstmt.close();
                cstmt = null;
                return true;
            } catch (SQLException ex) {
                logger.info("connection is broken down:" + ex);
                return false;
            } finally {
                if (cstmt != null) {
                    try {
                        cstmt.close();
                    } catch (SQLException ex) {
                    }
                }
            }
        }
        return false;
    }

    private static void check()
            throws ConnectionException {
        if (!instanced) {
            throw new ConnectionException("the DataStore hasn't been instanced", cnString);
        }
    }

    private static void validate() {
        synchronized (lock) {
            if (connection != null) {
                try {
                    logger.info("check connection");
                    logger.debug(sqlcheck);
                    stmt.executeQuery(sqlcheck);
                    logger.info("connection is ok");
                } catch (SQLException ex) {
                    logger.error("check connection error:" + ex.toString());
                    logger.error("the connection has been broken down, set it to null to recreate");

                    connection = null;
                }
            }
        }
    }

    public static boolean isPooling() {
        return pooling;
    }

    public static boolean isInstanced() {
        return instanced;
    }

    public static boolean isConnected() {
        return (isInstanced()) && (connection != null);
    }

    public static void setLogger(Logger logger) {
        logger = logger;
    }

    public static Object getLock() {
        return lock;
    }
}
