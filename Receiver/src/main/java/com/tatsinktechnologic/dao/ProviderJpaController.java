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
import com.tatsinktechnologic.beans_entity.Provider_Category;
import com.tatsinktechnologic.beans_entity.Provider_Address;
import com.tatsinktechnologic.beans_entity.Service;
import java.util.HashSet;
import java.util.Set;
import com.tatsinktechnologic.beans_entity.Person;
import com.tatsinktechnologic.beans_entity.Provider;
import com.tatsinktechnologic.dao.exceptions.IllegalOrphanException;
import com.tatsinktechnologic.dao.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class ProviderJpaController implements Serializable {

    public ProviderJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Provider provider) {
        if (provider.getListService() == null) {
            provider.setListService(new HashSet<Service>());
        }
        if (provider.getListPerson() == null) {
            provider.setListPerson(new HashSet<Person>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Provider_Category provider_category = provider.getProvider_category();
            if (provider_category != null) {
                provider_category = em.getReference(provider_category.getClass(), provider_category.getProvider_category_id());
                provider.setProvider_category(provider_category);
            }
            Provider_Address provider_address = provider.getProvider_address();
            if (provider_address != null) {
                provider_address = em.getReference(provider_address.getClass(), provider_address.getProvider_address_id());
                provider.setProvider_address(provider_address);
            }
            Set<Service> attachedListService = new HashSet<Service>();
            for (Service listServiceServiceToAttach : provider.getListService()) {
                listServiceServiceToAttach = em.getReference(listServiceServiceToAttach.getClass(), listServiceServiceToAttach.getService_id());
                attachedListService.add(listServiceServiceToAttach);
            }
            provider.setListService(attachedListService);
            Set<Person> attachedListPerson = new HashSet<Person>();
            for (Person listPersonPersonToAttach : provider.getListPerson()) {
                listPersonPersonToAttach = em.getReference(listPersonPersonToAttach.getClass(), listPersonPersonToAttach.getPerson_id());
                attachedListPerson.add(listPersonPersonToAttach);
            }
            provider.setListPerson(attachedListPerson);
            em.persist(provider);
            if (provider_category != null) {
                provider_category.getListProvider().add(provider);
                provider_category = em.merge(provider_category);
            }
            if (provider_address != null) {
                provider_address.getListProvider().add(provider);
                provider_address = em.merge(provider_address);
            }
            for (Service listServiceService : provider.getListService()) {
                Provider oldProviderOfListServiceService = listServiceService.getProvider();
                listServiceService.setProvider(provider);
                listServiceService = em.merge(listServiceService);
                if (oldProviderOfListServiceService != null) {
                    oldProviderOfListServiceService.getListService().remove(listServiceService);
                    oldProviderOfListServiceService = em.merge(oldProviderOfListServiceService);
                }
            }
            for (Person listPersonPerson : provider.getListPerson()) {
                Provider oldProviderOfListPersonPerson = listPersonPerson.getProvider();
                listPersonPerson.setProvider(provider);
                listPersonPerson = em.merge(listPersonPerson);
                if (oldProviderOfListPersonPerson != null) {
                    oldProviderOfListPersonPerson.getListPerson().remove(listPersonPerson);
                    oldProviderOfListPersonPerson = em.merge(oldProviderOfListPersonPerson);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Provider provider) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Provider persistentProvider = em.find(Provider.class, provider.getProvider_id());
            Provider_Category provider_categoryOld = persistentProvider.getProvider_category();
            Provider_Category provider_categoryNew = provider.getProvider_category();
            Provider_Address provider_addressOld = persistentProvider.getProvider_address();
            Provider_Address provider_addressNew = provider.getProvider_address();
            Set<Service> listServiceOld = persistentProvider.getListService();
            Set<Service> listServiceNew = provider.getListService();
            Set<Person> listPersonOld = persistentProvider.getListPerson();
            Set<Person> listPersonNew = provider.getListPerson();
            List<String> illegalOrphanMessages = null;
            for (Service listServiceOldService : listServiceOld) {
                if (!listServiceNew.contains(listServiceOldService)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Service " + listServiceOldService + " since its provider field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (provider_categoryNew != null) {
                provider_categoryNew = em.getReference(provider_categoryNew.getClass(), provider_categoryNew.getProvider_category_id());
                provider.setProvider_category(provider_categoryNew);
            }
            if (provider_addressNew != null) {
                provider_addressNew = em.getReference(provider_addressNew.getClass(), provider_addressNew.getProvider_address_id());
                provider.setProvider_address(provider_addressNew);
            }
            Set<Service> attachedListServiceNew = new HashSet<Service>();
            for (Service listServiceNewServiceToAttach : listServiceNew) {
                listServiceNewServiceToAttach = em.getReference(listServiceNewServiceToAttach.getClass(), listServiceNewServiceToAttach.getService_id());
                attachedListServiceNew.add(listServiceNewServiceToAttach);
            }
            listServiceNew = attachedListServiceNew;
            provider.setListService(listServiceNew);
            Set<Person> attachedListPersonNew = new HashSet<Person>();
            for (Person listPersonNewPersonToAttach : listPersonNew) {
                listPersonNewPersonToAttach = em.getReference(listPersonNewPersonToAttach.getClass(), listPersonNewPersonToAttach.getPerson_id());
                attachedListPersonNew.add(listPersonNewPersonToAttach);
            }
            listPersonNew = attachedListPersonNew;
            provider.setListPerson(listPersonNew);
            provider = em.merge(provider);
            if (provider_categoryOld != null && !provider_categoryOld.equals(provider_categoryNew)) {
                provider_categoryOld.getListProvider().remove(provider);
                provider_categoryOld = em.merge(provider_categoryOld);
            }
            if (provider_categoryNew != null && !provider_categoryNew.equals(provider_categoryOld)) {
                provider_categoryNew.getListProvider().add(provider);
                provider_categoryNew = em.merge(provider_categoryNew);
            }
            if (provider_addressOld != null && !provider_addressOld.equals(provider_addressNew)) {
                provider_addressOld.getListProvider().remove(provider);
                provider_addressOld = em.merge(provider_addressOld);
            }
            if (provider_addressNew != null && !provider_addressNew.equals(provider_addressOld)) {
                provider_addressNew.getListProvider().add(provider);
                provider_addressNew = em.merge(provider_addressNew);
            }
            for (Service listServiceNewService : listServiceNew) {
                if (!listServiceOld.contains(listServiceNewService)) {
                    Provider oldProviderOfListServiceNewService = listServiceNewService.getProvider();
                    listServiceNewService.setProvider(provider);
                    listServiceNewService = em.merge(listServiceNewService);
                    if (oldProviderOfListServiceNewService != null && !oldProviderOfListServiceNewService.equals(provider)) {
                        oldProviderOfListServiceNewService.getListService().remove(listServiceNewService);
                        oldProviderOfListServiceNewService = em.merge(oldProviderOfListServiceNewService);
                    }
                }
            }
            for (Person listPersonOldPerson : listPersonOld) {
                if (!listPersonNew.contains(listPersonOldPerson)) {
                    listPersonOldPerson.setProvider(null);
                    listPersonOldPerson = em.merge(listPersonOldPerson);
                }
            }
            for (Person listPersonNewPerson : listPersonNew) {
                if (!listPersonOld.contains(listPersonNewPerson)) {
                    Provider oldProviderOfListPersonNewPerson = listPersonNewPerson.getProvider();
                    listPersonNewPerson.setProvider(provider);
                    listPersonNewPerson = em.merge(listPersonNewPerson);
                    if (oldProviderOfListPersonNewPerson != null && !oldProviderOfListPersonNewPerson.equals(provider)) {
                        oldProviderOfListPersonNewPerson.getListPerson().remove(listPersonNewPerson);
                        oldProviderOfListPersonNewPerson = em.merge(oldProviderOfListPersonNewPerson);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = provider.getProvider_id();
                if (findProvider(id) == null) {
                    throw new NonexistentEntityException("The provider with id " + id + " no longer exists.");
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
            Provider provider;
            try {
                provider = em.getReference(Provider.class, id);
                provider.getProvider_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The provider with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Set<Service> listServiceOrphanCheck = provider.getListService();
            for (Service listServiceOrphanCheckService : listServiceOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Provider (" + provider + ") cannot be destroyed since the Service " + listServiceOrphanCheckService + " in its listService field has a non-nullable provider field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Provider_Category provider_category = provider.getProvider_category();
            if (provider_category != null) {
                provider_category.getListProvider().remove(provider);
                provider_category = em.merge(provider_category);
            }
            Provider_Address provider_address = provider.getProvider_address();
            if (provider_address != null) {
                provider_address.getListProvider().remove(provider);
                provider_address = em.merge(provider_address);
            }
            Set<Person> listPerson = provider.getListPerson();
            for (Person listPersonPerson : listPerson) {
                listPersonPerson.setProvider(null);
                listPersonPerson = em.merge(listPersonPerson);
            }
            em.remove(provider);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Provider> findProviderEntities() {
        return findProviderEntities(true, -1, -1);
    }

    public List<Provider> findProviderEntities(int maxResults, int firstResult) {
        return findProviderEntities(false, maxResults, firstResult);
    }

    private List<Provider> findProviderEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Provider.class));
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

    public Provider findProvider(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Provider.class, id);
        } finally {
            em.close();
        }
    }

    public int getProviderCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Provider> rt = cq.from(Provider.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
