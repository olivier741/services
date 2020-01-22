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
package com.viettel.mmserver.warnning;

/**
 *
 * @author olivier.tatsinkou
 */
import com.viettel.mmserver.agent.JmxAgent;
import com.viettel.mmserver.agent.MmJMXServerSec;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class SMSFormater
{
  private boolean loggerName = false;
  private boolean logLevel = false;
  private boolean threadName = false;
  private String content = "";
  private int errorId = 0;
  private String appId = "";
  private SimpleDateFormat dateFomarter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
  
  public SMSFormater(ErrorDefinition ef)
  {
    if (ef != null)
    {
      String format = ef.getSmsFormat();
      if (format != null)
      {
        if (format.contains("LOGLEVEL")) {
          this.logLevel = true;
        }
        if (format.contains("LOGGERNAME")) {
          this.loggerName = true;
        }
        if (format.contains("THREADNAME")) {
          this.threadName = true;
        }
      }
      this.content = ef.getSmsContent();
      this.errorId = ef.getErrorId();
      this.appId = ef.getAppId();
    }
  }
  
  public String format(LoggingEvent event, String language)
  {
    StringBuilder sms = new StringBuilder();
    sms.append(this.content);
    sms.append(". ");
    sms.append(this.appId);
    sms.append(" ");
    sms.append(JmxAgent.getIp());
    sms.append(":");
    sms.append(MmJMXServerSec.getPort());
    if ((language != null) && (language.trim().equals("en")))
    {
      sms.append(" error ");
      sms.append(this.errorId);
      sms.append(" at ");
    }
    else
    {
      sms.append(" co loi ");
      sms.append(this.errorId);
      sms.append(" luc ");
    }
    sms.append(this.dateFomarter.format(new Date()));
    sms.append(". ");
    if (this.loggerName)
    {
      sms.append("LoggerName: ");
      sms.append(event.getLoggerName());
      sms.append(". ");
    }
    if (this.threadName)
    {
      sms.append("ThreadName: ");
      sms.append(event.getThreadName());
      sms.append(". ");
    }
    if (this.logLevel)
    {
      sms.append("LogLevel: ");
      sms.append(event.getLevel().toString());
    }
    return sms.toString().trim();
  }
}
