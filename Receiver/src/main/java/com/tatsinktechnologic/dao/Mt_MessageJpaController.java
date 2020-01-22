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
import com.tatsinktechnologic.beans_entity.Command;
import com.tatsinktechnologic.beans_entity.Mt_Message;
import com.tatsinktechnologic.dao.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class Mt_MessageJpaController implements Serializable {

    public Mt_MessageJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Mt_Message mt_Message) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Command command = mt_Message.getCommand();
            if (command != null) {
                command = em.getReference(command.getClass(), command.getCommand_id());
                mt_Message.setCommand(command);
            }
            em.persist(mt_Message);
            if (command != null) {
                command.getListMt_Message().add(mt_Message);
                command = em.merge(command);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Mt_Message mt_Message) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Mt_Message persistentMt_Message = em.find(Mt_Message.class, mt_Message.getMt_message_id());
            Command commandOld = persistentMt_Message.getCommand();
            Command commandNew = mt_Message.getCommand();
            if (commandNew != null) {
                commandNew = em.getReference(commandNew.getClass(), commandNew.getCommand_id());
                mt_Message.setCommand(commandNew);
            }
            mt_Message = em.merge(mt_Message);
            if (commandOld != null && !commandOld.equals(commandNew)) {
                commandOld.getListMt_Message().remove(mt_Message);
                commandOld = em.merge(commandOld);
            }
            if (commandNew != null && !commandNew.equals(commandOld)) {
                commandNew.getListMt_Message().add(mt_Message);
                commandNew = em.merge(commandNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = mt_Message.getMt_message_id();
                if (findMt_Message(id) == null) {
                    throw new NonexistentEntityException("The mt_Message with id " + id + " no longer exists.");
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
            Mt_Message mt_Message;
            try {
                mt_Message = em.getReference(Mt_Message.class, id);
                mt_Message.getMt_message_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The mt_Message with id " + id + " no longer exists.", enfe);
            }
            Command command = mt_Message.getCommand();
            if (command != null) {
                command.getListMt_Message().remove(mt_Message);
                command = em.merge(command);
            }
            em.remove(mt_Message);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Mt_Message> findMt_MessageEntities() {
        return findMt_MessageEntities(true, -1, -1);
    }

    public List<Mt_Message> findMt_MessageEntities(int maxResults, int firstResult) {
        return findMt_MessageEntities(false, maxResults, firstResult);
    }

    private List<Mt_Message> findMt_MessageEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Mt_Message.class));
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

    public Mt_Message findMt_Message(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Mt_Message.class, id);
        } finally {
            em.close();
        }
    }

    public int getMt_MessageCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Mt_Message> rt = cq.from(Mt_Message.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
