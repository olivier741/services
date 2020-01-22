/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.client.soap;

import com.tatsinktechnologic.entities.api_gateway.Security_Mode;
import com.tatsinktechnologic.entities.api_gateway.WS_Header_Param;
import com.tatsinktechnologic.entities.api_gateway.WS_Webservice;
import com.tatsinktechnologic.gateway.bean.Param;
import com.tatsinktechnologic.gateway.bean.Soap_Resp;
import com.tatsinktechnologic.gateway.util.ConverterXML_JSON;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.HttpHeaders;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author olivier.tatsinkou
 */
public class RemoteAPI_Soap {

    private static Logger logger = Logger.getLogger(RemoteAPI_Soap.class);

    /**
     * Dummy class implementing HostnameVerifier to trust all host names
     */
    private static class TrustAllHosts implements HostnameVerifier {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * Dummy class implementing X509TrustManager to trust all certificates
     */
    private static class TrustAllCertificates implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    private static class SoapResp {

        private SOAPMessage soap_message;
        private int error_code;

        public SOAPMessage getSoap_message() {
            return soap_message;
        }

        public void setSoap_message(SOAPMessage soap_message) {
            this.soap_message = soap_message;
        }

        public int getError_code() {
            return error_code;
        }

        public void setError_code(int error_code) {
            this.error_code = error_code;
        }

    }

    /**
     * Sends SOAP request and saves it in a queue.
     *
     * @param request SOAP Message request object
     * @return SOAP Message response object
     */
    private static SoapResp sendSoapRequest(String endpointUrl, SOAPMessage request) {
        SoapResp response = new SoapResp();
        try {
            final boolean isHttps = endpointUrl.toLowerCase().startsWith("https");
            HttpsURLConnection httpsConnection = null;

            // Open HTTPS connection
            if (isHttps) {
                // Create SSL context and trust all certificates
                SSLContext sslContext = SSLContext.getInstance("SSL");
                TrustManager[] trustAll = new TrustManager[]{new TrustAllCertificates()};
                sslContext.init(null, trustAll, new java.security.SecureRandom());
                // Set trust all certificates context to HttpsURLConnection
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                // Open HTTPS connection
                URL url = new URL(endpointUrl);
                httpsConnection = (HttpsURLConnection) url.openConnection();
                // Trust all hosts
                httpsConnection.setHostnameVerifier(new TrustAllHosts());
                // Connect
                httpsConnection.connect();
            }

            // Send HTTP SOAP request and get response
            SOAPConnection soapConnection = SOAPConnectionFactory.newInstance().createConnection();
            response.setSoap_message(soapConnection.call(request, endpointUrl));

            // Close connection
            soapConnection.close();

            // Close HTTPS connection
            if (isHttps) {
                httpsConnection.disconnect();
            }

        } catch (SOAPException | IOException | NoSuchAlgorithmException | KeyManagementException ex) {
            logger.error("WEB SERVICE ERROR in sendSoapRequest", ex);
            response.setError_code(-1);
        } catch (Exception e) {
            logger.error("ERROR in sendSoapRequest", e);
            response.setError_code(-2);
        }
        return response;
    }

    public static Soap_Resp consumeSoap(WS_Webservice websrv, List<Param> listParam) {

        String endPoint = websrv.getApi_url();
        String soapString = websrv.getApi_soap_request();
        String username = websrv.getSecurity_login();
        String password = websrv.getSecurity_password();
        String authorizationType = websrv.getAuthorizationType();
        String token = websrv.getToken();
        String content_type = websrv.getContent_type();
        Soap_Resp soap_req = new Soap_Resp();

        for (Param param : listParam) {
            soapString = soapString.replace(param.getAlias(), param.getValue());
        }
        SOAPMessage soapResponse = null;
        try {

            soap_req.setSoap_request(soapString);
            InputStream is = new ByteArrayInputStream(soapString.getBytes());
            SOAPMessage request = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage(null, is);
            MimeHeaders headers = request.getMimeHeaders();

            List<WS_Header_Param> listheaders_param = new ArrayList<WS_Header_Param>(websrv.getListWS_Header_Param());

            if (listheaders_param != null && !listheaders_param.isEmpty()) {
                for (WS_Header_Param head_param : listheaders_param) {
                    String param_key = head_param.getParam_key();
                    String param_val = head_param.getParam_value();
                    for (Param param : listParam) {
                        param_key = param_key.replace(param.getAlias(), param.getValue());
                        param_val = param_val.replace(param.getAlias(), param.getValue());
                    }
                    headers.addHeader(param_key, param_val);
                }
            }

            if (content_type != null) {
                headers.setHeader("Content-Type", content_type);
            } else {
                headers.setHeader("Content-Type", "application/xml");
            }

            Security_Mode secure_mode = websrv.getRemote_secutity_mode();

            switch (secure_mode) {
                case SOAP_BASIC_AUTH:
                    if (!StringUtils.isBlank(username)) {
                        if (!StringUtils.isBlank(password)) {
                            String authorization = new sun.misc.BASE64Encoder().encode((username + ":" + password).getBytes());
                            headers.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + authorization);
                        }
                    }
                    break;
                case SOAP_HEAD_TOKEN:
                    if (!StringUtils.isBlank(token)) {
                        if (!StringUtils.isBlank(authorizationType)) {
                            token = authorizationType + " " + token;
                        }

                        headers.addHeader(HttpHeaders.AUTHORIZATION, token);
                    }
                    break;

            }

            soap_req.setSoap_head_request1(ConverterXML_JSON.getSoap_Header1(headers));
            soap_req.setSoap_head_request2(ConverterXML_JSON.getSoap_Header2(headers));

            request.saveChanges();
            SoapResp soapResp = sendSoapRequest(endPoint, request);
            soapResponse = soapResp.getSoap_message();
            soap_req.setSoap_erro_code(soapResp.getError_code());
        } catch (UnsupportedOperationException | SOAPException | IOException ex) {
            logger.error("WEB SERVICE ERROR in consumeSoap", ex);
            soap_req.setSoap_erro_code(-3);
        } catch (Exception e) {
            logger.error("ERROR in consumeSoap", e);
            soap_req.setSoap_erro_code(-4);
        }

        StringWriter sw = new StringWriter();

        try {
            if (soapResponse != null) {
                TransformerFactory.newInstance()
                        .newTransformer()
                        .transform(new DOMSource(soapResponse.getSOAPPart()), new StreamResult(sw));
            }
        } catch (TransformerException e) {
            logger.error("WEB SERVICE ERROR: cannot convert Saop response to test/XML", e);
            soap_req.setSoap_erro_code(-5);
        }

        // Now you have the XML as a String:
        soap_req.setSoap_response(sw.toString());
        return soap_req;
    }

}
