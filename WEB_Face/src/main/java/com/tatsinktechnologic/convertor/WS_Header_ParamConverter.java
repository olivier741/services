/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.convertor;

import com.tatsinktechnologic.entities.api_gateway.WS_Header_Param;
import java.util.List;
import java.util.Objects;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

/**
 *
 * @author olivier
 */
@FacesConverter(value = "headerParamPickListConverter")
public class WS_Header_ParamConverter implements Converter{
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object object) {
            String result = null;
            if (object == null || "".equals(object)) {
                    result = null;
            }

            if (object instanceof WS_Header_Param) {
                WS_Header_Param entity = (WS_Header_Param)object;
                result  = entity.getParam_key()+" | "+entity.getParam_value();
            } 
            return result;
    }

    @Override
    public WS_Header_Param getAsObject(FacesContext context, UIComponent component, String uuid) {
       
       DualListModel<WS_Header_Param> model = (DualListModel<WS_Header_Param>) ((PickList) component).getValue();
      for (WS_Header_Param entity : model.getSource()) {
          if ( uuid.equals(entity.getParam_key()+" | "+entity.getParam_value())) {
              return entity;
          }
      }
      for (WS_Header_Param entity : model.getTarget()) {
          if (uuid.equals(entity.getParam_key()+" | "+entity.getParam_value())) {
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
