/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.dao_controller.WS_Header_ParamJpaController;
import com.tatsinktechnologic.dao_controller.WS_WebserviceJpaController;
import com.tatsinktechnologic.entities.api_gateway.Type_API;
import com.tatsinktechnologic.entities.api_gateway.WS_Header_Param;
import com.tatsinktechnologic.entities.api_gateway.WS_Webservice;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.SerializationUtils;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.DualListModel;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.springframework.ui.context.Theme;

/**
 *
 * @author olivier.tatsinkou
 */
@Named
@ViewScoped
public class WS_webserviceMB implements Serializable {

    private WS_Webservice selectedWS_Webservice;
    private WS_Webservice selectedWS_Webservice_cache;
    private List<WS_Webservice> listWS_Webservice;
    private DualListModel<WS_Header_Param> dualListModelheader_params;
    private WS_Header_Param header_param;
    private String allHeader_Param;
    private Date expire_time;

    private int start_hour;
    private int start_min;
    private int start_sec;

    private boolean do_create;
    private boolean do_view;
    private boolean do_edit;
    private boolean do_reset;

    private boolean skip;

    private boolean skip_first;

    private boolean isSoap_Rest;

    @Inject
    private Commun_Controller commun_controller;

    @Inject
    private WS_WebserviceJpaController ws_webservice_controller;
    
    @Inject
    private WS_Header_ParamJpaController header_param_controller;

    public WS_Webservice getSelectedWS_Webservice() {
        return selectedWS_Webservice;
    }

    public void setSelectedWS_Webservice(WS_Webservice selectedWS_Webservice) {
        this.selectedWS_Webservice = selectedWS_Webservice;
    }

    public WS_Webservice getSelectedWS_Webservice_cache() {
        return selectedWS_Webservice_cache;
    }

    public void setSelectedWS_Webservice_cache(WS_Webservice selectedWS_Webservice_cache) {
        this.selectedWS_Webservice_cache = selectedWS_Webservice_cache;
    }

    public List<WS_Webservice> getListWS_Webservice() {
        return listWS_Webservice;
    }

    public void setListWS_Webservice(List<WS_Webservice> listWS_Webservice) {
        this.listWS_Webservice = listWS_Webservice;
    }

    public DualListModel<WS_Header_Param> getDualListModelheader_params() {
        return dualListModelheader_params;
    }

    public void setDualListModelheader_params(DualListModel<WS_Header_Param> dualListModelheader_params) {
        this.dualListModelheader_params = dualListModelheader_params;
    }

    public WS_Header_Param getHeader_param() {
        return header_param;
    }

    public void setHeader_param(WS_Header_Param header_param) {
        this.header_param = header_param;
    }

    public String getAllHeader_Param() {
        return allHeader_Param;
    }

    public void setAllHeader_Param(String allHeader_Param) {
        this.allHeader_Param = allHeader_Param;
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

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public boolean isSkip_first() {
        return skip_first;
    }

    public void setSkip_first(boolean skip_first) {
        this.skip_first = skip_first;
    }

    public boolean isIsSoap_Rest() {
        return isSoap_Rest;
    }

    public void setIsSoap_Rest(boolean isSoap_Rest) {
        this.isSoap_Rest = isSoap_Rest;
    }

    public Date getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(Date expire_time) {
        this.expire_time = expire_time;
    }

    public int getStart_hour() {
        return start_hour;
    }

    public void setStart_hour(int start_hour) {
        this.start_hour = start_hour;
    }

    public int getStart_min() {
        return start_min;
    }

    public void setStart_min(int start_min) {
        this.start_min = start_min;
    }

    public int getStart_sec() {
        return start_sec;
    }

    public void setStart_sec(int start_sec) {
        this.start_sec = start_sec;
    }

    @PostConstruct
    public void init() {
        List<WS_Header_Param> headerParamTarget = new ArrayList<WS_Header_Param>();
        List<WS_Header_Param> headerParamSource = new ArrayList<WS_Header_Param>();
        
        listWS_Webservice = ws_webservice_controller.findWS_WebserviceEntities();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        if (selectedWS_Webservice == null) {
            selectedWS_Webservice = new WS_Webservice();
        }else{
            headerParamSource = commun_controller.getHeader_ParamByWebservice(selectedWS_Webservice.getWebservice_name());
        }
        header_param = new WS_Header_Param();
        dualListModelheader_params = new DualListModel<WS_Header_Param>(headerParamSource, headerParamTarget);

    }
    
     public String getHeader_ParamByWebservice(String webservice_name) {
        List<WS_Header_Param> listheader_param = commun_controller.getHeader_ParamByWebservice(webservice_name);
        String result = "";
        for (WS_Header_Param head_pr : listheader_param) {
            result = result + head_pr.getParam_key()+" | "+head_pr.getParam_value() + "\n";
        }
        allHeader_Param = result;
        return result;
    }
     
     
       public void addHeaderParam() {
        FacesMessage msg = null;
        if (header_param != null && !StringUtils.isBlank(header_param.getParam_key())  && !StringUtils.isBlank(header_param.getParam_value())) {
            List<WS_Header_Param> headerParamTarget = dualListModelheader_params.getTarget();
            List<WS_Header_Param> headerParamSource = dualListModelheader_params.getSource();
            
            if (!headerParamTarget.contains(header_param) && !headerParamSource.contains(header_param)) {
                dualListModelheader_params.getSource().add(header_param);
                msg = new FacesMessage("Add Header parameter", header_param.toString());
            }else{
                msg = new FacesMessage("Already have this Header Parameter", header_param.toString());
            }
            
        } else {
            msg = new FacesMessage("ERROR you mus provide Header Parameter", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        header_param = new WS_Header_Param();
    }

    public void view(WS_Webservice webservice) {
        List<WS_Header_Param> headerParamTarget = new ArrayList<WS_Header_Param>();
        selectedWS_Webservice = webservice;
        selectedWS_Webservice_cache = SerializationUtils.clone(webservice);
        
        headerParamTarget = commun_controller.getHeader_ParamByWebservice(selectedWS_Webservice.getWebservice_name());

        dualListModelheader_params.setTarget(headerParamTarget);
        
        expire_time = selectedWS_Webservice.getExpire_time_token();

        if (expire_time!=null){
            start_hour = expire_time.getHours();
            start_min = expire_time.getMinutes();
            start_sec = expire_time.getSeconds();
        }
        

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

        selectedWS_Webservice = selectedWS_Webservice_cache;
        listWS_Webservice = ws_webservice_controller.findWS_WebserviceEntities();

    }

    public void clear() {
        List<WS_Header_Param> headerParamTarget = new ArrayList<WS_Header_Param>();
        List<WS_Header_Param> headerParamSource = new ArrayList<WS_Header_Param>();
        
        dualListModelheader_params = new DualListModel<WS_Header_Param>(headerParamSource, headerParamTarget);
        
        selectedWS_Webservice = new WS_Webservice();
        header_param = new WS_Header_Param();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public void edit() {
        FacesMessage msg = null;
        if (selectedWS_Webservice != null) {
            String client_name = selectedWS_Webservice.getWebservice_name().trim();
            List<WS_Header_Param> listheader_param = commun_controller.getHeader_ParamByWebservice(client_name);
            if (listheader_param != null && !listheader_param.isEmpty()) {
                try {
                    commun_controller.deleteHeaderParamByWebservice(client_name);
                } catch (Exception e) {
                    msg = new FacesMessage("ERROR during  deletation of Header Param of : ", client_name);
                    e.printStackTrace();
                }
            }
            
            
            try {
                
                if (expire_time!=null){
                    java.sql.Timestamp start_t = new java.sql.Timestamp(expire_time.getTime());

                    start_t.setHours(start_hour);
                    start_t.setMinutes(start_min);
                    start_t.setSeconds(start_sec);

                    selectedWS_Webservice.setExpire_time_token(start_t);
                }
                ws_webservice_controller.edit(selectedWS_Webservice);
                
                List<WS_Header_Param> headerParamTarget = dualListModelheader_params.getTarget();
                
                 for (WS_Header_Param head_param : headerParamTarget) {
                    WS_Header_Param header_param = new WS_Header_Param();
                    header_param.setParam_key(head_param.getParam_key());
                    header_param.setParam_value(head_param.getParam_value());
                    header_param.setWs_webservice(selectedWS_Webservice);
                    header_param_controller.create(header_param);
                }

                listWS_Webservice = ws_webservice_controller.findWS_WebserviceEntities();
                msg = new FacesMessage("Success Update", client_name);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  updating Row", client_name);
                e.printStackTrace();
            }
        } else {
            msg = new FacesMessage("ERROR : you must provide Role", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        List<WS_Header_Param> headerParamTarget = new ArrayList<WS_Header_Param>();
        List<WS_Header_Param> headerParamSource = new ArrayList<WS_Header_Param>();
        
        dualListModelheader_params = new DualListModel<WS_Header_Param>(headerParamSource, headerParamTarget);
        header_param = new WS_Header_Param();
        
        selectedWS_Webservice = new WS_Webservice();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public String delete() {
        FacesMessage msg = null;
        String client_name = selectedWS_Webservice.getWebservice_name().trim();

         List<WS_Header_Param> listheader_param = commun_controller.getHeader_ParamByWebservice(client_name);
            if (listheader_param != null && !listheader_param.isEmpty()) {
                try {
                    commun_controller.deleteHeaderParamByWebservice(client_name);
                } catch (Exception e) {
                    msg = new FacesMessage("ERROR during  deletation of Header Param of : ", client_name);
                    e.printStackTrace();
                }
            }
            
        try {
            ws_webservice_controller.destroy(selectedWS_Webservice.getWs_webservice_id());

            listWS_Webservice = ws_webservice_controller.findWS_WebserviceEntities();
            msg = new FacesMessage("Success Delete", client_name);
        } catch (Exception e) {
            msg = new FacesMessage("ERROR during  Cancellation Row", client_name);
            e.printStackTrace();
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        List<WS_Header_Param> headerParamTarget = new ArrayList<WS_Header_Param>();
        List<WS_Header_Param> headerParamSource = new ArrayList<WS_Header_Param>();
        
        dualListModelheader_params = new DualListModel<WS_Header_Param>(headerParamSource, headerParamTarget);
        header_param = new WS_Header_Param();
        
        selectedWS_Webservice = new WS_Webservice();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }

    public String onAddNew() {

        FacesMessage msg = null;
        if (selectedWS_Webservice != null) {
            String client_name = selectedWS_Webservice.getWebservice_name().trim();
            if (client_name == null || client_name.equals("")) {
                msg = new FacesMessage("You must provide CLient Name", "");
            } else {
                WS_Webservice ws_client = commun_controller.getOneByWS_Webservice(client_name);
                if (ws_client != null) {
                    msg = new FacesMessage("This Serivice already exist", "");
                } else {
                    try {

                        if (expire_time!=null){
                            java.sql.Timestamp start_t = new java.sql.Timestamp(expire_time.getTime());

                            start_t.setHours(start_hour);
                            start_t.setMinutes(start_min);
                            start_t.setSeconds(start_sec);

                            selectedWS_Webservice.setExpire_time_token(start_t);
                        }

                        ws_webservice_controller.create(selectedWS_Webservice);
                        
                        List<WS_Header_Param> headerParamTarget = dualListModelheader_params.getTarget();
                
                        for (WS_Header_Param head_param : headerParamTarget) {
                           WS_Header_Param header_param = new WS_Header_Param();
                           header_param.setParam_key(head_param.getParam_key());
                           header_param.setParam_value(head_param.getParam_value());
                           header_param.setWs_webservice(selectedWS_Webservice);
                           header_param_controller.create(header_param);
                       }

                        msg = new FacesMessage("New webservice added", client_name);
                        listWS_Webservice = ws_webservice_controller.findWS_WebserviceEntities();
                  
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

        List<WS_Header_Param> headerParamTarget = new ArrayList<WS_Header_Param>();
        List<WS_Header_Param> headerParamSource = new ArrayList<WS_Header_Param>();
        
        dualListModelheader_params = new DualListModel<WS_Header_Param>(headerParamSource, headerParamTarget);
        header_param = new WS_Header_Param();
        
        selectedWS_Webservice = new WS_Webservice();
        
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }

    public String onFlowProcess(FlowEvent event) {

        if (selectedWS_Webservice.getWebservice_type() == Type_API.SOAP) {
            isSoap_Rest = true;
        } else {
            isSoap_Rest = false;
        }

        if (skip) {
            skip = false;   //reset in case user goes back
            return "confirm";
        }
        if (skip_first) {
            skip_first = false;
            return "api_commun";
        } else {

            return event.getNewStep();
        }
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
