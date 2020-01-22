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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory
{
  private static final QName _Validate_QNAME = new QName("http://passport.viettel.com/", "validate");
  private static final QName _GetAppFunctions_QNAME = new QName("http://passport.viettel.com/", "getAppFunctions");
  private static final QName _GetAppFunctionsResponse_QNAME = new QName("http://passport.viettel.com/", "getAppFunctionsResponse");
  private static final QName _ValidateResponse_QNAME = new QName("http://passport.viettel.com/", "validateResponse");
  private static final QName _GetAllowedAppResponse_QNAME = new QName("http://passport.viettel.com/", "getAllowedAppResponse");
  private static final QName _GetAllowedApp_QNAME = new QName("http://passport.viettel.com/", "getAllowedApp");
  
  public Validate createValidate()
  {
    return new Validate();
  }
  
  public ValidateResponse createValidateResponse()
  {
    return new ValidateResponse();
  }
  
  public GetAppFunctions createGetAppFunctions()
  {
    return new GetAppFunctions();
  }
  
  public GetAppFunctionsResponse createGetAppFunctionsResponse()
  {
    return new GetAppFunctionsResponse();
  }
  
  public GetAllowedAppResponse createGetAllowedAppResponse()
  {
    return new GetAllowedAppResponse();
  }
  
  public GetAllowedApp createGetAllowedApp()
  {
    return new GetAllowedApp();
  }
  
  @XmlElementDecl(namespace="http://passport.viettel.com/", name="validate")
  public JAXBElement<Validate> createValidate(Validate value)
  {
    return new JAXBElement(_Validate_QNAME, Validate.class, null, value);
  }
  
  @XmlElementDecl(namespace="http://passport.viettel.com/", name="getAppFunctions")
  public JAXBElement<GetAppFunctions> createGetAppFunctions(GetAppFunctions value)
  {
    return new JAXBElement(_GetAppFunctions_QNAME, GetAppFunctions.class, null, value);
  }
  
  @XmlElementDecl(namespace="http://passport.viettel.com/", name="getAppFunctionsResponse")
  public JAXBElement<GetAppFunctionsResponse> createGetAppFunctionsResponse(GetAppFunctionsResponse value)
  {
    return new JAXBElement(_GetAppFunctionsResponse_QNAME, GetAppFunctionsResponse.class, null, value);
  }
  
  @XmlElementDecl(namespace="http://passport.viettel.com/", name="validateResponse")
  public JAXBElement<ValidateResponse> createValidateResponse(ValidateResponse value)
  {
    return new JAXBElement(_ValidateResponse_QNAME, ValidateResponse.class, null, value);
  }
  
  @XmlElementDecl(namespace="http://passport.viettel.com/", name="getAllowedAppResponse")
  public JAXBElement<GetAllowedAppResponse> createGetAllowedAppResponse(GetAllowedAppResponse value)
  {
    return new JAXBElement(_GetAllowedAppResponse_QNAME, GetAllowedAppResponse.class, null, value);
  }
  
  @XmlElementDecl(namespace="http://passport.viettel.com/", name="getAllowedApp")
  public JAXBElement<GetAllowedApp> createGetAllowedApp(GetAllowedApp value)
  {
    return new JAXBElement(_GetAllowedApp_QNAME, GetAllowedApp.class, null, value);
  }
}