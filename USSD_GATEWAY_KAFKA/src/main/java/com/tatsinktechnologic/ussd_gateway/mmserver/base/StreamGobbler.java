/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.base;

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
