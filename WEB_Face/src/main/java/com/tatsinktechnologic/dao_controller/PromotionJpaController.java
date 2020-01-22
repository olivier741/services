/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_controller;

import com.tatsinktechnologic.dao_controller.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.dao_controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entities.registration.Product;
import com.tatsinktechnologic.entities.registration.Promotion;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
public class PromotionJpaController implements Serializable {

    @Inject
    EntityManagerFactory emf;
    
    @Resource
    private UserTransaction utx;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Promotion promotion) throws RollbackFailureException, Exception {
        if (promotion.getListProduct() == null) {
            promotion.setListProduct(new HashSet<Product>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Set<Product> attachedListProduct = new HashSet<Product>();
            for (Product listProductProductToAttach : promotion.getListProduct()) {
                listProductProductToAttach = em.getReference(listProductProductToAttach.getClass(), listProductProductToAttach.getProduct_id());
                attachedListProduct.add(listProductProductToAttach);
            }
            promotion.setListProduct(attachedListProduct);
            em.persist(promotion);
            for (Product listProductProduct : promotion.getListProduct()) {
                Promotion oldPromotionOfListProductProduct = listProductProduct.getPromotion();
                listProductProduct.setPromotion(promotion);
                listProductProduct = em.merge(listProductProduct);
                if (oldPromotionOfListProductProduct != null) {
                    oldPromotionOfListProductProduct.getListProduct().remove(listProductProduct);
                    oldPromotionOfListProductProduct = em.merge(oldPromotionOfListProductProduct);
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

    public void edit(Promotion promotion) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Promotion persistentPromotion = em.find(Promotion.class, promotion.getPromotion_id());
            Set<Product> listProductOld = persistentPromotion.getListProduct();
            Set<Product> listProductNew = promotion.getListProduct();
            Set<Product> attachedListProductNew = new HashSet<Product>();
            for (Product listProductNewProductToAttach : listProductNew) {
                listProductNewProductToAttach = em.getReference(listProductNewProductToAttach.getClass(), listProductNewProductToAttach.getProduct_id());
                attachedListProductNew.add(listProductNewProductToAttach);
            }
            listProductNew = attachedListProductNew;
            promotion.setListProduct(listProductNew);
            promotion = em.merge(promotion);
            for (Product listProductOldProduct : listProductOld) {
                if (!listProductNew.contains(listProductOldProduct)) {
                    listProductOldProduct.setPromotion(null);
                    listProductOldProduct = em.merge(listProductOldProduct);
                }
            }
            for (Product listProductNewProduct : listProductNew) {
                if (!listProductOld.contains(listProductNewProduct)) {
                    Promotion oldPromotionOfListProductNewProduct = listProductNewProduct.getPromotion();
                    listProductNewProduct.setPromotion(promotion);
                    listProductNewProduct = em.merge(listProductNewProduct);
                    if (oldPromotionOfListProductNewProduct != null && !oldPromotionOfListProductNewProduct.equals(promotion)) {
                        oldPromotionOfListProductNewProduct.getListProduct().remove(listProductNewProduct);
                        oldPromotionOfListProductNewProduct = em.merge(oldPromotionOfListProductNewProduct);
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
                int id = promotion.getPromotion_id();
                if (findPromotion(id) == null) {
                    throw new NonexistentEntityException("The promotion with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Promotion promotion;
            try {
                promotion = em.getReference(Promotion.class, id);
                promotion.getPromotion_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The promotion with id " + id + " no longer exists.", enfe);
            }
            Set<Product> listProduct = promotion.getListProduct();
            for (Product listProductProduct : listProduct) {
                listProductProduct.setPromotion(null);
                listProductProduct = em.merge(listProductProduct);
            }
            em.remove(promotion);
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

    public List<Promotion> findPromotionEntities() {
        return findPromotionEntities(true, -1, -1);
    }

    public List<Promotion> findPromotionEntities(int maxResults, int firstResult) {
        return findPromotionEntities(false, maxResults, firstResult);
    }

    private List<Promotion> findPromotionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Promotion.class));
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

    public Promotion findPromotion(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Promotion.class, id);
        } finally {
            em.close();
        }
    }

    public int getPromotionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Promotion> rt = cq.from(Promotion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
