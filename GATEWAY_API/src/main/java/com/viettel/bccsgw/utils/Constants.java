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
public class Constants
{
  public static final String WS_CONFIG_FILE = "../conf/system.properties";
  public static final int RESULT_CODE_SUCCESS = 1;
  public static final int RESULT_CODE_FAIL = -1;
  public static final int RESULT_CODE_PROCESSING = 0;
  public static final String OK = "OK";
  public static final int INTERNET_CLIENT = 1;
  public static final int LOCAL_CLIENT = 0;
  public static final int ACTIVE = 1;
  public static final int INACTIVE = 0;
  public static final String PREFIX_ERROR = "ERROR:";
  public static final int REQUEST_TYPE_GET_REQUEST = 1;
  public static final int REQUEST_TYPE_VALIDATE = 2;
  public static final int REQUEST_TYPE_WAIT = 3;
  public static final int REQUEST_TYPE_EXECUTE = 4;
  public static final int REQUEST_TYPE_FINISH = 5;
  public static final String SPLITER = "#@s@#";
  public static final String REQUEST_ERROR = "GWERROR";
  public static final String REQUEST_TIMEOUT = "GWTIMEOUT";
  public static final String REQUEST_REJECT = "GWREJECT";
  public static final boolean INSERT = true;
  public static final String WS_CODE_LOAD_DATABASE = "BCCSReloadDB";
  public static final String WS_CODE_LOAD_PROPERTIES_FILE = "BCCSReloadProperties";
  public static final String END_LINE_REPLACE_STRING = "#@n@#";
  public static final String TAB_REPLACE_STRING = "#@t@#";
  public static final String SPLIT_FIELD_STRING = "@@@";
  public static final int STATUS_NORMAL = 1;
  public static final String TAG_GT = "#gt#";
  public static final String TAG_LT = "#lt#";
  public static final String ENCODE_LT = "&lt;";
  public static final String ENCODE_GT = "&gt;";
  public static final String INFO_SYSTEM_STOP = "INFO_SYSTEM_STOP:This thread is stopped ";
  public static final String INFO_SYSTEM_START = "INFO_SYSTEM_START:This thread is started ";
  public static final String subPathRequest = "/bccsgw_log";
  public static final String subPathActionLogWs = "/action_log_ws";
  public static final String LOG_WRITER_CONF = "../conf/log_writer.cfg";
}

