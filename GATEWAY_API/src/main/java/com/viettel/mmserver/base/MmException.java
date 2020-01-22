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
package com.viettel.mmserver.base;

/**
 *
 * @author olivier.tatsinkou
 */
public class MmException
  extends Exception
{
  private static final long serialVersionUID = -777216335204861186L;
  
  public MmException() {}
  
  public MmException(String s)
  {
    super(s);
  }
  
  public MmException(Throwable cause)
  {
    super(cause);
  }
  
  public MmException(String message, Throwable cause)
  {
    super(message, cause);
  }
}