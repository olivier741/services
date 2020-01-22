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
@XmlType(name="Output", propOrder={"error", "description", "_return", "original"})
@XmlRootElement(name="Output")
public class Output
{
  @XmlElement(required=true)
  protected String error;
  @XmlElement(required=true)
  protected String description;
  @XmlElements({@XmlElement(name="return", required=true)})
  protected ArrayList<String> _return;
  @XmlElement(required=true)
  protected String original;
  
  public String getError()
  {
    return this.error;
  }
  
  public void setError(String value)
  {
    this.error = value;
  }
  
  public String getDescription()
  {
    return this.description;
  }
  
  public void setDescription(String value)
  {
    this.description = value;
  }
  
  public ArrayList<String> getReturn()
  {
    if (this._return == null) {
      this._return = new ArrayList();
    }
    return this._return;
  }
  
  public void setReturn(ArrayList<String> _return)
  {
    this._return = _return;
  }
  
  public String getOriginal()
  {
    return this.original;
  }
  
  public void setOriginal(String original)
  {
    this.original = original;
  }
}

