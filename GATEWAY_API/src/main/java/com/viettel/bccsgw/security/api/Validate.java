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
package com.viettel.bccsgw.security.api;

/**
 *
 * @author olivier.tatsinkou
 */
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="validate", propOrder={"userName", "password", "domainCode"})
public class Validate
{
  protected String userName;
  protected String password;
  protected String domainCode;
  
  public String getUserName()
  {
    return this.userName;
  }
  
  public void setUserName(String value)
  {
    this.userName = value;
  }
  
  public String getPassword()
  {
    return this.password;
  }
  
  public void setPassword(String value)
  {
    this.password = value;
  }
  
  public String getDomainCode()
  {
    return this.domainCode;
  }
  
  public void setDomainCode(String value)
  {
    this.domainCode = value;
  }
}