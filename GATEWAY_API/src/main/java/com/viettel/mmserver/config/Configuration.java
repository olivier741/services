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
import java.util.ArrayList;

public class Configuration
  implements Serializable
{
  private ArrayList<Param> params = new ArrayList();
  
  public Param getParamByName(String paramName)
  {
    for (Param p : this.params) {
      if (p.getName().equals(paramName)) {
        return p;
      }
    }
    return null;
  }
  
  public void addParams(ArrayList<Param> params)
  {
    this.params.addAll(params);
  }
  
  public void addParam(Param param)
  {
    this.params.add(param);
  }
  
  public ArrayList<Param> getParams()
  {
    return this.params;
  }
  
  public void setParams(ArrayList<Param> params)
  {
    this.params = params;
  }
  
  public String toString()
  {
    String r = "";
    for (Param p : this.params)
    {
      r = r + p.toString();
      r = r + "\n";
    }
    return r;
  }
  
  public Configuration getCopy()
  {
    Configuration copy = new Configuration();
    ArrayList<Param> copyParam = new ArrayList();
    for (Param p : this.params) {
      copyParam.add(p.getCopy());
    }
    copy.params = copyParam;
    return copy;
  }
  
  public boolean isDifferent(Configuration cfg)
  {
    if (cfg.params.size() != this.params.size()) {
      return true;
    }
    for (int i = 0; i < this.params.size(); i++)
    {
      Param p = (Param)this.params.get(i);
      if (p.isDifferent((Param)cfg.params.get(i))) {
        return true;
      }
    }
    return false;
  }
}

