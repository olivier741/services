/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.gateway.security;


import com.tatsinktechnologic.dao_commun_repository.CommunDAO_Repository;
import com.tatsinktechnologic.entities.api_gateway.WS_AccessManagement;
import com.tatsinktechnologic.entities.api_gateway.WS_Block_API;
import com.tatsinktechnologic.entities.api_gateway.WS_Client;
import com.tatsinktechnologic.gateway.bean.Credential;
import com.tatsinktechnologic.gateway.util.TokenManagement;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.hash.Sha256Hash;

/**
 *
 * @author olivier.tatsinkou
 */
@Path("/authentication")
public class AuthenticationService_tokenAuth {
    
    private static CommunDAO_Repository communConf = CommunDAO_Repository.getConfigurationLoader();
    private static Logger logger = Logger.getLogger(AuthenticationService_tokenAuth.class);
    private static String tokenSec;
    private static int tokenduration;
    private static HashMap<String,WS_AccessManagement> SETACCESSMANAGEMENT;
    
    static {
        SETACCESSMANAGEMENT = communConf.getSETACCESSMANAGEMENT();
        tokenSec = communConf.getApp_conf().getTokenSecret();
        tokenduration = communConf.getApp_conf().getTokenDuration();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    @Path("/auth")
    public Response authenticateUser( Credential compte ) {
      
        WS_AccessManagement accessMng = SETACCESSMANAGEMENT.get(compte.getWs_client_name()+"_"+compte.getWs_block_api_name());
        
        if (accessMng!=null){
            WS_Client ws_client = accessMng.getWs_client();
            WS_Block_API ws_block_api = accessMng.getWs_block_api();
            
            if (ws_client!=null && ws_block_api!=null ){
                String sys_password = ws_client.getPassword();
                String sys_login = ws_client.getLogin();
              if (checkIP(ws_client.getIp_address())){
                  try {
                      
                     String curLoginEncrypt = new Sha256Hash(compte.getUser_name(), Base64.decode(ws_client.getLogin_salt()), 1024).toBase64();

                     String curPassEncrypt = new Sha256Hash(compte.getPassword(), Base64.decode(ws_client.getPassword_salt()), 1024).toBase64();
                     // Authenticate the user using the credentials provided
                     authenticate(curLoginEncrypt,sys_login, curPassEncrypt,sys_password);

                     // Issue a token for the user
                     String token = issueToken(sys_login);

                     // Return the token on the response

                     return Response.ok(token).build();

                 } catch (Exception e) {
                     return Response.status(Response.Status.FORBIDDEN)
                             .header(HttpHeaders.WWW_AUTHENTICATE,"ERROR  Message=\"" + e.getMessage() + "\"")
                             .build();
                 }   
              }else{
                return Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE,"ERROR : MESSAGE=\" IP address of this Client is not allow \"")
                        .build();    
              }
                
            }else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE,"ERROR : MESSAGE=\" Client or Webservice not define \"")
                        .build();    
            }
        }else{
            return Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE,"ERROR : MESSAGE=\" Not Configuration for this Account \"")
                        .build();    
        }

    }

    private void authenticate( String loging, String sysLoging,String password,String sys_password) throws Exception {
        // Authenticate against a database, LDAP, file or whatever
        // Throw an Exception if the credentials are invalid
       
        if(!loging.equals(sysLoging) || !(sys_password.equals(password) )){
            Exception e = new Exception();
            throw e;
        }

    }

    private String issueToken(String username) {
        // Issue a token (can be a random String persisted to a database or a JWT token)
        // The issued token must be associated to a user
        // Return the issued token
        
       String token =TokenManagement.getTokenHS256(username,tokenSec,tokenduration);
       return token;
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