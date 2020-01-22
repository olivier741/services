/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.base;

/**
 *
 * @author olivier.tatsinkou
 */
public class MmException
  extends Exception
{
  private static final long serialVersionUID = -777216335204861186L;
  
  public MmException() {}
  
  public MmException(String s)
  {
    super(s);
  }
  
  public MmException(Throwable cause)
  {
    super(cause);
  }
  
  public MmException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
