/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entity.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.tatsinktechnologic.entity.State;
import com.tatsinktechnologic.entity.State_Change;
import com.tatsinktechnologic.entity.controller.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class State_ChangeJpaController implements Serializable {

    public State_ChangeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(State_Change state_Change) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            State current_state = state_Change.getCurrent_state();
            if (current_state != null) {
                current_state = em.getReference(current_state.getClass(), current_state.getState_id());
                state_Change.setCurrent_state(current_state);
            }
            State next_state = state_Change.getNext_state();
            if (next_state != null) {
                next_state = em.getReference(next_state.getClass(), next_state.getState_id());
                state_Change.setNext_state(next_state);
            }
            em.persist(state_Change);
            if (current_state != null) {
                current_state.getListCurrent_state().add(state_Change);
                current_state = em.merge(current_state);
            }
            if (next_state != null) {
                next_state.getListCurrent_state().add(state_Change);
                next_state = em.merge(next_state);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(State_Change state_Change) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            State_Change persistentState_Change = em.find(State_Change.class, state_Change.getState_change_id());
            State current_stateOld = persistentState_Change.getCurrent_state();
            State current_stateNew = state_Change.getCurrent_state();
            State next_stateOld = persistentState_Change.getNext_state();
            State next_stateNew = state_Change.getNext_state();
            if (current_stateNew != null) {
                current_stateNew = em.getReference(current_stateNew.getClass(), current_stateNew.getState_id());
                state_Change.setCurrent_state(current_stateNew);
            }
            if (next_stateNew != null) {
                next_stateNew = em.getReference(next_stateNew.getClass(), next_stateNew.getState_id());
                state_Change.setNext_state(next_stateNew);
            }
            state_Change = em.merge(state_Change);
            if (current_stateOld != null && !current_stateOld.equals(current_stateNew)) {
                current_stateOld.getListCurrent_state().remove(state_Change);
                current_stateOld = em.merge(current_stateOld);
            }
            if (current_stateNew != null && !current_stateNew.equals(current_stateOld)) {
                current_stateNew.getListCurrent_state().add(state_Change);
                current_stateNew = em.merge(current_stateNew);
            }
            if (next_stateOld != null && !next_stateOld.equals(next_stateNew)) {
                next_stateOld.getListCurrent_state().remove(state_Change);
                next_stateOld = em.merge(next_stateOld);
            }
            if (next_stateNew != null && !next_stateNew.equals(next_stateOld)) {
                next_stateNew.getListCurrent_state().add(state_Change);
                next_stateNew = em.merge(next_stateNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = state_Change.getState_change_id();
                if (findState_Change(id) == null) {
                    throw new NonexistentEntityException("The state_Change with id " + id + " no longer exists.");
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
            State_Change state_Change;
            try {
                state_Change = em.getReference(State_Change.class, id);
                state_Change.getState_change_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The state_Change with id " + id + " no longer exists.", enfe);
            }
            State current_state = state_Change.getCurrent_state();
            if (current_state != null) {
                current_state.getListCurrent_state().remove(state_Change);
                current_state = em.merge(current_state);
            }
            State next_state = state_Change.getNext_state();
            if (next_state != null) {
                next_state.getListCurrent_state().remove(state_Change);
                next_state = em.merge(next_state);
            }
            em.remove(state_Change);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<State_Change> findState_ChangeEntities() {
        return findState_ChangeEntities(true, -1, -1);
    }

    public List<State_Change> findState_ChangeEntities(int maxResults, int firstResult) {
        return findState_ChangeEntities(false, maxResults, firstResult);
    }

    private List<State_Change> findState_ChangeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(State_Change.class));
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

    public State_Change findState_Change(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(State_Change.class, id);
        } finally {
            em.close();
        }
    }

    public int getState_ChangeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<State_Change> rt = cq.from(State_Change.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
