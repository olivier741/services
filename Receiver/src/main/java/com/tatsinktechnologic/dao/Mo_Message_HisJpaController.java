/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao;

import com.tatsinktechnologic.beans_entity.Mo_Message_His;
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
public class Mo_Message_HisJpaController implements Serializable {

    public Mo_Message_HisJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Mo_Message_His mo_Message_His) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Process_Unit process_unit = mo_Message_His.getProcess_unit();
            if (process_unit != null) {
                process_unit = em.getReference(process_unit.getClass(), process_unit.getProcess_unit_id());
                mo_Message_His.setProcess_unit(process_unit);
            }
            em.persist(mo_Message_His);
            if (process_unit != null) {
                process_unit.getListMo_Message_His().add(mo_Message_His);
                process_unit = em.merge(process_unit);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Mo_Message_His mo_Message_His) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Mo_Message_His persistentMo_Message_His = em.find(Mo_Message_His.class, mo_Message_His.getMo_message_his_id());
            Process_Unit process_unitOld = persistentMo_Message_His.getProcess_unit();
            Process_Unit process_unitNew = mo_Message_His.getProcess_unit();
            if (process_unitNew != null) {
                process_unitNew = em.getReference(process_unitNew.getClass(), process_unitNew.getProcess_unit_id());
                mo_Message_His.setProcess_unit(process_unitNew);
            }
            mo_Message_His = em.merge(mo_Message_His);
            if (process_unitOld != null && !process_unitOld.equals(process_unitNew)) {
                process_unitOld.getListMo_Message_His().remove(mo_Message_His);
                process_unitOld = em.merge(process_unitOld);
            }
            if (process_unitNew != null && !process_unitNew.equals(process_unitOld)) {
                process_unitNew.getListMo_Message_His().add(mo_Message_His);
                process_unitNew = em.merge(process_unitNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = mo_Message_His.getMo_message_his_id();
                if (findMo_Message_His(id) == null) {
                    throw new NonexistentEntityException("The mo_Message_His with id " + id + " no longer exists.");
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
            Mo_Message_His mo_Message_His;
            try {
                mo_Message_His = em.getReference(Mo_Message_His.class, id);
                mo_Message_His.getMo_message_his_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The mo_Message_His with id " + id + " no longer exists.", enfe);
            }
            Process_Unit process_unit = mo_Message_His.getProcess_unit();
            if (process_unit != null) {
                process_unit.getListMo_Message_His().remove(mo_Message_His);
                process_unit = em.merge(process_unit);
            }
            em.remove(mo_Message_His);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Mo_Message_His> findMo_Message_HisEntities() {
        return findMo_Message_HisEntities(true, -1, -1);
    }

    public List<Mo_Message_His> findMo_Message_HisEntities(int maxResults, int firstResult) {
        return findMo_Message_HisEntities(false, maxResults, firstResult);
    }

    private List<Mo_Message_His> findMo_Message_HisEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Mo_Message_His.class));
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

    public Mo_Message_His findMo_Message_His(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Mo_Message_His.class, id);
        } finally {
            em.close();
        }
    }

    public int getMo_Message_HisCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Mo_Message_His> rt = cq.from(Mo_Message_His.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
