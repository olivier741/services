/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.dao_controller.ChatGroupJpaController;
import com.tatsinktechnologic.dao_controller.UserJpaController;
import com.tatsinktechnologic.entities.account.User;
import com.tatsinktechnologic.entities.chat_sms.ChatGroup;
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
 * @author olivier.tatsinkou
 */
@Named
@ViewScoped
public class ChatGroupMB implements Serializable {

    private ChatGroup selectedChatGroup;
    private ChatGroup selectedChatGroup_cache;
    private List<ChatGroup> listChatGroup;
    private List<User> listUser;
    private User selectUser;

    private boolean do_create;
    private boolean do_view;
    private boolean do_edit;
    private boolean do_reset;

    @Inject
    private Commun_Controller commun_controller;

    @Inject
    private ChatGroupJpaController chatGrp_controller;

    @Inject
    private UserJpaController user_controller;

    public ChatGroup getSelectedChatGroup() {
        return selectedChatGroup;
    }

    public void setSelectedChatGroup(ChatGroup selectedChatGroup) {
        this.selectedChatGroup = selectedChatGroup;
    }

    public ChatGroup getSelectedChatGroup_cache() {
        return selectedChatGroup_cache;
    }

    public void setSelectedChatGroup_cache(ChatGroup selectedChatGroup_cache) {
        this.selectedChatGroup_cache = selectedChatGroup_cache;
    }

    public List<ChatGroup> getListChatGroup() {
        return listChatGroup;
    }

    public void setListChatGroup(List<ChatGroup> listChatGroup) {
        this.listChatGroup = listChatGroup;
    }

    public List<User> getListUser() {
        return listUser;
    }

    public void setListUser(List<User> listUser) {
        this.listUser = listUser;
    }

    public User getSelectUser() {
        return selectUser;
    }

    public void setSelectUser(User selectUser) {
        this.selectUser = selectUser;
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
        listChatGroup = chatGrp_controller.findChatGroupEntities();
        listUser = user_controller.findUserEntities();

        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        if (selectedChatGroup != null) {
            selectUser = commun_controller.getUserByChatGroup(selectedChatGroup.getGroup_channel());
        } else {
            selectedChatGroup = new ChatGroup();
        }
    }

    public void view(ChatGroup chatGrp) {
        selectedChatGroup = chatGrp;
        selectedChatGroup_cache = SerializationUtils.clone(chatGrp);
        selectUser = selectedChatGroup.getUser();

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
        selectedChatGroup = selectedChatGroup_cache;
        selectUser = selectedChatGroup.getUser();
        listChatGroup = chatGrp_controller.findChatGroupEntities();

    }

    public void clear() {
        selectedChatGroup = new ChatGroup();

        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public void edit() {
        FacesMessage msg = null;
        if (selectedChatGroup != null) {
            String chatgrp_channel = selectedChatGroup.getGroup_channel().trim();
            User new_user = null;

            if (listUser != null && !listUser.isEmpty()) {
                if (selectUser != null) {
                    String user_name = selectUser.getUsername();
                    for (User user : listUser) {
                        if (user.getUsername().equals(user_name.trim())) {
                            new_user = user;
                            break;
                        }
                    }
                }

            }

            if (new_user != null) {
                selectedChatGroup.setUser(new_user);
            }

            try {

                chatGrp_controller.edit(selectedChatGroup);
                listChatGroup = chatGrp_controller.findChatGroupEntities();
                msg = new FacesMessage("Success Update", chatgrp_channel);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  updating Row", chatgrp_channel);
                e.printStackTrace();
            }
        } else {
            msg = new FacesMessage("ERROR : you must provide Product", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedChatGroup = new ChatGroup();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public String delete() {
        FacesMessage msg = null;
        String chatgrp_channel = selectedChatGroup.getGroup_channel().trim();
        User user = selectedChatGroup.getUser();

        boolean mustEdit = false;

        if (user != null) {
            selectedChatGroup.setUser(null);
            mustEdit = true;
        }

        if (mustEdit) {
            try {
                chatGrp_controller.edit(selectedChatGroup);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  delete Row", chatgrp_channel);
                e.printStackTrace();
            }
        }

        try {
            chatGrp_controller.destroy(selectedChatGroup.getChatgroup_id());
            listChatGroup = chatGrp_controller.findChatGroupEntities();
            msg = new FacesMessage("Success Delete", chatgrp_channel);
        } catch (Exception e) {
            msg = new FacesMessage("ERROR during  Cancellation Row", chatgrp_channel);
            e.printStackTrace();
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedChatGroup = new ChatGroup();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

        return null;
    }

    public String onAddNew() {
        // Add one new car to the table:
        FacesMessage msg = null;
        if (selectedChatGroup != null) {
            String chatgrp_channel = selectedChatGroup.getGroup_channel().trim();

            if (chatgrp_channel == null || chatgrp_channel.equals("")) {
                msg = new FacesMessage("You must provide CHANNEL", "");
            } else {
                ChatGroup chatGrp = commun_controller.getOneByChatGroup(chatgrp_channel);
                if (chatGrp != null) {
                    msg = new FacesMessage("This Product already exist", "");
                } else {

                    User new_user = null;

                    if (listUser != null && !listUser.isEmpty()) {
                        if (selectUser != null) {
                            String user_name = selectUser.getUsername();
                            for (User user : listUser) {
                                if (user.getUsername().equals(user_name.trim())) {
                                    new_user = user;
                                    break;
                                }
                            }
                        }
                    }

                    if (new_user != null) {
                        selectedChatGroup.setUser(new_user);
                    }

                    try {

                        chatGrp_controller.create(selectedChatGroup);
                        listChatGroup = chatGrp_controller.findChatGroupEntities();

                        msg = new FacesMessage("Success Create", chatgrp_channel);
                    } catch (Exception e) {
                        msg = new FacesMessage("ERROR during  Create new Row", chatgrp_channel);
                        e.printStackTrace();
                    }
                }
            }
        } else {
            msg = new FacesMessage("ERROR : you must provide Product Code", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedChatGroup = new ChatGroup();

        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

        return null;
    }

}
