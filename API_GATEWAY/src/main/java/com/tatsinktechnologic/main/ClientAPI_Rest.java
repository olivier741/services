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
import com.tatsinktechnologic.gateway.bean.WS_Response;
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
 * @author olivier
 */
public class ClientAPI_Rest {

    private static String token= "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyYW5kb20iOiI4NDU2MjBiYy1hYWViLTRjMzItYjZmMy03ODcxMzM4YWUwZTUiLCJ1c2VyX25hbWUiOiJwbys0d1NHYmFuVVhkODRCaUJ2cWlaS0svYUFNVnNOaUpSM05ZOTE0VEhRPSIsImlzcyI6InBvKzR3U0diYW5VWGQ4NEJpQnZxaVpLSy9hQU1Wc05pSlIzTlk5MTRUSFE9IiwiZXhwIjoxNTY4NzEzNDkxfQ.dx1JgIGIwTwa6v9JSTxgA7qYfaOt2dAKk1xpi_bIDLY";
    /**
     * @param args the command line arguments
     */
    private static Logger logger = Logger.getLogger(ClientAPI_Rest.class);
    
    
    private static String getToken(String url, String path,Credential account){
        String result = null;
        try {
            WebClient client_auth = WebClient.create(url);
            ClientConfiguration config = WebClient.getConfig(client_auth);

            config.getOutInterceptors().add(new LoggingOutInterceptor());
            config.getInInterceptors().add(new LoggingInInterceptor());

            Response resp_token = client_auth.path(path)
                    .accept(MediaType.APPLICATION_XML)
                    .type(MediaType.APPLICATION_XML)
                    .post(account);

            result = resp_token.readEntity(String.class);

            logger.info("Auth request : \n" + ConverterXML_JSON.convertCredentialToXML(account));
            logger.info("Auth token : " + result);
            
        } catch (WebServiceException wse) {
            logger.error("Cannot get Token 1",wse);
        } catch (Exception ex) {
            logger.error("Cannot get Token 2",ex);
        }
        
        return result;
    }
    
    private static Response sendAPI_Request(String url,String path,Credential account,String token,int con_timeout,int rcv_timeout,WS_Request request){
       
        WebClient client_request = WebClient.create(url);
        ClientConfiguration configBook = WebClient.getConfig(client_request);

        configBook.getOutInterceptors().add(new LoggingOutInterceptor());
        configBook.getInInterceptors().add(new LoggingInInterceptor());

        HTTPConduit http = (HTTPConduit) WebClient.getConfig(client_request).getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        // indefined
        
        Response response = null;
        
        try {

            httpClientPolicy.setConnectionTimeout(con_timeout);
            // indefined
            httpClientPolicy.setReceiveTimeout(rcv_timeout);

            http.setClient(httpClientPolicy);

            response = client_request.path(path).accept(MediaType.APPLICATION_XML)
                    .type(MediaType.APPLICATION_XML)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header("Account", account.getWs_client_name() + "_" + account.getWs_block_api_name())
                    .post(request, Response.class);
            
        } catch (WebServiceException wse) {
           logger.error("Webservice Error 1",wse);
           response= Response.status(Response.Status.BAD_GATEWAY)
                        .header(HttpHeaders.RETRY_AFTER,"ERROR : MESSAGE= "+wse.getMessage())
                        .build(); 
        } catch (Exception ex) {
            logger.error("Webservice Error 2",ex);
            response= Response.status(Response.Status.BAD_GATEWAY)
                        .header(HttpHeaders.RETRY_AFTER,"ERROR : MESSAGE=" +ex.getMessage())
                        .build();
        }
       return response;
    }

    public static void main(String[] args) {
        // TODO code application logic here

        // TODO code application logic here
        PropertyConfigurator.configure("etc" + File.separator + "log4j.cfg");
        logger.info("Load log4j config file done.");
        
        Credential account = new Credential();
        account.setWs_client_name("test");
        account.setWs_block_api_name("test_block");
        account.setUser_name("test");
        account.setPassword("test");


       
        WS_Request request = new WS_Request();
        request.setWs_client_name(account.getWs_client_name());
        request.setWs_block_api_name(account.getWs_block_api_name());


        List<Param> listparam = new ArrayList<Param>();

        Param param1 = new Param();
        param1.setAlias("%sender%");
        param1.setValue("IRON");
        listparam.add(param1);

        Param param2 = new Param();
        param2.setAlias("%msisdn%");
        param2.setValue("237661000504");
        listparam.add(param2);

        Param param3 = new Param();
        param3.setAlias("%message%");
        param3.setValue("Hello");
        listparam.add(param3);

        request.setWSparam(listparam);

        String stringReq = ConverterXML_JSON.convertWS_RequestToXML(request);

        logger.info("request : \n" + stringReq);


        Timestamp beforeRequest_time = new Timestamp(System.currentTimeMillis());

        Response response = sendAPI_Request("http://127.0.0.1:9125",
                                        "/api_service/sendRequest",
                                        account,
                                        token,
                                        6000,
                                        6000,
                                        request);

        int error_code = response.getStatus();
        if (error_code!=200){
                        token= getToken("http://127.0.0.1:9125",
                               "/authentication/auth",
                               account);

                        response = sendAPI_Request("http://127.0.0.1:9125",
                                            "/api_service/sendRequest",
                                            account,
                                            token,
                                            6000,
                                            6000,
                                            request);
                        error_code = response.getStatus();
            }

            if (error_code==200){
                Timestamp receive_time = new Timestamp(System.currentTimeMillis());
                long diffInMS = (receive_time.getTime() - beforeRequest_time.getTime());

                WS_Block_Response resp = response.readEntity(WS_Block_Response.class);
                if (resp!= null){
                     String stringResp = ConverterXML_JSON.convertWS_Block_ResponseToXML(resp);

                    logger.info("duration to request " + "OCM_Charging" + " : " + diffInMS + " ms");
                    logger.info("response : \n" + stringResp);
                }
            }


    }

}
