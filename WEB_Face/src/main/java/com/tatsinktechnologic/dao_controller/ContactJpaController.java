/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_controller;

import com.tatsinktechnologic.entities.account.Contact;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entities.account.UserContactRel;
import java.util.HashSet;
import java.util.Set;
import com.tatsinktechnologic.entities.account.Phone;
import com.tatsinktechnologic.entities.account.Email;
import com.tatsinktechnologic.dao_controller.exceptions.IllegalOrphanException;
import com.tatsinktechnologic.dao_controller.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.dao_controller.exceptions.RollbackFailureException;
import java.util.ArrayList;
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
public class ContactJpaController implements Serializable {

    @Inject
    EntityManagerFactory emf;
    
    @Resource
    private UserTransaction utx;


    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Contact contact) throws RollbackFailureException, Exception {
        if (contact.getUserContactRels() == null) {
            contact.setUserContactRels(new HashSet<UserContactRel>());
        }
        if (contact.getListphone_number() == null) {
            contact.setListphone_number(new HashSet<Phone>());
        }
        if (contact.getListEmail() == null) {
            contact.setListEmail(new HashSet<Email>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Set<UserContactRel> attachedUserContactRels = new HashSet<UserContactRel>();
            for (UserContactRel userContactRelsUserContactRelToAttach : contact.getUserContactRels()) {
                userContactRelsUserContactRelToAttach = em.getReference(userContactRelsUserContactRelToAttach.getClass(), userContactRelsUserContactRelToAttach.getUser_contact_id());
                attachedUserContactRels.add(userContactRelsUserContactRelToAttach);
            }
            contact.setUserContactRels(attachedUserContactRels);
            Set<Phone> attachedListphone_number = new HashSet<Phone>();
            for (Phone listphone_numberPhoneToAttach : contact.getListphone_number()) {
                listphone_numberPhoneToAttach = em.getReference(listphone_numberPhoneToAttach.getClass(), listphone_numberPhoneToAttach.getPhone_id());
                attachedListphone_number.add(listphone_numberPhoneToAttach);
            }
            contact.setListphone_number(attachedListphone_number);
            Set<Email> attachedListEmail = new HashSet<Email>();
            for (Email listEmailEmailToAttach : contact.getListEmail()) {
                listEmailEmailToAttach = em.getReference(listEmailEmailToAttach.getClass(), listEmailEmailToAttach.getEmail_id());
                attachedListEmail.add(listEmailEmailToAttach);
            }
            contact.setListEmail(attachedListEmail);
            em.persist(contact);
            for (UserContactRel userContactRelsUserContactRel : contact.getUserContactRels()) {
                Contact oldContactOfUserContactRelsUserContactRel = userContactRelsUserContactRel.getContact();
                userContactRelsUserContactRel.setContact(contact);
                userContactRelsUserContactRel = em.merge(userContactRelsUserContactRel);
                if (oldContactOfUserContactRelsUserContactRel != null) {
                    oldContactOfUserContactRelsUserContactRel.getUserContactRels().remove(userContactRelsUserContactRel);
                    oldContactOfUserContactRelsUserContactRel = em.merge(oldContactOfUserContactRelsUserContactRel);
                }
            }
            for (Phone listphone_numberPhone : contact.getListphone_number()) {
                Contact oldContactOfListphone_numberPhone = listphone_numberPhone.getContact();
                listphone_numberPhone.setContact(contact);
                listphone_numberPhone = em.merge(listphone_numberPhone);
                if (oldContactOfListphone_numberPhone != null) {
                    oldContactOfListphone_numberPhone.getListphone_number().remove(listphone_numberPhone);
                    oldContactOfListphone_numberPhone = em.merge(oldContactOfListphone_numberPhone);
                }
            }
            for (Email listEmailEmail : contact.getListEmail()) {
                Contact oldContactOfListEmailEmail = listEmailEmail.getContact();
                listEmailEmail.setContact(contact);
                listEmailEmail = em.merge(listEmailEmail);
                if (oldContactOfListEmailEmail != null) {
                    oldContactOfListEmailEmail.getListEmail().remove(listEmailEmail);
                    oldContactOfListEmailEmail = em.merge(oldContactOfListEmailEmail);
                }
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

    public void edit(Contact contact) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Contact persistentContact = em.find(Contact.class, contact.getContact_id());
            Set<UserContactRel> userContactRelsOld = persistentContact.getUserContactRels();
            Set<UserContactRel> userContactRelsNew = contact.getUserContactRels();
            Set<Phone> listphone_numberOld = persistentContact.getListphone_number();
            Set<Phone> listphone_numberNew = contact.getListphone_number();
            Set<Email> listEmailOld = persistentContact.getListEmail();
            Set<Email> listEmailNew = contact.getListEmail();
            List<String> illegalOrphanMessages = null;
            for (Phone listphone_numberOldPhone : listphone_numberOld) {
                if (!listphone_numberNew.contains(listphone_numberOldPhone)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Phone " + listphone_numberOldPhone + " since its contact field is not nullable.");
                }
            }
            for (Email listEmailOldEmail : listEmailOld) {
                if (!listEmailNew.contains(listEmailOldEmail)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Email " + listEmailOldEmail + " since its contact field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Set<UserContactRel> attachedUserContactRelsNew = new HashSet<UserContactRel>();
            for (UserContactRel userContactRelsNewUserContactRelToAttach : userContactRelsNew) {
                userContactRelsNewUserContactRelToAttach = em.getReference(userContactRelsNewUserContactRelToAttach.getClass(), userContactRelsNewUserContactRelToAttach.getUser_contact_id());
                attachedUserContactRelsNew.add(userContactRelsNewUserContactRelToAttach);
            }
            userContactRelsNew = attachedUserContactRelsNew;
            contact.setUserContactRels(userContactRelsNew);
            Set<Phone> attachedListphone_numberNew = new HashSet<Phone>();
            for (Phone listphone_numberNewPhoneToAttach : listphone_numberNew) {
                listphone_numberNewPhoneToAttach = em.getReference(listphone_numberNewPhoneToAttach.getClass(), listphone_numberNewPhoneToAttach.getPhone_id());
                attachedListphone_numberNew.add(listphone_numberNewPhoneToAttach);
            }
            listphone_numberNew = attachedListphone_numberNew;
            contact.setListphone_number(listphone_numberNew);
            Set<Email> attachedListEmailNew = new HashSet<Email>();
            for (Email listEmailNewEmailToAttach : listEmailNew) {
                listEmailNewEmailToAttach = em.getReference(listEmailNewEmailToAttach.getClass(), listEmailNewEmailToAttach.getEmail_id());
                attachedListEmailNew.add(listEmailNewEmailToAttach);
            }
            listEmailNew = attachedListEmailNew;
            contact.setListEmail(listEmailNew);
            contact = em.merge(contact);
            for (UserContactRel userContactRelsOldUserContactRel : userContactRelsOld) {
                if (!userContactRelsNew.contains(userContactRelsOldUserContactRel)) {
                    userContactRelsOldUserContactRel.setContact(null);
                    userContactRelsOldUserContactRel = em.merge(userContactRelsOldUserContactRel);
                }
            }
            for (UserContactRel userContactRelsNewUserContactRel : userContactRelsNew) {
                if (!userContactRelsOld.contains(userContactRelsNewUserContactRel)) {
                    Contact oldContactOfUserContactRelsNewUserContactRel = userContactRelsNewUserContactRel.getContact();
                    userContactRelsNewUserContactRel.setContact(contact);
                    userContactRelsNewUserContactRel = em.merge(userContactRelsNewUserContactRel);
                    if (oldContactOfUserContactRelsNewUserContactRel != null && !oldContactOfUserContactRelsNewUserContactRel.equals(contact)) {
                        oldContactOfUserContactRelsNewUserContactRel.getUserContactRels().remove(userContactRelsNewUserContactRel);
                        oldContactOfUserContactRelsNewUserContactRel = em.merge(oldContactOfUserContactRelsNewUserContactRel);
                    }
                }
            }
            for (Phone listphone_numberNewPhone : listphone_numberNew) {
                if (!listphone_numberOld.contains(listphone_numberNewPhone)) {
                    Contact oldContactOfListphone_numberNewPhone = listphone_numberNewPhone.getContact();
                    listphone_numberNewPhone.setContact(contact);
                    listphone_numberNewPhone = em.merge(listphone_numberNewPhone);
                    if (oldContactOfListphone_numberNewPhone != null && !oldContactOfListphone_numberNewPhone.equals(contact)) {
                        oldContactOfListphone_numberNewPhone.getListphone_number().remove(listphone_numberNewPhone);
                        oldContactOfListphone_numberNewPhone = em.merge(oldContactOfListphone_numberNewPhone);
                    }
                }
            }
            for (Email listEmailNewEmail : listEmailNew) {
                if (!listEmailOld.contains(listEmailNewEmail)) {
                    Contact oldContactOfListEmailNewEmail = listEmailNewEmail.getContact();
                    listEmailNewEmail.setContact(contact);
                    listEmailNewEmail = em.merge(listEmailNewEmail);
                    if (oldContactOfListEmailNewEmail != null && !oldContactOfListEmailNewEmail.equals(contact)) {
                        oldContactOfListEmailNewEmail.getListEmail().remove(listEmailNewEmail);
                        oldContactOfListEmailNewEmail = em.merge(oldContactOfListEmailNewEmail);
                    }
                }
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
                Integer id = contact.getContact_id();
                if (findContact(id) == null) {
                    throw new NonexistentEntityException("The contact with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Contact contact;
            try {
                contact = em.getReference(Contact.class, id);
                contact.getContact_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The contact with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Set<Phone> listphone_numberOrphanCheck = contact.getListphone_number();
            for (Phone listphone_numberOrphanCheckPhone : listphone_numberOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Contact (" + contact + ") cannot be destroyed since the Phone " + listphone_numberOrphanCheckPhone + " in its listphone_number field has a non-nullable contact field.");
            }
            Set<Email> listEmailOrphanCheck = contact.getListEmail();
            for (Email listEmailOrphanCheckEmail : listEmailOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Contact (" + contact + ") cannot be destroyed since the Email " + listEmailOrphanCheckEmail + " in its listEmail field has a non-nullable contact field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Set<UserContactRel> userContactRels = contact.getUserContactRels();
            for (UserContactRel userContactRelsUserContactRel : userContactRels) {
                userContactRelsUserContactRel.setContact(null);
                userContactRelsUserContactRel = em.merge(userContactRelsUserContactRel);
            }
            em.remove(contact);
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

    public List<Contact> findContactEntities() {
        return findContactEntities(true, -1, -1);
    }

    public List<Contact> findContactEntities(int maxResults, int firstResult) {
        return findContactEntities(false, maxResults, firstResult);
    }

    private List<Contact> findContactEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Contact.class));
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

    public Contact findContact(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Contact.class, id);
        } finally {
            em.close();
        }
    }

    public int getContactCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Contact> rt = cq.from(Contact.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
