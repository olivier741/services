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
import com.tatsinktechnologic.beans_entity.Address;
import com.tatsinktechnologic.beans_entity.Person;
import com.tatsinktechnologic.beans_entity.Provider;
import com.tatsinktechnologic.dao.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class PersonJpaController implements Serializable {

    public PersonJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Person person) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Address address = person.getAddress();
            if (address != null) {
                address = em.getReference(address.getClass(), address.getAddress_id());
                person.setAddress(address);
            }
            Provider provider = person.getProvider();
            if (provider != null) {
                provider = em.getReference(provider.getClass(), provider.getProvider_id());
                person.setProvider(provider);
            }
            em.persist(person);
            if (address != null) {
                address.getListPerson().add(person);
                address = em.merge(address);
            }
            if (provider != null) {
                provider.getListPerson().add(person);
                provider = em.merge(provider);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Person person) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Person persistentPerson = em.find(Person.class, person.getPerson_id());
            Address addressOld = persistentPerson.getAddress();
            Address addressNew = person.getAddress();
            Provider providerOld = persistentPerson.getProvider();
            Provider providerNew = person.getProvider();
            if (addressNew != null) {
                addressNew = em.getReference(addressNew.getClass(), addressNew.getAddress_id());
                person.setAddress(addressNew);
            }
            if (providerNew != null) {
                providerNew = em.getReference(providerNew.getClass(), providerNew.getProvider_id());
                person.setProvider(providerNew);
            }
            person = em.merge(person);
            if (addressOld != null && !addressOld.equals(addressNew)) {
                addressOld.getListPerson().remove(person);
                addressOld = em.merge(addressOld);
            }
            if (addressNew != null && !addressNew.equals(addressOld)) {
                addressNew.getListPerson().add(person);
                addressNew = em.merge(addressNew);
            }
            if (providerOld != null && !providerOld.equals(providerNew)) {
                providerOld.getListPerson().remove(person);
                providerOld = em.merge(providerOld);
            }
            if (providerNew != null && !providerNew.equals(providerOld)) {
                providerNew.getListPerson().add(person);
                providerNew = em.merge(providerNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = person.getPerson_id();
                if (findPerson(id) == null) {
                    throw new NonexistentEntityException("The person with id " + id + " no longer exists.");
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
            Person person;
            try {
                person = em.getReference(Person.class, id);
                person.getPerson_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The person with id " + id + " no longer exists.", enfe);
            }
            Address address = person.getAddress();
            if (address != null) {
                address.getListPerson().remove(person);
                address = em.merge(address);
            }
            Provider provider = person.getProvider();
            if (provider != null) {
                provider.getListPerson().remove(person);
                provider = em.merge(provider);
            }
            em.remove(person);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Person> findPersonEntities() {
        return findPersonEntities(true, -1, -1);
    }

    public List<Person> findPersonEntities(int maxResults, int firstResult) {
        return findPersonEntities(false, maxResults, firstResult);
    }

    private List<Person> findPersonEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Person.class));
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

    public Person findPerson(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Person.class, id);
        } finally {
            em.close();
        }
    }

    public int getPersonCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Person> rt = cq.from(Person.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
