/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.registration.Action;
import com.tatsinktechnologic.entities.registration.Product;
import com.tatsinktechnologic.dao_controller.ActionJpaController;
import com.tatsinktechnologic.dao_controller.ProductJpaController;
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
public class ActionMB implements Serializable {
    
    private Action selectedAction;
    private Action selectedAction_cache;
    private List<Product> listproduct;
    private Product selectedProduct;
     private List<Action> listAction;

    private String product_string;
    private boolean do_create;
    private boolean do_view;
    private boolean do_edit;
    private boolean do_reset;
    
    
     @Inject
    private Commun_Controller commun_controller;
    
     @Inject
    private ActionJpaController action_controller;

    @Inject
    private ProductJpaController product_controller;

    public Action getSelectedAction() {
        return selectedAction;
    }

    public void setSelectedAction(Action selectedAction) {
        this.selectedAction = selectedAction;
    }

    public Action getSelectedAction_cache() {
        return selectedAction_cache;
    }

    public void setSelectedAction_cache(Action selectedAction_cache) {
        this.selectedAction_cache = selectedAction_cache;
    }

    public List<Product> getListproduct() {
        return listproduct;
    }

    public void setListproduct(List<Product> listproduct) {
        this.listproduct = listproduct;
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public String getProduct_string() {
        return product_string;
    }

    public void setProduct_string(String product_string) {
        this.product_string = product_string;
    }

    public List<Action> getListAction() {
        return listAction;
    }

    public void setListAction(List<Action> listAction) {
        this.listAction = listAction;
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
        listAction = action_controller.findActionEntities();
        listproduct = product_controller.findProductEntities();
    
        
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        if (selectedAction != null) {
            selectedProduct = commun_controller.getProductByAction(selectedAction.getAction_name());
            
        } else {
            selectedAction = new Action();
        }
    }
    
       public String getProduct_Action(String action_name) {
        Product product =  commun_controller.getProductByAction(action_name);
        String result = product.getProduct_code();
        product_string = result;
        return result;
    }
    
    
      public void view(Action action) {
        selectedAction = action;
        selectedAction_cache = SerializationUtils.clone(action);
        selectedProduct = selectedAction.getProduct();
       

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
        selectedAction = selectedAction_cache;
        selectedProduct = selectedAction.getProduct();
        listAction = action_controller.findActionEntities();

    }

    
    
    public void clear() {

        selectedAction = new Action();
        
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;;

    }
    
    
    
        public void edit() {
        FacesMessage msg = null;
        if (selectedAction != null) {
            String action_name = selectedAction.getAction_name().trim();
            Product new_product = null;

            if (listproduct != null && !listproduct.isEmpty()) {
               if (selectedProduct!=null){
                    String  prod_code = selectedProduct.getProduct_code();
                    for (Product prod: listproduct){
                        if (prod.getProduct_code().equals(prod_code.trim())){
                            new_product = prod;
                            break;
                        }
                    }
               }
             
            }

          
           if (new_product!=null){
               selectedAction.setProduct(new_product);
           }
           
          
           try {
              
                
                action_controller.edit(selectedAction);
                listAction = action_controller.findActionEntities();
                msg = new FacesMessage("Success Update", action_name);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  updating Row", action_name);
                e.printStackTrace();
            }   
        } else {
            msg = new FacesMessage("ERROR : you must provide Product", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedAction = new Action();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        
    }

    public String delete() {
        FacesMessage msg = null;
        String action_name = selectedAction.getAction_name().trim();
        Product product = selectedAction.getProduct();
        

        if (product != null ) {
            selectedAction.setProduct(null);
        
            try {
                action_controller.edit(selectedAction);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  delete Row", action_name);
                e.printStackTrace();
            }
        
        }
         
        try {
            action_controller.destroy(selectedAction.getAction_id());
            listAction = action_controller.findActionEntities();
            msg = new FacesMessage("Success Delete", action_name);
        } catch (Exception e) {
            msg = new FacesMessage("ERROR during  Cancellation Row", action_name);
            e.printStackTrace();
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedAction = new Action();
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        
       
        return null;
    }

    public String onAddNew() {
        // Add one new car to the table:
        FacesMessage msg = null;
        if (selectedAction != null && selectedAction.getAction_name() != null) {
            String action_name = selectedAction.getAction_name().trim();
            Product new_product = null;

            if (listproduct != null && !listproduct.isEmpty()) {
               if (selectedProduct!=null){
                    String  prod_code = selectedProduct.getProduct_code();
                    for (Product prod: listproduct){
                        if (prod.getProduct_code().equals(prod_code.trim())){
                            new_product = prod;
                            break;
                        }
                    }
               }
             
            }

          
           if (new_product!=null){
               selectedAction.setProduct(new_product);
           }


           try {

                action_controller.create(selectedAction);
                listAction = action_controller.findActionEntities();
                msg = new FacesMessage("Success Create", action_name);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  Create new Row", action_name);
                e.printStackTrace();
            }   
        } else {
            msg = new FacesMessage("ERROR : you must provide Product Code", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        selectedAction = new Action();
        
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
   
        
        return null;
    }


    
}
