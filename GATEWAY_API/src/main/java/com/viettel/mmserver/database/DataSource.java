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
package com.viettel.mmserver.database;

/**
 *
 * @author olivier.tatsinkou
 */

public class DataSource
{
  protected Data[] data;
  
  public DataSource() {}
  
  public DataSource(Data[] data)
  {
    if (data != null)
    {
      this.data = new Data[data.length];
      System.arraycopy(data, 0, this.data, 0, data.length);
    }
  }
  
  public void add(Data d)
  {
    if (this.data == null)
    {
      this.data = new Data[] { d };
    }
    else
    {
      Data[] newData = new Data[this.data.length + 1];
      System.arraycopy(this.data, 0, newData, 0, this.data.length);
      newData[this.data.length] = d;
      this.data = newData;
    }
  }
  
  public Data getData(String name)
  {
    if ((this.data == null) || (this.data.length == 0)) {
      return null;
    }
    for (Data d : this.data) {
      if (d.getName().equals(name)) {
        return d;
      }
    }
    return null;
  }
}
