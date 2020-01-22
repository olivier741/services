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
package com.viettel.mmserver.base;

/**
 *
 * @author olivier.tatsinkou
 */
import java.util.ArrayList;

public class ConfigObject
{
  private static String typeDeliminator = ">><<";
  private static String valueDeliminator = "\t";
  private String config;
  
  public ConfigObject()
  {
    this.config = "";
  }
  
  public ConfigObject(String config)
  {
    this.config = config;
  }
  
  public synchronized boolean addConfig(String name, String[] param)
  {
    boolean result = false;
    if ((name != null) && (!name.equals("")) && (param != null) && (param.length > 0))
    {
      StringBuilder configBuilder = new StringBuilder();
      if ((this.config != null) && (!this.config.equals("")) && (name != null) && (!name.equals("")))
      {
        String[] configs = this.config.split("\\n");
        for (int i = 0; i < configs.length; i++)
        {
          int nameIndex = configs[i].indexOf(typeDeliminator);
          if ((nameIndex == -1) || ((nameIndex != -1) && (!configs[i].substring(0, nameIndex).equals(name))))
          {
            configBuilder.append(configs[i]);
            configBuilder.append("\n");
          }
        }
        this.config = configBuilder.toString();
        result = true;
      }
      configBuilder.append(name);
      configBuilder.append(typeDeliminator);
      for (int i = 0; i < param.length; i++)
      {
        configBuilder.append(param[i]);
        configBuilder.append(valueDeliminator);
      }
      configBuilder.append("\n");
      this.config = configBuilder.toString();
      result = true;
    }
    return result;
  }
  
  public synchronized boolean removeConfig(String name)
  {
    boolean result = false;
    StringBuilder configBuilder = new StringBuilder();
    if ((this.config != null) && (!this.config.equals("")) && (name != null) && (!name.equals("")))
    {
      String[] configs = this.config.split("\\n");
      for (int i = 0; i < configs.length; i++)
      {
        int nameIndex = configs[i].indexOf(typeDeliminator);
        if ((nameIndex == -1) || ((nameIndex != -1) && (!configs[i].substring(0, nameIndex).equals(name)))) {
          configBuilder.append(configs[i]);
        }
      }
      this.config = configBuilder.toString();
      result = true;
    }
    return result;
  }
  
  public synchronized String[] readConfig(String key)
  {
    String[] values = null;
    if ((this.config != null) && (!this.config.equals("")) && (key != null) && (!key.equals("")))
    {
      String[] configs = this.config.split("\\n");
      for (int i = 0; i < configs.length; i++)
      {
        int nameIndex = configs[i].indexOf(typeDeliminator);
        if ((nameIndex != -1) && (configs[i].substring(0, nameIndex).equals(key)))
        {
          String temp = configs[i].substring(nameIndex + typeDeliminator.length());
          values = temp.split(valueDeliminator);
        }
      }
    }
    return values;
  }
  
  public synchronized ArrayList<String> getListParams()
  {
    ArrayList<String> list = new ArrayList();
    if ((this.config != null) && (!this.config.equals("")))
    {
      String[] configs = this.config.split("\\n");
      for (int i = 0; i < configs.length; i++)
      {
        int nameIndex = configs[i].indexOf(typeDeliminator);
        if (nameIndex != -1) {
          list.add(configs[i].substring(0, nameIndex));
        }
      }
    }
    return list;
  }
  
  public String getConfig()
  {
    return this.config;
  }
}

