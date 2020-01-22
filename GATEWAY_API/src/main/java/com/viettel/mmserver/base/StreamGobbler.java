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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;

class StreamGobbler
  extends Thread
{
  private InputStream is;
  private String type;
  private Logger log;
  
  StreamGobbler(InputStream is, String type)
  {
    this.is = is;
    this.type = type;
    this.log = Logger.getRootLogger();
  }
  
  public void run()
  {
    try
    {
      InputStreamReader isr = new InputStreamReader(this.is);
      BufferedReader br = new BufferedReader(isr);
      String line = null;
      while ((line = br.readLine()) != null) {
        this.log.info(line);
      }
    }
    catch (IOException ioe)
    {
      this.log.error("Error in execute remote command");
      this.log.error(ioe);
    }
  }
}
