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
package com.viettel.bccsgw.security;

/**
 *
 * @author olivier.tatsinkou
 */
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class PassTranformer
{
  private static byte[] key = { -95, -29, -62, 25, 25, -83, -18, -85 };
  private static String algorithm = "DES";
  private static SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
  private static Cipher encoder;
  private static Cipher decoder;
  
  static
  {
    try
    {
      encoder = Cipher.getInstance(algorithm);
      encoder.init(1, keySpec);
      decoder = Cipher.getInstance(algorithm);
      decoder.init(2, keySpec);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  private static byte[] _encrypt(byte[] arrByte)
    throws Exception
  {
    return encoder.doFinal(arrByte);
  }
  
  private static byte[] _decrypt(byte[] arrByte)
    throws Exception
  {
    return decoder.doFinal(arrByte);
  }
  
  public static String encrypt(String str)
    throws Exception
  {
    return toHexa(_encrypt(str.getBytes()));
  }
  
  public static String decrypt(String str)
    throws Exception
  {
    return new String(_decrypt(ByteUtils.stringToBytes(str)));
  }
  
  protected static final byte[] Hexhars = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
  
  public static String toHexa(byte[] b)
  {
    StringBuilder s = new StringBuilder(2 * b.length);
    for (int i = 0; i < b.length; i++)
    {
      int v = b[i] & 0xFF;
      
      s.append((char)Hexhars[(v >> 4)]);
      s.append((char)Hexhars[(v & 0xF)]);
    }
    return s.toString();
  }
}
