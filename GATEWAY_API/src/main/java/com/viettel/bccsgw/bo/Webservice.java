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
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class Webservice
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private Long wsId;
  private String wsName;
  private String wsdl;
  private String msgTemplate;
  private String description;
  private String userUpdated;
  private Date updatedTime;
  private String userCreated;
  private Date createdTime;
  private long maxConnection;
  private String wsCode;
  private int status;
  private Date expiredTime;
  private String porttype;
  private String wsUsernameTag;
  private String wsPasswordTag;
  private String wsUsername;
  private String wsPassword;
  private Integer timeout;
  private Long warningTimeLevel1;
  private Long warningTimeLevel2;
  private Long warningTimeLevel3;
  private Set<AccessManegement> accessManegementSet;
  
  public Webservice() {}
  
  public Webservice(Long wsId)
  {
    this.wsId = wsId;
  }
  
  public Webservice(Long wsId, String wsName, String wsdl, String msgTemplate, String userUpdated, Date updatedTime, String userCreated, Date createdTime, long maxConnection, String wsCode, int status, Date expiredTime, String porttype)
  {
    this.wsId = wsId;
    this.wsName = wsName;
    this.wsdl = wsdl;
    this.msgTemplate = msgTemplate;
    this.userUpdated = userUpdated;
    this.updatedTime = updatedTime;
    this.userCreated = userCreated;
    this.createdTime = createdTime;
    this.maxConnection = maxConnection;
    this.wsCode = wsCode;
    this.status = status;
    this.expiredTime = expiredTime;
    this.porttype = porttype;
  }
  
  public Long getWsId()
  {
    return this.wsId;
  }
  
  public void setWsId(Long wsId)
  {
    this.wsId = wsId;
  }
  
  public String getWsName()
  {
    return this.wsName;
  }
  
  public void setWsName(String wsName)
  {
    this.wsName = wsName;
  }
  
  public String getWsdl()
  {
    return this.wsdl;
  }
  
  public void setWsdl(String wsdl)
  {
    this.wsdl = wsdl;
  }
  
  public String getMsgTemplate()
  {
    return this.msgTemplate;
  }
  
  public void setMsgTemplate(String msgTemplate)
  {
    this.msgTemplate = msgTemplate;
  }
  
  public String getDescription()
  {
    return this.description;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public String getUserUpdated()
  {
    return this.userUpdated;
  }
  
  public void setUserUpdated(String userUpdated)
  {
    this.userUpdated = userUpdated;
  }
  
  public Date getUpdatedTime()
  {
    return this.updatedTime;
  }
  
  public void setUpdatedTime(Date updatedTime)
  {
    this.updatedTime = updatedTime;
  }
  
  public String getUserCreated()
  {
    return this.userCreated;
  }
  
  public void setUserCreated(String userCreated)
  {
    this.userCreated = userCreated;
  }
  
  public Date getCreatedTime()
  {
    return this.createdTime;
  }
  
  public void setCreatedTime(Date createdTime)
  {
    this.createdTime = createdTime;
  }
  
  public long getMaxConnection()
  {
    return this.maxConnection;
  }
  
  public void setMaxConnection(long maxConnection)
  {
    this.maxConnection = maxConnection;
  }
  
  public String getWsCode()
  {
    return this.wsCode;
  }
  
  public void setWsCode(String wsCode)
  {
    this.wsCode = wsCode;
  }
  
  public int getStatus()
  {
    return this.status;
  }
  
  public void setStatus(int status)
  {
    this.status = status;
  }
  
  public Date getExpiredTime()
  {
    return this.expiredTime;
  }
  
  public void setExpiredTime(Date expiredTime)
  {
    this.expiredTime = expiredTime;
  }
  
  public String getPorttype()
  {
    return this.porttype;
  }
  
  public void setPorttype(String porttype)
  {
    this.porttype = porttype;
  }
  
  public String getWsUsernameTag()
  {
    return this.wsUsernameTag;
  }
  
  public void setWsUsernameTag(String wsUsernameTag)
  {
    this.wsUsernameTag = wsUsernameTag;
  }
  
  public String getWsPasswordTag()
  {
    return this.wsPasswordTag;
  }
  
  public void setWsPasswordTag(String wsPasswordTag)
  {
    this.wsPasswordTag = wsPasswordTag;
  }
  
  public String getWsUsername()
  {
    return this.wsUsername;
  }
  
  public void setWsUsername(String wsUsername)
  {
    this.wsUsername = wsUsername;
  }
  
  public String getWsPassword()
  {
    return this.wsPassword;
  }
  
  public void setWsPassword(String wsPassword)
  {
    this.wsPassword = wsPassword;
  }
  
  public Set<AccessManegement> getAccessManegementSet()
  {
    return this.accessManegementSet;
  }
  
  public void setAccessManegementSet(Set<AccessManegement> accessManegementSet)
  {
    this.accessManegementSet = accessManegementSet;
  }
  
  public Integer getTimeout()
  {
    return this.timeout;
  }
  
  public void setTimeout(Integer timeout)
  {
    this.timeout = timeout;
  }
  
  public Long getWarningTimeLevel1()
  {
    return this.warningTimeLevel1;
  }
  
  public void setWarningTimeLevel1(Long warningTimeLevel1)
  {
    this.warningTimeLevel1 = warningTimeLevel1;
  }
  
  public Long getWarningTimeLevel2()
  {
    return this.warningTimeLevel2;
  }
  
  public void setWarningTimeLevel2(Long warningTimeLevel2)
  {
    this.warningTimeLevel2 = warningTimeLevel2;
  }
  
  public Long getWarningTimeLevel3()
  {
    return this.warningTimeLevel3;
  }
  
  public void setWarningTimeLevel3(Long warningTimeLevel3)
  {
    this.warningTimeLevel3 = warningTimeLevel3;
  }
  
  public int hashCode()
  {
    int hash = 0;
    hash += (this.wsId != null ? this.wsId.hashCode() : 0);
    return hash;
  }
  
  public boolean equals(Object object)
  {
    if (!(object instanceof Webservice)) {
      return false;
    }
    Webservice other = (Webservice)object;
    if (((this.wsId == null) && (other.wsId != null)) || ((this.wsId != null) && (!this.wsId.equals(other.wsId)))) {
      return false;
    }
    return true;
  }
  
  public String toString()
  {
    return "com.viettel.bccsgw.bo.Webservice[wsId=" + this.wsId + "]";
  }
}
