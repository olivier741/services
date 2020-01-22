/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.registration.Promotion;
import com.tatsinktechnologic.dao_controller.PromotionJpaController;
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
 * @author olivier
 */

@Named
@ViewScoped
public class PromotionMB implements Serializable{
    
    private Promotion selectedPromotion;
    private Promotion selectedPromotion_cache;
    private List<Promotion> listPromotion;
    private boolean do_create;
    private boolean do_view;
    private boolean do_edit;
    private boolean do_reset;
    
    private Date start_time;
    private Date end_time;
    
    private int start_hour;
    private int start_min;
    private int start_sec;
    
    private int end_hour;
    private int end_min;
    private int end_sec;
           
    
    @Inject
    private Commun_Controller commun_controller;

    @Inject
    private PromotionJpaController promotion_controller;

    public Promotion getSelectedPromotion() {
        return selectedPromotion;
    }

    public void setSelectedPromotion(Promotion selectedPromotion) {
        this.selectedPromotion = selectedPromotion;
    }

    public Promotion getSelectedPromotion_cache() {
        return selectedPromotion_cache;
    }

    public void setSelectedPromotion_cache(Promotion selectedPromotion_cache) {
        this.selectedPromotion_cache = selectedPromotion_cache;
    }

    public List<Promotion> getListPromotion() {
        return listPromotion;
    }

    public void setListPromotion(List<Promotion> listPromotion) {
        this.listPromotion = listPromotion;
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

   

    public int getEnd_hour() {
        return end_hour;
    }

    public void setEnd_hour(int end_hour) {
        this.end_hour = end_hour;
    }

    public int getEnd_min() {
        return end_min;
    }

    public void setEnd_min(int end_min) {
        this.end_min = end_min;
    }

    public int getEnd_sec() {
        return end_sec;
    }

    public void setEnd_sec(int end_sec) {
        this.end_sec = end_sec;
    }

   
    
    @PostConstruct
    public void init() {
        listPromotion = promotion_controller.findPromotionEntities();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        if (selectedPromotion == null) {      
            selectedPromotion = new Promotion();
        }
    }
    
    
     public void view(Promotion promotion) {
        selectedPromotion = promotion;
        selectedPromotion_cache = SerializationUtils.clone(promotion);
        start_time = selectedPromotion.getStart_time();
        end_time = selectedPromotion.getEnd_time();
        
        if (start_time!= null){
            start_hour = start_time.getHours();
            start_min = start_time.getMinutes();
            start_sec=start_time.getSeconds();
        }
       
        if (end_time!=null){
            end_hour = end_time.getHours();
            end_min = end_time.getMinutes();
            end_sec = end_time.getSeconds();
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
        selectedPromotion = selectedPromotion_cache;
        listPromotion = promotion_controller.findPromotionEntities();

    }

  

    public void clear() {

        selectedPromotion = new Promotion();
        start_time = null;
        end_time = null;
        
        start_hour = 0;
        start_min = 0;
        start_sec=0;
        
        end_hour = 0;
        end_min = 0;
        end_sec = 0;
        
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;;

    }

    public void edit() {
        FacesMessage msg = null;
        if (selectedPromotion != null) {
            String promotion_name = selectedPromotion.getPromotion_name().trim();
                   
                    try {
                        if (start_time!= null){
                            java.sql.Timestamp start_t = new java.sql.Timestamp(start_time.getTime());
                       
                            start_t.setHours(start_hour);
                            start_t.setMinutes(start_min);
                            start_t.setSeconds(start_sec);
                            
                            selectedPromotion.setStart_time(start_t);
                        }
                        
                        if (end_time!=null){
                             java.sql.Timestamp end_t = new java.sql.Timestamp(end_time.getTime());
                            
                             end_t.setHours(end_hour);
                             end_t.setMinutes(end_min);
                             end_t.setSeconds(end_sec);
                             
                             selectedPromotion.setEnd_time(end_t);
                        }
                        
                        promotion_controller.edit(selectedPromotion);
                     
                         listPromotion = promotion_controller.findPromotionEntities();
                        msg = new FacesMessage("Success Update", promotion_name);
                    } catch (Exception e) {
                        msg = new FacesMessage("ERROR during  updating Row", promotion_name);
                        e.printStackTrace();
                    }
            
        } else {
            msg = new FacesMessage("ERROR : you must provide Role", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedPromotion= new Promotion();
        start_time = null;
        end_time = null;
        
        start_hour = 0;
        start_min = 0;
        start_sec=0;
        
        end_hour = 0;
        end_min = 0;
        end_sec = 0;
        
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public String delete() {
        FacesMessage msg = null;
        String promotion_name = selectedPromotion.getPromotion_name().trim();
 
        try {
            promotion_controller.destroy(selectedPromotion.getPromotion_id());

            listPromotion = promotion_controller.findPromotionEntities();
            msg = new FacesMessage("Success Delete", promotion_name);
        } catch (Exception e) {
            msg = new FacesMessage("ERROR during  Cancellation Row", promotion_name);
            e.printStackTrace();
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedPromotion= new Promotion();
        start_time = null;
        end_time = null;
        
        start_hour = 0;
        start_min = 0;
        start_sec=0;
        
        end_hour = 0;
        end_min = 0;
        end_sec = 0;
        
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }

    public String onAddNew() {
        // Add one new car to the table:
        FacesMessage msg = null;
        if (selectedPromotion != null) {
            String promotion_name = selectedPromotion.getPromotion_name().trim();
            if (promotion_name == null || promotion_name.equals("")) {
                msg = new FacesMessage("You must provide Service Name", "");
            } else {
                Promotion promotion = commun_controller.getOneByPromotion(promotion_name);
                if (promotion != null) {
                    msg = new FacesMessage("This Promotion already exist", "");
                } else {
                    try {
                        
                        java.sql.Timestamp start_t = new java.sql.Timestamp(start_time.getTime());
                        java.sql.Timestamp end_t = new java.sql.Timestamp(end_time.getTime());
                        
                        start_t.setHours(start_hour);
                        start_t.setMinutes(start_min);
                        start_t.setSeconds(start_sec);
                        
                        end_t.setHours(end_hour);
                        end_t.setMinutes(end_min);
                        end_t.setSeconds(end_sec);
                        
                        selectedPromotion.setStart_time(start_t);
                        selectedPromotion.setEnd_time(end_t);
                        
                        promotion_controller.create(selectedPromotion);

                        msg = new FacesMessage("New Promotion added", promotion_name);
                        listPromotion = promotion_controller.findPromotionEntities();
                        
                    } catch (Exception e) {
                        msg = new FacesMessage("ERROR during add row", promotion_name);
                        e.printStackTrace();
                    }

                }

            }
        } else {
            msg = new FacesMessage("ERROR : you must provide User", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedPromotion = new Promotion();
        start_time = null;
        end_time = null;
        
        start_hour = 0;
        start_min = 0;
        start_sec=0;
        
        end_hour = 0;
        end_min = 0;
        end_sec = 0;
        
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        return null;
    }
    
   
}