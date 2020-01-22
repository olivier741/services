/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_controller;

import com.tatsinktechnologic.dao_controller.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.dao_controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entities.api_gateway.WS_Block_API;
import com.tatsinktechnologic.entities.api_gateway.WS_Block_WebserviceRel;
import com.tatsinktechnologic.entities.api_gateway.WS_Webservice;
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
public class WS_Block_WebserviceRelJpaController implements Serializable {

    @Inject
    EntityManagerFactory emf;
    
    @Resource
    private UserTransaction utx;
    
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(WS_Block_WebserviceRel WS_Block_WebserviceRel) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            WS_Block_API ws_block_api = WS_Block_WebserviceRel.getWs_block_api();
            if (ws_block_api != null) {
                ws_block_api = em.getReference(ws_block_api.getClass(), ws_block_api.getWs_block_api_id());
                WS_Block_WebserviceRel.setWs_block_api(ws_block_api);
            }
            WS_Webservice ws_webservice = WS_Block_WebserviceRel.getWs_webservice();
            if (ws_webservice != null) {
                ws_webservice = em.getReference(ws_webservice.getClass(), ws_webservice.getWs_webservice_id());
                WS_Block_WebserviceRel.setWs_webservice(ws_webservice);
            }
            em.persist(WS_Block_WebserviceRel);
            if (ws_block_api != null) {
                ws_block_api.getListWS_Block_WebserviceRel().add(WS_Block_WebserviceRel);
                ws_block_api = em.merge(ws_block_api);
            }
            if (ws_webservice != null) {
                ws_webservice.getListWS_Block_WebserviceRel().add(WS_Block_WebserviceRel);
                ws_webservice = em.merge(ws_webservice);
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

    public void edit(WS_Block_WebserviceRel WS_Block_WebserviceRel) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            WS_Block_WebserviceRel persistentWS_Block_WebserviceRel = em.find(WS_Block_WebserviceRel.class, WS_Block_WebserviceRel.getWs_Block_WebserviceRel_id());
            WS_Block_API ws_block_apiOld = persistentWS_Block_WebserviceRel.getWs_block_api();
            WS_Block_API ws_block_apiNew = WS_Block_WebserviceRel.getWs_block_api();
            WS_Webservice ws_webserviceOld = persistentWS_Block_WebserviceRel.getWs_webservice();
            WS_Webservice ws_webserviceNew = WS_Block_WebserviceRel.getWs_webservice();
            if (ws_block_apiNew != null) {
                ws_block_apiNew = em.getReference(ws_block_apiNew.getClass(), ws_block_apiNew.getWs_block_api_id());
                WS_Block_WebserviceRel.setWs_block_api(ws_block_apiNew);
            }
            if (ws_webserviceNew != null) {
                ws_webserviceNew = em.getReference(ws_webserviceNew.getClass(), ws_webserviceNew.getWs_webservice_id());
                WS_Block_WebserviceRel.setWs_webservice(ws_webserviceNew);
            }
            WS_Block_WebserviceRel = em.merge(WS_Block_WebserviceRel);
            if (ws_block_apiOld != null && !ws_block_apiOld.equals(ws_block_apiNew)) {
                ws_block_apiOld.getListWS_Block_WebserviceRel().remove(WS_Block_WebserviceRel);
                ws_block_apiOld = em.merge(ws_block_apiOld);
            }
            if (ws_block_apiNew != null && !ws_block_apiNew.equals(ws_block_apiOld)) {
                ws_block_apiNew.getListWS_Block_WebserviceRel().add(WS_Block_WebserviceRel);
                ws_block_apiNew = em.merge(ws_block_apiNew);
            }
            if (ws_webserviceOld != null && !ws_webserviceOld.equals(ws_webserviceNew)) {
                ws_webserviceOld.getListWS_Block_WebserviceRel().remove(WS_Block_WebserviceRel);
                ws_webserviceOld = em.merge(ws_webserviceOld);
            }
            if (ws_webserviceNew != null && !ws_webserviceNew.equals(ws_webserviceOld)) {
                ws_webserviceNew.getListWS_Block_WebserviceRel().add(WS_Block_WebserviceRel);
                ws_webserviceNew = em.merge(ws_webserviceNew);
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
                Long id = WS_Block_WebserviceRel.getWs_Block_WebserviceRel_id();
                if (findWS_Block_WebserviceRel(id) == null) {
                    throw new NonexistentEntityException("The wS_Block_WebserviceRel with id " + id + " no longer exists.");
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
            WS_Block_WebserviceRel WS_Block_WebserviceRel;
            try {
                WS_Block_WebserviceRel = em.getReference(WS_Block_WebserviceRel.class, id);
                WS_Block_WebserviceRel.getWs_Block_WebserviceRel_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The WS_Block_WebserviceRel with id " + id + " no longer exists.", enfe);
            }
            WS_Block_API ws_block_api = WS_Block_WebserviceRel.getWs_block_api();
            if (ws_block_api != null) {
                ws_block_api.getListWS_Block_WebserviceRel().remove(WS_Block_WebserviceRel);
                ws_block_api = em.merge(ws_block_api);
            }
            WS_Webservice ws_webservice = WS_Block_WebserviceRel.getWs_webservice();
            if (ws_webservice != null) {
                ws_webservice.getListWS_Block_WebserviceRel().remove(WS_Block_WebserviceRel);
                ws_webservice = em.merge(ws_webservice);
            }
            em.remove(WS_Block_WebserviceRel);
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

    public List<WS_Block_WebserviceRel> findWS_Block_WebserviceRelEntities() {
        return findWS_Block_WebserviceRelEntities(true, -1, -1);
    }

    public List<WS_Block_WebserviceRel> findWS_Block_WebserviceRelEntities(int maxResults, int firstResult) {
        return findWS_Block_WebserviceRelEntities(false, maxResults, firstResult);
    }

    private List<WS_Block_WebserviceRel> findWS_Block_WebserviceRelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(WS_Block_WebserviceRel.class));
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

    public WS_Block_WebserviceRel findWS_Block_WebserviceRel(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(WS_Block_WebserviceRel.class, id);
        } finally {
            em.close();
        }
    }

    public int getWS_Block_WebserviceRelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<WS_Block_WebserviceRel> rt = cq.from(WS_Block_WebserviceRel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
