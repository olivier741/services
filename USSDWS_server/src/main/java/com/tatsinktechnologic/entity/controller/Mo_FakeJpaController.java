/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entity.controller;

import com.tatsinktechnologic.entity.Mo_Fake;
import com.tatsinktechnologic.entity.controller.exceptions.NonexistentEntityException;
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
public class Mo_FakeJpaController implements Serializable {

    public Mo_FakeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Mo_Fake mo_Fake) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(mo_Fake);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Mo_Fake mo_Fake) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            mo_Fake = em.merge(mo_Fake);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = mo_Fake.getMo_fake_id();
                if (findMo_Fake(id) == null) {
                    throw new NonexistentEntityException("The mo_Fake with id " + id + " no longer exists.");
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
            Mo_Fake mo_Fake;
            try {
                mo_Fake = em.getReference(Mo_Fake.class, id);
                mo_Fake.getMo_fake_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The mo_Fake with id " + id + " no longer exists.", enfe);
            }
            em.remove(mo_Fake);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Mo_Fake> findMo_FakeEntities() {
        return findMo_FakeEntities(true, -1, -1);
    }

    public List<Mo_Fake> findMo_FakeEntities(int maxResults, int firstResult) {
        return findMo_FakeEntities(false, maxResults, firstResult);
    }

    private List<Mo_Fake> findMo_FakeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Mo_Fake.class));
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

    public Mo_Fake findMo_Fake(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Mo_Fake.class, id);
        } finally {
            em.close();
        }
    }

    public int getMo_FakeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Mo_Fake> rt = cq.from(Mo_Fake.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
