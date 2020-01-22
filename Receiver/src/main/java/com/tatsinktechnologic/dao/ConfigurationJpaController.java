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
import com.tatsinktechnologic.beans_entity.Configuration;
import com.tatsinktechnologic.beans_entity.Process_Unit;
import com.tatsinktechnologic.dao.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class ConfigurationJpaController implements Serializable {

    public ConfigurationJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Configuration configuration) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Command command = configuration.getCommand();
            if (command != null) {
                command = em.getReference(command.getClass(), command.getCommand_id());
                configuration.setCommand(command);
            }
            Process_Unit process_unit = configuration.getProcess_unit();
            if (process_unit != null) {
                process_unit = em.getReference(process_unit.getClass(), process_unit.getProcess_unit_id());
                configuration.setProcess_unit(process_unit);
            }
            em.persist(configuration);
            if (command != null) {
                command.getListConfig().add(configuration);
                command = em.merge(command);
            }
            if (process_unit != null) {
                process_unit.getListConfiguration().add(configuration);
                process_unit = em.merge(process_unit);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Configuration configuration) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Configuration persistentConfiguration = em.find(Configuration.class, configuration.getCcnfiguration_id());
            Command commandOld = persistentConfiguration.getCommand();
            Command commandNew = configuration.getCommand();
            Process_Unit process_unitOld = persistentConfiguration.getProcess_unit();
            Process_Unit process_unitNew = configuration.getProcess_unit();
            if (commandNew != null) {
                commandNew = em.getReference(commandNew.getClass(), commandNew.getCommand_id());
                configuration.setCommand(commandNew);
            }
            if (process_unitNew != null) {
                process_unitNew = em.getReference(process_unitNew.getClass(), process_unitNew.getProcess_unit_id());
                configuration.setProcess_unit(process_unitNew);
            }
            configuration = em.merge(configuration);
            if (commandOld != null && !commandOld.equals(commandNew)) {
                commandOld.getListConfig().remove(configuration);
                commandOld = em.merge(commandOld);
            }
            if (commandNew != null && !commandNew.equals(commandOld)) {
                commandNew.getListConfig().add(configuration);
                commandNew = em.merge(commandNew);
            }
            if (process_unitOld != null && !process_unitOld.equals(process_unitNew)) {
                process_unitOld.getListConfiguration().remove(configuration);
                process_unitOld = em.merge(process_unitOld);
            }
            if (process_unitNew != null && !process_unitNew.equals(process_unitOld)) {
                process_unitNew.getListConfiguration().add(configuration);
                process_unitNew = em.merge(process_unitNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = configuration.getCcnfiguration_id();
                if (findConfiguration(id) == null) {
                    throw new NonexistentEntityException("The configuration with id " + id + " no longer exists.");
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
            Configuration configuration;
            try {
                configuration = em.getReference(Configuration.class, id);
                configuration.getCcnfiguration_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The configuration with id " + id + " no longer exists.", enfe);
            }
            Command command = configuration.getCommand();
            if (command != null) {
                command.getListConfig().remove(configuration);
                command = em.merge(command);
            }
            Process_Unit process_unit = configuration.getProcess_unit();
            if (process_unit != null) {
                process_unit.getListConfiguration().remove(configuration);
                process_unit = em.merge(process_unit);
            }
            em.remove(configuration);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Configuration> findConfigurationEntities() {
        return findConfigurationEntities(true, -1, -1);
    }

    public List<Configuration> findConfigurationEntities(int maxResults, int firstResult) {
        return findConfigurationEntities(false, maxResults, firstResult);
    }

    private List<Configuration> findConfigurationEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Configuration.class));
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

    public Configuration findConfiguration(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Configuration.class, id);
        } finally {
            em.close();
        }
    }

    public int getConfigurationCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Configuration> rt = cq.from(Configuration.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
