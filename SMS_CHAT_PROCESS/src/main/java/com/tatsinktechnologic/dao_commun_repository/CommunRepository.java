/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_commun_repository;

import com.tatsinktechnologic.entity.register.Product;
import com.tatsinktechnologic.entity.register.Register;
import com.tatsinktechnologic.entity.register.Register_;
import com.tatsinktechnologic.entity.register.Service;
import com.tatsinktechnologic.entity.sms_chat.Alias;
import com.tatsinktechnologic.entity.sms_chat.ChatGroup;
import com.tatsinktechnologic.entity.sms_chat.ChatGroup_Register;
import com.tatsinktechnologic.entity.sms_chat.Chat_Level;
import com.tatsinktechnologic.entity.sms_chat.ContentMessage;
import com.tatsinktechnologic.entity.sms_chat.ContentMessage_;
import com.tatsinktechnologic.entity.sms_chat.Message_Status;
import com.tatsinktechnologic.entity.sms_chat.Mo_Push;
import com.tatsinktechnologic.entity.sms_chat.Mo_Push_;
import com.tatsinktechnologic.entity.sms_chat.PushGroup_UserRel;
import com.tatsinktechnologic.entity.sms_chat.PushGroup_UserRel_;
import com.tatsinktechnologic.entity.sms_chat.ServiceHistory;
import com.tatsinktechnologic.entity.sms_chat.ServiceHistory_;
import com.tatsinktechnologic.xml.Chat_Type;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.apache.log4j.Logger;

/**
 *
 * @author olivier
 */
public class CommunRepository {

    private EntityManagerFactory emf;

    private static Logger logger = Logger.getLogger(CommunRepository.class);

    public CommunRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * select * from register where register.msisdn = msisdn and status = 1 and
     * expire_time > receive_time
     */
    public List<Register> getListRegister(String msisdn, Timestamp receive_time, List<String> listProduct) {

        EntityManager em = emf.createEntityManager();
        List<Register> result = null;
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Register> criteria = builder.createQuery(Register.class);
            Root<Register> regRoot = criteria.from(Register.class);
            criteria.select(regRoot);

            Expression<Timestamp> regExpression_expiretime = regRoot.get(Register_.expire_time);
            Expression<String> productExpression = regRoot.get("product").get("product_code");
            Predicate listProductPredicate = productExpression.in(listProduct);

            criteria.where(builder.and(builder.equal(regRoot.get("msisdn"), msisdn),
                    builder.equal(regRoot.get("status"), 1),
                    builder.greaterThanOrEqualTo(regExpression_expiretime, receive_time),
                    listProductPredicate
            )
            );
            Query query = em.createQuery(criteria);

            logger.info("SQL query : " + query.unwrap(org.hibernate.Query.class).getQueryString());
            logger.info("SQL param msisdn = " + msisdn);
            logger.info("SQL param status = " + 1);
            logger.info("SQL param expire_time = " + receive_time);

            result = query.getResultList();
        } finally {
            em.close();
        }

        return result;
    }

    /**
     * select * from register where autoextend = true and status = 1 and
     * register_id % index_id = 0 and ronwnum <= maxrow
     * and expire_time > receive_time
     */
    public List<Register> getExtendRegister(int maxrow, int index_id, Timestamp receive_time) {

        EntityManager em = emf.createEntityManager();
        List<Register> result = null;
        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Register> criteria = builder.createQuery(Register.class);
            Root<Register> regRoot = criteria.from(Register.class);
            criteria.select(regRoot);

            Expression<Integer> regExpression_id = regRoot.get(Register_.register_id);
            Expression<Integer> regExpression_mod = builder.mod(regExpression_id, index_id);
            Expression<Timestamp> regExpression_expiretime = regRoot.get(Register_.expire_time);

            criteria.where(builder.and(builder.equal(regRoot.get("autoextend"), true),
                    builder.equal(regRoot.get("status"), 1),
                    builder.equal(regExpression_mod, 0),
                    builder.greaterThanOrEqualTo(regExpression_expiretime, receive_time)
            )
            );
            Query query = em.createQuery(criteria);
            query.setMaxResults(maxrow);

            logger.info("SQL query : " + query.unwrap(org.hibernate.Query.class).getQueryString());
            logger.info("SQL param autoextend = " + true);
            logger.info("SQL param status = " + 1);
            logger.info("SQL param id = " + index_id);
            logger.info("SQL param receive_time = " + receive_time);

            result = query.getResultList();
        } finally {
            em.close();
        }

        return result;
    }

    /**
     * select * from content where service = ? and message_status = accept and
     * expire_time > current_time and launch_time < currnent_time
     */
    public ContentMessage getContentOfService(Service serv) {

        EntityManager em = emf.createEntityManager();
        ContentMessage result = null;
        try {

            Timestamp current_time = new Timestamp(System.currentTimeMillis());

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ContentMessage> criteria = builder.createQuery(ContentMessage.class);
            Root<ContentMessage> contentRoot = criteria.from(ContentMessage.class);
            criteria.select(contentRoot);

            Expression<Timestamp> regExpression_expire_time = contentRoot.get(ContentMessage_.expire_time);
            Expression<Timestamp> regExpression_launch_time = contentRoot.get(ContentMessage_.launch_time);

            criteria.where(builder.and(
                    builder.equal(contentRoot.get("service"), serv),
                    builder.equal(contentRoot.get("message_status"), Message_Status.ACCEPT),
                    builder.greaterThanOrEqualTo(regExpression_expire_time, current_time),
                    builder.lessThanOrEqualTo(regExpression_launch_time, current_time)
            )
            );
            Query query = em.createQuery(criteria);
            logger.info("SQL query : " + query.unwrap(org.hibernate.Query.class).getQueryString());
            logger.info("SQL param current_time = " + current_time);
            logger.info("SQL param service = " + serv);
            List<ContentMessage> listEntity = query.getResultList();
            if (listEntity != null && !listEntity.isEmpty()) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    /**
     * select * from register where register.msisdn = msisdn and
     * register.product.service=serv
     */
    public Register getRegisterOfService(String msisdn, Service serv) {

        EntityManager em = emf.createEntityManager();
        Register result = null;
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Register> criteria = builder.createQuery(Register.class);
            Root<Register> regRoot = criteria.from(Register.class);
            criteria.select(regRoot);
            criteria.where(builder.and(builder.equal(regRoot.get("msisdn"), msisdn),
                    builder.equal(regRoot.get("product").get("service"), serv)
            )
            );
            Query query = em.createQuery(criteria);
            logger.info("SQL query : " + query.unwrap(org.hibernate.Query.class).getQueryString());
            logger.info("SQL param msisdn = " + msisdn);
            logger.info("SQL param service = " + serv);
            List<Register> listEntity = query.getResultList();
            if (listEntity != null && !listEntity.isEmpty()) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    /**
     * select * from register where status = 1 and register_id % index_id = 0
     * and ronwnum <= maxrow
     * and register.product.service=serv
     * and expire_time > receive_time and msisdn not in (select msisdn from
     * serviceHistory where service_time > current_time and
     * serviceHistory.content = content)
     */
    public List<Register> listRegisterOfService(int maxrow, int index_id, Timestamp receive_time, Service serv, ContentMessage content) {

        EntityManager em = emf.createEntityManager();
        List<Register> result = null;
        try {

            Timestamp current_time = new Timestamp(System.currentTimeMillis());
            current_time.setHours(0);
            current_time.setMinutes(0);
            current_time.setSeconds(0);
            current_time.setNanos(0);

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Register> criteria = builder.createQuery(Register.class);
            Root<Register> regRoot = criteria.from(Register.class);

            Subquery<String> subquery = criteria.subquery(String.class);
            Root<ServiceHistory> servHistRoot = subquery.from(ServiceHistory.class);
            subquery.select(servHistRoot.get(ServiceHistory_.msisdn));

            Expression<Timestamp> regExpression_current_time = servHistRoot.get(ServiceHistory_.process_time);
            subquery.where(builder.and(builder.greaterThanOrEqualTo(regExpression_current_time, current_time),
                    builder.equal(servHistRoot.get("content"), content)
            ));

            criteria.select(regRoot);

            Expression<Integer> regExpression_id = regRoot.get(Register_.register_id);
            Expression<Integer> regExpression_mod = builder.mod(regExpression_id, index_id);
            Expression<Timestamp> regExpression_expiretime = regRoot.get(Register_.expire_time);

            criteria.where(builder.and(builder.equal(regRoot.get("product").get("service"), serv),
                    builder.equal(regRoot.get("status"), 1),
                    builder.equal(regExpression_mod, 0),
                    builder.greaterThanOrEqualTo(regExpression_expiretime, receive_time),
                    builder.not(builder.in(regRoot.get(Register_.msisdn)).value(subquery))
            )
            );
            Query query = em.createQuery(criteria);
            query.setMaxResults(maxrow);

            logger.info("SQL query : " + query.unwrap(org.hibernate.Query.class).getQueryString());
            logger.info("SQL param service = " + serv);
            logger.info("SQL param expire_time = " + receive_time);
            result = query.getResultList();

        } finally {
            em.close();
        }

        return result;
    }

    /**
     * select * from register where msisdn = ? and product = ?
     */
    public Register getRegister(String msisdn, Product prod) {

        EntityManager em = emf.createEntityManager();
        Register result = null;
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Register> criteria = builder.createQuery(Register.class);
            Root<Register> regRoot = criteria.from(Register.class);
            criteria.select(regRoot);
            criteria.where(builder.and(builder.equal(regRoot.get("msisdn"), msisdn),
                    builder.equal(regRoot.get("product"), prod)
            )
            );
            Query query = em.createQuery(criteria);
            logger.info("SQL query : " + query.unwrap(org.hibernate.Query.class).getQueryString());
            logger.info("SQL param msisdn = " + msisdn);
            logger.info("SQL param product = " + prod);
            List<Register> listEntity = query.getResultList();
            if (listEntity != null && !listEntity.isEmpty()) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    /**
     * select * from ChatGroup where group_channel = ?
     */
    public ChatGroup getChatGroup(String channel, Chat_Type chat_type) {
        EntityManager em = emf.createEntityManager();
        ChatGroup result = null;
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ChatGroup> criteria = builder.createQuery(ChatGroup.class);
            Root<ChatGroup> chatGroupRoot = criteria.from(ChatGroup.class);
            criteria.select(chatGroupRoot);
            criteria.where(builder.and(builder.equal(chatGroupRoot.get("group_channel"), channel),
                    builder.equal(chatGroupRoot.get("chat_type"), chat_type)));

            Query query = em.createQuery(criteria);
            logger.info("SQL query : " + query.unwrap(org.hibernate.Query.class).getQueryString());
            logger.info("SQL param service_name = " + channel);

            List<ChatGroup> listEntity = query.getResultList();
            if (listEntity != null && !listEntity.isEmpty()) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

  

    /**
     * select * from ChatGroup_Register where
     * ChatGroup_Register.chatgroup.group_channel = ? and
     * ChatGroup_Register.register.status = 1 and
     * ChatGroup_Register.register.msisdn = ?
     */
    public ChatGroup_Register getChatGroup_RegisterByMsisdn(String channel, String msisdn) {
        EntityManager em = emf.createEntityManager();
        ChatGroup_Register result = null;
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ChatGroup_Register> criteria = builder.createQuery(ChatGroup_Register.class);
            Root<ChatGroup_Register> chatGroup_regRoot = criteria.from(ChatGroup_Register.class);
            criteria.select(chatGroup_regRoot);
            criteria.where(builder.and(builder.equal(chatGroup_regRoot.get("chatgroup").get("group_channel"), channel),
                    builder.equal(chatGroup_regRoot.get("chatgroup").get("msisdn"), msisdn),
                    builder.equal(chatGroup_regRoot.get("register").get("status"), 1))
            );

            Query query = em.createQuery(criteria);
            logger.info("SQL query : " + query.unwrap(org.hibernate.Query.class).getQueryString());
            logger.info("SQL param service_name = " + channel);

            List<ChatGroup_Register> listEntity = query.getResultList();
            if (listEntity != null && !listEntity.isEmpty()) {
                result = listEntity.get(0);
            }

        } finally {
            em.close();
        }

        return result;
    }
    
    
      /**
     * select * from ChatGroup_Register where
     * ChatGroup_Register.chatgroup.group_channel = ? and
     * ChatGroup_Register.register.status = 1
     */
    public List<ChatGroup_Register> getChatGroup_Register(String channel) {
        EntityManager em = emf.createEntityManager();
        List<ChatGroup_Register> result = null;
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ChatGroup_Register> criteria = builder.createQuery(ChatGroup_Register.class);
            Root<ChatGroup_Register> chatGroup_regRoot = criteria.from(ChatGroup_Register.class);
            criteria.select(chatGroup_regRoot);
            criteria.where(builder.and(builder.equal(chatGroup_regRoot.get("chatgroup").get("group_channel"), channel),
                    builder.equal(chatGroup_regRoot.get("register").get("status"), 1))
            );

            Query query = em.createQuery(criteria);
            logger.info("SQL query : " + query.unwrap(org.hibernate.Query.class).getQueryString());
            logger.info("SQL param service_name = " + channel);

            result = query.getResultList();

        } finally {
            em.close();
        }

        return result;
    }

    /**
     * select * from ChatGroup_Register where
     * ChatGroup_Register.chatgroup.group_channel = ? and
     * ChatGroup_Register.chat_level = MASTER ChatGroup_Register.register.status
     * = 1
     */
    public HashMap<String, ChatGroup> getChatGroup(String channel) {
        EntityManager em = emf.createEntityManager();
        HashMap<String, ChatGroup> result = null;
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ChatGroup> criteria = builder.createQuery(ChatGroup.class);
            Root<ChatGroup> chatGroupRoot = criteria.from(ChatGroup.class);
            criteria.select(chatGroupRoot);
            criteria.where(builder.equal(chatGroupRoot.get("group_channel"), channel));

            Query query = em.createQuery(criteria);
            logger.info("SQL query : " + query.unwrap(org.hibernate.Query.class).getQueryString());
            logger.info("SQL param service_name = " + channel);

            List<ChatGroup> listEntity = query.getResultList();
            result = new HashMap<String, ChatGroup>();
            if (listEntity != null && !listEntity.isEmpty()) {
                for (ChatGroup chatGrp_reg : listEntity) {
                    result.put(chatGrp_reg.getMaster_msisdn(), chatGrp_reg);
                }
            }

        } finally {
            em.close();
        }

        return result;
    }

    /**
     * select * from Service where service_name = ?
     */
    public Service getService(String service_name) {
        EntityManager em = emf.createEntityManager();
        Service result = null;
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Service> criteria = builder.createQuery(Service.class);
            Root<Service> servRoot = criteria.from(Service.class);
            criteria.select(servRoot);
            criteria.where(builder.equal(servRoot.get("service_name"), service_name));

            Query query = em.createQuery(criteria);
            logger.info("SQL query : " + query.unwrap(org.hibernate.Query.class).getQueryString());
            logger.info("SQL param service_name = " + service_name);

            List<Service> listEntity = query.getResultList();
            if (listEntity != null && !listEntity.isEmpty()) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    /**
     * select * from Alias where msisdn = ? and Alias.service = ?
     */
    public Alias getAlias(String msisdn, Service serv) {
        EntityManager em = emf.createEntityManager();
        Alias result = null;
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Alias> criteria = builder.createQuery(Alias.class);
            Root<Alias> aliasRoot = criteria.from(Alias.class);
            criteria.select(aliasRoot);
            criteria.where(builder.and(builder.equal(aliasRoot.get("msisdn"), msisdn),
                    builder.equal(aliasRoot.get("service"), serv))
            );

            Query query = em.createQuery(criteria);
            logger.info("SQL query : " + query.unwrap(org.hibernate.Query.class).getQueryString());
            logger.info("SQL param msisdn = " + msisdn);
            logger.info("SQL param service_name = " + serv.getService_name());

            List<Alias> listEntity = query.getResultList();
            if (listEntity != null && !listEntity.isEmpty()) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public List<PushGroup_UserRel> getPushGroup_UserRel() {

        EntityManager em = emf.createEntityManager();
        List<PushGroup_UserRel> result = null;
        try {

            Timestamp current_time = new Timestamp(System.currentTimeMillis());

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<PushGroup_UserRel> criteria = builder.createQuery(PushGroup_UserRel.class);
            Root<PushGroup_UserRel> pushGrpUserRoot = criteria.from(PushGroup_UserRel.class);
            criteria.select(pushGrpUserRoot);

            Expression<Timestamp> expression_sendtime = pushGrpUserRoot.get(PushGroup_UserRel_.send_time);

            criteria.where(builder.and(builder.equal(pushGrpUserRoot.get("status"), true),
                    builder.greaterThanOrEqualTo(expression_sendtime, current_time)
            )
            );
            Query query = em.createQuery(criteria);

            logger.info("SQL query : " + query.unwrap(org.hibernate.Query.class).getQueryString());
            logger.info("SQL param status = " + true);
            logger.info("SQL param receive_time = " + current_time);

            result = query.getResultList();
        } finally {
            em.close();
        }

        return result;
    }

    /**
     * update Alias set alias = ? where msisdn = ?
     */
    public boolean addMo_Push(String channel, int user_id, String message, String group_name) {
        boolean result = false;

        EntityManager em = emf.createEntityManager();

        String INSERT_SQL = "INSERT INTO Mo_Push (msisdn,channel,operator,group_name,user_id,message) \n"
                + " SELECT pushphone.phone_number msisdn,   \n"
                + "        :channel channel,                \n"
                + "        pushphone.operator operator,     \n"
                + "        pushgroup.group_name group_name, \n"
                + "        :user_id  user_id,               \n"
                + "        :message  message                \n"
                + " FROM PushPhoneGroupRel                  \n"
                + " WHERE pushgroup.group_name = :group_name ";

        try {

            Query query = em.createQuery(INSERT_SQL);
            query.setParameter("channel", channel);
            query.setParameter("user_id", user_id);
            query.setParameter("message", message);
            query.setParameter("group_name", group_name);

            int rown = query.executeUpdate();
            logger.info("SQL query : " + query.unwrap(org.hibernate.Query.class).getQueryString());
            logger.info("Rows affected: " + rown);

            result = true;
        } finally {
            em.close();
        }

        return result;
    }
    
    
    public List<Mo_Push> getMo_Push(int maxrow, int index_id) {

        EntityManager em = emf.createEntityManager();
        List<Mo_Push> result = null;
        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Mo_Push> criteria = builder.createQuery(Mo_Push.class);
            Root<Mo_Push> mo_pushRoot = criteria.from(Mo_Push.class);
            criteria.select(mo_pushRoot);

            Expression<Integer> mo_pushExpression_id = mo_pushRoot.get(Mo_Push_.mo_push_id);
            Expression<Integer> mo_pushExpression_mod = builder.mod(mo_pushExpression_id, index_id);

            criteria.where(builder.equal(mo_pushExpression_mod, 0) );
            Query query = em.createQuery(criteria);
            query.setMaxResults(maxrow);

            logger.info("SQL query : " + query.unwrap(org.hibernate.Query.class).getQueryString());

            result = query.getResultList();

        } finally {
            em.close();
        }

        return result;
    }

}
