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
import javax.swing.JPasswordField;

public class PasswordParam
  extends Param
{
  private String password = "";
  
  public PasswordParam() {}
  
  public PasswordParam(String password)
  {
    this.password = password;
  }
  
  public PasswordParam(String password, boolean readOnly)
  {
    this(password);
    setReadOnly(readOnly);
  }
  
  public PasswordParam(String name, String passwords)
  {
    super(name);
    this.password = passwords;
  }
  
  public PasswordParam(String name, String password, boolean readOnly)
  {
    this(name, password);
    setReadOnly(readOnly);
  }
  
  public String getValue()
  {
    return this.password;
  }
  
  public boolean isDifferent(Param p)
  {
    if (((p instanceof PasswordParam)) && 
      (this.password.equals(((PasswordParam)p).getValue())) && (getName().equals(p.getName()))) {
      return false;
    }
    return true;
  }
  
  public void setPassword(String pass)
  {
    this.password = pass;
  }
  
  public String toString()
  {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < this.password.length(); i++) {
      s.append("*");
    }
    return s.toString();
  }
  
  public synchronized JComponent getComponent()
  {
    return new JPasswordField(this.password);
  }
  
  public Param getCopy()
  {
    PasswordParam copy = new PasswordParam(getName(), new String(this.password), isReadOnly());
    return copy;
  }
}

