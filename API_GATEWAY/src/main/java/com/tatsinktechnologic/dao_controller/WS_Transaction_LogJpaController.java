/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_controller;

import com.tatsinktechnologic.dao_controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entities.api_gateway.WS_AccessManagement;
import com.tatsinktechnologic.entities.api_gateway.WS_Transaction_Log;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class WS_Transaction_LogJpaController implements Serializable {

    public WS_Transaction_LogJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(WS_Transaction_Log WS_Transaction_Log) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WS_AccessManagement access_management = WS_Transaction_Log.getAccess_management();
            if (access_management != null) {
                access_management = em.getReference(access_management.getClass(), access_management.getWs_access_mng_id());
                WS_Transaction_Log.setAccess_management(access_management);
            }
            em.persist(WS_Transaction_Log);
            if (access_management != null) {
                access_management.getListtransaction_log().add(WS_Transaction_Log);
                access_management = em.merge(access_management);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(WS_Transaction_Log WS_Transaction_Log) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WS_Transaction_Log persistentWS_Transaction_Log = em.find(WS_Transaction_Log.class, WS_Transaction_Log.getWs_transaction_id());
            WS_AccessManagement access_managementOld = persistentWS_Transaction_Log.getAccess_management();
            WS_AccessManagement access_managementNew = WS_Transaction_Log.getAccess_management();
            if (access_managementNew != null) {
                access_managementNew = em.getReference(access_managementNew.getClass(), access_managementNew.getWs_access_mng_id());
                WS_Transaction_Log.setAccess_management(access_managementNew);
            }
            WS_Transaction_Log = em.merge(WS_Transaction_Log);
            if (access_managementOld != null && !access_managementOld.equals(access_managementNew)) {
                access_managementOld.getListtransaction_log().remove(WS_Transaction_Log);
                access_managementOld = em.merge(access_managementOld);
            }
            if (access_managementNew != null && !access_managementNew.equals(access_managementOld)) {
                access_managementNew.getListtransaction_log().add(WS_Transaction_Log);
                access_managementNew = em.merge(access_managementNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = WS_Transaction_Log.getWs_transaction_id();
                if (findWS_Transaction_Log(id) == null) {
                    throw new NonexistentEntityException("The wS_Transaction_Log with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WS_Transaction_Log WS_Transaction_Log;
            try {
                WS_Transaction_Log = em.getReference(WS_Transaction_Log.class, id);
                WS_Transaction_Log.getWs_transaction_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The WS_Transaction_Log with id " + id + " no longer exists.", enfe);
            }
            WS_AccessManagement access_management = WS_Transaction_Log.getAccess_management();
            if (access_management != null) {
                access_management.getListtransaction_log().remove(WS_Transaction_Log);
                access_management = em.merge(access_management);
            }
            em.remove(WS_Transaction_Log);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<WS_Transaction_Log> findWS_Transaction_LogEntities() {
        return findWS_Transaction_LogEntities(true, -1, -1);
    }

    public List<WS_Transaction_Log> findWS_Transaction_LogEntities(int maxResults, int firstResult) {
        return findWS_Transaction_LogEntities(false, maxResults, firstResult);
    }

    private List<WS_Transaction_Log> findWS_Transaction_LogEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(WS_Transaction_Log.class));
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

    public WS_Transaction_Log findWS_Transaction_Log(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(WS_Transaction_Log.class, id);
        } finally {
            em.close();
        }
    }

    public int getWS_Transaction_LogCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<WS_Transaction_Log> rt = cq.from(WS_Transaction_Log.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
