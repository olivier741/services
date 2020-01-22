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
import com.tatsinktechnologic.entities.api_gateway.WS_Client;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class WS_ClientJpaController implements Serializable {

    public WS_ClientJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(WS_Client WS_Client) {
        if (WS_Client.getListAccessManagement() == null) {
            WS_Client.setListAccessManagement(new HashSet<WS_AccessManagement>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Set<WS_AccessManagement> attachedListAccessManagement = new HashSet<WS_AccessManagement>();
            for (WS_AccessManagement listAccessManagementWS_AccessManagementToAttach : WS_Client.getListAccessManagement()) {
                listAccessManagementWS_AccessManagementToAttach = em.getReference(listAccessManagementWS_AccessManagementToAttach.getClass(), listAccessManagementWS_AccessManagementToAttach.getWs_access_mng_id());
                attachedListAccessManagement.add(listAccessManagementWS_AccessManagementToAttach);
            }
            WS_Client.setListAccessManagement(attachedListAccessManagement);
            em.persist(WS_Client);
            for (WS_AccessManagement listAccessManagementWS_AccessManagement : WS_Client.getListAccessManagement()) {
                WS_Client oldWs_clientOfListAccessManagementWS_AccessManagement = listAccessManagementWS_AccessManagement.getWs_client();
                listAccessManagementWS_AccessManagement.setWs_client(WS_Client);
                listAccessManagementWS_AccessManagement = em.merge(listAccessManagementWS_AccessManagement);
                if (oldWs_clientOfListAccessManagementWS_AccessManagement != null) {
                    oldWs_clientOfListAccessManagementWS_AccessManagement.getListAccessManagement().remove(listAccessManagementWS_AccessManagement);
                    oldWs_clientOfListAccessManagementWS_AccessManagement = em.merge(oldWs_clientOfListAccessManagementWS_AccessManagement);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(WS_Client WS_Client) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WS_Client persistentWS_Client = em.find(WS_Client.class, WS_Client.getWs_client_id());
            Set<WS_AccessManagement> listAccessManagementOld = persistentWS_Client.getListAccessManagement();
            Set<WS_AccessManagement> listAccessManagementNew = WS_Client.getListAccessManagement();
            Set<WS_AccessManagement> attachedListAccessManagementNew = new HashSet<WS_AccessManagement>();
            for (WS_AccessManagement listAccessManagementNewWS_AccessManagementToAttach : listAccessManagementNew) {
                listAccessManagementNewWS_AccessManagementToAttach = em.getReference(listAccessManagementNewWS_AccessManagementToAttach.getClass(), listAccessManagementNewWS_AccessManagementToAttach.getWs_access_mng_id());
                attachedListAccessManagementNew.add(listAccessManagementNewWS_AccessManagementToAttach);
            }
            listAccessManagementNew = attachedListAccessManagementNew;
            WS_Client.setListAccessManagement(listAccessManagementNew);
            WS_Client = em.merge(WS_Client);
            for (WS_AccessManagement listAccessManagementOldWS_AccessManagement : listAccessManagementOld) {
                if (!listAccessManagementNew.contains(listAccessManagementOldWS_AccessManagement)) {
                    listAccessManagementOldWS_AccessManagement.setWs_client(null);
                    listAccessManagementOldWS_AccessManagement = em.merge(listAccessManagementOldWS_AccessManagement);
                }
            }
            for (WS_AccessManagement listAccessManagementNewWS_AccessManagement : listAccessManagementNew) {
                if (!listAccessManagementOld.contains(listAccessManagementNewWS_AccessManagement)) {
                    WS_Client oldWs_clientOfListAccessManagementNewWS_AccessManagement = listAccessManagementNewWS_AccessManagement.getWs_client();
                    listAccessManagementNewWS_AccessManagement.setWs_client(WS_Client);
                    listAccessManagementNewWS_AccessManagement = em.merge(listAccessManagementNewWS_AccessManagement);
                    if (oldWs_clientOfListAccessManagementNewWS_AccessManagement != null && !oldWs_clientOfListAccessManagementNewWS_AccessManagement.equals(WS_Client)) {
                        oldWs_clientOfListAccessManagementNewWS_AccessManagement.getListAccessManagement().remove(listAccessManagementNewWS_AccessManagement);
                        oldWs_clientOfListAccessManagementNewWS_AccessManagement = em.merge(oldWs_clientOfListAccessManagementNewWS_AccessManagement);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = WS_Client.getWs_client_id();
                if (findWS_Client(id) == null) {
                    throw new NonexistentEntityException("The wS_Client with id " + id + " no longer exists.");
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
            WS_Client WS_Client;
            try {
                WS_Client = em.getReference(WS_Client.class, id);
                WS_Client.getWs_client_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The WS_Client with id " + id + " no longer exists.", enfe);
            }
            Set<WS_AccessManagement> listAccessManagement = WS_Client.getListAccessManagement();
            for (WS_AccessManagement listAccessManagementWS_AccessManagement : listAccessManagement) {
                listAccessManagementWS_AccessManagement.setWs_client(null);
                listAccessManagementWS_AccessManagement = em.merge(listAccessManagementWS_AccessManagement);
            }
            em.remove(WS_Client);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<WS_Client> findWS_ClientEntities() {
        return findWS_ClientEntities(true, -1, -1);
    }

    public List<WS_Client> findWS_ClientEntities(int maxResults, int firstResult) {
        return findWS_ClientEntities(false, maxResults, firstResult);
    }

    private List<WS_Client> findWS_ClientEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(WS_Client.class));
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

    public WS_Client findWS_Client(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(WS_Client.class, id);
        } finally {
            em.close();
        }
    }

    public int getWS_ClientCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<WS_Client> rt = cq.from(WS_Client.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
