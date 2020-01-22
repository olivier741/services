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
public abstract interface TaskInforMBean
{
  public abstract String getClassPath();
  
  public abstract void setClassPath(String paramString);
  
  public abstract Integer getId();
  
  public abstract String getJarPath();
  
  public abstract void setJarPath(String paramString);
  
  public abstract String getName();
  
  public abstract void setName(String paramString);
  
  public abstract String getParam();
  
  public abstract void setParam(String paramString);
  
  public abstract String getType();
  
  public abstract void setType(String paramString);
  
  public abstract String[] getTaskTimeString();
  
  public abstract String addTaskTimeString(String paramString);
  
  public abstract void removeTaskTimeString(int paramInt);
  
  public abstract String toString();
}
