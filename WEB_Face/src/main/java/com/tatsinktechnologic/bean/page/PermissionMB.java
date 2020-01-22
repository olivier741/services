/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.account.Permission;
import com.tatsinktechnologic.dao_controller.PermissionJpaController;
import java.io.Serializable;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author olivier.tatsinkou
 */
@Named
@ViewScoped
public class PermissionMB implements Serializable{
    
    
    @Inject
    private Commun_Controller commun_controller;
    
    @Inject
    private PermissionJpaController perm_controller;

   

    public List<Permission> getListPermission(){
        
        return perm_controller.findPermissionEntities();
    }
    
    
}
