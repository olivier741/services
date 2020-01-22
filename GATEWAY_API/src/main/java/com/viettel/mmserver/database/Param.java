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
public class Param
  extends Data
{
  public static final int IN = 0;
  public static final int OUT = 1;
  public static final int INOUT = 2;
  public static final String BOOLEAN = "boolean";
  public static final String BYTE = "byte";
  public static final String SHORT = "short";
  public static final String INT = "int";
  public static final String LONG = "long";
  public static final String FLOAT = "float";
  public static final String DOUBLE = "double";
  public static final String STRING = "string";
  public static final String BLOB = "blob";
  public static final String CLOB = "clob";
  public static final String DATE = "date";
  protected int type;
  
  public Param(String name, String datatype, Object value, int type)
  {
    super(name, datatype, value);
    this.type = type;
  }
  
  public int getType()
  {
    return this.type;
  }
  
  public void setType(int type)
  {
    this.type = type;
  }
}

