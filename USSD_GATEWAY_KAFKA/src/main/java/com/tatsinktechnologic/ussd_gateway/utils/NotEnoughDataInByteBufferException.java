 package com.tatsinktechnologic.ussd_gateway.utils;
 
 public class NotEnoughDataInByteBufferException
   extends Exception
 {
   private int available;
   private int expected;
   
   public NotEnoughDataInByteBufferException(int p_available, int p_expected)
   {
     super("Not enough data in byte buffer. Expected " + p_expected + ", available: " + p_available + ".");
     
 
     this.available = p_available;
     this.expected = p_expected;
   }
   
   public NotEnoughDataInByteBufferException(String s)
   {
     super(s);
     this.available = 0;
     this.expected = 0;
   }
   
   public int getAvailable()
   {
     return this.available;
   }
   
   public int getExpected()
   {
     return this.expected;
   }
 }


