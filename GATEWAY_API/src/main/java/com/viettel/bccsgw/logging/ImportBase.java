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
package com.viettel.bccsgw.logging;

/**
 *
 * @author olivier.tatsinkou
 */
import com.viettel.bccsgw.dao.DatabaseConnection;
import com.viettel.mmserver.base.ProcessThreadMX;
import com.viettel.mmserver.config.ComboBoxParam;
import com.viettel.mmserver.config.Configuration;
import com.viettel.mmserver.config.NumberFormatter;
import com.viettel.mmserver.config.NumberFormatter.Type;
import com.viettel.mmserver.config.Param;
import com.viettel.mmserver.config.TextParam;
import com.viettel.mmserver.config.TextParam.TextType;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public abstract class ImportBase
        extends ProcessThreadMX {

    protected Connection mcnMain;
    private boolean isStop = true;
    protected long numberRecordCommit = 200L;
    protected long MAX_FILE_SIZE = 20100000L;
    protected int totalCommit = 0;
    protected int total = 0;
    protected int totalErr = 0;
    protected String replacedStringForEndLine = "#@n@#";
    protected String replacedStringForTab = "#@t@#";
    private String fileConfig;
    private long threadSleep;
    protected String tempFolder = "";
    protected String backupFolder = "";
    protected String backupStyle = "";
    protected String errorFolder = "";
    private String wildcardFile = "";
    protected String eofSymbol = "";
    protected String eorSymbol = "";
    protected String wsCodeIgnore = "";
    protected String logFilePath = "";
    protected Map<String, String> irgnoreCodeMap = new HashMap();

    public ImportBase() {
        super(ImportBase.class.getSimpleName());
    }

    public ImportBase(String theadName, String fileConfig, String logPath) {
        super(theadName);
        try {
            registerAgent("Import:name=" + this.threadName);
            this.fileConfig = fileConfig;
            this.logFilePath = logPath;
            initConfigFile();
        } catch (Exception ex) {
            this.logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    protected void initConfigFile() {
        try {
            Properties defaultProps = new Properties();
            FileInputStream in = new FileInputStream(this.fileConfig);
            defaultProps.load(in);
            in.close();
            if ((defaultProps.getProperty("LogFilePath") != null) && (!defaultProps.getProperty("LogFilePath").equals(""))) {
                this.logFilePath = defaultProps.getProperty("LogFilePath");
            }
            this.tempFolder = defaultProps.getProperty("TempFolder");
            if ((this.tempFolder == null) || (this.tempFolder.equals(""))) {
                this.tempFolder = ("../importLog/" + this.threadName + "/temp");
            }
            this.backupFolder = defaultProps.getProperty("BackupFolder");
            if ((this.backupFolder == null) || (this.backupFolder.equals(""))) {
                this.backupFolder = ("../importLog/" + this.threadName + "/backup");
            }
            this.backupStyle = defaultProps.getProperty("BackupStyle");
            if ((this.backupStyle == null) || (this.backupStyle.equals(""))) {
                this.backupStyle = "Daily";
            }
            this.errorFolder = defaultProps.getProperty("ErrorFolder");
            if ((this.errorFolder == null) || (this.errorFolder.equals(""))) {
                this.errorFolder = ("../importLog/" + this.threadName + "/error");
            }
            this.wildcardFile = defaultProps.getProperty("Wildcard");
            if ((this.wildcardFile == null) || (this.wildcardFile.equals(""))) {
                this.wildcardFile = "*.*";
            }
            this.eofSymbol = defaultProps.getProperty("EOFSymbol");
            this.eorSymbol = defaultProps.getProperty("EORSymbol");
            this.wsCodeIgnore = defaultProps.getProperty("WsCodeIgnore");
            this.replacedStringForEndLine = defaultProps.getProperty("ReplacedStringForEndLine");
            this.replacedStringForTab = defaultProps.getProperty("ReplacedStringForTab");
            this.numberRecordCommit = Long.valueOf(defaultProps.getProperty("NumberOfCommit")).longValue();
            this.threadSleep = Long.valueOf(defaultProps.getProperty("ThreadSleep")).longValue();
        } catch (Exception ex) {
            this.logger.error("Can not load file config.properties");
            this.numberRecordCommit = 200L;
        }
    }

    protected void prepareStart() {
        super.prepareStart();
    }

    protected void process() {
        try {
            if (!this.isStop) {
                this.total = 0;
                this.totalCommit = 0;
                this.totalErr = 0;
                if (this.mcnMain == null) {
                    this.mcnMain = DatabaseConnection.openConnection("../conf/dbLog.properties");
                    this.logger.info("Connected database!");
                }
                processImportDB();
                if ((this.total > 0) || (this.totalErr > 0)) {
                    this.logger.info("<html><font color=green>Total record error: " + this.totalErr + "</font></html>");
                    this.logger.info("<html><font color=green>Total record imported: " + this.total + "</font></html>");
                }
                this.logger.debug("Thread sleep in " + this.threadSleep + " ms");
                Thread.sleep(this.threadSleep);
            } else {
                super.stop();
            }
        } catch (Exception ex) {
            this.logger.error(ex, ex);
            super.stop();
        }
    }

    protected abstract String getSqlInsert();

    protected void processImportDB() {
        if ((this.logFilePath == null) || (this.logFilePath.equals(""))) {
            this.logger.error("Cannot find log folder");
            return;
        }
        if ((this.tempFolder == null) || (this.tempFolder.equals(""))) {
            this.logger.error("Temp folder is null");
            return;
        }
        File file = new File(this.tempFolder);
        if ((!file.exists())
                && (!file.mkdirs())) {
            this.logger.error("Create folder " + this.tempFolder + " error!");
        }
        if ((this.backupFolder == null) || (this.backupFolder.equals(""))) {
            this.logger.error("BackupFolder folder is null");
        } else {
            file = new File(this.backupFolder);
            if ((!file.exists())
                    && (!file.mkdirs())) {
                this.logger.error("Create folder " + this.backupFolder + " error!");
            }
        }
        if ((this.errorFolder == null) || (this.errorFolder.equals(""))) {
            this.logger.error("ErrorFolder folder is null");
        } else {
            file = new File(this.errorFolder);
            if ((!file.exists())
                    && (!file.mkdirs())) {
                this.logger.error("Create folder " + this.errorFolder + " error!");
            }
        }
        if ((this.logFilePath != null) && (!this.logFilePath.endsWith(File.separator))) {
            this.logFilePath += File.separatorChar;
        }
        if ((this.tempFolder != null) && (!this.tempFolder.endsWith(File.separator))) {
            this.tempFolder += File.separatorChar;
        }
        if (!this.backupFolder.endsWith(File.separator)) {
            this.backupFolder += File.separatorChar;
        }
        if (!this.errorFolder.endsWith(File.separator)) {
            this.errorFolder += File.separatorChar;
        }
        PreparedStatement stmt = null;
        try {
            this.mcnMain.setAutoCommit(false);

            stmt = this.mcnMain.prepareStatement(getSqlInsert());
            File fl = new File(this.logFilePath);
            String[] fileList = fl.list();
            if ((fileList == null) || (fileList.length == 0)) {
                return;
            }
            this.logger.info("Total file need process: " + fileList.length);
            Arrays.sort(fileList);
            String fileName = "";
            Pattern fileWildcard = null;
            try {
                fileWildcard = Pattern.compile(this.wildcardFile);
            } catch (Exception ex) {
                this.logger.error("Wildcard file error " + ex.getMessage());
                stop();
            }
            for (int i = 0; i < fileList.length; i++) {
                fileName = fileList[i];
                Matcher matcher = fileWildcard.matcher(fileName);
                if (matcher.matches()) {
                    readFile(fileName, stmt);
                    if (this.totalCommit > 0) {
                        stmt.executeBatch();
                        this.mcnMain.commit();
                        stmt.clearBatch();
                        this.totalCommit = 0;
                    }
                }
            }
        } catch (Exception ex) {
            this.logger.error("error when process: " + ex.getMessage());
            this.logger.error(ex, ex);
            ex.printStackTrace();
            try {
                stmt.clearBatch();
                this.mcnMain.rollback();
            } catch (SQLException ex1) {
                this.logger.error("Error rollback statement" + ex1);
            }
        } finally {
            closeObject(stmt);
        }
    }

    protected abstract void readFile(String paramString, PreparedStatement paramPreparedStatement)
            throws Exception;

    public void stop() {
        this.logger.info("INFO_SYSTEM_STOP:This thread is stopped ");
        this.isStop = true;
        try {
            if (this.mcnMain != null) {
                this.mcnMain.close();
                this.mcnMain = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            this.logger.error("Error stop" + ex);
        }
        super.stop();
    }

    public void start() {
        this.logger.info("INFO_SYSTEM_START:This thread is started ");
        this.isStop = false;
        super.start();
    }

    public void restart() {
        stop();
        start();
    }

    public Configuration loadConfig() {
        Configuration c = new Configuration();
        initConfigFile();
        c.addParam(new TextParam("LogFilePath", this.logFilePath, TextParam.TextType.TEXT_FIELD));
        c.addParam(new TextParam("TempFolder", this.tempFolder, TextParam.TextType.TEXT_FIELD));
        c.addParam(new TextParam("BackupFolder", this.backupFolder, TextParam.TextType.TEXT_FIELD));
        c.addParam(new ComboBoxParam("BackupStyle", this.backupStyle, new String[]{"Daily", "Monthly", "Yearly", "Directly"}));
        c.addParam(new TextParam("ErrorFolder", this.errorFolder, TextParam.TextType.TEXT_FIELD));
        c.addParam(new TextParam("Wildcard", this.wildcardFile, TextParam.TextType.TEXT_FIELD));
        c.addParam(new TextParam("EOFSymbol", this.eofSymbol, TextParam.TextType.TEXT_FIELD));
        c.addParam(new TextParam("EORSymbol", this.eorSymbol, TextParam.TextType.TEXT_FIELD));
        c.addParam(new TextParam("WsCodeIgnore", this.wsCodeIgnore, TextParam.TextType.TEXT_FIELD));
        c.addParam(new TextParam("NumberOfCommit", String.valueOf(this.numberRecordCommit), TextParam.TextType.TEXT_FIELD, new NumberFormatter(NumberFormatter.Type.Integer)));
        c.addParam(new TextParam("ReplacedStringForEndLine", this.replacedStringForEndLine, TextParam.TextType.TEXT_FIELD));
        c.addParam(new TextParam("ReplacedStringForTab", this.replacedStringForTab, TextParam.TextType.TEXT_FIELD));
        c.addParam(new TextParam("ThreadSleep", String.valueOf(this.threadSleep), TextParam.TextType.TEXT_FIELD, new NumberFormatter(NumberFormatter.Type.Integer)));
        return c;
    }

    public boolean saveConfig(Configuration newConfig) {
        try {
            Properties outProperties = new Properties();
            if ((newConfig.getParamByName("LogFilePath").getValue() != null) && (!newConfig.getParamByName("LogFilePath").getValue().equals(""))) {
                this.logFilePath = (newConfig.getParamByName("LogFilePath").getValue() != null ? newConfig.getParamByName("LogFilePath").getValue() : "");
            }
            outProperties.setProperty("LogFilePath", String.valueOf(this.logFilePath));

            this.tempFolder = (newConfig.getParamByName("TempFolder").getValue() != null ? newConfig.getParamByName("TempFolder").getValue() : "");
            outProperties.setProperty("TempFolder", String.valueOf(this.tempFolder));

            this.backupFolder = (newConfig.getParamByName("BackupFolder").getValue() != null ? newConfig.getParamByName("BackupFolder").getValue() : "");
            outProperties.setProperty("BackupFolder", String.valueOf(this.backupFolder));

            this.backupStyle = (newConfig.getParamByName("BackupStyle").getValue() != null ? newConfig.getParamByName("BackupStyle").getValue() : "");
            outProperties.setProperty("BackupStyle", String.valueOf(this.backupStyle));

            this.errorFolder = (newConfig.getParamByName("ErrorFolder").getValue() != null ? newConfig.getParamByName("ErrorFolder").getValue() : "");
            outProperties.setProperty("ErrorFolder", String.valueOf(this.errorFolder));

            this.wildcardFile = (newConfig.getParamByName("Wildcard").getValue() != null ? newConfig.getParamByName("Wildcard").getValue() : "");
            outProperties.setProperty("Wildcard", String.valueOf(this.wildcardFile));

            this.eofSymbol = (newConfig.getParamByName("EOFSymbol").getValue() != null ? newConfig.getParamByName("EOFSymbol").getValue() : "");
            outProperties.setProperty("EOFSymbol", String.valueOf(this.eofSymbol));

            this.eorSymbol = (newConfig.getParamByName("EORSymbol").getValue() != null ? newConfig.getParamByName("EORSymbol").getValue() : "");
            outProperties.setProperty("EORSymbol", String.valueOf(this.eorSymbol));

            this.wsCodeIgnore = (newConfig.getParamByName("WsCodeIgnore").getValue() != null ? newConfig.getParamByName("WsCodeIgnore").getValue() : "");
            outProperties.setProperty("WsCodeIgnore", String.valueOf(this.wsCodeIgnore));

            this.replacedStringForEndLine = (newConfig.getParamByName("ReplacedStringForEndLine").getValue() != null ? newConfig.getParamByName("ReplacedStringForEndLine").getValue() : "");
            outProperties.setProperty("ReplacedStringForEndLine", String.valueOf(this.replacedStringForEndLine));

            this.replacedStringForTab = (newConfig.getParamByName("ReplacedStringForTab").getValue() != null ? newConfig.getParamByName("ReplacedStringForTab").getValue() : "");
            outProperties.setProperty("ReplacedStringForTab", String.valueOf(this.replacedStringForTab));

            this.numberRecordCommit = Long.valueOf(newConfig.getParamByName("NumberOfCommit").getValue() != null ? newConfig.getParamByName("NumberOfCommit").getValue() : "100").longValue();
            outProperties.setProperty("NumberOfCommit", String.valueOf(this.numberRecordCommit));

            this.threadSleep = Long.valueOf(newConfig.getParamByName("ThreadSleep").getValue() != null ? newConfig.getParamByName("ThreadSleep").getValue() : "600000").longValue();
            outProperties.setProperty("ThreadSleep", String.valueOf(this.threadSleep));

            FileOutputStream out = new FileOutputStream(this.fileConfig);
            outProperties.store(out, "Update config file");
            out.close();
        } catch (Exception ex) {
            this.logger.error(ex, ex);
            return false;
        }
        return true;
    }

    public static void closeObject(Connection obj) {
        try {
            if ((obj != null)
                    && (!obj.isClosed())) {
                if (!obj.getAutoCommit()) {
                    obj.rollback();
                }
                obj.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeObject(CallableStatement obj) {
        try {
            if (obj != null) {
                obj.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeObject(Statement obj) {
        try {
            if (obj != null) {
                obj.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeObject(ResultSet obj) {
        try {
            if (obj != null) {
                obj.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeObject(PreparedStatement obj) {
        try {
            if (obj != null) {
                obj.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void safeClose(FileInputStream is) {
        try {
            is.close();
        } catch (Exception ex) {
            is = null;
        }
    }

    protected static void safeClose(PrintWriter is) {
        try {
            is.close();
        } catch (Exception ex) {
            is = null;
        }
    }

    protected static void safeClose(FileOutputStream is) {
        try {
            is.close();
        } catch (Exception ex) {
            is = null;
        }
    }

    protected static void safeClose(BufferedInputStream is) {
        try {
            is.close();
        } catch (Exception ex) {
            is = null;
        }
    }
}
