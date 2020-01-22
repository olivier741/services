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
import com.tatsinktechnologic.entity.register.Service;
import com.tatsinktechnologic.entity.sms_chat.ContentMessage;
import com.tatsinktechnologic.entity.sms_chat.ServiceHistory;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class ContentMessageJpaController implements Serializable {

    public ContentMessageJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ContentMessage contentMessage) {
        if (contentMessage.getListServiceHistory() == null) {
            contentMessage.setListServiceHistory(new HashSet<ServiceHistory>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Service service = contentMessage.getService();
            if (service != null) {
                service = em.getReference(service.getClass(), service.getService_id());
                contentMessage.setService(service);
            }
            Set<ServiceHistory> attachedListServiceHistory = new HashSet<ServiceHistory>();
            for (ServiceHistory listServiceHistoryServiceHistoryToAttach : contentMessage.getListServiceHistory()) {
                listServiceHistoryServiceHistoryToAttach = em.getReference(listServiceHistoryServiceHistoryToAttach.getClass(), listServiceHistoryServiceHistoryToAttach.getServiceHistory_id());
                attachedListServiceHistory.add(listServiceHistoryServiceHistoryToAttach);
            }
            contentMessage.setListServiceHistory(attachedListServiceHistory);
            em.persist(contentMessage);
            if (service != null) {
                service.getListContentMessage().add(contentMessage);
                service = em.merge(service);
            }
            for (ServiceHistory listServiceHistoryServiceHistory : contentMessage.getListServiceHistory()) {
                ContentMessage oldContentOfListServiceHistoryServiceHistory = listServiceHistoryServiceHistory.getContent();
                listServiceHistoryServiceHistory.setContent(contentMessage);
                listServiceHistoryServiceHistory = em.merge(listServiceHistoryServiceHistory);
                if (oldContentOfListServiceHistoryServiceHistory != null) {
                    oldContentOfListServiceHistoryServiceHistory.getListServiceHistory().remove(listServiceHistoryServiceHistory);
                    oldContentOfListServiceHistoryServiceHistory = em.merge(oldContentOfListServiceHistoryServiceHistory);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ContentMessage contentMessage) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ContentMessage persistentContentMessage = em.find(ContentMessage.class, contentMessage.getContent_id());
            Service serviceOld = persistentContentMessage.getService();
            Service serviceNew = contentMessage.getService();
            Set<ServiceHistory> listServiceHistoryOld = persistentContentMessage.getListServiceHistory();
            Set<ServiceHistory> listServiceHistoryNew = contentMessage.getListServiceHistory();
            if (serviceNew != null) {
                serviceNew = em.getReference(serviceNew.getClass(), serviceNew.getService_id());
                contentMessage.setService(serviceNew);
            }
            Set<ServiceHistory> attachedListServiceHistoryNew = new HashSet<ServiceHistory>();
            for (ServiceHistory listServiceHistoryNewServiceHistoryToAttach : listServiceHistoryNew) {
                listServiceHistoryNewServiceHistoryToAttach = em.getReference(listServiceHistoryNewServiceHistoryToAttach.getClass(), listServiceHistoryNewServiceHistoryToAttach.getServiceHistory_id());
                attachedListServiceHistoryNew.add(listServiceHistoryNewServiceHistoryToAttach);
            }
            listServiceHistoryNew = attachedListServiceHistoryNew;
            contentMessage.setListServiceHistory(listServiceHistoryNew);
            contentMessage = em.merge(contentMessage);
            if (serviceOld != null && !serviceOld.equals(serviceNew)) {
                serviceOld.getListContentMessage().remove(contentMessage);
                serviceOld = em.merge(serviceOld);
            }
            if (serviceNew != null && !serviceNew.equals(serviceOld)) {
                serviceNew.getListContentMessage().add(contentMessage);
                serviceNew = em.merge(serviceNew);
            }
            for (ServiceHistory listServiceHistoryOldServiceHistory : listServiceHistoryOld) {
                if (!listServiceHistoryNew.contains(listServiceHistoryOldServiceHistory)) {
                    listServiceHistoryOldServiceHistory.setContent(null);
                    listServiceHistoryOldServiceHistory = em.merge(listServiceHistoryOldServiceHistory);
                }
            }
            for (ServiceHistory listServiceHistoryNewServiceHistory : listServiceHistoryNew) {
                if (!listServiceHistoryOld.contains(listServiceHistoryNewServiceHistory)) {
                    ContentMessage oldContentOfListServiceHistoryNewServiceHistory = listServiceHistoryNewServiceHistory.getContent();
                    listServiceHistoryNewServiceHistory.setContent(contentMessage);
                    listServiceHistoryNewServiceHistory = em.merge(listServiceHistoryNewServiceHistory);
                    if (oldContentOfListServiceHistoryNewServiceHistory != null && !oldContentOfListServiceHistoryNewServiceHistory.equals(contentMessage)) {
                        oldContentOfListServiceHistoryNewServiceHistory.getListServiceHistory().remove(listServiceHistoryNewServiceHistory);
                        oldContentOfListServiceHistoryNewServiceHistory = em.merge(oldContentOfListServiceHistoryNewServiceHistory);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = contentMessage.getContent_id();
                if (findContentMessage(id) == null) {
                    throw new NonexistentEntityException("The contentMessage with id " + id + " no longer exists.");
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
            ContentMessage contentMessage;
            try {
                contentMessage = em.getReference(ContentMessage.class, id);
                contentMessage.getContent_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The contentMessage with id " + id + " no longer exists.", enfe);
            }
            Service service = contentMessage.getService();
            if (service != null) {
                service.getListContentMessage().remove(contentMessage);
                service = em.merge(service);
            }
            Set<ServiceHistory> listServiceHistory = contentMessage.getListServiceHistory();
            for (ServiceHistory listServiceHistoryServiceHistory : listServiceHistory) {
                listServiceHistoryServiceHistory.setContent(null);
                listServiceHistoryServiceHistory = em.merge(listServiceHistoryServiceHistory);
            }
            em.remove(contentMessage);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ContentMessage> findContentMessageEntities() {
        return findContentMessageEntities(true, -1, -1);
    }

    public List<ContentMessage> findContentMessageEntities(int maxResults, int firstResult) {
        return findContentMessageEntities(false, maxResults, firstResult);
    }

    private List<ContentMessage> findContentMessageEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ContentMessage.class));
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

    public ContentMessage findContentMessage(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ContentMessage.class, id);
        } finally {
            em.close();
        }
    }

    public int getContentMessageCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ContentMessage> rt = cq.from(ContentMessage.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
