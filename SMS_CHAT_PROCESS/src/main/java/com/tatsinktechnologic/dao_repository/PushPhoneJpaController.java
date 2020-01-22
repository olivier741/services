/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_repository;

import com.tatsinktechnologic.dao_repository.exceptions.IllegalOrphanException;
import com.tatsinktechnologic.dao_repository.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.entity.sms_chat.PushPhone;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entity.sms_chat.PushPhoneGroupRel;
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
public class PushPhoneJpaController implements Serializable {

    public PushPhoneJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PushPhone pushPhone) {
        if (pushPhone.getPushPhoneGroup_UserRel() == null) {
            pushPhone.setPushPhoneGroup_UserRel(new HashSet<PushPhoneGroupRel>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Set<PushPhoneGroupRel> attachedPushPhoneGroup_UserRel = new HashSet<PushPhoneGroupRel>();
            for (PushPhoneGroupRel pushPhoneGroup_UserRelPushPhoneGroupRelToAttach : pushPhone.getPushPhoneGroup_UserRel()) {
                pushPhoneGroup_UserRelPushPhoneGroupRelToAttach = em.getReference(pushPhoneGroup_UserRelPushPhoneGroupRelToAttach.getClass(), pushPhoneGroup_UserRelPushPhoneGroupRelToAttach.getPushphone_group_user_id());
                attachedPushPhoneGroup_UserRel.add(pushPhoneGroup_UserRelPushPhoneGroupRelToAttach);
            }
            pushPhone.setPushPhoneGroup_UserRel(attachedPushPhoneGroup_UserRel);
            em.persist(pushPhone);
            for (PushPhoneGroupRel pushPhoneGroup_UserRelPushPhoneGroupRel : pushPhone.getPushPhoneGroup_UserRel()) {
                PushPhone oldPushphoneOfPushPhoneGroup_UserRelPushPhoneGroupRel = pushPhoneGroup_UserRelPushPhoneGroupRel.getPushphone();
                pushPhoneGroup_UserRelPushPhoneGroupRel.setPushphone(pushPhone);
                pushPhoneGroup_UserRelPushPhoneGroupRel = em.merge(pushPhoneGroup_UserRelPushPhoneGroupRel);
                if (oldPushphoneOfPushPhoneGroup_UserRelPushPhoneGroupRel != null) {
                    oldPushphoneOfPushPhoneGroup_UserRelPushPhoneGroupRel.getPushPhoneGroup_UserRel().remove(pushPhoneGroup_UserRelPushPhoneGroupRel);
                    oldPushphoneOfPushPhoneGroup_UserRelPushPhoneGroupRel = em.merge(oldPushphoneOfPushPhoneGroup_UserRelPushPhoneGroupRel);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PushPhone pushPhone) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PushPhone persistentPushPhone = em.find(PushPhone.class, pushPhone.getPushphone_id());
            Set<PushPhoneGroupRel> pushPhoneGroup_UserRelOld = persistentPushPhone.getPushPhoneGroup_UserRel();
            Set<PushPhoneGroupRel> pushPhoneGroup_UserRelNew = pushPhone.getPushPhoneGroup_UserRel();
            List<String> illegalOrphanMessages = null;
            for (PushPhoneGroupRel pushPhoneGroup_UserRelOldPushPhoneGroupRel : pushPhoneGroup_UserRelOld) {
                if (!pushPhoneGroup_UserRelNew.contains(pushPhoneGroup_UserRelOldPushPhoneGroupRel)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain PushPhoneGroupRel " + pushPhoneGroup_UserRelOldPushPhoneGroupRel + " since its pushphone field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Set<PushPhoneGroupRel> attachedPushPhoneGroup_UserRelNew = new HashSet<PushPhoneGroupRel>();
            for (PushPhoneGroupRel pushPhoneGroup_UserRelNewPushPhoneGroupRelToAttach : pushPhoneGroup_UserRelNew) {
                pushPhoneGroup_UserRelNewPushPhoneGroupRelToAttach = em.getReference(pushPhoneGroup_UserRelNewPushPhoneGroupRelToAttach.getClass(), pushPhoneGroup_UserRelNewPushPhoneGroupRelToAttach.getPushphone_group_user_id());
                attachedPushPhoneGroup_UserRelNew.add(pushPhoneGroup_UserRelNewPushPhoneGroupRelToAttach);
            }
            pushPhoneGroup_UserRelNew = attachedPushPhoneGroup_UserRelNew;
            pushPhone.setPushPhoneGroup_UserRel(pushPhoneGroup_UserRelNew);
            pushPhone = em.merge(pushPhone);
            for (PushPhoneGroupRel pushPhoneGroup_UserRelNewPushPhoneGroupRel : pushPhoneGroup_UserRelNew) {
                if (!pushPhoneGroup_UserRelOld.contains(pushPhoneGroup_UserRelNewPushPhoneGroupRel)) {
                    PushPhone oldPushphoneOfPushPhoneGroup_UserRelNewPushPhoneGroupRel = pushPhoneGroup_UserRelNewPushPhoneGroupRel.getPushphone();
                    pushPhoneGroup_UserRelNewPushPhoneGroupRel.setPushphone(pushPhone);
                    pushPhoneGroup_UserRelNewPushPhoneGroupRel = em.merge(pushPhoneGroup_UserRelNewPushPhoneGroupRel);
                    if (oldPushphoneOfPushPhoneGroup_UserRelNewPushPhoneGroupRel != null && !oldPushphoneOfPushPhoneGroup_UserRelNewPushPhoneGroupRel.equals(pushPhone)) {
                        oldPushphoneOfPushPhoneGroup_UserRelNewPushPhoneGroupRel.getPushPhoneGroup_UserRel().remove(pushPhoneGroup_UserRelNewPushPhoneGroupRel);
                        oldPushphoneOfPushPhoneGroup_UserRelNewPushPhoneGroupRel = em.merge(oldPushphoneOfPushPhoneGroup_UserRelNewPushPhoneGroupRel);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = pushPhone.getPushphone_id();
                if (findPushPhone(id) == null) {
                    throw new NonexistentEntityException("The pushPhone with id " + id + " no longer exists.");
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
            PushPhone pushPhone;
            try {
                pushPhone = em.getReference(PushPhone.class, id);
                pushPhone.getPushphone_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pushPhone with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Set<PushPhoneGroupRel> pushPhoneGroup_UserRelOrphanCheck = pushPhone.getPushPhoneGroup_UserRel();
            for (PushPhoneGroupRel pushPhoneGroup_UserRelOrphanCheckPushPhoneGroupRel : pushPhoneGroup_UserRelOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This PushPhone (" + pushPhone + ") cannot be destroyed since the PushPhoneGroupRel " + pushPhoneGroup_UserRelOrphanCheckPushPhoneGroupRel + " in its pushPhoneGroup_UserRel field has a non-nullable pushphone field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(pushPhone);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PushPhone> findPushPhoneEntities() {
        return findPushPhoneEntities(true, -1, -1);
    }

    public List<PushPhone> findPushPhoneEntities(int maxResults, int firstResult) {
        return findPushPhoneEntities(false, maxResults, firstResult);
    }

    private List<PushPhone> findPushPhoneEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PushPhone.class));
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

    public PushPhone findPushPhone(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PushPhone.class, id);
        } finally {
            em.close();
        }
    }

    public int getPushPhoneCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PushPhone> rt = cq.from(PushPhone.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
