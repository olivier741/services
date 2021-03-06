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
import java.util.ArrayList;

public class ParamList
{
  private ArrayList<Param> params;
  
  public ParamList(Param[] param)
  {
    this();
    if ((param == null) || (param.length == 0)) {
      return;
    }
    for (Param p : param) {
      this.params.add(p);
    }
  }
  
  public ParamList()
  {
    this.params = new ArrayList();
  }
  
  public Param[] allParam()
  {
    return (Param[])this.params.toArray(new Param[this.params.size()]);
  }
  
  public void add(Param p)
  {
    this.params.add(p);
  }
  
  public void remove(Param p)
  {
    this.params.remove(p);
  }
  
  public void remove(String name)
  {
    for (Param p : this.params) {
      if (p.getName().equals(name))
      {
        this.params.remove(p);
        return;
      }
    }
  }
  
  public Param[] param(int type)
  {
    ArrayList<Param> v = new ArrayList();
    for (Param p : this.params) {
      if (p.getType() == type) {
        v.add(p);
      }
    }
    return (Param[])v.toArray(new Param[v.size()]);
  }
  
  public Param[] paramIn()
  {
    ArrayList<Param> v = new ArrayList();
    for (Param p : this.params) {
      if ((p.getType() == 0) || (p.getType() == 2)) {
        v.add(p);
      }
    }
    return (Param[])v.toArray(new Param[v.size()]);
  }
  
  public Param[] paramOut()
  {
    ArrayList<Param> v = new ArrayList();
    for (Param p : this.params) {
      if ((p.getType() == 1) || (p.getType() == 2)) {
        v.add(p);
      }
    }
    return (Param[])v.toArray(new Param[v.size()]);
  }
  
  public int size()
  {
    return this.params.size();
  }
}