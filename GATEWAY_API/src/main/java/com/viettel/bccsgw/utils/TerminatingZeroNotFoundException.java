package com.viettel.bccsgw.utils;
 
 public class TerminatingZeroNotFoundException
   extends Exception
 {
   public TerminatingZeroNotFoundException()
   {
     super("Terminating zero not found in buffer.");
   }
   
   public TerminatingZeroNotFoundException(String s)
   {
     super(s);
   }
 }
