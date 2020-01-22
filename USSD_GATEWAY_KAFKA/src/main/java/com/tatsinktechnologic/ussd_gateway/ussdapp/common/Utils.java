/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.common;

/**
 *
 * @author olivier.tatsinkou
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Utils
{
  private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  
  public static int getNumCoreCPU()
  {
    Runtime rt = Runtime.getRuntime();
    return rt.availableProcessors();
  }
  
  public static Object loadKeyFromJofFile(String ojectFile)
    throws Exception
  {
    if (ojectFile == null) {
      return null;
    }
    ojectFile = ojectFile.trim();
    if (ojectFile.isEmpty()) {
      return null;
    }
    ObjectInputStream ostream = null;
    try
    {
      FileInputStream fin = new FileInputStream(ojectFile);
      ostream = new ObjectInputStream(fin);
      
      return ostream.readObject();
    }
    finally
    {
      if (ostream != null) {
        try
        {
          ostream.close();
        }
        catch (IOException ex) {}
      }
    }
  }
  
  public static int percent(int value, int max)
  {
    return (int)(value / max * 100.0D + 0.5D);
  }
  
  private static void byte2hex(byte b, StringBuffer buf)
  {
    int high = (b & 0xF0) >> 4;
    int low = b & 0xF;
    buf.append(HEX_CHARS[high]);
    buf.append(HEX_CHARS[low]);
  }
  
  public static String toHexString(byte[] block)
  {
    StringBuffer buf = new StringBuffer();
    int len = block.length;
    for (int i = 0; i < len; i++) {
      byte2hex(block[i], buf);
    }
    return buf.toString();
  }
}

