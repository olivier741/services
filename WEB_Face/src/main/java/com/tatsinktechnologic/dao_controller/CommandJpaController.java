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
import com.tatsinktechnologic.entities.registration.Action;
import com.tatsinktechnologic.entities.registration.Command;
import com.tatsinktechnologic.entities.registration.Parameter;
import com.tatsinktechnologic.entities.registration.Mo_Hist;
import java.util.HashSet;
import java.util.Set;
import com.tatsinktechnologic.entities.registration.Mt_Hist;
import java.util.List;
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
public class CommandJpaController implements Serializable {

    @Inject
    EntityManagerFactory emf;
    
    @Resource
    private UserTransaction utx;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Command command) throws RollbackFailureException, Exception {
        if (command.getListMo_Hist() == null) {
            command.setListMo_Hist(new HashSet<Mo_Hist>());
        }
        if (command.getListMt_Hist() == null) {
            command.setListMt_Hist(new HashSet<Mt_Hist>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Action action = command.getAction();
            if (action != null) {
                action = em.getReference(action.getClass(), action.getAction_id());
                command.setAction(action);
            }
            Parameter parameter = command.getParameter();
            if (parameter != null) {
                parameter = em.getReference(parameter.getClass(), parameter.getParameter_id());
                command.setParameter(parameter);
            }
            Set<Mo_Hist> attachedListMo_Hist = new HashSet<Mo_Hist>();
            for (Mo_Hist listMo_HistMo_HistToAttach : command.getListMo_Hist()) {
                listMo_HistMo_HistToAttach = em.getReference(listMo_HistMo_HistToAttach.getClass(), listMo_HistMo_HistToAttach.getMo_his_id());
                attachedListMo_Hist.add(listMo_HistMo_HistToAttach);
            }
            command.setListMo_Hist(attachedListMo_Hist);
            Set<Mt_Hist> attachedListMt_Hist = new HashSet<Mt_Hist>();
            for (Mt_Hist listMt_HistMt_HistToAttach : command.getListMt_Hist()) {
                listMt_HistMt_HistToAttach = em.getReference(listMt_HistMt_HistToAttach.getClass(), listMt_HistMt_HistToAttach.getMt_his_id());
                attachedListMt_Hist.add(listMt_HistMt_HistToAttach);
            }
            command.setListMt_Hist(attachedListMt_Hist);
            em.persist(command);
            if (action != null) {
                action.getListCommand().add(command);
                action = em.merge(action);
            }
            if (parameter != null) {
                parameter.getListCommand().add(command);
                parameter = em.merge(parameter);
            }
            for (Mo_Hist listMo_HistMo_Hist : command.getListMo_Hist()) {
                Command oldCommandOfListMo_HistMo_Hist = listMo_HistMo_Hist.getCommand();
                listMo_HistMo_Hist.setCommand(command);
                listMo_HistMo_Hist = em.merge(listMo_HistMo_Hist);
                if (oldCommandOfListMo_HistMo_Hist != null) {
                    oldCommandOfListMo_HistMo_Hist.getListMo_Hist().remove(listMo_HistMo_Hist);
                    oldCommandOfListMo_HistMo_Hist = em.merge(oldCommandOfListMo_HistMo_Hist);
                }
            }
            for (Mt_Hist listMt_HistMt_Hist : command.getListMt_Hist()) {
                Command oldCommandOfListMt_HistMt_Hist = listMt_HistMt_Hist.getCommand();
                listMt_HistMt_Hist.setCommand(command);
                listMt_HistMt_Hist = em.merge(listMt_HistMt_Hist);
                if (oldCommandOfListMt_HistMt_Hist != null) {
                    oldCommandOfListMt_HistMt_Hist.getListMt_Hist().remove(listMt_HistMt_Hist);
                    oldCommandOfListMt_HistMt_Hist = em.merge(oldCommandOfListMt_HistMt_Hist);
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

    public void edit(Command command) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Command persistentCommand = em.find(Command.class, command.getCommand_id());
            Action actionOld = persistentCommand.getAction();
            Action actionNew = command.getAction();
            Parameter parameterOld = persistentCommand.getParameter();
            Parameter parameterNew = command.getParameter();
            Set<Mo_Hist> listMo_HistOld = persistentCommand.getListMo_Hist();
            Set<Mo_Hist> listMo_HistNew = command.getListMo_Hist();
            Set<Mt_Hist> listMt_HistOld = persistentCommand.getListMt_Hist();
            Set<Mt_Hist> listMt_HistNew = command.getListMt_Hist();
            if (actionNew != null) {
                actionNew = em.getReference(actionNew.getClass(), actionNew.getAction_id());
                command.setAction(actionNew);
            }
            if (parameterNew != null) {
                parameterNew = em.getReference(parameterNew.getClass(), parameterNew.getParameter_id());
                command.setParameter(parameterNew);
            }
            Set<Mo_Hist> attachedListMo_HistNew = new HashSet<Mo_Hist>();
            for (Mo_Hist listMo_HistNewMo_HistToAttach : listMo_HistNew) {
                listMo_HistNewMo_HistToAttach = em.getReference(listMo_HistNewMo_HistToAttach.getClass(), listMo_HistNewMo_HistToAttach.getMo_his_id());
                attachedListMo_HistNew.add(listMo_HistNewMo_HistToAttach);
            }
            listMo_HistNew = attachedListMo_HistNew;
            command.setListMo_Hist(listMo_HistNew);
            Set<Mt_Hist> attachedListMt_HistNew = new HashSet<Mt_Hist>();
            for (Mt_Hist listMt_HistNewMt_HistToAttach : listMt_HistNew) {
                listMt_HistNewMt_HistToAttach = em.getReference(listMt_HistNewMt_HistToAttach.getClass(), listMt_HistNewMt_HistToAttach.getMt_his_id());
                attachedListMt_HistNew.add(listMt_HistNewMt_HistToAttach);
            }
            listMt_HistNew = attachedListMt_HistNew;
            command.setListMt_Hist(listMt_HistNew);
            command = em.merge(command);
            if (actionOld != null && !actionOld.equals(actionNew)) {
                actionOld.getListCommand().remove(command);
                actionOld = em.merge(actionOld);
            }
            if (actionNew != null && !actionNew.equals(actionOld)) {
                actionNew.getListCommand().add(command);
                actionNew = em.merge(actionNew);
            }
            if (parameterOld != null && !parameterOld.equals(parameterNew)) {
                parameterOld.getListCommand().remove(command);
                parameterOld = em.merge(parameterOld);
            }
            if (parameterNew != null && !parameterNew.equals(parameterOld)) {
                parameterNew.getListCommand().add(command);
                parameterNew = em.merge(parameterNew);
            }
            for (Mo_Hist listMo_HistOldMo_Hist : listMo_HistOld) {
                if (!listMo_HistNew.contains(listMo_HistOldMo_Hist)) {
                    listMo_HistOldMo_Hist.setCommand(null);
                    listMo_HistOldMo_Hist = em.merge(listMo_HistOldMo_Hist);
                }
            }
            for (Mo_Hist listMo_HistNewMo_Hist : listMo_HistNew) {
                if (!listMo_HistOld.contains(listMo_HistNewMo_Hist)) {
                    Command oldCommandOfListMo_HistNewMo_Hist = listMo_HistNewMo_Hist.getCommand();
                    listMo_HistNewMo_Hist.setCommand(command);
                    listMo_HistNewMo_Hist = em.merge(listMo_HistNewMo_Hist);
                    if (oldCommandOfListMo_HistNewMo_Hist != null && !oldCommandOfListMo_HistNewMo_Hist.equals(command)) {
                        oldCommandOfListMo_HistNewMo_Hist.getListMo_Hist().remove(listMo_HistNewMo_Hist);
                        oldCommandOfListMo_HistNewMo_Hist = em.merge(oldCommandOfListMo_HistNewMo_Hist);
                    }
                }
            }
            for (Mt_Hist listMt_HistOldMt_Hist : listMt_HistOld) {
                if (!listMt_HistNew.contains(listMt_HistOldMt_Hist)) {
                    listMt_HistOldMt_Hist.setCommand(null);
                    listMt_HistOldMt_Hist = em.merge(listMt_HistOldMt_Hist);
                }
            }
            for (Mt_Hist listMt_HistNewMt_Hist : listMt_HistNew) {
                if (!listMt_HistOld.contains(listMt_HistNewMt_Hist)) {
                    Command oldCommandOfListMt_HistNewMt_Hist = listMt_HistNewMt_Hist.getCommand();
                    listMt_HistNewMt_Hist.setCommand(command);
                    listMt_HistNewMt_Hist = em.merge(listMt_HistNewMt_Hist);
                    if (oldCommandOfListMt_HistNewMt_Hist != null && !oldCommandOfListMt_HistNewMt_Hist.equals(command)) {
                        oldCommandOfListMt_HistNewMt_Hist.getListMt_Hist().remove(listMt_HistNewMt_Hist);
                        oldCommandOfListMt_HistNewMt_Hist = em.merge(oldCommandOfListMt_HistNewMt_Hist);
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
                int id = command.getCommand_id();
                if (findCommand(id) == null) {
                    throw new NonexistentEntityException("The command with id " + id + " no longer exists.");
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
            Command command;
            try {
                command = em.getReference(Command.class, id);
                command.getCommand_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The command with id " + id + " no longer exists.", enfe);
            }
            Action action = command.getAction();
            if (action != null) {
                action.getListCommand().remove(command);
                action = em.merge(action);
            }
            Parameter parameter = command.getParameter();
            if (parameter != null) {
                parameter.getListCommand().remove(command);
                parameter = em.merge(parameter);
            }
            Set<Mo_Hist> listMo_Hist = command.getListMo_Hist();
            for (Mo_Hist listMo_HistMo_Hist : listMo_Hist) {
                listMo_HistMo_Hist.setCommand(null);
                listMo_HistMo_Hist = em.merge(listMo_HistMo_Hist);
            }
            Set<Mt_Hist> listMt_Hist = command.getListMt_Hist();
            for (Mt_Hist listMt_HistMt_Hist : listMt_Hist) {
                listMt_HistMt_Hist.setCommand(null);
                listMt_HistMt_Hist = em.merge(listMt_HistMt_Hist);
            }
            em.remove(command);
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

    public List<Command> findCommandEntities() {
        return findCommandEntities(true, -1, -1);
    }

    public List<Command> findCommandEntities(int maxResults, int firstResult) {
        return findCommandEntities(false, maxResults, firstResult);
    }

    private List<Command> findCommandEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Command.class));
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

    public Command findCommand(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Command.class, id);
        } finally {
            em.close();
        }
    }

    public int getCommandCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Command> rt = cq.from(Command.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
