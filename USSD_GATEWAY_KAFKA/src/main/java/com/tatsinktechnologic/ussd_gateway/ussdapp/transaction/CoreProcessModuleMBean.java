/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.transaction;

/**
 *
 * @author olivier.tatsinkou
 */
public abstract interface CoreProcessModuleMBean
{
  public abstract String viewInfo();
  
  public abstract void reloadNumGwReceiveThread()
    throws Exception;
  
  public abstract void reloadNumGwReceiveThread(int paramInt)
    throws Exception;
  
  public abstract void reloadNumVasReceiveThread()
    throws Exception;
  
  public abstract void reloadNumVasReceiveThread(int paramInt)
    throws Exception;
  
  public abstract void reloadStateSet()
    throws Exception;
}
