/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.database;

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
