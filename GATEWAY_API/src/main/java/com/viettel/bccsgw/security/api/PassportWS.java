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
package com.viettel.bccsgw.security.api;

/**
 *
 * @author olivier.tatsinkou
 */
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(name="passportWS", targetNamespace="http://passport.viettel.com/")
@XmlSeeAlso({ObjectFactory.class})
public abstract interface PassportWS
{
  @WebMethod
  @WebResult(targetNamespace="")
  @RequestWrapper(localName="validate", targetNamespace="http://passport.viettel.com/", className="com.viettel.passport.Validate")
  @ResponseWrapper(localName="validateResponse", targetNamespace="http://passport.viettel.com/", className="com.viettel.passport.ValidateResponse")
  public abstract String validate(@WebParam(name="userName", targetNamespace="") String paramString1, @WebParam(name="password", targetNamespace="") String paramString2, @WebParam(name="domainCode", targetNamespace="") String paramString3);
  
  @WebMethod
  @WebResult(targetNamespace="")
  @RequestWrapper(localName="getAllowedApp", targetNamespace="http://passport.viettel.com/", className="com.viettel.passport.GetAllowedApp")
  @ResponseWrapper(localName="getAllowedAppResponse", targetNamespace="http://passport.viettel.com/", className="com.viettel.passport.GetAllowedAppResponse")
  public abstract String getAllowedApp(@WebParam(name="userName", targetNamespace="") String paramString);
  
  @WebMethod
  @WebResult(targetNamespace="")
  @RequestWrapper(localName="getAppFunctions", targetNamespace="http://passport.viettel.com/", className="com.viettel.passport.GetAppFunctions")
  @ResponseWrapper(localName="getAppFunctionsResponse", targetNamespace="http://passport.viettel.com/", className="com.viettel.passport.GetAppFunctionsResponse")
  public abstract String getAppFunctions(@WebParam(name="domainCode", targetNamespace="") String paramString);
}