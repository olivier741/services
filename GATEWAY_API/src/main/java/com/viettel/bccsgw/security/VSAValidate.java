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
package com.viettel.bccsgw.security;

/**
 *
 * @author olivier.tatsinkou
 */

import com.viettel.bccsgw.security.api.PassportWSService;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class VSAValidate
{
  private static ResourceBundle resourceBundler = ResourceBundle.getBundle("cas");
  private String casValidateUrl;
  private String user;
  private String password;
  private String domainCode;
  private UserToken userToken;
  private boolean successfulAuthentication;
  private static final int DEFAULT_TIME_OUT_VALUE = 5000;
  private int timeOutVal = 5000;
  
  public int getTimeOutVal()
  {
    return this.timeOutVal;
  }
  
  public void setTimeOutVal(int timeOutVal)
  {
    this.timeOutVal = timeOutVal;
  }
  
  public boolean isAuthenticationSuccesful()
  {
    return this.successfulAuthentication;
  }
  
  public UserToken getUserToken()
  {
    return this.userToken;
  }
  
  public String getCasValidateUrl()
  {
    return this.casValidateUrl;
  }
  
  public void setCasValidateUrl(String casValidateUrl)
  {
    this.casValidateUrl = casValidateUrl;
  }
  
  public String getDomainCode()
  {
    return this.domainCode;
  }
  
  public void setDomainCode(String domainCode)
  {
    this.domainCode = domainCode;
  }
  
  public String getPassword()
  {
    return this.password;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
  }
  
  public String getUser()
  {
    return this.user;
  }
  
  public void setUser(String user)
  {
    this.user = user;
  }
  
  public VSAValidate()
  {
    setCasValidateUrl(resourceBundler.getString("passportServiceUrl"));
    setDomainCode(resourceBundler.getString("domainCode"));
  }
  
  public void validate()
    throws IOException, ParserConfigurationException
  {
    URL url = null;
    try
    {
      URL baseUrl = PassportWSService.class.getResource(".");
      url = new URL(baseUrl, getCasValidateUrl());
    }
    catch (MalformedURLException e)
    {
      e.printStackTrace();
    }
    PassportWSService pws = new PassportWSService(url, new QName("http://passport.viettel.com/", "passportWSService"));
    

    String entireResponse = pws.getPassportWSPort().validate(this.user, this.password, this.domainCode);
    try
    {
      if ("no".equalsIgnoreCase(entireResponse))
      {
        this.successfulAuthentication = false;
        this.userToken = null;
        return;
      }
      this.userToken = UserToken.parseXMLResponse(entireResponse, false);
      if ((this.userToken != null) && (this.userToken.getObjectTokens() != null) && (this.userToken.getObjectTokens().size() > 0))
      {
        System.out.println("authenticate successful [username=" + this.user + "]");
        this.successfulAuthentication = true;
      }
      else
      {
        System.out.println("authenticate failure [username=" + this.user + "]");
        this.successfulAuthentication = false;
      }
    }
    catch (SAXException ex)
    {
      ex.printStackTrace();
    }
  }
  
  public ArrayList<AppToken> getAllApp()
    throws IOException, ParserConfigurationException
  {
    URL url = null;
    try
    {
      URL baseUrl = PassportWSService.class.getResource(".");
      url = new URL(baseUrl, getCasValidateUrl());
    }
    catch (MalformedURLException e)
    {
      e.printStackTrace();
    }
    PassportWSService pws = new PassportWSService(url, new QName("http://passport.viettel.com/", "passportWSService"));
    

    String entireResponse = pws.getPassportWSPort().getAllowedApp(this.user);
    
    return AppToken.parseApp(entireResponse);
  }
  
  public ArrayList<ObjectToken> getAllMenu()
    throws IOException, ParserConfigurationException, SAXException
  {
    this.domainCode = this.domainCode.trim().toLowerCase();
    URL url = null;
    try
    {
      URL baseUrl = PassportWSService.class.getResource(".");
      url = new URL(baseUrl, getCasValidateUrl());
    }
    catch (MalformedURLException e)
    {
      e.printStackTrace();
    }
    PassportWSService pws = new PassportWSService(url, new QName("http://passport.viettel.com/", "passportWSService"));
    

    String entireResponse = pws.getPassportWSPort().getAppFunctions(this.domainCode);
    


    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    

    Document doc = db.parse(new InputSource(new StringReader(entireResponse)));
    

    ArrayList<ObjectToken> arrlObjects = new ArrayList();
    NodeList nl = doc.getElementsByTagName("ObjectAll");
    if ((nl != null) && (nl.getLength() > 0))
    {
      Element objectEle = (Element)nl.item(0);
      NodeList nlObjects = objectEle.getElementsByTagName("Row");
      if ((nlObjects != null) && (nlObjects.getLength() > 0)) {
        for (int i = 0; i < nlObjects.getLength(); i++)
        {
          Element el = (Element)nlObjects.item(i);
          ObjectToken mt = ObjectToken.getMenuToken(el);
          arrlObjects.add(mt);
        }
      }
    }
    return arrlObjects;
  }
}
