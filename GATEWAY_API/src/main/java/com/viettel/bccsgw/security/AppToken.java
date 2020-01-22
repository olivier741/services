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
import com.viettel.bccsgw.utils.XmlUtil;
import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class AppToken
{
  private String appCode;
  private String appName;
  
  public String getAppCode()
  {
    return this.appCode;
  }
  
  public void setAppCode(String appCode)
  {
    this.appCode = appCode;
  }
  
  public String getAppName()
  {
    return this.appName;
  }
  
  public void setAppName(String appName)
  {
    this.appName = appName;
  }
  
  public static ArrayList<AppToken> parseApp(String entireResponse)
  {
    ArrayList<AppToken> re = new ArrayList();
    try
    {
      DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      

      Document doc = db.parse(new InputSource(new StringReader(entireResponse)));
      

      NodeList nl = doc.getElementsByTagName("AppAll");
      if ((nl != null) && (nl.getLength() > 0))
      {
        Element userEle = (Element)nl.item(0);
        NodeList nlApp = userEle.getElementsByTagName("Row");
        if ((nlApp != null) && (nlApp.getLength() > 0)) {
          for (int j = 0; j < nlApp.getLength(); j++)
          {
            Element el = (Element)nlApp.item(j);
            re.add(getAppToken(el));
          }
        }
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return re;
  }
  
  private static AppToken getAppToken(Element appEle)
  {
    String appCode = XmlUtil.getTextValue(appEle, "APP_CODE");
    String appName = XmlUtil.getTextValue(appEle, "APP_NAME");
    
    AppToken at = new AppToken();
    at.setAppCode(appCode);
    at.setAppName(appName);
    
    return at;
  }
}
