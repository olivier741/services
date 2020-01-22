/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.w3c.dom.NodeList;


/**
 *
 * @author olivier.tatsinkou
 */
public class SoapProxy {
    
    public static void main(String[] args)  {

    String endPoint = "http://10.124.160.118:8935/send_sms?wsdl";
    String endPoint1 = "http://127.0.0.1:8935/send?wsdl";
    String endPoint2 = "http://10.124.249.16:8022/BCCSGateway?wsdl";
    String endPoint3 = "http://127.0.0.1:8935/sender_sms?wsdl";
    
    
    String chargeString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.bccsgw.viettel.com/\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <web:gwOperation>\n" +
                    "         <Input>\n" +
                    "            <username>cf78e938013cb8c475b8956adffa51854a3f484e25</username>\n" +
                    "            <password>cf78e938013cb8c475b8956adffa5185ed503060278bb2d1071bce3a1</password>\n" +
                    "            <wscode>AddBalance</wscode>\n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    "           <param name=\"client\" value=\"PROFESSIONAL_NUMBER\"/>\n" +
                    "           <param name=\"requestId\" value=\"1\"/>  \n" +
                    "           <param name=\"input\" value=\"237662442204|PROFESSIONAL_NUMBER|Test Add balance|10|10|26|Test add description\"/>  \n" +
                    "            <!--Optional:-->\n" +
                    "            <rawData>?</rawData>\n" +
                    "         </Input>\n" +
                    "      </web:gwOperation>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";

    String soapString = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:com=\"http://com.nexttel.WS_Send_SMS\">\n" +
                        "   <soapenv:Header>\n" +
                        "      <com:password>admin</com:password>\n" +
                        "      <com:user>admin</com:user>\n" +
                        "   </soapenv:Header>\n" +
                        "   <soapenv:Body>\n" +
                        "      <com:send_sms>\n" +
                        "         <!--Optional:-->\n" +
                        "         <ListMsisdn>237661000504</ListMsisdn>\n" +
                        "         <!--Optional:-->\n" +
                        "         <message>test on phone</message>\n" +
                        "         <!--Optional:-->\n" +
                        "         <Channel>TEST456</Channel>\n" +
                        "      </com:send_sms>\n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>";
    
    String soapString1= "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        "   <soap:Header>\n" +
                        "      <wsse:Security soap:mustUnderstand=\"1\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">\n" +
                        "         <wsse:UsernameToken wsu:Id=\"UsernameToken-f7920544-e96b-4d6b-8a57-2ebd2853bf59\">\n" +
                        "            <wsse:Username>?</wsse:Username>\n" +
                        "            <wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">?</wsse:Password>\n" +
                        "         </wsse:UsernameToken>\n" +
                        "      </wsse:Security>\n" +
                        "   </soap:Header>\n" +
                        "   <soap:Body>\n" +
                        "      <ns2:send_sms xmlns:ns2=\"http://cm.nexttel.noc.Sender_SMS\">\n" +
                        "         <Sender>NOC_SMS</Sender>\n" +
                        "         <Receiver>237661000504</Receiver>\n" +
                        "         <MsgContent>Dear All</MsgContent>\n" +
                        "      </ns2:send_sms>\n" +
                        "   </soap:Body>\n" +
                        "</soap:Envelope>";
    
    String soapString2 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:com=\"http://com.nexttel.WS_Send_SMS\">\n" +
                        "   <soapenv:Header>\n" +
                        "      <com:password>?</com:password>\n" +
                        "      <com:user>?</com:user>\n" +
                        "   </soapenv:Header>\n" +
                        "   <soapenv:Body>\n" +
                        "      <com:send_sms>\n" +
                        "         <!--Optional:-->\n" +
                        "         <ListMsisdn>?</ListMsisdn>\n" +
                        "         <!--Optional:-->\n" +
                        "         <message>?</message>\n" +
                        "         <!--Optional:-->\n" +
                        "         <Channel>?</Channel>\n" +
                        "      </com:send_sms>\n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>";
    
     String soapString3 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:com=\"http://com.nexttel.WS_Send_SMS\">\n" +
                        "   <soapenv:Body>\n" +
                        "      <com:send_sms>\n" +
                        "         <!--Optional:-->\n" +
                        "         <ListMsisdn>?</ListMsisdn>\n" +
                        "         <!--Optional:-->\n" +
                        "         <message>?</message>\n" +
                        "         <!--Optional:-->\n" +
                        "         <Channel>?</Channel>\n" +
                        "      </com:send_sms>\n" +
                        "   </soapenv:Body>\n" +
                        "</soapenv:Envelope>";

        try {
            System.out.println(consumSoap_1_1( soapString1,endPoint3));
        } catch (SOAPException e) {
            try {
                System.out.println(consumSoap_1_2( soapString1, endPoint1 ));
            } catch (Exception e1) {
                e.printStackTrace();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    
    
    
  }

    
  private static String consumSoap_1_1( String soapString,String endPoint ) throws UnsupportedOperationException, SOAPException, IOException{
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        
        InputStream is = new ByteArrayInputStream(soapString.getBytes());

        SOAPMessage request = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage(null, is);

        MimeHeaders headers = request.getMimeHeaders();

        headers.setHeader("Content-Type", "application/xml");

        // get the body
        SOAPBody soapBody = request.getSOAPBody();

        // find your node based on tag name
        NodeList nodes = soapBody.getElementsByTagName("Receiver");

        if(nodes.item(0) != null) nodes.item(0).setTextContent("661000504");

        // check if the node exists and get the value

        String someMsgContent = null;
        Node node = nodes.item(0);
        someMsgContent = node != null ? node.getTextContent() : "";
        
        request.saveChanges();

        SOAPMessage soapResponse = soapConnection.call(request, endPoint);

        StringWriter sw = new StringWriter();

        try {
            TransformerFactory.newInstance()
                              .newTransformer()
                              .transform(new DOMSource(soapResponse.getSOAPPart()), new StreamResult(sw));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }

        // Now you have the XML as a String:
        return sw.toString();
    }
  
  
  
  private static String consumSoap_1_1_BasiAuth( String soapString,String endPoint,String username, String password ) throws UnsupportedOperationException, SOAPException, IOException{
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        
        InputStream is = new ByteArrayInputStream(soapString.getBytes());

        SOAPMessage request = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage(null, is);

        String authorization = new sun.misc.BASE64Encoder().encode((username+":"+password).getBytes());
        
        MimeHeaders headers = request.getMimeHeaders();

        headers.setHeader("Content-Type", "application/xml");
        headers.addHeader("Authorization", "Basic " + authorization);
        
        // get the body
        SOAPBody soapBody = request.getSOAPBody();

        // find your node based on tag name
        NodeList nodes = soapBody.getElementsByTagName("Receiver");

        if(nodes.item(0) != null) nodes.item(0).setTextContent("661000504");

        // check if the node exists and get the value

        String someMsgContent = null;
        Node node = nodes.item(0);
        someMsgContent = node != null ? node.getTextContent() : "";
        
        request.saveChanges();

        SOAPMessage soapResponse = soapConnection.call(request, endPoint);

        StringWriter sw = new StringWriter();

        try {
            TransformerFactory.newInstance()
                              .newTransformer()
                              .transform(new DOMSource(soapResponse.getSOAPPart()), new StreamResult(sw));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }

        // Now you have the XML as a String:
        return sw.toString();
    }
  
  
  
  
  private static String consumSoap_1_1_HeaderAuth( String soapString,String endPoint,String user_name,String pwd) throws UnsupportedOperationException, SOAPException, IOException{
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        
        InputStream is = new ByteArrayInputStream(soapString.getBytes());

        SOAPMessage request = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage(null, is);

        
        SOAPPart soapPart = request.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();

        SOAPBody soapBody = soapEnvelope.getBody();
        SOAPElement Header = soapBody.addBodyElement(new QName("Header"));
        
        SOAPElement Security= Header.addChildElement(new QName("Security"));
        SOAPElement UsernameToken= Security.addChildElement(new QName("UsernameToken"));
        SOAPElement Username= UsernameToken.addChildElement(new QName("Username"));
        SOAPElement Password= UsernameToken.addChildElement(new QName("Password"));

        
        //enter the username and password
        Username.addTextNode(user_name);
        Password.addTextNode(pwd);
        
        MimeHeaders headers = request.getMimeHeaders();

        headers.setHeader("Content-Type", "application/xml");

        // find your node based on tag name
        NodeList node_Username = soapBody.getElementsByTagName("Username");
        NodeList node_Password = soapBody.getElementsByTagName("Password");

        if(node_Username.item(0) != null) node_Username.item(0).setTextContent(user_name);
        if(node_Password.item(0) != null) node_Password.item(0).setTextContent(pwd);

       
        
        request.saveChanges();

        SOAPMessage soapResponse = soapConnection.call(request, endPoint);

        StringWriter sw = new StringWriter();

        try {
            TransformerFactory.newInstance()
                              .newTransformer()
                              .transform(new DOMSource(soapResponse.getSOAPPart()), new StreamResult(sw));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }

        // Now you have the XML as a String:
        return sw.toString();
    }
  
  
  
  
  
  private static String consumSoap_1_1_HeaderAuth_charge( String soapString,String endPoint,String user_name,String pwd) throws UnsupportedOperationException, SOAPException, IOException{
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        
        InputStream is = new ByteArrayInputStream(soapString.getBytes());

        System.out.println(soapString);
        SOAPMessage request = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage(null, is);

        
        SOAPPart soapPart = request.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();

        SOAPBody soapBody = soapEnvelope.getBody();
        SOAPElement Header = soapBody.addBodyElement(new QName("Header"));
        
//        SOAPElement Security= Header.addChildElement(new QName("Security"));
//        SOAPElement UsernameToken= Security.addChildElement(new QName("UsernameToken"));
//        SOAPElement Username= UsernameToken.addChildElement(new QName("Username"));
//        SOAPElement Password= UsernameToken.addChildElement(new QName("Password"));

        
        //enter the username and password
//        Username.addTextNode(user_name);
//        Password.addTextNode(pwd);
        
        MimeHeaders headers = request.getMimeHeaders();

        headers.setHeader("Content-Type", "application/xml");

        // find your node based on tag name
        NodeList node_param= soapBody.getElementsByTagName("param");
//        NodeList node_Password = soapBody.getElementsByTagName("Password");

//        if(node_param.item(0) != null){
//            node_param.item(0).setTextContent(user_name);
//            System.out.println(node_param.item(0).getTextContent());
//        }
        
        for (int i = 0; i < node_param.getLength(); i++) {
            Node child = node_param.item(i);
            NamedNodeMap attrs = child.getAttributes();
            Node node_name = attrs.getNamedItem("name");
            Node node_value = attrs.getNamedItem("value");
            
            String nodeString = node_name.getNodeValue();
            System.out.println(nodeString);
            
            String valueString = node_value.getNodeValue();
            System.out.println(valueString);
            
//            boolean copyIt = true;
//            if (node_name != null) {
//                String compString = node_name.getNodeValue();
//                 System.out.println(compString);
//                if (!compString.equals("none")) {
//                    copyIt = false;
//                }
//            }
//            if (copyIt) {
//                String value = attrs.getNamedItem("value").getNodeValue();
//
//            }
    }

        
//        if(node_Password.item(0) != null) node_Password.item(0).setTextContent(pwd);

       
        
        request.saveChanges();
         request.writeTo(System.out);
         System.out.println(request.getSOAPBody().toString());
        SOAPMessage soapResponse = soapConnection.call(request, endPoint);

        StringWriter sw = new StringWriter();

        try {
            TransformerFactory.newInstance()
                              .newTransformer()
                              .transform(new DOMSource(soapResponse.getSOAPPart()), new StreamResult(sw));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }

        // Now you have the XML as a String:
        return sw.toString();
    }
  
  
  private static String consumSoap_1_2( String soapString,String endPoint ) throws UnsupportedOperationException, SOAPException, IOException{
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        
        InputStream is = new ByteArrayInputStream(soapString.getBytes());

        SOAPMessage request = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(null, is);

        MimeHeaders headers = request.getMimeHeaders();

        headers.setHeader("Content-Type", "application/xml");
        
//        SOAPPart soapPart = request.getSOAPPart();
//        
//        SOAPEnvelope envelope = soapPart.getEnvelope();
        
        SOAPHeader header = request.getSOAPHeader();
        SOAPBody soapBody = request.getSOAPBody();

        // get the body
//        SOAPBody soapBody = envelope.getBody();

        // find your node based on tag name
        NodeList nodes = soapBody.getElementsByTagName("ListMsisdn");

        if(nodes.item(0) != null) nodes.item(0).setTextContent("661000504");

        System.out.println(nodes.getClass().getName());
        // check if the node exists and get the value

        String someMsgContent = null;
        Node node = nodes.item(0);
        someMsgContent = node != null ? node.getTextContent() : "";

        System.out.println(someMsgContent);
        request.saveChanges();

        SOAPMessage soapResponse = soapConnection.call(request, endPoint);

        StringWriter sw = new StringWriter();

        try {
            TransformerFactory.newInstance()
                              .newTransformer()
                              .transform(new DOMSource(soapResponse.getSOAPPart()), new StreamResult(sw));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }

        // Now you have the XML as a String:
        return sw.toString();
    }
 
}
