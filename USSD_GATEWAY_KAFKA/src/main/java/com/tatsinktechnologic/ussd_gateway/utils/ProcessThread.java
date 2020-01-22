 package com.tatsinktechnologic.ussd_gateway.utils;
 
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import org.apache.log4j.Logger;
 
 public abstract class ProcessThread
   implements Runnable
 {
   public static final int RUNNING = 0;
   public static final int STOPPED = 1;
   public static final int STOPPING = 2;
   protected String threadName;
   protected boolean running;
   protected int status;
   protected String lastTime;
   protected int priority;
   protected Thread t;
   protected final Object lock = new Object();
   protected Logger logger;
   protected boolean dumpStatus;
   protected long lastExecuteTime;
   
   protected ProcessThread()
   {
     this("Unknown");
   }
   
   public ProcessThread(String threadName)
   {
     this.threadName = threadName;
     this.logger = Logger.getLogger(threadName);
     this.priority = 5;
   }
   
   public void run()
   {
     setProcessStatus(0);
     this.logger.info(this.threadName + " is starting to run");
     prepareStart();
     while (this.running) {
       try
       {
         process();
         if (this.dumpStatus) {
           this.logger.debug(this.threadName + "_" + Thread.currentThread().getName() + " is running");
         }
         this.lastExecuteTime = System.currentTimeMillis();
         Thread.yield();
       }
       catch (RuntimeException ex)
       {
         this.logger.error("runtime exception", ex);
         this.logger.info("continue processing ...");
       }
     }
     this.logger.info(this.threadName + " is dead");
     setProcessStatus(1);
   }
   
   public boolean isRunning()
   {
     return this.running;
   }
   
   public boolean isStopping()
   {
     return this.status == 2;
   }
   
   protected abstract void process();
   
   protected void prepareStart() {}
   
   protected void prepareStop() {}
   
   public void start()
   {
     if (!this.running)
     {
       this.lastTime = getTime(System.currentTimeMillis());
       this.running = true;
       this.logger.info("starting " + this.threadName + " process...");
       this.t = new Thread(this);
       this.t.setPriority(this.priority);
       this.t.setName(this.threadName);
       this.t.start();
       setProcessStatus(0);
       this.logger.info(this.threadName + " process  is started");
     }
     else
     {
       this.logger.info(this.threadName + " process  is started");
     }
   }
   
   private String getTime(long time)
   {
     SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
     return formatter.format(new Date(time));
   }
   
   public void stop()
   {
     if (this.running)
     {
       setProcessStatus(2);
       this.lastTime = getTime(System.currentTimeMillis());
       prepareStop();
       this.logger.info("stop " + this.threadName + " process");
       this.running = false;
       synchronized (this.lock)
       {
         this.lock.notifyAll();
       }
       if (this.t != null) {
         this.t.interrupt();
       }
       try
       {
         if ((this.t != null) && (this.t.isAlive()))
         {
           this.logger.info("waiting " + this.threadName + " process stop...");
           this.t.join();
         }
       }
       catch (InterruptedException ex)
       {
         this.logger.error("stop process exception:" + ex);
       }
       finally
       {
         this.logger.info(this.threadName + " process is stopped");
       }
       setProcessStatus(1);
     }
   }
   
   public void restart()
   {
     stop();
     
     start();
   }
   
   public void destroy()
   {
     stop();
   }
   
   public String getThreadName()
   {
     return this.threadName;
   }
   
   public void setThreadName(String threadName)
   {
     this.threadName = threadName;
     this.logger = Logger.getLogger(threadName);
   }
   
   protected void setProcessStatus(int status)
   {
     this.status = status;
   }
   
   protected int getProcessStatus()
   {
     return this.status;
   }
   
   public int getPriority()
   {
     return this.priority;
   }
   
   public void setPriority(int priority)
   {
     this.priority = priority;
   }
   
   public void setDumpThread(boolean dumpStatus)
   {
     this.logger.info("set dump thread is " + dumpStatus);
     this.dumpStatus = dumpStatus;
   }
   
   public String getInfor()
   {
     StringBuffer buff = new StringBuffer();
     buff.append("Thread Name:" + this.threadName);
     buff.append("\r\nLast action(start/stop):" + this.lastTime);
     buff.append("\r\nLast time:" + getTime(this.lastExecuteTime));
     buff.append("\r\nStatus:" + getStatusDesc());
     buff.append("\r\nPriority:" + this.priority);
     return buff.toString();
   }
   
   public String getStatusDesc()
   {
     switch (this.status)
     {
     case 0: 
       return "running";
     case 1: 
       return "stopped";
     }
     return "stopping";
   }
   
   public void setLogger(Logger logger)
   {
     this.logger = logger;
   }
   
   public String[] getConfigs()
   {
     return new String[0];
   }
 }


