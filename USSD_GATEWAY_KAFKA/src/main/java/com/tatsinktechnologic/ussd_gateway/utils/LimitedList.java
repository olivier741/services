 package com.tatsinktechnologic.ussd_gateway.utils;
 
 import java.io.Serializable;
 
 public class LimitedList
   implements Serializable
 {
   private int max;
   private Object[] list = new Object[0];
   private static final long serialVersionUID = 20091803113026L;
   
   public LimitedList(int maxSize)
   {
     this.max = maxSize;
   }
   
   public Object get(int index)
   {
     if ((index < 0) || (index >= length())) {
       throw new ArrayIndexOutOfBoundsException(index);
     }
     return this.list[index];
   }
   
   public void add(Object ob)
   {
     if (ob == null) {
       return;
     }
     if (find(ob) != null) {
       remove(ob);
     }
     int length = length();
     if (length == 0)
     {
       this.list = new Object[] { ob };
       return;
     }
     if (length < this.max)
     {
       Object[] newList = new Object[length + 1];
       System.arraycopy(this.list, 0, newList, 0, length);
       newList[length] = ob;
       this.list = newList;
     }
     else
     {
       Object[] newList = new Object[length];
       System.arraycopy(this.list, 1, newList, 0, length - 1);
       newList[(length - 1)] = ob;
       this.list = newList;
     }
   }
   
   public void remove(Object ob)
   {
     if (find(ob) == null) {
       return;
     }
     int length = length();
     Object[] newList = new Object[length - 1];
     int i = 0;
     for (Object aOb : this.list) {
       if ((aOb == null) || (!aOb.equals(ob)))
       {
         newList[i] = aOb;
         i++;
       }
     }
     this.list = newList;
   }
   
   public void replace(Object ob, int index)
   {
     if ((index < 0) || (index >= length())) {
       throw new ArrayIndexOutOfBoundsException(index);
     }
     this.list[index] = ob;
   }
   
   public Object find(Object ob)
   {
     if ((this.list == null) || (this.list.length == 0)) {
       return null;
     }
     for (Object aOb : this.list) {
       if ((aOb != null) && (aOb.equals(ob))) {
         return aOb;
       }
     }
     return null;
   }
   
   public Object[] getElements()
   {
     if (this.list == null) {
       return new Object[0];
     }
     Object[] ob = new Object[this.list.length];
     System.arraycopy(this.list, 0, this.list, 0, this.list.length);
     return ob;
   }
   
   public int length()
   {
     return this.list == null ? 0 : this.list.length;
   }
   
   public int capacity()
   {
     return this.max;
   }
   
   public void clear()
   {
     this.list = null;
   }
 }


