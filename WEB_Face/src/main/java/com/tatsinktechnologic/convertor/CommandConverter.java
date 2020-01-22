/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.convertor;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.registration.Command;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

/**
 *
 * @author olivier.tatsinkou
 */
@FacesConverter(value = "commandConverter")
public class CommandConverter implements Converter{
    @Inject
    private Commun_Controller commun_controller;
    
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object object) {
            String result = null;
            if (object == null || "".equals(object)) {
                    result = null;
            }

            if (object instanceof Command) {
                Command comd = (Command)object;
                result  = comd.getCommand_code();
            } 
            return result;
    }

    @Override
    public Command getAsObject(FacesContext context, UIComponent component, String uuid) {
        Command comd = commun_controller.getOneByCommand(uuid);
        return comd;
    }
}