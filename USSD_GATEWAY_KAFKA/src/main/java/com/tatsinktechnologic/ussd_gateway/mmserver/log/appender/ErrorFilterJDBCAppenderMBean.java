/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.log.appender;

/**
 *
 * @author olivier.tatsinkou
 */
public abstract interface ErrorFilterJDBCAppenderMBean
{
  public abstract void start();
  
  public abstract void stop();
  
  public abstract String getInfor();
}

