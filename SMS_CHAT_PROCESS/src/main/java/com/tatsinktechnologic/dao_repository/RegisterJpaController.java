/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_repository;

import com.tatsinktechnologic.dao_repository.exceptions.IllegalOrphanException;
import com.tatsinktechnologic.dao_repository.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.entity.register.Register;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entity.sms_chat.ChatGroup_Register;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class RegisterJpaController implements Serializable {

    public RegisterJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Register register) {
        if (register.getListChatGroup_Register() == null) {
            register.setListChatGroup_Register(new HashSet<ChatGroup_Register>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Set<ChatGroup_Register> attachedListChatGroup_Register = new HashSet<ChatGroup_Register>();
            for (ChatGroup_Register listChatGroup_RegisterChatGroup_RegisterToAttach : register.getListChatGroup_Register()) {
                listChatGroup_RegisterChatGroup_RegisterToAttach = em.getReference(listChatGroup_RegisterChatGroup_RegisterToAttach.getClass(), listChatGroup_RegisterChatGroup_RegisterToAttach.getChatgroup_reg_id());
                attachedListChatGroup_Register.add(listChatGroup_RegisterChatGroup_RegisterToAttach);
            }
            register.setListChatGroup_Register(attachedListChatGroup_Register);
            em.persist(register);
            for (ChatGroup_Register listChatGroup_RegisterChatGroup_Register : register.getListChatGroup_Register()) {
                Register oldRegisterOfListChatGroup_RegisterChatGroup_Register = listChatGroup_RegisterChatGroup_Register.getRegister();
                listChatGroup_RegisterChatGroup_Register.setRegister(register);
                listChatGroup_RegisterChatGroup_Register = em.merge(listChatGroup_RegisterChatGroup_Register);
                if (oldRegisterOfListChatGroup_RegisterChatGroup_Register != null) {
                    oldRegisterOfListChatGroup_RegisterChatGroup_Register.getListChatGroup_Register().remove(listChatGroup_RegisterChatGroup_Register);
                    oldRegisterOfListChatGroup_RegisterChatGroup_Register = em.merge(oldRegisterOfListChatGroup_RegisterChatGroup_Register);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Register register) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Register persistentRegister = em.find(Register.class, register.getRegister_id());
            Set<ChatGroup_Register> listChatGroup_RegisterOld = persistentRegister.getListChatGroup_Register();
            Set<ChatGroup_Register> listChatGroup_RegisterNew = register.getListChatGroup_Register();
            List<String> illegalOrphanMessages = null;
            for (ChatGroup_Register listChatGroup_RegisterOldChatGroup_Register : listChatGroup_RegisterOld) {
                if (!listChatGroup_RegisterNew.contains(listChatGroup_RegisterOldChatGroup_Register)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ChatGroup_Register " + listChatGroup_RegisterOldChatGroup_Register + " since its register field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Set<ChatGroup_Register> attachedListChatGroup_RegisterNew = new HashSet<ChatGroup_Register>();
            for (ChatGroup_Register listChatGroup_RegisterNewChatGroup_RegisterToAttach : listChatGroup_RegisterNew) {
                listChatGroup_RegisterNewChatGroup_RegisterToAttach = em.getReference(listChatGroup_RegisterNewChatGroup_RegisterToAttach.getClass(), listChatGroup_RegisterNewChatGroup_RegisterToAttach.getChatgroup_reg_id());
                attachedListChatGroup_RegisterNew.add(listChatGroup_RegisterNewChatGroup_RegisterToAttach);
            }
            listChatGroup_RegisterNew = attachedListChatGroup_RegisterNew;
            register.setListChatGroup_Register(listChatGroup_RegisterNew);
            register = em.merge(register);
            for (ChatGroup_Register listChatGroup_RegisterNewChatGroup_Register : listChatGroup_RegisterNew) {
                if (!listChatGroup_RegisterOld.contains(listChatGroup_RegisterNewChatGroup_Register)) {
                    Register oldRegisterOfListChatGroup_RegisterNewChatGroup_Register = listChatGroup_RegisterNewChatGroup_Register.getRegister();
                    listChatGroup_RegisterNewChatGroup_Register.setRegister(register);
                    listChatGroup_RegisterNewChatGroup_Register = em.merge(listChatGroup_RegisterNewChatGroup_Register);
                    if (oldRegisterOfListChatGroup_RegisterNewChatGroup_Register != null && !oldRegisterOfListChatGroup_RegisterNewChatGroup_Register.equals(register)) {
                        oldRegisterOfListChatGroup_RegisterNewChatGroup_Register.getListChatGroup_Register().remove(listChatGroup_RegisterNewChatGroup_Register);
                        oldRegisterOfListChatGroup_RegisterNewChatGroup_Register = em.merge(oldRegisterOfListChatGroup_RegisterNewChatGroup_Register);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = register.getRegister_id();
                if (findRegister(id) == null) {
                    throw new NonexistentEntityException("The register with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Register register;
            try {
                register = em.getReference(Register.class, id);
                register.getRegister_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The register with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Set<ChatGroup_Register> listChatGroup_RegisterOrphanCheck = register.getListChatGroup_Register();
            for (ChatGroup_Register listChatGroup_RegisterOrphanCheckChatGroup_Register : listChatGroup_RegisterOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Register (" + register + ") cannot be destroyed since the ChatGroup_Register " + listChatGroup_RegisterOrphanCheckChatGroup_Register + " in its listChatGroup_Register field has a non-nullable register field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(register);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Register> findRegisterEntities() {
        return findRegisterEntities(true, -1, -1);
    }

    public List<Register> findRegisterEntities(int maxResults, int firstResult) {
        return findRegisterEntities(false, maxResults, firstResult);
    }

    private List<Register> findRegisterEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Register.class));
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

    public Register findRegister(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Register.class, id);
        } finally {
            em.close();
        }
    }

    public int getRegisterCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Register> rt = cq.from(Register.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
