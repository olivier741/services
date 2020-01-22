/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.process;

import com.tatsinktechnologic.beans.Process_Request;
import com.tatsinktechnologic.dao_commun_repository.CommunRepository;
import com.tatsinktechnologic.dao_repository.ServiceHistoryJpaController;
import com.tatsinktechnologic.entity.register.Notification_Conf;
import com.tatsinktechnologic.entity.register.Product;
import com.tatsinktechnologic.entity.register.Register;
import com.tatsinktechnologic.entity.register.Service;
import com.tatsinktechnologic.entity.sms_chat.ContentMessage;
import com.tatsinktechnologic.entity.sms_chat.ServiceHistory;
import com.tatsinktechnologic.loadconfig.Load_Configuration;
import com.tatsinktechnologic.sender.Sender;
import com.tatsinktechnologic.util.Utils;
import com.tatsinktechnologic.xml.Application;
import com.tatsinktechnologic.xml.Chat_Process;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import javax.persistence.EntityManagerFactory;
import org.apache.log4j.Logger;

/**
 *
 * @author olivier.tatsinkou
 */
public class SMSProcess_Notification implements Runnable {

    private static Logger logger = Logger.getLogger(SMSProcess_Notification.class);

    private static Load_Configuration commonConfig = Load_Configuration.getConfigurationLoader();

    private static EntityManagerFactory emf;
    private static int sleep_duration;
    private static Application app_conf;
    private static InetAddress address;
    private static HashMap<String, Product> SETPRODUCT;
    private static HashMap<String, Notification_Conf> SETNOTIFICATION ;
    private static HashMap<String, Service> SETSERVICE ;

    private CommunRepository communRepo;
    private int index_id;
    private int maxrow;
    private Chat_Process chatProc;

    public SMSProcess_Notification(int index_id, int maxrow, Chat_Process chatProc, EntityManagerFactory emf) {
        this.emf = commonConfig.getEmf();
        this.SETPRODUCT = commonConfig.getSETPRODUCT();
        this.SETSERVICE=commonConfig.getSETSERVICE();
        this.index_id = index_id;
        this.maxrow = maxrow;
        this.app_conf = commonConfig.getApp_conf();
        this.chatProc = chatProc;
        this.sleep_duration = chatProc.getSleep_duration();
        this.address = Utils.gethostName();
        this.communRepo = new CommunRepository(emf);
    }

    @Override
    public void run() {

        logger.info("################################## START PROCESS SMS NOTIFICATION ###########################");
        String service_name = chatProc.getService_name().toUpperCase().trim();
        Service serv = SETSERVICE.get(service_name);
        while (true) {
            // Removing an element from the Queue using poll()
            // The poll() method returns null if the Queue is empty.
            
            Timestamp star_time = new Timestamp(System.currentTimeMillis());
            if (serv != null) {
                logger.info("SERVICE = "+service_name);
                logger.info("TIME = "+star_time);
                ContentMessage contentMsg = communRepo.getContentOfService(serv);

                if (contentMsg != null) {
                    
                    logger.info("------ CONTENT LABEL = "+contentMsg.getContent_label()+" ------------");

                    List<Register> listRegister = communRepo.listRegisterOfService(maxrow, index_id, star_time, serv, contentMsg);

                    if (listRegister != null && !listRegister.isEmpty()) {

                        for (Register register : listRegister) {
                           

                            String msisdn = register.getMsisdn();
                            String channel = serv.getSend_channel();
                            String transaction_id = register.getTransaction_id();
                            Product product = register.getProduct();
                            String product_code = product.getProduct_code();
                            String exchange_mode = register.getExchange_mode();
                            Timestamp receive_time = star_time;

                            Process_Request process_mo = new Process_Request();
                            process_mo.setChannel(channel);
                            process_mo.setCommand_code(product_code);
                            process_mo.setContent(contentMsg.getMessage());
                            process_mo.setExchange_mode(exchange_mode);
                            process_mo.setLanguage(null);
                            process_mo.setMsisdn(msisdn);
                            process_mo.setNotification_code("NOTIFY-SERVICE-" + contentMsg.getContent_label());
                            process_mo.setProduct_name(product_code);
                            process_mo.setReceive_time(receive_time);
                            process_mo.setReceiver(channel);
                            process_mo.setTransaction_id(transaction_id);
                            
                            logger.info("MSISDN = "+msisdn);
                            logger.info("CHANNEL = "+channel);
                            logger.info("TRANSACTION ID = "+transaction_id);
                            logger.info("PRODUCT = "+product_code);

                            // send to sender
                            Sender.addMo_Queue(process_mo);

                            ServiceHistory srv_his = new ServiceHistory();
                            srv_his.setContent(contentMsg);
                            srv_his.setMsisdn(msisdn);
                            srv_his.setProcess_time(receive_time);
                            srv_his.setProduct_code(product_code);
                            srv_his.setService_name(service_name);
                            srv_his.setTransaction_id(transaction_id);
                            srv_his.setDescription("NOTIFY-SERVICE-" + contentMsg.getContent_label());

                            ServiceHistoryJpaController srv_his_controller = new ServiceHistoryJpaController(emf);
                            srv_his_controller.create(srv_his);

                        }
                    }else{
                        logger.info("-------------- NOT REGISTER FOR THIS CONTENT LABEL = "+contentMsg.getContent_label()+" ----------------");
                    }

                }else{
                  
                    logger.info("-------------- NOT CONTENT ----------------");

                }
            }

            try {
                Thread.sleep(sleep_duration);
            } catch (Exception e) {
            }

        }
    }

    public static void executeRunnables(final ExecutorService service, List<Runnable> runnables) {
        //On exécute chaque "Runnable" de la liste "runnables"
        for (Runnable r : runnables) {

            service.execute(r);
        }
        service.shutdown();
    }
}
