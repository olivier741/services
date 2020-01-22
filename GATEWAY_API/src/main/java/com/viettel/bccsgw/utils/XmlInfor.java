package com.viettel.bccsgw.utils;

import java.util.ArrayList;

public class XmlInfor
{
  protected String name;
  protected String value;
  protected XmlInfor parent;
  protected XmlAttr[] attrs;
  protected XmlInfor[] childs;
  
  public XmlInfor(String name)
  {
    this.name = name;
  }
  
  public String getValue()
  {
    return this.value;
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
  
  public void addAttr(XmlAttr attr)
  {
    if (attr == null) {
      return;
    }
    if (this.attrs == null)
    {
      this.attrs = new XmlAttr[] { attr };
      return;
    }
    int length = this.attrs.length;
    XmlAttr[] newAttrs = new XmlAttr[length + 1];
    System.arraycopy(this.attrs, 0, newAttrs, 0, length);
    newAttrs[length] = attr;
    this.attrs = newAttrs;
  }
  
  public void addChild(XmlInfor child)
  {
    if (child == null) {
      return;
    }
    child.parent = this;
    if (this.childs == null)
    {
      this.childs = new XmlInfor[] { child };
      return;
    }
    int length = this.childs.length;
    XmlInfor[] newInfors = new XmlInfor[length + 1];
    System.arraycopy(this.childs, 0, newInfors, 0, length);
    newInfors[length] = child;
    this.childs = newInfors;
  }
  
  public XmlInfor getChild(String name)
  {
    if ((this.childs == null) || (this.childs.length == 0)) {
      return null;
    }
    for (XmlInfor child : this.childs) {
      if (child.getName().equals(name)) {
        return child;
      }
    }
    return null;
  }
  
  public XmlInfor[] getChilds(String name)
  {
    if ((this.childs == null) || (this.childs.length == 0)) {
      return new XmlInfor[0];
    }
    ArrayList<XmlInfor> v = new ArrayList();
    for (XmlInfor child : this.childs) {
      if (child.getName().equals(name)) {
        v.add(child);
      }
    }
    XmlInfor[] childs = new XmlInfor[v.size()];
    v.toArray(childs);
    return childs;
  }
  
  public String getAttribute(String name)
  {
    if ((this.attrs == null) || (this.attrs.length == 0)) {
      return null;
    }
    for (XmlAttr attr : this.attrs) {
      if (attr.name.equals(name)) {
        return attr.value;
      }
    }
    return null;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public XmlInfor getParent()
  {
    return this.parent;
  }
  
  public XmlAttr[] getAttributes()
  {
    if (this.attrs == null) {
      return new XmlAttr[0];
    }
    XmlAttr[] attr = new XmlAttr[this.attrs.length];
    System.arraycopy(this.attrs, 0, attr, 0, this.attrs.length);
    return attr;
  }
  
  public XmlInfor[] getChilds()
  {
    if (this.childs == null) {
      return new XmlInfor[0];
    }
    XmlInfor[] child = new XmlInfor[this.childs.length];
    System.arraycopy(this.childs, 0, child, 0, this.childs.length);
    return child;
  }
}