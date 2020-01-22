/*
 * Copyright 2019 olivier.tatsinkou.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.viettel.mmserver.config;

/**
 *
 * @author olivier.tatsinkou
 */
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TextParam
  extends Param
{
  public static final int TEXTAREA_COLUMN = 30;
  public static final int TEXTAREA_ROW = 4;
  private Formatter formatter = null;
  
  public Formatter getFormatter()
  {
    return this.formatter;
  }
  
  public void setFormatter(Formatter formatter)
  {
    this.formatter = formatter;
    ensureValidateData();
  }
  
  public boolean isDifferent(Param p)
  {
    if ((p instanceof TextParam))
    {
      TextParam lp = (TextParam)p;
      if ((lp.getValue().equals(this.value)) && (lp.type.equals(this.type))) {
        return false;
      }
      return true;
    }
    return true;
  }
  
  public synchronized JComponent getComponent()
  {
    if (this.type.equals(TextType.TEXT_FIELD)) {
      return new JTextField(this.value);
    }
    return new JTextArea(this.value, 4, 30);
  }
  
  public Param getCopy()
  {
    TextParam copy = new TextParam(getName(), new String(this.value), isReadOnly(), this.type, this.formatter);
    return copy;
  }
  
  public TextParam() {}
  
  public static enum TextType
  {
    TEXT_FIELD,  TEXT_AREA;
    
    private TextType() {}
  }
  
  private TextType type = TextType.TEXT_FIELD;
  private String value = "";
  
  public TextParam(String value)
  {
    this.value = value;
  }
  
  public TextParam(String value, Formatter formater)
  {
    this(value);
    this.formatter = formater;
    ensureValidateData();
  }
  
  public TextParam(String value, TextType type)
  {
    this.value = value;
    this.type = type;
  }
  
  public TextParam(String value, TextType type, Formatter formatter)
  {
    this(value, type);
    this.formatter = formatter;
    ensureValidateData();
  }
  
  public TextParam(String name, String value)
  {
    super(name);
    this.value = value;
  }
  
  public TextParam(String name, String value, Formatter formatter)
  {
    this(name, value);
    this.formatter = formatter;
    ensureValidateData();
  }
  
  public TextParam(String name, String value, boolean readOnly)
  {
    super(name, readOnly);
    this.value = value;
  }
  
  public TextParam(String name, String value, boolean readOnly, Formatter formater)
  {
    this(name, value, readOnly);
    this.formatter = formater;
    ensureValidateData();
  }
  
  public TextParam(String name, String value, TextType type)
  {
    super(name);
    this.value = value;
    this.type = type;
  }
  
  public TextParam(String name, String value, TextType type, Formatter formatter)
  {
    this(name, value, type);
    this.formatter = formatter;
    ensureValidateData();
  }
  
  public TextParam(String name, String value, boolean readOnly, TextType type)
  {
    this(name, value, readOnly);
    this.type = type;
  }
  
  public TextParam(String name, String value, boolean readOnly, TextType type, Formatter formatter)
  {
    this(name, value, readOnly, type);
    this.formatter = formatter;
    ensureValidateData();
  }
  
  public TextType getType()
  {
    return this.type;
  }
  
  public void setType(TextType type)
  {
    this.type = type;
  }
  
  public String getValue()
  {
    return this.value;
  }
  
  public void setValue(String value)
  {
    this.value = value;
    ensureValidateData();
  }
  
  public String toString()
  {
    return this.value;
  }
  
  private void ensureValidateData()
  {
    if ((this.formatter != null) && (this.value != null) && 
      (!this.formatter.verify(this.value))) {
      throw new TextParamException("value is in wrong format");
    }
  }
}