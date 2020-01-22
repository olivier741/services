/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.ussdgwinterface;

/**
 *
 * @author olivier.tatsinkou
 */
public abstract interface UssdGwInfModuleMBean
{
  public abstract String viewAllInfo();
  
  public abstract String viewLoadInfo();
  
  public abstract void reloadNumSendThread()
    throws Exception;
  
  public abstract void reloadNumSendThread(int paramInt)
    throws Exception;
}
