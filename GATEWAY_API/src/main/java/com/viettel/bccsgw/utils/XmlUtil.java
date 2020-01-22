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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.DOMOutputter;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtil
{
  public static void changeElementTable(String[] arrHeader, Element mElement, ArrayList vtInput)
    throws Exception
  {
    changeElementTable(arrHeader, mElement, "row", vtInput);
  }
  
  public static void changeElementTable(String[] arrHeader, Element mElement, String elemntName, ArrayList vtInput)
    throws Exception
  {
    ArrayList vtRow = null;
    Element row = null;
    mElement.removeChildren(elemntName);
    if ((vtInput != null) && 
      (vtInput.size() > 0) && (arrHeader.length > 0)) {
      for (int i = 0; i < vtInput.size(); i++)
      {
        vtRow = (ArrayList)vtInput.get(i);
        if ((vtRow != null) && 
          (arrHeader.length + 1 <= vtRow.size()))
        {
          row = new Element(elemntName);
          for (int j = 1; j < vtRow.size(); j++) {
            row.setAttribute(arrHeader[i], vtRow.get(j).toString());
          }
          row.setText(vtRow.get(0).toString());
        }
        mElement.addContent(row);
      }
    }
  }
  
  public static ArrayList getArrayList(Element mElement)
    throws Exception
  {
    String strHeader = mElement.getChild("header").getTextTrim();
    String[] arrHeader = strHeader.split(",");
    return getArrayList(arrHeader, mElement, "row");
  }
  
  public static ArrayList getArrayList(Element mElement, String elementName)
    throws Exception
  {
    String strHeader = mElement.getChild("header").getTextTrim();
    String[] arrHeader = strHeader.split(",");
    return getArrayList(arrHeader, mElement, elementName);
  }
  
  public static ArrayList getArrayList(String[] arrHeader, Element mElement, String elementName)
    throws Exception
  {
    ArrayList vtTable = new ArrayList();
    try
    {
      List rows = mElement.getChildren(elementName);
      for (int i = 0; i < rows.size(); i++)
      {
        Element row = (Element)rows.get(i);
        ArrayList vtElement = new ArrayList();
        for (int j = 0; j < arrHeader.length; j++) {
          vtElement.add(row.getAttribute(arrHeader[j].trim()).getValue());
        }
        vtTable.add(vtElement);
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return vtTable;
  }
  
  public static ArrayList getThreadList(String[] arrAttr, Element mElement)
    throws Exception
  {
    ArrayList arrThread = new ArrayList();
    try
    {
      List rows = mElement.getChildren("thread");
      for (int i = 0; i < rows.size(); i++)
      {
        Element thread = (Element)rows.get(i);
        HashMap mapAttr = new HashMap();
        for (int k = 0; k < arrAttr.length; k++) {
          mapAttr.put(arrAttr[k], findElementByPath(thread, arrAttr[k]).getValue());
        }
        arrThread.add(mapAttr);
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return arrThread;
  }
  
  public static Element findElementByPath(Element root, String sPath)
  {
    Element eChild = null;
    String[] arrElement = sPath.split("/");
    if (arrElement.length == 0)
    {
      arrElement = new String[1];
      arrElement[0] = sPath;
    }
    if (arrElement.length > 0) {
      for (int i = 0; i < arrElement.length; i++)
      {
        eChild = root.getChild(arrElement[i]);
        root = eChild;
      }
    }
    return root;
  }
  
  public static Attribute findAtribute(Element root, String sElementPath, String atributeName)
  {
    root = findElementByPath(root, sElementPath);
    Attribute a = null;
    if (root != null) {
      a = root.getAttribute(atributeName);
    }
    return a;
  }
  
  public static boolean xmlCheck(String xmlContent)
  {
    SAXBuilder builder = new SAXBuilder();
    try
    {
      builder.build(new ByteArrayInputStream(xmlContent.getBytes("UTF-8")));
    }
    catch (JDOMException jex)
    {
      return false;
    }
    catch (IOException ioex)
    {
      return false;
    }
    return true;
  }
  
  public static Element findElement(Element current, String elementName)
  {
    if (current.getName().equals(elementName)) {
      return current;
    }
    List<Element> children = current.getChildren();
    if (children.isEmpty()) {
      return null;
    }
    Element elementTemp = null;
    for (Element child : children)
    {
      elementTemp = findElement(child, elementName);
      if (elementTemp != null) {
        break;
      }
    }
    return elementTemp;
  }
  
  public static org.w3c.dom.Document convertToDOM(org.jdom.Document jdomDoc)
    throws JDOMException
  {
    DOMOutputter outputter = new DOMOutputter();
    return outputter.output(jdomDoc);
  }
  
  public static String getTextValue(org.w3c.dom.Element ele, String tagName)
  {
    NodeList nodeList = ele.getElementsByTagName(tagName);
    if ((nodeList != null) && (nodeList.getLength() > 0)) {
      try
      {
        org.w3c.dom.Element elm = (org.w3c.dom.Element)nodeList.item(0);
        if (elm.getFirstChild() != null) {
          return elm.getFirstChild().getNodeValue();
        }
      }
      catch (DOMException e) {}
    }
    return "";
  }
  
  public static int getIntValue(org.w3c.dom.Element ele, String tagName)
  {
    if ((ele != null) && (tagName != null))
    {
      String tmp = getTextValue(ele, tagName);
      if (tmp != null) {
        try
        {
          return Integer.parseInt(tmp);
        }
        catch (NumberFormatException e) {}
      }
    }
    return -1;
  }
  
  public static long getLongValue(org.w3c.dom.Element ele, String tagName)
  {
    if ((ele != null) && (tagName != null))
    {
      String tmp = getTextValue(ele, tagName);
      if (tmp != null) {
        try
        {
          return Long.parseLong(tmp);
        }
        catch (NumberFormatException e) {}
      }
    }
    return -1L;
  }
}
