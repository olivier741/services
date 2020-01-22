/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.registration.Service;
import com.tatsinktechnologic.dao_controller.ServiceJpaController;
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
public class ServiceMB implements Serializable{
    
    private Service selectedService;
    private Service selectedService_cache;
    private List<Service> filteredServices;
    private List<Service> listService;
    private boolean do_create;
    private boolean do_view;
    private boolean do_edit;
    private boolean do_reset;
    
    @Inject
    private Commun_Controller commun_controller;

    @Inject
    private ServiceJpaController service_controller;

    public Service getSelectedService() {
        return selectedService;
    }

    public void setSelectedService(Service selectedService) {
        this.selectedService = selectedService;
    }

    public Service getSelectedService_cache() {
        return selectedService_cache;
    }

    public void setSelectedService_cache(Service selectedService_cache) {
        this.selectedService_cache = selectedService_cache;
    }

    public List<Service> getFilteredServices() {
        return filteredServices;
    }

    public void setFilteredServices(List<Service> filteredServices) {
        this.filteredServices = filteredServices;
    }

    public List<Service> getListService() {
        return listService;
    }

    public void setListService(List<Service> listService) {
        this.listService = listService;
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
        listService = service_controller.findServiceEntities();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        if (selectedService == null) {      
            selectedService = new Service();
        }
    }
    
    
     public void view(Service service) {
        selectedService = service;
        selectedService_cache = SerializationUtils.clone(service);
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
        selectedService = selectedService_cache;
        listService = service_controller.findServiceEntities();

    }

  

    public void clear() {

        selectedService = new Service();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public void edit() {
        FacesMessage msg = null;
        if (selectedService != null) {
            String service_name = selectedService.getService_name().trim();
                   
                    try {
                        service_controller.edit(selectedService);
                     
                         listService = service_controller.findServiceEntities();
                        msg = new FacesMessage("Success Update", service_name);
                        selectedService= new Service();
                    } catch (Exception e) {
                        msg = new FacesMessage("ERROR during  updating Row", service_name);
                        e.printStackTrace();
                    }
            
        } else {
            msg = new FacesMessage("ERROR : you must provide Role", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public String delete() {
        FacesMessage msg = null;
        String service_name = selectedService.getService_name().trim();
 
        try {
            service_controller.destroy(selectedService.getService_id());

            listService = service_controller.findServiceEntities();
            msg = new FacesMessage("Success Delete", service_name);
        } catch (Exception e) {
            msg = new FacesMessage("ERROR during  Cancellation Row", service_name);
            e.printStackTrace();
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedService = new Service();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }

    public String onAddNew() {
        // Add one new car to the table:
        FacesMessage msg = null;
        if (selectedService != null) {
            String service_name = selectedService.getService_name().trim();
            if (service_name == null || service_name.equals("")) {
                msg = new FacesMessage("You must provide Service Name", "");
            } else {
                Service serivice = commun_controller.getOneByService(service_name);
                if (serivice != null) {
                    msg = new FacesMessage("This Serivice already exist", "");
                } else {
                    try {
                        service_controller.create(selectedService);

                        msg = new FacesMessage("New Service added", service_name);
                        listService = service_controller.findServiceEntities();
                        selectedService = new Service();
                    } catch (Exception e) {
                        msg = new FacesMessage("ERROR during add row", service_name);
                        e.printStackTrace();
                    }

                }

            }
        } else {
            msg = new FacesMessage("ERROR : you must provide User", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }
}
