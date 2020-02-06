/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.resfull.interfaces;

import com.tatsinktechnologic.beans.UssdMessage;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author olivier.tatsinkou
 */
@Path("/ussdservice")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public interface IUSSDService {

    @POST
    @Path("ussdMessage_post")
    public UssdMessage addUssdMessage_post(UssdMessage ussdMessage);

    @GET
    @Path("ussdMessage_get")
    public UssdMessage addUssdMessage_get(UssdMessage ussdMessage);
    
    @PUT
    @Path("ussdMessage_put")
    public UssdMessage addUssdMessage_put(UssdMessage ussdMessage);
    
    @HEAD
    @Path("ussdMessage_head")
    public UssdMessage addUssdMessage_head(UssdMessage ussdMessage);
    
    @DELETE
    @Path("ussdMessage_delete")
    public UssdMessage addUssdMessage_delete(UssdMessage ussdMessage);
    
    @PATCH
    @Path("ussdMessage_patch")
    public UssdMessage addUssdMessage_patch(UssdMessage ussdMessage);
    
    @OPTIONS
    @Path("ussdMessage_options")
    public UssdMessage addUssdMessage_options(UssdMessage ussdMessage);

}
