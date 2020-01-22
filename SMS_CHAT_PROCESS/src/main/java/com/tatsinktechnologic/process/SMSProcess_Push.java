/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.process;

import com.tatsinktechnologic.beans.Process_Request;
import com.tatsinktechnologic.entity.sms_chat.ContentMessage;
import com.tatsinktechnologic.entity.register.Notification_Conf;
import com.tatsinktechnologic.entity.register.Product;
import com.tatsinktechnologic.entity.register.Register;
import com.tatsinktechnologic.entity.register.Service;
import com.tatsinktechnologic.entity.sms_chat.ServiceHistory;
import com.tatsinktechnologic.dao_commun_repository.CommunRepository;
import com.tatsinktechnologic.dao_repository.Mo_Push_HisJpaController;
import com.tatsinktechnologic.dao_repository.ServiceHistoryJpaController;
import com.tatsinktechnologic.entity.sms_chat.Mo_Push;
import com.tatsinktechnologic.entity.sms_chat.Mo_Push_His;
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
public class SMSProcess_Push implements Runnable {

    private static Logger logger = Logger.getLogger(SMSProcess_Push.class);

    private static Load_Configuration commonConfig = Load_Configuration.getConfigurationLoader();

    private static EntityManagerFactory emf;
    private static int sleep_duration;
    private CommunRepository communRepo;
    private int index_id;
    private int maxrow;
    private Chat_Process chatProc;

    public SMSProcess_Push(int index_id, int maxrow, Chat_Process chatProc, EntityManagerFactory emf) {
        this.emf = commonConfig.getEmf();
        this.index_id = index_id;
        this.maxrow = maxrow;
        this.chatProc = chatProc;
        this.sleep_duration = chatProc.getSleep_duration();
        this.communRepo = new CommunRepository(emf);
    }

    @Override
    public void run() {

        logger.info("################################## START PROCESS PUSH SMS ###########################");

        while (true) {
            // Removing an element from the Queue using poll()
            // The poll() method returns null if the Queue is empty.

            List<Mo_Push> lisMo_Push = communRepo.getMo_Push(maxrow, index_id);

            if (lisMo_Push != null && !lisMo_Push.isEmpty()) {

                for (Mo_Push mopush : lisMo_Push) {
                    String msisdn = mopush.getMsisdn();
                    String channel = mopush.getChannel();
                    String message = mopush.getMessage();
                    Timestamp receive_time = mopush.getReceive_time();

                    Process_Request process_mo = new Process_Request();
                    process_mo.setChannel(channel);
                    process_mo.setCommand_code("");
                    process_mo.setContent(message);
                    process_mo.setExchange_mode("");
                    process_mo.setLanguage(null);
                    process_mo.setMsisdn(msisdn);
                    process_mo.setNotification_code("PUSH-SERVICE");
                    process_mo.setProduct_name("");
                    process_mo.setReceive_time(receive_time);
                    process_mo.setReceiver(channel);
                    process_mo.setTransaction_id("");

                    logger.info("MSISDN = " + msisdn);
                    logger.info("CHANNEL = " + channel);
                    logger.info("MESSAGE = " + message);

                    // send to sender
                    Sender.addMo_Queue(process_mo);
                    
                    Mo_Push_His mo_pus_his = new Mo_Push_His();
                    mo_pus_his.setChannel(channel);
                    mo_pus_his.setGroupe_name(mopush.getGroupe_name());
                    mo_pus_his.setMessage(message);
                    mo_pus_his.setMsisdn(msisdn);
                    mo_pus_his.setOperator(mopush.getOperator());
                    mo_pus_his.setReceive_time(receive_time);
                    mo_pus_his.setUser_id(mopush.getUser_id());
                    
                    Mo_Push_HisJpaController mo_pushHisController = new Mo_Push_HisJpaController(emf);
                    mo_pushHisController.create(mo_pus_his);
                    
                }

            }else{
                logger.info("-------------- NOT PUSH SMS ----------------");
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
