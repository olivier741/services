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
public class BooleanLock
{
  private boolean value;
  
  public BooleanLock()
  {
    this(false);
  }
  
  public BooleanLock(boolean value)
  {
    this.value = value;
  }
  
  public synchronized void setValue(boolean newValue)
  {
    if (newValue != this.value)
    {
      this.value = newValue;
      notifyAll();
    }
  }
  
  public synchronized boolean waitToSetTrue(long msTimeOut)
    throws InterruptedException
  {
    boolean success = waitUntilFalse(msTimeOut);
    if (success) {
      setValue(true);
    }
    return success;
  }
  
  public synchronized boolean waitToSetFalse(long msTimeOut)
    throws InterruptedException
  {
    boolean success = waitUntilTrue(msTimeOut);
    if (success) {
      setValue(false);
    }
    return success;
  }
  
  public synchronized boolean waitUntilTrue(long msTimeOut)
    throws InterruptedException
  {
    return waitUntilStateIs(true, msTimeOut);
  }
  
  public synchronized boolean waitUntilFalse(long msTimeOut)
    throws InterruptedException
  {
    return waitUntilStateIs(false, msTimeOut);
  }
  
  public synchronized boolean isTrue()
  {
    return this.value;
  }
  
  public synchronized boolean isFalse()
  {
    return !this.value;
  }
  
  public synchronized boolean waitUntilStateIs(boolean state, long msTimeOut)
    throws InterruptedException
  {
    if (msTimeOut == 0L)
    {
      while (this.value != state) {
        wait();
      }
      return true;
    }
    long endTime = System.currentTimeMillis() + msTimeOut;
    long remainTime = msTimeOut;
    while ((this.value != state) && (remainTime > 0L))
    {
      wait(remainTime);
      remainTime = endTime - System.currentTimeMillis();
    }
    return this.value == state;
  }
}
