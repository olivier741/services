/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao;

import com.tatsinktechnologic.beans_entity.Content_Message;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.beans_entity.Product;
import com.tatsinktechnologic.dao.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class Content_MessageJpaController implements Serializable {

    public Content_MessageJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Content_Message content_Message) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Product product = content_Message.getProduct();
            if (product != null) {
                product = em.getReference(product.getClass(), product.getProduct_id());
                content_Message.setProduct(product);
            }
            em.persist(content_Message);
            if (product != null) {
                product.getListContent_Message().add(content_Message);
                product = em.merge(product);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Content_Message content_Message) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Content_Message persistentContent_Message = em.find(Content_Message.class, content_Message.getContent_msg_id());
            Product productOld = persistentContent_Message.getProduct();
            Product productNew = content_Message.getProduct();
            if (productNew != null) {
                productNew = em.getReference(productNew.getClass(), productNew.getProduct_id());
                content_Message.setProduct(productNew);
            }
            content_Message = em.merge(content_Message);
            if (productOld != null && !productOld.equals(productNew)) {
                productOld.getListContent_Message().remove(content_Message);
                productOld = em.merge(productOld);
            }
            if (productNew != null && !productNew.equals(productOld)) {
                productNew.getListContent_Message().add(content_Message);
                productNew = em.merge(productNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = content_Message.getContent_msg_id();
                if (findContent_Message(id) == null) {
                    throw new NonexistentEntityException("The content_Message with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Content_Message content_Message;
            try {
                content_Message = em.getReference(Content_Message.class, id);
                content_Message.getContent_msg_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The content_Message with id " + id + " no longer exists.", enfe);
            }
            Product product = content_Message.getProduct();
            if (product != null) {
                product.getListContent_Message().remove(content_Message);
                product = em.merge(product);
            }
            em.remove(content_Message);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Content_Message> findContent_MessageEntities() {
        return findContent_MessageEntities(true, -1, -1);
    }

    public List<Content_Message> findContent_MessageEntities(int maxResults, int firstResult) {
        return findContent_MessageEntities(false, maxResults, firstResult);
    }

    private List<Content_Message> findContent_MessageEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Content_Message.class));
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

    public Content_Message findContent_Message(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Content_Message.class, id);
        } finally {
            em.close();
        }
    }

    public int getContent_MessageCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Content_Message> rt = cq.from(Content_Message.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
