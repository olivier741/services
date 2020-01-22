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
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="Input", propOrder={"username", "password", "wscode", "param", "rawData"})
@XmlRootElement(name="Input")
public class Input
{
  @XmlElement(required=true)
  protected String username;
  @XmlElement(required=true)
  protected String password;
  @XmlElement(required=true)
  protected String wscode;
  protected RawData rawData;
  @XmlElements({@XmlElement(required=false)})
  protected ArrayList<Param> param;
  
  public String getUsername()
  {
    return this.username;
  }
  
  public void setUsername(String value)
  {
    this.username = value;
  }
  
  public String getPassword()
  {
    return this.password;
  }
  
  public void setPassword(String value)
  {
    this.password = value;
  }
  
  public String getWscode()
  {
    return this.wscode;
  }
  
  public void setWscode(String value)
  {
    this.wscode = value;
  }
  
  public ArrayList<Param> getParam()
  {
    if (this.param == null) {
      this.param = new ArrayList();
    }
    return this.param;
  }
  
  public void setParam(ArrayList<Param> param)
  {
    this.param = param;
  }
  
  public RawData getRawData()
  {
    return this.rawData;
  }
  
  public void setRawData(RawData rawData)
  {
    this.rawData = rawData;
  }
  
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name="Param")
  public static class Param
  {
    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected String value;
    
    public String getName()
    {
      return this.name;
    }
    
    public void setName(String value)
    {
      this.name = value;
    }
    
    public String getValue()
    {
      return this.value;
    }
    
    public void setValue(String value)
    {
      this.value = value;
    }
  }
  
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name="", propOrder={"value"})
  public static class RawData
  {
    @XmlValue
    protected String value;
    
    public String getValue()
    {
      return this.value;
    }
    
    public void setValue(String value)
    {
      this.value = value;
    }
  }
}
