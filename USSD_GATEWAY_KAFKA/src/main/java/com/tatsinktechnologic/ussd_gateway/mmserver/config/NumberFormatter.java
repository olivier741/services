/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.config;

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
    switch (type.ordinal())
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

