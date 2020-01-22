/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.account.Contact;
import com.tatsinktechnologic.entities.account.Role;
import com.tatsinktechnologic.entities.account.User;
import com.tatsinktechnologic.entities.account.UserContactRel;
import com.tatsinktechnologic.entities.account.UserRoleRel;
import com.tatsinktechnologic.dao_controller.ContactJpaController;
import com.tatsinktechnologic.dao_controller.RoleJpaController;
import com.tatsinktechnologic.dao_controller.UserContactRelJpaController;
import com.tatsinktechnologic.dao_controller.UserJpaController;
import com.tatsinktechnologic.dao_controller.UserRoleRelJpaController;
import com.tatsinktechnologic.utils.CredentialEncrypter;
import com.tatsinktechnologic.utils.PasswordGenerators;
import com.tatsinktechnologic.utils.TwoTuple;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DualListModel;
import org.springframework.ui.context.Theme;

/**
 *
 * @author olivier.tatsinkou
 */
@Named
@ViewScoped
public class UserMB implements Serializable {

    private User selectedUser;
    private User selectedUser_cache;
    private DualListModel<Contact> dualListModelcontacts;
    private DualListModel<Role> dualListModelroles;
    private List<Contact> listContact;
    private List<Role> listRoles;
    private List<User> listUser;
    private String roles_string;
    private String contacts_string;
    private boolean do_create;
    private boolean do_view;
    private boolean do_edit;
    private boolean do_reset;

    private String user_name;
    private String curPass;
    private String newPass1;
    private String newPass2;
    private UIInput newPass1UI;

    @Inject
    private Commun_Controller commun_controller;

    @Inject
    private RoleJpaController role_controller;

    @Inject
    private UserJpaController user_controller;

    @Inject
    private ContactJpaController contact_controller;

    @Inject
    private UserRoleRelJpaController user_role_controller;

    @Inject
    private UserContactRelJpaController user_contact_controller;

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public User getSelectedUser_cache() {
        return selectedUser_cache;
    }

    public void setSelectedUser_cache(User selectedUser_cache) {
        this.selectedUser_cache = selectedUser_cache;
    }

    public List<User> getListUser() {
        return listUser;
    }

    public void setListUser(List<User> listUser) {
        this.listUser = listUser;
    }

    public String getRoles_string() {
        return roles_string;
    }

    public void setRoles_string(String roles_string) {
        this.roles_string = roles_string;
    }

    public String getContacts_string() {
        return contacts_string;
    }

    public void setContacts_string(String contacts_string) {
        this.contacts_string = contacts_string;
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

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getCurPass() {
        return curPass;
    }

    public void setCurPass(String curPass) {
        this.curPass = curPass;
    }

    public String getNewPass1() {
        return newPass1;
    }

    public void setNewPass1(String newPass1) {
        this.newPass1 = newPass1;
    }

    public String getNewPass2() {
        return newPass2;
    }

    public void setNewPass2(String newPass2) {
        this.newPass2 = newPass2;
    }

    public UIInput getNewPass1UI() {
        return newPass1UI;
    }

    public void setNewPass1UI(UIInput newPass1UI) {
        this.newPass1UI = newPass1UI;
    }

    public DualListModel<Contact> getDualListModelcontacts() {
        return dualListModelcontacts;
    }

    public void setDualListModelcontacts(DualListModel<Contact> dualListModelcontacts) {
        this.dualListModelcontacts = dualListModelcontacts;
    }

    public DualListModel<Role> getDualListModelroles() {
        return dualListModelroles;
    }

    public void setDualListModelroles(DualListModel<Role> dualListModelroles) {
        this.dualListModelroles = dualListModelroles;
    }

    public List<Contact> getListContact() {
        return listContact;
    }

    public void setListContact(List<Contact> listContact) {
        this.listContact = listContact;
    }

    public List<Role> getListRoles() {
        return listRoles;
    }

    public void setListRoles(List<Role> listRoles) {
        this.listRoles = listRoles;
    }

    @PostConstruct
    public void init() {
        List<Contact> contactSource = new ArrayList<Contact>();
        List<Contact> contactTarget = new ArrayList<Contact>();

        List<Role> roleSource = new ArrayList<Role>();
        List<Role> roleTarget = new ArrayList<Role>();

        listUser = user_controller.findUserEntities();

        listRoles = role_controller.findRoleEntities();
        listContact = contact_controller.findContactEntities();

        dualListModelcontacts = new DualListModel<Contact>(listContact, contactTarget);
        dualListModelroles = new DualListModel<Role>(listRoles, roleTarget);

        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        if (selectedUser != null) {
            roleTarget = commun_controller.getRolesByUsername(selectedUser.getUsername());
            for (Role rol : roleTarget) {
                if (roleSource.contains(rol)) {
                    dualListModelroles.getSource().remove(rol);
                }
            }
            dualListModelroles.setTarget(roleTarget);

            contactTarget = commun_controller.getContactByUsername(selectedUser.getUsername());
            for (Contact cont : contactTarget) {
                if (contactSource.contains(cont)) {
                    dualListModelcontacts.getSource().remove(cont);
                }
            }
            dualListModelcontacts.setTarget(contactTarget);
        } else {
            selectedUser = new User();
        }
    }

    public String getRoleUser(String user_name) {
        List<Role> listRol = commun_controller.getRolesByUsername(user_name);
        String result = "";
        for (Role role : listRol) {
            result = result + role.getRoleName() + "\n";
        }
        roles_string = result;
        return result;
    }

    public String getContactUser(String user_name) {
        List<Contact> listCont = commun_controller.getContactByUsername(user_name);
        String result = "";
        for (Contact cont : listCont) {
            result = result + cont.getContact_name() + "\n";
        }
        contacts_string = result;
        return result;
    }

    public void view(User user) {
        List<Contact> contactSource = new ArrayList<Contact>();
        List<Contact> contactTarget = new ArrayList<Contact>();

        List<Role> roleSource = new ArrayList<Role>();
        List<Role> roleTarget = new ArrayList<Role>();

        selectedUser = user;
        selectedUser_cache = SerializationUtils.clone(user);

        roleTarget = commun_controller.getRolesByUsername(selectedUser.getUsername());
        contactTarget = commun_controller.getContactByUsername(selectedUser.getUsername());

        for (Role role : listRoles) {
            if (!roleTarget.contains(role)) {
                roleSource.add(role);
            }
        }
        dualListModelroles = new DualListModel<Role>(roleSource, roleTarget);

        for (Contact cont : listContact) {
            if (!contactTarget.contains(cont)) {
                contactSource.add(cont);
            }
        }
        dualListModelcontacts = new DualListModel<Contact>(contactSource, contactTarget);

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
        selectedUser = selectedUser_cache;

    }

    public void clear() {

        List<Contact> contactTarget = new ArrayList<Contact>();

        List<Role> roleTarget = new ArrayList<Role>();

        dualListModelcontacts = new DualListModel<Contact>(listContact, contactTarget);
        dualListModelroles = new DualListModel<Role>(listRoles, roleTarget);

        selectedUser = new User();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public void edit() {
        FacesMessage msg = null;
        if (selectedUser != null) {
            String user_name = selectedUser.getUsername().trim();
            List<Role> listrole = commun_controller.getRolesByUsername(user_name);
            List<Contact> listcont = commun_controller.getContactByUsername(user_name);
            if (!user_name.equals("admin")) {
                if (listrole != null && !listrole.isEmpty()) {
                    try {
                        commun_controller.deleteUserRoleRelByUser(user_name);
                    } catch (Exception e) {
                        msg = new FacesMessage("ERROR during  delete Row", user_name);
                        e.printStackTrace();
                    }
                }

                if (listcont != null && !listcont.isEmpty()) {
                    try {
                        commun_controller.deleteUserContactRelByUser(user_name);
                    } catch (Exception e) {
                        msg = new FacesMessage("ERROR during  delete Row", user_name);
                        e.printStackTrace();
                    }
                }

                try {

                    if (selectedUser.isIsReset()) {
                        String passwod = PasswordGenerators.generateRandomPassword();
                        java.sql.Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
                        TwoTuple<String, String> hashAndSalt = CredentialEncrypter.saltedHash(passwod);
                        System.out.println("############################## new password is  = " + passwod);

                        selectedUser.setPassword(hashAndSalt.t1);
                        selectedUser.setSalt(hashAndSalt.t2);
                        selectedUser.setLast_update(date);
                    }

                    user_controller.edit(selectedUser);

                    List<Role> roleTarget = dualListModelroles.getTarget();

                    for (Role rol : roleTarget) {
                        UserRoleRel userRole = new UserRoleRel();
                        userRole.setRole(rol);
                        userRole.setUser(selectedUser);
                        user_role_controller.create(userRole);
                    }

                    List<Contact> contactTarget = dualListModelcontacts.getTarget();
                    for (Contact cont : contactTarget) {
                        UserContactRel userCont = new UserContactRel();
                        userCont.setContact(cont);
                        userCont.setUser(selectedUser);
                        user_contact_controller.create(userCont);
                    }
                    listUser = user_controller.findUserEntities();
                    msg = new FacesMessage("Success Update", user_name);
                } catch (Exception e) {
                    msg = new FacesMessage("ERROR during  updating Row", user_name);
                    e.printStackTrace();
                }
            } else {
                msg = new FacesMessage("you cannot edit this user", user_name);
            }
        } else {
            msg = new FacesMessage("ERROR : you must provide Role", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);

        List<Contact> contactTarget = new ArrayList<Contact>();

        List<Role> roleTarget = new ArrayList<Role>();

        dualListModelcontacts = new DualListModel<Contact>(listContact, contactTarget);
        dualListModelroles = new DualListModel<Role>(listRoles, roleTarget);

        selectedUser = new User();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public String delete() {
        FacesMessage msg = null;
        String user_name = selectedUser.getUsername().trim();
        List<Role> listrole = commun_controller.getRolesByUsername(user_name);
        List<Contact> listcont = commun_controller.getContactByUsername(user_name);

        if (listrole != null && !listrole.isEmpty()) {
            try {
                commun_controller.deleteUserRoleRelByUser(selectedUser.getUsername());
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  delete Row", user_name);
                e.printStackTrace();
            }
        }

        if (listcont != null && !listcont.isEmpty()) {
            try {
                commun_controller.deleteUserContactRelByUser(selectedUser.getUsername());
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  delete Row", user_name);
                e.printStackTrace();
            }
        }

        try {

            user_controller.destroy(selectedUser.getUser_id());

            listUser = user_controller.findUserEntities();
            msg = new FacesMessage("Success Delete", user_name);
        } catch (Exception e) {
            msg = new FacesMessage("ERROR during  Cancellation Row", user_name);
            e.printStackTrace();
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        List<Contact> contactTarget = new ArrayList<Contact>();
        List<Role> roleTarget = new ArrayList<Role>();

        dualListModelcontacts = new DualListModel<Contact>(listContact, contactTarget);
        dualListModelroles = new DualListModel<Role>(listRoles, roleTarget);
        selectedUser = new User();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }

    public String onAddNew() {
        // Add one new car to the table:
        FacesMessage msg = null;
        if (selectedUser != null) {
            String user_name = selectedUser.getUsername().trim();
            if (user_name == null || user_name.equals("")) {
                msg = new FacesMessage("You must provide User Name", "");
            } else {
                User user = commun_controller.getOneByUsername(user_name);
                if (user != null) {
                    msg = new FacesMessage("This User already exist", "");
                } else {
                    try {
                        String passwod = PasswordGenerators.generateRandomPassword();
                        java.sql.Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
                        TwoTuple<String, String> hashAndSalt = CredentialEncrypter.saltedHash(passwod);
                        System.out.println("############################## new password is  = " + passwod);
                        selectedUser.setIslock(true);
                        selectedUser.setIsReset(true);
                        selectedUser.setPassword(hashAndSalt.t1);
                        selectedUser.setSalt(hashAndSalt.t2);
                        selectedUser.setCreate_date(date);

                        user_controller.create(selectedUser);

                        commun_controller.deleteUserRoleRelByUser(selectedUser.getUsername());
                        commun_controller.deleteUserContactRelByUser(selectedUser.getUsername());

                        user = commun_controller.getOneByUsername(user_name);

                        List<Role> roleTarget = dualListModelroles.getTarget();

                        for (Role rol : roleTarget) {
                            UserRoleRel userRole = new UserRoleRel();
                            userRole.setRole(rol);
                            userRole.setUser(selectedUser);
                            user_role_controller.create(userRole);
                        }

                        List<Contact> contactTarget = dualListModelcontacts.getTarget();
                        for (Contact cont : contactTarget) {
                            UserContactRel userCont = new UserContactRel();
                            userCont.setContact(cont);
                            userCont.setUser(selectedUser);
                            user_contact_controller.create(userCont);
                        }

                        msg = new FacesMessage("New User added", user_name);
                        listUser = user_controller.findUserEntities();
                        selectedUser = new User();
                    } catch (Exception e) {
                        msg = new FacesMessage("ERROR during add row", user_name);
                        e.printStackTrace();
                    }

                }

            }
        } else {
            msg = new FacesMessage("ERROR : you must provide User", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);

        List<Contact> contactTarget = new ArrayList<Contact>();

        List<Role> roleTarget = new ArrayList<Role>();

        dualListModelcontacts = new DualListModel<Contact>(listContact, contactTarget);
        dualListModelroles = new DualListModel<Role>(listRoles, roleTarget);
        selectedUser = new User();

        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }

    public void validateNewPass(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        newPass1 = newPass1UI.getLocalValue().toString();
        newPass2 = value.toString();
        if (!newPass1.equals(newPass2)) {
            FacesMessage errorMessage = new FacesMessage("New passwords must match.");
            throw new ValidatorException(errorMessage);
        }
    }

    public void validateCurPass(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        curPass = value.toString();
        user_name = (String) component.getAttributes().get("login");

        User user = commun_controller.getOneByUsername(user_name);

        String curPassEncrypt = new Sha256Hash(curPass, Base64.decode(user.getSalt()), 1024).toBase64();

        if (!user.getPassword().equals(curPassEncrypt)) {
            System.out.println("encrypt current pass :" + curPass + "database value =" + user.getPassword());
            FacesMessage errorMessage = new FacesMessage("Current password is not correct.");
            throw new ValidatorException(errorMessage);
        }
    }

    public String changePwd() {
        FacesMessage msg = null;
        String page = "/web_site/index.xhtml?faces-redirect=true";

        User user = commun_controller.getOneByUsername(user_name);

        try {
            java.sql.Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
            TwoTuple<String, String> hashAndSalt = CredentialEncrypter.saltedHash(newPass1);

            user.setPassword(hashAndSalt.t1);
            user.setSalt(hashAndSalt.t2);
            user.setLast_update(date);

            user_controller.edit(user);
            msg = new FacesMessage("You are success Change Your Password", user.getUsername());
        } catch (Exception e) {
            msg = new FacesMessage("ERROR : You  Cannot Change Your Password", user.getUsername());
            e.printStackTrace();
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        user_name = null;
        return page;
    }

    public void onTransfer(TransferEvent event) {
        StringBuilder builder = new StringBuilder();
        for (Object item : event.getItems()) {
            builder.append(((Theme) item).getName()).append("<br />");
        }

        FacesMessage msg = new FacesMessage();
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        msg.setSummary("Items Transferred");
        msg.setDetail(builder.toString());

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onSelect(SelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Item Selected", ""));
    }

    public void onUnselect(UnselectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Item Unselected", ""));
    }

    public void onReorder() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "List Reordered", null));
    }
}
