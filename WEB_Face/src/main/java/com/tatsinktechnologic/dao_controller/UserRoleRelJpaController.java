/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entities.account.User;
import com.tatsinktechnologic.entities.account.Role;
import com.tatsinktechnologic.entities.account.UserRoleRel;
import com.tatsinktechnologic.dao_controller.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.dao_controller.exceptions.RollbackFailureException;
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
 * @author olivier
 */
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class UserRoleRelJpaController implements Serializable {

    @Inject
    EntityManagerFactory emf;
    
    @Resource
    private UserTransaction utx;
    
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UserRoleRel userRoleRel) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User user = userRoleRel.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getUser_id());
                userRoleRel.setUser(user);
            }
            Role role = userRoleRel.getRole();
            if (role != null) {
                role = em.getReference(role.getClass(), role.getRole_id());
                userRoleRel.setRole(role);
            }
            em.persist(userRoleRel);
            if (user != null) {
                user.getUserRoleRels().add(userRoleRel);
                user = em.merge(user);
            }
            if (role != null) {
                role.getUserRoleRels().add(userRoleRel);
                role = em.merge(role);
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

    public void edit(UserRoleRel userRoleRel) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            UserRoleRel persistentUserRoleRel = em.find(UserRoleRel.class, userRoleRel.getUser_role_id());
            User userOld = persistentUserRoleRel.getUser();
            User userNew = userRoleRel.getUser();
            Role roleOld = persistentUserRoleRel.getRole();
            Role roleNew = userRoleRel.getRole();
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getUser_id());
                userRoleRel.setUser(userNew);
            }
            if (roleNew != null) {
                roleNew = em.getReference(roleNew.getClass(), roleNew.getRole_id());
                userRoleRel.setRole(roleNew);
            }
            userRoleRel = em.merge(userRoleRel);
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.getUserRoleRels().remove(userRoleRel);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.getUserRoleRels().add(userRoleRel);
                userNew = em.merge(userNew);
            }
            if (roleOld != null && !roleOld.equals(roleNew)) {
                roleOld.getUserRoleRels().remove(userRoleRel);
                roleOld = em.merge(roleOld);
            }
            if (roleNew != null && !roleNew.equals(roleOld)) {
                roleNew.getUserRoleRels().add(userRoleRel);
                roleNew = em.merge(roleNew);
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
                Integer id = userRoleRel.getUser_role_id();
                if (findUserRoleRel(id) == null) {
                    throw new NonexistentEntityException("The userRoleRel with id " + id + " no longer exists.");
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
            UserRoleRel userRoleRel;
            try {
                userRoleRel = em.getReference(UserRoleRel.class, id);
                userRoleRel.getUser_role_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userRoleRel with id " + id + " no longer exists.", enfe);
            }
            User user = userRoleRel.getUser();
            if (user != null) {
                user.getUserRoleRels().remove(userRoleRel);
                user = em.merge(user);
            }
            Role role = userRoleRel.getRole();
            if (role != null) {
                role.getUserRoleRels().remove(userRoleRel);
                role = em.merge(role);
            }
            em.remove(userRoleRel);
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

    public List<UserRoleRel> findUserRoleRelEntities() {
        return findUserRoleRelEntities(true, -1, -1);
    }

    public List<UserRoleRel> findUserRoleRelEntities(int maxResults, int firstResult) {
        return findUserRoleRelEntities(false, maxResults, firstResult);
    }

    private List<UserRoleRel> findUserRoleRelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UserRoleRel.class));
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

    public UserRoleRel findUserRoleRel(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserRoleRel.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserRoleRelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UserRoleRel> rt = cq.from(UserRoleRel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
