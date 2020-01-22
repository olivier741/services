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
import com.viettel.bccsgw.utils.DateTimeUtils;
import com.viettel.bccsgw.utils.FileUtils;
import com.viettel.bccsgw.utils.ResourceBundleUtils;
import com.viettel.bccsgw.utils.StringUtils;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.apache.log4j.Logger;

public class ImportActionLogWSThread
  extends ImportBase
{
  public ImportActionLogWSThread(String threadName, String config, String logPath)
  {
    super(threadName, config, logPath);
  }
  
  protected String getSqlInsert()
  {
    String sql = "INSERT INTO " + ResourceBundleUtils.getResource("schema") + ".ACTION_LOG_WS (APP_NAME, TRANS_ID, WS_CODE, REQUEST, IP_LOCAL, RESPONSE, " + " START_DATE, END_DATE, DURATION, EXCEPTION, CREATED, ACTION_WS_ID) " + " VALUES (?, ?,?,?,?,?,to_timestamp(?,'dd/MM/yyyy HH24:mi:ss.FF3'), " + " to_timestamp(?,'dd/MM/yyyy HH24:mi:ss.FF3'), ?,?,sysdate," + ResourceBundleUtils.getResource("schema") + ".ACTION_LOG_WS_SEQ.NEXTVAL)";
    


    return sql;
  }
  
  protected void readFile(String fileName, PreparedStatement stmt)
    throws Exception
  {
    Vector vtData = StringUtils.toStringVetor(this.wsCodeIgnore, ",");
    if ((vtData != null) && (vtData.size() > 0)) {
      for (int i = 0; i < vtData.size(); i++) {
        if ((vtData.get(i) != null) && (!vtData.get(i).equals(""))) {
          this.irgnoreCodeMap.put(vtData.get(i).toString().toUpperCase(), vtData.get(i).toString());
        }
      }
    } else {
      this.irgnoreCodeMap = new HashMap();
    }
    long firstLength = new File(this.logFilePath + fileName).length();
    Thread.sleep(300L);
    long lastLength = new File(this.logFilePath + fileName).length();
    if (firstLength != lastLength)
    {
      this.logger.error("Length of file " + this.logFilePath + fileName + " is changing, ignore file now");
      return;
    }
    if (firstLength == 0L)
    {
      this.logger.error("File " + this.logFilePath + fileName + " is empty, ignore file now");
      return;
    }
    if (firstLength > this.MAX_FILE_SIZE)
    {
      this.logger.error("File " + this.logFilePath + fileName + " is large than 20M, ignore file now");
      return;
    }
    String extendFileTemp = DateTimeUtils.convertDateToString(new Date(), "yyyyMMddHHmmss") + "_";
    

    File fileProcess = new File(this.logFilePath + fileName);
    if ((fileProcess.isFile()) && (!fileProcess.renameTo(new File(this.tempFolder + extendFileTemp + fileName))))
    {
      this.logger.error("Move file " + fileName + " to temp folder fail, ignore file now.");
      return;
    }
    int errorCount = 0;
    FileInputStream is = null;
    BufferedInputStream bis = null;
    FileOutputStream os = null;
    PrintWriter pw = null;
    String strLine = "";
    try
    {
      is = new FileInputStream(this.tempFolder + extendFileTemp + fileName);
      bis = new BufferedInputStream(is);
      os = new FileOutputStream(this.tempFolder + extendFileTemp + fileName + ".error");
      pw = new PrintWriter(os);
      if (bis.available() <= 0) {
        return;
      }
      String record = null;
      BufferedReader br = new BufferedReader(new InputStreamReader(bis));
      while ((record = br.readLine()) != null)
      {
        String[] arrayRecord = record.split(this.eorSymbol);
        if ((arrayRecord != null) && (arrayRecord.length >= 1)) {
          for (int i = 0; i < arrayRecord.length; i++) {
            if ((arrayRecord[i] != null) && (!arrayRecord[i].equals("")))
            {
              String[] strArr = arrayRecord[i].split(this.eofSymbol);
              if (strArr != null) {
                if ((strArr.length < 7) && (strArr.length > 1))
                {
                  pw.println(arrayRecord[i]);
                  errorCount++;
                  this.totalErr += 1;
                }
                else
                {
                  String appName = strArr[0] != null ? strArr[0] : "";
                  String transId = strArr[1];
                  String wsCode = strArr[2];
                  String strRequest = strArr[3];
                  String strIpLocal = strArr[4];
                  String strResponse = strArr[5];
                  String strStartDate = strArr[6];
                  String strEndDate = strArr[7];
                  Long duration = Long.valueOf((strArr[8] != null) && (!strArr[8].equals("")) ? Long.valueOf(strArr[8]).longValue() : 0L);
                  String strException = strArr.length > 9 ? strArr[9] : "";
                  if ((wsCode == null) || (wsCode.equals("")) || 
                    (this.irgnoreCodeMap.get(wsCode.toUpperCase()) == null))
                  {
                    strRequest = strRequest != null ? strRequest.replaceAll(this.replacedStringForEndLine, "\n").replaceAll(this.replacedStringForTab, "\t") : "";
                    if (strRequest.length() >= 4000) {
                      strRequest = strRequest.substring(0, 1999);
                    }
                    strResponse = strResponse != null ? strResponse.replaceAll(this.replacedStringForEndLine, "\n").replaceAll(this.replacedStringForTab, "\t") : "";
                    if (strResponse.length() >= 4000) {
                      strResponse = strResponse.substring(0, 1999);
                    }
                    if (strException.length() > 4000) {
                      strException = strException.substring(0, 1999);
                    }
                    stmt.setString(1, appName);
                    stmt.setString(2, transId);
                    stmt.setString(3, wsCode);
                    stmt.setString(4, strRequest);
                    stmt.setString(5, strIpLocal);
                    stmt.setString(6, strResponse);
                    stmt.setString(7, strStartDate);
                    stmt.setString(8, strEndDate);
                    stmt.setLong(9, duration.longValue());
                    stmt.setString(10, strException);
                    stmt.addBatch();
                    try
                    {
                      if (this.totalCommit > this.numberRecordCommit)
                      {
                        int[] result = stmt.executeBatch();
                        this.mcnMain.commit();
                        this.logger.info("<html><font color=green> <ActionLogWS>" + (result.length + 1) + " record inserted into database!</font></html>");
                        stmt.clearBatch();
                        this.totalCommit = 0;
                      }
                      this.totalCommit += 1;
                      this.total += 1;
                    }
                    catch (Exception ex)
                    {
                      this.logger.error(ex, ex);
                      this.totalCommit = 0;
                      stmt.clearBatch();
                      this.mcnMain.rollback();
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      this.totalCommit = 0;
      stmt.clearBatch();
      this.mcnMain.rollback();
      this.logger.error("Error when read file " + fileName + "\n " + strLine);
      this.logger.error(ex, ex);
    }
    finally
    {
      safeClose(pw);
      safeClose(os);
      safeClose(bis);
      safeClose(is);
    }
    String dateTimeFile = DateTimeUtils.convertDateToString(new Date(), "ddMMyyyy-HHmmss");
    if ((this.backupStyle != null) && (this.backupStyle.length() > 0) && (!this.backupStyle.equalsIgnoreCase("Delete"))) {
      FileUtils.backup(this.tempFolder, this.backupFolder, extendFileTemp + fileName, fileName, this.backupStyle, "yyyyMMdd");
    }
    if ((fileName != null) && (!fileName.equals("")))
    {
      File file = new File(this.tempFolder + extendFileTemp + fileName);
      if ((file.exists()) && 
        (!file.delete())) {
        this.logger.warn("Can not delete file " + extendFileTemp + this.tempFolder + fileName);
      }
    }
    this.logger.info("Process done file " + fileName);
    if (errorCount != 0) {
      FileUtils.backup(this.tempFolder, this.errorFolder, extendFileTemp + fileName + ".error", fileName + ".error" + "." + dateTimeFile, this.backupStyle, "yyyyMMdd");
    }
    File fileError = new File(this.tempFolder + extendFileTemp + fileName + ".error");
    if ((fileError.exists()) && 
      (!fileError.delete())) {
      this.logger.warn("Can not delete file " + this.tempFolder + extendFileTemp + fileName + ".error");
    }
  }
}
