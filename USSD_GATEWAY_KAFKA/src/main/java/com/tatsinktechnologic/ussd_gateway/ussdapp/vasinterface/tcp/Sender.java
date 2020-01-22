/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.ussdapp.vasinterface.tcp;

/**
 *
 * @author olivier.tatsinkou
 */

import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasRequest;
import com.tatsinktechnologic.ussd_gateway.ussdapp.common.VasResponse;
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
  private boolean allMsgEncrypted;
  
  public Sender(Logger logger, String pIp, int pPort, String hUser, String hPass, boolean allMsgEncrypt)
    throws Exception
  {
    this.logger = logger;
    

    this.pIp = pIp;
    this.pPort = pPort;
    this.hUser = hUser;
    this.hPass = hPass;
    this.encoder = null;
    this.allMsgEncrypted = allMsgEncrypt;
    
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
        VasRequest msg = new VasRequest("1");
        msg.setEncryptedParams(hashValue);
        msg.setBizId(1);
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
  
  public synchronized void send(VasRequest req)   throws Exception
  {
    if ((req.getParams() != null) && ((req.isNeedEncrypt()) || (this.allMsgEncrypted)))
    {
      this.logger.info("message need to encrypt befor send");
      if (this.encoder == null) {
        throw new Exception("encoder not found. Cant send message");
      }
      req.setEncryptedParams(this.encoder.doFinal(req.getParams().getBytes("UTF-16BE")));
      
      req.setParams(null);
      
      req.setNeedEncrypt(true);
      this.logger.info("encryption complete");
    }
    this.logger.info("check is connected first");
    if (!isConnected())
    {
      this.logger.warn("connection down. Try reconnect");
      connect();
    }
    if (!req.isValid()) {
      throw new Exception("Request is invalid");
    }
    this.logger.info("send message");
    this.session1.write(req);
  }
  
  public synchronized void send(VasResponse rsp)   throws Exception
  {
    if ((rsp.getContent() != null) && ((rsp.isNeedEncrypt()) || (this.allMsgEncrypted)))
    {
      this.logger.info("message need to encrypt befor send");
      if (this.encoder == null) {
        throw new Exception("encoder not found. Cant send message");
      }
      rsp.setEncryptedContent(this.encoder.doFinal(rsp.getContent().getBytes("UTF-16BE")));
      
      rsp.setContent(null);
      
      rsp.setNeedEncrypt(true);
      this.logger.info("encryption complete");
    }
    this.logger.info("check is connected first");
    if (!isConnected())
    {
      this.logger.warn("connection down. Try reconnect");
      connect();
    }
    this.logger.info("send message");
    this.session1.write(rsp);
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
  
  private class ClientSessionHandler    extends IoHandlerAdapter
  {
    private ClientSessionHandler() {}
    
    public void messageReceived(IoSession session, Object message)
    {
      if ((message instanceof VasResponse))
      {
        VasResponse loginRspMsg = (VasResponse)message;
        if (loginRspMsg.getId().equals("2")) {
          synchronized (Sender.this.lock)
          {
            Sender.this.randomInfo = loginRspMsg.getContent();
            if (Sender.this.randomInfo != null)
            {
              Sender.this.receivedRandomString = true;
              Sender.this.lock.notifyAll();
            }
          }
        } else if (loginRspMsg.getId().equals("1")) {
          synchronized (Sender.this.lock)
          {
            Sender.this.loginOk = ((loginRspMsg.getContent() != null) && (loginRspMsg.getContent().equals("1")));
            

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
