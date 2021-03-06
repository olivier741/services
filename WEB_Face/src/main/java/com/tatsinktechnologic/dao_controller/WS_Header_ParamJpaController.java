/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_controller;

import com.tatsinktechnologic.dao_controller.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.dao_controller.exceptions.RollbackFailureException;
import com.tatsinktechnologic.entities.api_gateway.WS_Header_Param;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

/**
 *
 * @author olivier
 */
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class WS_Header_ParamJpaController implements Serializable {

    @Inject
    EntityManagerFactory emf;
    
    @Resource
    private UserTransaction utx;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(WS_Header_Param WS_Header_Param) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            em.persist(WS_Header_Param);
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

    public void edit(WS_Header_Param WS_Header_Param) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            WS_Header_Param = em.merge(WS_Header_Param);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = WS_Header_Param.getWs_header_param_id();
                if (findWS_Header_Param(id) == null) {
                    throw new NonexistentEntityException("The wS_Header_Param with id " + id + " no longer exists.");
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
            WS_Header_Param WS_Header_Param;
            try {
                WS_Header_Param = em.getReference(WS_Header_Param.class, id);
                WS_Header_Param.getWs_header_param_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The WS_Header_Param with id " + id + " no longer exists.", enfe);
            }
            em.remove(WS_Header_Param);
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

    public List<WS_Header_Param> findWS_Header_ParamEntities() {
        return findWS_Header_ParamEntities(true, -1, -1);
    }

    public List<WS_Header_Param> findWS_Header_ParamEntities(int maxResults, int firstResult) {
        return findWS_Header_ParamEntities(false, maxResults, firstResult);
    }

    private List<WS_Header_Param> findWS_Header_ParamEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(WS_Header_Param.class));
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

    public WS_Header_Param findWS_Header_Param(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(WS_Header_Param.class, id);
        } finally {
            em.close();
        }
    }

    public int getWS_Header_ParamCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<WS_Header_Param> rt = cq.from(WS_Header_Param.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
