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
import com.viettel.mmserver.base.ProcessThreadMX;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.apache.log4j.Logger;

public class SecurityFileChannel
  extends ProcessThreadMX
{
  private Map<String, Vector<byte[]>> contents;
  private Map<String, File> files;
  private static SecurityFileChannel instance = null;
  
  public static synchronized SecurityFileChannel getInstance()
  {
    if (instance == null) {
      instance = new SecurityFileChannel();
    }
    return instance;
  }
  
  private SecurityFileChannel()
  {
    super("FileChannel", "Security file channnel");
    this.contents = new HashMap();
    this.files = new HashMap();
    try
    {
      registerAgent("logging:type=LogWriter, name=" + this.threadName);
    }
    catch (Exception ex)
    {
      this.logger.error("Register JMX agent error: " + ex);
    }
    super.start();
  }
  
  public void start()
  {
    super.start();
  }
  
  public void stop()
  {
    super.stop();
  }
  
  public void writeLn(String dir, String fileName, String content)
    throws IOException
  {
    String path = dir + File.separator + fileName;
    File file = (File)this.files.get(path);
    if (file == null)
    {
      File directory = new File(dir);
      if (!directory.exists())
      {
        this.logger.info("Directory " + dir + " does not exit, " + "created new directory");
        
        directory.mkdir();
      }
      file = new File(path);
      this.files.put(path, file);
    }
    Vector<byte[]> fBytes = (Vector)this.contents.get(path);
    if (fBytes == null)
    {
      fBytes = new Vector();
      this.contents.put(path, fBytes);
    }
    content = content + "\n";
    byte[] bytes = content.getBytes();
    fBytes.add(bytes);
    this.logger.debug("A message added to cache");
  }
  
  private void writeAll()
    throws IOException
  {
    Iterator iter = this.contents.keySet().iterator();
    while (iter.hasNext())
    {
      String path = (String)iter.next();
      Vector<byte[]> bytes = (Vector)this.contents.get(path);
      if (bytes.isEmpty()) {
        return;
      }
      File file = (File)this.files.get(path);
      if (file != null) {
        write(file, bytes);
      }
    }
  }
  
  private void write(File file, Vector<byte[]> bytes)
    throws IOException
  {
    FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
    this.logger.info("Lock file for write operation");
    FileLock flock = channel.lock();
    for (int i = 0; i < bytes.size(); i++)
    {
      byte[] aMsg = (byte[])bytes.get(i);
      ByteBuffer bf = ByteBuffer.wrap(aMsg);
      channel.write(bf, file.length());
      this.logger.debug("Writen a message to file");
    }
    bytes.clear();
    flock.release();
    channel.close();
    this.logger.info("File unlocked");
  }
  
  protected void process()
  {
    try
    {
      writeAll();
    }
    catch (IOException ex)
    {
      this.logger.error("Error when write to file: " + ex);
      ex.printStackTrace();
    }
    try
    {
      Thread.sleep(1000L);
    }
    catch (InterruptedException ex)
    {
      this.logger.error("Interupted thead: " + ex);
      ex.printStackTrace();
    }
  }
}
