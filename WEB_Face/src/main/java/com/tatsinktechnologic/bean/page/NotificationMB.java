/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.registration.Command;
import com.tatsinktechnologic.entities.registration.Notification_Conf;
import com.tatsinktechnologic.dao_controller.CommandJpaController;
import com.tatsinktechnologic.dao_controller.Notification_ConfJpaController;
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
 * @author olivier.tatsinkou
 */
@Named
@ViewScoped
public class NotificationMB implements Serializable{
    private Notification_Conf selectedNotification_Conf;
    private Notification_Conf selectedNotification_Conf_cache;
    private List<Notification_Conf> listNotification_Conf;
    private List<Command> listcommand;
    private Command selectedCommand;
    

    private boolean do_create;
    private boolean do_view;
    private boolean do_edit;
    private boolean do_reset;
    
    @Inject
    private Commun_Controller commun_controller;

    @Inject
    private CommandJpaController command_controller;
    
    @Inject
    private Notification_ConfJpaController notification_controller;

    public Notification_Conf getSelectedNotification_Conf() {
        return selectedNotification_Conf;
    }

    public void setSelectedNotification_Conf(Notification_Conf selectedNotification_Conf) {
        this.selectedNotification_Conf = selectedNotification_Conf;
    }

    public Notification_Conf getSelectedNotification_Conf_cache() {
        return selectedNotification_Conf_cache;
    }

    public void setSelectedNotification_Conf_cache(Notification_Conf selectedNotification_Conf_cache) {
        this.selectedNotification_Conf_cache = selectedNotification_Conf_cache;
    }

    public List<Notification_Conf> getListNotification_Conf() {
        return listNotification_Conf;
    }

    public void setListNotification_Conf(List<Notification_Conf> listNotification_Conf) {
        this.listNotification_Conf = listNotification_Conf;
    }

    public List<Command> getListcommand() {
        return listcommand;
    }

    public void setListcommand(List<Command> listcommand) {
        this.listcommand = listcommand;
    }

    public Command getSelectedCommand() {
        return selectedCommand;
    }

    public void setSelectedCommand(Command selectedCommand) {
        this.selectedCommand = selectedCommand;
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
        listNotification_Conf= notification_controller.findNotification_ConfEntities();
        listcommand = command_controller.findCommandEntities();
        Command comd = new Command();
        comd.setCommand_code("-- none --");
        
        if (listcommand!=null){
            listcommand.add(0,comd);
        }else{
            listcommand = new ArrayList<Command>();
            listcommand.add(comd);
        }
        
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        if (selectedNotification_Conf!= null) {
            selectedCommand = commun_controller.getCommandByNotification_Conf(selectedNotification_Conf.getNofication_name());
        } else {
            selectedNotification_Conf = new Notification_Conf();
        }
    }
    
    
      public void view(Notification_Conf noification) {
        selectedNotification_Conf = noification;
        selectedNotification_Conf_cache = SerializationUtils.clone(noification);
        selectedCommand = selectedNotification_Conf.getCommand();
        
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
        
        selectedNotification_Conf = selectedNotification_Conf_cache;
        selectedCommand = selectedNotification_Conf.getCommand();
        listNotification_Conf = notification_controller.findNotification_ConfEntities();

    }

    public void clear() {
        selectedNotification_Conf = new Notification_Conf();
        
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

   public void edit() {
        FacesMessage msg = null;
        if (selectedNotification_Conf != null) {
            String notif_name = selectedNotification_Conf.getNofication_name().trim();
            
            Command new_command=null;

            
              if (listcommand!= null && !listcommand.isEmpty()) {
               if (selectedCommand!=null){
                    String  command_name = selectedCommand.getCommand_code();
                    for (Command cmd: listcommand){
                        if (cmd.getCommand_code().equals(command_name.trim())){
                            new_command = cmd;
                            break;
                        }
                    }
               }
             
            }
            
        
           if (new_command!=null && !new_command.getCommand_code().equals("-- none --")){
               selectedNotification_Conf.setCommand(new_command);
           }else{
              selectedNotification_Conf.setCommand(null); 
           }

           
            try {
                notification_controller.edit(selectedNotification_Conf);
                listNotification_Conf= notification_controller.findNotification_ConfEntities();
                msg = new FacesMessage("Success Update", notif_name);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  updating Row", notif_name);
                e.printStackTrace();
            }

        } else {
            msg = new FacesMessage("ERROR : you must provide Notification Name", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedNotification_Conf = new Notification_Conf();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public String delete() {
        FacesMessage msg = null;
       
        if (selectedNotification_Conf != null) {
             String notif_name = selectedNotification_Conf.getNofication_name().trim();
             
             Command comd = selectedNotification_Conf.getCommand();
             
              boolean mustEdit = false;
        
            if (comd != null ) {
               selectedNotification_Conf.setCommand(null);
                try {
                     notification_controller.edit(selectedNotification_Conf);
                } catch (Exception e) {
                    msg = new FacesMessage("ERROR during  delete Row", notif_name);
                    e.printStackTrace();
                }
            }
           
            try {
                notification_controller.destroy(selectedNotification_Conf.getConfig_id());
                listNotification_Conf= notification_controller.findNotification_ConfEntities();
                msg = new FacesMessage("Success Delete", notif_name);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  Cancellation Row", notif_name);
                e.printStackTrace();
            }
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedNotification_Conf = new Notification_Conf();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }

    public String onAddNew() {
        // Add one new car to the table:
        FacesMessage msg = null;
        if (selectedNotification_Conf != null) {
           String notif_name = selectedNotification_Conf.getNofication_name().trim();

            if (notif_name == null || notif_name.equals("")) {
                msg = new FacesMessage("You must provide Command Label", "");
            } else {
                Notification_Conf nofification = commun_controller.getOneByNotification_Conf(notif_name);
                if (nofification != null) {
                    msg = new FacesMessage("This Command already exist", "");
                } else {
                    
                        Command new_command=null;

                        if (listcommand!= null && !listcommand.isEmpty()) {
                         if (selectedCommand!=null){
                              String  command_name = selectedCommand.getCommand_code();
                              for (Command cmd: listcommand){
                                  if (cmd.getCommand_code().equals(command_name.trim())){
                                      new_command = cmd;
                                      break;
                                  }
                              }
                         }

                      }


                     if (new_command!=null && !new_command.getCommand_code().equals("-- none --")){
                         selectedNotification_Conf.setCommand(new_command);
                     }else{
                        selectedNotification_Conf.setCommand(null); 
                     }

                    
                    try {
                        notification_controller.create(selectedNotification_Conf);

                        listNotification_Conf= notification_controller.findNotification_ConfEntities();
                        msg = new FacesMessage("New Command added", notif_name);
                    } catch (Exception e) {
                        msg = new FacesMessage("ERROR during add row", notif_name);
                        e.printStackTrace();
                    }
                }
            }
        } else {
            msg = new FacesMessage("ERROR : you must provide Command", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedNotification_Conf = new Notification_Conf();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }  
}
