/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.process;

import com.tatsinktechnologic.beans.Process_Request;
import com.tatsinktechnologic.dao_commun_repository.CommunRepository;
import com.tatsinktechnologic.entity.register.Service;
import com.tatsinktechnologic.entity.sms_chat.Alias;
import com.tatsinktechnologic.entity.sms_chat.ChatGroup;
import com.tatsinktechnologic.entity.sms_chat.ChatGroup_Register;
import com.tatsinktechnologic.sender.Sender;
import com.tatsinktechnologic.util.Utils;
import com.tatsinktechnologic.xml.Application;
import com.tatsinktechnologic.xml.Chat_Process;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import javax.persistence.EntityManagerFactory;
import org.apache.log4j.Logger;

/**
 *
 * @author olivier.tatsinkou
 */
public class SMSProcessOneToMany implements Runnable {

    private static Logger logger = Logger.getLogger(SMSProcessOneToMany.class);

    private static EntityManagerFactory emf;
    private static int sleep_duration;
    private static Application app_conf;
    private static Chat_Process chatProc;
    private static InetAddress address;
    private static BlockingQueue<Process_Request> chat_queue;

    private CommunRepository communRepo;

    public static void addMo_Queue(Process_Request process_req) {
        try {
            chat_queue.put(process_req);
        } catch (InterruptedException e) {
            logger.error("Error to add in chat_queue :" + process_req, e);
        }

    }

    public SMSProcessOneToMany(BlockingQueue<Process_Request> reg_queue, Chat_Process chatProc, EntityManagerFactory emf) {
        this.emf = emf;
        this.chat_queue = reg_queue;
        this.chatProc = chatProc;
        this.sleep_duration = chatProc.getSleep_duration();
        this.address = Utils.gethostName();
        this.communRepo = new CommunRepository(emf);
    }

    @Override
    public void run() {

        logger.info("################################## START PROCESS REGISTER ###########################");
        Service serv = communRepo.getService(chatProc.getService_name());
        String message = chatProc.getMsg_format();
        if (serv != null) {

            while (true) {
                // Removing an element from the Queue using poll()
                // The poll() method returns null if the Queue is empty.
                Process_Request process_mo = null;

                try {
                    //consuming messages 
                    process_mo = chat_queue.take();
                    logger.info("Get message in ONE_TO_ONE queue :" + process_mo);
                    logger.info("ONE_TO_ONE Queue size :" + chat_queue.size());
                } catch (InterruptedException e) {
                    logger.error("Error to Get in chat_queue :" + process_mo, e);
                }

                if (process_mo != null) {
                    String msisdn = process_mo.getMsisdn();
                    String receiver = process_mo.getReceiver();

                    Alias alias = communRepo.getAlias(msisdn, serv);

                    ChatGroup chat_group = communRepo.getChatGroup(receiver, chatProc.getType());

                    message = message.replace("_tab_", "\n");
                    message = message.replace("_alias_", alias.getAlias());
                    message = message.replace("_content_", process_mo.getContent());

                    if (chat_group != null) {

                        List<ChatGroup_Register> listChatGrp_Reg = communRepo.getChatGroup_Register(receiver);
                        HashMap<String, ChatGroup> mapChatGroup = communRepo.getChatGroup(receiver);
                        ChatGroup chatGrp_requester = null;

                        if (mapChatGroup != null && !mapChatGroup.isEmpty()) {
                            chatGrp_requester = mapChatGroup.get(msisdn);
                            if (chatGrp_requester != null) {
                                if (listChatGrp_Reg != null && !listChatGrp_Reg.isEmpty()) {
                                    for (ChatGroup_Register chatGrp_reg : listChatGrp_Reg) {
                                        String chat_partener_msisdn = chatGrp_reg.getRegister().getMsisdn();
                                        if (!chat_partener_msisdn.equals(msisdn)) {
                                            process_mo.setMsisdn(chat_partener_msisdn);
                                            process_mo.setContent(message);
                                            Sender.addMo_Queue(process_mo);
                                            logger.info("Message trans_id = " + process_mo.getTransaction_id() + " --> From MASTER : " + msisdn + " to SLAVE -->  " + chat_partener_msisdn + " in group : " + receiver);
                                        }
                                    }
                                } else {
                                    logger.info("Message trans_id = " + process_mo.getTransaction_id() + " --> from : " + msisdn + " Not Receiver in Group : " + receiver);
                                }
                            } else {

                                List<ChatGroup> listChatGrp = new ArrayList<ChatGroup>(mapChatGroup.values());
                                if (listChatGrp != null && !listChatGrp.isEmpty()) {
                                    for (ChatGroup chatGrp : listChatGrp) {
                                        if (chatGrp.isIsReceive()) {
                                            String chat_partener_msisdn = chatGrp.getMaster_msisdn();
                                            process_mo.setMsisdn(chat_partener_msisdn);
                                            process_mo.setContent(message);
                                            Sender.addMo_Queue(process_mo);
                                            logger.info("Message trans_id = " + process_mo.getTransaction_id() + " --> from SLAVE : " + msisdn + " to MASTER -->  " + chat_partener_msisdn + " in group : " + receiver);

                                        } else {
                                            String chat_partener_msisdn = chatGrp.getMaster_msisdn();
                                            logger.info("Master cannot Receive this Message");
                                            logger.info("Message trans_id = " + process_mo.getTransaction_id() + " --> from SLAVE : " + msisdn + " to MASTER -->  " + chat_partener_msisdn + " in group : " + receiver);
                                        }

                                    }
                                } else {
                                    logger.info("Message trans_id = " + process_mo.getTransaction_id() + " --> from : " + msisdn + " Not Receiver in Group : " + receiver);
                                }

                            }
                        } else {
                            logger.info("Message trans_id = " + process_mo.getTransaction_id() + " --> from : " + msisdn + " Not MASTER in Group : " + receiver);
                        }

                    } else {
                        logger.info("Message trans_id = " + process_mo.getTransaction_id() + " --> from : " + msisdn + " Group Chat : " + receiver + " Not exist");
                    }
                }

                try {
                    Thread.sleep(sleep_duration);
                } catch (Exception e) {
                }

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
