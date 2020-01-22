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
import com.tatsinktechnologic.entities.account.Role;
import com.tatsinktechnologic.entities.account.Permission;
import com.tatsinktechnologic.entities.account.RolePermissionRel;
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
public class RolePermissionRelJpaController implements Serializable {

    @Inject
    EntityManagerFactory emf;
    
    @Resource
    private UserTransaction utx;


    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RolePermissionRel rolePermissionRel) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Role role = rolePermissionRel.getRole();
            if (role != null) {
                role = em.getReference(role.getClass(), role.getRole_id());
                rolePermissionRel.setRole(role);
            }
            Permission permission = rolePermissionRel.getPermission();
            if (permission != null) {
                permission = em.getReference(permission.getClass(), permission.getPermission_id());
                rolePermissionRel.setPermission(permission);
            }
            em.persist(rolePermissionRel);
            if (role != null) {
                role.getRolePermissionRels().add(rolePermissionRel);
                role = em.merge(role);
            }
            if (permission != null) {
                permission.getRolePermissionRels().add(rolePermissionRel);
                permission = em.merge(permission);
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

    public void edit(RolePermissionRel rolePermissionRel) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            RolePermissionRel persistentRolePermissionRel = em.find(RolePermissionRel.class, rolePermissionRel.getRole_permission_id());
            Role roleOld = persistentRolePermissionRel.getRole();
            Role roleNew = rolePermissionRel.getRole();
            Permission permissionOld = persistentRolePermissionRel.getPermission();
            Permission permissionNew = rolePermissionRel.getPermission();
            if (roleNew != null) {
                roleNew = em.getReference(roleNew.getClass(), roleNew.getRole_id());
                rolePermissionRel.setRole(roleNew);
            }
            if (permissionNew != null) {
                permissionNew = em.getReference(permissionNew.getClass(), permissionNew.getPermission_id());
                rolePermissionRel.setPermission(permissionNew);
            }
            rolePermissionRel = em.merge(rolePermissionRel);
            if (roleOld != null && !roleOld.equals(roleNew)) {
                roleOld.getRolePermissionRels().remove(rolePermissionRel);
                roleOld = em.merge(roleOld);
            }
            if (roleNew != null && !roleNew.equals(roleOld)) {
                roleNew.getRolePermissionRels().add(rolePermissionRel);
                roleNew = em.merge(roleNew);
            }
            if (permissionOld != null && !permissionOld.equals(permissionNew)) {
                permissionOld.getRolePermissionRels().remove(rolePermissionRel);
                permissionOld = em.merge(permissionOld);
            }
            if (permissionNew != null && !permissionNew.equals(permissionOld)) {
                permissionNew.getRolePermissionRels().add(rolePermissionRel);
                permissionNew = em.merge(permissionNew);
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
                Integer id = rolePermissionRel.getRole_permission_id();
                if (findRolePermissionRel(id) == null) {
                    throw new NonexistentEntityException("The rolePermissionRel with id " + id + " no longer exists.");
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
            RolePermissionRel rolePermissionRel;
            try {
                rolePermissionRel = em.getReference(RolePermissionRel.class, id);
                rolePermissionRel.getRole_permission_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rolePermissionRel with id " + id + " no longer exists.", enfe);
            }
            Role role = rolePermissionRel.getRole();
            if (role != null) {
                role.getRolePermissionRels().remove(rolePermissionRel);
                role = em.merge(role);
            }
            Permission permission = rolePermissionRel.getPermission();
            if (permission != null) {
                permission.getRolePermissionRels().remove(rolePermissionRel);
                permission = em.merge(permission);
            }
            em.remove(rolePermissionRel);
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

    public List<RolePermissionRel> findRolePermissionRelEntities() {
        return findRolePermissionRelEntities(true, -1, -1);
    }

    public List<RolePermissionRel> findRolePermissionRelEntities(int maxResults, int firstResult) {
        return findRolePermissionRelEntities(false, maxResults, firstResult);
    }

    private List<RolePermissionRel> findRolePermissionRelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RolePermissionRel.class));
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

    public RolePermissionRel findRolePermissionRel(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RolePermissionRel.class, id);
        } finally {
            em.close();
        }
    }

    public int getRolePermissionRelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RolePermissionRel> rt = cq.from(RolePermissionRel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
