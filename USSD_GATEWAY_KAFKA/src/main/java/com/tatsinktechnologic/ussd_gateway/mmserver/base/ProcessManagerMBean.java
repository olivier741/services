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

public abstract interface ProcessManagerMBean
{
  public abstract void stop(Integer paramInteger);
  
  public abstract String listProcess();
  
  public abstract String getProcessState(Integer paramInteger);
  
  public abstract void kill(Integer paramInteger);
  
  public abstract void cleanup();
  
  public abstract void stopAll();
}
