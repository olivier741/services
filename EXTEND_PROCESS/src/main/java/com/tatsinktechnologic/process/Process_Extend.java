/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.process;

import com.tatsinktechnologic.beans.Process_Request;
import com.tatsinktechnologic.beans_entity.Command;

import com.tatsinktechnologic.beans_entity.Product;
import com.tatsinktechnologic.beans_entity.Promotion;
import com.tatsinktechnologic.beans_entity.Reduction_Type;
import com.tatsinktechnologic.beans_entity.Register;
import com.tatsinktechnologic.dao_commun_repository.CommunRepository;
import com.tatsinktechnologic.loadconfig.Load_Configuration;
import com.tatsinktechnologic.dao_repository.RegisterJpaController;
import com.tatsinktechnologic.resfull.bean.WS_Block_Response;
import com.tatsinktechnologic.resfull.bean.WS_Response;
import com.tatsinktechnologic.resfull.client.Webservice_Charge;
import com.tatsinktechnologic.sender.Sender;
import com.tatsinktechnologic.util.Utils;
import com.tatsinktechnologic.xml.Application;
import com.tatsinktechnologic.xml.ProcessThread_Ext;
import java.net.InetAddress;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
import javax.persistence.EntityManagerFactory;
import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author olivier
 */
public class Process_Extend implements Runnable {

    private static Logger logger = Logger.getLogger(Process_Extend.class);

    private static Load_Configuration commonConfig = Load_Configuration.getConfigurationLoader();

    private static EntityManagerFactory emf;
    private static int sleep_duration;
    private static Application app_conf;
    private static ProcessThread_Ext procThreadConf;
    private static InetAddress address;
    private static HashMap<String, Product> SETPRODUCT;
    private static HashMap<String, Command> SETCOMMAND;

    private CommunRepository communRepo;
    private int index_id;
    private int maxrow;

    public Process_Extend(int maxrow, int index_id) {
        this.emf = commonConfig.getEmf();
        this.index_id = index_id;
        this.maxrow = maxrow;
        this.app_conf = commonConfig.getApp_conf();
        this.procThreadConf = app_conf.getProcess_thread_extend();
        this.sleep_duration = procThreadConf.getSleep_duration();
        SETPRODUCT = commonConfig.getSETPRODUCT();
        SETCOMMAND = commonConfig.getSETCOMMAND();
        address = Utils.gethostName();
        communRepo = new CommunRepository(emf);
    }

    @Override
    public void run() {

        logger.info("################################## START PROCESS REGISTER ###########################");
        while (true) {
            // Removing an element from the Queue using poll()
            // The poll() method returns null if the Queue is empty.
            Process_Request process_mo = new Process_Request();
            Timestamp star_time = new Timestamp(System.currentTimeMillis());
            List<Register> listRegister = communRepo.getExtendRegister(maxrow, index_id, star_time);

            if (listRegister != null && !listRegister.isEmpty()) {

                for (Register register : listRegister) {

                    String msisdn = register.getMsisdn();
                    String transaction_id = register.getTransaction_id();
                    Product product = register.getProduct();
                    String product_code = product.getProduct_code();
                    String exchange_mode = register.getExchange_mode();
                    Timestamp receive_time = star_time;

                    String api_error = "";
                    String api_desc_error = "";
                    int useproduct = -1;

                    int charge_status = 0;
                    Timestamp charge_time = null;
                    String mo_his_desc = "";

                    if (!StringUtils.isBlank(product_code)) {
                        product = SETPRODUCT.get(product_code);
                        // get restric offer
                        List<String> listRestric_product = null;
                        if (!StringUtils.isBlank(product.getRestrict_product())) {
                            Pattern ptn = Pattern.compile("\\|");
                            listRestric_product = Arrays.asList(ptn.split(product.getRestrict_product().toUpperCase().trim()));
                        }

                        // get day of registration in the week
                        List<String> listRegDay = null;
                        if (!StringUtils.isBlank(product.getRegister_day())) {
                            Pattern ptn = Pattern.compile("\\|");
                            listRegDay = Arrays.asList(ptn.split(product.getRegister_day().trim()));
                        }

                        // get live duration of offer
                        Timestamp prod_start_date = product.getStart_date();
                        Timestamp prod_end_date = product.getEnd_date();

                        // get time of registration in the day
                        Time prod_start_day_time = product.getStart_reg_time();
                        Time prod_end_day_time = product.getEnd_reg_time();

                        boolean isextend = product.isIsextend();
                        boolean isOveride = product.isIsOveride_reg();
                        String validity = product.getValidity();

                        String pending_duration = product.getPending_duration();
                        Timestamp cancelTime = getExpire_Time(pending_duration, register.getExpire_time());

                        Calendar c = Calendar.getInstance();
                        c.setTime(receive_time);
                        String dayOfWeek = String.valueOf(c.get(Calendar.DAY_OF_WEEK));

                        Timestamp expire_time = getExpire_Time(validity, receive_time);

                        useproduct = 0;

                        if (prod_start_date != null && prod_end_date != null) {
                            if (prod_start_date.after(prod_end_date)) {
                                useproduct = 1;               // start time is after end time. wrong time configuration
                            } else {
                                if (prod_start_date.after(receive_time)) {
                                    useproduct = 2;            // start time is after receive time customer cannot register to product. product not available.
                                }
                                if (prod_end_date.before(receive_time)) {
                                    useproduct = 3;            // end time is before receive time customer cannot register to product. product is expire
                                }
                            }
                        } else if (prod_start_date != null) {
                            if (prod_start_date.after(receive_time)) {
                                useproduct = 2;            // start time is after receive time customer cannot register to product. product not available.
                            }
                        } else if (prod_end_date != null) {
                            if (prod_end_date.before(receive_time)) {
                                useproduct = 3;            // end time is before receive time customer cannot register to product. product is expire
                            }
                        }

                        if (useproduct == 0) {
                            if (listRegDay != null && !listRegDay.isEmpty() && !listRegDay.contains(dayOfWeek)) {
                                useproduct = 4;                   // customer cannot register to this offer on that day
                            } else {
                                if (prod_start_day_time != null && prod_end_day_time != null) {
                                    if (prod_start_day_time.after(prod_end_day_time)) {
                                        useproduct = 5;               // start time is after end time. wrong time configuration
                                    } else {
                                        if (prod_start_day_time.after(new Time(receive_time.getTime()))) {
                                            useproduct = 6;            // start time is after receive time customer cannot register to product. product not available on that hour.
                                        }
                                        if (prod_end_day_time.before(new Time(receive_time.getTime()))) {
                                            useproduct = 7;            // end time is before receive time customer cannot register to product. product not available on that hour.
                                        }
                                    }
                                } else if (prod_start_day_time != null) {
                                    if (prod_start_day_time.after(new Time(receive_time.getTime()))) {
                                        useproduct = 6;            // start time is after receive time customer cannot register to product. product not available on that hour.
                                    }
                                } else if (prod_end_day_time != null) {
                                    if (prod_end_day_time.before(new Time(receive_time.getTime()))) {
                                        useproduct = 7;            // end time is before receive time customer cannot register to product. product not available on that hour.
                                    }
                                }

                            }
                        }

                        if (useproduct == 0) {

                            if (listRestric_product != null && !listRestric_product.isEmpty()) {
                                if (communRepo.checkRestrictProduct(msisdn, listRestric_product)) {
                                    useproduct = 8;    // customer already register to restricted offer
                                }
                            }
                        }

                        if (useproduct == 0) {

                            if (register.getStatus()==1) {
                                useproduct = 19;  // customer is active
                            }

                        }
                        
                         if (useproduct == 0) {

                            if (receive_time.before(cancelTime)) {
                                useproduct = 18;  // customer will be cancal due to expiration of pending duration
                            }

                        }

                        if (useproduct == 0) {

                            Promotion promo = product.getPromotion();

                            if (promo != null) {  // offer have promotion

                                Timestamp promo_start_time = promo.getStart_time();
                                Timestamp promo_end_time = promo.getEnd_time();

                                if (promo_start_time != null && promo_end_time != null) {
                                    if (promo_start_time.after(promo_end_time)) {
                                        logger.warn("WRONG TIME CONFIGURATION OF THIS PROMOTION ");   // start time is after end time wrong time configuration     
                                        logger.warn("SYSTEM WILL NOT TAKE CARE THIS PROMOTION :" + promo);
                                        useproduct = 9;
                                    } else {
                                        if (promo_start_time.after(receive_time)) {
                                            logger.warn("PROMOTION IS NOT AVAILABLE ");                   // start time is after receive time customer cannot register to promotion. promotion not available.
                                            logger.warn("SYSTEM WILL NOT TAKE CARE THIS PROMOTION :" + promo);
                                            useproduct = 10;
                                        }
                                        if (promo_end_time.before(receive_time)) {
                                            logger.warn("PROMOTION IS EXPIRE");
                                            logger.warn("SYSTEM WILL NOT TAKE CARE THIS PROMOTION :" + promo);
                                            useproduct = 11;            // end time is before receive time customer cannot register to promotion. promotion is expire
                                        }
                                    }
                                } else if (promo_start_time != null) {
                                    if (promo_start_time.after(receive_time)) {
                                        logger.warn("PROMOTION IS NOT AVAILABLE ");                   // start time is after receive time customer cannot register to promotion. promotion not available.
                                        logger.warn("SYSTEM WILL NOT TAKE CARE THIS PROMOTION :" + promo);
                                        useproduct = 10;
                                    }
                                } else if (promo_end_time != null) {
                                    if (promo_end_time.before(receive_time)) {
                                        logger.warn("PROMOTION IS EXPIRE");                         // end time is before receive time customer cannot register to promotion. promotion is expire
                                        logger.warn("SYSTEM WILL NOT TAKE CARE THIS PROMOTION :" + promo);
                                        useproduct = 11;
                                    }
                                }

                                if (useproduct == 0) {
                                    if (!communRepo.authorizationPromo(promo, msisdn.trim())) {
                                        useproduct = 12;         // customer cannot register to promotion. its phone number not obey to promotion restriction policy 
                                    } else {

                                        if (product.getExtend_fee() > 0 || promo.getPromotion_reg_fee() > 0) {
                                            Webservice_Charge web_service = new Webservice_Charge();
                                            try {
                                                WS_Block_Response ws_resp = web_service.requestCharge_Product(msisdn, transaction_id, product);

                                                if (ws_resp != null) {
                                                    api_error = ws_resp.getListws_response().get(0).getAPI_GW_Error();
                                                    api_desc_error = ws_resp.getListws_response().get(0).getAPI_GW_Description();
                                                } else {
                                                    logger.error("API-GATEWAY Authentication issue ");
                                                }

                                            } catch (Exception e) {
                                                useproduct = 16;
                                                logger.error("API error : ", e);
                                            }
                                        }

                                        if (useproduct == 0) {
                                            if (!api_error.equals("00") && !api_error.equals("")) {
                                                if (api_error.equals("66")) {  // customer is block
                                                    useproduct = 15;
                                                } else if (api_error.equals("33")) { // not enough money
                                                    useproduct = 17;
                                                } else {
                                                    useproduct = 13;
                                                }
                                            } else {
                                                if (expire_time == null) {
                                                    useproduct = 14;
                                                }
                                            }
                                        }

                                    }
                                } else {
                                    if (product.getExtend_fee() > 0) {
                                        Webservice_Charge web_service = new Webservice_Charge();
                                        try {
                                            WS_Block_Response ws_resp = web_service.requestCharge_Product(msisdn, transaction_id, product);

                                            if (ws_resp != null) {
                                                api_error = ws_resp.getListws_response().get(0).getAPI_GW_Error();
                                                api_desc_error = ws_resp.getListws_response().get(0).getAPI_GW_Description();
                                            } else {
                                                logger.error("API-GATEWAY Authentication issue ");
                                            }

                                        } catch (Exception e) {
                                            useproduct = 16;
                                            logger.error("API error : ", e);
                                        }

                                    }

                                    if (useproduct == 0) {
                                        if (!api_error.equals("00") && !api_error.equals("")) {
                                            if (api_error.equals("66")) {  // customer is block
                                                useproduct = 15;
                                            } else if (api_error.equals("33")) { // not enough money
                                                useproduct = 17;
                                            } else {
                                                useproduct = 13;
                                            }
                                        } else {
                                            if (expire_time == null) {
                                                useproduct = 14;
                                            }
                                        }
                                    }

                                }
                            } else {  // offer don't have promotion
                                if (product.getExtend_fee() > 0) {
                                    Webservice_Charge web_service = new Webservice_Charge();
                                    try {
                                        WS_Block_Response ws_resp = web_service.requestCharge_Product(msisdn, transaction_id, product);

                                        if (ws_resp != null) {
                                            api_error = ws_resp.getListws_response().get(0).getAPI_GW_Error();
                                            api_desc_error = ws_resp.getListws_response().get(0).getAPI_GW_Description();
                                        } else {
                                            logger.error("API-GATEWAY Authentication issue ");
                                        }

                                    } catch (Exception e) {
                                        useproduct = 16;
                                        logger.error("API error : ", e);
                                    }

                                }

                                if (useproduct == 0) {
                                    if (!api_error.equals("00") && !api_error.equals("")) {
                                        if (api_error.equals("66")) {  // customer is block
                                            useproduct = 15;
                                        } else if (api_error.equals("33")) { // not enough money
                                            useproduct = 17;
                                        } else {
                                            useproduct = 13;
                                        }
                                    } else {
                                        if (expire_time == null) {
                                            useproduct = 14;
                                        }
                                    }
                                }

                            }

                        }

                        RegisterJpaController regCont = null;

                        switch (useproduct) {
                            case 0:

                                int numberReg = register.getNumber_reg();
                                register.setNumber_reg(numberReg + 1);

                                register.setAutoextend(isextend);
                                register.setExpire_time(expire_time);
                                register.setRenew_time(receive_time);
                                register.setMsisdn(msisdn);
                                register.setStatus(1);
                                register.setTransaction_id(transaction_id);
                                register.setExchange_mode(exchange_mode);

                                regCont = new RegisterJpaController(emf);

                                if (isOveride) {
                                    try {
                                        regCont.edit(register);
                                        process_mo.setNotification_code("EXT-PRODUCT-SUCCESS-OVERIDE-" + product_code);
                                        mo_his_desc = api_desc_error;
                                    } catch (Exception e) {
                                        logger.error("Customer don't have account of this Offer. Cannot Edit", e);
                                    }
                                } else {
                                    process_mo.setNotification_code("EXT-PRODUCT-NOT-OVERIDE-" + product_code);
                                    mo_his_desc = "CUSTOMER ALREADY REGISTER TO THIS OFFER: CANNOT OVERIDE";
                                }

                                charge_status = 0;
                                break;
                            case 1:
                                process_mo.setNotification_code("EXT-PRODUCT-WRONG-TIME-" + product_code);
                                mo_his_desc = "WRONG TIME CONFIGURATION OF THIS PRODUCT";
                                charge_status = 1;
                                break;
                            case 2:
                                process_mo.setNotification_code("EXT-PRODUCT-NOT-AVAILABLE-" + product_code);
                                mo_his_desc = "PRODUCT IS NOT AVAILABLE";
                                charge_status = 2;
                                break;
                            case 3:
                                process_mo.setNotification_code("EXT-PRODUCT-EXPIRE-" + product_code);
                                mo_his_desc = "PRODUCT IS EXPIRE";
                                charge_status = 3;
                                break;
                            case 4:
                                process_mo.setNotification_code("EXT-PRODUCT-INVALID-DAY-" + product_code);
                                mo_his_desc = "PRODUCT IS NOT VALID THIS DAY";
                                charge_status = 4;
                                break;
                            case 5:
                                process_mo.setNotification_code("EXT-PRODUCT-WRONG-HOUR-" + product_code);
                                mo_his_desc = "WRONG HOUR CONFIGURATION OF THIS PRODUCT";
                                charge_status = 5;
                                break;
                            case 6:
                                process_mo.setNotification_code("EXT-PRODUCT-NOT-AVAILABLE-HOUR-" + product_code);
                                mo_his_desc = "PRODUCT IS NOT AVAILABLE THIS HOUR";
                                charge_status = 6;
                                break;
                            case 7:
                                process_mo.setNotification_code("EXT-PRODUCT-EXPIRE-HOUR-" + product_code);
                                mo_his_desc = "PRODUCT IS EXPIRE AT THIS HOUR";
                                charge_status = 7;
                                break;
                            case 8:
                                process_mo.setNotification_code("EXT-PRODUCT-RESTRICTION-EXIST-" + product_code);
                                mo_his_desc = "CUSTOMER HAS REGISTER TO RESTRICT PRODUCT";
                                charge_status = 8;
                                break;
                            case 12:
                                process_mo.setNotification_code("REG-PRODUCT-PROMO-NOT-ALLOW-" + product_code);
                                mo_his_desc = "CUSTOMER NOT ALLOW TO GET PROMOTION";
                                charge_status = 12;
                                break;
                            case 13:
                                process_mo.setNotification_code("EXT-PRODUCT-WRONG-CHARGE-" + product_code);
                                mo_his_desc = "CUSTOMER NOT CHARGE";
                                charge_status = 13;
                                break;
                            case 14:
                                process_mo.setNotification_code("EXT-PRODUCT-WRONG-EXPIRRE_DATE-" + product_code);
                                mo_his_desc = "SYSTEM ERROR : CANNOT GET EXPIRE TIME OF PROMOTION";
                                charge_status = 14;
                                break;
                            case 15:
                                register.setStatus(-1);
                                register.setCancel_time(receive_time);
                                regCont = new RegisterJpaController(emf);
                                try {
                                    regCont.edit(register);
                                } catch (Exception e) {
                                    logger.error("Customer don't have account of this Offer. Cannot Edit", e);
                                }

                                process_mo.setNotification_code("EXT-PRODUCT-CANCEL" + product_code);
                                mo_his_desc = "CUSTOMER IS BLOCK IN NETWORK | " + api_desc_error;
                                charge_status = 15;
                                break;
                            case 16:
                                process_mo.setNotification_code("EXT-PRODUCT-WRONG-API-CONNECTION-" + product_code);
                                mo_his_desc = "Charging API Connection refused";
                                charge_status = 16;
                                break;
                            case 17:
                                process_mo.setNotification_code("EXT-PRODUCT-NOT-MONEY-" + product_code);
                                mo_his_desc = "CUSTOMER NOT ENOUGH MONEY";
                                charge_status = 17;
                                break;

                            case 18:

                                register.setStatus(-1);
                                register.setCancel_time(receive_time);
                                regCont = new RegisterJpaController(emf);
                                try {
                                    regCont.edit(register);
                                } catch (Exception e) {
                                    logger.error("Customer don't have account of this Offer. Cannot Edit", e);
                                }

                                process_mo.setNotification_code("EXT-PRODUCT-CANCEL" + product_code);
                                mo_his_desc = "CUSTOMER IS BLOCK IN NETWORK | " + api_desc_error;
                                charge_status = 18;
                                break;
                        }

                    } else {
                        process_mo.setNotification_code("REG-PRODUCT-NOT-EXIST");
                        mo_his_desc = "PRODUCT NOT EXIST";
                        charge_status = -1;
                    }

                    // send to sender
                    Sender.addMo_Queue(process_mo);

                    try {
                        Thread.sleep(sleep_duration);
                    } catch (Exception e) {
                    }

                }
            }
        }
    }

    private Timestamp getExpire_Time(String validity, Timestamp current_time) {
        Timestamp result = null;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(current_time.getTime());

        if (validity.toUpperCase().startsWith("D")) {
            String value = validity.replace("D", "");
            try {
                int nbDay = Integer.parseInt(value);
                cal.add(Calendar.DAY_OF_MONTH, nbDay);
                result = new Timestamp(cal.getTime().getTime());
            } catch (Exception e) {

            }
        } else if (validity.toUpperCase().startsWith("H")) {
            String value = validity.replace("H", "");
            try {
                int nbHour = Integer.parseInt(value);
                cal.add(Calendar.HOUR_OF_DAY, nbHour);
                result = new Timestamp(cal.getTime().getTime());
            } catch (Exception e) {

            }
        }
        return result;
    }

    public static void executeRunnables(final ExecutorService service, List<Runnable> runnables) {
        //On exécute chaque "Runnable" de la liste "runnables"
        for (Runnable r : runnables) {

            service.execute(r);
        }
        service.shutdown();
    }

}
