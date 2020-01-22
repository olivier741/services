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
import com.tatsinktechnologic.beans_entity.Mo_Message_His;
import java.util.HashSet;
import java.util.Set;
import com.tatsinktechnologic.beans_entity.Mt_Message_His;
import com.tatsinktechnologic.beans_entity.Charge_His;
import com.tatsinktechnologic.beans_entity.Configuration;
import com.tatsinktechnologic.beans_entity.ProcessKey;
import com.tatsinktechnologic.beans_entity.Process_Unit;
import com.tatsinktechnologic.dao.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.dao.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class Process_UnitJpaController implements Serializable {

    public Process_UnitJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Process_Unit process_Unit) throws PreexistingEntityException, Exception {
        if (process_Unit.getProcess_unit_id() == null) {
            process_Unit.setProcess_unit_id(new ProcessKey());
        }
        if (process_Unit.getListMo_Message_His() == null) {
            process_Unit.setListMo_Message_His(new HashSet<Mo_Message_His>());
        }
        if (process_Unit.getListMt_Message_his() == null) {
            process_Unit.setListMt_Message_his(new HashSet<Mt_Message_His>());
        }
        if (process_Unit.getListCharge_His() == null) {
            process_Unit.setListCharge_His(new HashSet<Charge_His>());
        }
        if (process_Unit.getListConfiguration() == null) {
            process_Unit.setListConfiguration(new HashSet<Configuration>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Set<Mo_Message_His> attachedListMo_Message_His = new HashSet<Mo_Message_His>();
            for (Mo_Message_His listMo_Message_HisMo_Message_HisToAttach : process_Unit.getListMo_Message_His()) {
                listMo_Message_HisMo_Message_HisToAttach = em.getReference(listMo_Message_HisMo_Message_HisToAttach.getClass(), listMo_Message_HisMo_Message_HisToAttach.getMo_message_his_id());
                attachedListMo_Message_His.add(listMo_Message_HisMo_Message_HisToAttach);
            }
            process_Unit.setListMo_Message_His(attachedListMo_Message_His);
            Set<Mt_Message_His> attachedListMt_Message_his = new HashSet<Mt_Message_His>();
            for (Mt_Message_His listMt_Message_hisMt_Message_HisToAttach : process_Unit.getListMt_Message_his()) {
                listMt_Message_hisMt_Message_HisToAttach = em.getReference(listMt_Message_hisMt_Message_HisToAttach.getClass(), listMt_Message_hisMt_Message_HisToAttach.getMt_message_his_id());
                attachedListMt_Message_his.add(listMt_Message_hisMt_Message_HisToAttach);
            }
            process_Unit.setListMt_Message_his(attachedListMt_Message_his);
            Set<Charge_His> attachedListCharge_His = new HashSet<Charge_His>();
            for (Charge_His listCharge_HisCharge_HisToAttach : process_Unit.getListCharge_His()) {
                listCharge_HisCharge_HisToAttach = em.getReference(listCharge_HisCharge_HisToAttach.getClass(), listCharge_HisCharge_HisToAttach.getCharge_his_id());
                attachedListCharge_His.add(listCharge_HisCharge_HisToAttach);
            }
            process_Unit.setListCharge_His(attachedListCharge_His);
            Set<Configuration> attachedListConfiguration = new HashSet<Configuration>();
            for (Configuration listConfigurationConfigurationToAttach : process_Unit.getListConfiguration()) {
                listConfigurationConfigurationToAttach = em.getReference(listConfigurationConfigurationToAttach.getClass(), listConfigurationConfigurationToAttach.getCcnfiguration_id());
                attachedListConfiguration.add(listConfigurationConfigurationToAttach);
            }
            process_Unit.setListConfiguration(attachedListConfiguration);
            em.persist(process_Unit);
            for (Mo_Message_His listMo_Message_HisMo_Message_His : process_Unit.getListMo_Message_His()) {
                Process_Unit oldProcess_unitOfListMo_Message_HisMo_Message_His = listMo_Message_HisMo_Message_His.getProcess_unit();
                listMo_Message_HisMo_Message_His.setProcess_unit(process_Unit);
                listMo_Message_HisMo_Message_His = em.merge(listMo_Message_HisMo_Message_His);
                if (oldProcess_unitOfListMo_Message_HisMo_Message_His != null) {
                    oldProcess_unitOfListMo_Message_HisMo_Message_His.getListMo_Message_His().remove(listMo_Message_HisMo_Message_His);
                    oldProcess_unitOfListMo_Message_HisMo_Message_His = em.merge(oldProcess_unitOfListMo_Message_HisMo_Message_His);
                }
            }
            for (Mt_Message_His listMt_Message_hisMt_Message_His : process_Unit.getListMt_Message_his()) {
                Process_Unit oldProcess_unitOfListMt_Message_hisMt_Message_His = listMt_Message_hisMt_Message_His.getProcess_unit();
                listMt_Message_hisMt_Message_His.setProcess_unit(process_Unit);
                listMt_Message_hisMt_Message_His = em.merge(listMt_Message_hisMt_Message_His);
                if (oldProcess_unitOfListMt_Message_hisMt_Message_His != null) {
                    oldProcess_unitOfListMt_Message_hisMt_Message_His.getListMt_Message_his().remove(listMt_Message_hisMt_Message_His);
                    oldProcess_unitOfListMt_Message_hisMt_Message_His = em.merge(oldProcess_unitOfListMt_Message_hisMt_Message_His);
                }
            }
            for (Charge_His listCharge_HisCharge_His : process_Unit.getListCharge_His()) {
                Process_Unit oldProcess_unitOfListCharge_HisCharge_His = listCharge_HisCharge_His.getProcess_unit();
                listCharge_HisCharge_His.setProcess_unit(process_Unit);
                listCharge_HisCharge_His = em.merge(listCharge_HisCharge_His);
                if (oldProcess_unitOfListCharge_HisCharge_His != null) {
                    oldProcess_unitOfListCharge_HisCharge_His.getListCharge_His().remove(listCharge_HisCharge_His);
                    oldProcess_unitOfListCharge_HisCharge_His = em.merge(oldProcess_unitOfListCharge_HisCharge_His);
                }
            }
            for (Configuration listConfigurationConfiguration : process_Unit.getListConfiguration()) {
                Process_Unit oldProcess_unitOfListConfigurationConfiguration = listConfigurationConfiguration.getProcess_unit();
                listConfigurationConfiguration.setProcess_unit(process_Unit);
                listConfigurationConfiguration = em.merge(listConfigurationConfiguration);
                if (oldProcess_unitOfListConfigurationConfiguration != null) {
                    oldProcess_unitOfListConfigurationConfiguration.getListConfiguration().remove(listConfigurationConfiguration);
                    oldProcess_unitOfListConfigurationConfiguration = em.merge(oldProcess_unitOfListConfigurationConfiguration);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProcess_Unit(process_Unit.getProcess_unit_id()) != null) {
                throw new PreexistingEntityException("Process_Unit " + process_Unit + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Process_Unit process_Unit) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Process_Unit persistentProcess_Unit = em.find(Process_Unit.class, process_Unit.getProcess_unit_id());
            Set<Mo_Message_His> listMo_Message_HisOld = persistentProcess_Unit.getListMo_Message_His();
            Set<Mo_Message_His> listMo_Message_HisNew = process_Unit.getListMo_Message_His();
            Set<Mt_Message_His> listMt_Message_hisOld = persistentProcess_Unit.getListMt_Message_his();
            Set<Mt_Message_His> listMt_Message_hisNew = process_Unit.getListMt_Message_his();
            Set<Charge_His> listCharge_HisOld = persistentProcess_Unit.getListCharge_His();
            Set<Charge_His> listCharge_HisNew = process_Unit.getListCharge_His();
            Set<Configuration> listConfigurationOld = persistentProcess_Unit.getListConfiguration();
            Set<Configuration> listConfigurationNew = process_Unit.getListConfiguration();
            Set<Mo_Message_His> attachedListMo_Message_HisNew = new HashSet<Mo_Message_His>();
            for (Mo_Message_His listMo_Message_HisNewMo_Message_HisToAttach : listMo_Message_HisNew) {
                listMo_Message_HisNewMo_Message_HisToAttach = em.getReference(listMo_Message_HisNewMo_Message_HisToAttach.getClass(), listMo_Message_HisNewMo_Message_HisToAttach.getMo_message_his_id());
                attachedListMo_Message_HisNew.add(listMo_Message_HisNewMo_Message_HisToAttach);
            }
            listMo_Message_HisNew = attachedListMo_Message_HisNew;
            process_Unit.setListMo_Message_His(listMo_Message_HisNew);
            Set<Mt_Message_His> attachedListMt_Message_hisNew = new HashSet<Mt_Message_His>();
            for (Mt_Message_His listMt_Message_hisNewMt_Message_HisToAttach : listMt_Message_hisNew) {
                listMt_Message_hisNewMt_Message_HisToAttach = em.getReference(listMt_Message_hisNewMt_Message_HisToAttach.getClass(), listMt_Message_hisNewMt_Message_HisToAttach.getMt_message_his_id());
                attachedListMt_Message_hisNew.add(listMt_Message_hisNewMt_Message_HisToAttach);
            }
            listMt_Message_hisNew = attachedListMt_Message_hisNew;
            process_Unit.setListMt_Message_his(listMt_Message_hisNew);
            Set<Charge_His> attachedListCharge_HisNew = new HashSet<Charge_His>();
            for (Charge_His listCharge_HisNewCharge_HisToAttach : listCharge_HisNew) {
                listCharge_HisNewCharge_HisToAttach = em.getReference(listCharge_HisNewCharge_HisToAttach.getClass(), listCharge_HisNewCharge_HisToAttach.getCharge_his_id());
                attachedListCharge_HisNew.add(listCharge_HisNewCharge_HisToAttach);
            }
            listCharge_HisNew = attachedListCharge_HisNew;
            process_Unit.setListCharge_His(listCharge_HisNew);
            Set<Configuration> attachedListConfigurationNew = new HashSet<Configuration>();
            for (Configuration listConfigurationNewConfigurationToAttach : listConfigurationNew) {
                listConfigurationNewConfigurationToAttach = em.getReference(listConfigurationNewConfigurationToAttach.getClass(), listConfigurationNewConfigurationToAttach.getCcnfiguration_id());
                attachedListConfigurationNew.add(listConfigurationNewConfigurationToAttach);
            }
            listConfigurationNew = attachedListConfigurationNew;
            process_Unit.setListConfiguration(listConfigurationNew);
            process_Unit = em.merge(process_Unit);
            for (Mo_Message_His listMo_Message_HisOldMo_Message_His : listMo_Message_HisOld) {
                if (!listMo_Message_HisNew.contains(listMo_Message_HisOldMo_Message_His)) {
                    listMo_Message_HisOldMo_Message_His.setProcess_unit(null);
                    listMo_Message_HisOldMo_Message_His = em.merge(listMo_Message_HisOldMo_Message_His);
                }
            }
            for (Mo_Message_His listMo_Message_HisNewMo_Message_His : listMo_Message_HisNew) {
                if (!listMo_Message_HisOld.contains(listMo_Message_HisNewMo_Message_His)) {
                    Process_Unit oldProcess_unitOfListMo_Message_HisNewMo_Message_His = listMo_Message_HisNewMo_Message_His.getProcess_unit();
                    listMo_Message_HisNewMo_Message_His.setProcess_unit(process_Unit);
                    listMo_Message_HisNewMo_Message_His = em.merge(listMo_Message_HisNewMo_Message_His);
                    if (oldProcess_unitOfListMo_Message_HisNewMo_Message_His != null && !oldProcess_unitOfListMo_Message_HisNewMo_Message_His.equals(process_Unit)) {
                        oldProcess_unitOfListMo_Message_HisNewMo_Message_His.getListMo_Message_His().remove(listMo_Message_HisNewMo_Message_His);
                        oldProcess_unitOfListMo_Message_HisNewMo_Message_His = em.merge(oldProcess_unitOfListMo_Message_HisNewMo_Message_His);
                    }
                }
            }
            for (Mt_Message_His listMt_Message_hisOldMt_Message_His : listMt_Message_hisOld) {
                if (!listMt_Message_hisNew.contains(listMt_Message_hisOldMt_Message_His)) {
                    listMt_Message_hisOldMt_Message_His.setProcess_unit(null);
                    listMt_Message_hisOldMt_Message_His = em.merge(listMt_Message_hisOldMt_Message_His);
                }
            }
            for (Mt_Message_His listMt_Message_hisNewMt_Message_His : listMt_Message_hisNew) {
                if (!listMt_Message_hisOld.contains(listMt_Message_hisNewMt_Message_His)) {
                    Process_Unit oldProcess_unitOfListMt_Message_hisNewMt_Message_His = listMt_Message_hisNewMt_Message_His.getProcess_unit();
                    listMt_Message_hisNewMt_Message_His.setProcess_unit(process_Unit);
                    listMt_Message_hisNewMt_Message_His = em.merge(listMt_Message_hisNewMt_Message_His);
                    if (oldProcess_unitOfListMt_Message_hisNewMt_Message_His != null && !oldProcess_unitOfListMt_Message_hisNewMt_Message_His.equals(process_Unit)) {
                        oldProcess_unitOfListMt_Message_hisNewMt_Message_His.getListMt_Message_his().remove(listMt_Message_hisNewMt_Message_His);
                        oldProcess_unitOfListMt_Message_hisNewMt_Message_His = em.merge(oldProcess_unitOfListMt_Message_hisNewMt_Message_His);
                    }
                }
            }
            for (Charge_His listCharge_HisOldCharge_His : listCharge_HisOld) {
                if (!listCharge_HisNew.contains(listCharge_HisOldCharge_His)) {
                    listCharge_HisOldCharge_His.setProcess_unit(null);
                    listCharge_HisOldCharge_His = em.merge(listCharge_HisOldCharge_His);
                }
            }
            for (Charge_His listCharge_HisNewCharge_His : listCharge_HisNew) {
                if (!listCharge_HisOld.contains(listCharge_HisNewCharge_His)) {
                    Process_Unit oldProcess_unitOfListCharge_HisNewCharge_His = listCharge_HisNewCharge_His.getProcess_unit();
                    listCharge_HisNewCharge_His.setProcess_unit(process_Unit);
                    listCharge_HisNewCharge_His = em.merge(listCharge_HisNewCharge_His);
                    if (oldProcess_unitOfListCharge_HisNewCharge_His != null && !oldProcess_unitOfListCharge_HisNewCharge_His.equals(process_Unit)) {
                        oldProcess_unitOfListCharge_HisNewCharge_His.getListCharge_His().remove(listCharge_HisNewCharge_His);
                        oldProcess_unitOfListCharge_HisNewCharge_His = em.merge(oldProcess_unitOfListCharge_HisNewCharge_His);
                    }
                }
            }
            for (Configuration listConfigurationOldConfiguration : listConfigurationOld) {
                if (!listConfigurationNew.contains(listConfigurationOldConfiguration)) {
                    listConfigurationOldConfiguration.setProcess_unit(null);
                    listConfigurationOldConfiguration = em.merge(listConfigurationOldConfiguration);
                }
            }
            for (Configuration listConfigurationNewConfiguration : listConfigurationNew) {
                if (!listConfigurationOld.contains(listConfigurationNewConfiguration)) {
                    Process_Unit oldProcess_unitOfListConfigurationNewConfiguration = listConfigurationNewConfiguration.getProcess_unit();
                    listConfigurationNewConfiguration.setProcess_unit(process_Unit);
                    listConfigurationNewConfiguration = em.merge(listConfigurationNewConfiguration);
                    if (oldProcess_unitOfListConfigurationNewConfiguration != null && !oldProcess_unitOfListConfigurationNewConfiguration.equals(process_Unit)) {
                        oldProcess_unitOfListConfigurationNewConfiguration.getListConfiguration().remove(listConfigurationNewConfiguration);
                        oldProcess_unitOfListConfigurationNewConfiguration = em.merge(oldProcess_unitOfListConfigurationNewConfiguration);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ProcessKey id = process_Unit.getProcess_unit_id();
                if (findProcess_Unit(id) == null) {
                    throw new NonexistentEntityException("The process_Unit with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ProcessKey id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Process_Unit process_Unit;
            try {
                process_Unit = em.getReference(Process_Unit.class, id);
                process_Unit.getProcess_unit_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The process_Unit with id " + id + " no longer exists.", enfe);
            }
            Set<Mo_Message_His> listMo_Message_His = process_Unit.getListMo_Message_His();
            for (Mo_Message_His listMo_Message_HisMo_Message_His : listMo_Message_His) {
                listMo_Message_HisMo_Message_His.setProcess_unit(null);
                listMo_Message_HisMo_Message_His = em.merge(listMo_Message_HisMo_Message_His);
            }
            Set<Mt_Message_His> listMt_Message_his = process_Unit.getListMt_Message_his();
            for (Mt_Message_His listMt_Message_hisMt_Message_His : listMt_Message_his) {
                listMt_Message_hisMt_Message_His.setProcess_unit(null);
                listMt_Message_hisMt_Message_His = em.merge(listMt_Message_hisMt_Message_His);
            }
            Set<Charge_His> listCharge_His = process_Unit.getListCharge_His();
            for (Charge_His listCharge_HisCharge_His : listCharge_His) {
                listCharge_HisCharge_His.setProcess_unit(null);
                listCharge_HisCharge_His = em.merge(listCharge_HisCharge_His);
            }
            Set<Configuration> listConfiguration = process_Unit.getListConfiguration();
            for (Configuration listConfigurationConfiguration : listConfiguration) {
                listConfigurationConfiguration.setProcess_unit(null);
                listConfigurationConfiguration = em.merge(listConfigurationConfiguration);
            }
            em.remove(process_Unit);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Process_Unit> findProcess_UnitEntities() {
        return findProcess_UnitEntities(true, -1, -1);
    }

    public List<Process_Unit> findProcess_UnitEntities(int maxResults, int firstResult) {
        return findProcess_UnitEntities(false, maxResults, firstResult);
    }

    private List<Process_Unit> findProcess_UnitEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Process_Unit.class));
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

    public Process_Unit findProcess_Unit(ProcessKey id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Process_Unit.class, id);
        } finally {
            em.close();
        }
    }

    public int getProcess_UnitCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Process_Unit> rt = cq.from(Process_Unit.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
