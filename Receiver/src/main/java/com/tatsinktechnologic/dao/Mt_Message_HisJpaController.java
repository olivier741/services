/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao;

import com.tatsinktechnologic.beans_entity.Mt_Message_His;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.beans_entity.Process_Unit;
import com.tatsinktechnologic.dao.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class Mt_Message_HisJpaController implements Serializable {

    public Mt_Message_HisJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Mt_Message_His mt_Message_His) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Process_Unit process_unit = mt_Message_His.getProcess_unit();
            if (process_unit != null) {
                process_unit = em.getReference(process_unit.getClass(), process_unit.getProcess_unit_id());
                mt_Message_His.setProcess_unit(process_unit);
            }
            em.persist(mt_Message_His);
            if (process_unit != null) {
                process_unit.getListMt_Message_his().add(mt_Message_His);
                process_unit = em.merge(process_unit);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Mt_Message_His mt_Message_His) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Mt_Message_His persistentMt_Message_His = em.find(Mt_Message_His.class, mt_Message_His.getMt_message_his_id());
            Process_Unit process_unitOld = persistentMt_Message_His.getProcess_unit();
            Process_Unit process_unitNew = mt_Message_His.getProcess_unit();
            if (process_unitNew != null) {
                process_unitNew = em.getReference(process_unitNew.getClass(), process_unitNew.getProcess_unit_id());
                mt_Message_His.setProcess_unit(process_unitNew);
            }
            mt_Message_His = em.merge(mt_Message_His);
            if (process_unitOld != null && !process_unitOld.equals(process_unitNew)) {
                process_unitOld.getListMt_Message_his().remove(mt_Message_His);
                process_unitOld = em.merge(process_unitOld);
            }
            if (process_unitNew != null && !process_unitNew.equals(process_unitOld)) {
                process_unitNew.getListMt_Message_his().add(mt_Message_His);
                process_unitNew = em.merge(process_unitNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = mt_Message_His.getMt_message_his_id();
                if (findMt_Message_His(id) == null) {
                    throw new NonexistentEntityException("The mt_Message_His with id " + id + " no longer exists.");
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
            Mt_Message_His mt_Message_His;
            try {
                mt_Message_His = em.getReference(Mt_Message_His.class, id);
                mt_Message_His.getMt_message_his_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The mt_Message_His with id " + id + " no longer exists.", enfe);
            }
            Process_Unit process_unit = mt_Message_His.getProcess_unit();
            if (process_unit != null) {
                process_unit.getListMt_Message_his().remove(mt_Message_His);
                process_unit = em.merge(process_unit);
            }
            em.remove(mt_Message_His);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Mt_Message_His> findMt_Message_HisEntities() {
        return findMt_Message_HisEntities(true, -1, -1);
    }

    public List<Mt_Message_His> findMt_Message_HisEntities(int maxResults, int firstResult) {
        return findMt_Message_HisEntities(false, maxResults, firstResult);
    }

    private List<Mt_Message_His> findMt_Message_HisEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Mt_Message_His.class));
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

    public Mt_Message_His findMt_Message_His(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Mt_Message_His.class, id);
        } finally {
            em.close();
        }
    }

    public int getMt_Message_HisCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Mt_Message_His> rt = cq.from(Mt_Message_His.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
