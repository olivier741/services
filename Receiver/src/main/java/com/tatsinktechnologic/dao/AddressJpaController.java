/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao;

import com.tatsinktechnologic.beans_entity.Address;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.beans_entity.Person;
import com.tatsinktechnologic.dao.exceptions.NonexistentEntityException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class AddressJpaController implements Serializable {

    public AddressJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Address address) {
        if (address.getListPerson() == null) {
            address.setListPerson(new HashSet<Person>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Set<Person> attachedListPerson = new HashSet<Person>();
            for (Person listPersonPersonToAttach : address.getListPerson()) {
                listPersonPersonToAttach = em.getReference(listPersonPersonToAttach.getClass(), listPersonPersonToAttach.getPerson_id());
                attachedListPerson.add(listPersonPersonToAttach);
            }
            address.setListPerson(attachedListPerson);
            em.persist(address);
            for (Person listPersonPerson : address.getListPerson()) {
                Address oldAddressOfListPersonPerson = listPersonPerson.getAddress();
                listPersonPerson.setAddress(address);
                listPersonPerson = em.merge(listPersonPerson);
                if (oldAddressOfListPersonPerson != null) {
                    oldAddressOfListPersonPerson.getListPerson().remove(listPersonPerson);
                    oldAddressOfListPersonPerson = em.merge(oldAddressOfListPersonPerson);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Address address) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Address persistentAddress = em.find(Address.class, address.getAddress_id());
            Set<Person> listPersonOld = persistentAddress.getListPerson();
            Set<Person> listPersonNew = address.getListPerson();
            Set<Person> attachedListPersonNew = new HashSet<Person>();
            for (Person listPersonNewPersonToAttach : listPersonNew) {
                listPersonNewPersonToAttach = em.getReference(listPersonNewPersonToAttach.getClass(), listPersonNewPersonToAttach.getPerson_id());
                attachedListPersonNew.add(listPersonNewPersonToAttach);
            }
            listPersonNew = attachedListPersonNew;
            address.setListPerson(listPersonNew);
            address = em.merge(address);
            for (Person listPersonOldPerson : listPersonOld) {
                if (!listPersonNew.contains(listPersonOldPerson)) {
                    listPersonOldPerson.setAddress(null);
                    listPersonOldPerson = em.merge(listPersonOldPerson);
                }
            }
            for (Person listPersonNewPerson : listPersonNew) {
                if (!listPersonOld.contains(listPersonNewPerson)) {
                    Address oldAddressOfListPersonNewPerson = listPersonNewPerson.getAddress();
                    listPersonNewPerson.setAddress(address);
                    listPersonNewPerson = em.merge(listPersonNewPerson);
                    if (oldAddressOfListPersonNewPerson != null && !oldAddressOfListPersonNewPerson.equals(address)) {
                        oldAddressOfListPersonNewPerson.getListPerson().remove(listPersonNewPerson);
                        oldAddressOfListPersonNewPerson = em.merge(oldAddressOfListPersonNewPerson);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = address.getAddress_id();
                if (findAddress(id) == null) {
                    throw new NonexistentEntityException("The address with id " + id + " no longer exists.");
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
            Address address;
            try {
                address = em.getReference(Address.class, id);
                address.getAddress_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The address with id " + id + " no longer exists.", enfe);
            }
            Set<Person> listPerson = address.getListPerson();
            for (Person listPersonPerson : listPerson) {
                listPersonPerson.setAddress(null);
                listPersonPerson = em.merge(listPersonPerson);
            }
            em.remove(address);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Address> findAddressEntities() {
        return findAddressEntities(true, -1, -1);
    }

    public List<Address> findAddressEntities(int maxResults, int firstResult) {
        return findAddressEntities(false, maxResults, firstResult);
    }

    private List<Address> findAddressEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Address.class));
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

    public Address findAddress(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Address.class, id);
        } finally {
            em.close();
        }
    }

    public int getAddressCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Address> rt = cq.from(Address.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
