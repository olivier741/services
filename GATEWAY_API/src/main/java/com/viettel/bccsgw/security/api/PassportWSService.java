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
import com.viettel.bccsgw.security.api.PassportWS;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name="passportWSService", targetNamespace="http://passport.viettel.com/", wsdlLocation="http://192.168.176.190:5676/passportv3/passportWS?wsdl")
public class PassportWSService
  extends Service
{
  private static final URL PASSPORTWSSERVICE_WSDL_LOCATION;
  private static final WebServiceException PASSPORTWSSERVICE_EXCEPTION;
  private static final QName PASSPORTWSSERVICE_QNAME = new QName("http://passport.viettel.com/", "passportWSService");
  
  static
  {
    URL url = null;
    WebServiceException e = null;
    try
    {
      url = new URL("http://192.168.176.190:5676/passportv3/passportWS?wsdl");
    }
    catch (MalformedURLException ex)
    {
      e = new WebServiceException(ex);
    }
    PASSPORTWSSERVICE_WSDL_LOCATION = url;
    PASSPORTWSSERVICE_EXCEPTION = e;
  }
  
  public PassportWSService()
  {
    super(__getWsdlLocation(), PASSPORTWSSERVICE_QNAME);
  }
  
  public PassportWSService(WebServiceFeature... features)
  {
    super(__getWsdlLocation(), PASSPORTWSSERVICE_QNAME, features);
  }
  
  public PassportWSService(URL wsdlLocation)
  {
    super(wsdlLocation, PASSPORTWSSERVICE_QNAME);
  }
  
  public PassportWSService(URL wsdlLocation, WebServiceFeature... features)
  {
    super(wsdlLocation, PASSPORTWSSERVICE_QNAME, features);
  }
  
  public PassportWSService(URL wsdlLocation, QName serviceName)
  {
    super(wsdlLocation, serviceName);
  }
  
  public PassportWSService(URL wsdlLocation, QName serviceName, WebServiceFeature... features)
  {
    super(wsdlLocation, serviceName, features);
  }
  
  @WebEndpoint(name="passportWSPort")
  public PassportWS getPassportWSPort()
  {
    return (PassportWS)super.getPort(new QName("http://passport.viettel.com/", "passportWSPort"), PassportWS.class);
  }
  
  @WebEndpoint(name="passportWSPort")
  public PassportWS getPassportWSPort(WebServiceFeature... features)
  {
    return (PassportWS)super.getPort(new QName("http://passport.viettel.com/", "passportWSPort"), PassportWS.class, features);
  }
  
  private static URL __getWsdlLocation()
  {
    if (PASSPORTWSSERVICE_EXCEPTION != null) {
      throw PASSPORTWSSERVICE_EXCEPTION;
    }
    return PASSPORTWSSERVICE_WSDL_LOCATION;
  }
}
