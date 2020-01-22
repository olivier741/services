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
import com.tatsinktechnologic.beans_entity.Mo_Message;
import com.tatsinktechnologic.dao.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class Mo_MessageJpaController implements Serializable {

    public Mo_MessageJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Mo_Message mo_Message) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Command command = mo_Message.getCommand();
            if (command != null) {
                command = em.getReference(command.getClass(), command.getCommand_id());
                mo_Message.setCommand(command);
            }
            em.persist(mo_Message);
            if (command != null) {
                command.getListMo_Message().add(mo_Message);
                command = em.merge(command);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Mo_Message mo_Message) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Mo_Message persistentMo_Message = em.find(Mo_Message.class, mo_Message.getMo_message_id());
            Command commandOld = persistentMo_Message.getCommand();
            Command commandNew = mo_Message.getCommand();
            if (commandNew != null) {
                commandNew = em.getReference(commandNew.getClass(), commandNew.getCommand_id());
                mo_Message.setCommand(commandNew);
            }
            mo_Message = em.merge(mo_Message);
            if (commandOld != null && !commandOld.equals(commandNew)) {
                commandOld.getListMo_Message().remove(mo_Message);
                commandOld = em.merge(commandOld);
            }
            if (commandNew != null && !commandNew.equals(commandOld)) {
                commandNew.getListMo_Message().add(mo_Message);
                commandNew = em.merge(commandNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = mo_Message.getMo_message_id();
                if (findMo_Message(id) == null) {
                    throw new NonexistentEntityException("The mo_Message with id " + id + " no longer exists.");
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
            Mo_Message mo_Message;
            try {
                mo_Message = em.getReference(Mo_Message.class, id);
                mo_Message.getMo_message_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The mo_Message with id " + id + " no longer exists.", enfe);
            }
            Command command = mo_Message.getCommand();
            if (command != null) {
                command.getListMo_Message().remove(mo_Message);
                command = em.merge(command);
            }
            em.remove(mo_Message);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Mo_Message> findMo_MessageEntities() {
        return findMo_MessageEntities(true, -1, -1);
    }

    public List<Mo_Message> findMo_MessageEntities(int maxResults, int firstResult) {
        return findMo_MessageEntities(false, maxResults, firstResult);
    }

    private List<Mo_Message> findMo_MessageEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Mo_Message.class));
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

    public Mo_Message findMo_Message(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Mo_Message.class, id);
        } finally {
            em.close();
        }
    }

    public int getMo_MessageCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Mo_Message> rt = cq.from(Mo_Message.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
