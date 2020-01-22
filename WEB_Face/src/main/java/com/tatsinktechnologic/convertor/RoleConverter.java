/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.convertor;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.account.Role;
import java.util.List;
import java.util.Objects;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

/**
 *
 * @author olivier
 */
@FacesConverter(value = "rolePickListConverter")
public class RoleConverter implements Converter{
    
    
    @Inject
    private Commun_Controller commun_controller;
    
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object object) {
            String result = null;
            if (object == null || "".equals(object)) {
                    result = null;
            }

            if (object instanceof Role) {
                Role role = (Role)object;
                result  = role.getRoleName();
            } 
            return result;
    }

    @Override
    public Role getAsObject(FacesContext context, UIComponent component, String uuid) {
       
       DualListModel<Role> model = (DualListModel<Role>) ((PickList) component).getValue();
      for (Role entity : model.getSource()) {
          if (entity.getRoleName().equals(uuid)) {
              return entity;
          }
      }
      for (Role entity : model.getTarget()) {
          if (entity.getRoleName().equals(uuid)) {
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
