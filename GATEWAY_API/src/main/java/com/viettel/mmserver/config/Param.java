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
package com.viettel.mmserver.config;

/**
 *
 * @author olivier.tatsinkou
 */
import java.io.Serializable;
import javax.swing.JComponent;

public abstract class Param
  implements Serializable
{
  private String name = "";
  private boolean readOnly = false;
  
  public abstract String getValue();
  
  public Param() {}
  
  public Param(String name)
  {
    this.name = name;
    this.readOnly = false;
  }
  
  public Param(String name, boolean readOnly)
  {
    this.name = name;
    this.readOnly = readOnly;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public boolean isReadOnly()
  {
    return this.readOnly;
  }
  
  public void setReadOnly(boolean readOnly)
  {
    this.readOnly = readOnly;
  }
  
  public String toString()
  {
    return "Param name: " + this.name + "\nClass: " + getClass().getName();
  }
  
  public abstract boolean isDifferent(Param paramParam);
  
  public abstract JComponent getComponent();
  
  public abstract Param getCopy();
}

