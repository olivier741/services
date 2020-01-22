/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_repository;

import com.tatsinktechnologic.dao_repository.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.entity.sms_chat.Mo_Push;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author olivier.tatsinkou
 */
public class Mo_PushJpaController implements Serializable {

    public Mo_PushJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Mo_Push mo_Push) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(mo_Push);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Mo_Push mo_Push) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            mo_Push = em.merge(mo_Push);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = mo_Push.getMo_push_id();
                if (findMo_Push(id) == null) {
                    throw new NonexistentEntityException("The mo_Push with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Mo_Push mo_Push;
            try {
                mo_Push = em.getReference(Mo_Push.class, id);
                mo_Push.getMo_push_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The mo_Push with id " + id + " no longer exists.", enfe);
            }
            em.remove(mo_Push);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Mo_Push> findMo_PushEntities() {
        return findMo_PushEntities(true, -1, -1);
    }

    public List<Mo_Push> findMo_PushEntities(int maxResults, int firstResult) {
        return findMo_PushEntities(false, maxResults, firstResult);
    }

    private List<Mo_Push> findMo_PushEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Mo_Push.class));
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

    public Mo_Push findMo_Push(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Mo_Push.class, id);
        } finally {
            em.close();
        }
    }

    public int getMo_PushCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Mo_Push> rt = cq.from(Mo_Push.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
