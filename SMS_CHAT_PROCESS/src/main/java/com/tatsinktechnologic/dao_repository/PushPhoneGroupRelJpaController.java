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
import com.tatsinktechnologic.entity.sms_chat.PushPhone;
import com.tatsinktechnologic.entity.sms_chat.PushPhoneGroupRel;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class PushPhoneGroupRelJpaController implements Serializable {

    public PushPhoneGroupRelJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PushPhoneGroupRel pushPhoneGroupRel) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PushPhone pushphone = pushPhoneGroupRel.getPushphone();
            if (pushphone != null) {
                pushphone = em.getReference(pushphone.getClass(), pushphone.getPushphone_id());
                pushPhoneGroupRel.setPushphone(pushphone);
            }
            em.persist(pushPhoneGroupRel);
            if (pushphone != null) {
                pushphone.getPushPhoneGroup_UserRel().add(pushPhoneGroupRel);
                pushphone = em.merge(pushphone);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PushPhoneGroupRel pushPhoneGroupRel) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PushPhoneGroupRel persistentPushPhoneGroupRel = em.find(PushPhoneGroupRel.class, pushPhoneGroupRel.getPushphone_group_user_id());
            PushPhone pushphoneOld = persistentPushPhoneGroupRel.getPushphone();
            PushPhone pushphoneNew = pushPhoneGroupRel.getPushphone();
            if (pushphoneNew != null) {
                pushphoneNew = em.getReference(pushphoneNew.getClass(), pushphoneNew.getPushphone_id());
                pushPhoneGroupRel.setPushphone(pushphoneNew);
            }
            pushPhoneGroupRel = em.merge(pushPhoneGroupRel);
            if (pushphoneOld != null && !pushphoneOld.equals(pushphoneNew)) {
                pushphoneOld.getPushPhoneGroup_UserRel().remove(pushPhoneGroupRel);
                pushphoneOld = em.merge(pushphoneOld);
            }
            if (pushphoneNew != null && !pushphoneNew.equals(pushphoneOld)) {
                pushphoneNew.getPushPhoneGroup_UserRel().add(pushPhoneGroupRel);
                pushphoneNew = em.merge(pushphoneNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = pushPhoneGroupRel.getPushphone_group_user_id();
                if (findPushPhoneGroupRel(id) == null) {
                    throw new NonexistentEntityException("The pushPhoneGroupRel with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PushPhoneGroupRel pushPhoneGroupRel;
            try {
                pushPhoneGroupRel = em.getReference(PushPhoneGroupRel.class, id);
                pushPhoneGroupRel.getPushphone_group_user_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pushPhoneGroupRel with id " + id + " no longer exists.", enfe);
            }
            PushPhone pushphone = pushPhoneGroupRel.getPushphone();
            if (pushphone != null) {
                pushphone.getPushPhoneGroup_UserRel().remove(pushPhoneGroupRel);
                pushphone = em.merge(pushphone);
            }
            em.remove(pushPhoneGroupRel);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PushPhoneGroupRel> findPushPhoneGroupRelEntities() {
        return findPushPhoneGroupRelEntities(true, -1, -1);
    }

    public List<PushPhoneGroupRel> findPushPhoneGroupRelEntities(int maxResults, int firstResult) {
        return findPushPhoneGroupRelEntities(false, maxResults, firstResult);
    }

    private List<PushPhoneGroupRel> findPushPhoneGroupRelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PushPhoneGroupRel.class));
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

    public PushPhoneGroupRel findPushPhoneGroupRel(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PushPhoneGroupRel.class, id);
        } finally {
            em.close();
        }
    }

    public int getPushPhoneGroupRelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PushPhoneGroupRel> rt = cq.from(PushPhoneGroupRel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
