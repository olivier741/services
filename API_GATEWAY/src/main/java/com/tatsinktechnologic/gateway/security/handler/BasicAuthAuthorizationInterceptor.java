/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.gateway.security.handler;

/**
 *
 * @author olivier.tatsinkou
 */
import java.io.IOException; 
import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.container.ContainerRequestContext; 
import javax.ws.rs.container.ContainerRequestFilter; 
import javax.ws.rs.core.Response; 

import org.apache.cxf.configuration.security.AuthorizationPolicy; 
import org.apache.cxf.message.Message; 
import org.apache.cxf.phase.PhaseInterceptorChain; 
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;

public class BasicAuthAuthorizationInterceptor  implements ContainerRequestFilter {
    
    private static Logger logger = Logger.getLogger(BasicAuthAuthorizationInterceptor.class);
         
    @Override 
    public void filter(ContainerRequestContext req) throws IOException { 
        logger.info("filter!"); 
        Message message = PhaseInterceptorChain.getCurrentMessage(); 
        AuthorizationPolicy policy = (AuthorizationPolicy)message.get(AuthorizationPolicy.class); 
         
        if (policy==null){ 
            req.abortWith(Response.status(401).header("WWW-Authenticate", "Basic realm=\"PTServer\"").build());         
            return; 
        }
        
        String username = policy.getUserName(); 
        String password = policy.getPassword();  
        
       if ("demo".equals(password)&&"demo".equals(username)&&checkIP()) { 
            return; 
        }else if (!("demo".equals(password)&&"demo".equals(username))){ 
            req.abortWith(Response.status(401).header("WWW-Authenticate", "Basic realm=\"PTServer\"").build()); 
        }else if (!checkIP()) {
            req.abortWith(Response.status(401).header("WWW-IP Authorization", "client IP address is not allow.").build()); 
            
        }
       

    } 
    
     private boolean checkIP() { 
       
        boolean result = false;
//      List<String> listIP= Arrays.asList(prop.getProperty("IP_ALLOW").split(","));

        Message message = PhaseInterceptorChain.getCurrentMessage();
        HttpServletRequest request = (HttpServletRequest)message.get(AbstractHTTPDestination.HTTP_REQUEST);

        logger.info("Client IP Address = " + request.getRemoteAddr()); 

        if ("127.0.0.1".equals(request.getRemoteAddr())) result= true;
        return result;
   }

}
