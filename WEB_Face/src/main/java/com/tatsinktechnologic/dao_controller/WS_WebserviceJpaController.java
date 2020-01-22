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
import com.tatsinktechnologic.entities.api_gateway.WS_Block_WebserviceRel;
import com.tatsinktechnologic.entities.api_gateway.WS_Webservice;
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
public class WS_WebserviceJpaController implements Serializable {

    @Inject
    EntityManagerFactory emf;
    
    @Resource
    private UserTransaction utx;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(WS_Webservice WS_Webservice) throws RollbackFailureException, Exception {
        if (WS_Webservice.getListWS_Block_WebserviceRel() == null) {
            WS_Webservice.setListWS_Block_WebserviceRel(new HashSet<WS_Block_WebserviceRel>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Set<WS_Block_WebserviceRel> attachedListWS_Block_WebserviceRel = new HashSet<WS_Block_WebserviceRel>();
            for (WS_Block_WebserviceRel listWS_Block_WebserviceRelWS_Block_WebserviceRelToAttach : WS_Webservice.getListWS_Block_WebserviceRel()) {
                listWS_Block_WebserviceRelWS_Block_WebserviceRelToAttach = em.getReference(listWS_Block_WebserviceRelWS_Block_WebserviceRelToAttach.getClass(), listWS_Block_WebserviceRelWS_Block_WebserviceRelToAttach.getWs_Block_WebserviceRel_id());
                attachedListWS_Block_WebserviceRel.add(listWS_Block_WebserviceRelWS_Block_WebserviceRelToAttach);
            }
            WS_Webservice.setListWS_Block_WebserviceRel(attachedListWS_Block_WebserviceRel);
            em.persist(WS_Webservice);
            for (WS_Block_WebserviceRel listWS_Block_WebserviceRelWS_Block_WebserviceRel : WS_Webservice.getListWS_Block_WebserviceRel()) {
                WS_Webservice oldWs_webserviceOfListWS_Block_WebserviceRelWS_Block_WebserviceRel = listWS_Block_WebserviceRelWS_Block_WebserviceRel.getWs_webservice();
                listWS_Block_WebserviceRelWS_Block_WebserviceRel.setWs_webservice(WS_Webservice);
                listWS_Block_WebserviceRelWS_Block_WebserviceRel = em.merge(listWS_Block_WebserviceRelWS_Block_WebserviceRel);
                if (oldWs_webserviceOfListWS_Block_WebserviceRelWS_Block_WebserviceRel != null) {
                    oldWs_webserviceOfListWS_Block_WebserviceRelWS_Block_WebserviceRel.getListWS_Block_WebserviceRel().remove(listWS_Block_WebserviceRelWS_Block_WebserviceRel);
                    oldWs_webserviceOfListWS_Block_WebserviceRelWS_Block_WebserviceRel = em.merge(oldWs_webserviceOfListWS_Block_WebserviceRelWS_Block_WebserviceRel);
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

    public void edit(WS_Webservice WS_Webservice) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            WS_Webservice persistentWS_Webservice = em.find(WS_Webservice.class, WS_Webservice.getWs_webservice_id());
            Set<WS_Block_WebserviceRel> listWS_Block_WebserviceRelOld = persistentWS_Webservice.getListWS_Block_WebserviceRel();
            Set<WS_Block_WebserviceRel> listWS_Block_WebserviceRelNew = WS_Webservice.getListWS_Block_WebserviceRel();
            Set<WS_Block_WebserviceRel> attachedListWS_Block_WebserviceRelNew = new HashSet<WS_Block_WebserviceRel>();
            for (WS_Block_WebserviceRel listWS_Block_WebserviceRelNewWS_Block_WebserviceRelToAttach : listWS_Block_WebserviceRelNew) {
                listWS_Block_WebserviceRelNewWS_Block_WebserviceRelToAttach = em.getReference(listWS_Block_WebserviceRelNewWS_Block_WebserviceRelToAttach.getClass(), listWS_Block_WebserviceRelNewWS_Block_WebserviceRelToAttach.getWs_Block_WebserviceRel_id());
                attachedListWS_Block_WebserviceRelNew.add(listWS_Block_WebserviceRelNewWS_Block_WebserviceRelToAttach);
            }
            listWS_Block_WebserviceRelNew = attachedListWS_Block_WebserviceRelNew;
            WS_Webservice.setListWS_Block_WebserviceRel(listWS_Block_WebserviceRelNew);
            WS_Webservice = em.merge(WS_Webservice);
            for (WS_Block_WebserviceRel listWS_Block_WebserviceRelOldWS_Block_WebserviceRel : listWS_Block_WebserviceRelOld) {
                if (!listWS_Block_WebserviceRelNew.contains(listWS_Block_WebserviceRelOldWS_Block_WebserviceRel)) {
                    listWS_Block_WebserviceRelOldWS_Block_WebserviceRel.setWs_webservice(null);
                    listWS_Block_WebserviceRelOldWS_Block_WebserviceRel = em.merge(listWS_Block_WebserviceRelOldWS_Block_WebserviceRel);
                }
            }
            for (WS_Block_WebserviceRel listWS_Block_WebserviceRelNewWS_Block_WebserviceRel : listWS_Block_WebserviceRelNew) {
                if (!listWS_Block_WebserviceRelOld.contains(listWS_Block_WebserviceRelNewWS_Block_WebserviceRel)) {
                    WS_Webservice oldWs_webserviceOfListWS_Block_WebserviceRelNewWS_Block_WebserviceRel = listWS_Block_WebserviceRelNewWS_Block_WebserviceRel.getWs_webservice();
                    listWS_Block_WebserviceRelNewWS_Block_WebserviceRel.setWs_webservice(WS_Webservice);
                    listWS_Block_WebserviceRelNewWS_Block_WebserviceRel = em.merge(listWS_Block_WebserviceRelNewWS_Block_WebserviceRel);
                    if (oldWs_webserviceOfListWS_Block_WebserviceRelNewWS_Block_WebserviceRel != null && !oldWs_webserviceOfListWS_Block_WebserviceRelNewWS_Block_WebserviceRel.equals(WS_Webservice)) {
                        oldWs_webserviceOfListWS_Block_WebserviceRelNewWS_Block_WebserviceRel.getListWS_Block_WebserviceRel().remove(listWS_Block_WebserviceRelNewWS_Block_WebserviceRel);
                        oldWs_webserviceOfListWS_Block_WebserviceRelNewWS_Block_WebserviceRel = em.merge(oldWs_webserviceOfListWS_Block_WebserviceRelNewWS_Block_WebserviceRel);
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
                Long id = WS_Webservice.getWs_webservice_id();
                if (findWS_Webservice(id) == null) {
                    throw new NonexistentEntityException("The wS_Webservice with id " + id + " no longer exists.");
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
            WS_Webservice WS_Webservice;
            try {
                WS_Webservice = em.getReference(WS_Webservice.class, id);
                WS_Webservice.getWs_webservice_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The WS_Webservice with id " + id + " no longer exists.", enfe);
            }
            Set<WS_Block_WebserviceRel> listWS_Block_WebserviceRel = WS_Webservice.getListWS_Block_WebserviceRel();
            for (WS_Block_WebserviceRel listWS_Block_WebserviceRelWS_Block_WebserviceRel : listWS_Block_WebserviceRel) {
                listWS_Block_WebserviceRelWS_Block_WebserviceRel.setWs_webservice(null);
                listWS_Block_WebserviceRelWS_Block_WebserviceRel = em.merge(listWS_Block_WebserviceRelWS_Block_WebserviceRel);
            }
            em.remove(WS_Webservice);
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

    public List<WS_Webservice> findWS_WebserviceEntities() {
        return findWS_WebserviceEntities(true, -1, -1);
    }

    public List<WS_Webservice> findWS_WebserviceEntities(int maxResults, int firstResult) {
        return findWS_WebserviceEntities(false, maxResults, firstResult);
    }

    private List<WS_Webservice> findWS_WebserviceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(WS_Webservice.class));
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

    public WS_Webservice findWS_Webservice(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(WS_Webservice.class, id);
        } finally {
            em.close();
        }
    }

    public int getWS_WebserviceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<WS_Webservice> rt = cq.from(WS_Webservice.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
