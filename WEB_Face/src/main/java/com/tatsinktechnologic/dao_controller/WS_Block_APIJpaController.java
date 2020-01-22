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
import com.tatsinktechnologic.entities.api_gateway.WS_AccessManagement;
import com.tatsinktechnologic.entities.api_gateway.WS_Block_API;
import java.util.HashSet;
import java.util.Set;
import com.tatsinktechnologic.entities.api_gateway.WS_Block_WebserviceRel;
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
public class WS_Block_APIJpaController implements Serializable {

    @Inject
    EntityManagerFactory emf;
    
    @Resource
    private UserTransaction utx;



    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(WS_Block_API WS_Block_API) throws RollbackFailureException, Exception {
        if (WS_Block_API.getListAccessManagement() == null) {
            WS_Block_API.setListAccessManagement(new HashSet<WS_AccessManagement>());
        }
        if (WS_Block_API.getListWS_Block_WebserviceRel() == null) {
            WS_Block_API.setListWS_Block_WebserviceRel(new HashSet<WS_Block_WebserviceRel>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Set<WS_AccessManagement> attachedListAccessManagement = new HashSet<WS_AccessManagement>();
            for (WS_AccessManagement listAccessManagementWS_AccessManagementToAttach : WS_Block_API.getListAccessManagement()) {
                listAccessManagementWS_AccessManagementToAttach = em.getReference(listAccessManagementWS_AccessManagementToAttach.getClass(), listAccessManagementWS_AccessManagementToAttach.getWs_access_mng_id());
                attachedListAccessManagement.add(listAccessManagementWS_AccessManagementToAttach);
            }
            WS_Block_API.setListAccessManagement(attachedListAccessManagement);
            Set<WS_Block_WebserviceRel> attachedListWS_Block_WebserviceRel = new HashSet<WS_Block_WebserviceRel>();
            for (WS_Block_WebserviceRel listWS_Block_WebserviceRelWS_Block_WebserviceRelToAttach : WS_Block_API.getListWS_Block_WebserviceRel()) {
                listWS_Block_WebserviceRelWS_Block_WebserviceRelToAttach = em.getReference(listWS_Block_WebserviceRelWS_Block_WebserviceRelToAttach.getClass(), listWS_Block_WebserviceRelWS_Block_WebserviceRelToAttach.getWs_Block_WebserviceRel_id());
                attachedListWS_Block_WebserviceRel.add(listWS_Block_WebserviceRelWS_Block_WebserviceRelToAttach);
            }
            WS_Block_API.setListWS_Block_WebserviceRel(attachedListWS_Block_WebserviceRel);
            em.persist(WS_Block_API);
            for (WS_AccessManagement listAccessManagementWS_AccessManagement : WS_Block_API.getListAccessManagement()) {
                WS_Block_API oldWs_block_apiOfListAccessManagementWS_AccessManagement = listAccessManagementWS_AccessManagement.getWs_block_api();
                listAccessManagementWS_AccessManagement.setWs_block_api(WS_Block_API);
                listAccessManagementWS_AccessManagement = em.merge(listAccessManagementWS_AccessManagement);
                if (oldWs_block_apiOfListAccessManagementWS_AccessManagement != null) {
                    oldWs_block_apiOfListAccessManagementWS_AccessManagement.getListAccessManagement().remove(listAccessManagementWS_AccessManagement);
                    oldWs_block_apiOfListAccessManagementWS_AccessManagement = em.merge(oldWs_block_apiOfListAccessManagementWS_AccessManagement);
                }
            }
            for (WS_Block_WebserviceRel listWS_Block_WebserviceRelWS_Block_WebserviceRel : WS_Block_API.getListWS_Block_WebserviceRel()) {
                WS_Block_API oldWs_block_apiOfListWS_Block_WebserviceRelWS_Block_WebserviceRel = listWS_Block_WebserviceRelWS_Block_WebserviceRel.getWs_block_api();
                listWS_Block_WebserviceRelWS_Block_WebserviceRel.setWs_block_api(WS_Block_API);
                listWS_Block_WebserviceRelWS_Block_WebserviceRel = em.merge(listWS_Block_WebserviceRelWS_Block_WebserviceRel);
                if (oldWs_block_apiOfListWS_Block_WebserviceRelWS_Block_WebserviceRel != null) {
                    oldWs_block_apiOfListWS_Block_WebserviceRelWS_Block_WebserviceRel.getListWS_Block_WebserviceRel().remove(listWS_Block_WebserviceRelWS_Block_WebserviceRel);
                    oldWs_block_apiOfListWS_Block_WebserviceRelWS_Block_WebserviceRel = em.merge(oldWs_block_apiOfListWS_Block_WebserviceRelWS_Block_WebserviceRel);
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

    public void edit(WS_Block_API WS_Block_API) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            WS_Block_API persistentWS_Block_API = em.find(WS_Block_API.class, WS_Block_API.getWs_block_api_id());
            Set<WS_AccessManagement> listAccessManagementOld = persistentWS_Block_API.getListAccessManagement();
            Set<WS_AccessManagement> listAccessManagementNew = WS_Block_API.getListAccessManagement();
            Set<WS_Block_WebserviceRel> listWS_Block_WebserviceRelOld = persistentWS_Block_API.getListWS_Block_WebserviceRel();
            Set<WS_Block_WebserviceRel> listWS_Block_WebserviceRelNew = WS_Block_API.getListWS_Block_WebserviceRel();
            Set<WS_AccessManagement> attachedListAccessManagementNew = new HashSet<WS_AccessManagement>();
            for (WS_AccessManagement listAccessManagementNewWS_AccessManagementToAttach : listAccessManagementNew) {
                listAccessManagementNewWS_AccessManagementToAttach = em.getReference(listAccessManagementNewWS_AccessManagementToAttach.getClass(), listAccessManagementNewWS_AccessManagementToAttach.getWs_access_mng_id());
                attachedListAccessManagementNew.add(listAccessManagementNewWS_AccessManagementToAttach);
            }
            listAccessManagementNew = attachedListAccessManagementNew;
            WS_Block_API.setListAccessManagement(listAccessManagementNew);
            Set<WS_Block_WebserviceRel> attachedListWS_Block_WebserviceRelNew = new HashSet<WS_Block_WebserviceRel>();
            for (WS_Block_WebserviceRel listWS_Block_WebserviceRelNewWS_Block_WebserviceRelToAttach : listWS_Block_WebserviceRelNew) {
                listWS_Block_WebserviceRelNewWS_Block_WebserviceRelToAttach = em.getReference(listWS_Block_WebserviceRelNewWS_Block_WebserviceRelToAttach.getClass(), listWS_Block_WebserviceRelNewWS_Block_WebserviceRelToAttach.getWs_Block_WebserviceRel_id());
                attachedListWS_Block_WebserviceRelNew.add(listWS_Block_WebserviceRelNewWS_Block_WebserviceRelToAttach);
            }
            listWS_Block_WebserviceRelNew = attachedListWS_Block_WebserviceRelNew;
            WS_Block_API.setListWS_Block_WebserviceRel(listWS_Block_WebserviceRelNew);
            WS_Block_API = em.merge(WS_Block_API);
            for (WS_AccessManagement listAccessManagementOldWS_AccessManagement : listAccessManagementOld) {
                if (!listAccessManagementNew.contains(listAccessManagementOldWS_AccessManagement)) {
                    listAccessManagementOldWS_AccessManagement.setWs_block_api(null);
                    listAccessManagementOldWS_AccessManagement = em.merge(listAccessManagementOldWS_AccessManagement);
                }
            }
            for (WS_AccessManagement listAccessManagementNewWS_AccessManagement : listAccessManagementNew) {
                if (!listAccessManagementOld.contains(listAccessManagementNewWS_AccessManagement)) {
                    WS_Block_API oldWs_block_apiOfListAccessManagementNewWS_AccessManagement = listAccessManagementNewWS_AccessManagement.getWs_block_api();
                    listAccessManagementNewWS_AccessManagement.setWs_block_api(WS_Block_API);
                    listAccessManagementNewWS_AccessManagement = em.merge(listAccessManagementNewWS_AccessManagement);
                    if (oldWs_block_apiOfListAccessManagementNewWS_AccessManagement != null && !oldWs_block_apiOfListAccessManagementNewWS_AccessManagement.equals(WS_Block_API)) {
                        oldWs_block_apiOfListAccessManagementNewWS_AccessManagement.getListAccessManagement().remove(listAccessManagementNewWS_AccessManagement);
                        oldWs_block_apiOfListAccessManagementNewWS_AccessManagement = em.merge(oldWs_block_apiOfListAccessManagementNewWS_AccessManagement);
                    }
                }
            }
            for (WS_Block_WebserviceRel listWS_Block_WebserviceRelOldWS_Block_WebserviceRel : listWS_Block_WebserviceRelOld) {
                if (!listWS_Block_WebserviceRelNew.contains(listWS_Block_WebserviceRelOldWS_Block_WebserviceRel)) {
                    listWS_Block_WebserviceRelOldWS_Block_WebserviceRel.setWs_block_api(null);
                    listWS_Block_WebserviceRelOldWS_Block_WebserviceRel = em.merge(listWS_Block_WebserviceRelOldWS_Block_WebserviceRel);
                }
            }
            for (WS_Block_WebserviceRel listWS_Block_WebserviceRelNewWS_Block_WebserviceRel : listWS_Block_WebserviceRelNew) {
                if (!listWS_Block_WebserviceRelOld.contains(listWS_Block_WebserviceRelNewWS_Block_WebserviceRel)) {
                    WS_Block_API oldWs_block_apiOfListWS_Block_WebserviceRelNewWS_Block_WebserviceRel = listWS_Block_WebserviceRelNewWS_Block_WebserviceRel.getWs_block_api();
                    listWS_Block_WebserviceRelNewWS_Block_WebserviceRel.setWs_block_api(WS_Block_API);
                    listWS_Block_WebserviceRelNewWS_Block_WebserviceRel = em.merge(listWS_Block_WebserviceRelNewWS_Block_WebserviceRel);
                    if (oldWs_block_apiOfListWS_Block_WebserviceRelNewWS_Block_WebserviceRel != null && !oldWs_block_apiOfListWS_Block_WebserviceRelNewWS_Block_WebserviceRel.equals(WS_Block_API)) {
                        oldWs_block_apiOfListWS_Block_WebserviceRelNewWS_Block_WebserviceRel.getListWS_Block_WebserviceRel().remove(listWS_Block_WebserviceRelNewWS_Block_WebserviceRel);
                        oldWs_block_apiOfListWS_Block_WebserviceRelNewWS_Block_WebserviceRel = em.merge(oldWs_block_apiOfListWS_Block_WebserviceRelNewWS_Block_WebserviceRel);
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
                Long id = WS_Block_API.getWs_block_api_id();
                if (findWS_Block_API(id) == null) {
                    throw new NonexistentEntityException("The wS_Block_API with id " + id + " no longer exists.");
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
            WS_Block_API WS_Block_API;
            try {
                WS_Block_API = em.getReference(WS_Block_API.class, id);
                WS_Block_API.getWs_block_api_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The WS_Block_API with id " + id + " no longer exists.", enfe);
            }
            Set<WS_AccessManagement> listAccessManagement = WS_Block_API.getListAccessManagement();
            for (WS_AccessManagement listAccessManagementWS_AccessManagement : listAccessManagement) {
                listAccessManagementWS_AccessManagement.setWs_block_api(null);
                listAccessManagementWS_AccessManagement = em.merge(listAccessManagementWS_AccessManagement);
            }
            Set<WS_Block_WebserviceRel> listWS_Block_WebserviceRel = WS_Block_API.getListWS_Block_WebserviceRel();
            for (WS_Block_WebserviceRel listWS_Block_WebserviceRelWS_Block_WebserviceRel : listWS_Block_WebserviceRel) {
                listWS_Block_WebserviceRelWS_Block_WebserviceRel.setWs_block_api(null);
                listWS_Block_WebserviceRelWS_Block_WebserviceRel = em.merge(listWS_Block_WebserviceRelWS_Block_WebserviceRel);
            }
            em.remove(WS_Block_API);
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

    public List<WS_Block_API> findWS_Block_APIEntities() {
        return findWS_Block_APIEntities(true, -1, -1);
    }

    public List<WS_Block_API> findWS_Block_APIEntities(int maxResults, int firstResult) {
        return findWS_Block_APIEntities(false, maxResults, firstResult);
    }

    private List<WS_Block_API> findWS_Block_APIEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(WS_Block_API.class));
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

    public WS_Block_API findWS_Block_API(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(WS_Block_API.class, id);
        } finally {
            em.close();
        }
    }

    public int getWS_Block_APICount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<WS_Block_API> rt = cq.from(WS_Block_API.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
