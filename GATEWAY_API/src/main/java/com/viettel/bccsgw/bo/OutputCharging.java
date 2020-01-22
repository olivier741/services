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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="Message", propOrder={"errorCode", "message", "requestId", "data", "original"})
@XmlRootElement(name="Message")
public class OutputCharging
{
  @XmlElement(name="errorCode", required=true)
  protected String errorCode;
  @XmlElement(name="message", required=true)
  protected String message;
  @XmlElement(name="requestId", required=true)
  protected String requestId;
  @XmlElements({@XmlElement(name="data", required=true)})
  protected ArrayList<Entry> data;
  @XmlElement(name="original", required=true)
  protected String original;
  
  public String getOriginal()
  {
    return this.original;
  }
  
  public void setOriginal(String original)
  {
    this.original = original;
  }
  
  public String getErrorCode()
  {
    return this.errorCode;
  }
  
  public void setErrorCode(String errorCode)
  {
    this.errorCode = errorCode;
  }
  
  public String getMessage()
  {
    return this.message;
  }
  
  public void setMessage(String message)
  {
    this.message = message;
  }
  
  public String getRequestId()
  {
    return this.requestId;
  }
  
  public void setRequestId(String requestId)
  {
    this.requestId = requestId;
  }
  
  public ArrayList<Entry> getData()
  {
    return this.data;
  }
  
  public void setData(ArrayList<Entry> data)
  {
    this.data = data;
  }
}
