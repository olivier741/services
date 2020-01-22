/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.convertor;

/**
 *
 * @author olivier
 */

import com.tatsinktechnologic.entities.account.Email;
import com.tatsinktechnologic.entities.account.Phone;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.util.List;
import java.util.Objects;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "phonePickListConverter")
public class PhonePickListConverter implements Converter {

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object object) {
            String result = null;
            if (object == null || "".equals(object)) {
                    result = null;
            }

            if (object instanceof Phone) {
                Phone entity = (Phone)object;
                result  = entity.getPhone_number();
            } 
            return result;
    }

    @Override
    public Phone getAsObject(FacesContext context, UIComponent component, String uuid) {
       
       DualListModel<Phone> model = (DualListModel<Phone>) ((PickList) component).getValue();
      for (Phone entity : model.getSource()) {
          if (entity.getPhone_number().equals(uuid)) {
              return entity;
          }
      }
      for (Phone entity : model.getTarget()) {
          if (entity.getPhone_number().equals(uuid)) {
              return entity;
          }
      }
      return null;


    }

    /**
     *
     * Function retrieves the object by its hashCode.
     *
     * Because this has to be generic, typing is raw - and therefore disable IDE
     * warning.
     *
     *
     *
     * @param objects list of arbitrary objects
     *
     * @param uuid hashCode of the object to retrieve
     *
     * @return correct object with corresponding hashCode or null, if none found
     *
     */
    @SuppressWarnings("unchecked")
    private Object retrieveObject(final List objects, final String uuid) {

        return objects
                .stream()
                .filter(Objects::nonNull)
                .filter(obj -> uuid.equals(String.valueOf(obj.hashCode())))
                .findFirst()
                .orElse(null);

    }

}