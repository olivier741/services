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
package com.vtc.service;

/**
 *
 * @author olivier.tatsinkou
 */
import com.viettel.bccsgw.bo.Entry;
import com.viettel.bccsgw.bo.OutputCharging;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class MapCode
{
  private Logger logger = Logger.getLogger(MapCode.class);
  private HashMap<String, OutputCharging> map = new HashMap();
  
  public MapCode()
    throws Exception
  {
    String check = "";
    try
    {
      FileReader fr = new FileReader("../conf/map_old_code.xml");
      
      InputSource is = new InputSource(fr);
      DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = df.newDocumentBuilder();
      Document dc = db.parse(is);
      Element root = dc.getDocumentElement();
      
      NodeList list = root.getElementsByTagName("Message");
      if (list.getLength() < 1) {
        throw new Exception("Message");
      }
      for (int i = 0; i < list.getLength(); i++)
      {
        check = "" + i;
        Element ele = (Element)list.item(i);
        OutputCharging output = new OutputCharging();
        





        check = check + "-Code";
        NodeList code = ele.getElementsByTagName("Code");
        Element codeElement = (Element)code.item(0);
        output.setErrorCode(codeElement.getTextContent());
        
        check = check + "-Value";
        NodeList value = ele.getElementsByTagName("Value");
        Element valueElement = (Element)value.item(0);
        output.setMessage(valueElement.getTextContent());
        
        check = check + "-Return";
        NodeList old = ele.getElementsByTagName("Return");
        Element oldElement = (Element)old.item(0);
        this.map.put(oldElement.getTextContent(), output);
      }
    }
    catch (Exception ex)
    {
      this.logger.error("Error load map_old_code.xml: " + check, ex);
      throw ex;
    }
  }
  
  public HashMap<String, OutputCharging> getMap()
  {
    return this.map;
  }
  
  public void setMap(HashMap<String, OutputCharging> map)
  {
    this.map = map;
  }
  
  public OutputCharging getOutput(String oldCode, String requestId)
  {
    OutputCharging out = (OutputCharging)this.map.get(oldCode);
    if (out == null)
    {
      out = new OutputCharging();
      out.setMessage("Not map code: " + oldCode);
      out.setErrorCode(oldCode);
    }
    out.setRequestId(requestId);
    ArrayList<Entry> listEntry = new ArrayList();
    listEntry.add(new Entry());
    out.setData(listEntry);
    return out;
  }
  
  public static void main(String[] a)
    throws Exception
  {
    PropertyConfigurator.configure("../conf/log4j.cfg");
    MapCode map = new MapCode();
    
    HashMap<String, OutputCharging> m = map.getMap();
    for (String string : m.keySet()) {
      System.out.println(string + "=" + m.get(string));
    }
  }
}
