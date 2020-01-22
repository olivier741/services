/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_controller;

import com.tatsinktechnologic.entities.account.Permission;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entities.account.RolePermissionRel;
import com.tatsinktechnologic.dao_controller.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.dao_controller.exceptions.RollbackFailureException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
public class PermissionJpaController implements Serializable {

    @Inject
    EntityManagerFactory emf;
    
    @Resource
    private UserTransaction utx;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Permission permission) throws RollbackFailureException, Exception {
        if (permission.getRolePermissionRels() == null) {
            permission.setRolePermissionRels(new HashSet<RolePermissionRel>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Set<RolePermissionRel> attachedRolePermissionRels = new HashSet<RolePermissionRel>();
            for (RolePermissionRel rolePermissionRelsRolePermissionRelToAttach : permission.getRolePermissionRels()) {
                rolePermissionRelsRolePermissionRelToAttach = em.getReference(rolePermissionRelsRolePermissionRelToAttach.getClass(), rolePermissionRelsRolePermissionRelToAttach.getRole_permission_id());
                attachedRolePermissionRels.add(rolePermissionRelsRolePermissionRelToAttach);
            }
            permission.setRolePermissionRels(attachedRolePermissionRels);
            em.persist(permission);
            for (RolePermissionRel rolePermissionRelsRolePermissionRel : permission.getRolePermissionRels()) {
                Permission oldPermissionOfRolePermissionRelsRolePermissionRel = rolePermissionRelsRolePermissionRel.getPermission();
                rolePermissionRelsRolePermissionRel.setPermission(permission);
                rolePermissionRelsRolePermissionRel = em.merge(rolePermissionRelsRolePermissionRel);
                if (oldPermissionOfRolePermissionRelsRolePermissionRel != null) {
                    oldPermissionOfRolePermissionRelsRolePermissionRel.getRolePermissionRels().remove(rolePermissionRelsRolePermissionRel);
                    oldPermissionOfRolePermissionRelsRolePermissionRel = em.merge(oldPermissionOfRolePermissionRelsRolePermissionRel);
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

    public void edit(Permission permission) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Permission persistentPermission = em.find(Permission.class, permission.getPermission_id());
            Set<RolePermissionRel> rolePermissionRelsOld = persistentPermission.getRolePermissionRels();
            Set<RolePermissionRel> rolePermissionRelsNew = permission.getRolePermissionRels();
            Set<RolePermissionRel> attachedRolePermissionRelsNew = new HashSet<RolePermissionRel>();
            for (RolePermissionRel rolePermissionRelsNewRolePermissionRelToAttach : rolePermissionRelsNew) {
                rolePermissionRelsNewRolePermissionRelToAttach = em.getReference(rolePermissionRelsNewRolePermissionRelToAttach.getClass(), rolePermissionRelsNewRolePermissionRelToAttach.getRole_permission_id());
                attachedRolePermissionRelsNew.add(rolePermissionRelsNewRolePermissionRelToAttach);
            }
            rolePermissionRelsNew = attachedRolePermissionRelsNew;
            permission.setRolePermissionRels(rolePermissionRelsNew);
            permission = em.merge(permission);
            for (RolePermissionRel rolePermissionRelsOldRolePermissionRel : rolePermissionRelsOld) {
                if (!rolePermissionRelsNew.contains(rolePermissionRelsOldRolePermissionRel)) {
                    rolePermissionRelsOldRolePermissionRel.setPermission(null);
                    rolePermissionRelsOldRolePermissionRel = em.merge(rolePermissionRelsOldRolePermissionRel);
                }
            }
            for (RolePermissionRel rolePermissionRelsNewRolePermissionRel : rolePermissionRelsNew) {
                if (!rolePermissionRelsOld.contains(rolePermissionRelsNewRolePermissionRel)) {
                    Permission oldPermissionOfRolePermissionRelsNewRolePermissionRel = rolePermissionRelsNewRolePermissionRel.getPermission();
                    rolePermissionRelsNewRolePermissionRel.setPermission(permission);
                    rolePermissionRelsNewRolePermissionRel = em.merge(rolePermissionRelsNewRolePermissionRel);
                    if (oldPermissionOfRolePermissionRelsNewRolePermissionRel != null && !oldPermissionOfRolePermissionRelsNewRolePermissionRel.equals(permission)) {
                        oldPermissionOfRolePermissionRelsNewRolePermissionRel.getRolePermissionRels().remove(rolePermissionRelsNewRolePermissionRel);
                        oldPermissionOfRolePermissionRelsNewRolePermissionRel = em.merge(oldPermissionOfRolePermissionRelsNewRolePermissionRel);
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
                Integer id = permission.getPermission_id();
                if (findPermission(id) == null) {
                    throw new NonexistentEntityException("The permission with id " + id + " no longer exists.");
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
            Permission permission;
            try {
                permission = em.getReference(Permission.class, id);
                permission.getPermission_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The permission with id " + id + " no longer exists.", enfe);
            }
            Set<RolePermissionRel> rolePermissionRels = permission.getRolePermissionRels();
            for (RolePermissionRel rolePermissionRelsRolePermissionRel : rolePermissionRels) {
                rolePermissionRelsRolePermissionRel.setPermission(null);
                rolePermissionRelsRolePermissionRel = em.merge(rolePermissionRelsRolePermissionRel);
            }
            em.remove(permission);
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

    public List<Permission> findPermissionEntities() {
        return findPermissionEntities(true, -1, -1);
    }

    public List<Permission> findPermissionEntities(int maxResults, int firstResult) {
        return findPermissionEntities(false, maxResults, firstResult);
    }

    private List<Permission> findPermissionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Permission.class));
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

    public Permission findPermission(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Permission.class, id);
        } finally {
            em.close();
        }
    }

    public int getPermissionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Permission> rt = cq.from(Permission.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
