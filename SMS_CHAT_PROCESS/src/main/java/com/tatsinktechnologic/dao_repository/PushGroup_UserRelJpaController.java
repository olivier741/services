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
import com.tatsinktechnologic.entity.sms_chat.PushGroup;
import com.tatsinktechnologic.entity.sms_chat.PushGroup_UserRel;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class PushGroup_UserRelJpaController implements Serializable {

    public PushGroup_UserRelJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PushGroup_UserRel pushGroup_UserRel) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PushGroup pushgroup = pushGroup_UserRel.getPushgroup();
            if (pushgroup != null) {
                pushgroup = em.getReference(pushgroup.getClass(), pushgroup.getPushgroup_id());
                pushGroup_UserRel.setPushgroup(pushgroup);
            }
            em.persist(pushGroup_UserRel);
            if (pushgroup != null) {
                pushgroup.getPushGroup_UserRelRels().add(pushGroup_UserRel);
                pushgroup = em.merge(pushgroup);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PushGroup_UserRel pushGroup_UserRel) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PushGroup_UserRel persistentPushGroup_UserRel = em.find(PushGroup_UserRel.class, pushGroup_UserRel.getPushgroup_user_id());
            PushGroup pushgroupOld = persistentPushGroup_UserRel.getPushgroup();
            PushGroup pushgroupNew = pushGroup_UserRel.getPushgroup();
            if (pushgroupNew != null) {
                pushgroupNew = em.getReference(pushgroupNew.getClass(), pushgroupNew.getPushgroup_id());
                pushGroup_UserRel.setPushgroup(pushgroupNew);
            }
            pushGroup_UserRel = em.merge(pushGroup_UserRel);
            if (pushgroupOld != null && !pushgroupOld.equals(pushgroupNew)) {
                pushgroupOld.getPushGroup_UserRelRels().remove(pushGroup_UserRel);
                pushgroupOld = em.merge(pushgroupOld);
            }
            if (pushgroupNew != null && !pushgroupNew.equals(pushgroupOld)) {
                pushgroupNew.getPushGroup_UserRelRels().add(pushGroup_UserRel);
                pushgroupNew = em.merge(pushgroupNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = pushGroup_UserRel.getPushgroup_user_id();
                if (findPushGroup_UserRel(id) == null) {
                    throw new NonexistentEntityException("The pushGroup_UserRel with id " + id + " no longer exists.");
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
            PushGroup_UserRel pushGroup_UserRel;
            try {
                pushGroup_UserRel = em.getReference(PushGroup_UserRel.class, id);
                pushGroup_UserRel.getPushgroup_user_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pushGroup_UserRel with id " + id + " no longer exists.", enfe);
            }
            PushGroup pushgroup = pushGroup_UserRel.getPushgroup();
            if (pushgroup != null) {
                pushgroup.getPushGroup_UserRelRels().remove(pushGroup_UserRel);
                pushgroup = em.merge(pushgroup);
            }
            em.remove(pushGroup_UserRel);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PushGroup_UserRel> findPushGroup_UserRelEntities() {
        return findPushGroup_UserRelEntities(true, -1, -1);
    }

    public List<PushGroup_UserRel> findPushGroup_UserRelEntities(int maxResults, int firstResult) {
        return findPushGroup_UserRelEntities(false, maxResults, firstResult);
    }

    private List<PushGroup_UserRel> findPushGroup_UserRelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PushGroup_UserRel.class));
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

    public PushGroup_UserRel findPushGroup_UserRel(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PushGroup_UserRel.class, id);
        } finally {
            em.close();
        }
    }

    public int getPushGroup_UserRelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PushGroup_UserRel> rt = cq.from(PushGroup_UserRel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
