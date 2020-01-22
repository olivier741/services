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

public class AccessManegement
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private Long amId;
  private long maxConnection;
  private String description;
  private String userUpdate;
  private Date updatedTime;
  private String userCreated;
  private Date createdTime;
  private Integer status;
  private Date expiredTime;
  private Webservice webservice;
  private Client client;
  
  public AccessManegement() {}
  
  public AccessManegement(Long amId)
  {
    this.amId = amId;
  }
  
  public AccessManegement(Long amId, long maxConnection, String userUpdate, Date updatedTime, String userCreated, Date createdTime)
  {
    this.amId = amId;
    this.maxConnection = maxConnection;
    this.userUpdate = userUpdate;
    this.updatedTime = updatedTime;
    this.userCreated = userCreated;
    this.createdTime = createdTime;
  }
  
  public Long getAmId()
  {
    return this.amId;
  }
  
  public void setAmId(Long amId)
  {
    this.amId = amId;
  }
  
  public long getMaxConnection()
  {
    return this.maxConnection;
  }
  
  public void setMaxConnection(long maxConnection)
  {
    this.maxConnection = maxConnection;
  }
  
  public String getDescription()
  {
    return this.description;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public String getUserUpdate()
  {
    return this.userUpdate;
  }
  
  public void setUserUpdate(String userUpdate)
  {
    this.userUpdate = userUpdate;
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
  
  public Integer getStatus()
  {
    return this.status;
  }
  
  public void setStatus(Integer status)
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
  
  public Webservice getWebservice()
  {
    return this.webservice;
  }
  
  public void setWebservice(Webservice webservice)
  {
    this.webservice = webservice;
  }
  
  public Client getClient()
  {
    return this.client;
  }
  
  public void setClient(Client client)
  {
    this.client = client;
  }
  
  public int hashCode()
  {
    int hash = 0;
    hash += (this.amId != null ? this.amId.hashCode() : 0);
    return hash;
  }
  
  public boolean equals(Object object)
  {
    if (!(object instanceof AccessManegement)) {
      return false;
    }
    AccessManegement other = (AccessManegement)object;
    if (((this.amId == null) && (other.amId != null)) || ((this.amId != null) && (!this.amId.equals(other.amId)))) {
      return false;
    }
    return true;
  }
  
  public String toString()
  {
    return "com.viettel.bccsgw.bo.AccessManegement[amId=" + this.amId + "]";
  }
}
