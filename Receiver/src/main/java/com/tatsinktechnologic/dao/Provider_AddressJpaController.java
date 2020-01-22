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
import com.tatsinktechnologic.beans_entity.Provider_Address;
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
public class Provider_AddressJpaController implements Serializable {

    public Provider_AddressJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Provider_Address provider_Address) {
        if (provider_Address.getListProvider() == null) {
            provider_Address.setListProvider(new HashSet<Provider>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Set<Provider> attachedListProvider = new HashSet<Provider>();
            for (Provider listProviderProviderToAttach : provider_Address.getListProvider()) {
                listProviderProviderToAttach = em.getReference(listProviderProviderToAttach.getClass(), listProviderProviderToAttach.getProvider_id());
                attachedListProvider.add(listProviderProviderToAttach);
            }
            provider_Address.setListProvider(attachedListProvider);
            em.persist(provider_Address);
            for (Provider listProviderProvider : provider_Address.getListProvider()) {
                Provider_Address oldProvider_addressOfListProviderProvider = listProviderProvider.getProvider_address();
                listProviderProvider.setProvider_address(provider_Address);
                listProviderProvider = em.merge(listProviderProvider);
                if (oldProvider_addressOfListProviderProvider != null) {
                    oldProvider_addressOfListProviderProvider.getListProvider().remove(listProviderProvider);
                    oldProvider_addressOfListProviderProvider = em.merge(oldProvider_addressOfListProviderProvider);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Provider_Address provider_Address) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Provider_Address persistentProvider_Address = em.find(Provider_Address.class, provider_Address.getProvider_address_id());
            Set<Provider> listProviderOld = persistentProvider_Address.getListProvider();
            Set<Provider> listProviderNew = provider_Address.getListProvider();
            List<String> illegalOrphanMessages = null;
            for (Provider listProviderOldProvider : listProviderOld) {
                if (!listProviderNew.contains(listProviderOldProvider)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Provider " + listProviderOldProvider + " since its provider_address field is not nullable.");
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
            provider_Address.setListProvider(listProviderNew);
            provider_Address = em.merge(provider_Address);
            for (Provider listProviderNewProvider : listProviderNew) {
                if (!listProviderOld.contains(listProviderNewProvider)) {
                    Provider_Address oldProvider_addressOfListProviderNewProvider = listProviderNewProvider.getProvider_address();
                    listProviderNewProvider.setProvider_address(provider_Address);
                    listProviderNewProvider = em.merge(listProviderNewProvider);
                    if (oldProvider_addressOfListProviderNewProvider != null && !oldProvider_addressOfListProviderNewProvider.equals(provider_Address)) {
                        oldProvider_addressOfListProviderNewProvider.getListProvider().remove(listProviderNewProvider);
                        oldProvider_addressOfListProviderNewProvider = em.merge(oldProvider_addressOfListProviderNewProvider);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = provider_Address.getProvider_address_id();
                if (findProvider_Address(id) == null) {
                    throw new NonexistentEntityException("The provider_Address with id " + id + " no longer exists.");
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
            Provider_Address provider_Address;
            try {
                provider_Address = em.getReference(Provider_Address.class, id);
                provider_Address.getProvider_address_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The provider_Address with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Set<Provider> listProviderOrphanCheck = provider_Address.getListProvider();
            for (Provider listProviderOrphanCheckProvider : listProviderOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Provider_Address (" + provider_Address + ") cannot be destroyed since the Provider " + listProviderOrphanCheckProvider + " in its listProvider field has a non-nullable provider_address field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(provider_Address);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Provider_Address> findProvider_AddressEntities() {
        return findProvider_AddressEntities(true, -1, -1);
    }

    public List<Provider_Address> findProvider_AddressEntities(int maxResults, int firstResult) {
        return findProvider_AddressEntities(false, maxResults, firstResult);
    }

    private List<Provider_Address> findProvider_AddressEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Provider_Address.class));
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

    public Provider_Address findProvider_Address(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Provider_Address.class, id);
        } finally {
            em.close();
        }
    }

    public int getProvider_AddressCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Provider_Address> rt = cq.from(Provider_Address.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
