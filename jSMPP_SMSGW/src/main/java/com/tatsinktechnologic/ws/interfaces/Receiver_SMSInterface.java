/*
 * Copyright 2018 olivier.tatsinkou.
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
package com.tatsinktechnologic.ws.interfaces;

import com.tatsinktechnologic.beans.DeliveryMessage;
import com.tatsinktechnologic.beans.ReceiverResponse;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import org.apache.cxf.annotations.EndpointProperties;
import org.apache.cxf.annotations.EndpointProperty;

/**
 *
 * @author olivier.tatsinkou
 */
@WebService(name="Receiver_SMSInterface",targetNamespace = "http://com.tatsinktechnologic.Receiver_SMS",portName = "RECEIVE_SMSPort")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL)
@EndpointProperties({
    @EndpointProperty(key = "action", value="UsernameToken"),
    @EndpointProperty(key = "passwordType", value="PasswordText"),
    @EndpointProperty(key = "ws-security.callback-handler", value="com.tatsinktechnologic.ws.handler.ClientPasswordCallback"),
    //@EndpointProperty(key = "ws-security.validate.token", value="false"),
})

public interface Receiver_SMSInterface {
    @WebMethod(operationName="receive_sms")
    public ReceiverResponse receive_sms(@WebParam(name = "Sender") String sender,@WebParam(name = "Receiver") String receiver,@WebParam(name = "MsgContent") String content); 
    
    @WebMethod(operationName="receive_delivery")
    public ReceiverResponse receive_delivery(@WebParam(name = "delivery") DeliveryMessage delivery_msg); 
}


