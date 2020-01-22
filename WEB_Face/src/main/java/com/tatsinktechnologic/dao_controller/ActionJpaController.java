/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_controller;

import com.tatsinktechnologic.dao_controller.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.dao_controller.exceptions.RollbackFailureException;
import com.tatsinktechnologic.entities.registration.Action;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entities.registration.Product;
import com.tatsinktechnologic.entities.registration.Command;
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
public class ActionJpaController implements Serializable {

    @Inject
    EntityManagerFactory emf;
    
    @Resource
    private UserTransaction utx;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Action action) throws RollbackFailureException, Exception {
        if (action.getListCommand() == null) {
            action.setListCommand(new HashSet<Command>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Product product = action.getProduct();
            if (product != null) {
                product = em.getReference(product.getClass(), product.getProduct_id());
                action.setProduct(product);
            }
            Set<Command> attachedListCommand = new HashSet<Command>();
            for (Command listCommandCommandToAttach : action.getListCommand()) {
                listCommandCommandToAttach = em.getReference(listCommandCommandToAttach.getClass(), listCommandCommandToAttach.getCommand_id());
                attachedListCommand.add(listCommandCommandToAttach);
            }
            action.setListCommand(attachedListCommand);
            em.persist(action);
            if (product != null) {
                product.getListAction().add(action);
                product = em.merge(product);
            }
            for (Command listCommandCommand : action.getListCommand()) {
                Action oldActionOfListCommandCommand = listCommandCommand.getAction();
                listCommandCommand.setAction(action);
                listCommandCommand = em.merge(listCommandCommand);
                if (oldActionOfListCommandCommand != null) {
                    oldActionOfListCommandCommand.getListCommand().remove(listCommandCommand);
                    oldActionOfListCommandCommand = em.merge(oldActionOfListCommandCommand);
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

    public void edit(Action action) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Action persistentAction = em.find(Action.class, action.getAction_id());
            Product productOld = persistentAction.getProduct();
            Product productNew = action.getProduct();
            Set<Command> listCommandOld = persistentAction.getListCommand();
            Set<Command> listCommandNew = action.getListCommand();
            if (productNew != null) {
                productNew = em.getReference(productNew.getClass(), productNew.getProduct_id());
                action.setProduct(productNew);
            }
            Set<Command> attachedListCommandNew = new HashSet<Command>();
            for (Command listCommandNewCommandToAttach : listCommandNew) {
                listCommandNewCommandToAttach = em.getReference(listCommandNewCommandToAttach.getClass(), listCommandNewCommandToAttach.getCommand_id());
                attachedListCommandNew.add(listCommandNewCommandToAttach);
            }
            listCommandNew = attachedListCommandNew;
            action.setListCommand(listCommandNew);
            action = em.merge(action);
            if (productOld != null && !productOld.equals(productNew)) {
                productOld.getListAction().remove(action);
                productOld = em.merge(productOld);
            }
            if (productNew != null && !productNew.equals(productOld)) {
                productNew.getListAction().add(action);
                productNew = em.merge(productNew);
            }
            for (Command listCommandOldCommand : listCommandOld) {
                if (!listCommandNew.contains(listCommandOldCommand)) {
                    listCommandOldCommand.setAction(null);
                    listCommandOldCommand = em.merge(listCommandOldCommand);
                }
            }
            for (Command listCommandNewCommand : listCommandNew) {
                if (!listCommandOld.contains(listCommandNewCommand)) {
                    Action oldActionOfListCommandNewCommand = listCommandNewCommand.getAction();
                    listCommandNewCommand.setAction(action);
                    listCommandNewCommand = em.merge(listCommandNewCommand);
                    if (oldActionOfListCommandNewCommand != null && !oldActionOfListCommandNewCommand.equals(action)) {
                        oldActionOfListCommandNewCommand.getListCommand().remove(listCommandNewCommand);
                        oldActionOfListCommandNewCommand = em.merge(oldActionOfListCommandNewCommand);
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
                int id = action.getAction_id();
                if (findAction(id) == null) {
                    throw new NonexistentEntityException("The action with id " + id + " no longer exists.");
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
            Action action;
            try {
                action = em.getReference(Action.class, id);
                action.getAction_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The action with id " + id + " no longer exists.", enfe);
            }
            Product product = action.getProduct();
            if (product != null) {
                product.getListAction().remove(action);
                product = em.merge(product);
            }
            Set<Command> listCommand = action.getListCommand();
            for (Command listCommandCommand : listCommand) {
                listCommandCommand.setAction(null);
                listCommandCommand = em.merge(listCommandCommand);
            }
            em.remove(action);
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

    public List<Action> findActionEntities() {
        return findActionEntities(true, -1, -1);
    }

    public List<Action> findActionEntities(int maxResults, int firstResult) {
        return findActionEntities(false, maxResults, firstResult);
    }

    private List<Action> findActionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Action.class));
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

    public Action findAction(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Action.class, id);
        } finally {
            em.close();
        }
    }

    public int getActionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Action> rt = cq.from(Action.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
