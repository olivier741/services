/*
 * Copyright 2019 olivier.tatsinkou.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.viettel.bccsgw.sendmt;

/**
 *
 * @author olivier.tatsinkou
 */
import com.viettel.bccsgw.utils.Hex;
import com.viettel.bccsgw.utils.Protocol;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import sun.misc.BASE64Encoder;

public class MtStub
{
  protected Protocol protocol;
  private Object lock = new Object();
  private Logger logger = Logger.getLogger(MtStub.class);
  private HttpClient httpclient;
  private BASE64Encoder encoder;
  private String xmlns;
  private String username;
  private String password;
  
  public MtStub(String url, String xmlns, String username, String password)
  {
    this.protocol = new Protocol(url);
    this.xmlns = xmlns;
    this.username = username;
    this.password = password;
    this.encoder = new BASE64Encoder();
    instance();
  }
  
  public void close() {}
  
  public void instance()
  {
    this.httpclient = new HttpClient();
  }
  
  public void reload(String url, String xmlns, String username, String password)
  {
    if ((!this.protocol.getUrl().equals(url)) || (!this.xmlns.equals(xmlns)) || (!this.username.equals(username)) || (!this.password.equals(password)))
    {
      this.protocol.setUrl(url);
      this.xmlns = xmlns;
      this.username = username;
      this.password = password;
      close();
      instance();
    }
  }
  
  private int sendMT(String sessionId, String serviceId, String sender, String receiver, String contentType, String content, String status)
  {
    synchronized (this.lock)
    {
      PostMethod post = new PostMethod(this.protocol.getUrl());
      String response = "";
      int error;
      try
      {
        String soapAction = this.xmlns + "receiverMO";
        String reqContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">  <soap:Header>    <AuthHeader xmlns=\"" + this.xmlns + "\">" + "      <Username>" + this.username + "</Username>" + "      <Password>" + this.password + "</Password>" + "    </AuthHeader>" + "  </soap:Header>" + "  <soap:Body>" + "    <sendMT xmlns=\"" + this.xmlns + "\">" + "       <SessionId>" + sessionId + "</SessionId>" + "       <ServiceId>" + serviceId + "</ServiceId>" + "       <Sender>" + sender + "</Sender>" + "       <Receiver>" + receiver + "</Receiver>" + "       <ContentType>" + contentType + "</ContentType>" + "       <Content>" + content + "</Content>" + "       <Status>" + status + "</Status>" + "    </sendMT>" + "  </soap:Body>" + "</soap:Envelope>";
        
        this.logger.debug("send soap message to " + this.protocol.getUrl());
        this.logger.debug("POST " + this.protocol.getServer() + " HTTP/1.1");
        this.logger.debug("Content-Type: text/xml; charset=utf-8");
        this.logger.debug("Connection: Keep-Alive");
        this.logger.debug("Content-Length: " + reqContent.length());
        this.logger.debug("SOAPAction: \"" + soapAction + "\"");
        this.logger.debug("");
        this.logger.debug(content);
        this.logger.debug("");
        
        RequestEntity entity = new StringRequestEntity(reqContent, "text/xml", "UTF-8");
        post.setRequestEntity(entity);
        
        post.setRequestHeader("SOAPAction", soapAction);
        this.httpclient.executeMethod(post);
        response = post.getResponseBodyAsString();
        this.logger.debug(response);
        
        int start = response.indexOf("<sendMTResult>") + "<sendMTResult>".length();
        int end = response.lastIndexOf("</sendMTResult>");
        error = Integer.parseInt(response.substring(start, end));
      }
      catch (Exception ex)
      {
        this.logger.error("soap message error " + ex.getMessage());
        this.logger.error("response content:" + response);
        this.httpclient = new HttpClient();
        error = 1;
      }
      finally
      {
        post.releaseConnection();
      }
      return error;
    }
  }
  
  public int send(String sessionId, String serviceId, String sender, String receiver, String contentType, String content, String status)
  {
    if (content == null) {
      content = "";
    }
    content = this.encoder.encode(content.getBytes());
    return sendMT(sessionId, serviceId, sender, receiver, contentType, content, status);
  }
  
  public int send(String sessionId, String serviceId, String sender, String receiver, String contentType, byte[] content, String status)
  {
    if (content == null) {
      content = new byte[0];
    }
    String soapContent = Hex.encode(content);
    return sendMT(sessionId, serviceId, sender, receiver, contentType, soapContent, status);
  }
}
