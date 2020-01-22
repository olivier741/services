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
public class NumberFormatter
  extends Formatter
{
  public NumberFormatter() {}
  
  public static enum Type
  {
    Byte,  Double,  Float,  Integer,  Long,  Short;
    
    private Type() {}
  }
  
  private Type type = null;
  
  public NumberFormatter(Type type)
  {
    this.type = type;
  }
  
  public boolean verify(String value)
  {
    switch (this.type.ordinal())
    {
    case 1: 
      try
      {
        Byte.valueOf(value);
        return true;
      }
      catch (Exception e)
      {
        return false;
      }
    case 2: 
      try
      {
        Double.valueOf(value);
        return true;
      }
      catch (Exception e)
      {
        return false;
      }
    case 3: 
      try
      {
        Float.valueOf(value);
        return true;
      }
      catch (Exception e)
      {
        return false;
      }
    case 4: 
      try
      {
        Integer.valueOf(value);
        return true;
      }
      catch (Exception e)
      {
        return false;
      }
    case 5: 
      try
      {
        Long.valueOf(value);
        return true;
      }
      catch (Exception e)
      {
        return false;
      }
    case 6: 
      try
      {
        Long.valueOf(value);
        return true;
      }
      catch (Exception e)
      {
        return false;
      }
    }
    return false;
  }
}
