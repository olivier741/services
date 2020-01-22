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
import java.util.Date;
import java.util.Set;

public class Client
{
  private static final long serialVersionUID = 1L;
  private Long clientId;
  private String clientName;
  private String username;
  private String ipAddress;
  private String userUpdated;
  private Date updatedTime;
  private String userCreated;
  private Date createdTime;
  private String description;
  private long maxRequest;
  private int status;
  private Date expiredTime;
  private int clientType;
  private String password;
  private Set<AccessManegement> accessManegementSet;
  
  public Long getClientId()
  {
    return this.clientId;
  }
  
  public void setClientId(Long clientId)
  {
    this.clientId = clientId;
  }
  
  public String getClientName()
  {
    return this.clientName;
  }
  
  public void setClientName(String clientName)
  {
    this.clientName = clientName;
  }
  
  public String getUsername()
  {
    return this.username;
  }
  
  public void setUsername(String username)
  {
    this.username = username;
  }
  
  public String getIpAddress()
  {
    return this.ipAddress;
  }
  
  public void setIpAddress(String ipAddress)
  {
    this.ipAddress = ipAddress;
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
  
  public String getDescription()
  {
    return this.description;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public long getMaxRequest()
  {
    return this.maxRequest;
  }
  
  public void setMaxRequest(long maxRequest)
  {
    this.maxRequest = maxRequest;
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
  
  public int getClientType()
  {
    return this.clientType;
  }
  
  public void setClientType(int clientType)
  {
    this.clientType = clientType;
  }
  
  public String getPassword()
  {
    return this.password;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
  }
  
  public Set<AccessManegement> getAccessManegementSet()
  {
    return this.accessManegementSet;
  }
  
  public void setAccessManegementSet(Set<AccessManegement> accessManegementSet)
  {
    this.accessManegementSet = accessManegementSet;
  }
  
  public int hashCode()
  {
    int hash = 0;
    hash += (this.clientId != null ? this.clientId.hashCode() : 0);
    return hash;
  }
  
  public boolean equals(Object object)
  {
    if (!(object instanceof Client)) {
      return false;
    }
    Client other = (Client)object;
    if (((this.clientId == null) && (other.clientId != null)) || ((this.clientId != null) && (!this.clientId.equals(other.clientId)))) {
      return false;
    }
    return true;
  }
  
  public String toString()
  {
    return "com.unitel.gateway.entity.Client[clientId=" + this.clientId + "]";
  }
}
