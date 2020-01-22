/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page;

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.registration.Product;
import com.tatsinktechnologic.entities.registration.Promotion;
import com.tatsinktechnologic.entities.registration.Service;
import com.tatsinktechnologic.dao_controller.ProductJpaController;
import com.tatsinktechnologic.dao_controller.PromotionJpaController;
import com.tatsinktechnologic.dao_controller.ServiceJpaController;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
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
public class ProductMB implements Serializable {

    private Product selectedProduct;
    private Product selectedProduct_cache;
    private List<Service> listservice;
    private Service selectedService;
    private List<Promotion> listpromotion;
    private Promotion selectedPromotion;
    private List<Product> listProduct;
    private String service_string;
    private String promotion_string;
    private boolean do_create;
    private boolean do_view;
    private boolean do_edit;
    private boolean do_reset;

    private Date start_time;
    private Date end_time;

    private Time reg_start_time;
    private Time reg_end_time;

    private int start_hour;
    private int start_min;
    private int start_sec;

    private int end_hour;
    private int end_min;
    private int end_sec;

    private int reg_start_hour;
    private int reg_start_min;
    private int reg_start_sec;

    private int reg_end_hour;
    private int reg_end_min;
    private int reg_end_sec;

    @Inject
    private Commun_Controller commun_controller;

    @Inject
    private ServiceJpaController service_controller;

    @Inject
    private ProductJpaController product_controller;

    @Inject
    private PromotionJpaController promotion_controller;

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public Product getSelectedProduct_cache() {
        return selectedProduct_cache;
    }

    public void setSelectedProduct_cache(Product selectedProduct_cache) {
        this.selectedProduct_cache = selectedProduct_cache;
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

    public Time getReg_start_time() {
        return reg_start_time;
    }

    public void setReg_start_time(Time reg_start_time) {
        this.reg_start_time = reg_start_time;
    }

    public Time getReg_end_time() {
        return reg_end_time;
    }

    public void setReg_end_time(Time reg_end_time) {
        this.reg_end_time = reg_end_time;
    }

    public List<Promotion> getListpromotion() {
        return listpromotion;
    }

    public void setListpromotion(List<Promotion> listpromotion) {
        this.listpromotion = listpromotion;
    }

    public Promotion getSelectedPromotion() {
        return selectedPromotion;
    }

    public void setSelectedPromotion(Promotion selectedPromotion) {
        this.selectedPromotion = selectedPromotion;
    }

    public List<Product> getListProduct() {
        return listProduct;
    }

    public void setListProduct(List<Product> listProduct) {
        this.listProduct = listProduct;
    }

    public String getService_string() {
        return service_string;
    }

    public void setService_string(String service_string) {
        this.service_string = service_string;
    }

    public String getPromotion_string() {
        return promotion_string;
    }

    public void setPromotion_string(String promotion_string) {
        this.promotion_string = promotion_string;
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

    public int getReg_start_hour() {
        return reg_start_hour;
    }

    public void setReg_start_hour(int reg_start_hour) {
        this.reg_start_hour = reg_start_hour;
    }

    public int getReg_start_min() {
        return reg_start_min;
    }

    public void setReg_start_min(int reg_start_min) {
        this.reg_start_min = reg_start_min;
    }

    public int getReg_start_sec() {
        return reg_start_sec;
    }

    public void setReg_start_sec(int reg_start_sec) {
        this.reg_start_sec = reg_start_sec;
    }

    public int getReg_end_hour() {
        return reg_end_hour;
    }

    public void setReg_end_hour(int reg_end_hour) {
        this.reg_end_hour = reg_end_hour;
    }

    public int getReg_end_min() {
        return reg_end_min;
    }

    public void setReg_end_min(int reg_end_min) {
        this.reg_end_min = reg_end_min;
    }

    public int getReg_end_sec() {
        return reg_end_sec;
    }

    public void setReg_end_sec(int reg_end_sec) {
        this.reg_end_sec = reg_end_sec;
    }

    @PostConstruct
    public void init() {
        listProduct = product_controller.findProductEntities();
        listservice = service_controller.findServiceEntities();

        listpromotion = promotion_controller.findPromotionEntities();
        Promotion prom = new Promotion();
        prom.setPromotion_name("-- none --");

        if (listpromotion != null) {
            listpromotion.add(0, prom);
        } else {
            listpromotion = new ArrayList<Promotion>();
            listpromotion.add(prom);
        }

        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;
        if (selectedProduct != null) {
            selectedService = commun_controller.getServiceByProduct(selectedProduct.getProduct_code());
            selectedPromotion = commun_controller.getPromotionByProduct(selectedProduct.getProduct_code());
        } else {
            selectedProduct = new Product();
        }
    }

    public void view(Product product) {
        selectedProduct = product;
        selectedProduct_cache = SerializationUtils.clone(product);
        selectedService = selectedProduct.getService();
        selectedPromotion = selectedProduct.getPromotion();

        start_time = selectedProduct.getStart_date();
        end_time = selectedProduct.getEnd_date();

        reg_start_time = selectedProduct.getStart_reg_time();
        reg_end_time = selectedProduct.getEnd_reg_time();

        if (start_time != null) {
            start_hour = start_time.getHours();
            start_min = start_time.getMinutes();
            start_sec = start_time.getSeconds();
        }

        if (end_time != null) {
            end_hour = end_time.getHours();
            end_min = end_time.getMinutes();
            end_sec = end_time.getSeconds();
        }

        if (reg_start_time != null) {
            reg_start_hour = reg_start_time.getHours();
            reg_start_min = reg_start_time.getMinutes();
            reg_start_sec = reg_start_time.getSeconds();
        }

        if (reg_end_time != null) {
            reg_end_hour = reg_end_time.getHours();
            reg_end_min = reg_end_time.getMinutes();
            reg_end_sec = reg_end_time.getSeconds();
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
        selectedProduct = selectedProduct_cache;
        selectedService = selectedProduct.getService();
        selectedPromotion = selectedProduct.getPromotion();
        listProduct = product_controller.findProductEntities();

    }

    public void clear() {

        selectedProduct = new Product();
        start_time = null;
        end_time = null;

        start_hour = 0;
        start_min = 0;
        start_sec = 0;

        end_hour = 0;
        end_min = 0;
        end_sec = 0;

        reg_start_hour = 0;
        reg_start_min = 0;
        reg_start_sec = 0;

        reg_end_hour = 0;
        reg_end_min = 0;
        reg_end_sec = 0;

        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public void edit() {
        FacesMessage msg = null;
        if (selectedProduct != null) {
            String product_code = selectedProduct.getProduct_code().trim();
            Service new_service = null;
            Promotion new_prom = null;

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

            if (listpromotion != null && !listpromotion.isEmpty()) {
                if (selectedPromotion != null) {
                    String promo_name = selectedPromotion.getPromotion_name();
                    for (Promotion prom : listpromotion) {
                        if (prom.getPromotion_name().equals(promo_name.trim())) {
                            new_prom = prom;
                            break;
                        }
                    }
                }

            }

            if (new_service != null) {
                selectedProduct.setService(new_service);
            }

            if (new_prom != null && !new_prom.getPromotion_name().equals("-- none --")) {
                selectedProduct.setPromotion(new_prom);
            } else {
                selectedProduct.setPromotion(null);
            }

            try {
                java.sql.Timestamp start_t = null;
                java.sql.Timestamp end_t = null;

                java.sql.Time reg_start_t = null;
                java.sql.Time reg_end_t = null;

                if (start_time != null) {
                    start_t = new java.sql.Timestamp(start_time.getTime());

                    start_t.setHours(start_hour);
                    start_t.setMinutes(start_min);
                    start_t.setSeconds(start_sec);

                    selectedProduct.setStart_date(start_t);

                }

                if (reg_start_hour != 0 || reg_start_min != 0 || reg_start_sec != 0) {
                    reg_start_t = new java.sql.Time(Calendar.getInstance().getTime().getTime());

                    reg_start_t.setHours(reg_start_hour);
                    reg_start_t.setMinutes(reg_start_min);
                    reg_start_t.setSeconds(reg_start_sec);

                    selectedProduct.setStart_reg_time(reg_start_t);
                }

                if (end_time != null) {
                    end_t = new java.sql.Timestamp(end_time.getTime());

                    end_t.setHours(end_hour);
                    end_t.setMinutes(end_min);
                    end_t.setSeconds(end_sec);
                    selectedProduct.setEnd_date(end_t);
                }

                if (reg_end_hour != 0 || reg_end_min != 0 || reg_end_sec != 0) {
                    reg_end_t = new java.sql.Time(Calendar.getInstance().getTime().getTime());

                    reg_end_t.setHours(reg_end_hour);
                    reg_end_t.setMinutes(reg_end_min);
                    reg_end_t.setSeconds(reg_end_sec);
                    selectedProduct.setEnd_reg_time(reg_end_t);
                }

                product_controller.edit(selectedProduct);
                listProduct = product_controller.findProductEntities();
                msg = new FacesMessage("Success Update", product_code);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  updating Row", product_code);
                e.printStackTrace();
            }
        } else {
            msg = new FacesMessage("ERROR : you must provide Product", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);

        selectedProduct = new Product();
        start_time = null;
        end_time = null;

        start_hour = 0;
        start_min = 0;
        start_sec = 0;

        end_hour = 0;
        end_min = 0;
        end_sec = 0;

        reg_start_hour = 0;
        reg_start_min = 0;
        reg_start_sec = 0;

        reg_end_hour = 0;
        reg_end_min = 0;
        reg_end_sec = 0;
        
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

    }

    public String delete() {
        FacesMessage msg = null;
        String product_code = selectedProduct.getProduct_code().trim();
        Service service = selectedProduct.getService();
        Promotion promo = selectedProduct.getPromotion();

        boolean mustEdit = false;

        if (service != null) {
            selectedProduct.setService(null);
            mustEdit = true;
        }

        if (promo != null) {
            selectedProduct.setPromotion(null);
            mustEdit = true;
        }

        if (mustEdit) {
            try {
                product_controller.edit(selectedProduct);
            } catch (Exception e) {
                msg = new FacesMessage("ERROR during  delete Row", product_code);
                e.printStackTrace();
            }
        }

        try {
            product_controller.destroy(selectedProduct.getProduct_id());
            listProduct = product_controller.findProductEntities();
            msg = new FacesMessage("Success Delete", product_code);
        } catch (Exception e) {
            msg = new FacesMessage("ERROR during  Cancellation Row", product_code);
            e.printStackTrace();
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);

        selectedProduct = new Product();
        start_time = null;
        end_time = null;

        start_hour = 0;
        start_min = 0;
        start_sec = 0;

        end_hour = 0;
        end_min = 0;
        end_sec = 0;

        reg_start_hour = 0;
        reg_start_min = 0;
        reg_start_sec = 0;

        reg_end_hour = 0;
        reg_end_min = 0;
        reg_end_sec = 0;
        
        
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

       

        return null;
    }

    public String onAddNew() {
        // Add one new car to the table:
        FacesMessage msg = null;
        if (selectedProduct != null) {
            String product_code = selectedProduct.getProduct_code().trim();

            if (product_code == null || product_code.equals("")) {
                msg = new FacesMessage("You must provide Poduct Label", "");
            } else {
                Product product = commun_controller.getOneByProduct(product_code);
                if (product != null) {
                    msg = new FacesMessage("This Product already exist", "");
                } else {

                    Service new_service = null;
                    Promotion new_prom = null;

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

                    if (listpromotion != null && !listpromotion.isEmpty()) {
                        if (selectedPromotion != null) {
                            String promo_name = selectedPromotion.getPromotion_name();
                            for (Promotion prom : listpromotion) {
                                if (prom.getPromotion_name().equals(promo_name.trim())) {
                                    new_prom = prom;
                                    break;
                                }
                            }
                        }

                    }

                    if (new_service != null) {
                        selectedProduct.setService(new_service);
                    }

                    if (new_prom != null && !new_prom.getPromotion_name().equals("-- none --")) {
                        selectedProduct.setPromotion(new_prom);
                    } else {
                        selectedProduct.setPromotion(null);
                    }

                    try {

                        java.sql.Timestamp start_t = null;
                        java.sql.Timestamp end_t = null;

                        java.sql.Time reg_start_t = null;
                        java.sql.Time reg_end_t = null;

                        if (start_time != null) {
                            start_t = new java.sql.Timestamp(start_time.getTime());

                            start_t.setHours(start_hour);
                            start_t.setMinutes(start_min);
                            start_t.setSeconds(start_sec);

                            selectedProduct.setStart_date(start_t);

                        }

                        if (reg_start_hour != 0 || reg_start_min != 0 || reg_start_sec != 0) {
                            reg_start_t = new java.sql.Time(Calendar.getInstance().getTime().getTime());

                            reg_start_t.setHours(reg_start_hour);
                            reg_start_t.setMinutes(reg_start_min);
                            reg_start_t.setSeconds(reg_start_sec);

                            selectedProduct.setStart_reg_time(reg_start_t);
                        }

                        if (end_time != null) {
                            end_t = new java.sql.Timestamp(end_time.getTime());

                            end_t.setHours(end_hour);
                            end_t.setMinutes(end_min);
                            end_t.setSeconds(end_sec);
                            selectedProduct.setEnd_date(end_t);
                        }

                        if (reg_end_hour != 0 || reg_end_min != 0 || reg_end_sec != 0) {
                            reg_end_t = new java.sql.Time(Calendar.getInstance().getTime().getTime());

                            reg_end_t.setHours(reg_end_hour);
                            reg_end_t.setMinutes(reg_end_min);
                            reg_end_t.setSeconds(reg_end_sec);
                            selectedProduct.setEnd_reg_time(reg_end_t);
                        }

                        product_controller.create(selectedProduct);
                        listProduct = product_controller.findProductEntities();
                        msg = new FacesMessage("Success Create", product_code);
                    } catch (Exception e) {
                        msg = new FacesMessage("ERROR during  Create new Row", product_code);
                        e.printStackTrace();
                    }
                }
            }
        } else {
            msg = new FacesMessage("ERROR : you must provide Product Code", "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);

        selectedProduct = new Product();
        start_time = null;
        end_time = null;

        start_hour = 0;
        start_min = 0;
        start_sec = 0;

        end_hour = 0;
        end_min = 0;
        end_sec = 0;

        reg_start_hour = 0;
        reg_start_min = 0;
        reg_start_sec = 0;

        reg_end_hour = 0;
        reg_end_min = 0;
        reg_end_sec = 0;
        
        do_create = true;
        do_view = false;
        do_edit = false;
        do_reset = true;

        return null;
    }

    public void validateRestrict(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value != null) {
            String restrictProduct = value.toString();
            if (!restrictProduct.trim().equals("")) {
                Pattern ptn = Pattern.compile("\\|");
                List<String> listProduc_resctrict = Arrays.asList(ptn.split(restrictProduct.toUpperCase().trim()));

                int i = 0;
                for (String restrict_prod : listProduc_resctrict) {
                    for (Product prod : listProduct) {
                        if (restrict_prod.trim().equals(prod.getProduct_code())) {
                            i++;
                            break;
                        }
                    }
                }

                if (i != listProduc_resctrict.size()) {
                    String message = context.getApplication().evaluateExpressionGet(context, "Restricted Product don't Exist", String.class);
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
                    throw new ValidatorException(msg);
                }
            }
        }

    }

    public void validateReg_Day(FacesContext context, UIComponent component, Object obj) throws ValidatorException {
        if (obj != null) {
            String reg_day = obj.toString();

            if (!reg_day.equals("")) {

                Pattern ptn = Pattern.compile("\\|");
                List<String> listreg_day = Arrays.asList(ptn.split(reg_day.toUpperCase().trim()));

                int i = 0;
                for (String invalue : listreg_day) {
                    if (!invalue.matches("\\d+")) {
                        break;
                    }
                    i++;
                }

                if (i != listreg_day.size()) {
                    String message = context.getApplication().evaluateExpressionGet(context, "Wrong Format. Please provide following format : day1|day2... (ie: 1|2|6) ", String.class);
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
                    throw new ValidatorException(msg);
                }

            }
        }

    }

    public void validatePending_Validity(FacesContext context, UIComponent component, Object obj) throws ValidatorException {
        if (obj != null) {
            String reg_day = obj.toString();

            if (!reg_day.equals("")) {
                if (reg_day.toUpperCase().startsWith("D")) {
                    String value = reg_day.replace("D", "");
                    try {
                        Integer.parseInt(value);
                    } catch (Exception e) {
                        String message = context.getApplication().evaluateExpressionGet(context, "Wrong Format. Please provide following format : D([1-9])* ", String.class);
                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
                        throw new ValidatorException(msg);
                    }
                } else if (reg_day.toUpperCase().startsWith("H")) {
                    String value = reg_day.replace("H", "");
                    try {
                        Integer.parseInt(value);
                    } catch (Exception e) {
                        String message = context.getApplication().evaluateExpressionGet(context, "Wrong Format. Please provide following format :  H([1-9])* ", String.class);
                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
                        throw new ValidatorException(msg);
                    }
                } else {
                    String message = context.getApplication().evaluateExpressionGet(context, "Wrong Format. Please provide following format : D([1-9])* or H([1-9])* ", String.class);
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
                    throw new ValidatorException(msg);
                }
            } else {
                String message = context.getApplication().evaluateExpressionGet(context, "Please provide following format : D([1-9])* or H([1-9])* ", String.class);
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
                throw new ValidatorException(msg);
            }
        }

    }

}
