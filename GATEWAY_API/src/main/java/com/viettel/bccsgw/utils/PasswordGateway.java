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
package com.viettel.bccsgw.utils;

/**
 *
 * @author olivier.tatsinkou
 */
import java.io.PrintStream;
import java.security.MessageDigest;
import sun.misc.BASE64Encoder;

public class PasswordGateway
{
  public static String encrypt(String plaintext)
    throws Exception
  {
    MessageDigest md = MessageDigest.getInstance("SHA-1");
    md.update(plaintext.getBytes("UTF-8"));
    byte[] raw = md.digest();
    String hash = new BASE64Encoder().encode(raw);
    return hash;
  }
  
  public static void main(String[] args)
  {
    try
    {
      MessageDigest md = null;
      md = MessageDigest.getInstance("SHA-1");
      md.update("123".getBytes("UTF-8"));
      byte[] raw = md.digest();
      String hash = new BASE64Encoder().encode(raw);
      System.out.println("hash-->" + hash);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
