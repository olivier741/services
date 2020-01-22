/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.beans_entity.Promotion;
import com.tatsinktechnologic.beans_entity.Service;
import com.tatsinktechnologic.beans_entity.Action;
import java.util.HashSet;
import java.util.Set;
import com.tatsinktechnologic.beans_entity.Register;
import com.tatsinktechnologic.beans_entity.Charge_His;
import com.tatsinktechnologic.beans_entity.Content_Message;
import com.tatsinktechnologic.beans_entity.Product;
import com.tatsinktechnologic.dao.exceptions.IllegalOrphanException;
import com.tatsinktechnologic.dao.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class ProductJpaController implements Serializable {

    public ProductJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Product product) {
        if (product.getListAction() == null) {
            product.setListAction(new HashSet<Action>());
        }
        if (product.getListRegister() == null) {
            product.setListRegister(new HashSet<Register>());
        }
        if (product.getListCharge_His() == null) {
            product.setListCharge_His(new HashSet<Charge_His>());
        }
        if (product.getListContent_Message() == null) {
            product.setListContent_Message(new HashSet<Content_Message>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Promotion promotion = product.getPromotion();
            if (promotion != null) {
                promotion = em.getReference(promotion.getClass(), promotion.getPromotion_id());
                product.setPromotion(promotion);
            }
            Service service = product.getService();
            if (service != null) {
                service = em.getReference(service.getClass(), service.getService_id());
                product.setService(service);
            }
            Set<Action> attachedListAction = new HashSet<Action>();
            for (Action listActionActionToAttach : product.getListAction()) {
                listActionActionToAttach = em.getReference(listActionActionToAttach.getClass(), listActionActionToAttach.getAction_id());
                attachedListAction.add(listActionActionToAttach);
            }
            product.setListAction(attachedListAction);
            Set<Register> attachedListRegister = new HashSet<Register>();
            for (Register listRegisterRegisterToAttach : product.getListRegister()) {
                listRegisterRegisterToAttach = em.getReference(listRegisterRegisterToAttach.getClass(), listRegisterRegisterToAttach.getRegister_id());
                attachedListRegister.add(listRegisterRegisterToAttach);
            }
            product.setListRegister(attachedListRegister);
            Set<Charge_His> attachedListCharge_His = new HashSet<Charge_His>();
            for (Charge_His listCharge_HisCharge_HisToAttach : product.getListCharge_His()) {
                listCharge_HisCharge_HisToAttach = em.getReference(listCharge_HisCharge_HisToAttach.getClass(), listCharge_HisCharge_HisToAttach.getCharge_his_id());
                attachedListCharge_His.add(listCharge_HisCharge_HisToAttach);
            }
            product.setListCharge_His(attachedListCharge_His);
            Set<Content_Message> attachedListContent_Message = new HashSet<Content_Message>();
            for (Content_Message listContent_MessageContent_MessageToAttach : product.getListContent_Message()) {
                listContent_MessageContent_MessageToAttach = em.getReference(listContent_MessageContent_MessageToAttach.getClass(), listContent_MessageContent_MessageToAttach.getContent_msg_id());
                attachedListContent_Message.add(listContent_MessageContent_MessageToAttach);
            }
            product.setListContent_Message(attachedListContent_Message);
            em.persist(product);
            if (promotion != null) {
                promotion.getListProduct().add(product);
                promotion = em.merge(promotion);
            }
            if (service != null) {
                service.getListProduct().add(product);
                service = em.merge(service);
            }
            for (Action listActionAction : product.getListAction()) {
                Product oldProductOfListActionAction = listActionAction.getProduct();
                listActionAction.setProduct(product);
                listActionAction = em.merge(listActionAction);
                if (oldProductOfListActionAction != null) {
                    oldProductOfListActionAction.getListAction().remove(listActionAction);
                    oldProductOfListActionAction = em.merge(oldProductOfListActionAction);
                }
            }
            for (Register listRegisterRegister : product.getListRegister()) {
                Product oldProductOfListRegisterRegister = listRegisterRegister.getProduct();
                listRegisterRegister.setProduct(product);
                listRegisterRegister = em.merge(listRegisterRegister);
                if (oldProductOfListRegisterRegister != null) {
                    oldProductOfListRegisterRegister.getListRegister().remove(listRegisterRegister);
                    oldProductOfListRegisterRegister = em.merge(oldProductOfListRegisterRegister);
                }
            }
            for (Charge_His listCharge_HisCharge_His : product.getListCharge_His()) {
                Product oldProductOfListCharge_HisCharge_His = listCharge_HisCharge_His.getProduct();
                listCharge_HisCharge_His.setProduct(product);
                listCharge_HisCharge_His = em.merge(listCharge_HisCharge_His);
                if (oldProductOfListCharge_HisCharge_His != null) {
                    oldProductOfListCharge_HisCharge_His.getListCharge_His().remove(listCharge_HisCharge_His);
                    oldProductOfListCharge_HisCharge_His = em.merge(oldProductOfListCharge_HisCharge_His);
                }
            }
            for (Content_Message listContent_MessageContent_Message : product.getListContent_Message()) {
                Product oldProductOfListContent_MessageContent_Message = listContent_MessageContent_Message.getProduct();
                listContent_MessageContent_Message.setProduct(product);
                listContent_MessageContent_Message = em.merge(listContent_MessageContent_Message);
                if (oldProductOfListContent_MessageContent_Message != null) {
                    oldProductOfListContent_MessageContent_Message.getListContent_Message().remove(listContent_MessageContent_Message);
                    oldProductOfListContent_MessageContent_Message = em.merge(oldProductOfListContent_MessageContent_Message);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Product product) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Product persistentProduct = em.find(Product.class, product.getProduct_id());
            Promotion promotionOld = persistentProduct.getPromotion();
            Promotion promotionNew = product.getPromotion();
            Service serviceOld = persistentProduct.getService();
            Service serviceNew = product.getService();
            Set<Action> listActionOld = persistentProduct.getListAction();
            Set<Action> listActionNew = product.getListAction();
            Set<Register> listRegisterOld = persistentProduct.getListRegister();
            Set<Register> listRegisterNew = product.getListRegister();
            Set<Charge_His> listCharge_HisOld = persistentProduct.getListCharge_His();
            Set<Charge_His> listCharge_HisNew = product.getListCharge_His();
            Set<Content_Message> listContent_MessageOld = persistentProduct.getListContent_Message();
            Set<Content_Message> listContent_MessageNew = product.getListContent_Message();
            List<String> illegalOrphanMessages = null;
            for (Action listActionOldAction : listActionOld) {
                if (!listActionNew.contains(listActionOldAction)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Action " + listActionOldAction + " since its product field is not nullable.");
                }
            }
            for (Register listRegisterOldRegister : listRegisterOld) {
                if (!listRegisterNew.contains(listRegisterOldRegister)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Register " + listRegisterOldRegister + " since its product field is not nullable.");
                }
            }
            for (Charge_His listCharge_HisOldCharge_His : listCharge_HisOld) {
                if (!listCharge_HisNew.contains(listCharge_HisOldCharge_His)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Charge_His " + listCharge_HisOldCharge_His + " since its product field is not nullable.");
                }
            }
            for (Content_Message listContent_MessageOldContent_Message : listContent_MessageOld) {
                if (!listContent_MessageNew.contains(listContent_MessageOldContent_Message)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Content_Message " + listContent_MessageOldContent_Message + " since its product field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (promotionNew != null) {
                promotionNew = em.getReference(promotionNew.getClass(), promotionNew.getPromotion_id());
                product.setPromotion(promotionNew);
            }
            if (serviceNew != null) {
                serviceNew = em.getReference(serviceNew.getClass(), serviceNew.getService_id());
                product.setService(serviceNew);
            }
            Set<Action> attachedListActionNew = new HashSet<Action>();
            for (Action listActionNewActionToAttach : listActionNew) {
                listActionNewActionToAttach = em.getReference(listActionNewActionToAttach.getClass(), listActionNewActionToAttach.getAction_id());
                attachedListActionNew.add(listActionNewActionToAttach);
            }
            listActionNew = attachedListActionNew;
            product.setListAction(listActionNew);
            Set<Register> attachedListRegisterNew = new HashSet<Register>();
            for (Register listRegisterNewRegisterToAttach : listRegisterNew) {
                listRegisterNewRegisterToAttach = em.getReference(listRegisterNewRegisterToAttach.getClass(), listRegisterNewRegisterToAttach.getRegister_id());
                attachedListRegisterNew.add(listRegisterNewRegisterToAttach);
            }
            listRegisterNew = attachedListRegisterNew;
            product.setListRegister(listRegisterNew);
            Set<Charge_His> attachedListCharge_HisNew = new HashSet<Charge_His>();
            for (Charge_His listCharge_HisNewCharge_HisToAttach : listCharge_HisNew) {
                listCharge_HisNewCharge_HisToAttach = em.getReference(listCharge_HisNewCharge_HisToAttach.getClass(), listCharge_HisNewCharge_HisToAttach.getCharge_his_id());
                attachedListCharge_HisNew.add(listCharge_HisNewCharge_HisToAttach);
            }
            listCharge_HisNew = attachedListCharge_HisNew;
            product.setListCharge_His(listCharge_HisNew);
            Set<Content_Message> attachedListContent_MessageNew = new HashSet<Content_Message>();
            for (Content_Message listContent_MessageNewContent_MessageToAttach : listContent_MessageNew) {
                listContent_MessageNewContent_MessageToAttach = em.getReference(listContent_MessageNewContent_MessageToAttach.getClass(), listContent_MessageNewContent_MessageToAttach.getContent_msg_id());
                attachedListContent_MessageNew.add(listContent_MessageNewContent_MessageToAttach);
            }
            listContent_MessageNew = attachedListContent_MessageNew;
            product.setListContent_Message(listContent_MessageNew);
            product = em.merge(product);
            if (promotionOld != null && !promotionOld.equals(promotionNew)) {
                promotionOld.getListProduct().remove(product);
                promotionOld = em.merge(promotionOld);
            }
            if (promotionNew != null && !promotionNew.equals(promotionOld)) {
                promotionNew.getListProduct().add(product);
                promotionNew = em.merge(promotionNew);
            }
            if (serviceOld != null && !serviceOld.equals(serviceNew)) {
                serviceOld.getListProduct().remove(product);
                serviceOld = em.merge(serviceOld);
            }
            if (serviceNew != null && !serviceNew.equals(serviceOld)) {
                serviceNew.getListProduct().add(product);
                serviceNew = em.merge(serviceNew);
            }
            for (Action listActionNewAction : listActionNew) {
                if (!listActionOld.contains(listActionNewAction)) {
                    Product oldProductOfListActionNewAction = listActionNewAction.getProduct();
                    listActionNewAction.setProduct(product);
                    listActionNewAction = em.merge(listActionNewAction);
                    if (oldProductOfListActionNewAction != null && !oldProductOfListActionNewAction.equals(product)) {
                        oldProductOfListActionNewAction.getListAction().remove(listActionNewAction);
                        oldProductOfListActionNewAction = em.merge(oldProductOfListActionNewAction);
                    }
                }
            }
            for (Register listRegisterNewRegister : listRegisterNew) {
                if (!listRegisterOld.contains(listRegisterNewRegister)) {
                    Product oldProductOfListRegisterNewRegister = listRegisterNewRegister.getProduct();
                    listRegisterNewRegister.setProduct(product);
                    listRegisterNewRegister = em.merge(listRegisterNewRegister);
                    if (oldProductOfListRegisterNewRegister != null && !oldProductOfListRegisterNewRegister.equals(product)) {
                        oldProductOfListRegisterNewRegister.getListRegister().remove(listRegisterNewRegister);
                        oldProductOfListRegisterNewRegister = em.merge(oldProductOfListRegisterNewRegister);
                    }
                }
            }
            for (Charge_His listCharge_HisNewCharge_His : listCharge_HisNew) {
                if (!listCharge_HisOld.contains(listCharge_HisNewCharge_His)) {
                    Product oldProductOfListCharge_HisNewCharge_His = listCharge_HisNewCharge_His.getProduct();
                    listCharge_HisNewCharge_His.setProduct(product);
                    listCharge_HisNewCharge_His = em.merge(listCharge_HisNewCharge_His);
                    if (oldProductOfListCharge_HisNewCharge_His != null && !oldProductOfListCharge_HisNewCharge_His.equals(product)) {
                        oldProductOfListCharge_HisNewCharge_His.getListCharge_His().remove(listCharge_HisNewCharge_His);
                        oldProductOfListCharge_HisNewCharge_His = em.merge(oldProductOfListCharge_HisNewCharge_His);
                    }
                }
            }
            for (Content_Message listContent_MessageNewContent_Message : listContent_MessageNew) {
                if (!listContent_MessageOld.contains(listContent_MessageNewContent_Message)) {
                    Product oldProductOfListContent_MessageNewContent_Message = listContent_MessageNewContent_Message.getProduct();
                    listContent_MessageNewContent_Message.setProduct(product);
                    listContent_MessageNewContent_Message = em.merge(listContent_MessageNewContent_Message);
                    if (oldProductOfListContent_MessageNewContent_Message != null && !oldProductOfListContent_MessageNewContent_Message.equals(product)) {
                        oldProductOfListContent_MessageNewContent_Message.getListContent_Message().remove(listContent_MessageNewContent_Message);
                        oldProductOfListContent_MessageNewContent_Message = em.merge(oldProductOfListContent_MessageNewContent_Message);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = product.getProduct_id();
                if (findProduct(id) == null) {
                    throw new NonexistentEntityException("The product with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Product product;
            try {
                product = em.getReference(Product.class, id);
                product.getProduct_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The product with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Set<Action> listActionOrphanCheck = product.getListAction();
            for (Action listActionOrphanCheckAction : listActionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Product (" + product + ") cannot be destroyed since the Action " + listActionOrphanCheckAction + " in its listAction field has a non-nullable product field.");
            }
            Set<Register> listRegisterOrphanCheck = product.getListRegister();
            for (Register listRegisterOrphanCheckRegister : listRegisterOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Product (" + product + ") cannot be destroyed since the Register " + listRegisterOrphanCheckRegister + " in its listRegister field has a non-nullable product field.");
            }
            Set<Charge_His> listCharge_HisOrphanCheck = product.getListCharge_His();
            for (Charge_His listCharge_HisOrphanCheckCharge_His : listCharge_HisOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Product (" + product + ") cannot be destroyed since the Charge_His " + listCharge_HisOrphanCheckCharge_His + " in its listCharge_His field has a non-nullable product field.");
            }
            Set<Content_Message> listContent_MessageOrphanCheck = product.getListContent_Message();
            for (Content_Message listContent_MessageOrphanCheckContent_Message : listContent_MessageOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Product (" + product + ") cannot be destroyed since the Content_Message " + listContent_MessageOrphanCheckContent_Message + " in its listContent_Message field has a non-nullable product field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Promotion promotion = product.getPromotion();
            if (promotion != null) {
                promotion.getListProduct().remove(product);
                promotion = em.merge(promotion);
            }
            Service service = product.getService();
            if (service != null) {
                service.getListProduct().remove(product);
                service = em.merge(service);
            }
            em.remove(product);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Product> findProductEntities() {
        return findProductEntities(true, -1, -1);
    }

    public List<Product> findProductEntities(int maxResults, int firstResult) {
        return findProductEntities(false, maxResults, firstResult);
    }

    private List<Product> findProductEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Product.class));
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

    public Product findProduct(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Product.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Product> rt = cq.from(Product.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
