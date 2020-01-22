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
package com.viettel.bccsgw.utils;

/**
 *
 * @author olivier.tatsinkou
 */
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SMTPAlerter
{
  public static boolean sendMail(String smtps_host, int smtps_port, String smtps_username, String smtps_password, Vector receiver, String mailSubject, String mailContent, Hashtable<String, FileDataSource> filename, String smtpsCertFile)
  {
    try
    {
      Properties props = System.getProperties();
      props.put("mail.smtps.host", smtps_host);
      props.put("mail.smtps.port", Integer.valueOf(smtps_port));
      props.put("mail.smtps.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.transport.protocol.rfc822", "smtps");
      props.put("javax.net.ssl.trustStore", smtpsCertFile);
      



      Session mailSession = Session.getDefaultInstance(props);
      


      Multipart multipart = new MimeMultipart();
      MimeBodyPart messagePart = new MimeBodyPart();
      messagePart.setContent(mailContent, "text/html; charset=UTF-8");
      multipart.addBodyPart(messagePart);
      if (filename != null) {
        for (String name : filename.keySet())
        {
          MimeBodyPart attachmentPart = new MimeBodyPart();
          attachmentPart.setDataHandler(new DataHandler((DataSource)filename.get(name)));
          attachmentPart.setFileName(name);
          multipart.addBodyPart(attachmentPart);
        }
      }
      MimeMessage message = new MimeMessage(mailSession);
      
      message.setFrom(new InternetAddress(smtps_username, smtps_username, "UTF-8"));
      if (receiver != null) {
        for (int i = 0; i < receiver.size(); i++)
        {
          String email = (String)receiver.get(i);
          if (isEmail(email)) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
          } else {
            return false;
          }
        }
      }
      MailcapCommandMap mc = (MailcapCommandMap)CommandMap.getDefaultCommandMap();
      mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
      mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
      mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
      mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
      mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
      CommandMap.setDefaultCommandMap(mc);
      
      message.setSubject(mailSubject, "utf-8");
      message.setSentDate(new Date());
      message.setContent(multipart);
      

      Transport transport = mailSession.getTransport("smtps");
      transport.connect(smtps_host, smtps_port, smtps_username, smtps_password);
      transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
      transport.close();
      return true;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return false;
  }
  
  public static boolean isEmail(String str)
  {
    if ((str == null) || (str.equals(""))) {
      return false;
    }
    try
    {
      String emailReg = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Za-z0-9]{2,4}$";
      Pattern pattern = Pattern.compile(emailReg);
      Matcher matcher = pattern.matcher(str);
      if (matcher.matches()) {
        return true;
      }
      return false;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return false;
  }
}