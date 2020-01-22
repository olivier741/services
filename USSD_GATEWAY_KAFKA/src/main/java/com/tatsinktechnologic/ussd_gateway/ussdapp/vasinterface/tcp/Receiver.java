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
import com.tatsinktechnologic.ussd_gateway.utils.BlockQueue;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;
import javax.crypto.Cipher;
import org.apache.log4j.Logger;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class Receiver
{
  private String pIp;
  private int hPort;
  private String pUser;
  private String pPass;
  private Vector<IoSession> sessions;
  private NioSocketAcceptor acceptor;
  private Logger logger;
  private BlockQueue queue;
  private int connectorId;
  private Cipher decoder;
  private int idleTime;
  private MessageDigest md;
  private Hashtable<IoSession, String> randomInfos = new Hashtable();
  private boolean allMsgEncrypted;
  
  public Receiver(Logger logger, int connectorId, String pIp, int hPort, String pUser, String pPass, int idleTime, boolean allMsgEncrypt)
    throws Exception
  {
    this.logger = logger;
    

    this.connectorId = connectorId;
    this.pIp = pIp;
    this.hPort = hPort;
    this.pUser = pUser;
    this.pPass = pPass;
    this.decoder = null;
    this.idleTime = idleTime;
    this.sessions = new Vector();
    this.allMsgEncrypted = allMsgEncrypt;
    
    logger.info("make acceptor and open a port");
    this.acceptor = new NioSocketAcceptor();
    DefaultIoFilterChainBuilder chain = this.acceptor.getFilterChain();
    MdcInjectionFilter mdcInjectionFilter = new MdcInjectionFilter();
    chain.addLast("mdc", mdcInjectionFilter);
    

    ProtocolCodecFilter protocolFilter = new ProtocolCodecFilter(new ObjectSerializationCodecFactory());
    chain.addLast("codec", protocolFilter);
    this.acceptor.setHandler(new ServerSessionHandler());
    this.md = MessageDigest.getInstance("SHA-1");
    if (this.md == null) {
      throw new Exception("Cant create object to serve authentication");
    }
  }
  
  public void setDecoder(Cipher decoder)
  {
    this.decoder = decoder;
  }
  
  public void setQueue(BlockQueue queue)
  {
    this.queue = queue;
  }
  
  private boolean haveAlreadySession(IoSession session)
  {
    return this.sessions.contains(session);
  }
  
  public void stop()
  {
    this.acceptor.unbind();
    this.acceptor.dispose();
  }
  
  public void start()
    throws Exception
  {
    this.logger.info("bind to port: " + this.hPort);
    this.acceptor.setReuseAddress(true);
    this.acceptor.bind(new SocketAddress[] { new InetSocketAddress(this.hPort) });
    this.logger.info("bind success");
  }
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder("Receiver:\r\n");
    buf.append("Pear Ip: ");
    buf.append(this.pIp);
    buf.append("\r\nHost Port: ");
    buf.append(this.hPort);
    buf.append("\r\nPear user: ");
    buf.append(this.pUser);
    buf.append("\r\nPear pass: ");
    buf.append("******");
    
    return buf.toString();
  }
  
  private synchronized String decode(byte[] input)
    throws Exception
  {
    return new String(this.decoder.doFinal(input), "UTF-16BE");
  }
  
  private class ServerSessionHandler
    extends IoHandlerAdapter
  {
    private ServerSessionHandler() {}
    
    public void sessionOpened(IoSession session)
    {
      Receiver.this.logger.info("have a new connection, id = " + session.getId());
      



      Receiver.this.logger.info("check incoming ip");
      InetSocketAddress address = (InetSocketAddress)session.getRemoteAddress();
      String ip = address.getAddress().getHostAddress();
      if (!ip.equals(Receiver.this.pIp))
      {
        Receiver.this.logger.warn("ip not allow: " + ip);
        session.close(true);
      }
      else
      {
        if (Receiver.this.idleTime > 0) {
          session.getConfig().setIdleTime(IdleStatus.READER_IDLE, Receiver.this.idleTime);
        }
        String randomInfo = Receiver.this.getRandomString();
        Receiver.this.randomInfos.put(session, randomInfo);
        
        VasResponse randomInfoMsg = new VasResponse("2");
        randomInfoMsg.setContent(randomInfo);
        session.write(randomInfoMsg);
        Receiver.this.logger.info("send random string successed!");
      }
    }
    
    public void messageReceived(IoSession session, Object message)
    {
      Receiver.this.logger.info("receive message");
      if (Receiver.this.haveAlreadySession(session))
      {
        if ((message instanceof VasRequest)) {
          try
          {
            VasRequest msg = (VasRequest)message;
            if (!msg.isValid())
            {
              Receiver.this.logger.info("Request is invalid");
              return;
            }
            msg.setSendRecvTime(System.currentTimeMillis());
            if ((Receiver.this.allMsgEncrypted) && (msg.getEncryptedParams() == null) && (msg.getParams() != null))
            {
              Receiver.this.logger.warn("ussd string is NOT encrypted. Reject this message");
            }
            else
            {
              if ((msg.getEncryptedParams() != null) && (msg.getParams() == null))
              {
                Receiver.this.logger.info("message is encrypted. Decode it first");
                if (Receiver.this.decoder == null)
                {
                  Receiver.this.logger.error("no decoder to decode");
                  return;
                }
                try
                {
                  msg.setParams(Receiver.this.decode(msg.getEncryptedParams()));
                  

                  msg.setNeedEncrypt(true);
                }
                catch (Exception e)
                {
                  Receiver.this.logger.error("decode fail", e);
                  return;
                }
                Receiver.this.logger.info("decode success");
              }
              else
              {
                msg.setNeedEncrypt(false);
                msg.setLoggedParams();
              }
              msg.setConnectorId(Receiver.this.connectorId);
              Receiver.this.logger.info("put message in queue");
              Receiver.this.queue.enqueue(msg);
            }
          }
          catch (IndexOutOfBoundsException ex)
          {
            Receiver.this.logger.error("@connector.recvqueue - Receive queue is full!");
          }
        } else if ((message instanceof VasResponse)) {
          try
          {
            VasResponse msg = (VasResponse)message;
            msg.setSendRecvTime(System.currentTimeMillis());
            if ((Receiver.this.allMsgEncrypted) && (msg.getEncryptedContent() == null) && (msg.getContent() != null))
            {
              Receiver.this.logger.warn("ussd string is NOT encrypted. Reject this message");
            }
            else
            {
              if ((msg.getEncryptedContent() != null) && (msg.getContent() == null))
              {
                Receiver.this.logger.info("message is encrypted. Decode it first");
                if (Receiver.this.decoder == null)
                {
                  Receiver.this.logger.error("no decoder to decode");
                  return;
                }
                try
                {
                  msg.setContent(Receiver.this.decode(msg.getEncryptedContent()));
                  

                  msg.setNeedEncrypt(true);
                }
                catch (Exception e)
                {
                  Receiver.this.logger.error("decode fail", e);
                  return;
                }
                Receiver.this.logger.info("decode success");
              }
              else
              {
                msg.setNeedEncrypt(false);
                msg.setLoggedContent();
              }
              msg.setConnectorId(Receiver.this.connectorId);
              Receiver.this.logger.info("put message in queue");
              Receiver.this.queue.enqueue(msg);
            }
          }
          catch (IndexOutOfBoundsException ex)
          {
            Receiver.this.logger.error("@connector.recvqueue - Receive queue is full!");
          }
        }
      }
      else if ((message instanceof VasRequest))
      {
        VasRequest loginReq = (VasRequest)message;
        if (loginReq.getBizId() == 1)
        {
          VasResponse loginRsp = loginReq.makeResponse();
          Receiver.this.logger.info("login message. check user/pass");
          try
          {
            byte[] clientHash = loginReq.getEncryptedParams();
            if (!Receiver.this.randomInfos.containsKey(session))
            {
              Receiver.this.logger.warn("Dont know this session. Reject connection");
              session.close(true);
            }
            else
            {
              String randomInfo = (String)Receiver.this.randomInfos.get(session);
              String serverHashRawData = Receiver.this.pUser + randomInfo + Receiver.this.pPass;
              byte[] serverHashBytes = serverHashRawData.getBytes("UTF-8");
              byte[] serverHash = Receiver.this.md.digest(serverHashBytes);
              if (Arrays.equals(clientHash, serverHash))
              {
                Receiver.this.logger.info("authenticate pass. Send login response message back");
                loginRsp.setContent("1");
                session.write(loginRsp);
                

                Receiver.this.sessions.add(session);
              }
              else
              {
                Receiver.this.logger.warn("user/pass wrong. Reject connection");
                loginRsp.setContent("0");
                session.write(loginRsp);
                session.close(true);
              }
            }
          }
          catch (Exception ex)
          {
            Receiver.this.logger.warn("Cant authentication cuz internal error. Reject connection", ex);
            loginRsp.setContent("0");
            session.write(loginRsp);
            session.close(true);
          }
        }
        else
        {
          Receiver.this.logger.warn("message is not login message. close session!");
          session.close(true);
        }
      }
    }
    
    public void sessionIdle(IoSession session, IdleStatus status)
    {
      Receiver.this.logger.info("session " + session.getId() + " idle. Close it.");
      session.close(true);
    }
    
    public void exceptionCaught(IoSession session, Throwable cause)
    {
      Receiver.this.logger.error("session " + session.getId() + " have error: " + cause.getMessage(), cause);
      session.close(true);
    }
    
    public void sessionClosed(IoSession session)
    {
      Receiver.this.logger.info("session " + session.getId() + " closed!");
      
      Receiver.this.sessions.remove(session);
      Receiver.this.randomInfos.remove(session);
    }
  }
  
  private String getRandomString()
  {
    long result = System.currentTimeMillis();
    return String.valueOf(result);
  }
}

