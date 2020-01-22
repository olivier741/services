/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.convertor;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.registration.Product;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

/**
 *
 * @author olivier
 */
@FacesConverter(value = "productConverter")
public class ProductConverter implements Converter{
    
    @Inject
    private Commun_Controller commun_controller;
    
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object object) {
            String result = null;
            if (object == null || "".equals(object)) {
                    result = null;
            }

            if (object instanceof Product) {
                Product product = (Product)object;
                result  = product.getProduct_code();
            } 
            return result;
    }

    @Override
    public Product getAsObject(FacesContext context, UIComponent component, String uuid) {
        
        if (uuid == null || uuid.isEmpty()) {
            return null;
        }

        try {
            return  commun_controller.getOneByProduct(uuid.trim());
        } catch (NumberFormatException e) {
            throw new ConverterException(new FacesMessage(uuid + " is not a valid Product"), e);
        }
    }
}
