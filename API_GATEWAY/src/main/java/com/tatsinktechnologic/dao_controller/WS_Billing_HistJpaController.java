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
import com.tatsinktechnologic.entities.api_gateway.WS_Billing_Hist;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class WS_Billing_HistJpaController implements Serializable {

    public WS_Billing_HistJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(WS_Billing_Hist WS_Billing_Hist) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WS_AccessManagement access_management = WS_Billing_Hist.getAccess_management();
            if (access_management != null) {
                access_management = em.getReference(access_management.getClass(), access_management.getWs_access_mng_id());
                WS_Billing_Hist.setAccess_management(access_management);
            }
            em.persist(WS_Billing_Hist);
            if (access_management != null) {
                access_management.getListbilling_hist().add(WS_Billing_Hist);
                access_management = em.merge(access_management);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(WS_Billing_Hist WS_Billing_Hist) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WS_Billing_Hist persistentWS_Billing_Hist = em.find(WS_Billing_Hist.class, WS_Billing_Hist.getBill_id());
            WS_AccessManagement access_managementOld = persistentWS_Billing_Hist.getAccess_management();
            WS_AccessManagement access_managementNew = WS_Billing_Hist.getAccess_management();
            if (access_managementNew != null) {
                access_managementNew = em.getReference(access_managementNew.getClass(), access_managementNew.getWs_access_mng_id());
                WS_Billing_Hist.setAccess_management(access_managementNew);
            }
            WS_Billing_Hist = em.merge(WS_Billing_Hist);
            if (access_managementOld != null && !access_managementOld.equals(access_managementNew)) {
                access_managementOld.getListbilling_hist().remove(WS_Billing_Hist);
                access_managementOld = em.merge(access_managementOld);
            }
            if (access_managementNew != null && !access_managementNew.equals(access_managementOld)) {
                access_managementNew.getListbilling_hist().add(WS_Billing_Hist);
                access_managementNew = em.merge(access_managementNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = WS_Billing_Hist.getBill_id();
                if (findWS_Billing_Hist(id) == null) {
                    throw new NonexistentEntityException("The wS_Billing_Hist with id " + id + " no longer exists.");
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
            WS_Billing_Hist WS_Billing_Hist;
            try {
                WS_Billing_Hist = em.getReference(WS_Billing_Hist.class, id);
                WS_Billing_Hist.getBill_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The WS_Billing_Hist with id " + id + " no longer exists.", enfe);
            }
            WS_AccessManagement access_management = WS_Billing_Hist.getAccess_management();
            if (access_management != null) {
                access_management.getListbilling_hist().remove(WS_Billing_Hist);
                access_management = em.merge(access_management);
            }
            em.remove(WS_Billing_Hist);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<WS_Billing_Hist> findWS_Billing_HistEntities() {
        return findWS_Billing_HistEntities(true, -1, -1);
    }

    public List<WS_Billing_Hist> findWS_Billing_HistEntities(int maxResults, int firstResult) {
        return findWS_Billing_HistEntities(false, maxResults, firstResult);
    }

    private List<WS_Billing_Hist> findWS_Billing_HistEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(WS_Billing_Hist.class));
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

    public WS_Billing_Hist findWS_Billing_Hist(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(WS_Billing_Hist.class, id);
        } finally {
            em.close();
        }
    }

    public int getWS_Billing_HistCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<WS_Billing_Hist> rt = cq.from(WS_Billing_Hist.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
