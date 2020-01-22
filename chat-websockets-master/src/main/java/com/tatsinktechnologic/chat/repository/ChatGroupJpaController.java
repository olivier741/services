/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.chat.repository;

import com.tatsinktechnologic.chat.entities.ChatGroup;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.chat.entities.User;
import com.tatsinktechnologic.chat.repository.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class ChatGroupJpaController implements Serializable {

    public ChatGroupJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ChatGroup chatGroup) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
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
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ChatGroup chatGroup) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
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
            em.getTransaction().commit();
        } catch (Exception ex) {
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

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
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
            em.getTransaction().commit();
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
