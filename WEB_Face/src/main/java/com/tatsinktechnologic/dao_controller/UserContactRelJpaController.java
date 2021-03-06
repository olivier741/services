/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entities.account.User;
import com.tatsinktechnologic.entities.account.Contact;
import com.tatsinktechnologic.entities.account.UserContactRel;
import com.tatsinktechnologic.dao_controller.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.dao_controller.exceptions.RollbackFailureException;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author olivier
 */
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class UserContactRelJpaController implements Serializable {

    @Inject
    EntityManagerFactory emf;
    
    @Resource
    private UserTransaction utx;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UserContactRel userContactRel) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User user = userContactRel.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getUser_id());
                userContactRel.setUser(user);
            }
            Contact contact = userContactRel.getContact();
            if (contact != null) {
                contact = em.getReference(contact.getClass(), contact.getContact_id());
                userContactRel.setContact(contact);
            }
            em.persist(userContactRel);
            if (user != null) {
                user.getUserContactRels().add(userContactRel);
                user = em.merge(user);
            }
            if (contact != null) {
                contact.getUserContactRels().add(userContactRel);
                contact = em.merge(contact);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserContactRel userContactRel) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            UserContactRel persistentUserContactRel = em.find(UserContactRel.class, userContactRel.getUser_contact_id());
            User userOld = persistentUserContactRel.getUser();
            User userNew = userContactRel.getUser();
            Contact contactOld = persistentUserContactRel.getContact();
            Contact contactNew = userContactRel.getContact();
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getUser_id());
                userContactRel.setUser(userNew);
            }
            if (contactNew != null) {
                contactNew = em.getReference(contactNew.getClass(), contactNew.getContact_id());
                userContactRel.setContact(contactNew);
            }
            userContactRel = em.merge(userContactRel);
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.getUserContactRels().remove(userContactRel);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.getUserContactRels().add(userContactRel);
                userNew = em.merge(userNew);
            }
            if (contactOld != null && !contactOld.equals(contactNew)) {
                contactOld.getUserContactRels().remove(userContactRel);
                contactOld = em.merge(contactOld);
            }
            if (contactNew != null && !contactNew.equals(contactOld)) {
                contactNew.getUserContactRels().add(userContactRel);
                contactNew = em.merge(contactNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = userContactRel.getUser_contact_id();
                if (findUserContactRel(id) == null) {
                    throw new NonexistentEntityException("The userContactRel with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            UserContactRel userContactRel;
            try {
                userContactRel = em.getReference(UserContactRel.class, id);
                userContactRel.getUser_contact_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userContactRel with id " + id + " no longer exists.", enfe);
            }
            User user = userContactRel.getUser();
            if (user != null) {
                user.getUserContactRels().remove(userContactRel);
                user = em.merge(user);
            }
            Contact contact = userContactRel.getContact();
            if (contact != null) {
                contact.getUserContactRels().remove(userContactRel);
                contact = em.merge(contact);
            }
            em.remove(userContactRel);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UserContactRel> findUserContactRelEntities() {
        return findUserContactRelEntities(true, -1, -1);
    }

    public List<UserContactRel> findUserContactRelEntities(int maxResults, int firstResult) {
        return findUserContactRelEntities(false, maxResults, firstResult);
    }

    private List<UserContactRel> findUserContactRelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UserContactRel.class));
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

    public UserContactRel findUserContactRel(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserContactRel.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserContactRelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UserContactRel> rt = cq.from(UserContactRel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
