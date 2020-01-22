/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.dao_controller.WS_AccessManagementJpaController;
import com.tatsinktechnologic.dao_controller.WS_Block_APIJpaController;
import com.tatsinktechnologic.dao_controller.WS_ClientJpaController;
import com.tatsinktechnologic.entities.account.Phone;
import com.tatsinktechnologic.entities.api_gateway.WS_AccessManagement;
import com.tatsinktechnologic.entities.api_gateway.WS_Block_API;
import com.tatsinktechnologic.entities.api_gateway.WS_Block_WebserviceRel;
import com.tatsinktechnologic.entities.api_gateway.WS_Client;
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

/**
 *
 * @author olivier.tatsinkou
 */
@Named
@ViewScoped
public class WS_AccessManagementMB implements Serializable {
    
    private WS_AccessManagement selectedWS_AccessManagement;
    private WS_AccessManagement selectedWS_AccessManagement_cache;
    private List<WS_AccessManagement> listWS_AccessManagement;
    private List<WS_Client> listws_client;
    private WS_Client selectedWS_Client;
    private List<WS_Block_API> listWS_Block_API;
    private WS_Block_API selectedWS_Block_API;
    private String allWebservice;
    
    
    private Date start_time;
    private Date end_time;
    
    private boolean do_create;
    private boolean do_view;
    private boolean do_edit;
    private boolean do_reset;
    
    @Inject
    private Commun_Controller commun_controller;
    
    @Inject
    private WS_AccessManagementJpaController ws_accessMgn_controller;

    @Inject
    private WS_ClientJpaController ws_client_controller;
    
   @Inject
    private WS_Block_APIJpaController ws_block_api_controller;


    public WS_AccessManagement getSelectedWS_AccessManagement() {
        return selectedWS_AccessManagement;
    }

    public void setSelectedWS_AccessManagement(WS_AccessManagement selectedWS_AccessManagement) {
        this.selectedWS_AccessManagement = selectedWS_AccessManagement;
    }

    public WS_AccessManagement getSelectedWS_AccessManagement_cache() {
        return selectedWS_AccessManagement_cache;
    }

    public void setSelectedWS_AccessManagement_cache(WS_AccessManagement selectedWS_AccessManagement_cache) {
        this.selectedWS_AccessManagement_cache = selectedWS_AccessManagement_cache;
    }

    public List<WS_AccessManagement> getListWS_AccessManagement() {
        return listWS_AccessManagement;
    }

    public void setListWS_AccessManagement(List<WS_AccessManagement> listWS_AccessManagement) {
        this.listWS_AccessManagement = listWS_AccessManagement;
    }

    public List<WS_Client> getListws_client() {
        return listws_client;
    }

    public void setListws_client(List<WS_Client> listws_client) {
        this.listws_client = listws_client;
    }

    public WS_Client getSelectedWS_Client() {
        return selectedWS_Client;
    }

    public void setSelectedWS_Client(WS_Client selectedWS_Client) {
        this.selectedWS_Client = selectedWS_Client;
    }

    public List<WS_Block_API> getListWS_Block_API() {
        return listWS_Block_API;
    }

    public void setListWS_Block_API(List<WS_Block_API> listWS_Block_API) {
        this.listWS_Block_API = listWS_Block_API;
    }

    public WS_Block_API getSelectedWS_Block_API() {
        return selectedWS_Block_API;
    }

    public void setSelectedWS_Block_API(WS_Block_API selectedWS_Block_API) {
        this.selectedWS_Block_API = selectedWS_Block_API;
    }

 
    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
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

    public String getAllWebservice() {
        return allWebservice;
    }

    public void setAllWebservice(String allWebservice) {
        this.allWebservice = allWebservice;
    }
     
    
    
       
    @PostConstruct
    public void init() {
        listWS_AccessManagement = ws_accessMgn_controller.findWS_AccessManagementEntities();
        listws_client = ws_client_controller.findWS_ClientEntities();
        
        listWS_Block_API = ws_block_api_controller.findWS_Block_APIEntities();
        
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        if (selectedWS_AccessManagement != null) {
            selectedWS_Client = commun_controller.getWS_ClientByWS_AccessManagement(selectedWS_AccessManagement.getAccess_name());
//            selectedWS_Webservice = commun_controller.getWS_WebserviceByWS_AccessManagement(selectedWS_AccessManagement.getAccess_name());
        } else {
            selectedWS_AccessManagement = new WS_AccessManagement();
        }
    }
    
      public String getWebservice(String access_name) {
        WS_Block_API ws_block = commun_controller.getWS_Block_APIByWS_AccessManagement(access_name);
        
        List <WS_Block_WebserviceRel> list_block_web = new ArrayList<WS_Block_WebserviceRel>(ws_block.getListWS_Block_WebserviceRel());
        
        String result = "";
        for (WS_Block_WebserviceRel block_websrv : list_block_web) {
            result = result + block_websrv.getWs_webservice().getWebservice_name() + "\n";
        }
        allWebservice = result;
        return result;
    }
    
    public void view(WS_AccessManagement ws_access) {
        selectedWS_AccessManagement = ws_access;
        selectedWS_AccessManagement_cache = SerializationUtils.clone(ws_access);
        selectedWS_Client = selectedWS_AccessManagement.getWs_client();
//        selectedWS_Webservice = selectedWS_AccessManagement.getWs_webservice();
            
        
        start_time = selectedWS_AccessManagement.getStart_time();
        end_time = selectedWS_AccessManagement.getExpire_time();
        
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
        selectedWS_AccessManagement = selectedWS_AccessManagement_cache;
        selectedWS_Client = selectedWS_AccessManagement.getWs_client();
//        selectedWS_Webservice = selectedWS_AccessManagement.getWs_webservice();
        listWS_AccessManagement = ws_accessMgn_controller.findWS_AccessManagementEntities();

    }

    
    
    public void clear() {
        selectedWS_AccessManagement = new WS_AccessManagement();

        start_time = null;
        end_time = null;
        
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }
    
       public void edit() {
        FacesMessage msg = null;
        if (selectedWS_AccessManagement != null) {
            String access_name = selectedWS_AccessManagement.getAccess_name().trim();
            WS_Client new_ws_client = null;
            WS_Block_API new_blockApi= null;

            if (listws_client != null && !listws_client.isEmpty()) {
               if (selectedWS_Client!=null){
                    String  client_name = selectedWS_Client.getClient_name();
                    for (WS_Client wsClient: listws_client){
                        if (wsClient.getClient_name().equals(client_name.trim())){
                            new_ws_client = wsClient;
                            break;
                        }
                    }
               }
             
            }

           if (listWS_Block_API != null && !listWS_Block_API.isEmpty()) {
               if (selectedWS_Block_API!=null){
                   String block_name = selectedWS_Block_API.getBlock_api_name();
                    for(WS_Block_API block_api:listWS_Block_API){
                       if (block_api.getBlock_api_name().equals(block_name.trim())){
                           new_blockApi = block_api;
                           break;
                       }
                    }
               }
               
            }
           
           if (new_ws_client!=null){
               selectedWS_AccessManagement.setWs_client(new_ws_client);
           }
           
           if (new_blockApi!=null){
               selectedWS_AccessManagement.setWs_block_api(new_blockApi);
           }

           try {
               java.sql.Timestamp start_t= null;
               java.sql.Timestamp end_t =null;
               
                if (start_time!=null){
                    start_t = new java.sql.Timestamp(start_time.getTime());
                }
                
                if (end_time!=null){
                    end_t = new java.sql.Timestamp(end_time.getTime());
                }
                
                selectedWS_AccessManagement.setStart_time(start_t);
                selectedWS_AccessManagement.setExpire_time(end_t);
                              
                ws_accessMgn_controller.edit(selectedWS_AccessManagement);
                listWS_AccessManagement = ws_accessMgn_controller.findWS_AccessManagementEntities();
                msg = new FacesMessage("Success Update", access_name);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  updating Row", access_name);
                e.printStackTrace();
            }   
        } else {
            msg = new FacesMessage("ERROR : you must provide Product", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedWS_AccessManagement = new WS_AccessManagement();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        
       

    }

    public String delete() {
        FacesMessage msg = null;
        String access_name = selectedWS_AccessManagement.getAccess_name().trim();
        WS_Client ws_client = selectedWS_AccessManagement.getWs_client();
//        WS_Webservice ws_webservice= selectedWS_AccessManagement.getWs_webservice();

        boolean mustEdit = false;
        
        if (ws_client != null ) {
           selectedWS_AccessManagement.setWs_client(null);
           mustEdit = true;
        }
        
//        if (ws_webservice != null ) {
////            selectedWS_AccessManagement.setWs_webservice(null);
//            mustEdit = true;
//        }

        if (mustEdit){
             try {
                ws_accessMgn_controller.edit(selectedWS_AccessManagement);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  delete Row", access_name);
                e.printStackTrace();
            }
        }
       
         
        try {
            ws_accessMgn_controller.destroy(selectedWS_AccessManagement.getWs_access_mng_id());
            listWS_AccessManagement = ws_accessMgn_controller.findWS_AccessManagementEntities();
            msg = new FacesMessage("Success Delete", access_name);
        } catch (Exception e) {
            msg = new FacesMessage("ERROR during  Cancellation Row", access_name);
            e.printStackTrace();
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedWS_AccessManagement = new WS_AccessManagement();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        
  
        return null;
    }

    public String onAddNew() {
        // Add one new car to the table:
        FacesMessage msg = null;
        if (selectedWS_AccessManagement != null ) {
            String access_name = selectedWS_AccessManagement.getAccess_name().trim();
           
            if (access_name == null || access_name.equals("")) {
                msg = new FacesMessage("You must provide Poduct Label", "");
            } else {
                WS_AccessManagement accesMgn = commun_controller.getOneByWS_AccessManagement(access_name);
                if (accesMgn != null) {
                    msg = new FacesMessage("This Product already exist", "");
                } else {
            
                        WS_Client new_ws_client = null;
                        WS_Block_API new_blockApi= null;

                        if (listws_client != null && !listws_client.isEmpty()) {
                           if (selectedWS_Client!=null){
                                String  client_name = selectedWS_Client.getClient_name();
                                for (WS_Client wsClient: listws_client){
                                    if (wsClient.getClient_name().equals(client_name.trim())){
                                        new_ws_client = wsClient;
                                        break;
                                    }
                                }
                           }

                        }

                       if (listWS_Block_API != null && !listWS_Block_API.isEmpty()) {
                           if (selectedWS_Block_API!=null){
                               String block_name = selectedWS_Block_API.getBlock_api_name();
                                for(WS_Block_API block_api:listWS_Block_API){
                                   if (block_api.getBlock_api_name().equals(block_name.trim())){
                                       new_blockApi = block_api;
                                       break;
                                   }
                                }
                           }

                        }

                       if (new_ws_client!=null){
                           selectedWS_AccessManagement.setWs_client(new_ws_client);
                       }

                       if (new_blockApi!=null){
                           selectedWS_AccessManagement.setWs_block_api(new_blockApi);
                       }

                       try {
                           
                            java.sql.Timestamp start_t= null;
                            java.sql.Timestamp end_t =null;

                             if (start_time!=null){
                                 start_t = new java.sql.Timestamp(start_time.getTime());
                             }

                             if (end_time!=null){
                                 end_t = new java.sql.Timestamp(end_time.getTime());
                             }

                             selectedWS_AccessManagement.setStart_time(start_t);
                             selectedWS_AccessManagement.setExpire_time(end_t);

                            ws_accessMgn_controller.create(selectedWS_AccessManagement);
                            listWS_AccessManagement = ws_accessMgn_controller.findWS_AccessManagementEntities();
                            msg = new FacesMessage("Success Create", access_name);
                        } catch (Exception e) {
                            msg = new FacesMessage("ERROR during  Create new Row", access_name);
                            e.printStackTrace();
                        } 
                }
             }
        } else {
            msg = new FacesMessage("ERROR : you must provide Product Code", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedWS_AccessManagement = new WS_AccessManagement();
        
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
       
        return null;
    }

    
}
