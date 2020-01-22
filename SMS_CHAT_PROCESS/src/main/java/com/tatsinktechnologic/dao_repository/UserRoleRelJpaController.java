/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_repository;

import com.tatsinktechnologic.dao_repository.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entity.account.User;
import com.tatsinktechnologic.entity.account.Role;
import com.tatsinktechnologic.entity.account.UserRoleRel;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class UserRoleRelJpaController implements Serializable {

    public UserRoleRelJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UserRoleRel userRoleRel) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
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
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserRoleRel userRoleRel) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
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
            em.getTransaction().commit();
        } catch (Exception ex) {
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

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
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
            em.getTransaction().commit();
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
