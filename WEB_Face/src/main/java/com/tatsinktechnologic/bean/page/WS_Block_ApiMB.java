/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.dao_controller.WS_Block_APIJpaController;
import com.tatsinktechnologic.dao_controller.WS_Block_WebserviceRelJpaController;
import com.tatsinktechnologic.dao_controller.WS_WebserviceJpaController;
import com.tatsinktechnologic.entities.api_gateway.WS_Block_API;
import com.tatsinktechnologic.entities.api_gateway.WS_Block_WebserviceRel;
import com.tatsinktechnologic.entities.api_gateway.WS_Webservice;
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
 * @author olivier
 */
@Named
@ViewScoped
public class WS_Block_ApiMB implements Serializable {

    private WS_Block_API selectedWS_Block_API;
    private WS_Block_API selectedWS_Block_API_cache;
    private DualListModel<WS_Webservice> dualListModelws_Webservices;
    private List<WS_Webservice> listWS_Webservice;
    private List<WS_Block_API> listWS_Block_API;
    private String ws_Webservice_string;
    private boolean do_create;
    private boolean do_view;
    private boolean do_edit;
    private boolean do_reset;

    @Inject
    private Commun_Controller commun_controller;

    @Inject
    private WS_Block_APIJpaController ws_block_api_controller;

    @Inject
    private WS_WebserviceJpaController webservice_controller;

    @Inject
    private WS_Block_WebserviceRelJpaController block_webservice_controller;

    public WS_Block_API getSelectedWS_Block_API() {
        return selectedWS_Block_API;
    }

    public void setSelectedWS_Block_API(WS_Block_API selectedWS_Block_API) {
        this.selectedWS_Block_API = selectedWS_Block_API;
    }

    public WS_Block_API getSelectedWS_Block_API_cache() {
        return selectedWS_Block_API_cache;
    }

    public void setSelectedWS_Block_API_cache(WS_Block_API selectedWS_Block_API_cache) {
        this.selectedWS_Block_API_cache = selectedWS_Block_API_cache;
    }

    public DualListModel<WS_Webservice> getDualListModelws_Webservices() {
        return dualListModelws_Webservices;
    }

    public void setDualListModelws_Webservices(DualListModel<WS_Webservice> dualListModelws_Webservices) {
        this.dualListModelws_Webservices = dualListModelws_Webservices;
    }

    public List<WS_Webservice> getListWS_Webservice() {
        return listWS_Webservice;
    }

    public void setListWS_Webservice(List<WS_Webservice> listWS_Webservice) {
        this.listWS_Webservice = listWS_Webservice;
    }

    public List<WS_Block_API> getListWS_Block_API() {
        return listWS_Block_API;
    }

    public void setListWS_Block_API(List<WS_Block_API> listWS_Block_API) {
        this.listWS_Block_API = listWS_Block_API;
    }



    public String getWs_Webservice_string() {
        return ws_Webservice_string;
    }

    public void setWs_Webservice_string(String ws_Webservice_string) {
        this.ws_Webservice_string = ws_Webservice_string;
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
        List<WS_Webservice> webserviceSource = new ArrayList<WS_Webservice>();
        List<WS_Webservice> webserviceTarget = new ArrayList<WS_Webservice>();

        listWS_Block_API = ws_block_api_controller.findWS_Block_APIEntities();

        listWS_Webservice = webservice_controller.findWS_WebserviceEntities();

        dualListModelws_Webservices = new DualListModel<WS_Webservice>(listWS_Webservice, webserviceTarget);

        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

        if (selectedWS_Block_API != null) {

            webserviceSource = dualListModelws_Webservices.getSource();
            webserviceTarget = commun_controller.getwebserviceByBlockname(selectedWS_Block_API.getBlock_api_name());

            for (WS_Webservice websrv : webserviceTarget) {
                if (webserviceSource.contains(websrv)) {
                    webserviceSource.remove(websrv);
                }
            }
            dualListModelws_Webservices.setSource(webserviceSource);
            dualListModelws_Webservices.setTarget(webserviceTarget);
        } else {
            selectedWS_Block_API = new WS_Block_API();
        }
    }

    public String getWebservice(String block_api_name) {
        List<WS_Webservice> listwebsrv = commun_controller.getwebserviceByBlockname(selectedWS_Block_API.getBlock_api_name());
        String result = "";
        for (WS_Webservice websrv : listwebsrv) {
            result = result + websrv.getWebservice_name() + "\n";
        }
        ws_Webservice_string = result;
        return result;
    }

    public void view(WS_Block_API block_api) {
        List<WS_Webservice> webserviceSource = new ArrayList<WS_Webservice>();
        List<WS_Webservice> webserviceTarget = new ArrayList<WS_Webservice>();

        selectedWS_Block_API = block_api;
        selectedWS_Block_API_cache = SerializationUtils.clone(block_api);

        webserviceTarget = commun_controller.getwebserviceByBlockname(selectedWS_Block_API.getBlock_api_name());

        for (WS_Webservice websrv : listWS_Webservice) {
            if (!webserviceTarget.contains(websrv)) {
                webserviceSource.add(websrv);
            }
        }
        dualListModelws_Webservices = new DualListModel<WS_Webservice>(webserviceSource, webserviceTarget);

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
        selectedWS_Block_API = selectedWS_Block_API_cache;

    }

    public void clear() {

        List<WS_Webservice> webserviceTarget = new ArrayList<WS_Webservice>();

        dualListModelws_Webservices = new DualListModel<WS_Webservice>(listWS_Webservice, webserviceTarget);

        selectedWS_Block_API = new WS_Block_API();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public void edit() {
        FacesMessage msg = null;
        if (selectedWS_Block_API != null) {
            String block_name = selectedWS_Block_API.getBlock_api_name().trim();
            List<WS_Webservice> listwebsrv = commun_controller.getwebserviceByBlockname(block_name);

            if (listwebsrv != null && !listwebsrv.isEmpty()) {
                try {
                    commun_controller.deleteBockWebserviceRelByBlock_API(block_name);
                } catch (Exception e) {
                    msg = new FacesMessage("ERROR during  delete Row", block_name);
                    e.printStackTrace();
                }
            }

            try {

                ws_block_api_controller.edit(selectedWS_Block_API);

                List<WS_Webservice> webserviceTarget = dualListModelws_Webservices.getTarget();

                int ex_order = 0;
                for (WS_Webservice websrv : webserviceTarget) {
                    WS_Block_WebserviceRel block_websrv = new WS_Block_WebserviceRel();
                    block_websrv.setExecution_order(ex_order);
                    block_websrv.setWs_block_api(selectedWS_Block_API);
                    block_websrv.setWs_webservice(websrv);

                    block_webservice_controller.create(block_websrv);
                    ex_order++;
                }

                listWS_Block_API = ws_block_api_controller.findWS_Block_APIEntities();
                msg = new FacesMessage("Success Update", block_name);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  updating Row", block_name);
                e.printStackTrace();
            }

        } else {
            msg = new FacesMessage("ERROR : you must provide Role", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);

        List<WS_Webservice> webserviceTarget = new ArrayList<WS_Webservice>();

        dualListModelws_Webservices = new DualListModel<WS_Webservice>(listWS_Webservice, webserviceTarget);

        selectedWS_Block_API = new WS_Block_API();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public String delete() {
        FacesMessage msg = null;
        String block_name = selectedWS_Block_API.getBlock_api_name().trim();

        List<WS_Webservice> listwebsrv = commun_controller.getwebserviceByBlockname(block_name);

        if (listwebsrv != null && !listwebsrv.isEmpty()) {
            try {
                commun_controller.deleteBockWebserviceRelByBlock_API(block_name);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  delete Row", block_name);
                e.printStackTrace();
            }
        }

        try {

            ws_block_api_controller.destroy(selectedWS_Block_API.getWs_block_api_id());

            listWS_Block_API = ws_block_api_controller.findWS_Block_APIEntities();
            msg = new FacesMessage("Success Delete", block_name);
        } catch (Exception e) {
            msg = new FacesMessage("ERROR during  Cancellation Row", block_name);
            e.printStackTrace();
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);

        List<WS_Webservice> webserviceTarget = new ArrayList<WS_Webservice>();

        dualListModelws_Webservices = new DualListModel<WS_Webservice>(listWS_Webservice, webserviceTarget);

        selectedWS_Block_API = new WS_Block_API();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }

    public String onAddNew() {
        // Add one new car to the table:
        FacesMessage msg = null;
        if (selectedWS_Block_API != null) {
            String block_name = selectedWS_Block_API.getBlock_api_name().trim();
            if (block_name == null || block_name.equals("")) {
                msg = new FacesMessage("You must provide Name", "");
            } else {
                WS_Block_API block_api = commun_controller.getOneByBockname(block_name);
                if (block_api != null) {
                    msg = new FacesMessage("already exist", "");
                } else {
                    try {

                        ws_block_api_controller.create(selectedWS_Block_API);

                        commun_controller.deleteBockWebserviceRelByBlock_API(block_name);

                        block_api = commun_controller.getOneByBockname(block_name);

                        List<WS_Webservice> webserviceTarget = dualListModelws_Webservices.getTarget();

                        int ex_order = 0;
                        for (WS_Webservice websrv : webserviceTarget) {
                            WS_Block_WebserviceRel block_websrv = new WS_Block_WebserviceRel();
                            block_websrv.setExecution_order(ex_order);
                            block_websrv.setWs_block_api(selectedWS_Block_API);
                            block_websrv.setWs_webservice(websrv);

                            block_webservice_controller.create(block_websrv);
                            ex_order++;
                        }

                        listWS_Block_API = ws_block_api_controller.findWS_Block_APIEntities();

                        msg = new FacesMessage("New added", block_name);
                    } catch (Exception e) {
                        msg = new FacesMessage("ERROR during add row", block_name);
                        e.printStackTrace();
                    }

                }

            }
        } else {
            msg = new FacesMessage("ERROR : you must provide Value", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        List<WS_Webservice> webserviceTarget = new ArrayList<WS_Webservice>();

        dualListModelws_Webservices = new DualListModel<WS_Webservice>(listWS_Webservice, webserviceTarget);

        selectedWS_Block_API = new WS_Block_API();
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
