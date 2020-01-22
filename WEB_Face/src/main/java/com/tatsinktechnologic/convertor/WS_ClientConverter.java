/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.convertor;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.api_gateway.WS_Client;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

/**
 *
 * @author olivier.tatsinkou
 */

@FacesConverter(value = "ws_clientConverter")
public class WS_ClientConverter implements Converter{
    @Inject
    private Commun_Controller commun_controller;
    
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object object) {
            String result = null;
            if (object == null || "".equals(object)) {
                    result = null;
            }

            if (object instanceof WS_Client) {
                WS_Client action = (WS_Client)object;
                result  = action.getClient_name();
            } 
            return result;
    }

    @Override
    public WS_Client getAsObject(FacesContext context, UIComponent component, String uuid) {
        WS_Client action = commun_controller.getOneByWS_Client(uuid);
        return action;
    }
}