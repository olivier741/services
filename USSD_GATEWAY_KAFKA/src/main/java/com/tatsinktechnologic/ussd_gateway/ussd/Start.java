/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussd;

/**
 *
 * @author olivier.tatsinkou
 */
public class Start
{
  public static void main(String[] args)
  {
    try
    {
      UssdApp.getInstance().start();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
