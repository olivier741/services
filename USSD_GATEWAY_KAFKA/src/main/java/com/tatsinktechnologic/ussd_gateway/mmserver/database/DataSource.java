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

