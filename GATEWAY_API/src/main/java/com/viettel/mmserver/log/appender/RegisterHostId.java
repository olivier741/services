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
package com.viettel.mmserver.log.appender;

/**
 *
 * @author olivier.tatsinkou
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterHostId
{
  private ArrayList<String> arrayHost = null;
  private List<String> listHost = null;
  private static RegisterHostId registerHostIdInstance = null;
  
  private RegisterHostId()
  {
    this.arrayHost = new ArrayList();
    this.listHost = Collections.synchronizedList(this.arrayHost);
  }
  
  public static RegisterHostId getInstance()
  {
    if (registerHostIdInstance == null) {
      registerHostIdInstance = new RegisterHostId();
    }
    return registerHostIdInstance;
  }
  
  public void addHost(String hostId)
  {
    this.listHost.add(hostId);
    int n = this.listHost.size();
  }
  
  public int containHost(String hostId)
  {
    for (int x = 0; x < 5; x++)
    {
      int n = this.listHost.size();
      for (int i = 0; i < n; i++) {
        if (((String)this.listHost.get(i)).equals(hostId))
        {
          this.listHost.remove(i);
          return i;
        }
      }
      try
      {
        Thread.sleep(1000L);
      }
      catch (InterruptedException ex)
      {
        Logger.getLogger(RegisterHostId.class.getName()).log(Level.SEVERE, null, ex);
        return -1;
      }
    }
    return -1;
  }
}