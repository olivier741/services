/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.dao_controller.ContentMessageJpaController;
import com.tatsinktechnologic.dao_controller.ServiceJpaController;
import com.tatsinktechnologic.entities.notification_service.ContentMessage;
import com.tatsinktechnologic.entities.notification_service.Message_Status;
import com.tatsinktechnologic.entities.registration.Service;
import java.io.Serializable;
import java.util.Date;
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
public class ContentMessage_AcceptMB implements Serializable {

    private ContentMessage selectedContentMessage;
    private ContentMessage selectedContentMessage_cache;
    private List<Service> listservice;
    private Service selectedService;
    private List<ContentMessage> listContentMessage;

    private String service_string;
    private boolean do_enable;
    private boolean do_view;
    private boolean do_edit;
    private boolean do_reset;

    private Date launch_time;

    private int launch_hour;
    private int launch_min;
    private int launch_sec;

    private Date expire_time;

    private int expire_hour;
    private int expire_min;
    private int expire_sec;

    @Inject
    private Commun_Controller commun_controller;

    @Inject
    private ServiceJpaController service_controller;

    @Inject
    private ContentMessageJpaController contentMessage_controller;

    public ContentMessage getSelectedContentMessage() {
        return selectedContentMessage;
    }

    public void setSelectedContentMessage(ContentMessage selectedContentMessage) {
        this.selectedContentMessage = selectedContentMessage;
    }

    public ContentMessage getSelectedContentMessage_cache() {
        return selectedContentMessage_cache;
    }

    public void setSelectedContentMessage_cache(ContentMessage selectedContentMessage_cache) {
        this.selectedContentMessage_cache = selectedContentMessage_cache;
    }

    public List<Service> getListservice() {
        return listservice;
    }

    public void setListservice(List<Service> listservice) {
        this.listservice = listservice;
    }

    public Service getSelectedService() {
        return selectedService;
    }

    public void setSelectedService(Service selectedService) {
        this.selectedService = selectedService;
    }

    public List<ContentMessage> getListContentMessage() {
        return listContentMessage;
    }

    public void setListContentMessage(List<ContentMessage> listContentMessage) {
        this.listContentMessage = listContentMessage;
    }

    public String getService_string() {
        return service_string;
    }

    public void setService_string(String service_string) {
        this.service_string = service_string;
    }

    public boolean isDo_enable() {
        return do_enable;
    }

    public void setDo_enable(boolean do_enable) {
        this.do_enable = do_enable;
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

    public Date getLaunch_time() {
        return launch_time;
    }

    public void setLaunch_time(Date launch_time) {
        this.launch_time = launch_time;
    }

    public int getLaunch_hour() {
        return launch_hour;
    }

    public void setLaunch_hour(int launch_hour) {
        this.launch_hour = launch_hour;
    }

    public int getLaunch_min() {
        return launch_min;
    }

    public void setLaunch_min(int launch_min) {
        this.launch_min = launch_min;
    }

    public int getLaunch_sec() {
        return launch_sec;
    }

    public void setLaunch_sec(int launch_sec) {
        this.launch_sec = launch_sec;
    }

    public Date getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(Date expire_time) {
        this.expire_time = expire_time;
    }

    public int getExpire_hour() {
        return expire_hour;
    }

    public void setExpire_hour(int expire_hour) {
        this.expire_hour = expire_hour;
    }

    public int getExpire_min() {
        return expire_min;
    }

    public void setExpire_min(int expire_min) {
        this.expire_min = expire_min;
    }

    public int getExpire_sec() {
        return expire_sec;
    }

    public void setExpire_sec(int expire_sec) {
        this.expire_sec = expire_sec;
    }

    @PostConstruct
    public void init() {
        listContentMessage = contentMessage_controller.findContentMessageEntities();
        listservice = service_controller.findServiceEntities();

        do_enable = false;
        do_view = true;
        do_edit = false;
        do_reset = true;
        if (selectedContentMessage != null) {
            selectedService = commun_controller.getServiceByContent(selectedContentMessage.getContent_label());
        } else {
            selectedContentMessage = new ContentMessage();
        }
    }

    public void view(ContentMessage contentMsg) {

        selectedContentMessage = contentMsg;
        selectedContentMessage_cache = SerializationUtils.clone(contentMsg);
        selectedService = selectedContentMessage.getService();

        launch_time = selectedContentMessage.getLaunch_time();

        if (launch_time != null) {
            launch_hour = launch_time.getHours();
            launch_min = launch_time.getMinutes();
            launch_sec = launch_time.getSeconds();
        }

        expire_time = selectedContentMessage.getExpire_time();

        if (expire_time != null) {
            expire_hour = expire_time.getHours();
            expire_min = expire_time.getMinutes();
            expire_sec = expire_time.getSeconds();
        }

        do_enable = true;
        do_view = true;
        do_edit = false;
        do_reset = true;
    }

    public void enableEdit() {

        if (selectedContentMessage_cache.getMessage_status() == Message_Status.CONFIRM || selectedContentMessage_cache.getMessage_status() == Message_Status.ACCEPT) {
            do_enable = false;
            do_view = false;
            do_edit = true;
            do_reset = true;
            selectedContentMessage = SerializationUtils.clone(selectedContentMessage_cache);
            selectedService = selectedContentMessage.getService();
            listContentMessage = contentMessage_controller.findContentMessageEntities();
        } else {
            FacesMessage msg = null;
            msg = new FacesMessage("This Message is not Editable on the following State :", selectedContentMessage.getMessage_status().name());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

    }

    public void clear() {

        selectedContentMessage = new ContentMessage();

        launch_time = null;

        launch_hour = 0;
        launch_min = 0;
        launch_sec = 0;

        expire_time = null;

        expire_hour = 0;
        expire_min = 0;
        expire_sec = 0;

        do_enable = false;
        do_view = true;
        do_edit = false;
        do_reset = true;

    }

    public void edit() {
        FacesMessage msg = null;
        if (selectedContentMessage != null) {

            String content_label = selectedContentMessage.getContent_label().trim();
            Service new_service = null;

            if (listservice != null && !listservice.isEmpty()) {
                if (selectedService != null) {
                    String serv_name = selectedService.getService_name();
                    for (Service serv : listservice) {
                        if (serv.getService_name().equals(serv_name.trim())) {
                            new_service = serv;
                            break;
                        }
                    }
                }

            }

            if (new_service != null) {
                selectedContentMessage.setService(new_service);
            }

            try {
                java.sql.Timestamp launch_time_t = null;

                if (launch_time != null) {
                    launch_time_t = new java.sql.Timestamp(launch_time.getTime());

                    launch_time_t.setHours(launch_hour);
                    launch_time_t.setMinutes(launch_min);
                    launch_time_t.setSeconds(launch_sec);

                    selectedContentMessage.setLaunch_time(launch_time_t);

                }

                java.sql.Timestamp expire_time_t = null;

                if (expire_time != null) {
                    expire_time_t = new java.sql.Timestamp(expire_time.getTime());

                    expire_time_t.setHours(expire_hour);
                    expire_time_t.setMinutes(expire_min);
                    expire_time_t.setSeconds(expire_sec);

                    selectedContentMessage.setExpire_time(expire_time_t);

                }

                java.sql.Timestamp confirm_time_t = new java.sql.Timestamp((new Date()).getTime());

                if (selectedContentMessage_cache != null) {
                    if (selectedContentMessage_cache.getMessage_status() == Message_Status.CONFIRM) {
                        if (selectedContentMessage.getMessage_status() == Message_Status.ACCEPT) {
                            selectedContentMessage.setAccpt_time(confirm_time_t);
                        }
                    }
                } else {
                    if (selectedContentMessage.getMessage_status() == Message_Status.ACCEPT) {
                        selectedContentMessage.setAccpt_time(confirm_time_t);
                    }
                }

                contentMessage_controller.edit(selectedContentMessage);
                listContentMessage = contentMessage_controller.findContentMessageEntities();
                msg = new FacesMessage("Success Update", content_label);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  updating Row", content_label);
                e.printStackTrace();
            }

        } else {
            msg = new FacesMessage("ERROR : you must provide Product", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);

        selectedContentMessage = new ContentMessage();

        launch_time = null;

        launch_hour = 0;
        launch_min = 0;
        launch_sec = 0;

        expire_time = null;

        expire_hour = 0;
        expire_min = 0;
        expire_sec = 0;

        do_enable = false;
        do_view = true;
        do_edit = false;
        do_reset = true;

    }

    public String delete() {
        FacesMessage msg = null;
        if (selectedContentMessage.getMessage_status() == Message_Status.CONFIRM || selectedContentMessage.getMessage_status() == Message_Status.ACCEPT) {
            String content_label = selectedContentMessage.getContent_label().trim();
            Service service = selectedContentMessage.getService();

            boolean mustEdit = false;

            if (service != null) {
                selectedContentMessage.setService(null);
                mustEdit = true;
            }

            if (mustEdit) {
                try {
                    contentMessage_controller.edit(selectedContentMessage);
                } catch (Exception e) {
                    msg = new FacesMessage("ERROR during  delete Row", content_label);
                    e.printStackTrace();
                }
            }

            try {
                contentMessage_controller.destroy(selectedContentMessage.getContent_id());
                listContentMessage = contentMessage_controller.findContentMessageEntities();
                msg = new FacesMessage("Success Delete", content_label);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  Cancellation Row", content_label);
                e.printStackTrace();
            }

            selectedContentMessage = new ContentMessage();

            launch_time = null;

            launch_hour = 0;
            launch_min = 0;
            launch_sec = 0;

            expire_time = null;

            expire_hour = 0;
            expire_min = 0;
            expire_sec = 0;

            do_enable = true;
            do_view = false;
            do_edit = false;
            do_reset = true;

        } else {
            msg = new FacesMessage("This Message is not Editable on the following State :", selectedContentMessage.getMessage_status().name());
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);

        return null;
    }

    public String onAddNew() {
        // Add one new car to the table:
        FacesMessage msg = null;
        if (selectedContentMessage != null) {
            String content_label = selectedContentMessage.getContent_label().trim();

            if (content_label == null || content_label.equals("")) {
                msg = new FacesMessage("You must provide Content Label", "");
            } else {
                ContentMessage content_Msg = commun_controller.getOneByContent(content_label);
                if (content_Msg != null) {
                    msg = new FacesMessage("This Content already exist", "");
                } else {

                    Service new_service = null;

                    if (listservice != null && !listservice.isEmpty()) {
                        if (selectedService != null) {
                            String serv_name = selectedService.getService_name();
                            for (Service serv : listservice) {
                                if (serv.getService_name().equals(serv_name.trim())) {
                                    new_service = serv;
                                    break;
                                }
                            }
                        }

                    }

                    if (new_service != null) {
                        selectedContentMessage.setService(new_service);
                    }

                    try {
                        java.sql.Timestamp launch_time_t = null;

                        if (launch_time != null) {
                            launch_time_t = new java.sql.Timestamp(launch_time.getTime());

                            launch_time_t.setHours(launch_hour);
                            launch_time_t.setMinutes(launch_min);
                            launch_time_t.setSeconds(launch_sec);

                            selectedContentMessage.setLaunch_time(launch_time_t);

                        }

                        java.sql.Timestamp expire_time_t = null;

                        if (expire_time != null) {
                            expire_time_t = new java.sql.Timestamp(expire_time.getTime());

                            expire_time_t.setHours(expire_hour);
                            expire_time_t.setMinutes(expire_min);
                            expire_time_t.setSeconds(expire_sec);

                            selectedContentMessage.setExpire_time(expire_time_t);

                        }

                        java.sql.Timestamp confirm_time_t = new java.sql.Timestamp((new Date()).getTime());

                        if (selectedContentMessage.getMessage_status() == Message_Status.ACCEPT) {
                            selectedContentMessage.setSubmit_time(confirm_time_t);
                        }

                        contentMessage_controller.create(selectedContentMessage);
                        listContentMessage = contentMessage_controller.findContentMessageEntities();
                        msg = new FacesMessage("Success Create", content_label);
                    } catch (Exception e) {
                        msg = new FacesMessage("ERROR during  Create new Row", content_label);
                        e.printStackTrace();
                    }
                }
            }
        } else {
            msg = new FacesMessage("ERROR : you must provide Product Code", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);

        selectedContentMessage = new ContentMessage();

        launch_time = null;

        launch_hour = 0;
        launch_min = 0;
        launch_sec = 0;

        expire_time = null;

        expire_hour = 0;
        expire_min = 0;
        expire_sec = 0;

        do_enable = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

        return null;
    }
}
