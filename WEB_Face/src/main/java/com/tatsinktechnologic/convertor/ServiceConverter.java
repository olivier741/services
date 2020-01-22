/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.convertor;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.registration.Service;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.application.FacesMessage;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
/**
 *
 * @author olivier.tatsinkou
 */
@FacesConverter(value = "serviceConverter")
public class ServiceConverter implements Converter{
   
    @Inject
    private Commun_Controller commun_controller;
    
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object object) {
            String result = null;
            if (object == null || "".equals(object)) {
                    result = null;
            }

            if (object instanceof Service) {
                Service service = (Service)object;
                result  = service.getService_name();
            } 
            return result;
    }

    @Override
    public Service getAsObject(FacesContext context, UIComponent component, String uuid) {
        
        if (uuid == null || uuid.isEmpty()) {
            return null;
        }

        try {
            return  commun_controller.getOneByService(uuid.trim());
        } catch (NumberFormatException e) {
            throw new ConverterException(new FacesMessage(uuid + " is not a valid Servivce"), e);
        }
    }
}
