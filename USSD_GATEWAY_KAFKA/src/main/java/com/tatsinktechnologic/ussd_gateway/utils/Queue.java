 package com.tatsinktechnologic.ussd_gateway.utils;
 
 import java.io.Serializable;
 import java.util.LinkedList;
 import java.util.ListIterator;
 
 public class Queue
   implements Serializable
 {
   protected int maxQueueSize = 0;
   protected LinkedList queueData = new LinkedList();
   protected Object mutex;
   private static final long serialVersionUID = 20091803113026L;
   
   public Queue()
   {
     this.mutex = this;
   }
   
   public Queue(int maxSize)
   {
     this.maxQueueSize = maxSize;
     this.mutex = this;
   }
   
   public int size()
   {
     synchronized (this.mutex)
     {
       return this.queueData.size();
     }
   }
   
   public boolean isEmpty()
   {
     synchronized (this.mutex)
     {
       return this.queueData.isEmpty();
     }
   }
   
   public Object dequeue()
   {
     synchronized (this.mutex)
     {
       Object first = null;
       if (size() > 0) {
         first = this.queueData.removeFirst();
       }
       return first;
     }
   }
   
   public Object dequeue(Object obj)
   {
     Object found;
     synchronized (this.mutex)
     {
       found = find(obj);
       if (found != null) {
         this.queueData.remove(found);
       }
     }
     return found;
   }
   
   public void enqueue(Object obj)
     throws IndexOutOfBoundsException
   {
     synchronized (this.mutex)
     {
       if ((this.maxQueueSize > 0) && (size() >= this.maxQueueSize)) {
         throw new IndexOutOfBoundsException("Queue is full. Element not added.");
       }
       this.queueData.add(obj);
     }
   }
   
   public Object find(Object obj)
   {
     synchronized (this.mutex)
     {
       ListIterator iter = this.queueData.listIterator(0);
       while (iter.hasNext())
       {
         Object current = iter.next();
         if (current.equals(obj)) {
           return current;
         }
       }
     }
     return null;
   }
   
   public Object[] toArray()
   {
     return this.queueData.toArray();
   }
 }


