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
import com.tatsinktechnologic.entities.account.Permission;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.util.List;
import java.util.Objects;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "permissionPickListConverter")
public class PermissionPicklistConverter implements Converter {

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object object) {
            String result = null;
            if (object == null || "".equals(object)) {
                    result = null;
            }

            if (object instanceof Permission) {
                Permission permission = (Permission)object;
                result  = permission.getPermissionStr();
            } 
            return result;
    }

    @Override
    public Permission getAsObject(FacesContext context, UIComponent component, String uuid) {
       
       DualListModel<Permission> model = (DualListModel<Permission>) ((PickList) component).getValue();
      for (Permission perm : model.getSource()) {
          if (perm.getPermissionStr().equals(uuid.replace("&#x2F;", "/"))) {
              return perm;
          }
      }
      for (Permission perm : model.getTarget()) {
          if (perm.getPermissionStr().equals(uuid.replace("&#x2F;", "/"))) {
              return perm;
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
