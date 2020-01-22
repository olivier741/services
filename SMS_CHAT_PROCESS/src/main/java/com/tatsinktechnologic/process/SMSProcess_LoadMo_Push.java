/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.process;

import com.tatsinktechnologic.dao_commun_repository.CommunRepository;
import com.tatsinktechnologic.dao_repository.PushGroup_UserRelJpaController;
import com.tatsinktechnologic.entity.sms_chat.PushGroup_UserRel;
import com.tatsinktechnologic.loadconfig.Load_Configuration;
import com.tatsinktechnologic.xml.Application;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ExecutorService;
import javax.persistence.EntityManagerFactory;
import org.apache.log4j.Logger;

/**
 *
 * @author olivier
 */
public class SMSProcess_LoadMo_Push implements Runnable {

    private static Logger logger = Logger.getLogger(SMSProcess_Push.class);

    private static Load_Configuration commonConfig = Load_Configuration.getConfigurationLoader();

    private static EntityManagerFactory emf;
    private static int sleep_duration = 1000;
    private static Application app_conf;
    private CommunRepository communRepo;

    public SMSProcess_LoadMo_Push() {
        this.emf = commonConfig.getEmf();
        this.communRepo = new CommunRepository(emf);
    }

    @Override
    public void run() {

        logger.info("################################## START PROCESS LOAD MO_PUSH ###########################");

        while (true) {
            // Removing an element from the Queue using poll()
            // The poll() method returns null if the Queue is empty.

            List<PushGroup_UserRel> listPushGroup_UserRel = communRepo.getPushGroup_UserRel();

            if (listPushGroup_UserRel != null && !listPushGroup_UserRel.isEmpty()) {
                for (PushGroup_UserRel pushGrpUser : listPushGroup_UserRel) {
                    String channel = pushGrpUser.getChannel();
                    String group_name = pushGrpUser.getPushgroup().getGroup_name();
                    int user_id = pushGrpUser.getUser().getUser_id();
                    String message = pushGrpUser.getMessage();

                    boolean isok = communRepo.addMo_Push(channel, user_id, message, group_name);

                    if (isok) {
                        pushGrpUser.setStatus(false);
                        PushGroup_UserRelJpaController pushGrpUserController = new PushGroup_UserRelJpaController(emf);
                        try {
                            pushGrpUserController.edit(pushGrpUser);
                        } catch (Exception e) {
                             logger.error("-------------- CANNOT CHANGE STATUS OF REQUESTED PUSH ----------------",e);
                        }
                    }else{
                         logger.error("-------------- CANNOT LOAD MO_PUSH ----------------");
                    }

                }
            }else{
                 logger.info("-------------- NOT PUSH SMS REQUESTED ----------------");
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
