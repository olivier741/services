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
package com.viettel.bccsgw.logging;

/**
 *
 * @author olivier.tatsinkou
 */
import java.util.Vector;

public class LogWriterThreadMrg
{
  private static Vector<LogWriter> threads;
  private static LogWriterThreadMrg instance = null;
  
  private LogWriterThreadMrg()
  {
    threads = new Vector();
  }
  
  public static synchronized LogWriterThreadMrg getInstance()
  {
    if (instance == null) {
      instance = new LogWriterThreadMrg();
    }
    return instance;
  }
  
  public void addThread(LogWriter lwThread)
  {
    threads.add(lwThread);
  }
  
  public void start(int threadIndex)
  {
    LogWriter thread = (LogWriter)threads.get(threadIndex);
    if (!thread.isRunning()) {
      thread.start();
    }
  }
  
  public void startAll()
  {
    for (LogWriter thread : threads) {
      if (!thread.isRunning()) {
        thread.start();
      }
    }
  }
  
  public void stop(int threadIndex)
  {
    LogWriter thread = (LogWriter)threads.get(threadIndex);
    thread.stop();
  }
  
  public void stopAll()
  {
    for (LogWriter thread : threads) {
      thread.stop();
    }
  }
}

