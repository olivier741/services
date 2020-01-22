/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_controller;

import com.tatsinktechnologic.dao_controller.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.dao_controller.exceptions.RollbackFailureException;
import com.tatsinktechnologic.entities.account.User;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entities.account.UserContactRel;
import java.util.HashSet;
import java.util.Set;
import com.tatsinktechnologic.entities.account.UserRoleRel;
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
public class UserJpaController implements Serializable {

    @Inject
    EntityManagerFactory emf;
    
    @Resource
    private UserTransaction utx;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) throws RollbackFailureException, Exception {
        if (user.getUserContactRels() == null) {
            user.setUserContactRels(new HashSet<UserContactRel>());
        }
        if (user.getUserRoleRels() == null) {
            user.setUserRoleRels(new HashSet<UserRoleRel>());
        }
        if (user.getChatGroups() == null) {
            user.setChatGroups(new HashSet<ChatGroup>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Set<UserContactRel> attachedUserContactRels = new HashSet<UserContactRel>();
            for (UserContactRel userContactRelsUserContactRelToAttach : user.getUserContactRels()) {
                userContactRelsUserContactRelToAttach = em.getReference(userContactRelsUserContactRelToAttach.getClass(), userContactRelsUserContactRelToAttach.getUser_contact_id());
                attachedUserContactRels.add(userContactRelsUserContactRelToAttach);
            }
            user.setUserContactRels(attachedUserContactRels);
            Set<UserRoleRel> attachedUserRoleRels = new HashSet<UserRoleRel>();
            for (UserRoleRel userRoleRelsUserRoleRelToAttach : user.getUserRoleRels()) {
                userRoleRelsUserRoleRelToAttach = em.getReference(userRoleRelsUserRoleRelToAttach.getClass(), userRoleRelsUserRoleRelToAttach.getUser_role_id());
                attachedUserRoleRels.add(userRoleRelsUserRoleRelToAttach);
            }
            user.setUserRoleRels(attachedUserRoleRels);
            Set<ChatGroup> attachedChatGroups = new HashSet<ChatGroup>();
            for (ChatGroup chatGroupsChatGroupToAttach : user.getChatGroups()) {
                chatGroupsChatGroupToAttach = em.getReference(chatGroupsChatGroupToAttach.getClass(), chatGroupsChatGroupToAttach.getChatgroup_id());
                attachedChatGroups.add(chatGroupsChatGroupToAttach);
            }
            user.setChatGroups(attachedChatGroups);
            em.persist(user);
            for (UserContactRel userContactRelsUserContactRel : user.getUserContactRels()) {
                User oldUserOfUserContactRelsUserContactRel = userContactRelsUserContactRel.getUser();
                userContactRelsUserContactRel.setUser(user);
                userContactRelsUserContactRel = em.merge(userContactRelsUserContactRel);
                if (oldUserOfUserContactRelsUserContactRel != null) {
                    oldUserOfUserContactRelsUserContactRel.getUserContactRels().remove(userContactRelsUserContactRel);
                    oldUserOfUserContactRelsUserContactRel = em.merge(oldUserOfUserContactRelsUserContactRel);
                }
            }
            for (UserRoleRel userRoleRelsUserRoleRel : user.getUserRoleRels()) {
                User oldUserOfUserRoleRelsUserRoleRel = userRoleRelsUserRoleRel.getUser();
                userRoleRelsUserRoleRel.setUser(user);
                userRoleRelsUserRoleRel = em.merge(userRoleRelsUserRoleRel);
                if (oldUserOfUserRoleRelsUserRoleRel != null) {
                    oldUserOfUserRoleRelsUserRoleRel.getUserRoleRels().remove(userRoleRelsUserRoleRel);
                    oldUserOfUserRoleRelsUserRoleRel = em.merge(oldUserOfUserRoleRelsUserRoleRel);
                }
            }
            for (ChatGroup chatGroupsChatGroup : user.getChatGroups()) {
                User oldUserOfChatGroupsChatGroup = chatGroupsChatGroup.getUser();
                chatGroupsChatGroup.setUser(user);
                chatGroupsChatGroup = em.merge(chatGroupsChatGroup);
                if (oldUserOfChatGroupsChatGroup != null) {
                    oldUserOfChatGroupsChatGroup.getChatGroups().remove(chatGroupsChatGroup);
                    oldUserOfChatGroupsChatGroup = em.merge(oldUserOfChatGroupsChatGroup);
                }
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

    public void edit(User user) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User persistentUser = em.find(User.class, user.getUser_id());
            Set<UserContactRel> userContactRelsOld = persistentUser.getUserContactRels();
            Set<UserContactRel> userContactRelsNew = user.getUserContactRels();
            Set<UserRoleRel> userRoleRelsOld = persistentUser.getUserRoleRels();
            Set<UserRoleRel> userRoleRelsNew = user.getUserRoleRels();
            Set<ChatGroup> chatGroupsOld = persistentUser.getChatGroups();
            Set<ChatGroup> chatGroupsNew = user.getChatGroups();
            Set<UserContactRel> attachedUserContactRelsNew = new HashSet<UserContactRel>();
            for (UserContactRel userContactRelsNewUserContactRelToAttach : userContactRelsNew) {
                userContactRelsNewUserContactRelToAttach = em.getReference(userContactRelsNewUserContactRelToAttach.getClass(), userContactRelsNewUserContactRelToAttach.getUser_contact_id());
                attachedUserContactRelsNew.add(userContactRelsNewUserContactRelToAttach);
            }
            userContactRelsNew = attachedUserContactRelsNew;
            user.setUserContactRels(userContactRelsNew);
            Set<UserRoleRel> attachedUserRoleRelsNew = new HashSet<UserRoleRel>();
            for (UserRoleRel userRoleRelsNewUserRoleRelToAttach : userRoleRelsNew) {
                userRoleRelsNewUserRoleRelToAttach = em.getReference(userRoleRelsNewUserRoleRelToAttach.getClass(), userRoleRelsNewUserRoleRelToAttach.getUser_role_id());
                attachedUserRoleRelsNew.add(userRoleRelsNewUserRoleRelToAttach);
            }
            userRoleRelsNew = attachedUserRoleRelsNew;
            user.setUserRoleRels(userRoleRelsNew);
            Set<ChatGroup> attachedChatGroupsNew = new HashSet<ChatGroup>();
            for (ChatGroup chatGroupsNewChatGroupToAttach : chatGroupsNew) {
                chatGroupsNewChatGroupToAttach = em.getReference(chatGroupsNewChatGroupToAttach.getClass(), chatGroupsNewChatGroupToAttach.getChatgroup_id());
                attachedChatGroupsNew.add(chatGroupsNewChatGroupToAttach);
            }
            chatGroupsNew = attachedChatGroupsNew;
            user.setChatGroups(chatGroupsNew);
            user = em.merge(user);
            for (UserContactRel userContactRelsOldUserContactRel : userContactRelsOld) {
                if (!userContactRelsNew.contains(userContactRelsOldUserContactRel)) {
                    userContactRelsOldUserContactRel.setUser(null);
                    userContactRelsOldUserContactRel = em.merge(userContactRelsOldUserContactRel);
                }
            }
            for (UserContactRel userContactRelsNewUserContactRel : userContactRelsNew) {
                if (!userContactRelsOld.contains(userContactRelsNewUserContactRel)) {
                    User oldUserOfUserContactRelsNewUserContactRel = userContactRelsNewUserContactRel.getUser();
                    userContactRelsNewUserContactRel.setUser(user);
                    userContactRelsNewUserContactRel = em.merge(userContactRelsNewUserContactRel);
                    if (oldUserOfUserContactRelsNewUserContactRel != null && !oldUserOfUserContactRelsNewUserContactRel.equals(user)) {
                        oldUserOfUserContactRelsNewUserContactRel.getUserContactRels().remove(userContactRelsNewUserContactRel);
                        oldUserOfUserContactRelsNewUserContactRel = em.merge(oldUserOfUserContactRelsNewUserContactRel);
                    }
                }
            }
            for (UserRoleRel userRoleRelsOldUserRoleRel : userRoleRelsOld) {
                if (!userRoleRelsNew.contains(userRoleRelsOldUserRoleRel)) {
                    userRoleRelsOldUserRoleRel.setUser(null);
                    userRoleRelsOldUserRoleRel = em.merge(userRoleRelsOldUserRoleRel);
                }
            }
            for (UserRoleRel userRoleRelsNewUserRoleRel : userRoleRelsNew) {
                if (!userRoleRelsOld.contains(userRoleRelsNewUserRoleRel)) {
                    User oldUserOfUserRoleRelsNewUserRoleRel = userRoleRelsNewUserRoleRel.getUser();
                    userRoleRelsNewUserRoleRel.setUser(user);
                    userRoleRelsNewUserRoleRel = em.merge(userRoleRelsNewUserRoleRel);
                    if (oldUserOfUserRoleRelsNewUserRoleRel != null && !oldUserOfUserRoleRelsNewUserRoleRel.equals(user)) {
                        oldUserOfUserRoleRelsNewUserRoleRel.getUserRoleRels().remove(userRoleRelsNewUserRoleRel);
                        oldUserOfUserRoleRelsNewUserRoleRel = em.merge(oldUserOfUserRoleRelsNewUserRoleRel);
                    }
                }
            }
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
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
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

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getUser_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            Set<UserContactRel> userContactRels = user.getUserContactRels();
            for (UserContactRel userContactRelsUserContactRel : userContactRels) {
                userContactRelsUserContactRel.setUser(null);
                userContactRelsUserContactRel = em.merge(userContactRelsUserContactRel);
            }
            Set<UserRoleRel> userRoleRels = user.getUserRoleRels();
            for (UserRoleRel userRoleRelsUserRoleRel : userRoleRels) {
                userRoleRelsUserRoleRel.setUser(null);
                userRoleRelsUserRoleRel = em.merge(userRoleRelsUserRoleRel);
            }
            Set<ChatGroup> chatGroups = user.getChatGroups();
            for (ChatGroup chatGroupsChatGroup : chatGroups) {
                chatGroupsChatGroup.setUser(null);
                chatGroupsChatGroup = em.merge(chatGroupsChatGroup);
            }
            em.remove(user);
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
