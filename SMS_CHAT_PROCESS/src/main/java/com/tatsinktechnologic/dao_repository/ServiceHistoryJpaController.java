/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_repository;

import com.tatsinktechnologic.dao_repository.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entity.sms_chat.ContentMessage;
import com.tatsinktechnologic.entity.sms_chat.ServiceHistory;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class ServiceHistoryJpaController implements Serializable {

    public ServiceHistoryJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ServiceHistory serviceHistory) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ContentMessage content = serviceHistory.getContent();
            if (content != null) {
                content = em.getReference(content.getClass(), content.getContent_id());
                serviceHistory.setContent(content);
            }
            em.persist(serviceHistory);
            if (content != null) {
                content.getListServiceHistory().add(serviceHistory);
                content = em.merge(content);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ServiceHistory serviceHistory) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ServiceHistory persistentServiceHistory = em.find(ServiceHistory.class, serviceHistory.getServiceHistory_id());
            ContentMessage contentOld = persistentServiceHistory.getContent();
            ContentMessage contentNew = serviceHistory.getContent();
            if (contentNew != null) {
                contentNew = em.getReference(contentNew.getClass(), contentNew.getContent_id());
                serviceHistory.setContent(contentNew);
            }
            serviceHistory = em.merge(serviceHistory);
            if (contentOld != null && !contentOld.equals(contentNew)) {
                contentOld.getListServiceHistory().remove(serviceHistory);
                contentOld = em.merge(contentOld);
            }
            if (contentNew != null && !contentNew.equals(contentOld)) {
                contentNew.getListServiceHistory().add(serviceHistory);
                contentNew = em.merge(contentNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = serviceHistory.getServiceHistory_id();
                if (findServiceHistory(id) == null) {
                    throw new NonexistentEntityException("The serviceHistory with id " + id + " no longer exists.");
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
            ServiceHistory serviceHistory;
            try {
                serviceHistory = em.getReference(ServiceHistory.class, id);
                serviceHistory.getServiceHistory_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The serviceHistory with id " + id + " no longer exists.", enfe);
            }
            ContentMessage content = serviceHistory.getContent();
            if (content != null) {
                content.getListServiceHistory().remove(serviceHistory);
                content = em.merge(content);
            }
            em.remove(serviceHistory);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ServiceHistory> findServiceHistoryEntities() {
        return findServiceHistoryEntities(true, -1, -1);
    }

    public List<ServiceHistory> findServiceHistoryEntities(int maxResults, int firstResult) {
        return findServiceHistoryEntities(false, maxResults, firstResult);
    }

    private List<ServiceHistory> findServiceHistoryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ServiceHistory.class));
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

    public ServiceHistory findServiceHistory(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ServiceHistory.class, id);
        } finally {
            em.close();
        }
    }

    public int getServiceHistoryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ServiceHistory> rt = cq.from(ServiceHistory.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
