/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_repository;

import com.tatsinktechnologic.dao_repository.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.entity.account.User;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entity.account.UserContactRel;
import java.util.HashSet;
import java.util.Set;
import com.tatsinktechnologic.entity.account.UserRoleRel;
import java.util.List;
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
        if (user.getUserContactRels() == null) {
            user.setUserContactRels(new HashSet<UserContactRel>());
        }
        if (user.getUserRoleRels() == null) {
            user.setUserRoleRels(new HashSet<UserRoleRel>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
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
            Set<UserContactRel> userContactRelsOld = persistentUser.getUserContactRels();
            Set<UserContactRel> userContactRelsNew = user.getUserContactRels();
            Set<UserRoleRel> userRoleRelsOld = persistentUser.getUserRoleRels();
            Set<UserRoleRel> userRoleRelsNew = user.getUserRoleRels();
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
