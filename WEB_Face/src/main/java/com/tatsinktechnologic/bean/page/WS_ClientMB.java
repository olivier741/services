/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.dao_controller.WS_ClientJpaController;
import com.tatsinktechnologic.entities.api_gateway.WS_Client;
import com.tatsinktechnologic.utils.CredentialEncrypter;
import com.tatsinktechnologic.utils.TwoTuple;
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
public class WS_ClientMB implements Serializable {

    private WS_Client selectedWS_Client;
    private WS_Client selectedWS_Client_cache;
    private List<WS_Client> listWS_Client;

    private boolean do_create;
    private boolean do_view;
    private boolean do_edit;
    private boolean do_reset;

    @Inject
    private Commun_Controller commun_controller;

    @Inject
    private WS_ClientJpaController ws_client_controller;

    public WS_Client getSelectedWS_Client() {
        return selectedWS_Client;
    }

    public void setSelectedWS_Client(WS_Client selectedWS_Client) {
        this.selectedWS_Client = selectedWS_Client;
    }

    public WS_Client getSelectedWS_Client_cache() {
        return selectedWS_Client_cache;
    }

    public void setSelectedWS_Client_cache(WS_Client selectedWS_Client_cache) {
        this.selectedWS_Client_cache = selectedWS_Client_cache;
    }

    public List<WS_Client> getListWS_Client() {
        return listWS_Client;
    }

    public void setListWS_Client(List<WS_Client> listWS_Client) {
        this.listWS_Client = listWS_Client;
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
        listWS_Client = ws_client_controller.findWS_ClientEntities();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        if (selectedWS_Client == null) {
            selectedWS_Client = new WS_Client();
        }
    }

    public void view(WS_Client ws_client) {
        selectedWS_Client = ws_client;
        selectedWS_Client_cache = SerializationUtils.clone(ws_client);
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
        selectedWS_Client_cache.setLogin("");
        selectedWS_Client_cache.setPassword("");
        selectedWS_Client = selectedWS_Client_cache;
        listWS_Client = ws_client_controller.findWS_ClientEntities();

    }

    public void clear() {

        selectedWS_Client = new WS_Client();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public void edit() {
        FacesMessage msg = null;
        if (selectedWS_Client != null) {
            String client_name = selectedWS_Client.getClient_name().trim();

            try {

                String passwod = selectedWS_Client.getPassword();
                String login = selectedWS_Client.getLogin();
                TwoTuple<String, String> pass_hashAndSalt = CredentialEncrypter.saltedHash(passwod);
                TwoTuple<String, String> login_hashAndSalt = CredentialEncrypter.saltedHash(login);

                selectedWS_Client.setPassword(pass_hashAndSalt.t1);
                selectedWS_Client.setPassword_salt(pass_hashAndSalt.t2);

                selectedWS_Client.setLogin(login_hashAndSalt.t1);
                selectedWS_Client.setLogin_salt(login_hashAndSalt.t2);

                ws_client_controller.edit(selectedWS_Client);

                listWS_Client = ws_client_controller.findWS_ClientEntities();
                msg = new FacesMessage("Success Update", client_name);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  updating Row", client_name);
                e.printStackTrace();
            }

        } else {
            msg = new FacesMessage("ERROR : you must provide Role", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedWS_Client = new WS_Client();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public String delete() {
        FacesMessage msg = null;
        String client_name = selectedWS_Client.getClient_name().trim();

        try {
            ws_client_controller.destroy(selectedWS_Client.getWs_client_id());

            listWS_Client = ws_client_controller.findWS_ClientEntities();
            msg = new FacesMessage("Success Delete", client_name);
        } catch (Exception e) {
            msg = new FacesMessage("ERROR during  Cancellation Row", client_name);
            e.printStackTrace();
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedWS_Client = new WS_Client();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }

    public String onAddNew() {

        FacesMessage msg = null;
        if (selectedWS_Client != null) {
            String client_name = selectedWS_Client.getClient_name().trim();
            if (client_name == null || client_name.equals("")) {
                msg = new FacesMessage("You must provide CLient Name", "");
            } else {
                WS_Client ws_client = commun_controller.getOneByWS_Client(client_name);
                if (ws_client != null) {
                    msg = new FacesMessage("This Serivice already exist", "");
                } else {
                    try {

                        String passwod = selectedWS_Client.getPassword();
                        String login = selectedWS_Client.getLogin();
                        TwoTuple<String, String> pass_hashAndSalt = CredentialEncrypter.saltedHash(passwod);
                        TwoTuple<String, String> login_hashAndSalt = CredentialEncrypter.saltedHash(login);

                        selectedWS_Client.setPassword(pass_hashAndSalt.t1);
                        selectedWS_Client.setPassword_salt(pass_hashAndSalt.t2);

                        selectedWS_Client.setLogin(login_hashAndSalt.t1);
                        selectedWS_Client.setLogin_salt(login_hashAndSalt.t2);

                        ws_client_controller.create(selectedWS_Client);

                        msg = new FacesMessage("New ws_client added", client_name);
                        listWS_Client = ws_client_controller.findWS_ClientEntities();
                        selectedWS_Client = new WS_Client();
                    } catch (Exception e) {
                        msg = new FacesMessage("ERROR during add row", client_name);
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
