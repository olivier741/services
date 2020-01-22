/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_controller;

import com.tatsinktechnologic.dao_controller.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.dao_controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entities.account.User;
import com.tatsinktechnologic.entities.chat_sms.ChatGroup;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author olivier.tatsinkou
 */
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class ChatGroupJpaController implements Serializable {

    @Inject
    EntityManagerFactory emf;
    
    @Resource
    private UserTransaction utx;



    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ChatGroup chatGroup) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User user = chatGroup.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getUser_id());
                chatGroup.setUser(user);
            }
            em.persist(chatGroup);
            if (user != null) {
                user.getChatGroups().add(chatGroup);
                user = em.merge(user);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ChatGroup chatGroup) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            ChatGroup persistentChatGroup = em.find(ChatGroup.class, chatGroup.getChatgroup_id());
            User userOld = persistentChatGroup.getUser();
            User userNew = chatGroup.getUser();
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getUser_id());
                chatGroup.setUser(userNew);
            }
            chatGroup = em.merge(chatGroup);
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.getChatGroups().remove(chatGroup);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.getChatGroups().add(chatGroup);
                userNew = em.merge(userNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = chatGroup.getChatgroup_id();
                if (findChatGroup(id) == null) {
                    throw new NonexistentEntityException("The chatGroup with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            ChatGroup chatGroup;
            try {
                chatGroup = em.getReference(ChatGroup.class, id);
                chatGroup.getChatgroup_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The chatGroup with id " + id + " no longer exists.", enfe);
            }
            User user = chatGroup.getUser();
            if (user != null) {
                user.getChatGroups().remove(chatGroup);
                user = em.merge(user);
            }
            em.remove(chatGroup);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ChatGroup> findChatGroupEntities() {
        return findChatGroupEntities(true, -1, -1);
    }

    public List<ChatGroup> findChatGroupEntities(int maxResults, int firstResult) {
        return findChatGroupEntities(false, maxResults, firstResult);
    }

    private List<ChatGroup> findChatGroupEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ChatGroup.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public ChatGroup findChatGroup(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ChatGroup.class, id);
        } finally {
            em.close();
        }
    }

    public int getChatGroupCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ChatGroup> rt = cq.from(ChatGroup.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
