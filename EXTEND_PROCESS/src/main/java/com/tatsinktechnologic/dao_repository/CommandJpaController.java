/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_repository;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.beans_entity.Action;
import com.tatsinktechnologic.beans_entity.Command;
import com.tatsinktechnologic.beans_entity.Parameter;
import com.tatsinktechnologic.beans_entity.Notification_Conf;
import java.util.HashSet;
import java.util.Set;
import com.tatsinktechnologic.beans_entity.Mt_Hist;
import com.tatsinktechnologic.dao_repository.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class CommandJpaController implements Serializable {

    public CommandJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Command command) {
        if (command.getListConfig() == null) {
            command.setListConfig(new HashSet<Notification_Conf>());
        }
        if (command.getListMt_Hist() == null) {
            command.setListMt_Hist(new HashSet<Mt_Hist>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
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
            Set<Notification_Conf> attachedListConfig = new HashSet<Notification_Conf>();
            for (Notification_Conf listConfigNotification_ConfToAttach : command.getListConfig()) {
                listConfigNotification_ConfToAttach = em.getReference(listConfigNotification_ConfToAttach.getClass(), listConfigNotification_ConfToAttach.getConfig_id());
                attachedListConfig.add(listConfigNotification_ConfToAttach);
            }
            command.setListConfig(attachedListConfig);
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
            for (Notification_Conf listConfigNotification_Conf : command.getListConfig()) {
                Command oldCommandOfListConfigNotification_Conf = listConfigNotification_Conf.getCommand();
                listConfigNotification_Conf.setCommand(command);
                listConfigNotification_Conf = em.merge(listConfigNotification_Conf);
                if (oldCommandOfListConfigNotification_Conf != null) {
                    oldCommandOfListConfigNotification_Conf.getListConfig().remove(listConfigNotification_Conf);
                    oldCommandOfListConfigNotification_Conf = em.merge(oldCommandOfListConfigNotification_Conf);
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
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Command command) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Command persistentCommand = em.find(Command.class, command.getCommand_id());
            Action actionOld = persistentCommand.getAction();
            Action actionNew = command.getAction();
            Parameter parameterOld = persistentCommand.getParameter();
            Parameter parameterNew = command.getParameter();
            Set<Notification_Conf> listConfigOld = persistentCommand.getListConfig();
            Set<Notification_Conf> listConfigNew = command.getListConfig();
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
            Set<Notification_Conf> attachedListConfigNew = new HashSet<Notification_Conf>();
            for (Notification_Conf listConfigNewNotification_ConfToAttach : listConfigNew) {
                listConfigNewNotification_ConfToAttach = em.getReference(listConfigNewNotification_ConfToAttach.getClass(), listConfigNewNotification_ConfToAttach.getConfig_id());
                attachedListConfigNew.add(listConfigNewNotification_ConfToAttach);
            }
            listConfigNew = attachedListConfigNew;
            command.setListConfig(listConfigNew);
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
            for (Notification_Conf listConfigOldNotification_Conf : listConfigOld) {
                if (!listConfigNew.contains(listConfigOldNotification_Conf)) {
                    listConfigOldNotification_Conf.setCommand(null);
                    listConfigOldNotification_Conf = em.merge(listConfigOldNotification_Conf);
                }
            }
            for (Notification_Conf listConfigNewNotification_Conf : listConfigNew) {
                if (!listConfigOld.contains(listConfigNewNotification_Conf)) {
                    Command oldCommandOfListConfigNewNotification_Conf = listConfigNewNotification_Conf.getCommand();
                    listConfigNewNotification_Conf.setCommand(command);
                    listConfigNewNotification_Conf = em.merge(listConfigNewNotification_Conf);
                    if (oldCommandOfListConfigNewNotification_Conf != null && !oldCommandOfListConfigNewNotification_Conf.equals(command)) {
                        oldCommandOfListConfigNewNotification_Conf.getListConfig().remove(listConfigNewNotification_Conf);
                        oldCommandOfListConfigNewNotification_Conf = em.merge(oldCommandOfListConfigNewNotification_Conf);
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
            em.getTransaction().commit();
        } catch (Exception ex) {
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

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
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
            Set<Notification_Conf> listConfig = command.getListConfig();
            for (Notification_Conf listConfigNotification_Conf : listConfig) {
                listConfigNotification_Conf.setCommand(null);
                listConfigNotification_Conf = em.merge(listConfigNotification_Conf);
            }
            Set<Mt_Hist> listMt_Hist = command.getListMt_Hist();
            for (Mt_Hist listMt_HistMt_Hist : listMt_Hist) {
                listMt_HistMt_Hist.setCommand(null);
                listMt_HistMt_Hist = em.merge(listMt_HistMt_Hist);
            }
            em.remove(command);
            em.getTransaction().commit();
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
