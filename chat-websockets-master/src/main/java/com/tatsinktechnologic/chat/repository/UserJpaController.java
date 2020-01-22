/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.chat.repository;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.chat.entities.ChatGroup;
import com.tatsinktechnologic.chat.entities.User;
import com.tatsinktechnologic.chat.repository.exceptions.NonexistentEntityException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class UserJpaController implements Serializable {

    public UserJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) {
        if (user.getChatGroups() == null) {
            user.setChatGroups(new HashSet<ChatGroup>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Set<ChatGroup> attachedChatGroups = new HashSet<ChatGroup>();
            for (ChatGroup chatGroupsChatGroupToAttach : user.getChatGroups()) {
                chatGroupsChatGroupToAttach = em.getReference(chatGroupsChatGroupToAttach.getClass(), chatGroupsChatGroupToAttach.getChatgroup_id());
                attachedChatGroups.add(chatGroupsChatGroupToAttach);
            }
            user.setChatGroups(attachedChatGroups);
            em.persist(user);
            for (ChatGroup chatGroupsChatGroup : user.getChatGroups()) {
                User oldUserOfChatGroupsChatGroup = chatGroupsChatGroup.getUser();
                chatGroupsChatGroup.setUser(user);
                chatGroupsChatGroup = em.merge(chatGroupsChatGroup);
                if (oldUserOfChatGroupsChatGroup != null) {
                    oldUserOfChatGroupsChatGroup.getChatGroups().remove(chatGroupsChatGroup);
                    oldUserOfChatGroupsChatGroup = em.merge(oldUserOfChatGroupsChatGroup);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User persistentUser = em.find(User.class, user.getUser_id());
            Set<ChatGroup> chatGroupsOld = persistentUser.getChatGroups();
            Set<ChatGroup> chatGroupsNew = user.getChatGroups();
            Set<ChatGroup> attachedChatGroupsNew = new HashSet<ChatGroup>();
            for (ChatGroup chatGroupsNewChatGroupToAttach : chatGroupsNew) {
                chatGroupsNewChatGroupToAttach = em.getReference(chatGroupsNewChatGroupToAttach.getClass(), chatGroupsNewChatGroupToAttach.getChatgroup_id());
                attachedChatGroupsNew.add(chatGroupsNewChatGroupToAttach);
            }
            chatGroupsNew = attachedChatGroupsNew;
            user.setChatGroups(chatGroupsNew);
            user = em.merge(user);
            for (ChatGroup chatGroupsOldChatGroup : chatGroupsOld) {
                if (!chatGroupsNew.contains(chatGroupsOldChatGroup)) {
                    chatGroupsOldChatGroup.setUser(null);
                    chatGroupsOldChatGroup = em.merge(chatGroupsOldChatGroup);
                }
            }
            for (ChatGroup chatGroupsNewChatGroup : chatGroupsNew) {
                if (!chatGroupsOld.contains(chatGroupsNewChatGroup)) {
                    User oldUserOfChatGroupsNewChatGroup = chatGroupsNewChatGroup.getUser();
                    chatGroupsNewChatGroup.setUser(user);
                    chatGroupsNewChatGroup = em.merge(chatGroupsNewChatGroup);
                    if (oldUserOfChatGroupsNewChatGroup != null && !oldUserOfChatGroupsNewChatGroup.equals(user)) {
                        oldUserOfChatGroupsNewChatGroup.getChatGroups().remove(chatGroupsNewChatGroup);
                        oldUserOfChatGroupsNewChatGroup = em.merge(oldUserOfChatGroupsNewChatGroup);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = user.getUser_id();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getUser_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            Set<ChatGroup> chatGroups = user.getChatGroups();
            for (ChatGroup chatGroupsChatGroup : chatGroups) {
                chatGroupsChatGroup.setUser(null);
                chatGroupsChatGroup = em.merge(chatGroupsChatGroup);
            }
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
