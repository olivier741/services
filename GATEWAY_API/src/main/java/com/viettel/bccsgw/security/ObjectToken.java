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
import java.util.ArrayList;
import org.w3c.dom.Element;


public class ObjectToken
  implements Comparable<ObjectToken>
{
  private ArrayList<ObjectToken> childObjects = new ArrayList();
  private Long ord;
  private long parentId;
  private long status;
  private long objectId;
  private long objectType;
  private String description;
  private String objectName;
  private String objectUrl;
  public static final String MODULE_TYPE = "M";
  public static final String COMPONENT_TYPE = "C";
  
  public String getDescription()
  {
    return this.description;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public long getObjectId()
  {
    return this.objectId;
  }
  
  public void setObjectId(long objectId)
  {
    this.objectId = objectId;
  }
  
  public String getObjectName()
  {
    return this.objectName;
  }
  
  public void setObjectName(String objectName)
  {
    this.objectName = objectName;
  }
  
  public String getObjectType()
  {
    if (this.objectType == 0L) {
      return "M";
    }
    return "C";
  }
  
  public void setObjectType(long objectType)
  {
    this.objectType = objectType;
  }
  
  public String getObjectUrl()
  {
    return this.objectUrl;
  }
  
  public void setObjectUrl(String objectUrl)
  {
    this.objectUrl = objectUrl;
  }
  
  public Long getOrd()
  {
    return this.ord;
  }
  
  public void setOrd(Long ord)
  {
    this.ord = ord;
  }
  
  public long getParentId()
  {
    return this.parentId;
  }
  
  public void setParentId(long parentId)
  {
    this.parentId = parentId;
  }
  
  public long getStatus()
  {
    return this.status;
  }
  
  public void setStatus(long status)
  {
    this.status = status;
  }
  
  public ArrayList<ObjectToken> getChildObjects()
  {
    return this.childObjects;
  }
  
  public void setChildObjects(ArrayList<ObjectToken> childObjects)
  {
    this.childObjects = childObjects;
  }
  
  public int compareTo(ObjectToken o)
  {
    return this.ord.compareTo(o.ord);
  }
  
  public static ArrayList<ObjectToken> findFirstLevelMenus(ArrayList<ObjectToken> listObjects)
  {
    ArrayList<ObjectToken> list = new ArrayList();
    for (Object item : listObjects)
    {
      ObjectToken mt = (ObjectToken)item;
      if (mt.getParentId() <= 0L) {
        list.add(mt);
      }
    }
    return list;
  }
  
  public static ObjectToken getMenuToken(Element menuEle)
  {
    ObjectToken mt = new ObjectToken();
    
    mt.setObjectId(XmlUtil.getLongValue(menuEle, "OBJECT_ID"));
    mt.setParentId(XmlUtil.getLongValue(menuEle, "PARENT_ID"));
    mt.setStatus(XmlUtil.getLongValue(menuEle, "STATUS"));
    mt.setOrd(Long.valueOf(XmlUtil.getLongValue(menuEle, "ORD")));
    mt.setObjectUrl(XmlUtil.getTextValue(menuEle, "OBJECT_URL"));
    mt.setObjectName(XmlUtil.getTextValue(menuEle, "OBJECT_NAME"));
    mt.setDescription(XmlUtil.getTextValue(menuEle, "DESCRIPTION"));
    mt.setObjectType(XmlUtil.getLongValue(menuEle, "OBJECT_TYPE_ID"));
    return mt;
  }
}
