/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.gateway.security.handler;

import com.tatsinktechnologic.gateway.bean.Role;
import com.tatsinktechnologic.gateway.security.Secured_tokenAuth_Role;
import com.tatsinktechnologic.gateway.util.TokenManagement;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;

/**
 *
 * @author olivier.tatsinkou
 */
@Secured_tokenAuth_Role
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthenticationFilter_tokenAuth_Role implements ContainerRequestFilter{
    
    private static final String REALM = "example";
    private static final String AUTHENTICATION_SCHEME = "Bearer";
    private static Logger logger = Logger.getLogger(AuthenticationFilter_tokenAuth.class);
    private static  Properties prop = new Properties();
    
    @Context
    private ResourceInfo resourceInfo;
    
    
     static {
        try {
             prop.load(new FileInputStream("etc" + File.separator + "webservice.properties"));
         } catch (IOException e) {
             logger.error("cannot load webservice config file", e);
         }  
    }
    
     @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the resource class which matches with the requested URL
        // Extract the roles declared by it
        Class<?> resourceClass = resourceInfo.getResourceClass();
        List<Role> classRoles = extractRoles(resourceClass);

        // Get the resource method which matches with the requested URL
        // Extract the roles declared by it
        Method resourceMethod = resourceInfo.getResourceMethod();
        List<Role> methodRoles = extractRoles(resourceMethod);
        
        String userName = requestContext.getUriInfo()
                                 .getPathParameters()
                                 .getFirst("userName");

        try {

            // Check if the user is allowed to execute the method
            // The method annotations override the class annotations
            if (methodRoles.isEmpty()) {
                checkPermissions(classRoles);
            } else {
                checkPermissions(methodRoles);
            }
            
             if (userName == null || "".equals(userName)) {
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

                if (checkIP()){
                     // Extract the token from the Authorization header
                        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

                      try {

                          // Validate the token
                          validateToken(token,userName);

                      } catch (Exception e) {
                          abortWithUnauthorized(requestContext,e);
                      }
                }else {
                    abortWithUnauthorized(requestContext,new Exception("IP address of this Client is not allow"));
                }
           }

        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
        }
    }
    
    // Extract the roles from the annotated element
    private List<Role> extractRoles(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return new ArrayList<Role>();
        } else {
            Secured_tokenAuth_Role secured = annotatedElement.getAnnotation(Secured_tokenAuth_Role.class);
            if (secured == null) {
                return new ArrayList<Role>();
            } else {
                Role[] allowedRoles = secured.value();
                return Arrays.asList(allowedRoles);
            }
        }
    }

    private void checkPermissions(List<Role> allowedRoles) throws Exception {
        // Check if the user contains one of the allowed roles
        // Throw an Exception if the user has not permission to execute the method
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
        
           TokenManagement.checkTokenHS256(token,prop.getProperty("tokenSecret"),username);

    }
    
     private boolean checkIP() { 
       
        boolean result = false;
        List<String> listIP= Arrays.asList(prop.getProperty("IP_Allow").split(","));

        Message message = PhaseInterceptorChain.getCurrentMessage();
        HttpServletRequest request = (HttpServletRequest)message.get(AbstractHTTPDestination.HTTP_REQUEST);

        logger.info("Client IP Address = " + request.getRemoteAddr()); 

        if (listIP.contains(request.getRemoteAddr())) result= true;
        return result;
   }

}
