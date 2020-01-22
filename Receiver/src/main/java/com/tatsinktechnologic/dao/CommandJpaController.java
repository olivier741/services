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
import com.tatsinktechnologic.beans_entity.Action;
import com.tatsinktechnologic.beans_entity.Command;
import com.tatsinktechnologic.beans_entity.Configuration;
import java.util.HashSet;
import java.util.Set;
import com.tatsinktechnologic.beans_entity.Mo_Message;
import com.tatsinktechnologic.beans_entity.Mt_Message;
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
            command.setListConfig(new HashSet<Configuration>());
        }
        if (command.getListMo_Message() == null) {
            command.setListMo_Message(new HashSet<Mo_Message>());
        }
        if (command.getListMt_Message() == null) {
            command.setListMt_Message(new HashSet<Mt_Message>());
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
            Set<Configuration> attachedListConfig = new HashSet<Configuration>();
            for (Configuration listConfigConfigurationToAttach : command.getListConfig()) {
                listConfigConfigurationToAttach = em.getReference(listConfigConfigurationToAttach.getClass(), listConfigConfigurationToAttach.getCcnfiguration_id());
                attachedListConfig.add(listConfigConfigurationToAttach);
            }
            command.setListConfig(attachedListConfig);
            Set<Mo_Message> attachedListMo_Message = new HashSet<Mo_Message>();
            for (Mo_Message listMo_MessageMo_MessageToAttach : command.getListMo_Message()) {
                listMo_MessageMo_MessageToAttach = em.getReference(listMo_MessageMo_MessageToAttach.getClass(), listMo_MessageMo_MessageToAttach.getMo_message_id());
                attachedListMo_Message.add(listMo_MessageMo_MessageToAttach);
            }
            command.setListMo_Message(attachedListMo_Message);
            Set<Mt_Message> attachedListMt_Message = new HashSet<Mt_Message>();
            for (Mt_Message listMt_MessageMt_MessageToAttach : command.getListMt_Message()) {
                listMt_MessageMt_MessageToAttach = em.getReference(listMt_MessageMt_MessageToAttach.getClass(), listMt_MessageMt_MessageToAttach.getMt_message_id());
                attachedListMt_Message.add(listMt_MessageMt_MessageToAttach);
            }
            command.setListMt_Message(attachedListMt_Message);
            em.persist(command);
            if (action != null) {
                action.getListCommand().add(command);
                action = em.merge(action);
            }
            for (Configuration listConfigConfiguration : command.getListConfig()) {
                Command oldCommandOfListConfigConfiguration = listConfigConfiguration.getCommand();
                listConfigConfiguration.setCommand(command);
                listConfigConfiguration = em.merge(listConfigConfiguration);
                if (oldCommandOfListConfigConfiguration != null) {
                    oldCommandOfListConfigConfiguration.getListConfig().remove(listConfigConfiguration);
                    oldCommandOfListConfigConfiguration = em.merge(oldCommandOfListConfigConfiguration);
                }
            }
            for (Mo_Message listMo_MessageMo_Message : command.getListMo_Message()) {
                Command oldCommandOfListMo_MessageMo_Message = listMo_MessageMo_Message.getCommand();
                listMo_MessageMo_Message.setCommand(command);
                listMo_MessageMo_Message = em.merge(listMo_MessageMo_Message);
                if (oldCommandOfListMo_MessageMo_Message != null) {
                    oldCommandOfListMo_MessageMo_Message.getListMo_Message().remove(listMo_MessageMo_Message);
                    oldCommandOfListMo_MessageMo_Message = em.merge(oldCommandOfListMo_MessageMo_Message);
                }
            }
            for (Mt_Message listMt_MessageMt_Message : command.getListMt_Message()) {
                Command oldCommandOfListMt_MessageMt_Message = listMt_MessageMt_Message.getCommand();
                listMt_MessageMt_Message.setCommand(command);
                listMt_MessageMt_Message = em.merge(listMt_MessageMt_Message);
                if (oldCommandOfListMt_MessageMt_Message != null) {
                    oldCommandOfListMt_MessageMt_Message.getListMt_Message().remove(listMt_MessageMt_Message);
                    oldCommandOfListMt_MessageMt_Message = em.merge(oldCommandOfListMt_MessageMt_Message);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Command command) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Command persistentCommand = em.find(Command.class, command.getCommand_id());
            Action actionOld = persistentCommand.getAction();
            Action actionNew = command.getAction();
            Set<Configuration> listConfigOld = persistentCommand.getListConfig();
            Set<Configuration> listConfigNew = command.getListConfig();
            Set<Mo_Message> listMo_MessageOld = persistentCommand.getListMo_Message();
            Set<Mo_Message> listMo_MessageNew = command.getListMo_Message();
            Set<Mt_Message> listMt_MessageOld = persistentCommand.getListMt_Message();
            Set<Mt_Message> listMt_MessageNew = command.getListMt_Message();
            List<String> illegalOrphanMessages = null;
            for (Mo_Message listMo_MessageOldMo_Message : listMo_MessageOld) {
                if (!listMo_MessageNew.contains(listMo_MessageOldMo_Message)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Mo_Message " + listMo_MessageOldMo_Message + " since its command field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (actionNew != null) {
                actionNew = em.getReference(actionNew.getClass(), actionNew.getAction_id());
                command.setAction(actionNew);
            }
            Set<Configuration> attachedListConfigNew = new HashSet<Configuration>();
            for (Configuration listConfigNewConfigurationToAttach : listConfigNew) {
                listConfigNewConfigurationToAttach = em.getReference(listConfigNewConfigurationToAttach.getClass(), listConfigNewConfigurationToAttach.getCcnfiguration_id());
                attachedListConfigNew.add(listConfigNewConfigurationToAttach);
            }
            listConfigNew = attachedListConfigNew;
            command.setListConfig(listConfigNew);
            Set<Mo_Message> attachedListMo_MessageNew = new HashSet<Mo_Message>();
            for (Mo_Message listMo_MessageNewMo_MessageToAttach : listMo_MessageNew) {
                listMo_MessageNewMo_MessageToAttach = em.getReference(listMo_MessageNewMo_MessageToAttach.getClass(), listMo_MessageNewMo_MessageToAttach.getMo_message_id());
                attachedListMo_MessageNew.add(listMo_MessageNewMo_MessageToAttach);
            }
            listMo_MessageNew = attachedListMo_MessageNew;
            command.setListMo_Message(listMo_MessageNew);
            Set<Mt_Message> attachedListMt_MessageNew = new HashSet<Mt_Message>();
            for (Mt_Message listMt_MessageNewMt_MessageToAttach : listMt_MessageNew) {
                listMt_MessageNewMt_MessageToAttach = em.getReference(listMt_MessageNewMt_MessageToAttach.getClass(), listMt_MessageNewMt_MessageToAttach.getMt_message_id());
                attachedListMt_MessageNew.add(listMt_MessageNewMt_MessageToAttach);
            }
            listMt_MessageNew = attachedListMt_MessageNew;
            command.setListMt_Message(listMt_MessageNew);
            command = em.merge(command);
            if (actionOld != null && !actionOld.equals(actionNew)) {
                actionOld.getListCommand().remove(command);
                actionOld = em.merge(actionOld);
            }
            if (actionNew != null && !actionNew.equals(actionOld)) {
                actionNew.getListCommand().add(command);
                actionNew = em.merge(actionNew);
            }
            for (Configuration listConfigOldConfiguration : listConfigOld) {
                if (!listConfigNew.contains(listConfigOldConfiguration)) {
                    listConfigOldConfiguration.setCommand(null);
                    listConfigOldConfiguration = em.merge(listConfigOldConfiguration);
                }
            }
            for (Configuration listConfigNewConfiguration : listConfigNew) {
                if (!listConfigOld.contains(listConfigNewConfiguration)) {
                    Command oldCommandOfListConfigNewConfiguration = listConfigNewConfiguration.getCommand();
                    listConfigNewConfiguration.setCommand(command);
                    listConfigNewConfiguration = em.merge(listConfigNewConfiguration);
                    if (oldCommandOfListConfigNewConfiguration != null && !oldCommandOfListConfigNewConfiguration.equals(command)) {
                        oldCommandOfListConfigNewConfiguration.getListConfig().remove(listConfigNewConfiguration);
                        oldCommandOfListConfigNewConfiguration = em.merge(oldCommandOfListConfigNewConfiguration);
                    }
                }
            }
            for (Mo_Message listMo_MessageNewMo_Message : listMo_MessageNew) {
                if (!listMo_MessageOld.contains(listMo_MessageNewMo_Message)) {
                    Command oldCommandOfListMo_MessageNewMo_Message = listMo_MessageNewMo_Message.getCommand();
                    listMo_MessageNewMo_Message.setCommand(command);
                    listMo_MessageNewMo_Message = em.merge(listMo_MessageNewMo_Message);
                    if (oldCommandOfListMo_MessageNewMo_Message != null && !oldCommandOfListMo_MessageNewMo_Message.equals(command)) {
                        oldCommandOfListMo_MessageNewMo_Message.getListMo_Message().remove(listMo_MessageNewMo_Message);
                        oldCommandOfListMo_MessageNewMo_Message = em.merge(oldCommandOfListMo_MessageNewMo_Message);
                    }
                }
            }
            for (Mt_Message listMt_MessageOldMt_Message : listMt_MessageOld) {
                if (!listMt_MessageNew.contains(listMt_MessageOldMt_Message)) {
                    listMt_MessageOldMt_Message.setCommand(null);
                    listMt_MessageOldMt_Message = em.merge(listMt_MessageOldMt_Message);
                }
            }
            for (Mt_Message listMt_MessageNewMt_Message : listMt_MessageNew) {
                if (!listMt_MessageOld.contains(listMt_MessageNewMt_Message)) {
                    Command oldCommandOfListMt_MessageNewMt_Message = listMt_MessageNewMt_Message.getCommand();
                    listMt_MessageNewMt_Message.setCommand(command);
                    listMt_MessageNewMt_Message = em.merge(listMt_MessageNewMt_Message);
                    if (oldCommandOfListMt_MessageNewMt_Message != null && !oldCommandOfListMt_MessageNewMt_Message.equals(command)) {
                        oldCommandOfListMt_MessageNewMt_Message.getListMt_Message().remove(listMt_MessageNewMt_Message);
                        oldCommandOfListMt_MessageNewMt_Message = em.merge(oldCommandOfListMt_MessageNewMt_Message);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = command.getCommand_id();
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

    public void destroy(Long id) throws IllegalOrphanException, NonexistentEntityException {
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
            List<String> illegalOrphanMessages = null;
            Set<Mo_Message> listMo_MessageOrphanCheck = command.getListMo_Message();
            for (Mo_Message listMo_MessageOrphanCheckMo_Message : listMo_MessageOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Command (" + command + ") cannot be destroyed since the Mo_Message " + listMo_MessageOrphanCheckMo_Message + " in its listMo_Message field has a non-nullable command field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Action action = command.getAction();
            if (action != null) {
                action.getListCommand().remove(command);
                action = em.merge(action);
            }
            Set<Configuration> listConfig = command.getListConfig();
            for (Configuration listConfigConfiguration : listConfig) {
                listConfigConfiguration.setCommand(null);
                listConfigConfiguration = em.merge(listConfigConfiguration);
            }
            Set<Mt_Message> listMt_Message = command.getListMt_Message();
            for (Mt_Message listMt_MessageMt_Message : listMt_Message) {
                listMt_MessageMt_Message.setCommand(null);
                listMt_MessageMt_Message = em.merge(listMt_MessageMt_Message);
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

    public Command findCommand(Long id) {
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
