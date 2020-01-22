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

public class ErrorCode
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private Long errorCodeId;
  private String errorCode;
  private String description;
  
  public ErrorCode() {}
  
  public ErrorCode(Long errorCodeId)
  {
    this.errorCodeId = errorCodeId;
  }
  
  public ErrorCode(Long errorCodeId, String description)
  {
    this.errorCodeId = errorCodeId;
    this.description = description;
  }
  
  public Long getErrorCodeId()
  {
    return this.errorCodeId;
  }
  
  public void setErrorCodeId(Long errorCodeId)
  {
    this.errorCodeId = errorCodeId;
  }
  
  public String getErrorCode()
  {
    return this.errorCode;
  }
  
  public void setErrorCode(String errorCode)
  {
    this.errorCode = errorCode;
  }
  
  public String getDescription()
  {
    return this.description;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public int hashCode()
  {
    int hash = 0;
    hash += (this.errorCodeId != null ? this.errorCodeId.hashCode() : 0);
    return hash;
  }
  
  public boolean equals(Object object)
  {
    if (!(object instanceof ErrorCode)) {
      return false;
    }
    ErrorCode other = (ErrorCode)object;
    if (((this.errorCodeId == null) && (other.errorCodeId != null)) || ((this.errorCodeId != null) && (!this.errorCodeId.equals(other.errorCodeId)))) {
      return false;
    }
    return true;
  }
  
  public String toString()
  {
    return "com.viettel.bccsgw.bo.ErrorCode[errorCodeId=" + this.errorCodeId + "]";
  }
}
