 package com.tatsinktechnologic.ussd_gateway.utils;
 
 public class DataSource
 {
   protected Data[] data;
   
   public DataSource() {}
   
   public DataSource(Data[] data)
   {
     if (data != null)
     {
       this.data = new Data[data.length];
       System.arraycopy(data, 0, this.data, 0, data.length);
     }
   }
   
   public void add(Data d)
   {
     if (this.data == null)
     {
       this.data = new Data[] { d };
     }
     else
     {
       Data[] newData = new Data[this.data.length + 1];
       System.arraycopy(this.data, 0, newData, 0, this.data.length);
       newData[this.data.length] = d;
       this.data = newData;
     }
   }
   
   public Data getData(String name)
   {
     if ((this.data == null) || (this.data.length == 0)) {
       return null;
     }
     for (Data d : this.data) {
       if (d.getName().equals(name)) {
         return d;
       }
     }
     return null;
   }
 }


