/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.utils;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.registration.Product;
import com.tatsinktechnologic.entities.registration.Promotion;
import com.tatsinktechnologic.entities.registration.Service;
import com.tatsinktechnologic.dao_controller.ProductJpaController;
import com.tatsinktechnologic.dao_controller.PromotionJpaController;
import com.tatsinktechnologic.dao_controller.ServiceJpaController;
import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;

/**
 *
 * @author olivier.tatsinkou
 */
public class LoadConfig_DB implements Serializable{

    private List<Service> listservice;
    private List<Promotion> listpromotion;
    private List<Product> listProduct;
        
    @Inject
    private ServiceJpaController service_controller;

    @Inject
    private ProductJpaController product_controller;
    
    @Inject
    private PromotionJpaController promotion_controller;
     
    private LoadConfig_DB() {
        listservice = service_controller.findServiceEntities();
        listProduct = product_controller.findProductEntities();
        listpromotion = promotion_controller.findPromotionEntities();
    }
    
    
    public void loadDB_Service(){
        listservice = service_controller.findServiceEntities();
    }
    
    public void loadDB_Product(){
        listProduct = product_controller.findProductEntities();
    }
    
    public void loadDB_Promotion(){
        listpromotion = promotion_controller.findPromotionEntities();
    }
     
    private static class SingletonCommunDAO{
        private static final LoadConfig_DB _loadConfig_DB= new LoadConfig_DB();
    }
    
    public static LoadConfig_DB getConfigurationLoader(){
        return SingletonCommunDAO._loadConfig_DB;
    }
    
    public Service getOneByService(String value){
        Service result = null;
        for(Service entity : listservice){
            if (entity.getService_name().equals(value)){
                result = entity;
                break;
            }
        }
        return result;
    }
    
     public Promotion getOneByPromotion(String value){
        Promotion result = null;
        for(Promotion entity : listpromotion){
            if (entity.getPromotion_name().equals(value)){
                result = entity;
                break;
            }
        }
        return result;
    }
    
    public Service getServiceByProduct(String value){
       Service result = null;
        for(Product entity : listProduct){
            if (entity.getProduct_code().equals(value)){
                result = entity.getService();
                break;
            }
        }
        return result;
    }
    
     public Promotion getPromotionByProduct(String value){
       Promotion result = null;
        for(Product entity : listProduct){
            if (entity.getProduct_code().equals(value)){
                result = entity.getPromotion();
                break;
            }
        }
        return result;
    }

    public List<Service> getListservice() {
        return listservice;
    }

    public List<Promotion> getListpromotion() {
        return listpromotion;
    }

    public List<Product> getListProduct() {
        return listProduct;
    }

    
    
}
