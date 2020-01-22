/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.config;

/**
 *
 * @author olivier.tatsinkou
 */
import java.io.Serializable;
import javax.swing.JComponent;

public abstract class Param
  implements Serializable
{
  private String name = "";
  private boolean readOnly = false;
  
  public abstract String getValue();
  
  public Param() {}
  
  public Param(String name)
  {
    this.name = name;
    this.readOnly = false;
  }
  
  public Param(String name, boolean readOnly)
  {
    this.name = name;
    this.readOnly = readOnly;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public boolean isReadOnly()
  {
    return this.readOnly;
  }
  
  public void setReadOnly(boolean readOnly)
  {
    this.readOnly = readOnly;
  }
  
  public String toString()
  {
    return "Param name: " + this.name + "\nClass: " + getClass().getName();
  }
  
  public abstract boolean isDifferent(Param paramParam);
  
  public abstract JComponent getComponent();
  
  public abstract Param getCopy();
}

