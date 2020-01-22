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
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class UserToken
{
  private String aliasName;
  private String birthPlace;
  private String cellphone;
  private Date dateOfBirth;
  private String description;
  private String email;
  private String fax;
  private String fullName;
  private long gender;
  private String identityCard;
  private Date issueDateIdent;
  private Date issueDatePassport;
  private String issuePlaceIdent;
  private String issuePlacePassport;
  private String passportNumber;
  private String staffCode;
  private long status;
  private String telephone;
  private String userName;
  private Long userId;
  private long userRight;
  private long timeToPasswordExpire;
  private Date lastChangePassword;
  private long passwordValidTime;
  
  public long getPasswordValidTime()
  {
    return this.passwordValidTime;
  }
  
  public void setPasswordValidTime(long passwordValidTime)
  {
    this.passwordValidTime = passwordValidTime;
  }
  
  public Date getLastChangePassword()
  {
    return this.lastChangePassword;
  }
  
  public void setLastChangePassword(Date lastChangePassword)
  {
    this.lastChangePassword = lastChangePassword;
  }
  
  public long getTimeToPasswordExpire()
  {
    return this.timeToPasswordExpire;
  }
  
  public void setTimeToPasswordExpire(long timeToPasswordExpire)
  {
    this.timeToPasswordExpire = timeToPasswordExpire;
  }
  
  public String getAliasName()
  {
    return this.aliasName;
  }
  
  public void setAliasName(String aliasName)
  {
    this.aliasName = aliasName;
  }
  
  public String getBirthPlace()
  {
    return this.birthPlace;
  }
  
  public void setBirthPlace(String birthPlace)
  {
    this.birthPlace = birthPlace;
  }
  
  public String getCellphone()
  {
    return this.cellphone;
  }
  
  public void setCellphone(String cellphone)
  {
    this.cellphone = cellphone;
  }
  
  public ArrayList<ObjectToken> getComponentList()
  {
    return this.componentList;
  }
  
  public void setComponentList(ArrayList<ObjectToken> componentList)
  {
    this.componentList = componentList;
  }
  
  public Date getDateOfBirth()
  {
    return this.dateOfBirth;
  }
  
  public void setDateOfBirth(Date dateOfBirth)
  {
    this.dateOfBirth = dateOfBirth;
  }
  
  public ArrayList<DeptToken> getDeptTokens()
  {
    return this.deptTokens;
  }
  
  public void setDeptTokens(ArrayList<DeptToken> deptTokens)
  {
    this.deptTokens = deptTokens;
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
  
  public String getFullName()
  {
    return this.fullName;
  }
  
  public void setFullName(String fullName)
  {
    this.fullName = fullName;
  }
  
  public long getGender()
  {
    return this.gender;
  }
  
  public void setGender(long gender)
  {
    this.gender = gender;
  }
  
  public String getIdentityCard()
  {
    return this.identityCard;
  }
  
  public void setIdentityCard(String identityCard)
  {
    this.identityCard = identityCard;
  }
  
  public Date getIssueDateIdent()
  {
    return this.issueDateIdent;
  }
  
  public void setIssueDateIdent(Date issueDateIdent)
  {
    this.issueDateIdent = issueDateIdent;
  }
  
  public Date getIssueDatePassport()
  {
    return this.issueDatePassport;
  }
  
  public void setIssueDatePassport(Date issueDatePassport)
  {
    this.issueDatePassport = issueDatePassport;
  }
  
  public String getIssuePlaceIdent()
  {
    return this.issuePlaceIdent;
  }
  
  public void setIssuePlaceIdent(String issuePlaceIdent)
  {
    this.issuePlaceIdent = issuePlaceIdent;
  }
  
  public String getIssuePlacePassport()
  {
    return this.issuePlacePassport;
  }
  
  public void setIssuePlacePassport(String issuePlacePassport)
  {
    this.issuePlacePassport = issuePlacePassport;
  }
  
  public ArrayList<ObjectToken> getObjectTokens()
  {
    return this.objectTokens;
  }
  
  public void setObjectTokens(ArrayList<ObjectToken> objectTokens)
  {
    this.objectTokens = objectTokens;
  }
  
  public ArrayList<ObjectToken> getParentMenu()
  {
    return this.parentMenu;
  }
  
  public void setParentMenu(ArrayList<ObjectToken> parentMenu)
  {
    this.parentMenu = parentMenu;
  }
  
  public String getPassportNumber()
  {
    return this.passportNumber;
  }
  
  public void setPassportNumber(String passportNumber)
  {
    this.passportNumber = passportNumber;
  }
  
  public String getStaffCode()
  {
    return this.staffCode;
  }
  
  public void setStaffCode(String staffCode)
  {
    this.staffCode = staffCode;
  }
  
  public long getStatus()
  {
    return this.status;
  }
  
  public void setStatus(long status)
  {
    this.status = status;
  }
  
  public String getTelephone()
  {
    return this.telephone;
  }
  
  public void setTelephone(String telephone)
  {
    this.telephone = telephone;
  }
  
  public Long getUserID()
  {
    return this.userId;
  }
  
  public void setUserID(Long userID)
  {
    this.userId = userID;
  }
  
  public String getUserName()
  {
    return this.userName;
  }
  
  public void setUserName(String userName)
  {
    this.userName = userName;
  }
  
  public long getUserRight()
  {
    return this.userRight;
  }
  
  public void setUserRight(long userRight)
  {
    this.userRight = userRight;
  }
  
  private ArrayList<DeptToken> deptTokens = new ArrayList();
  private ArrayList<ObjectToken> objectTokens = new ArrayList();
  private ArrayList<ObjectToken> parentMenu = new ArrayList();
  private ArrayList<ObjectToken> componentList = new ArrayList();
  private ArrayList<RoleToken> rolesList = new ArrayList();
  
  public ArrayList<RoleToken> getRolesList()
  {
    return this.rolesList;
  }
  
  public void setRolesList(ArrayList<RoleToken> rolesList)
  {
    this.rolesList = rolesList;
  }
  
  public static UserToken getUserToken(Element userEle)
  {
    UserToken ut = new UserToken();
    
    ut.setUserID(Long.valueOf(XmlUtil.getLongValue(userEle, "USER_ID")));
    ut.setUserRight(XmlUtil.getLongValue(userEle, "USER_RIGHT"));
    ut.setUserName(XmlUtil.getTextValue(userEle, "USER_NAME"));
    ut.setStatus(XmlUtil.getLongValue(userEle, "STATUS"));
    ut.setEmail(XmlUtil.getTextValue(userEle, "EMAIL"));
    ut.setCellphone(XmlUtil.getTextValue(userEle, "CELLPHONE"));
    ut.setTelephone(XmlUtil.getTextValue(userEle, "TELEPHONE"));
    ut.setFax(XmlUtil.getTextValue(userEle, "FAX"));
    ut.setGender(XmlUtil.getLongValue(userEle, "GENDER"));
    
    ut.setAliasName(XmlUtil.getTextValue(userEle, "ALIAS_NAME"));
    ut.setBirthPlace(XmlUtil.getTextValue(userEle, "BIRTH_PLACE"));
    ut.setIdentityCard(XmlUtil.getTextValue(userEle, "IDENTITY_CARD"));
    ut.setIssuePlaceIdent(XmlUtil.getTextValue(userEle, "ISSUE_PLACE_IDENT"));
    
    ut.setPassportNumber(XmlUtil.getTextValue(userEle, "PASSPORT_NUMBER"));
    ut.setIssuePlacePassport(XmlUtil.getTextValue(userEle, "ISSUE_PLACE_PASSPORT"));
    
    ut.setFullName(XmlUtil.getTextValue(userEle, "FULL_NAME"));
    ut.setDescription(XmlUtil.getTextValue(userEle, "DESCRIPTION"));
    ut.setStaffCode(XmlUtil.getTextValue(userEle, "STAFF_CODE"));
    
    ut.setPasswordValidTime(XmlUtil.getLongValue(userEle, "PASSWORD_VALID_TIME"));
    if (ut.getPasswordValidTime() > 0L) {
      ut.setTimeToPasswordExpire(XmlUtil.getLongValue(userEle, "TIME_TO_PASSWORD_EXPIRE"));
    } else {
      ut.setTimeToPasswordExpire(-1L);
    }
    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    ut.setLastChangePassword(null);
    try
    {
      ut.setLastChangePassword(df.parse(XmlUtil.getTextValue(userEle, "LAST_CHANGE_PASSWORD")));
    }
    catch (Exception e)
    {
      ut.setLastChangePassword(null);
    }
    return ut;
  }
  
  public static ArrayList<UserToken> parseUser(String entireResponse)
  {
    ArrayList<UserToken> re = new ArrayList();
    try
    {
      DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      

      Document doc = db.parse(new InputSource(new StringReader(entireResponse)));
      

      NodeList nl = doc.getElementsByTagName("UserData");
      if ((nl != null) && (nl.getLength() > 0))
      {
        Element userEle = (Element)nl.item(0);
        NodeList nlApp = userEle.getElementsByTagName("Row");
        if ((nlApp != null) && (nlApp.getLength() > 0)) {
          for (int j = 0; j < nlApp.getLength(); j++)
          {
            Element el = (Element)nlApp.item(j);
            re.add(getUserToken(el));
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
  
  public static UserToken parseXMLResponse(String response)
    throws ParserConfigurationException, SAXException, IOException
  {
    return parseXMLResponse(response, true);
  }
  
  public static UserToken parseXMLResponse(String response, boolean isWebInterface)
    throws ParserConfigurationException, SAXException, IOException
  {
    if (response.equalsIgnoreCase("no")) {
      return null;
    }
    UserToken userToken = new UserToken();
    
    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    

    Document doc = db.parse(new InputSource(new StringReader(response)));
    

    NodeList nl = doc.getElementsByTagName("UserData");
    if ((nl != null) && (nl.getLength() > 0))
    {
      Element userEle = (Element)nl.item(0);
      NodeList nlUsers = userEle.getElementsByTagName("Row");
      if ((nlUsers != null) && (nlUsers.getLength() > 0))
      {
        Element el = (Element)nlUsers.item(0);
        userToken = getUserToken(el);
      }
    }
    ArrayList<DeptToken> arrlDepts = new ArrayList();
    nl = doc.getElementsByTagName("Depts");
    if ((nl != null) && (nl.getLength() > 0))
    {
      Element groupEle = (Element)nl.item(0);
      NodeList nlDepts = groupEle.getElementsByTagName("Row");
      if ((nlDepts != null) && (nlDepts.getLength() > 0))
      {
        for (int i = 0; i < nlDepts.getLength(); i++)
        {
          Element el = (Element)nlDepts.item(i);
          DeptToken gt = DeptToken.getDeptToken(el);
          arrlDepts.add(gt);
        }
        userToken.setDeptTokens(arrlDepts);
      }
    }
    ArrayList<RoleToken> arrlRoles = new ArrayList();
    nl = doc.getElementsByTagName("Roles");
    if ((nl != null) && (nl.getLength() > 0))
    {
      Element roleEle = (Element)nl.item(0);
      NodeList nlRoles = roleEle.getElementsByTagName("Row");
      if ((nlRoles != null) && (nlRoles.getLength() > 0))
      {
        for (int i = 0; i < nlRoles.getLength(); i++)
        {
          Element el = (Element)nlRoles.item(i);
          RoleToken rt = RoleToken.getRoleToken(el);
          arrlRoles.add(rt);
        }
        userToken.setRolesList(arrlRoles);
      }
    }
    ArrayList<ObjectToken> arrlObjects = new ArrayList();
    nl = doc.getElementsByTagName("ObjectAll");
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
    userToken.setObjectTokens(arrlObjects);
    


    ArrayList<ObjectToken> arrAllComponent = new ArrayList();
    for (ObjectToken parentObj : arrlObjects)
    {
      if ((null == parentObj.getObjectUrl()) || ("".equals(parentObj.getObjectUrl()))) {
        parentObj.setObjectUrl("#");
      }
      if (parentObj.getObjectType().equals("C"))
      {
        arrAllComponent.add(parentObj);
      }
      else
      {
        List childList = new ArrayList();
        for (ObjectToken childObject : arrlObjects) {
          if (isWebInterface)
          {
            if ((childObject.getObjectType().equals("M")) && (childObject.getParentId() == parentObj.getObjectId())) {
              childList.add(childObject);
            }
          }
          else if (childObject.getParentId() == parentObj.getObjectId()) {
            childList.add(childObject);
          }
        }
        Collections.sort(childList);
        parentObj.setChildObjects((ArrayList)childList);
      }
    }
    ArrayList<ObjectToken> parentObject = ObjectToken.findFirstLevelMenus(arrlObjects);
    Collections.sort(parentObject);
    
    userToken.setParentMenu(parentObject);
    userToken.setComponentList(arrAllComponent);
    
    return userToken;
  }
}
