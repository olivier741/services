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
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

public class DateFormatter
  extends Formatter
{
  private SimpleDateFormat dateFormater = null;
  
  public DateFormatter() {}
  
  public DateFormatter(String format)
  {
    this.dateFormater = new SimpleDateFormat(format);
  }
  
  public boolean verify(String value)
  {
    try
    {
      ParsePosition index = new ParsePosition(0);
      this.dateFormater.parse(value, index);
      return index.getIndex() == value.length();
    }
    catch (Exception e) {}
    return false;
  }
}
