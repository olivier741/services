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
import java.util.ArrayList;
import java.util.Date;
import org.apache.log4j.spi.LoggingEvent;

public class ErrorDefinition
{
  public static final int MILISECONDOFONEMINUTE = 60000;
  private int errorId;
  private String appId;
  private String userName;
  private Date createTime;
  private boolean useFlag;
  private ArrayList<ErrorCondition> errorConditions;
  private String smsFormat;
  private String smsContent;
  private SMSFormater smsFormater = null;
  private boolean sendSMSFlag = false;
  private int numOfErrorOfErrorFrequence = 1;
  private int minutesOfErrorFrequence = 0;
  public static final String SMS_APPID = "APPID";
  public static final String SMS_ERRORID = "ERRORID";
  public static final String SMS_THREAD = "THREAD";
  public static final String SMS_LOGGERNAME = "LOGGERNAME";
  private ArrayList<Long> errorTimeTracking = new ArrayList();
  private long errorInteval;
  
  public String getAppId()
  {
    if (this.appId == null) {
      return "Unkown";
    }
    return this.appId;
  }
  
  public void setAppId(String appId)
  {
    this.appId = appId;
  }
  
  public Date getCreateTime()
  {
    return this.createTime;
  }
  
  public void setCreateTime(Date createTime)
  {
    this.createTime = createTime;
  }
  
  public ArrayList<ErrorCondition> getErrorConditions()
  {
    return this.errorConditions;
  }
  
  public void setErrorConditions(ArrayList<ErrorCondition> errorConditions)
  {
    this.errorConditions = errorConditions;
  }
  
  public int getErrorId()
  {
    return this.errorId;
  }
  
  public void setErrorId(int errorId)
  {
    this.errorId = errorId;
  }
  
  public String getSmsContent()
  {
    if (this.smsContent != null) {
      return this.smsContent;
    }
    return "";
  }
  
  public void setSmsContent(String smsContent)
  {
    this.smsContent = smsContent;
  }
  
  public String getSmsFormat()
  {
    return this.smsFormat;
  }
  
  public void setSmsFormat(String smsFormat)
  {
    this.smsFormat = smsFormat;
  }
  
  public boolean isUseFlag()
  {
    return this.useFlag;
  }
  
  public void setUseFlag(boolean useFlag)
  {
    this.useFlag = useFlag;
  }
  
  public String getUserName()
  {
    return this.userName;
  }
  
  public void setUserName(String userName)
  {
    this.userName = userName;
  }
  
  public int getMinutesOfErrorFrequence()
  {
    return this.minutesOfErrorFrequence;
  }
  
  public void setMinutesOfErrorFrequence(int iminutesOfErrorFrequence)
  {
    this.minutesOfErrorFrequence = iminutesOfErrorFrequence;
    this.errorInteval = (iminutesOfErrorFrequence * 60000);
  }
  
  public int getNumOfErrorOfErrorFrequence()
  {
    return this.numOfErrorOfErrorFrequence;
  }
  
  public void setNumOfErrorOfErrorFrequence(int inumOfErrorOfErrorFrequence)
  {
    this.numOfErrorOfErrorFrequence = inumOfErrorOfErrorFrequence;
  }
  
  public boolean isSendSMSFlag()
  {
    return this.sendSMSFlag;
  }
  
  public void setSendSMSFlag(boolean sendSMSFlag)
  {
    this.sendSMSFlag = sendSMSFlag;
  }
  
  public ErrorDefinition(int errorId, String appId, String userName, Date createTime, boolean useFlag, ArrayList<ErrorCondition> errorConditions, String smsFormat, String smsContent, boolean sendSMSFlag)
  {
    this.errorId = errorId;
    this.appId = appId;
    this.userName = userName;
    this.createTime = createTime;
    this.useFlag = useFlag;
    this.errorConditions = errorConditions;
    this.smsFormat = smsFormat;
    this.smsContent = smsContent;
    this.sendSMSFlag = sendSMSFlag;
    this.smsFormater = new SMSFormater(this);
  }
  
  public String getSMS(LoggingEvent event, String language)
  {
    if (this.smsFormater != null) {
      return this.smsFormater.format(event, language);
    }
    return "";
  }
  
  public String getDescription()
  {
    StringBuilder str = new StringBuilder();
    str.append("<html>");
    if (this.errorConditions != null) {
      for (ErrorCondition errorCondition : this.errorConditions) {
        str.append(System.getProperty("line.separator") + errorCondition.toString() + "</br>");
      }
    }
    if (this.smsFormat != null) {
      str.append(this.smsFormat);
    }
    if (this.smsContent != null) {
      str.append(this.smsContent);
    }
    str.append("</html>");
    return str.toString();
  }
  
  public boolean match(LoggingEvent loggingEvent)
  {
    int size = this.errorConditions.size();
    if (size == 0) {
      return false;
    }
    ErrorCondition ec = null;
    for (int i = 0; i < size; i++)
    {
      ec = (ErrorCondition)this.errorConditions.get(i);
      if (!ec.match(loggingEvent)) {
        return false;
      }
    }
    if (this.numOfErrorOfErrorFrequence <= 1) {
      return true;
    }
    if (this.errorTimeTracking.size() < this.numOfErrorOfErrorFrequence)
    {
      this.errorTimeTracking.add(Long.valueOf(loggingEvent.getTimeStamp()));
    }
    else
    {
      this.errorTimeTracking.remove(0);
      this.errorTimeTracking.add(Long.valueOf(loggingEvent.getTimeStamp()));
    }
    if (this.errorTimeTracking.size() < this.numOfErrorOfErrorFrequence) {
      return false;
    }
    if (((Long)this.errorTimeTracking.get(this.numOfErrorOfErrorFrequence - 1)).longValue() - ((Long)this.errorTimeTracking.get(0)).longValue() <= this.errorInteval)
    {
      this.errorTimeTracking = new ArrayList();
      return true;
    }
    return false;
  }
}

