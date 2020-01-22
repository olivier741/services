 package com.tatsinktechnologic.ussd_gateway.utils;
 
 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 
 public abstract class Console
   extends ProcessThread
 {
   protected BufferedReader in;
   
   public Console()
   {
     super("console");
     setPriority(1);
     this.in = new BufferedReader(new InputStreamReader(System.in));
   }
   
   protected void process()
   {
     try
     {
       String c = this.in.readLine();
       process(c);
     }
     catch (IOException e)
     {
       e.printStackTrace();
     }
   }
   
   protected abstract void process(String paramString);
 }



