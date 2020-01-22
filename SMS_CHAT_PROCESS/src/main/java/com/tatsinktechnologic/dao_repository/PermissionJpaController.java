/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_repository;

import com.tatsinktechnologic.dao_repository.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.entity.account.Permission;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entity.account.RolePermissionRel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class PermissionJpaController implements Serializable {

    public PermissionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Permission permission) {
        if (permission.getRolePermissionRels() == null) {
            permission.setRolePermissionRels(new HashSet<RolePermissionRel>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
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
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Permission permission) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
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
            em.getTransaction().commit();
        } catch (Exception ex) {
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

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
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
            em.getTransaction().commit();
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
