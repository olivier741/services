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
import javax.swing.JComboBox;
import javax.swing.JComponent;

public class ComboBoxParam
  extends Param
{
  private String value = "";
  private ArrayList<String> options = new ArrayList();
  
  public ComboBoxParam() {}
  
  public ComboBoxParam(String name, String value)
  {
    super(name);
    this.value = value;
  }
  
  public ComboBoxParam(String name, String value, boolean readOnly)
  {
    super(name, readOnly);
    this.value = value;
  }
  
  public ComboBoxParam(String name, String value, ArrayList<String> options)
  {
    super(name);
    this.value = value;
    this.options = options;
  }
  
  public ComboBoxParam(String value, String[] options)
  {
    this.value = value;
    boolean ok = false;
    for (String o : options)
    {
      if (value.equals(o)) {
        ok = true;
      }
      this.options.add(o);
    }
    if (!ok) {
      throw new ComboBoxParamException("value is not in options");
    }
  }
  
  public ComboBoxParam(String name, String value, String[] options)
  {
    super(name);
    this.value = value;
    boolean ok = false;
    for (String o : options)
    {
      if (value.equals(o)) {
        ok = true;
      }
      this.options.add(o);
    }
    if (!ok) {
      throw new ComboBoxParamException("value is not in options");
    }
  }
  
  public ComboBoxParam(String name, String value, ArrayList<String> options, boolean readOnly)
  {
    super(name, readOnly);
    this.value = value;
    this.options = options;
    if (!options.contains(value)) {
      throw new ComboBoxParamException("value is not in options");
    }
  }
  
  public ArrayList<String> getOptions()
  {
    return this.options;
  }
  
  public String getValue()
  {
    return this.value;
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
  
  public void addOption(String option)
  {
    this.options.add(option);
  }
  
  public void addOptions(ArrayList<String> options)
  {
    this.options.addAll(options);
  }
  
  public void setOptions(ArrayList<String> options)
  {
    this.options = options;
  }
  
  public String toString()
  {
    return this.value;
  }
  
  public String getDescription()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(super.toString());
    sb.append("value: " + this.value);
    sb.append("\noptions: ");
    for (String op : this.options)
    {
      sb.append(op);
      sb.append(", ");
    }
    return sb.toString();
  }
  
  public boolean isDifferent(Param p)
  {
    if ((p instanceof ComboBoxParam))
    {
      ComboBoxParam cp = (ComboBoxParam)p;
      if ((cp.getValue().equals(this.value)) && (cp.getOptions().equals(this.options))) {
        return false;
      }
      return true;
    }
    return true;
  }
  
  public synchronized JComponent getComponent()
  {
    JComboBox component = new JComboBox(this.options.toArray());
    component.setSelectedItem(this.value);
    return component;
  }
  
  public Param getCopy()
  {
    ArrayList<String> copyOptions = new ArrayList();
    for (String o : this.options) {
      copyOptions.add(new String(o));
    }
    ComboBoxParam copy = new ComboBoxParam(getName(), new String(this.value), copyOptions, isReadOnly());
    return copy;
  }
}
