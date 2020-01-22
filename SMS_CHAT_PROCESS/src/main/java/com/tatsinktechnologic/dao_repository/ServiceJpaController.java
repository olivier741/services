/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_repository;

import com.tatsinktechnologic.dao_repository.exceptions.IllegalOrphanException;
import com.tatsinktechnologic.dao_repository.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entity.register.Product;
import com.tatsinktechnologic.entity.register.Service;
import java.util.HashSet;
import java.util.Set;
import com.tatsinktechnologic.entity.sms_chat.Alias;
import com.tatsinktechnologic.entity.sms_chat.ContentMessage;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class ServiceJpaController implements Serializable {

    public ServiceJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Service service) {
        if (service.getListProduct() == null) {
            service.setListProduct(new HashSet<Product>());
        }
        if (service.getListAlias() == null) {
            service.setListAlias(new HashSet<Alias>());
        }
        if (service.getListContentMessage() == null) {
            service.setListContentMessage(new HashSet<ContentMessage>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Set<Product> attachedListProduct = new HashSet<Product>();
            for (Product listProductProductToAttach : service.getListProduct()) {
                listProductProductToAttach = em.getReference(listProductProductToAttach.getClass(), listProductProductToAttach.getProduct_id());
                attachedListProduct.add(listProductProductToAttach);
            }
            service.setListProduct(attachedListProduct);
            Set<Alias> attachedListAlias = new HashSet<Alias>();
            for (Alias listAliasAliasToAttach : service.getListAlias()) {
                listAliasAliasToAttach = em.getReference(listAliasAliasToAttach.getClass(), listAliasAliasToAttach.getAlias_id());
                attachedListAlias.add(listAliasAliasToAttach);
            }
            service.setListAlias(attachedListAlias);
            Set<ContentMessage> attachedListContentMessage = new HashSet<ContentMessage>();
            for (ContentMessage listContentMessageContentMessageToAttach : service.getListContentMessage()) {
                listContentMessageContentMessageToAttach = em.getReference(listContentMessageContentMessageToAttach.getClass(), listContentMessageContentMessageToAttach.getContent_id());
                attachedListContentMessage.add(listContentMessageContentMessageToAttach);
            }
            service.setListContentMessage(attachedListContentMessage);
            em.persist(service);
            for (Product listProductProduct : service.getListProduct()) {
                Service oldServiceOfListProductProduct = listProductProduct.getService();
                listProductProduct.setService(service);
                listProductProduct = em.merge(listProductProduct);
                if (oldServiceOfListProductProduct != null) {
                    oldServiceOfListProductProduct.getListProduct().remove(listProductProduct);
                    oldServiceOfListProductProduct = em.merge(oldServiceOfListProductProduct);
                }
            }
            for (Alias listAliasAlias : service.getListAlias()) {
                Service oldServiceOfListAliasAlias = listAliasAlias.getService();
                listAliasAlias.setService(service);
                listAliasAlias = em.merge(listAliasAlias);
                if (oldServiceOfListAliasAlias != null) {
                    oldServiceOfListAliasAlias.getListAlias().remove(listAliasAlias);
                    oldServiceOfListAliasAlias = em.merge(oldServiceOfListAliasAlias);
                }
            }
            for (ContentMessage listContentMessageContentMessage : service.getListContentMessage()) {
                Service oldServiceOfListContentMessageContentMessage = listContentMessageContentMessage.getService();
                listContentMessageContentMessage.setService(service);
                listContentMessageContentMessage = em.merge(listContentMessageContentMessage);
                if (oldServiceOfListContentMessageContentMessage != null) {
                    oldServiceOfListContentMessageContentMessage.getListContentMessage().remove(listContentMessageContentMessage);
                    oldServiceOfListContentMessageContentMessage = em.merge(oldServiceOfListContentMessageContentMessage);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Service service) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Service persistentService = em.find(Service.class, service.getService_id());
            Set<Product> listProductOld = persistentService.getListProduct();
            Set<Product> listProductNew = service.getListProduct();
            Set<Alias> listAliasOld = persistentService.getListAlias();
            Set<Alias> listAliasNew = service.getListAlias();
            Set<ContentMessage> listContentMessageOld = persistentService.getListContentMessage();
            Set<ContentMessage> listContentMessageNew = service.getListContentMessage();
            List<String> illegalOrphanMessages = null;
            for (ContentMessage listContentMessageOldContentMessage : listContentMessageOld) {
                if (!listContentMessageNew.contains(listContentMessageOldContentMessage)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ContentMessage " + listContentMessageOldContentMessage + " since its service field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Set<Product> attachedListProductNew = new HashSet<Product>();
            for (Product listProductNewProductToAttach : listProductNew) {
                listProductNewProductToAttach = em.getReference(listProductNewProductToAttach.getClass(), listProductNewProductToAttach.getProduct_id());
                attachedListProductNew.add(listProductNewProductToAttach);
            }
            listProductNew = attachedListProductNew;
            service.setListProduct(listProductNew);
            Set<Alias> attachedListAliasNew = new HashSet<Alias>();
            for (Alias listAliasNewAliasToAttach : listAliasNew) {
                listAliasNewAliasToAttach = em.getReference(listAliasNewAliasToAttach.getClass(), listAliasNewAliasToAttach.getAlias_id());
                attachedListAliasNew.add(listAliasNewAliasToAttach);
            }
            listAliasNew = attachedListAliasNew;
            service.setListAlias(listAliasNew);
            Set<ContentMessage> attachedListContentMessageNew = new HashSet<ContentMessage>();
            for (ContentMessage listContentMessageNewContentMessageToAttach : listContentMessageNew) {
                listContentMessageNewContentMessageToAttach = em.getReference(listContentMessageNewContentMessageToAttach.getClass(), listContentMessageNewContentMessageToAttach.getContent_id());
                attachedListContentMessageNew.add(listContentMessageNewContentMessageToAttach);
            }
            listContentMessageNew = attachedListContentMessageNew;
            service.setListContentMessage(listContentMessageNew);
            service = em.merge(service);
            for (Product listProductOldProduct : listProductOld) {
                if (!listProductNew.contains(listProductOldProduct)) {
                    listProductOldProduct.setService(null);
                    listProductOldProduct = em.merge(listProductOldProduct);
                }
            }
            for (Product listProductNewProduct : listProductNew) {
                if (!listProductOld.contains(listProductNewProduct)) {
                    Service oldServiceOfListProductNewProduct = listProductNewProduct.getService();
                    listProductNewProduct.setService(service);
                    listProductNewProduct = em.merge(listProductNewProduct);
                    if (oldServiceOfListProductNewProduct != null && !oldServiceOfListProductNewProduct.equals(service)) {
                        oldServiceOfListProductNewProduct.getListProduct().remove(listProductNewProduct);
                        oldServiceOfListProductNewProduct = em.merge(oldServiceOfListProductNewProduct);
                    }
                }
            }
            for (Alias listAliasOldAlias : listAliasOld) {
                if (!listAliasNew.contains(listAliasOldAlias)) {
                    listAliasOldAlias.setService(null);
                    listAliasOldAlias = em.merge(listAliasOldAlias);
                }
            }
            for (Alias listAliasNewAlias : listAliasNew) {
                if (!listAliasOld.contains(listAliasNewAlias)) {
                    Service oldServiceOfListAliasNewAlias = listAliasNewAlias.getService();
                    listAliasNewAlias.setService(service);
                    listAliasNewAlias = em.merge(listAliasNewAlias);
                    if (oldServiceOfListAliasNewAlias != null && !oldServiceOfListAliasNewAlias.equals(service)) {
                        oldServiceOfListAliasNewAlias.getListAlias().remove(listAliasNewAlias);
                        oldServiceOfListAliasNewAlias = em.merge(oldServiceOfListAliasNewAlias);
                    }
                }
            }
            for (ContentMessage listContentMessageNewContentMessage : listContentMessageNew) {
                if (!listContentMessageOld.contains(listContentMessageNewContentMessage)) {
                    Service oldServiceOfListContentMessageNewContentMessage = listContentMessageNewContentMessage.getService();
                    listContentMessageNewContentMessage.setService(service);
                    listContentMessageNewContentMessage = em.merge(listContentMessageNewContentMessage);
                    if (oldServiceOfListContentMessageNewContentMessage != null && !oldServiceOfListContentMessageNewContentMessage.equals(service)) {
                        oldServiceOfListContentMessageNewContentMessage.getListContentMessage().remove(listContentMessageNewContentMessage);
                        oldServiceOfListContentMessageNewContentMessage = em.merge(oldServiceOfListContentMessageNewContentMessage);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = service.getService_id();
                if (findService(id) == null) {
                    throw new NonexistentEntityException("The service with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Service service;
            try {
                service = em.getReference(Service.class, id);
                service.getService_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The service with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Set<ContentMessage> listContentMessageOrphanCheck = service.getListContentMessage();
            for (ContentMessage listContentMessageOrphanCheckContentMessage : listContentMessageOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Service (" + service + ") cannot be destroyed since the ContentMessage " + listContentMessageOrphanCheckContentMessage + " in its listContentMessage field has a non-nullable service field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Set<Product> listProduct = service.getListProduct();
            for (Product listProductProduct : listProduct) {
                listProductProduct.setService(null);
                listProductProduct = em.merge(listProductProduct);
            }
            Set<Alias> listAlias = service.getListAlias();
            for (Alias listAliasAlias : listAlias) {
                listAliasAlias.setService(null);
                listAliasAlias = em.merge(listAliasAlias);
            }
            em.remove(service);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Service> findServiceEntities() {
        return findServiceEntities(true, -1, -1);
    }

    public List<Service> findServiceEntities(int maxResults, int firstResult) {
        return findServiceEntities(false, maxResults, firstResult);
    }

    private List<Service> findServiceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Service.class));
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

    public Service findService(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Service.class, id);
        } finally {
            em.close();
        }
    }

    public int getServiceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Service> rt = cq.from(Service.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
