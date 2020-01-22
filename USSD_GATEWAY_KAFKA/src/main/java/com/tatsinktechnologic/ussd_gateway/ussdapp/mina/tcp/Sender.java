/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.mina.tcp;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.ussdapp.common.UssdMessage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class Sender
{
  private static final int TIME_WAIT_LOGIN_RSP = 30000;
  private static final int TIME_WAIT_RANDOM_STRING = 5000;
  private String pIp;
  private int pPort;
  private String hUser;
  private String hPass;
  private IoSession session1;
  private NioSocketConnector connector;
  private Logger logger;
  private final Object lock = new Object();
  private boolean haveResult;
  private boolean loginOk;
  private Cipher encoder;
  private boolean receivedRandomString;
  private String randomInfo;
  private MessageDigest md;
  
  public Sender(Logger logger, String pIp, int pPort, String hUser, String hPass)
    throws Exception
  {
    this.logger = logger;
    

    this.pIp = pIp;
    this.pPort = pPort;
    this.hUser = hUser;
    this.hPass = hPass;
    


    logger.info("make Nioconnector");
    this.connector = new NioSocketConnector();
    this.connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
    

    this.connector.setHandler(new ClientSessionHandler());
    
    this.connector.setDefaultRemoteAddress(new InetSocketAddress(pIp, pPort));
    this.connector.setConnectTimeoutMillis(10000L);
    

    this.md = MessageDigest.getInstance("SHA-1");
    if (this.md == null) {
      throw new Exception("Cant create object to serve authentication");
    }
  }
  
  public void setEncoder(Cipher encoder)
  {
    this.encoder = encoder;
  }
  
  private void connect()
    throws Exception
  {
    this.haveResult = false;
    this.loginOk = false;
    this.receivedRandomString = false;
    this.randomInfo = null;
    

    ConnectFuture future = this.connector.connect();
    future = future.awaitUninterruptibly();
    try
    {
      this.session1 = future.getSession();
      if (this.session1 == null) {
        throw new IOException("can't connect to server");
      }
      this.logger.info("connected to server");
    }
    catch (Exception ex)
    {
      this.logger.error(ex.getMessage(), ex);
      throw new IOException("can't connect to server");
    }
    this.logger.info("send login");
    try
    {
      login();
    }
    catch (Exception ex)
    {
      this.logger.warn("login has problem");
      this.session1.close(true);
      this.session1 = null;
      throw ex;
    }
  }
  
  private void login()
    throws Exception
  {
    synchronized (this.lock)
    {
      if (!this.haveResult)
      {
        if (!this.receivedRandomString)
        {
          try
          {
            this.lock.wait(5000L);
          }
          catch (InterruptedException ex) {}
          if (!this.receivedRandomString) {
            throw new Exception("Authenticated fail, server not available!");
          }
        }
        String hashRawData = this.hUser + this.randomInfo + this.hPass;
        byte[] hashBytes = hashRawData.getBytes("UTF-8");
        byte[] hashValue = this.md.digest(hashBytes);
        UssdMessage msg = new UssdMessage(100);
        msg.setEncryptedUssdString(hashValue);
        this.session1.write(msg);
        try
        {
          this.lock.wait(30000L);
        }
        catch (InterruptedException ex) {}
      }
      if (!this.loginOk) {
        throw new Exception("login fail!");
      }
    }
    this.logger.info("login success");
  }
  
  public synchronized void send(UssdMessage msg)
    throws Exception
  {
    String ussdString = msg.getUssdString();
    if (((this.encoder != null ? 1 : 0) & (ussdString != null ? 1 : 0)) != 0)
    {
      this.logger.info("encode ussd string");
      
      msg.setEncryptedUssdString(this.encoder.doFinal(ussdString.getBytes("UTF-16BE")));
      
      msg.setUssdString(null);
      this.logger.info("encryption complete");
    }
    this.logger.info("check is connected first");
    if (!isConnected())
    {
      this.logger.warn("connection down. Try reconnect");
      connect();
    }
    this.logger.info("send message");
    this.session1.write(msg);
  }
  
  public void stop()
  {
    if (this.session1 != null) {
      this.session1.close(true);
    }
    this.connector.dispose();
  }
  
  private boolean isConnected()
  {
    return (this.session1 != null) && (this.session1.isConnected());
  }
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder("Sender:\r\n");
    buf.append("Pear Ip: ");
    buf.append(this.pIp);
    buf.append("\r\nPear Port: ");
    buf.append(this.pPort);
    buf.append("\r\nHost user: ");
    buf.append(this.hUser);
    buf.append("\r\nHost pass: ");
    buf.append("******");
    
    return buf.toString();
  }
  
  private class ClientSessionHandler
    extends IoHandlerAdapter
  {
    private ClientSessionHandler() {}
    
    public void messageReceived(IoSession session, Object message)
    {
      if ((message instanceof UssdMessage))
      {
        UssdMessage loginRspMsg = (UssdMessage)message;
        if (loginRspMsg.getType() == 201) {
          synchronized (Sender.this.lock)
          {
            Sender.this.randomInfo = loginRspMsg.getMsisdn();
            if (Sender.this.randomInfo != null)
            {
              Sender.this.receivedRandomString = true;
              Sender.this.lock.notifyAll();
            }
          }
        } else if (loginRspMsg.getType() == 203) {
          synchronized (Sender.this.lock)
          {
            Sender.this.loginOk = (loginRspMsg.getDlgId() == 1);
            
            Sender.this.haveResult = true;
            Sender.this.lock.notifyAll();
          }
        }
      }
    }
    
    public void exceptionCaught(IoSession session, Throwable cause)
    {
      Sender.this.logger.error(cause);
      if ((cause instanceof IOException)) {
        session.close(true);
      }
    }
    
    public void sessionClosed(IoSession session)
      throws Exception
    {
      Sender.this.logger.info("session closed!");
      if (session == Sender.this.session1) {
        synchronized (Sender.this.lock)
        {
          Sender.this.haveResult = true;
          Sender.this.loginOk = false;
          Sender.this.randomInfo = null;
          Sender.this.receivedRandomString = false;
          Sender.this.lock.notifyAll();
        }
      }
    }
  }
}

