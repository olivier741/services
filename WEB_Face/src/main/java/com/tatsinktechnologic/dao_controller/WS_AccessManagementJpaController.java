/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_controller;

import com.tatsinktechnologic.dao_controller.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.dao_controller.exceptions.RollbackFailureException;
import com.tatsinktechnologic.entities.api_gateway.WS_AccessManagement;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entities.api_gateway.WS_Client;
import com.tatsinktechnologic.entities.api_gateway.WS_Block_API;
import com.tatsinktechnologic.entities.api_gateway.WS_Transaction_Log;
import java.util.HashSet;
import java.util.Set;
import com.tatsinktechnologic.entities.api_gateway.WS_Billing_Hist;
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
public class WS_AccessManagementJpaController implements Serializable {

    @Inject
    EntityManagerFactory emf;
    
    @Resource
    private UserTransaction utx;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(WS_AccessManagement WS_AccessManagement) throws RollbackFailureException, Exception {
        if (WS_AccessManagement.getListtransaction_log() == null) {
            WS_AccessManagement.setListtransaction_log(new HashSet<WS_Transaction_Log>());
        }
        if (WS_AccessManagement.getListbilling_hist() == null) {
            WS_AccessManagement.setListbilling_hist(new HashSet<WS_Billing_Hist>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            WS_Client ws_client = WS_AccessManagement.getWs_client();
            if (ws_client != null) {
                ws_client = em.getReference(ws_client.getClass(), ws_client.getWs_client_id());
                WS_AccessManagement.setWs_client(ws_client);
            }
            WS_Block_API ws_block_api = WS_AccessManagement.getWs_block_api();
            if (ws_block_api != null) {
                ws_block_api = em.getReference(ws_block_api.getClass(), ws_block_api.getWs_block_api_id());
                WS_AccessManagement.setWs_block_api(ws_block_api);
            }
            Set<WS_Transaction_Log> attachedListtransaction_log = new HashSet<WS_Transaction_Log>();
            for (WS_Transaction_Log listtransaction_logWS_Transaction_LogToAttach : WS_AccessManagement.getListtransaction_log()) {
                listtransaction_logWS_Transaction_LogToAttach = em.getReference(listtransaction_logWS_Transaction_LogToAttach.getClass(), listtransaction_logWS_Transaction_LogToAttach.getWs_transaction_id());
                attachedListtransaction_log.add(listtransaction_logWS_Transaction_LogToAttach);
            }
            WS_AccessManagement.setListtransaction_log(attachedListtransaction_log);
            Set<WS_Billing_Hist> attachedListbilling_hist = new HashSet<WS_Billing_Hist>();
            for (WS_Billing_Hist listbilling_histWS_Billing_HistToAttach : WS_AccessManagement.getListbilling_hist()) {
                listbilling_histWS_Billing_HistToAttach = em.getReference(listbilling_histWS_Billing_HistToAttach.getClass(), listbilling_histWS_Billing_HistToAttach.getBill_id());
                attachedListbilling_hist.add(listbilling_histWS_Billing_HistToAttach);
            }
            WS_AccessManagement.setListbilling_hist(attachedListbilling_hist);
            em.persist(WS_AccessManagement);
            if (ws_client != null) {
                ws_client.getListAccessManagement().add(WS_AccessManagement);
                ws_client = em.merge(ws_client);
            }
            if (ws_block_api != null) {
                ws_block_api.getListAccessManagement().add(WS_AccessManagement);
                ws_block_api = em.merge(ws_block_api);
            }
            for (WS_Transaction_Log listtransaction_logWS_Transaction_Log : WS_AccessManagement.getListtransaction_log()) {
                WS_AccessManagement oldAccess_managementOfListtransaction_logWS_Transaction_Log = listtransaction_logWS_Transaction_Log.getAccess_management();
                listtransaction_logWS_Transaction_Log.setAccess_management(WS_AccessManagement);
                listtransaction_logWS_Transaction_Log = em.merge(listtransaction_logWS_Transaction_Log);
                if (oldAccess_managementOfListtransaction_logWS_Transaction_Log != null) {
                    oldAccess_managementOfListtransaction_logWS_Transaction_Log.getListtransaction_log().remove(listtransaction_logWS_Transaction_Log);
                    oldAccess_managementOfListtransaction_logWS_Transaction_Log = em.merge(oldAccess_managementOfListtransaction_logWS_Transaction_Log);
                }
            }
            for (WS_Billing_Hist listbilling_histWS_Billing_Hist : WS_AccessManagement.getListbilling_hist()) {
                WS_AccessManagement oldAccess_managementOfListbilling_histWS_Billing_Hist = listbilling_histWS_Billing_Hist.getAccess_management();
                listbilling_histWS_Billing_Hist.setAccess_management(WS_AccessManagement);
                listbilling_histWS_Billing_Hist = em.merge(listbilling_histWS_Billing_Hist);
                if (oldAccess_managementOfListbilling_histWS_Billing_Hist != null) {
                    oldAccess_managementOfListbilling_histWS_Billing_Hist.getListbilling_hist().remove(listbilling_histWS_Billing_Hist);
                    oldAccess_managementOfListbilling_histWS_Billing_Hist = em.merge(oldAccess_managementOfListbilling_histWS_Billing_Hist);
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

    public void edit(WS_AccessManagement WS_AccessManagement) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            WS_AccessManagement persistentWS_AccessManagement = em.find(WS_AccessManagement.class, WS_AccessManagement.getWs_access_mng_id());
            WS_Client ws_clientOld = persistentWS_AccessManagement.getWs_client();
            WS_Client ws_clientNew = WS_AccessManagement.getWs_client();
            WS_Block_API ws_block_apiOld = persistentWS_AccessManagement.getWs_block_api();
            WS_Block_API ws_block_apiNew = WS_AccessManagement.getWs_block_api();
            Set<WS_Transaction_Log> listtransaction_logOld = persistentWS_AccessManagement.getListtransaction_log();
            Set<WS_Transaction_Log> listtransaction_logNew = WS_AccessManagement.getListtransaction_log();
            Set<WS_Billing_Hist> listbilling_histOld = persistentWS_AccessManagement.getListbilling_hist();
            Set<WS_Billing_Hist> listbilling_histNew = WS_AccessManagement.getListbilling_hist();
            if (ws_clientNew != null) {
                ws_clientNew = em.getReference(ws_clientNew.getClass(), ws_clientNew.getWs_client_id());
                WS_AccessManagement.setWs_client(ws_clientNew);
            }
            if (ws_block_apiNew != null) {
                ws_block_apiNew = em.getReference(ws_block_apiNew.getClass(), ws_block_apiNew.getWs_block_api_id());
                WS_AccessManagement.setWs_block_api(ws_block_apiNew);
            }
            Set<WS_Transaction_Log> attachedListtransaction_logNew = new HashSet<WS_Transaction_Log>();
            for (WS_Transaction_Log listtransaction_logNewWS_Transaction_LogToAttach : listtransaction_logNew) {
                listtransaction_logNewWS_Transaction_LogToAttach = em.getReference(listtransaction_logNewWS_Transaction_LogToAttach.getClass(), listtransaction_logNewWS_Transaction_LogToAttach.getWs_transaction_id());
                attachedListtransaction_logNew.add(listtransaction_logNewWS_Transaction_LogToAttach);
            }
            listtransaction_logNew = attachedListtransaction_logNew;
            WS_AccessManagement.setListtransaction_log(listtransaction_logNew);
            Set<WS_Billing_Hist> attachedListbilling_histNew = new HashSet<WS_Billing_Hist>();
            for (WS_Billing_Hist listbilling_histNewWS_Billing_HistToAttach : listbilling_histNew) {
                listbilling_histNewWS_Billing_HistToAttach = em.getReference(listbilling_histNewWS_Billing_HistToAttach.getClass(), listbilling_histNewWS_Billing_HistToAttach.getBill_id());
                attachedListbilling_histNew.add(listbilling_histNewWS_Billing_HistToAttach);
            }
            listbilling_histNew = attachedListbilling_histNew;
            WS_AccessManagement.setListbilling_hist(listbilling_histNew);
            WS_AccessManagement = em.merge(WS_AccessManagement);
            if (ws_clientOld != null && !ws_clientOld.equals(ws_clientNew)) {
                ws_clientOld.getListAccessManagement().remove(WS_AccessManagement);
                ws_clientOld = em.merge(ws_clientOld);
            }
            if (ws_clientNew != null && !ws_clientNew.equals(ws_clientOld)) {
                ws_clientNew.getListAccessManagement().add(WS_AccessManagement);
                ws_clientNew = em.merge(ws_clientNew);
            }
            if (ws_block_apiOld != null && !ws_block_apiOld.equals(ws_block_apiNew)) {
                ws_block_apiOld.getListAccessManagement().remove(WS_AccessManagement);
                ws_block_apiOld = em.merge(ws_block_apiOld);
            }
            if (ws_block_apiNew != null && !ws_block_apiNew.equals(ws_block_apiOld)) {
                ws_block_apiNew.getListAccessManagement().add(WS_AccessManagement);
                ws_block_apiNew = em.merge(ws_block_apiNew);
            }
            for (WS_Transaction_Log listtransaction_logOldWS_Transaction_Log : listtransaction_logOld) {
                if (!listtransaction_logNew.contains(listtransaction_logOldWS_Transaction_Log)) {
                    listtransaction_logOldWS_Transaction_Log.setAccess_management(null);
                    listtransaction_logOldWS_Transaction_Log = em.merge(listtransaction_logOldWS_Transaction_Log);
                }
            }
            for (WS_Transaction_Log listtransaction_logNewWS_Transaction_Log : listtransaction_logNew) {
                if (!listtransaction_logOld.contains(listtransaction_logNewWS_Transaction_Log)) {
                    WS_AccessManagement oldAccess_managementOfListtransaction_logNewWS_Transaction_Log = listtransaction_logNewWS_Transaction_Log.getAccess_management();
                    listtransaction_logNewWS_Transaction_Log.setAccess_management(WS_AccessManagement);
                    listtransaction_logNewWS_Transaction_Log = em.merge(listtransaction_logNewWS_Transaction_Log);
                    if (oldAccess_managementOfListtransaction_logNewWS_Transaction_Log != null && !oldAccess_managementOfListtransaction_logNewWS_Transaction_Log.equals(WS_AccessManagement)) {
                        oldAccess_managementOfListtransaction_logNewWS_Transaction_Log.getListtransaction_log().remove(listtransaction_logNewWS_Transaction_Log);
                        oldAccess_managementOfListtransaction_logNewWS_Transaction_Log = em.merge(oldAccess_managementOfListtransaction_logNewWS_Transaction_Log);
                    }
                }
            }
            for (WS_Billing_Hist listbilling_histOldWS_Billing_Hist : listbilling_histOld) {
                if (!listbilling_histNew.contains(listbilling_histOldWS_Billing_Hist)) {
                    listbilling_histOldWS_Billing_Hist.setAccess_management(null);
                    listbilling_histOldWS_Billing_Hist = em.merge(listbilling_histOldWS_Billing_Hist);
                }
            }
            for (WS_Billing_Hist listbilling_histNewWS_Billing_Hist : listbilling_histNew) {
                if (!listbilling_histOld.contains(listbilling_histNewWS_Billing_Hist)) {
                    WS_AccessManagement oldAccess_managementOfListbilling_histNewWS_Billing_Hist = listbilling_histNewWS_Billing_Hist.getAccess_management();
                    listbilling_histNewWS_Billing_Hist.setAccess_management(WS_AccessManagement);
                    listbilling_histNewWS_Billing_Hist = em.merge(listbilling_histNewWS_Billing_Hist);
                    if (oldAccess_managementOfListbilling_histNewWS_Billing_Hist != null && !oldAccess_managementOfListbilling_histNewWS_Billing_Hist.equals(WS_AccessManagement)) {
                        oldAccess_managementOfListbilling_histNewWS_Billing_Hist.getListbilling_hist().remove(listbilling_histNewWS_Billing_Hist);
                        oldAccess_managementOfListbilling_histNewWS_Billing_Hist = em.merge(oldAccess_managementOfListbilling_histNewWS_Billing_Hist);
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
                Long id = WS_AccessManagement.getWs_access_mng_id();
                if (findWS_AccessManagement(id) == null) {
                    throw new NonexistentEntityException("The wS_AccessManagement with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            WS_AccessManagement WS_AccessManagement;
            try {
                WS_AccessManagement = em.getReference(WS_AccessManagement.class, id);
                WS_AccessManagement.getWs_access_mng_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The WS_AccessManagement with id " + id + " no longer exists.", enfe);
            }
            WS_Client ws_client = WS_AccessManagement.getWs_client();
            if (ws_client != null) {
                ws_client.getListAccessManagement().remove(WS_AccessManagement);
                ws_client = em.merge(ws_client);
            }
            WS_Block_API ws_block_api = WS_AccessManagement.getWs_block_api();
            if (ws_block_api != null) {
                ws_block_api.getListAccessManagement().remove(WS_AccessManagement);
                ws_block_api = em.merge(ws_block_api);
            }
            Set<WS_Transaction_Log> listtransaction_log = WS_AccessManagement.getListtransaction_log();
            for (WS_Transaction_Log listtransaction_logWS_Transaction_Log : listtransaction_log) {
                listtransaction_logWS_Transaction_Log.setAccess_management(null);
                listtransaction_logWS_Transaction_Log = em.merge(listtransaction_logWS_Transaction_Log);
            }
            Set<WS_Billing_Hist> listbilling_hist = WS_AccessManagement.getListbilling_hist();
            for (WS_Billing_Hist listbilling_histWS_Billing_Hist : listbilling_hist) {
                listbilling_histWS_Billing_Hist.setAccess_management(null);
                listbilling_histWS_Billing_Hist = em.merge(listbilling_histWS_Billing_Hist);
            }
            em.remove(WS_AccessManagement);
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

    public List<WS_AccessManagement> findWS_AccessManagementEntities() {
        return findWS_AccessManagementEntities(true, -1, -1);
    }

    public List<WS_AccessManagement> findWS_AccessManagementEntities(int maxResults, int firstResult) {
        return findWS_AccessManagementEntities(false, maxResults, firstResult);
    }

    private List<WS_AccessManagement> findWS_AccessManagementEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(WS_AccessManagement.class));
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

    public WS_AccessManagement findWS_AccessManagement(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(WS_AccessManagement.class, id);
        } finally {
            em.close();
        }
    }

    public int getWS_AccessManagementCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<WS_AccessManagement> rt = cq.from(WS_AccessManagement.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
