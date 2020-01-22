/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao;

import com.tatsinktechnologic.beans_entity.Charge_His;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.beans_entity.Product;
import com.tatsinktechnologic.beans_entity.Process_Unit;
import com.tatsinktechnologic.dao.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class Charge_HisJpaController implements Serializable {

    public Charge_HisJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Charge_His charge_His) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Product product = charge_His.getProduct();
            if (product != null) {
                product = em.getReference(product.getClass(), product.getProduct_id());
                charge_His.setProduct(product);
            }
            Process_Unit process_unit = charge_His.getProcess_unit();
            if (process_unit != null) {
                process_unit = em.getReference(process_unit.getClass(), process_unit.getProcess_unit_id());
                charge_His.setProcess_unit(process_unit);
            }
            em.persist(charge_His);
            if (product != null) {
                product.getListCharge_His().add(charge_His);
                product = em.merge(product);
            }
            if (process_unit != null) {
                process_unit.getListCharge_His().add(charge_His);
                process_unit = em.merge(process_unit);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Charge_His charge_His) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Charge_His persistentCharge_His = em.find(Charge_His.class, charge_His.getCharge_his_id());
            Product productOld = persistentCharge_His.getProduct();
            Product productNew = charge_His.getProduct();
            Process_Unit process_unitOld = persistentCharge_His.getProcess_unit();
            Process_Unit process_unitNew = charge_His.getProcess_unit();
            if (productNew != null) {
                productNew = em.getReference(productNew.getClass(), productNew.getProduct_id());
                charge_His.setProduct(productNew);
            }
            if (process_unitNew != null) {
                process_unitNew = em.getReference(process_unitNew.getClass(), process_unitNew.getProcess_unit_id());
                charge_His.setProcess_unit(process_unitNew);
            }
            charge_His = em.merge(charge_His);
            if (productOld != null && !productOld.equals(productNew)) {
                productOld.getListCharge_His().remove(charge_His);
                productOld = em.merge(productOld);
            }
            if (productNew != null && !productNew.equals(productOld)) {
                productNew.getListCharge_His().add(charge_His);
                productNew = em.merge(productNew);
            }
            if (process_unitOld != null && !process_unitOld.equals(process_unitNew)) {
                process_unitOld.getListCharge_His().remove(charge_His);
                process_unitOld = em.merge(process_unitOld);
            }
            if (process_unitNew != null && !process_unitNew.equals(process_unitOld)) {
                process_unitNew.getListCharge_His().add(charge_His);
                process_unitNew = em.merge(process_unitNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = charge_His.getCharge_his_id();
                if (findCharge_His(id) == null) {
                    throw new NonexistentEntityException("The charge_His with id " + id + " no longer exists.");
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
            Charge_His charge_His;
            try {
                charge_His = em.getReference(Charge_His.class, id);
                charge_His.getCharge_his_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The charge_His with id " + id + " no longer exists.", enfe);
            }
            Product product = charge_His.getProduct();
            if (product != null) {
                product.getListCharge_His().remove(charge_His);
                product = em.merge(product);
            }
            Process_Unit process_unit = charge_His.getProcess_unit();
            if (process_unit != null) {
                process_unit.getListCharge_His().remove(charge_His);
                process_unit = em.merge(process_unit);
            }
            em.remove(charge_His);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Charge_His> findCharge_HisEntities() {
        return findCharge_HisEntities(true, -1, -1);
    }

    public List<Charge_His> findCharge_HisEntities(int maxResults, int firstResult) {
        return findCharge_HisEntities(false, maxResults, firstResult);
    }

    private List<Charge_His> findCharge_HisEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Charge_His.class));
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

    public Charge_His findCharge_His(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Charge_His.class, id);
        } finally {
            em.close();
        }
    }

    public int getCharge_HisCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Charge_His> rt = cq.from(Charge_His.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
