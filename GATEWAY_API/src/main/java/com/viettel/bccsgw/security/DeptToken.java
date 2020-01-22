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
import org.w3c.dom.Element;

public class DeptToken
{
  private Long status;
  private String tel;
  private String tin;
  private String address;
  private String code;
  private String contactName;
  private String contactTitle;
  private String deptName;
  private String description;
  private String email;
  private String fax;
  private String positionCode;
  private String positionName;
  private String locationCode;
  private String locationName;
  private String deptTypeName;
  
  public String getAddress()
  {
    return this.address;
  }
  
  public void setAddress(String address)
  {
    this.address = address;
  }
  
  public String getCode()
  {
    return this.code;
  }
  
  public void setCode(String code)
  {
    this.code = code;
  }
  
  public String getContactName()
  {
    return this.contactName;
  }
  
  public void setContactName(String contactName)
  {
    this.contactName = contactName;
  }
  
  public String getContactTitle()
  {
    return this.contactTitle;
  }
  
  public void setContactTitle(String contactTitle)
  {
    this.contactTitle = contactTitle;
  }
  
  public String getName()
  {
    return this.deptName;
  }
  
  public void setName(String deptName)
  {
    this.deptName = deptName;
  }
  
  public String getDeptTypeName()
  {
    return this.deptTypeName;
  }
  
  public void setDeptTypeName(String deptTypeName)
  {
    this.deptTypeName = deptTypeName;
  }
  
  public String getDescription()
  {
    return this.description;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public String getEmail()
  {
    return this.email;
  }
  
  public void setEmail(String email)
  {
    this.email = email;
  }
  
  public String getFax()
  {
    return this.fax;
  }
  
  public void setFax(String fax)
  {
    this.fax = fax;
  }
  
  public String getLocationCode()
  {
    return this.locationCode;
  }
  
  public void setLocationCode(String locationCode)
  {
    this.locationCode = locationCode;
  }
  
  public String getLocationName()
  {
    return this.locationName;
  }
  
  public void setLocationName(String locationName)
  {
    this.locationName = locationName;
  }
  
  public String getPositionCode()
  {
    return this.positionCode;
  }
  
  public void setPositionCode(String positionCode)
  {
    this.positionCode = positionCode;
  }
  
  public String getPositionName()
  {
    return this.positionName;
  }
  
  public void setPositionName(String positionName)
  {
    this.positionName = positionName;
  }
  
  public Long getStatus()
  {
    return this.status;
  }
  
  public void setStatus(Long status)
  {
    this.status = status;
  }
  
  public String getPhone()
  {
    return this.tel;
  }
  
  public void setPhone(String tel)
  {
    this.tel = tel;
  }
  
  public String getTin()
  {
    return this.tin;
  }
  
  public void setTin(String tin)
  {
    this.tin = tin;
  }
  
  public static DeptToken getDeptToken(Element groupEle)
  {
    DeptToken dt = new DeptToken();
    dt.setStatus(Long.valueOf(XmlUtil.getLongValue(groupEle, "STATUS")));
    dt.setName(XmlUtil.getTextValue(groupEle, "DEPT_NAME"));
    dt.setAddress(XmlUtil.getTextValue(groupEle, "ADDRESS"));
    dt.setDescription(XmlUtil.getTextValue(groupEle, "DESCRIPTION"));
    dt.setCode(XmlUtil.getTextValue(groupEle, "DEPT_CODE"));
    dt.setTin(XmlUtil.getTextValue(groupEle, "TIN"));
    dt.setEmail(XmlUtil.getTextValue(groupEle, "EMAIL"));
    dt.setContactName(XmlUtil.getTextValue(groupEle, "CONTACT_NAME"));
    dt.setContactTitle(XmlUtil.getTextValue(groupEle, "CONTACT_TITLE"));
    dt.setFax(XmlUtil.getTextValue(groupEle, "FAX"));
    dt.setPhone(XmlUtil.getTextValue(groupEle, "TEL"));
    dt.setLocationCode("");
    dt.setLocationName("");
    dt.setPositionCode("");
    dt.setPositionName("");
    dt.setDeptTypeName("");
    return dt;
  }
}

