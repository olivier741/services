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

public class TableParam
  extends Param
{
  private ArrayList<String> columns = new ArrayList();
  private ArrayList<Boolean> readability = new ArrayList();
  private ArrayList<Param> controls = null;
  private ArrayList<ArrayList<Param>> rows = new ArrayList();
  
  public TableParam() {}
  
  public TableParam(String name, ArrayList<String> columns)
  {
    super(name);
    this.columns = columns;
    if (columns != null) {
      for (int i = 0; i < columns.size(); i++) {
        this.readability.add(Boolean.FALSE);
      }
    }
  }
  
  public TableParam(String name, String[] columns)
  {
    super(name);
    for (String c : columns)
    {
      this.columns.add(c);
      this.readability.add(Boolean.FALSE);
    }
  }
  
  public void addRow(Param[] params)
  {
    if (params.length != this.columns.size()) {
      throw new TableParamException("New row length is different from collumn size");
    }
    ArrayList<Param> newRow = new ArrayList();
    for (Param p : params) {
      newRow.add(p);
    }
    this.rows.add(newRow);
  }
  
  public void addRow(ArrayList<Param> params)
    throws TableParamException
  {
    if (params.size() != this.columns.size()) {
      throw new TableParamException("New row size is different from collumn size");
    }
    this.rows.add(params);
  }
  
  public ArrayList<Param> getRow(int i)
  {
    if ((i < 0) || (i >= this.rows.size())) {
      throw new TableParamException("Index out of range: " + i);
    }
    return (ArrayList)this.rows.get(i);
  }
  
  public void setReadOnly(String columnName, boolean readOnly)
  {
    if (!this.columns.contains(columnName)) {
      throw new TableParamException("colummn " + columnName + " does not exist");
    }
    this.readability.set(this.columns.indexOf(columnName), Boolean.TRUE);
  }
  
  public ArrayList<Boolean> getColumnReadability()
  {
    return this.readability;
  }
  
  public void setParam(int row, int column, Param p)
  {
    if (row >= this.rows.size()) {
      throw new TableParamException("Index out of range: " + row);
    }
    if (column >= this.columns.size()) {
      throw new TableParamException("Index out of range: " + column);
    }
    ((ArrayList)this.rows.get(row)).set(column, p);
  }
  
  public void setRow(int row, ArrayList<Param> params)
  {
    this.rows.set(row, params);
  }
  
  public void deleteRow(int row)
  {
    this.rows.remove(row);
  }
  
  public void setControl(Param[] control)
  {
    this.controls = new ArrayList();
    for (Param p : control) {
      this.controls.add(p);
    }
  }
  
  public ArrayList<Param> getControls()
  {
    return this.controls;
  }
  
  public boolean isDifferent(Param p)
  {
    if ((p instanceof TableParam))
    {
      TableParam tp = (TableParam)p;
      if (!tp.columns.equals(this.columns)) {
        return true;
      }
      if (tp.rows.size() != this.rows.size()) {
        return true;
      }
      for (int i = 0; i < this.rows.size(); i++)
      {
        ArrayList<Param> row = (ArrayList)this.rows.get(i);
        for (int j = 0; j < row.size(); j++)
        {
          Param param = (Param)row.get(j);
          if (param.isDifferent(tp.getParam(i, j))) {
            return true;
          }
        }
      }
      return false;
    }
    return true;
  }
  
  public Param getParam(int row, int column)
  {
    if (this.rows == null) {
      return null;
    }
    if ((row >= this.rows.size()) || (column >= this.columns.size())) {
      return null;
    }
    return (Param)((ArrayList)this.rows.get(row)).get(column);
  }
  
  public String getDescription()
  {
    return "";
  }
  
  public String[] getColumns()
  {
    String[] s = new String[this.columns.size()];
    for (int i = 0; i < this.columns.size(); i++) {
      s[i] = ((String)this.columns.get(i));
    }
    return s;
  }
  
  public int getColumnCount()
  {
    return this.columns.size();
  }
  
  public int getRowCount()
  {
    return this.rows.size();
  }
  
  public JComponent getComponent()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public String getValue()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (String colum : this.columns)
    {
      sb.append(colum);
      sb.append(", ");
    }
    sb.append("]");
    sb.append("\n");
    for (ArrayList<Param> row : this.rows)
    {
      sb.append("[");
      for (Param p : row) {
        if (p != null)
        {
          sb.append(p.toString());
          sb.append("  ");
        }
        else
        {
          sb.append("NULL");
          sb.append("  ");
        }
      }
      sb.append("]");
    }
    return sb.toString();
  }
  
  public Param getCopy()
  {
    TableParam copy = new TableParam(getName(), this.columns);
    copy.readability = this.readability;
    copy.controls = this.controls;
    ArrayList<ArrayList<Param>> copyParam = new ArrayList();
    for (ArrayList<Param> row : this.rows)
    {
      ArrayList<Param> copyRow = new ArrayList();
      for (Param p : row) {
        copyRow.add(p.getCopy());
      }
      copyParam.add(copyRow);
    }
    copy.rows = copyParam;
    copy.setReadOnly(isReadOnly());
    return copy;
  }
}

