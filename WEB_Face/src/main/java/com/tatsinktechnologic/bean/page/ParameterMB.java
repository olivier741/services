/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.registration.Parameter;
import com.tatsinktechnologic.dao_controller.ParameterJpaController;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.SerializationUtils;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author olivier
 */
@Named
@ViewScoped
public class ParameterMB implements Serializable{
    
    private Parameter selectedParameter;
    private Parameter selectedParameter_cache;
    private List<Parameter> listParameter;
    private boolean do_create;
    private boolean do_view;
    private boolean do_edit;
    private boolean do_reset;
    
    @Inject
    private Commun_Controller commun_controller;

    @Inject
    private ParameterJpaController parameter_controller;

    public Parameter getSelectedParameter() {
        return selectedParameter;
    }

    public void setSelectedParameter(Parameter selectedParameter) {
        this.selectedParameter = selectedParameter;
    }

    public Parameter getSelectedParameter_cache() {
        return selectedParameter_cache;
    }

    public void setSelectedParameter_cache(Parameter selectedParameter_cache) {
        this.selectedParameter_cache = selectedParameter_cache;
    }

    public List<Parameter> getListParameter() {
        return listParameter;
    }

    public void setListParameter(List<Parameter> listParameter) {
        this.listParameter = listParameter;
    }

    public boolean isDo_create() {
        return do_create;
    }

    public void setDo_create(boolean do_create) {
        this.do_create = do_create;
    }

    public boolean isDo_view() {
        return do_view;
    }

    public void setDo_view(boolean do_view) {
        this.do_view = do_view;
    }

    public boolean isDo_edit() {
        return do_edit;
    }

    public void setDo_edit(boolean do_edit) {
        this.do_edit = do_edit;
    }

    public boolean isDo_reset() {
        return do_reset;
    }

    public void setDo_reset(boolean do_reset) {
        this.do_reset = do_reset;
    }
    
      @PostConstruct
    public void init() {
        listParameter = parameter_controller.findParameterEntities();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        if (selectedParameter == null) {      
            selectedParameter= new Parameter();
        }
    }
    
    
     public void view(Parameter parameter) {
        selectedParameter = parameter;
        selectedParameter_cache = SerializationUtils.clone(parameter);
         do_create = false;
        do_view = true;
        do_edit = false;
        do_reset = true;
    }

    public void enableEdit() {
        do_create = false;
        do_view = false;
        do_edit = true;
        do_reset = true;
        selectedParameter = selectedParameter_cache;
        listParameter = parameter_controller.findParameterEntities();

    }

  

    public void clear() {

        selectedParameter = new Parameter();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public void edit() {
        FacesMessage msg = null;
        if (selectedParameter != null) {
            String param_name = selectedParameter.getParam_name().trim();
                   
                    try {
                        parameter_controller.edit(selectedParameter);
                     
                         listParameter = parameter_controller.findParameterEntities();
                        msg = new FacesMessage("Success Update", param_name);
                    } catch (Exception e) {
                        msg = new FacesMessage("ERROR during  updating Row", param_name);
                        e.printStackTrace();
                    }
            
        } else {
            msg = new FacesMessage("ERROR : you must provide Parameter", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedParameter= new Parameter();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public String delete() {
        FacesMessage msg = null;
        String param_name = selectedParameter.getParam_name().trim();
 
        try {
            parameter_controller.destroy(selectedParameter.getParameter_id());

            listParameter = parameter_controller.findParameterEntities();
            msg = new FacesMessage("Success Delete", param_name);
        } catch (Exception e) {
            msg = new FacesMessage("ERROR during  Cancellation Row", param_name);
            e.printStackTrace();
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedParameter= new Parameter();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }

    public String onAddNew() {
        // Add one new car to the table:
        FacesMessage msg = null;
        if (selectedParameter != null) {
            String param_name = selectedParameter.getParam_name().trim();
            if (param_name == null || param_name.equals("")) {
                msg = new FacesMessage("You must provide Service Name", "");
            } else {
                Parameter param = commun_controller.getOneByParameter(param_name);
                if (param != null) {
                    msg = new FacesMessage("This Serivice already exist", "");
                } else {
                    try {
                        parameter_controller.create(selectedParameter);

                        msg = new FacesMessage("New Service added", param_name);
                        listParameter = parameter_controller.findParameterEntities();
                        selectedParameter = new Parameter();
                    } catch (Exception e) {
                        msg = new FacesMessage("ERROR during add row", param_name);
                        e.printStackTrace();
                    }

                }

            }
        } else {
            msg = new FacesMessage("ERROR : you must provide Parameter", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedParameter= new Parameter();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }
    
}
