/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.ussdgwinterface;

import com.tatsinktechnologic.ussd_gateway.ussdapp.common.UssdMessage;
import com.tatsinktechnologic.ussd_gateway.utils.BlockQueue;

/**
 *
 * @author olivier.tatsinkou
 */



public abstract interface Connector
{
  public abstract void start();
  
  public abstract void stop();
  
  public abstract void setQueue(BlockQueue paramBlockQueue);
  
  public abstract void send(UssdMessage paramUssdMessage)
    throws Exception;
  
  public abstract boolean haveBkConnector();
  
  public abstract int getBkConnectorId();
  
  public abstract int getId();
}
