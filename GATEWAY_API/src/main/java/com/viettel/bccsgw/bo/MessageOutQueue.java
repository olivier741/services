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
package com.viettel.bccsgw.bo;

/**
 *
 * @author olivier.tatsinkou
 */
public class MessageOutQueue
{
  private int messageId;
  private String message;
  private String sendStartDate;
  private int status;
  private String sendDate;
  private int retry;
  private String retryTime;
  private int logId;
  private String receiver;
  private String sender;
  private int condId;
  private String logDateTime;
  
  public int getCondId()
  {
    return this.condId;
  }
  
  public void setCondId(int condId)
  {
    this.condId = condId;
  }
  
  public int getLogId()
  {
    return this.logId;
  }
  
  public void setLogId(int logId)
  {
    this.logId = logId;
  }
  
  public String getMessage()
  {
    return this.message;
  }
  
  public void setMessage(String message)
  {
    this.message = message;
  }
  
  public int getMessageId()
  {
    return this.messageId;
  }
  
  public void setMessageId(int messageId)
  {
    this.messageId = messageId;
  }
  
  public String getReceiver()
  {
    return this.receiver;
  }
  
  public void setReceiver(String receiver)
  {
    this.receiver = receiver;
  }
  
  public int getRetry()
  {
    return this.retry;
  }
  
  public void setRetry(int retry)
  {
    this.retry = retry;
  }
  
  public String getRetryTime()
  {
    return this.retryTime;
  }
  
  public void setRetryTime(String retryTime)
  {
    this.retryTime = retryTime;
  }
  
  public String getSendDate()
  {
    return this.sendDate;
  }
  
  public void setSendDate(String sendDate)
  {
    this.sendDate = sendDate;
  }
  
  public String getSendStartDate()
  {
    return this.sendStartDate;
  }
  
  public void setSendStartDate(String sendStartDate)
  {
    this.sendStartDate = sendStartDate;
  }
  
  public String getSender()
  {
    return this.sender;
  }
  
  public void setSender(String sender)
  {
    this.sender = sender;
  }
  
  public int getStatus()
  {
    return this.status;
  }
  
  public void setStatus(int status)
  {
    this.status = status;
  }
  
  public String getLogDateTime()
  {
    return this.logDateTime;
  }
  
  public void setLogDateTime(String logDateTime)
  {
    this.logDateTime = logDateTime;
  }
}

