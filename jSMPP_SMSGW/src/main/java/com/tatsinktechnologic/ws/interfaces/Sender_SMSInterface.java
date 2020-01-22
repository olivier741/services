/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ws.interfaces;

import com.tatsinktechnologic.beans.SenderResponse;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import org.apache.cxf.annotations.EndpointProperties;
import org.apache.cxf.annotations.EndpointProperty;

/**
 *
 * @author olivier.tatsinkou
 */

@WebService(name="Send_SMSInterface",targetNamespace = "http://com.tatsinktechnologic.Sender_SMS",portName = "SEND_SMSPort")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL)
@EndpointProperties({
    @EndpointProperty(key = "action", value="UsernameToken"),
    @EndpointProperty(key = "passwordType", value="PasswordText"),
    @EndpointProperty(key = "ws-security.callback-handler", value="com.tatsinktechnologic.ws.handler.ServerPasswordCallback"),
    //@EndpointProperty(key = "ws-security.validate.token", value="false"),
})

public interface Sender_SMSInterface {
    @WebMethod(operationName="send_sms")
    public SenderResponse send_sms(@WebParam(name = "Sender") String sender,@WebParam(name = "Receiver") String receiver,@WebParam(name = "MsgContent") String content);    
}