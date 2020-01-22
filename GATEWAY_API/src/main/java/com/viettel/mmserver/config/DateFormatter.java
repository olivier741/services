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
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

public class DateFormatter
  extends Formatter
{
  private SimpleDateFormat dateFormater = null;
  
  public DateFormatter() {}
  
  public DateFormatter(String format)
  {
    this.dateFormater = new SimpleDateFormat(format);
  }
  
  public boolean verify(String value)
  {
    try
    {
      ParsePosition index = new ParsePosition(0);
      this.dateFormater.parse(value, index);
      return index.getIndex() == value.length();
    }
    catch (Exception e) {}
    return false;
  }
}
