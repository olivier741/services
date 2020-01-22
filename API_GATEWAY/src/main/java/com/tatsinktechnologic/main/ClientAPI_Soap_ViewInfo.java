/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.main;

import com.tatsinktechnologic.gateway.bean.Credential;
import com.tatsinktechnologic.gateway.bean.Param;
import com.tatsinktechnologic.gateway.bean.WS_Block_Response;
import com.tatsinktechnologic.gateway.bean.WS_Request;
import com.tatsinktechnologic.gateway.util.ConverterXML_JSON;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.WebServiceException;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author olivier.tatsinkou
 */
public class ClientAPI_Soap_ViewInfo {

     private static Logger logger = Logger.getLogger(ClientAPI_Soap_ViewInfo.class);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
          // TODO code application logic here

        // TODO code application logic here
        PropertyConfigurator.configure("etc" + File.separator + "log4j.cfg");
        logger.info("Load log4j config file done.");

        WebClient client_auth = WebClient.create("http://127.0.0.1:9125");
        ClientConfiguration config = WebClient.getConfig(client_auth);

        Credential account = new Credential();
        account.setWs_client_name("test");
        account.setWs_block_api_name("test_view_info_block");
        account.setUser_name("test");
        account.setPassword("test");

        config.getOutInterceptors().add(new LoggingOutInterceptor());
        config.getInInterceptors().add(new LoggingInInterceptor());

        Response resp_token = client_auth.path("/authentication/auth")
                .accept(MediaType.APPLICATION_XML)
                .type(MediaType.APPLICATION_XML)
                .post(account);

        String token = resp_token.readEntity(String.class);
        logger.info("Auth request : \n" + ConverterXML_JSON.convertCredentialToXML(account));
        logger.info("Auth token : " + token);

        WebClient client_request = WebClient.create("http://127.0.0.1:9125");
        ClientConfiguration configBook = WebClient.getConfig(client_request);

        configBook.getOutInterceptors().add(new LoggingOutInterceptor());
        configBook.getInInterceptors().add(new LoggingInInterceptor());

        HTTPConduit http = (HTTPConduit) WebClient.getConfig(client_request).getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        // indefined

        Timestamp beforeRequest_time = new Timestamp(System.currentTimeMillis());
         Response response = null;
        try {

            httpClientPolicy.setConnectionTimeout(6000);
            // indefined
            httpClientPolicy.setReceiveTimeout(60000);

            http.setClient(httpClientPolicy);

            WS_Request request = new WS_Request();
            request.setWs_client_name(account.getWs_client_name());
            request.setWs_block_api_name(account.getWs_block_api_name());
            request.setMsisdn("237661000504");
            request.setReg_transaction_id("jkjqdlf-455662qsdfd-3dqsdfd6qd-qdfd54");
            List<Param> listparam = new ArrayList<Param>();

            Param param1 = new Param();
            param1.setAlias("%sender%");
            param1.setValue("IRON");
            listparam.add(param1);

            Param param2 = new Param();
            param2.setAlias("_msisdn_");
            param2.setValue("237661000504");
            listparam.add(param2);

            Param param3 = new Param();
            param3.setAlias("%message%");
            param3.setValue("Hello");
            listparam.add(param3);

            request.setWSparam(listparam);

            String stringReq = ConverterXML_JSON.convertWS_RequestToXML(request);

            logger.info("request : \n" + stringReq);

            response = client_request.path("/api_service/sendRequest").accept(MediaType.APPLICATION_XML)
                    .type(MediaType.APPLICATION_XML)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header("Account", account.getWs_client_name() + "_" + account.getWs_block_api_name())
                    .post(request, Response.class);

            Timestamp receive_time = new Timestamp(System.currentTimeMillis());
            long diffInMS = (receive_time.getTime() - beforeRequest_time.getTime());
             WS_Block_Response resp = response.readEntity(WS_Block_Response.class);
            if (resp!= null){
                 String stringResp = ConverterXML_JSON.convertWS_Block_ResponseToXML(resp);

                stringResp = stringResp.replace("&lt;", "<");
                stringResp = stringResp.replace("&gt;", ">");
                logger.info("duration to request " + "OCM_Charging" + " : " + diffInMS + " ms");
                logger.info("response : \n" + stringResp);
            }
           
        } catch (WebServiceException wse) {
            logger.error("message 1");
        } catch (Exception ex) {
            logger.error("message 2");
        }

    }

}


