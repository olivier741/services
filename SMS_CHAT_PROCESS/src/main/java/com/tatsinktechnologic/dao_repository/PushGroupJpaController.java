/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_repository;

import com.tatsinktechnologic.dao_repository.exceptions.IllegalOrphanException;
import com.tatsinktechnologic.dao_repository.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.entity.sms_chat.PushGroup;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entity.sms_chat.PushGroup_UserRel;
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
public class PushGroupJpaController implements Serializable {

    public PushGroupJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PushGroup pushGroup) {
        if (pushGroup.getPushGroup_UserRelRels() == null) {
            pushGroup.setPushGroup_UserRelRels(new HashSet<PushGroup_UserRel>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Set<PushGroup_UserRel> attachedPushGroup_UserRelRels = new HashSet<PushGroup_UserRel>();
            for (PushGroup_UserRel pushGroup_UserRelRelsPushGroup_UserRelToAttach : pushGroup.getPushGroup_UserRelRels()) {
                pushGroup_UserRelRelsPushGroup_UserRelToAttach = em.getReference(pushGroup_UserRelRelsPushGroup_UserRelToAttach.getClass(), pushGroup_UserRelRelsPushGroup_UserRelToAttach.getPushgroup_user_id());
                attachedPushGroup_UserRelRels.add(pushGroup_UserRelRelsPushGroup_UserRelToAttach);
            }
            pushGroup.setPushGroup_UserRelRels(attachedPushGroup_UserRelRels);
            em.persist(pushGroup);
            for (PushGroup_UserRel pushGroup_UserRelRelsPushGroup_UserRel : pushGroup.getPushGroup_UserRelRels()) {
                PushGroup oldPushgroupOfPushGroup_UserRelRelsPushGroup_UserRel = pushGroup_UserRelRelsPushGroup_UserRel.getPushgroup();
                pushGroup_UserRelRelsPushGroup_UserRel.setPushgroup(pushGroup);
                pushGroup_UserRelRelsPushGroup_UserRel = em.merge(pushGroup_UserRelRelsPushGroup_UserRel);
                if (oldPushgroupOfPushGroup_UserRelRelsPushGroup_UserRel != null) {
                    oldPushgroupOfPushGroup_UserRelRelsPushGroup_UserRel.getPushGroup_UserRelRels().remove(pushGroup_UserRelRelsPushGroup_UserRel);
                    oldPushgroupOfPushGroup_UserRelRelsPushGroup_UserRel = em.merge(oldPushgroupOfPushGroup_UserRelRelsPushGroup_UserRel);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PushGroup pushGroup) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PushGroup persistentPushGroup = em.find(PushGroup.class, pushGroup.getPushgroup_id());
            Set<PushGroup_UserRel> pushGroup_UserRelRelsOld = persistentPushGroup.getPushGroup_UserRelRels();
            Set<PushGroup_UserRel> pushGroup_UserRelRelsNew = pushGroup.getPushGroup_UserRelRels();
            List<String> illegalOrphanMessages = null;
            for (PushGroup_UserRel pushGroup_UserRelRelsOldPushGroup_UserRel : pushGroup_UserRelRelsOld) {
                if (!pushGroup_UserRelRelsNew.contains(pushGroup_UserRelRelsOldPushGroup_UserRel)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain PushGroup_UserRel " + pushGroup_UserRelRelsOldPushGroup_UserRel + " since its pushgroup field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Set<PushGroup_UserRel> attachedPushGroup_UserRelRelsNew = new HashSet<PushGroup_UserRel>();
            for (PushGroup_UserRel pushGroup_UserRelRelsNewPushGroup_UserRelToAttach : pushGroup_UserRelRelsNew) {
                pushGroup_UserRelRelsNewPushGroup_UserRelToAttach = em.getReference(pushGroup_UserRelRelsNewPushGroup_UserRelToAttach.getClass(), pushGroup_UserRelRelsNewPushGroup_UserRelToAttach.getPushgroup_user_id());
                attachedPushGroup_UserRelRelsNew.add(pushGroup_UserRelRelsNewPushGroup_UserRelToAttach);
            }
            pushGroup_UserRelRelsNew = attachedPushGroup_UserRelRelsNew;
            pushGroup.setPushGroup_UserRelRels(pushGroup_UserRelRelsNew);
            pushGroup = em.merge(pushGroup);
            for (PushGroup_UserRel pushGroup_UserRelRelsNewPushGroup_UserRel : pushGroup_UserRelRelsNew) {
                if (!pushGroup_UserRelRelsOld.contains(pushGroup_UserRelRelsNewPushGroup_UserRel)) {
                    PushGroup oldPushgroupOfPushGroup_UserRelRelsNewPushGroup_UserRel = pushGroup_UserRelRelsNewPushGroup_UserRel.getPushgroup();
                    pushGroup_UserRelRelsNewPushGroup_UserRel.setPushgroup(pushGroup);
                    pushGroup_UserRelRelsNewPushGroup_UserRel = em.merge(pushGroup_UserRelRelsNewPushGroup_UserRel);
                    if (oldPushgroupOfPushGroup_UserRelRelsNewPushGroup_UserRel != null && !oldPushgroupOfPushGroup_UserRelRelsNewPushGroup_UserRel.equals(pushGroup)) {
                        oldPushgroupOfPushGroup_UserRelRelsNewPushGroup_UserRel.getPushGroup_UserRelRels().remove(pushGroup_UserRelRelsNewPushGroup_UserRel);
                        oldPushgroupOfPushGroup_UserRelRelsNewPushGroup_UserRel = em.merge(oldPushgroupOfPushGroup_UserRelRelsNewPushGroup_UserRel);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = pushGroup.getPushgroup_id();
                if (findPushGroup(id) == null) {
                    throw new NonexistentEntityException("The pushGroup with id " + id + " no longer exists.");
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
            PushGroup pushGroup;
            try {
                pushGroup = em.getReference(PushGroup.class, id);
                pushGroup.getPushgroup_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pushGroup with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Set<PushGroup_UserRel> pushGroup_UserRelRelsOrphanCheck = pushGroup.getPushGroup_UserRelRels();
            for (PushGroup_UserRel pushGroup_UserRelRelsOrphanCheckPushGroup_UserRel : pushGroup_UserRelRelsOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This PushGroup (" + pushGroup + ") cannot be destroyed since the PushGroup_UserRel " + pushGroup_UserRelRelsOrphanCheckPushGroup_UserRel + " in its pushGroup_UserRelRels field has a non-nullable pushgroup field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(pushGroup);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PushGroup> findPushGroupEntities() {
        return findPushGroupEntities(true, -1, -1);
    }

    public List<PushGroup> findPushGroupEntities(int maxResults, int firstResult) {
        return findPushGroupEntities(false, maxResults, firstResult);
    }

    private List<PushGroup> findPushGroupEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PushGroup.class));
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

    public PushGroup findPushGroup(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PushGroup.class, id);
        } finally {
            em.close();
        }
    }

    public int getPushGroupCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PushGroup> rt = cq.from(PushGroup.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
