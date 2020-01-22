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
import java.sql.SQLException;

public class ConnectionException
  extends SQLException
{
  protected String connUrl;
  
  public ConnectionException(String message, String connUrl)
  {
    super(message);
    this.connUrl = connUrl;
  }
  
  public String getConnUrl()
  {
    return this.connUrl;
  }
  
  public void setConnUrl(String connUrl)
  {
    this.connUrl = connUrl;
  }
}
