package com.viettel.bccsgw.utils;
 
 import java.util.LinkedList;
 
 public class BlockQueue
   extends Queue
 {
   private static final long serialVersionUID = 20091803113026L;
   private static final int TIMEWAIT = 2000;
   private int timeout;
   
   public BlockQueue()
   {
     this(2000);
   }
   
   public BlockQueue(int timeout)
   {
     this.timeout = timeout;
   }
   
   public BlockQueue(int timeout, int maxSize)
   {
     super(maxSize);
     this.timeout = timeout;
   }
   
   public void enqueue(Object obj)
   {
     synchronized (this.mutex)
     {
       if ((this.maxQueueSize > 0) && (size() >= this.maxQueueSize)) {
         throw new IndexOutOfBoundsException("Queue is full. Element not added.");
       }
       this.queueData.add(obj);
       this.mutex.notifyAll();
     }
   }
   
   public Object dequeue()
   {
     synchronized (this.mutex)
     {
       Object first = null;
       if (size() > 0) {
         first = this.queueData.removeFirst();
       } else {
         try
         {
           if (this.timeout == 0)
           {
             while (isEmpty()) {
               this.mutex.wait();
             }
             first = this.queueData.removeFirst();
           }
           else
           {
             this.mutex.wait(this.timeout);
             if (size() > 0) {
               first = this.queueData.removeFirst();
             }
           }
         }
         catch (InterruptedException ex) {}
       }
       return first;
     }
   }
 }


