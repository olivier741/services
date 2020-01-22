/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.basev1;

/**
 *
 * @author olivier.tatsinkou
 */
public abstract interface MmProcess
{
  public abstract Integer getId();
  
  public abstract void start();
  
  public abstract void stop();
  
  public abstract void suspend();
  
  public abstract void resume();
  
  public abstract Thread.State getState();
  
  public abstract String getInfor();
  
  public abstract String loadParams();
  
  public abstract String saveParams(String paramString);
}

