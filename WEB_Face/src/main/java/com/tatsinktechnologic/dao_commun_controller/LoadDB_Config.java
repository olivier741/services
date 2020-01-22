/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_commun_controller;

import com.tatsinktechnologic.dao_controller.RoleJpaController;
import com.tatsinktechnologic.dao_controller.UserJpaController;
import com.tatsinktechnologic.dao_controller.UserRoleRelJpaController;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;
import javax.inject.Inject;

/**
 *
 * @author olivier.tatsinkou
 */
public class LoadDB_Config implements Serializable{
    
    private HashMap<String,Set<String>> SetRolesByRoleName= new HashMap<String,Set<String>>();
    
    @Inject
    private Commun_Controller commun_repo;
    
    @Inject
    private RoleJpaController role_controller;
    
    @Inject
    private UserJpaController user_controller;
    
    @Inject 
    private UserRoleRelJpaController userRole_controller;
    
    
}
