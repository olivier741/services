 package com.tatsinktechnologic.ussd_gateway.utils;
 
 import java.io.IOException;
 import java.util.Stack;
 import org.xml.sax.Attributes;
 import org.xml.sax.InputSource;
 import org.xml.sax.SAXException;
 import org.xml.sax.XMLReader;
 import org.xml.sax.helpers.DefaultHandler;
 import org.xml.sax.helpers.XMLReaderFactory;
 
 public class XmlParser
 {
   public static XmlInfor parse(String path)
     throws SAXException, IOException
   {
     Stack<XmlInfor> stack = new Stack();
     
 
     XmlHandler handler = new XmlHandler(stack);
     XMLReader reader = XMLReaderFactory.createXMLReader();
     reader.setContentHandler(handler);
     
     InputSource in = new InputSource(XmlParser.class.getResourceAsStream(path));
     reader.parse(in);
     XmlInfor rootContent = handler.lastestEle;
     
     return rootContent;
   }
   
   public static XmlInfor parsePlain(String path)
     throws SAXException, IOException
   {
     Stack<XmlInfor> stack = new Stack();
     
 
     XmlHandler handler = new XmlHandler(stack);
     XMLReader reader = XMLReaderFactory.createXMLReader();
     reader.setContentHandler(handler);
     
     reader.parse(path);
     XmlInfor rootContent = handler.lastestEle;
     
     return rootContent;
   }
   
   static class XmlHandler
     extends DefaultHandler
   {
     Stack<XmlInfor> stack;
     XmlInfor lastestEle;
     
     public XmlHandler(Stack<XmlInfor> stack)
     {
       this.stack = stack;
     }
     
     public void startElement(String uri, String localName, String qName, Attributes attributes)
       throws SAXException
     {
       XmlInfor parent = null;
       if (!this.stack.empty()) {
         parent = (XmlInfor)this.stack.pop();
       }
       XmlInfor item = new XmlInfor(qName);
       
       int length = attributes.getLength();
       for (int i = 0; i < length; i++)
       {
         String name = attributes.getQName(i);
         String value = attributes.getValue(name);
         XmlAttr xmlAttr = new XmlAttr(name, value);
         item.addAttr(xmlAttr);
       }
       if (parent != null)
       {
         parent.addChild(item);
         this.stack.push(parent);
       }
       this.stack.push(item);
     }
     
     public void characters(char[] ch, int start, int length)
       throws SAXException
     {
       String s = new String(ch, start, length).trim();
       if (!this.stack.empty())
       {
         XmlInfor item = (XmlInfor)this.stack.pop();
         item.setValue(s);
         this.stack.push(item);
       }
     }
     
     public void endElement(String uri, String localName, String qName)
       throws SAXException
     {
       this.lastestEle = ((XmlInfor)this.stack.pop());
     }
   }
 }



