/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.registration.Action;
import com.tatsinktechnologic.entities.registration.Command;
import com.tatsinktechnologic.entities.registration.Parameter;
import com.tatsinktechnologic.dao_controller.ActionJpaController;
import com.tatsinktechnologic.dao_controller.CommandJpaController;
import com.tatsinktechnologic.dao_controller.ParameterJpaController;
import java.io.Serializable;
import java.util.ArrayList;
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
public class CommandMB implements Serializable{
    private Command selectedCommand;
    private Command selectedCommand_cache;
    private List<Command> listCommand;
    private List<Parameter> listparameter;
    private Parameter selectedParameter;
    
    private List<Action> listaction;
    private Action selectedAction;
    

    private boolean do_create;
    private boolean do_view;
    private boolean do_edit;
    private boolean do_reset;
    
    @Inject
    private Commun_Controller commun_controller;

    @Inject
    private CommandJpaController command_controller;
    
    @Inject
    private ParameterJpaController parameter_controller;
    
    @Inject
    private ActionJpaController action_controller;

    public Command getSelectedCommand() {
        return selectedCommand;
    }

    public void setSelectedCommand(Command selectedCommand) {
        this.selectedCommand = selectedCommand;
    }

    public Command getSelectedCommand_cache() {
        return selectedCommand_cache;
    }

    public void setSelectedCommand_cache(Command selectedCommand_cache) {
        this.selectedCommand_cache = selectedCommand_cache;
    }

    public List<Command> getListCommand() {
        return listCommand;
    }

    public void setListCommand(List<Command> listCommand) {
        this.listCommand = listCommand;
    }

    public List<Parameter> getListparameter() {
        return listparameter;
    }

    public void setListparameter(List<Parameter> listparameter) {
        this.listparameter = listparameter;
    }

    public Parameter getSelectedParameter() {
        return selectedParameter;
    }

    public void setSelectedParameter(Parameter selectedParameter) {
        this.selectedParameter = selectedParameter;
    }

    public List<Action> getListaction() {
        return listaction;
    }

    public void setListaction(List<Action> listaction) {
        this.listaction = listaction;
    }

    public Action getSelectedAction() {
        return selectedAction;
    }

    public void setSelectedAction(Action selectedAction) {
        this.selectedAction = selectedAction;
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
        listCommand = command_controller.findCommandEntities();
        listaction = action_controller.findActionEntities();
        listparameter = parameter_controller.findParameterEntities();
        Parameter param = new Parameter();
        param.setParam_name("-- none --");
        
        if (listparameter!=null){
            listparameter.add(0,param);
        }else{
            listparameter = new ArrayList<Parameter>();
            listparameter.add(param);
        }
        
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        if (selectedCommand != null) {
            selectedAction= commun_controller.getActionByCommand(selectedCommand.getCommand_code());
            selectedParameter = commun_controller.getParameterByCommand(selectedCommand.getCommand_code());
        } else {
            selectedCommand = new Command();
        }
    }
    
   
    
     public void view(Command command) {
        selectedCommand = command;
        selectedCommand_cache = SerializationUtils.clone(command);
         selectedAction = selectedCommand.getAction();
        selectedParameter = selectedCommand.getParameter();
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
        
        selectedCommand = selectedCommand_cache;
        selectedAction = selectedCommand.getAction();
        selectedParameter = selectedCommand.getParameter();
        listCommand = command_controller.findCommandEntities();

    }

    public void clear() {
        selectedCommand = new Command();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

     
    public void edit() {
        FacesMessage msg = null;
        if (selectedCommand != null) {
            String command_name = selectedCommand.getCommand_name().trim();
            
            Action new_action=null;
            Parameter new_parameter = null;
            
              if (listaction != null && !listaction.isEmpty()) {
               if (selectedAction!=null){
                    String  action_name = selectedAction.getAction_name();
                    for (Action act: listaction){
                        if (act.getAction_name().equals(action_name.trim())){
                            new_action = act;
                            break;
                        }
                    }
               }
             
            }
            
            if (listparameter != null && !listparameter.isEmpty()) {
               if (selectedParameter!=null){
                   String param_name = selectedParameter.getParam_name();
                    for(Parameter param:listparameter){
                       if (param.getParam_name().equals(param_name.trim())){
                           new_parameter = param;
                           break;
                       }
                    }
               }
               
            }
            
            if (new_action!=null){
               selectedCommand.setAction(new_action);
           }
           
            
           if (new_parameter!=null && !new_parameter.getParam_name().equals("-- none --")){
               selectedCommand.setParameter(new_parameter);
           }else{
              selectedCommand.setParameter(null); 
           }

           
            try {
                command_controller.edit(selectedCommand);
                listCommand = command_controller.findCommandEntities();
                msg = new FacesMessage("Success Update", command_name);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  updating Row", command_name);
                e.printStackTrace();
            }

        } else {
            msg = new FacesMessage("ERROR : you must provide Contact", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedCommand = new Command();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public String delete() {
        FacesMessage msg = null;
       

        if (selectedCommand != null) {
             String command_code = selectedCommand.getCommand_name().trim();
             Parameter param = selectedCommand.getParameter();
             Action action = selectedCommand.getAction();
             
              boolean mustEdit = false;
        
            if (action != null ) {
               selectedCommand.setAction(null);
               mustEdit = true;
            }
            
             if (param !=null){
                selectedCommand.setParameter(null);
                mustEdit = true;
             }
             
             if (mustEdit){
                try {
                     command_controller.edit(selectedCommand);
                 } catch (Exception e) {
                     msg = new FacesMessage("ERROR during  delete Row", command_code);
                     e.printStackTrace();
                 }
             }

            try {
                command_controller.destroy(selectedCommand.getCommand_id());
                listCommand = command_controller.findCommandEntities();
                msg = new FacesMessage("Success Delete", command_code);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  Cancellation Row", command_code);
                e.printStackTrace();
            }
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedCommand = new Command();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }

    public String onAddNew() {
        // Add one new car to the table:
        FacesMessage msg = null;
        if (selectedCommand != null) {
            String command_code = selectedCommand.getCommand_name().trim();

            if (command_code == null || command_code.equals("")) {
                msg = new FacesMessage("You must provide Command Label", "");
            } else {
                        Action new_action=null;
                        Parameter new_parameter = null;

                          if (listaction != null && !listaction.isEmpty()) {
                           if (selectedAction!=null){
                                String  action_name = selectedAction.getAction_name();
                                for (Action act: listaction){
                                    if (act.getAction_name().equals(action_name.trim())){
                                        new_action = act;
                                        break;
                                    }
                                }
                           }

                        }

                        if (listparameter != null && !listparameter.isEmpty()) {
                           if (selectedParameter!=null){
                               String param_name = selectedParameter.getParam_name();
                                for(Parameter param:listparameter){
                                   if (param.getParam_name().equals(param_name.trim())){
                                       new_parameter = param;
                                       break;
                                   }
                                }
                           }

                        }

                        if (new_action!=null){
                           selectedCommand.setAction(new_action);
                       }


                       if (new_parameter!=null && !new_parameter.getParam_name().equals("-- none --")){
                           selectedCommand.setParameter(new_parameter);
                       }else{
                          selectedCommand.setParameter(null); 
                       }

                    
                    try {
                        command_controller.create(selectedCommand);

                        listCommand = command_controller.findCommandEntities();
                        msg = new FacesMessage("New Command added", command_code);
                    } catch (Exception e) {
                        msg = new FacesMessage("ERROR during add row", command_code);
                        e.printStackTrace();
                    }
                
            }
        } else {
            msg = new FacesMessage("ERROR : you must provide Command", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedCommand = new Command();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }
    
}
