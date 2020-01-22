 package com.tatsinktechnologic.ussd_gateway.utils;
 
 public class Data
 {
   protected String name;
   protected String datatype;
   protected Object value;
   
   public Data(String name, String datatype, Object value)
   {
     this.name = name;
     this.datatype = datatype;
     this.value = value;
   }
   
   public String getName()
   {
     return this.name;
   }
   
   public void setName(String name)
   {
     this.name = name;
   }
   
   public String getDatatype()
   {
     return this.datatype;
   }
   
   public void setDatatype(String datatype)
   {
     this.datatype = datatype;
   }
   
   public Object getValue()
   {
     return this.value;
   }
   
   public void setValue(Object value)
   {
     this.value = value;
   }
 }


