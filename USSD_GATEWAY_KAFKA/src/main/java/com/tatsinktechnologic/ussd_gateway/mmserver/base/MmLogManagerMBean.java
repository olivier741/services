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
public abstract interface MmLogManagerMBean
{
  public abstract String summary();
  
  public abstract String getLevel(String paramString);
  
  public abstract String setLevel(String paramString1, String paramString2);
  
  public abstract String getRootLevel(String paramString);
  
  public abstract String setRootLevel(String paramString);
}
