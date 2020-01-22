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
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class ErrorCondition
{
  public static final String TYPE_EXACTLY = "EXACTLY";
  public static final String TYPE_ATLEAST = "ATLEAST";
  public static final String TYPE_ALL = "ALL";
  public static final String LOGFIELD_LEVEL = "LOGLEVEL";
  public static final String LOGFIELD_LOGGERNAME = "LOGGERNAME";
  public static final String LOGFIELD_MESSAGE = "MESSAGE";
  public static final String LOGFIELD_THREADNAME = "THREADNAME";
  private String type;
  private String logField;
  private String value;
  private String[] values = null;
  
  public ErrorCondition() {}
  
  public ErrorCondition(String logField, String condition)
  {
    this.logField = logField;
    if (condition.startsWith("ALL"))
    {
      this.type = "ALL";
      this.value = condition.substring("ALL".length()).trim();
      this.values = this.value.split("\\|");
    }
    else if (condition.startsWith("ATLEAST"))
    {
      this.type = "ATLEAST";
      this.value = condition.substring("ATLEAST".length()).trim();
      this.values = this.value.split("\\|");
    }
    else if (condition.startsWith("EXACTLY"))
    {
      this.type = "EXACTLY";
      this.value = condition.substring("EXACTLY".length()).trim();
    }
    if (this.values != null) {
      for (int i = 0; i < this.values.length; i++) {
        this.values[i] = this.values[i].trim();
      }
    }
  }
  
  public ErrorCondition(String type, String logField, String value)
  {
    this.type = type;
    this.logField = logField;
    this.value = value;
  }
  
  public String getLogField()
  {
    return this.logField;
  }
  
  public void setLogField(String logField)
  {
    this.logField = logField;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public void setType(String type)
  {
    this.type = type;
  }
  
  public String getValue()
  {
    return this.value;
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
  
  public String toString()
  {
    return this.logField + " " + this.type + " " + this.value;
  }
  
  public boolean match(LoggingEvent loggingEvent)
  {
    if (this.logField.equals("LOGLEVEL")) {
      return this.value.contains(loggingEvent.getLevel().toString());
    }
    if (this.logField.equals("LOGGERNAME"))
    {
      String loggerName = loggingEvent.getLoggerName();
      if (loggerName == null) {
        return false;
      }
      loggerName = loggerName.trim();
      if (this.type.equals("EXACTLY")) {
        return loggerName.equals(this.value);
      }
      if (this.type.equals("ATLEAST"))
      {
        int length = this.values.length;
        for (int i = 0; i < length; i++) {
          if (loggerName.contains(this.values[i])) {
            return true;
          }
        }
        return false;
      }
      if (this.type.equals("ALL"))
      {
        int length = this.values.length;
        for (int i = 0; i < length; i++) {
          if (!loggerName.contains(this.values[i])) {
            return false;
          }
        }
        return true;
      }
      return false;
    }
    if (this.logField.equals("MESSAGE"))
    {
      String message = loggingEvent.getMessage().toString();
      if (message == null) {
        return false;
      }
      message = message.trim();
      if (this.type.equals("EXACTLY")) {
        return message.equals(this.value);
      }
      if (this.type.equals("ATLEAST"))
      {
        int length = this.values.length;
        for (int i = 0; i < length; i++) {
          if (message.contains(this.values[i])) {
            return true;
          }
        }
        return false;
      }
      if (this.type.equals("ALL"))
      {
        int length = this.values.length;
        for (int i = 0; i < length; i++) {
          if (!message.contains(this.values[i])) {
            return false;
          }
        }
        return true;
      }
      return false;
    }
    if (this.logField.equals("THREADNAME"))
    {
      String threadName = loggingEvent.getThreadName();
      if (threadName == null) {
        return false;
      }
      threadName = threadName.trim();
      if (this.type.equals("EXACTLY")) {
        return threadName.equals(this.value);
      }
      if (this.type.equals("ATLEAST"))
      {
        int length = this.values.length;
        for (int i = 0; i < length; i++) {
          if (threadName.contains(this.values[i])) {
            return true;
          }
        }
        return false;
      }
      if (this.type.equals("ALL"))
      {
        int length = this.values.length;
        for (int i = 0; i < length; i++) {
          if (!threadName.contains(this.values[i])) {
            return false;
          }
        }
        return true;
      }
      return false;
    }
    return false;
  }
}
