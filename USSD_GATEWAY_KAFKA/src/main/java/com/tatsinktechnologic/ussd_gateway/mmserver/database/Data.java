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
public class Data
{
  private String name;
  private String datatype;
  private Object value;
  
  public Data(String name, String datatype, Object value)
  {
    this.name = name;
    this.datatype = datatype;
    this.value = value;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getDatatype()
  {
    return this.datatype;
  }
  
  public void setDatatype(String datatype)
  {
    this.datatype = datatype;
  }
  
  public Object getValue()
  {
    return this.value;
  }
  
  public void setValue(Object value)
  {
    this.value = value;
  }
}
