/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.mmserver.log.appender;

/**
 *
 * @author olivier.tatsinkou
 */
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ServerMonitor
  extends AppenderThreadMX
{
  public static final int SLEEP_TIME = 1000;
  private int port;
  private ServerSocket serverSocket = null;
  private SocketWriterManager swm = null;
  
  public ServerMonitor(int port)
  {
    super("ServerMonitor");
    try
    {
      registerAgent("Tools:type=log,name=SocketListener");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    this.port = port;
  }
  
  protected void process()
  {
    Socket socket = null;
    try
    {
      if (this.serverSocket == null) {
        this.serverSocket = new ServerSocket(this.port);
      }
      if (this.serverSocket == null) {
        Thread.sleep(1000L);
      }
      if (this.serverSocket != null) {
        socket = this.serverSocket.accept();
      }
    }
    catch (InterruptedIOException e)
    {
      System.out.println("exception accepting socket." + e);
    }
    catch (SocketException e)
    {
      System.out.println("Exception when acceptting new socket");
    }
    catch (IOException e)
    {
      System.out.println("exception accepting socket." + e);
    }
    catch (Exception e)
    {
      System.out.println("exception accepting socket." + e);
    }
    catch (Throwable tt)
    {
      System.out.println("exception accepting socket." + tt);
    }
    if (socket != null)
    {
      InetAddress remoteAddress = socket.getInetAddress();
      if (this.swm != null) {
        this.swm.addSocketWriter(socket);
      }
    }
  }
  
  protected void prepareStart()
  {
    if (this.serverSocket == null) {
      try
      {
        this.serverSocket = new ServerSocket(this.port);
      }
      catch (Exception e)
      {
        System.out.println("exception when create server socket." + e);
      }
    }
  }
  
  public void stop()
  {
    super.stop();
    if (this.serverSocket != null) {
      try
      {
        this.serverSocket.close();
      }
      catch (IOException ex)
      {
        System.out.println("Could not stop server Socket at port " + this.port);
      }
    }
  }
  
  public void setSwm(SocketWriterManager swm)
  {
    this.swm = swm;
  }
}

