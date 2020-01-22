/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.warnning;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.mmserver.agent.MmJMXServerSec;
import java.text.SimpleDateFormat;
import java.util.Date;
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
  
  public String format(LoggingEvent event)
  {
    StringBuilder sms = new StringBuilder();
    sms.append(this.content);
    sms.append(". ");
    sms.append(this.appId);
    sms.append(" " + MmJMXServerSec.getIp() + ":" + MmJMXServerSec.getPort());
    sms.append(" co loi ");
    sms.append(this.errorId);
    sms.append(" luc ");
    sms.append(this.dateFomarter.format(new Date(event.getTimeStamp())));
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