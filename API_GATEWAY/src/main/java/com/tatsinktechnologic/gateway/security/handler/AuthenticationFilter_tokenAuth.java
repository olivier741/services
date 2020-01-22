/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.gateway.security.handler;


import com.tatsinktechnologic.dao_commun_repository.CommunDAO_Repository;
import com.tatsinktechnologic.entities.api_gateway.WS_AccessManagement;
import com.tatsinktechnologic.entities.api_gateway.WS_Block_API;
import com.tatsinktechnologic.entities.api_gateway.WS_Client;
import com.tatsinktechnologic.gateway.util.TokenManagement;
import javax.ws.rs.Priorities;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;


import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
import com.tatsinktechnologic.gateway.security.Secured_tokenAuth;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author olivier.tatsinkou
 */
@Secured_tokenAuth
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter_tokenAuth implements ContainerRequestFilter{
   
    public static final String AUTHENTICATION_HEADER = "Account";
    private static CommunDAO_Repository communConf = CommunDAO_Repository.getConfigurationLoader();
    private static final String AUTHENTICATION_SCHEME = "Bearer";
    private static Logger logger = Logger.getLogger(AuthenticationFilter_tokenAuth.class);
    private static HashMap<String,WS_AccessManagement> SETACCESSMANAGEMENT;
    private static String tokenSec;
    private static int tokenduration;
    
    static {
        SETACCESSMANAGEMENT = communConf.getSETACCESSMANAGEMENT();
        tokenSec = communConf.getApp_conf().getTokenSecret();
        tokenduration = communConf.getApp_conf().getTokenDuration();
    }
    

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String ws_access_account = requestContext.getHeaderString(AUTHENTICATION_HEADER);
        
        if (ws_access_account == null || "".equals(ws_access_account)) {
            abortWithUnauthorized(requestContext, new Exception("this request don't have value in userName parameter.") );
            return;
        } else{
                    // Get the Authorization header from the request
               String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);


               // Validate the Authorization header
               if (!isTokenBasedAuthentication(authorizationHeader)) {
                   abortWithUnauthorized(requestContext, new Exception("Not Bearer Authentication in the Header request") );
                   return;
               }
               
                WS_AccessManagement accessMng = SETACCESSMANAGEMENT.get(ws_access_account);
        
                if (accessMng!=null){
                    
                    WS_Client ws_client = accessMng.getWs_client();
                    WS_Block_API ws_block_api = accessMng.getWs_block_api();

                    if (ws_client!=null && ws_block_api!=null ){
                        
                      
                        if (checkIP(ws_client.getIp_address())){
                          // Extract the token from the Authorization header
                          String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
                         
                          try {
                              // Validate the token
                              validateToken(token,ws_client.getLogin());

                          } catch (Exception e) {
                              abortWithUnauthorized(requestContext,e);
                          }
                      }else{
                        abortWithUnauthorized(requestContext,new Exception("IP address of this Client is not allow"));    
                      }

                    }else {
                        abortWithUnauthorized(requestContext,new Exception("Client or Webservice not define"));
                         
                    }
                }else{
                    abortWithUnauthorized(requestContext,new Exception("Not Configuration for this Account"));  
                }
        }
    
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {

        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                    .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext,Exception e) {

        // Abort the filter chain with a 401 status code response
        // The WWW-Authenticate header is sent along with the response
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                                         .header(HttpHeaders.WWW_AUTHENTICATE,AUTHENTICATION_SCHEME + " realm=\"" + e.getMessage() + "\"")
                                         .build());
    }

    private void validateToken(String token,String username) throws Exception {
        // Check if the token was issued by the server and if it's not expired
        // Throw an Exception if the token is invalid
        
        TokenManagement.checkTokenHS256(token,tokenSec,username);

    }
    
     private boolean checkIP(String ip_address) { 
       
        Pattern ptn = Pattern.compile("\\|");
        List<String> listIP= Arrays.asList(ptn.split(ip_address));

        Message message = PhaseInterceptorChain.getCurrentMessage();
        HttpServletRequest request = (HttpServletRequest)message.get(AbstractHTTPDestination.HTTP_REQUEST);
        String callerIpAddress = request.getRemoteAddr();
        IpAddressMatcher ipAddressMacher = new IpAddressMatcher(listIP);
        logger.info("Client IP Address = " + callerIpAddress); 

        return ipAddressMacher.matches(callerIpAddress);
   }
      
      
      
    private final class IpAddressMatcher {
            private final List<Integer> listnMaskBits;
            private final List<InetAddress> listrequiredAddress;

            /**
             * Takes a specific IP address or a range specified using the IP/Netmask (e.g.
             * 192.168.1.0/24 or 202.24.0.0/14).
             *
             * @param ipAddress the address or range of addresses from which the request must
             * come.
             */
            public IpAddressMatcher(List<String> listIpAddress) {
                listnMaskBits= new ArrayList<Integer>();
                listrequiredAddress =new ArrayList<InetAddress>();

                for(String ipAddress : listIpAddress){
                   int nMaskBits;
                   if (ipAddress.indexOf('/') > 0) {
                      String[] addressAndMask = ipAddress.split("/");
                      ipAddress = addressAndMask[0];
                      nMaskBits = Integer.parseInt(addressAndMask[1]);
                  }else {
                      nMaskBits = -1;
                  }
                  listnMaskBits.add(nMaskBits);
                  listrequiredAddress.add(parseAddress(ipAddress));
                }
                
            }

            public boolean matches(String address) {
                boolean result = false;
                int numberIP = listrequiredAddress.size();
                InetAddress remoteAddress = parseAddress(address);
                
                for (int i= 0;i < numberIP; i++){
 
                    if (!listrequiredAddress.get(i).getClass().equals(remoteAddress.getClass())) {
                           continue;
                     }
                    
                    if (listnMaskBits.get(i) < 0) {
                        if (remoteAddress.equals(listrequiredAddress.get(i))){
                            result= true;
                            break;
                        }else{
                            continue;
                        }
                    }
                    
                    byte[] remAddr = remoteAddress.getAddress();
                    byte[] reqAddr = listrequiredAddress.get(i).getAddress();

                    int oddBits = listnMaskBits.get(i) % 8;
                    int nMaskBytes = listnMaskBits.get(i) / 8 + (oddBits == 0 ? 0 : 1);
                    byte[] mask = new byte[nMaskBytes];
                    
                    Arrays.fill(mask, 0, oddBits == 0 ? mask.length : mask.length - 1, (byte) 0xFF);

                    if (oddBits != 0) {
                        int finalByte = (1 << oddBits) - 1;
                        finalByte <<= 8 - oddBits;
                        mask[mask.length - 1] = (byte) finalByte;
                    }
                    
                    boolean isNOK = true;
                    for (int j = 0; j < mask.length; j++) {
                        if ((remAddr[j] & mask[j]) != (reqAddr[j] & mask[j])) {
                            isNOK = false;
                            break;
                        }
                    }
                    
                    if (!isNOK){
                        continue;
                    }else{
                      result= true;
                      break;  
                    }

                }
                return result;
            }

            private InetAddress parseAddress(String address) {
                InetAddress result = null;
                try {
                    result= InetAddress.getByName(address);
                }
                catch (UnknownHostException e) {
                    logger.error("Failed to parse address" + address, e);
                }
                return result;        
            }
      }

}
