/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_repository;

import com.tatsinktechnologic.dao_repository.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.entity.sms_chat.Mo_Push_His;
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
public class Mo_Push_HisJpaController implements Serializable {

    public Mo_Push_HisJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Mo_Push_His mo_Push_His) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(mo_Push_His);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Mo_Push_His mo_Push_His) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            mo_Push_His = em.merge(mo_Push_His);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = mo_Push_His.getMo_push_his_id();
                if (findMo_Push_His(id) == null) {
                    throw new NonexistentEntityException("The mo_Push_His with id " + id + " no longer exists.");
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
            Mo_Push_His mo_Push_His;
            try {
                mo_Push_His = em.getReference(Mo_Push_His.class, id);
                mo_Push_His.getMo_push_his_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The mo_Push_His with id " + id + " no longer exists.", enfe);
            }
            em.remove(mo_Push_His);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Mo_Push_His> findMo_Push_HisEntities() {
        return findMo_Push_HisEntities(true, -1, -1);
    }

    public List<Mo_Push_His> findMo_Push_HisEntities(int maxResults, int firstResult) {
        return findMo_Push_HisEntities(false, maxResults, firstResult);
    }

    private List<Mo_Push_His> findMo_Push_HisEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Mo_Push_His.class));
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

    public Mo_Push_His findMo_Push_His(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Mo_Push_His.class, id);
        } finally {
            em.close();
        }
    }

    public int getMo_Push_HisCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Mo_Push_His> rt = cq.from(Mo_Push_His.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
