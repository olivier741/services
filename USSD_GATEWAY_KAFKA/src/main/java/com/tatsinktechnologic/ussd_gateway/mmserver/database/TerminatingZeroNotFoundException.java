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
public class TerminatingZeroNotFoundException
  extends Exception
{
  public TerminatingZeroNotFoundException()
  {
    super("Terminating zero not found in buffer.");
  }
  
  public TerminatingZeroNotFoundException(String s)
  {
    super(s);
  }
}

