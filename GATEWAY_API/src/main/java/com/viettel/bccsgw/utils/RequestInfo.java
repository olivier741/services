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
package com.viettel.bccsgw.utils;

/**
 *
 * @author olivier.tatsinkou
 */
import com.viettel.bccsgw.security.UserToken;
import java.util.Date;

public class RequestInfo
{
  String username;
  Integer currentNumRequest;
  UserToken ut;
  Date nearestActionTime;
  String ip;
  String password;
  
  public RequestInfo()
  {
    this.currentNumRequest = Integer.valueOf(0);
  }
  
  public String getUsername()
  {
    return this.username;
  }
  
  public void setUsername(String username)
  {
    this.username = username;
  }
  
  public Integer getCurrentNumRequest()
  {
    return this.currentNumRequest;
  }
  
  public void setCurrentNumRequest(Integer currentNumRequest)
  {
    this.currentNumRequest = currentNumRequest;
  }
  
  public UserToken getUt()
  {
    return this.ut;
  }
  
  public void setUt(UserToken ut)
  {
    this.ut = ut;
  }
  
  public Date getNearestActionTime()
  {
    return this.nearestActionTime;
  }
  
  public void setNearestActionTime(Date nearestActionTime)
  {
    this.nearestActionTime = nearestActionTime;
  }
  
  public String getIp()
  {
    return this.ip;
  }
  
  public void setIp(String ip)
  {
    this.ip = ip;
  }
  
  public String getPassword()
  {
    return this.password;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
  }
}