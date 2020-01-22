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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XmlConfig
{
  private Document document;
  private String xmlFileName = "";
  private Element root;
  
  public Document getDocument()
  {
    return this.document;
  }
  
  public void setRoot(Element root)
  {
    this.root = root;
  }
  
  public XmlConfig() {}
  
  public XmlConfig(String mConfFileName)
  {
    this.xmlFileName = mConfFileName;
  }
  
  public void load()
    throws Exception
  {
    SAXBuilder builder = new SAXBuilder();
    try
    {
      InputStream is = new FileInputStream(new File(this.xmlFileName));
      this.document = builder.build(is);
      this.root = this.document.getRootElement();
      is.close();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public void load(String xmlContent)
  {
    SAXBuilder builder = new SAXBuilder();
    try
    {
      InputStream is = new ByteArrayInputStream(xmlContent.getBytes("UTF-8"));
      this.document = builder.build(is);
      this.root = this.document.getRootElement();
      is.close();
    }
    catch (Exception ex)
    {
      System.out.println("Load xml error:" + ex.getMessage());
    }
  }
  
  public void writeToFile()
    throws IOException
  {
    FileWriter writer = null;
    try
    {
      XMLOutputter outputter = new XMLOutputter();
      writer = new FileWriter(this.xmlFileName);
      
      outputter.setFormat(Format.getPrettyFormat());
      outputter.output(this.document, writer);
    }
    catch (IOException e) {}finally
    {
      writer.flush();
      writer.close();
    }
  }
  
  public Element findElement(String sPath)
  {
    return XmlUtil.findElement(this.root, sPath);
  }
  
  public String getTextValue(String elementName)
  {
    Element element = XmlUtil.findElement(this.root, elementName);
    if (element != null) {
      return element.getTextTrim();
    }
    return null;
  }
  
  public void setTextValue(String sPath, String sValue)
  {
    Element element = null;
    
    element = XmlUtil.findElement(this.root, sPath);
    if (element != null) {
      element.setText(sValue);
    }
  }
  
  public void setAttributeValue(String sPath, String attrName, String sValue)
  {
    Attribute attribute = null;
    attribute = XmlUtil.findAtribute(this.root, sPath, attrName);
    if (attribute != null) {
      attribute.setValue(sValue);
    }
  }
  
  public Attribute getAttribute(String sPath, String attrName)
  {
    return XmlUtil.findAtribute(this.root, sPath, attrName);
  }
  
  public Element getRoot()
  {
    return this.root;
  }
  
  public List<Element> findListElement(String elementName)
  {
    List<Element> resultList = new ArrayList();
    findElement(resultList, this.root, elementName);
    return resultList;
  }
  
  public void findElement(List<Element> resultList, Element current, String elementName)
  {
    if (current.getName().equals(elementName))
    {
      resultList.add(current);
      return;
    }
    List<Element> children = current.getChildren();
    for (Element child : children) {
      findElement(resultList, child, elementName);
    }
  }
}

