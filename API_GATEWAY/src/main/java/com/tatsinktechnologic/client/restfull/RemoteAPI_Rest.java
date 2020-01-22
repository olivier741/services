/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.client.restfull;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.xml.ws.WebServiceException;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.log4j.Logger;

/**
 *
 * @author olivier.tatsinkou
 */
public class RemoteAPI_Rest {
    
      private static Logger logger = Logger.getLogger(RemoteAPI_Rest.class);

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

    public static Response sendRestfullRequest(Request request) {
        Response response = null;
        String endpointUrl = request.getHost();

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

            WebClient web_client = WebClient.create(endpointUrl);
            ClientConfiguration config = WebClient.getConfig(web_client);

            switch (request.getSec_mode()) {
                case REST_BASIC_AUTH:
                    AuthorizationPolicy pol = new AuthorizationPolicy();
                    if (request.getUser_name() != null) {
                        pol.setUserName(request.getUser_name());
                        
                       if (request.getPassword() != null) {
                            pol.setPassword(request.getPassword());
                            config.getHttpConduit().setAuthorization(pol);
                        }
                    }
                    break;

            }

            config.getOutInterceptors().add(new LoggingOutInterceptor());
            config.getInInterceptors().add(new LoggingInInterceptor());

            HTTPConduit http = (HTTPConduit) WebClient.getConfig(web_client).getConduit();
            HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
            // indefined

            httpClientPolicy.setConnectionTimeout(request.getConnexion_timeout());
            // indefined
            httpClientPolicy.setReceiveTimeout(request.getRequest_timeout());

            http.setClient(httpClientPolicy);

            switch (request.getMethod()){
                case GET : 
                     response = web_client.path(request.getPath())
                    .accept(request.getMedia_type())
                    .type(request.getContent_type())
                    .headers(request.getHeaders())
                    .get(Response.class);
                     
                     break;
                case POST:
                     response = web_client.path(request.getPath())
                        .accept(request.getMedia_type())
                        .type(request.getContent_type())
                        .headers(request.getHeaders())
                        .post(request.getRest_request(),Response.class);
                      break;
                      
                case PUT:
                     response = web_client.path(request.getPath())
                        .accept(request.getMedia_type())
                        .type(request.getContent_type())
                        .headers(request.getHeaders())
                        .put(request.getRest_request(),Response.class);
                      break;
                case DELETE:
                     response = web_client.path(request.getPath())
                        .accept(request.getMedia_type())
                        .type(request.getContent_type())
                        .headers(request.getHeaders())
                        .delete();
                      break;      
                 case HEAD:
                     response = web_client.path(request.getPath())
                        .accept(request.getMedia_type())
                        .type(request.getContent_type())
                        .headers(request.getHeaders())
                        .head();
                      break;   
            }
           
            if (isHttps) {
                httpsConnection.disconnect();
            }
            
        } catch (WebServiceException | IOException | NoSuchAlgorithmException | KeyManagementException ex) {
            logger.error(response, ex);
            response = Response.status(Response.Status.FORBIDDEN)
                             .header(HttpHeaders.WWW_AUTHENTICATE,"ERROR  Message=\"" + ex.getMessage() + "\"")
                             .build();
        }

        return response;

    }
       
       
}
