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

public class RoleToken
{
  private String roleCode;
  private String roleName;
  private String description;
  
  public String getDescription()
  {
    return this.description;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public String getRoleCode()
  {
    return this.roleCode;
  }
  
  public void setRoleCode(String roleCode)
  {
    this.roleCode = roleCode;
  }
  
  public String getRoleName()
  {
    return this.roleName;
  }
  
  public void setRoleName(String roleName)
  {
    this.roleName = roleName;
  }
  
  public static RoleToken getRoleToken(Element appEle)
  {
    String roleCode = XmlUtil.getTextValue(appEle, "ROLE_CODE");
    String roleName = XmlUtil.getTextValue(appEle, "ROLE_NAME");
    String description = XmlUtil.getTextValue(appEle, "DESCRIPTION");
    
    RoleToken rt = new RoleToken();
    rt.setRoleCode(roleCode);
    rt.setRoleName(roleName);
    rt.setDescription(description);
    
    return rt;
  }
}
