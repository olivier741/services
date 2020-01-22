/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.account.Contact;
import com.tatsinktechnologic.entities.account.Email;
import com.tatsinktechnologic.entities.account.Phone;
import com.tatsinktechnologic.dao_controller.ContactJpaController;
import com.tatsinktechnologic.dao_controller.EmailJpaController;
import com.tatsinktechnologic.dao_controller.PhoneJpaController;
import com.tatsinktechnologic.dao_controller.UserContactRelJpaController;
import com.tatsinktechnologic.dao_controller.UserJpaController;
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
public class ContactMB implements Serializable {

    private Contact selectedContact;
    private Contact selectedContact_cache;
    private List<Contact> listContact;
    private DualListModel<Phone> dualListModelphones;
    private DualListModel<Email> dualListModelemails;
    private String allEmail;
    private String allPhone;
    private Phone phone_number;
    private Email email;
    private boolean do_create;
    private boolean do_view;
    private boolean do_edit;
    private boolean do_reset;

    @Inject
    private Commun_Controller commun_controller;

    @Inject
    private UserJpaController user_controller;

    @Inject
    private ContactJpaController contact_controller;

    @Inject
    private PhoneJpaController phone_controller;

    @Inject
    private EmailJpaController email_controller;

    @Inject
    private UserContactRelJpaController user_contact_controller;

    public Contact getSelectedContact() {
        return selectedContact;
    }

    public void setSelectedContact(Contact selectedContact) {
        this.selectedContact = selectedContact;
    }

    public Contact getSelectedContact_cache() {
        return selectedContact_cache;
    }

    public void setSelectedContact_cache(Contact selectedContact_cache) {
        this.selectedContact_cache = selectedContact_cache;
    }

  

    public List<Contact> getListContact() {
        return listContact;
    }

    public void setListContact(List<Contact> listContact) {
        this.listContact = listContact;
    }

   
    public String getAllEmail() {
        return allEmail;
    }

    public void setAllEmail(String allEmail) {
        this.allEmail = allEmail;
    }

    public String getAllPhone() {
        return allPhone;
    }

    public void setAllPhone(String allPhone) {
        this.allPhone = allPhone;
    }

    public Phone getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(Phone phone_number) {
        this.phone_number = phone_number;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
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

    public Commun_Controller getCommun_controller() {
        return commun_controller;
    }

    public void setCommun_controller(Commun_Controller commun_controller) {
        this.commun_controller = commun_controller;
    }

    public UserJpaController getUser_controller() {
        return user_controller;
    }

    public void setUser_controller(UserJpaController user_controller) {
        this.user_controller = user_controller;
    }

    public ContactJpaController getContact_controller() {
        return contact_controller;
    }

    public void setContact_controller(ContactJpaController contact_controller) {
        this.contact_controller = contact_controller;
    }

    public PhoneJpaController getPhone_controller() {
        return phone_controller;
    }

    public void setPhone_controller(PhoneJpaController phone_controller) {
        this.phone_controller = phone_controller;
    }

    public EmailJpaController getEmail_controller() {
        return email_controller;
    }

    public void setEmail_controller(EmailJpaController email_controller) {
        this.email_controller = email_controller;
    }

    public UserContactRelJpaController getUser_contact_controller() {
        return user_contact_controller;
    }

    public void setUser_contact_controller(UserContactRelJpaController user_contact_controller) {
        this.user_contact_controller = user_contact_controller;
    }

    public DualListModel<Phone> getDualListModelphones() {
        return dualListModelphones;
    }

    public void setDualListModelphones(DualListModel<Phone> dualListModelphones) {
        this.dualListModelphones = dualListModelphones;
    }

    public DualListModel<Email> getDualListModelemails() {
        return dualListModelemails;
    }

    public void setDualListModelemails(DualListModel<Email> dualListModelemails) {
        this.dualListModelemails = dualListModelemails;
    }
    
    

    @PostConstruct
    public void init() {
        List<Phone> phoneSource = new ArrayList<Phone>();
        List<Phone> phoneTarget = new ArrayList<Phone>();
        
        List<Email> emailSource = new ArrayList<Email>();
        List<Email> emailTarget = new ArrayList<Email>();
        
        listContact = contact_controller.findContactEntities();
        
        dualListModelphones = new DualListModel<Phone>(phoneSource, phoneTarget);
        dualListModelemails = new DualListModel<Email>(emailSource, emailTarget);

        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        if (selectedContact != null) {
            phoneTarget = commun_controller.getPhoneByContact(selectedContact.getContact_name());
            emailTarget = commun_controller.getEmailByContact(selectedContact.getContact_name());
            
          
            dualListModelphones.setTarget(phoneTarget);
            dualListModelemails.setTarget(emailTarget);
            
        } else {
            selectedContact = new Contact();
        }
        phone_number = new Phone();
        email = new Email();
    }

    public String getPhonebyContact(String contact_name) {
        List<Phone> listphone = commun_controller.getPhoneByContact(contact_name);
        String result = "";
        for (Phone phone : listphone) {
            result = result + phone.getPhone_number() + "\n";
        }
        allPhone = result;
        return result;
    }

    public String getEmailbyContact(String contact_name) {
        List<Email> listphone = commun_controller.getEmailByContact(contact_name);
        String result = "";
        for (Email email : listphone) {
            result = result + email.getEmail() + "\n";
        }
        allEmail = result;
        return result;
    }

    public void view(Contact contact) {
        List<Phone> phoneTarget = new ArrayList<Phone>();
        
        List<Email> emailTarget = new ArrayList<Email>();
        
        selectedContact = contact;
        selectedContact_cache = SerializationUtils.clone(contact);
        phoneTarget = commun_controller.getPhoneByContact(selectedContact.getContact_name());
        emailTarget = commun_controller.getEmailByContact(selectedContact.getContact_name());
        
       
        dualListModelphones.setTarget(phoneTarget);

        dualListModelemails.setTarget(emailTarget);

        do_create = false;
        do_view = true;
        do_edit = false;
        do_reset = true;
        phone_number = new Phone();
        email = new Email();
    }

    public void enableEdit() {
        do_create = false;
        do_view = false;
        do_edit = true;
        do_reset = true;
        selectedContact = selectedContact_cache;

    }

    public void clear() {
        List<Phone> phoneSource = new ArrayList<Phone>();
        List<Phone> phoneTarget = new ArrayList<Phone>();
        
        List<Email> emailSource = new ArrayList<Email>();
        List<Email> emailTarget = new ArrayList<Email>();
        
        dualListModelphones = new DualListModel<Phone>(phoneSource, phoneTarget);
        dualListModelemails = new DualListModel<Email>(emailSource, emailTarget);
        
        selectedContact = new Contact();
        phone_number = new Phone();
        email = new Email();

        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public void addPhone() {
        FacesMessage msg = null;
        if (phone_number != null && phone_number.getPhone_number() != null && !phone_number.getPhone_number().trim().equals("")) {
            List<Phone> phoneTarget = dualListModelphones.getTarget();
            List<Phone> phoneSource = dualListModelphones.getSource();
            
            if (!phoneTarget.contains(phone_number) && !phoneSource.contains(phone_number)) {
                dualListModelphones.getSource().add(phone_number);
                msg = new FacesMessage("Add Phone number in list of selected Phone", phone_number.getPhone_number());
            }else{
                msg = new FacesMessage("Already have this phone number", phone_number.getPhone_number());
            }
            
        } else {
            msg = new FacesMessage("ERROR you mus provide Phone number", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        phone_number = new Phone();
    }

    public void addEmail() {
        FacesMessage msg = null;
        if (email != null && email.getEmail() != null && !email.getEmail().trim().equals("")) {
            List<Email> emailSource = dualListModelemails.getSource();
            List<Email> emailTarget = dualListModelemails.getTarget();

            
            if (!emailTarget.contains(email) && !emailSource.contains(email)) {
                 dualListModelemails.getSource().add(email);
                 msg = new FacesMessage("Add Email in list of selected Phone", email.getEmail());
            }else{
                msg = new FacesMessage("Already have this Email", email.getEmail());
            }
            
            msg = new FacesMessage("Add Email in list of selected Email", email.getEmail());
        } else {
            msg = new FacesMessage("ERROR you mus provide Email", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        email = new Email();
    }

    public void edit() {
        FacesMessage msg = null;
        if (selectedContact != null) {
            String contact_name = selectedContact.getContact_name().trim();
            List<Phone> listphones = commun_controller.getPhoneByContact(contact_name);
            List<Email> listemails = commun_controller.getEmailByContact(contact_name);

            if (listphones != null && !listphones.isEmpty()) {
                try {
                    commun_controller.deletePhoneByContact(contact_name);
                } catch (Exception e) {
                    msg = new FacesMessage("ERROR during  updating Row", contact_name);
                    e.printStackTrace();
                }
            } else {
                msg = new FacesMessage("phone number don't exist", contact_name);
            }

            if (listemails != null && !listemails.isEmpty()) {
                try {
                    commun_controller.deleteEmailByContact(contact_name);
                } catch (Exception e) {
                    msg = new FacesMessage("ERROR during  updating Row", contact_name);
                    e.printStackTrace();
                }
            } else {
                msg = new FacesMessage("Email don't exist", contact_name);
            }

            try {
                contact_controller.edit(selectedContact);
                 List<Phone> phoneTarget = dualListModelphones.getTarget();
                 List<Email> emailTarget = dualListModelemails.getTarget();

                for (Phone pho : phoneTarget) {
                    Phone phone = new Phone();
                    phone.setPhone_number(pho.getPhone_number());
                    phone.setContact(selectedContact);
                    phone_controller.create(phone);
                }

                for (Email email : emailTarget) {
                    Email mail = new Email();
                    mail.setEmail(email.getEmail());
                    mail.setContact(selectedContact);
                    email_controller.create(mail);
                }
                listContact = contact_controller.findContactEntities();
                msg = new FacesMessage("Success Update", contact_name);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  updating Row", contact_name);
                e.printStackTrace();
            }

        } else {
            msg = new FacesMessage("ERROR : you must provide Contact", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        List<Phone> phoneSource = new ArrayList<Phone>();
        List<Phone> phoneTarget = new ArrayList<Phone>();
        
        List<Email> emailSource = new ArrayList<Email>();
        List<Email> emailTarget = new ArrayList<Email>();
        
        dualListModelphones = new DualListModel<Phone>(phoneSource, phoneTarget);
        dualListModelemails = new DualListModel<Email>(emailSource, emailTarget);
        
        selectedContact = new Contact();
        phone_number = new Phone();
        email = new Email();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public String delete() {
        FacesMessage msg = null;
        String contact_name = selectedContact.getContact_name().trim();

        if (selectedContact != null) {
            List<Phone> listphones = commun_controller.getPhoneByContact(contact_name);
            List<Email> listemails = commun_controller.getEmailByContact(contact_name);

            if (listphones != null && !listphones.isEmpty()) {
                try {
                    commun_controller.deletePhoneByContact(contact_name);
                } catch (Exception e) {
                    msg = new FacesMessage("ERROR during  updating Row", contact_name);
                    e.printStackTrace();
                }
            }

            if (listemails != null && !listemails.isEmpty()) {
                try {
                    commun_controller.deleteEmailByContact(contact_name);
                } catch (Exception e) {
                    msg = new FacesMessage("ERROR during  updating Row", contact_name);
                    e.printStackTrace();
                }
            }

            try {
                contact_controller.destroy(selectedContact.getContact_id());
                listContact = contact_controller.findContactEntities();
                msg = new FacesMessage("Success Delete", contact_name);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  Cancellation Row", contact_name);
                e.printStackTrace();
            }
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        List<Phone> phoneSource = new ArrayList<Phone>();
        List<Phone> phoneTarget = new ArrayList<Phone>();
        
        List<Email> emailSource = new ArrayList<Email>();
        List<Email> emailTarget = new ArrayList<Email>();
        
        dualListModelphones = new DualListModel<Phone>(phoneSource, phoneTarget);
        dualListModelemails = new DualListModel<Email>(emailSource, emailTarget);
        
        selectedContact = new Contact();
        phone_number = new Phone();
        email = new Email();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }

    public String onAddNew() {
        // Add one new car to the table:
        FacesMessage msg = null;
        if (selectedContact != null) {
            String contact_name = selectedContact.getContact_name().trim();

            if (contact_name == null || contact_name.equals("")) {
                msg = new FacesMessage("You must provide Contact Label", "");
            } else {
                Contact contact = commun_controller.getOneByContact(contact_name);
                if (contact != null) {
                    msg = new FacesMessage("This Contact already exist", "");
                } else {
                    try {
                        contact_controller.create(selectedContact);

                        commun_controller.deletePhoneByContact(contact_name);
                        commun_controller.deleteEmailByContact(contact_name);

                        contact = commun_controller.getOneByContact(contact_name);

                        List<Phone> phoneTarget = dualListModelphones.getTarget();
                        List<Email> emailTarget = dualListModelemails.getTarget();

                       for (Phone pho : phoneTarget) {
                           Phone phone = new Phone();
                           phone.setPhone_number(pho.getPhone_number());
                           phone.setContact(selectedContact);
                           phone_controller.create(phone);
                       }

                       for (Email email : emailTarget) {
                           Email mail = new Email();
                           mail.setEmail(email.getEmail());
                           mail.setContact(selectedContact);
                           email_controller.create(mail);
                       }

                        msg = new FacesMessage("New Contact added", contact_name);
                        listContact = contact_controller.findContactEntities();

                    } catch (Exception e) {
                        msg = new FacesMessage("ERROR during add row", contact_name);
                        e.printStackTrace();
                    }
                }
            }
        } else {
            msg = new FacesMessage("ERROR : you must provide Contact", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        List<Phone> phoneSource = new ArrayList<Phone>();
        List<Phone> phoneTarget = new ArrayList<Phone>();
        
        List<Email> emailSource = new ArrayList<Email>();
        List<Email> emailTarget = new ArrayList<Email>();
        
        dualListModelphones = new DualListModel<Phone>(phoneSource, phoneTarget);
        dualListModelemails = new DualListModel<Email>(emailSource, emailTarget);
        
        selectedContact = new Contact();
        phone_number = new Phone();
        email = new Email();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
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
