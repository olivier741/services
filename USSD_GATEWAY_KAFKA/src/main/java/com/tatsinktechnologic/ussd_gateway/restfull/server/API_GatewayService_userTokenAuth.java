/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.restfull.server;

import com.tatsinktechnologic.ussd_gateway.ussdapp.common.UssdMessage;
import com.tatsinktechnologic.ussd_gateway.ussdapp.config.CommunDAO_Repository;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;

/**
 *
 * @author olivier.tatsinkou
 */
@Path("/ussdgw")
@Produces({MediaType.APPLICATION_XML,
           MediaType.APPLICATION_JSON,
           MediaType.APPLICATION_FORM_URLENCODED,
           MediaType.APPLICATION_JSON_PATCH_JSON,
           MediaType.APPLICATION_OCTET_STREAM,
           MediaType.WILDCARD})
public class API_GatewayService_userTokenAuth {

    private static Logger logger = Logger.getLogger(API_GatewayService_userTokenAuth.class);
    private static CommunDAO_Repository communConf = CommunDAO_Repository.getConfigurationLoader();


    @Context
    SecurityContext securityContext;

    @POST
    @Path("/sendRequest_post")
    @Consumes({MediaType.APPLICATION_XML,
           MediaType.APPLICATION_JSON,
           MediaType.APPLICATION_FORM_URLENCODED,
           MediaType.APPLICATION_JSON_PATCH_JSON,
           MediaType.APPLICATION_OCTET_STREAM,
           MediaType.WILDCARD})
    public Response sendRequest_Post(UssdMessage ussdMsg, @Context SecurityContext securityContext) {
         return processRequest(ussdMsg);
    }
    
    @GET
    @Path("/sendRequest_get")
    @Consumes({MediaType.APPLICATION_XML,
           MediaType.APPLICATION_JSON,
           MediaType.APPLICATION_FORM_URLENCODED,
           MediaType.APPLICATION_JSON_PATCH_JSON,
           MediaType.APPLICATION_OCTET_STREAM,
           MediaType.WILDCARD})
    public Response sendRequest_Get(UssdMessage ussdMsg, @Context SecurityContext securityContext) {
         return processRequest(ussdMsg);
    }
    
    
    @PUT
    @Path("/sendRequest_put")
    @Consumes({MediaType.APPLICATION_XML,
           MediaType.APPLICATION_JSON,
           MediaType.APPLICATION_FORM_URLENCODED,
           MediaType.APPLICATION_JSON_PATCH_JSON,
           MediaType.APPLICATION_OCTET_STREAM,
           MediaType.WILDCARD})
    public Response sendRequest_Put(UssdMessage ussdMsg, @Context SecurityContext securityContext) {
         return processRequest(ussdMsg);
    }
    
    @HEAD
    @Path("/sendRequest_head")
    @Consumes({MediaType.APPLICATION_XML,
           MediaType.APPLICATION_JSON,
           MediaType.APPLICATION_FORM_URLENCODED,
           MediaType.APPLICATION_JSON_PATCH_JSON,
           MediaType.APPLICATION_OCTET_STREAM,
           MediaType.WILDCARD})
    public Response sendRequest_Head(UssdMessage ussdMsg, @Context SecurityContext securityContext) {
        return processRequest(ussdMsg);
    }

    
    private Response processRequest(UssdMessage ussdMsg){
        
         Response.Status status_err = Response.Status.OK;
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         return Response.status(status_err)
                .entity("")
                .build();
    }
    
    private String getIP() {

        Message message = PhaseInterceptorChain.getCurrentMessage();
        HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        String callerIpAddress = request.getRemoteAddr();

        return callerIpAddress;
    }
}
