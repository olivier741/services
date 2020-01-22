/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_controller;

import com.tatsinktechnologic.entities.account.Role;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entities.account.UserRoleRel;
import java.util.HashSet;
import java.util.Set;
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
public class RoleJpaController implements Serializable {

    @Inject
    EntityManagerFactory emf;
    
    @Resource
    private UserTransaction utx;


    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Role role) throws RollbackFailureException, Exception {
        if (role.getUserRoleRels() == null) {
            role.setUserRoleRels(new HashSet<UserRoleRel>());
        }
        if (role.getRolePermissionRels() == null) {
            role.setRolePermissionRels(new HashSet<RolePermissionRel>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Set<UserRoleRel> attachedUserRoleRels = new HashSet<UserRoleRel>();
            for (UserRoleRel userRoleRelsUserRoleRelToAttach : role.getUserRoleRels()) {
                userRoleRelsUserRoleRelToAttach = em.getReference(userRoleRelsUserRoleRelToAttach.getClass(), userRoleRelsUserRoleRelToAttach.getUser_role_id());
                attachedUserRoleRels.add(userRoleRelsUserRoleRelToAttach);
            }
            role.setUserRoleRels(attachedUserRoleRels);
            Set<RolePermissionRel> attachedRolePermissionRels = new HashSet<RolePermissionRel>();
            for (RolePermissionRel rolePermissionRelsRolePermissionRelToAttach : role.getRolePermissionRels()) {
                rolePermissionRelsRolePermissionRelToAttach = em.getReference(rolePermissionRelsRolePermissionRelToAttach.getClass(), rolePermissionRelsRolePermissionRelToAttach.getRole_permission_id());
                attachedRolePermissionRels.add(rolePermissionRelsRolePermissionRelToAttach);
            }
            role.setRolePermissionRels(attachedRolePermissionRels);
            em.persist(role);
            for (UserRoleRel userRoleRelsUserRoleRel : role.getUserRoleRels()) {
                Role oldRoleOfUserRoleRelsUserRoleRel = userRoleRelsUserRoleRel.getRole();
                userRoleRelsUserRoleRel.setRole(role);
                userRoleRelsUserRoleRel = em.merge(userRoleRelsUserRoleRel);
                if (oldRoleOfUserRoleRelsUserRoleRel != null) {
                    oldRoleOfUserRoleRelsUserRoleRel.getUserRoleRels().remove(userRoleRelsUserRoleRel);
                    oldRoleOfUserRoleRelsUserRoleRel = em.merge(oldRoleOfUserRoleRelsUserRoleRel);
                }
            }
            for (RolePermissionRel rolePermissionRelsRolePermissionRel : role.getRolePermissionRels()) {
                Role oldRoleOfRolePermissionRelsRolePermissionRel = rolePermissionRelsRolePermissionRel.getRole();
                rolePermissionRelsRolePermissionRel.setRole(role);
                rolePermissionRelsRolePermissionRel = em.merge(rolePermissionRelsRolePermissionRel);
                if (oldRoleOfRolePermissionRelsRolePermissionRel != null) {
                    oldRoleOfRolePermissionRelsRolePermissionRel.getRolePermissionRels().remove(rolePermissionRelsRolePermissionRel);
                    oldRoleOfRolePermissionRelsRolePermissionRel = em.merge(oldRoleOfRolePermissionRelsRolePermissionRel);
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

    public void edit(Role role) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Role persistentRole = em.find(Role.class, role.getRole_id());
            Set<UserRoleRel> userRoleRelsOld = persistentRole.getUserRoleRels();
            Set<UserRoleRel> userRoleRelsNew = role.getUserRoleRels();
            Set<RolePermissionRel> rolePermissionRelsOld = persistentRole.getRolePermissionRels();
            Set<RolePermissionRel> rolePermissionRelsNew = role.getRolePermissionRels();
            Set<UserRoleRel> attachedUserRoleRelsNew = new HashSet<UserRoleRel>();
            for (UserRoleRel userRoleRelsNewUserRoleRelToAttach : userRoleRelsNew) {
                userRoleRelsNewUserRoleRelToAttach = em.getReference(userRoleRelsNewUserRoleRelToAttach.getClass(), userRoleRelsNewUserRoleRelToAttach.getUser_role_id());
                attachedUserRoleRelsNew.add(userRoleRelsNewUserRoleRelToAttach);
            }
            userRoleRelsNew = attachedUserRoleRelsNew;
            role.setUserRoleRels(userRoleRelsNew);
            Set<RolePermissionRel> attachedRolePermissionRelsNew = new HashSet<RolePermissionRel>();
            for (RolePermissionRel rolePermissionRelsNewRolePermissionRelToAttach : rolePermissionRelsNew) {
                rolePermissionRelsNewRolePermissionRelToAttach = em.getReference(rolePermissionRelsNewRolePermissionRelToAttach.getClass(), rolePermissionRelsNewRolePermissionRelToAttach.getRole_permission_id());
                attachedRolePermissionRelsNew.add(rolePermissionRelsNewRolePermissionRelToAttach);
            }
            rolePermissionRelsNew = attachedRolePermissionRelsNew;
            role.setRolePermissionRels(rolePermissionRelsNew);
            role = em.merge(role);
            for (UserRoleRel userRoleRelsOldUserRoleRel : userRoleRelsOld) {
                if (!userRoleRelsNew.contains(userRoleRelsOldUserRoleRel)) {
                    userRoleRelsOldUserRoleRel.setRole(null);
                    userRoleRelsOldUserRoleRel = em.merge(userRoleRelsOldUserRoleRel);
                }
            }
            for (UserRoleRel userRoleRelsNewUserRoleRel : userRoleRelsNew) {
                if (!userRoleRelsOld.contains(userRoleRelsNewUserRoleRel)) {
                    Role oldRoleOfUserRoleRelsNewUserRoleRel = userRoleRelsNewUserRoleRel.getRole();
                    userRoleRelsNewUserRoleRel.setRole(role);
                    userRoleRelsNewUserRoleRel = em.merge(userRoleRelsNewUserRoleRel);
                    if (oldRoleOfUserRoleRelsNewUserRoleRel != null && !oldRoleOfUserRoleRelsNewUserRoleRel.equals(role)) {
                        oldRoleOfUserRoleRelsNewUserRoleRel.getUserRoleRels().remove(userRoleRelsNewUserRoleRel);
                        oldRoleOfUserRoleRelsNewUserRoleRel = em.merge(oldRoleOfUserRoleRelsNewUserRoleRel);
                    }
                }
            }
            for (RolePermissionRel rolePermissionRelsOldRolePermissionRel : rolePermissionRelsOld) {
                if (!rolePermissionRelsNew.contains(rolePermissionRelsOldRolePermissionRel)) {
                    rolePermissionRelsOldRolePermissionRel.setRole(null);
                    rolePermissionRelsOldRolePermissionRel = em.merge(rolePermissionRelsOldRolePermissionRel);
                }
            }
            for (RolePermissionRel rolePermissionRelsNewRolePermissionRel : rolePermissionRelsNew) {
                if (!rolePermissionRelsOld.contains(rolePermissionRelsNewRolePermissionRel)) {
                    Role oldRoleOfRolePermissionRelsNewRolePermissionRel = rolePermissionRelsNewRolePermissionRel.getRole();
                    rolePermissionRelsNewRolePermissionRel.setRole(role);
                    rolePermissionRelsNewRolePermissionRel = em.merge(rolePermissionRelsNewRolePermissionRel);
                    if (oldRoleOfRolePermissionRelsNewRolePermissionRel != null && !oldRoleOfRolePermissionRelsNewRolePermissionRel.equals(role)) {
                        oldRoleOfRolePermissionRelsNewRolePermissionRel.getRolePermissionRels().remove(rolePermissionRelsNewRolePermissionRel);
                        oldRoleOfRolePermissionRelsNewRolePermissionRel = em.merge(oldRoleOfRolePermissionRelsNewRolePermissionRel);
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
                Integer id = role.getRole_id();
                if (findRole(id) == null) {
                    throw new NonexistentEntityException("The role with id " + id + " no longer exists.");
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
            Role role;
            try {
                role = em.getReference(Role.class, id);
                role.getRole_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The role with id " + id + " no longer exists.", enfe);
            }
            Set<UserRoleRel> userRoleRels = role.getUserRoleRels();
            for (UserRoleRel userRoleRelsUserRoleRel : userRoleRels) {
                userRoleRelsUserRoleRel.setRole(null);
                userRoleRelsUserRoleRel = em.merge(userRoleRelsUserRoleRel);
            }
            Set<RolePermissionRel> rolePermissionRels = role.getRolePermissionRels();
            for (RolePermissionRel rolePermissionRelsRolePermissionRel : rolePermissionRels) {
                rolePermissionRelsRolePermissionRel.setRole(null);
                rolePermissionRelsRolePermissionRel = em.merge(rolePermissionRelsRolePermissionRel);
            }
            em.remove(role);
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

    public List<Role> findRoleEntities() {
        return findRoleEntities(true, -1, -1);
    }

    public List<Role> findRoleEntities(int maxResults, int firstResult) {
        return findRoleEntities(false, maxResults, firstResult);
    }

    private List<Role> findRoleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Role.class));
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

    public Role findRole(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Role.class, id);
        } finally {
            em.close();
        }
    }

    public int getRoleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Role> rt = cq.from(Role.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
