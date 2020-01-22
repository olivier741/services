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
import com.tatsinktechnologic.entity.account.Contact;
import com.tatsinktechnologic.entity.account.Phone;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class PhoneJpaController implements Serializable {

    public PhoneJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Phone phone) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Contact contact = phone.getContact();
            if (contact != null) {
                contact = em.getReference(contact.getClass(), contact.getContact_id());
                phone.setContact(contact);
            }
            em.persist(phone);
            if (contact != null) {
                contact.getListphone_number().add(phone);
                contact = em.merge(contact);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Phone phone) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Phone persistentPhone = em.find(Phone.class, phone.getPhone_id());
            Contact contactOld = persistentPhone.getContact();
            Contact contactNew = phone.getContact();
            if (contactNew != null) {
                contactNew = em.getReference(contactNew.getClass(), contactNew.getContact_id());
                phone.setContact(contactNew);
            }
            phone = em.merge(phone);
            if (contactOld != null && !contactOld.equals(contactNew)) {
                contactOld.getListphone_number().remove(phone);
                contactOld = em.merge(contactOld);
            }
            if (contactNew != null && !contactNew.equals(contactOld)) {
                contactNew.getListphone_number().add(phone);
                contactNew = em.merge(contactNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = phone.getPhone_id();
                if (findPhone(id) == null) {
                    throw new NonexistentEntityException("The phone with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Phone phone;
            try {
                phone = em.getReference(Phone.class, id);
                phone.getPhone_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The phone with id " + id + " no longer exists.", enfe);
            }
            Contact contact = phone.getContact();
            if (contact != null) {
                contact.getListphone_number().remove(phone);
                contact = em.merge(contact);
            }
            em.remove(phone);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Phone> findPhoneEntities() {
        return findPhoneEntities(true, -1, -1);
    }

    public List<Phone> findPhoneEntities(int maxResults, int firstResult) {
        return findPhoneEntities(false, maxResults, firstResult);
    }

    private List<Phone> findPhoneEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Phone.class));
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

    public Phone findPhone(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Phone.class, id);
        } finally {
            em.close();
        }
    }

    public int getPhoneCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Phone> rt = cq.from(Phone.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
