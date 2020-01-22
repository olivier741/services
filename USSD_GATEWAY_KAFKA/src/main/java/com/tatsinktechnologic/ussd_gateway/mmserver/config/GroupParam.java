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
import java.util.ArrayList;
import javax.swing.JComponent;

public class GroupParam
  extends Param
{
  private ArrayList<Param> paramGroup = null;
  
  public GroupParam(String name)
  {
    this(name, false);
  }
  
  public GroupParam(String name, boolean readOnly)
  {
    super(name, readOnly);
    this.paramGroup = new ArrayList();
  }
  
  public void addParam(Param param)
  {
    if (((param instanceof TextParam)) || ((param instanceof PasswordParam)) || ((param instanceof ComboBoxParam))) {
      this.paramGroup.add(param);
    } else {
      throw new TextParamException("param is not a text param");
    }
  }
  
  public void removeParam(int index)
  {
    this.paramGroup.remove(index);
  }
  
  public Param getParam(int index)
  {
    return (Param)this.paramGroup.get(index);
  }
  
  public int getSize()
  {
    return this.paramGroup.size();
  }
  
  public String getValue()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public boolean isDifferent(Param p)
  {
    if (!(p instanceof GroupParam)) {
      return true;
    }
    if (!getName().equals(p.getName())) {
      return true;
    }
    GroupParam tgPram = (GroupParam)p;
    int size = getSize();
    if (size != tgPram.getSize()) {
      return true;
    }
    for (int i = 0; i < size; i++) {
      if (((Param)this.paramGroup.get(i)).isDifferent(tgPram.getParam(i))) {
        return true;
      }
    }
    return false;
  }
  
  public JComponent getComponent()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Param getCopy()
  {
    GroupParam copy = new GroupParam(new String(getName()), isReadOnly());
    for (Param param : this.paramGroup) {
      copy.addParam(param.getCopy());
    }
    return copy;
  }
  
  public String toString()
  {
    StringBuilder str = new StringBuilder();
    if (this.paramGroup != null) {
      for (int i = 0; i < getSize(); i++) {
        if (this.paramGroup.get(i) != null) {
          str.append(" " + ((Param)this.paramGroup.get(i)).toString());
        } else {
          str.append(" NULL");
        }
      }
    }
    return str.toString();
  }
}

