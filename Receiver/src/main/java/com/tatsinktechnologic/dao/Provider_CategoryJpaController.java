/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.beans_entity.Provider;
import com.tatsinktechnologic.beans_entity.Provider_Category;
import com.tatsinktechnologic.dao.exceptions.IllegalOrphanException;
import com.tatsinktechnologic.dao.exceptions.NonexistentEntityException;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class Provider_CategoryJpaController implements Serializable {

    public Provider_CategoryJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Provider_Category provider_Category) {
        if (provider_Category.getListProvider() == null) {
            provider_Category.setListProvider(new HashSet<Provider>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Set<Provider> attachedListProvider = new HashSet<Provider>();
            for (Provider listProviderProviderToAttach : provider_Category.getListProvider()) {
                listProviderProviderToAttach = em.getReference(listProviderProviderToAttach.getClass(), listProviderProviderToAttach.getProvider_id());
                attachedListProvider.add(listProviderProviderToAttach);
            }
            provider_Category.setListProvider(attachedListProvider);
            em.persist(provider_Category);
            for (Provider listProviderProvider : provider_Category.getListProvider()) {
                Provider_Category oldProvider_categoryOfListProviderProvider = listProviderProvider.getProvider_category();
                listProviderProvider.setProvider_category(provider_Category);
                listProviderProvider = em.merge(listProviderProvider);
                if (oldProvider_categoryOfListProviderProvider != null) {
                    oldProvider_categoryOfListProviderProvider.getListProvider().remove(listProviderProvider);
                    oldProvider_categoryOfListProviderProvider = em.merge(oldProvider_categoryOfListProviderProvider);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Provider_Category provider_Category) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Provider_Category persistentProvider_Category = em.find(Provider_Category.class, provider_Category.getProvider_category_id());
            Set<Provider> listProviderOld = persistentProvider_Category.getListProvider();
            Set<Provider> listProviderNew = provider_Category.getListProvider();
            List<String> illegalOrphanMessages = null;
            for (Provider listProviderOldProvider : listProviderOld) {
                if (!listProviderNew.contains(listProviderOldProvider)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Provider " + listProviderOldProvider + " since its provider_category field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Set<Provider> attachedListProviderNew = new HashSet<Provider>();
            for (Provider listProviderNewProviderToAttach : listProviderNew) {
                listProviderNewProviderToAttach = em.getReference(listProviderNewProviderToAttach.getClass(), listProviderNewProviderToAttach.getProvider_id());
                attachedListProviderNew.add(listProviderNewProviderToAttach);
            }
            listProviderNew = attachedListProviderNew;
            provider_Category.setListProvider(listProviderNew);
            provider_Category = em.merge(provider_Category);
            for (Provider listProviderNewProvider : listProviderNew) {
                if (!listProviderOld.contains(listProviderNewProvider)) {
                    Provider_Category oldProvider_categoryOfListProviderNewProvider = listProviderNewProvider.getProvider_category();
                    listProviderNewProvider.setProvider_category(provider_Category);
                    listProviderNewProvider = em.merge(listProviderNewProvider);
                    if (oldProvider_categoryOfListProviderNewProvider != null && !oldProvider_categoryOfListProviderNewProvider.equals(provider_Category)) {
                        oldProvider_categoryOfListProviderNewProvider.getListProvider().remove(listProviderNewProvider);
                        oldProvider_categoryOfListProviderNewProvider = em.merge(oldProvider_categoryOfListProviderNewProvider);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = provider_Category.getProvider_category_id();
                if (findProvider_Category(id) == null) {
                    throw new NonexistentEntityException("The provider_Category with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Provider_Category provider_Category;
            try {
                provider_Category = em.getReference(Provider_Category.class, id);
                provider_Category.getProvider_category_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The provider_Category with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Set<Provider> listProviderOrphanCheck = provider_Category.getListProvider();
            for (Provider listProviderOrphanCheckProvider : listProviderOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Provider_Category (" + provider_Category + ") cannot be destroyed since the Provider " + listProviderOrphanCheckProvider + " in its listProvider field has a non-nullable provider_category field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(provider_Category);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Provider_Category> findProvider_CategoryEntities() {
        return findProvider_CategoryEntities(true, -1, -1);
    }

    public List<Provider_Category> findProvider_CategoryEntities(int maxResults, int firstResult) {
        return findProvider_CategoryEntities(false, maxResults, firstResult);
    }

    private List<Provider_Category> findProvider_CategoryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Provider_Category.class));
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

    public Provider_Category findProvider_Category(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Provider_Category.class, id);
        } finally {
            em.close();
        }
    }

    public int getProvider_CategoryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Provider_Category> rt = cq.from(Provider_Category.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
