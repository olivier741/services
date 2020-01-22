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
package com.viettel.mmserver.basev1;

/**
 *
 * @author olivier.tatsinkou
 */
public abstract interface MmProcessAbstractMBean
{
  public abstract Integer getId();
  
  public abstract void start();
  
  public abstract void stop();
  
  public abstract void suspend();
  
  public abstract void resume();
  
  public abstract Thread.State getState();
  
  public abstract String getInfor();
  
  public abstract String loadParams();
  
  public abstract String saveParams(String paramString);
}

