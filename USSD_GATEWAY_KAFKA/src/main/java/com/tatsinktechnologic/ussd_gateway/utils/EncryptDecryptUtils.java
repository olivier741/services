/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.utils;

/**
 *
 * @author olivier.tatsinkou
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptDecryptUtils
{
  private static byte[] key = { -95, -29, -62, 25, 25, -83, -18, -85 };
  private static String algorithm = "DES";
  private static SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
  
  public EncryptDecryptUtils()
  {
    keySpec = new SecretKeySpec(key, algorithm);
  }
  
  public static byte[] encrypt(byte[] arrByte)
    throws Exception
  {
    Cipher cipher = Cipher.getInstance(algorithm);
    cipher.init(1, keySpec);
    byte[] data = cipher.doFinal(arrByte);
    
    return data;
  }
  
  public static byte[] decrypt(byte[] arrByte)
    throws Exception
  {
    Cipher cipher = Cipher.getInstance(algorithm);
    cipher.init(2, keySpec);
    return cipher.doFinal(arrByte);
  }
  
  public static void main(String[] args)
  {
    encryptFile("./src/default session", "./src/default session.encrypted");
  }
  
  public static String decryptToString()
    throws Exception
  {
    String input = "";
    String[] arrTemp = input.split("#");
    byte[] c = new byte[arrTemp.length];
    int i = 0;
    for (String a : arrTemp)
    {
      byte b = new Byte(a).byteValue();
      System.out.println(b);
      c[i] = b;
      i++;
    }
    return new String(decrypt(c));
  }
  
  public static void encryptFile(String originalFilePath, String encryptedFilePath)
  {
    try
    {
      FileInputStream stream = new FileInputStream(originalFilePath);
      OutputStream out = new FileOutputStream(encryptedFilePath);
      int bytesRead = 0;
      byte[] buffer = new byte[8192];
      while ((bytesRead = stream.read(buffer, 0, 8192)) != -1)
      {
        byte[] cloneBuffer = new byte[bytesRead];
        if (bytesRead < buffer.length) {
          for (int i = 0; i < bytesRead; i++) {
            cloneBuffer[i] = buffer[i];
          }
        }
        out.write(encrypt(cloneBuffer));
      }
      stream.close();
      out.close();
    }
    catch (FileNotFoundException fex)
    {
      fex.printStackTrace();
    }
    catch (IOException iex)
    {
      iex.printStackTrace();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public static void decryptFile(String encryptedFilePath, String decryptedFilePath)
  {
    try
    {
      FileInputStream stream = new FileInputStream(encryptedFilePath);
      OutputStream out = new FileOutputStream(decryptedFilePath);
      int bytesRead = 0;
      byte[] buffer = new byte[8192];
      while ((bytesRead = stream.read(buffer, 0, 8192)) != -1)
      {
        System.out.println(bytesRead);
        byte[] cloneBuffer = new byte[bytesRead];
        if (bytesRead < buffer.length) {
          for (int i = 0; i < bytesRead; i++) {
            cloneBuffer[i] = buffer[i];
          }
        }
        out.write(decrypt(cloneBuffer));
      }
      stream.close();
      out.close();
    }
    catch (FileNotFoundException fex)
    {
      fex.printStackTrace();
    }
    catch (IOException iex)
    {
      iex.printStackTrace();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public static String decryptFile(String encryptedFilePath)
  {
    String returnValue = "";
    try
    {
      FileInputStream stream = new FileInputStream(encryptedFilePath);
      int bytesRead = 0;
      byte[] buffer = new byte[8192];
      while ((bytesRead = stream.read(buffer, 0, 8192)) != -1)
      {
        System.out.println(bytesRead);
        byte[] cloneBuffer = new byte[bytesRead];
        if (bytesRead < buffer.length) {
          for (int i = 0; i < bytesRead; i++) {
            cloneBuffer[i] = buffer[i];
          }
        }
        returnValue = returnValue + new String(decrypt(cloneBuffer));
      }
      stream.close();
    }
    catch (FileNotFoundException fex)
    {
      fex.printStackTrace();
    }
    catch (IOException iex)
    {
      iex.printStackTrace();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return returnValue;
  }
}

