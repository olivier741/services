/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.vasinterface.tcp;

/**
 *
 * @author olivier.tatsinkou
 */
import java.util.TimerTask;

public class SwitchMainTask
  extends TimerTask
{
  private Vas_TCPConnector connector;
  
  public SwitchMainTask(Vas_TCPConnector connector)
  {
    this.connector = connector;
  }
  
  public void run()
  {
    this.connector.swToMainConnection();
  }
}
