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
public abstract interface LogConfigMBean
{
  public abstract int getLoggingPort();
  
  public abstract String getLoggingIP();
}

