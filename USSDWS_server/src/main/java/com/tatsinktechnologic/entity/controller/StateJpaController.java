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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author olivier.tatsinkou
 */
public class StateJpaController implements Serializable {

    public StateJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(State state) {
        if (state.getListCurrent_state() == null) {
            state.setListCurrent_state(new ArrayList<State_Change>());
        }
        if (state.getListnext_state() == null) {
            state.setListnext_state(new ArrayList<State_Change>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            State defaultState = state.getDefaultState();
            if (defaultState != null) {
                defaultState = em.getReference(defaultState.getClass(), defaultState.getState_id());
                state.setDefaultState(defaultState);
            }
            Collection<State_Change> attachedListCurrent_state = new ArrayList<State_Change>();
            for (State_Change listCurrent_stateState_ChangeToAttach : state.getListCurrent_state()) {
                listCurrent_stateState_ChangeToAttach = em.getReference(listCurrent_stateState_ChangeToAttach.getClass(), listCurrent_stateState_ChangeToAttach.getState_change_id());
                attachedListCurrent_state.add(listCurrent_stateState_ChangeToAttach);
            }
            state.setListCurrent_state(attachedListCurrent_state);
            Collection<State_Change> attachedListnext_state = new ArrayList<State_Change>();
            for (State_Change listnext_stateState_ChangeToAttach : state.getListnext_state()) {
                listnext_stateState_ChangeToAttach = em.getReference(listnext_stateState_ChangeToAttach.getClass(), listnext_stateState_ChangeToAttach.getState_change_id());
                attachedListnext_state.add(listnext_stateState_ChangeToAttach);
            }
            state.setListnext_state(attachedListnext_state);
            em.persist(state);
            if (defaultState != null) {
                State oldDefaultStateOfDefaultState = defaultState.getDefaultState();
                if (oldDefaultStateOfDefaultState != null) {
                    oldDefaultStateOfDefaultState.setDefaultState(null);
                    oldDefaultStateOfDefaultState = em.merge(oldDefaultStateOfDefaultState);
                }
                defaultState.setDefaultState(state);
                defaultState = em.merge(defaultState);
            }
            for (State_Change listCurrent_stateState_Change : state.getListCurrent_state()) {
                State oldCurrent_stateOfListCurrent_stateState_Change = listCurrent_stateState_Change.getCurrent_state();
                listCurrent_stateState_Change.setCurrent_state(state);
                listCurrent_stateState_Change = em.merge(listCurrent_stateState_Change);
                if (oldCurrent_stateOfListCurrent_stateState_Change != null) {
                    oldCurrent_stateOfListCurrent_stateState_Change.getListCurrent_state().remove(listCurrent_stateState_Change);
                    oldCurrent_stateOfListCurrent_stateState_Change = em.merge(oldCurrent_stateOfListCurrent_stateState_Change);
                }
            }
            for (State_Change listnext_stateState_Change : state.getListnext_state()) {
                State oldNext_stateOfListnext_stateState_Change = listnext_stateState_Change.getNext_state();
                listnext_stateState_Change.setNext_state(state);
                listnext_stateState_Change = em.merge(listnext_stateState_Change);
                if (oldNext_stateOfListnext_stateState_Change != null) {
                    oldNext_stateOfListnext_stateState_Change.getListnext_state().remove(listnext_stateState_Change);
                    oldNext_stateOfListnext_stateState_Change = em.merge(oldNext_stateOfListnext_stateState_Change);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(State state) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            State persistentState = em.find(State.class, state.getState_id());
            State defaultStateOld = persistentState.getDefaultState();
            State defaultStateNew = state.getDefaultState();
            Collection<State_Change> listCurrent_stateOld = persistentState.getListCurrent_state();
            Collection<State_Change> listCurrent_stateNew = state.getListCurrent_state();
            Collection<State_Change> listnext_stateOld = persistentState.getListnext_state();
            Collection<State_Change> listnext_stateNew = state.getListnext_state();
            if (defaultStateNew != null) {
                defaultStateNew = em.getReference(defaultStateNew.getClass(), defaultStateNew.getState_id());
                state.setDefaultState(defaultStateNew);
            }
            Collection<State_Change> attachedListCurrent_stateNew = new ArrayList<State_Change>();
            for (State_Change listCurrent_stateNewState_ChangeToAttach : listCurrent_stateNew) {
                listCurrent_stateNewState_ChangeToAttach = em.getReference(listCurrent_stateNewState_ChangeToAttach.getClass(), listCurrent_stateNewState_ChangeToAttach.getState_change_id());
                attachedListCurrent_stateNew.add(listCurrent_stateNewState_ChangeToAttach);
            }
            listCurrent_stateNew = attachedListCurrent_stateNew;
            state.setListCurrent_state(listCurrent_stateNew);
            Collection<State_Change> attachedListnext_stateNew = new ArrayList<State_Change>();
            for (State_Change listnext_stateNewState_ChangeToAttach : listnext_stateNew) {
                listnext_stateNewState_ChangeToAttach = em.getReference(listnext_stateNewState_ChangeToAttach.getClass(), listnext_stateNewState_ChangeToAttach.getState_change_id());
                attachedListnext_stateNew.add(listnext_stateNewState_ChangeToAttach);
            }
            listnext_stateNew = attachedListnext_stateNew;
            state.setListnext_state(listnext_stateNew);
            state = em.merge(state);
            if (defaultStateOld != null && !defaultStateOld.equals(defaultStateNew)) {
                defaultStateOld.setDefaultState(null);
                defaultStateOld = em.merge(defaultStateOld);
            }
            if (defaultStateNew != null && !defaultStateNew.equals(defaultStateOld)) {
                State oldDefaultStateOfDefaultState = defaultStateNew.getDefaultState();
                if (oldDefaultStateOfDefaultState != null) {
                    oldDefaultStateOfDefaultState.setDefaultState(null);
                    oldDefaultStateOfDefaultState = em.merge(oldDefaultStateOfDefaultState);
                }
                defaultStateNew.setDefaultState(state);
                defaultStateNew = em.merge(defaultStateNew);
            }
            for (State_Change listCurrent_stateOldState_Change : listCurrent_stateOld) {
                if (!listCurrent_stateNew.contains(listCurrent_stateOldState_Change)) {
                    listCurrent_stateOldState_Change.setCurrent_state(null);
                    listCurrent_stateOldState_Change = em.merge(listCurrent_stateOldState_Change);
                }
            }
            for (State_Change listCurrent_stateNewState_Change : listCurrent_stateNew) {
                if (!listCurrent_stateOld.contains(listCurrent_stateNewState_Change)) {
                    State oldCurrent_stateOfListCurrent_stateNewState_Change = listCurrent_stateNewState_Change.getCurrent_state();
                    listCurrent_stateNewState_Change.setCurrent_state(state);
                    listCurrent_stateNewState_Change = em.merge(listCurrent_stateNewState_Change);
                    if (oldCurrent_stateOfListCurrent_stateNewState_Change != null && !oldCurrent_stateOfListCurrent_stateNewState_Change.equals(state)) {
                        oldCurrent_stateOfListCurrent_stateNewState_Change.getListCurrent_state().remove(listCurrent_stateNewState_Change);
                        oldCurrent_stateOfListCurrent_stateNewState_Change = em.merge(oldCurrent_stateOfListCurrent_stateNewState_Change);
                    }
                }
            }
            for (State_Change listnext_stateOldState_Change : listnext_stateOld) {
                if (!listnext_stateNew.contains(listnext_stateOldState_Change)) {
                    listnext_stateOldState_Change.setNext_state(null);
                    listnext_stateOldState_Change = em.merge(listnext_stateOldState_Change);
                }
            }
            for (State_Change listnext_stateNewState_Change : listnext_stateNew) {
                if (!listnext_stateOld.contains(listnext_stateNewState_Change)) {
                    State oldNext_stateOfListnext_stateNewState_Change = listnext_stateNewState_Change.getNext_state();
                    listnext_stateNewState_Change.setNext_state(state);
                    listnext_stateNewState_Change = em.merge(listnext_stateNewState_Change);
                    if (oldNext_stateOfListnext_stateNewState_Change != null && !oldNext_stateOfListnext_stateNewState_Change.equals(state)) {
                        oldNext_stateOfListnext_stateNewState_Change.getListnext_state().remove(listnext_stateNewState_Change);
                        oldNext_stateOfListnext_stateNewState_Change = em.merge(oldNext_stateOfListnext_stateNewState_Change);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = state.getState_id();
                if (findState(id) == null) {
                    throw new NonexistentEntityException("The state with id " + id + " no longer exists.");
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
            State state;
            try {
                state = em.getReference(State.class, id);
                state.getState_id();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The state with id " + id + " no longer exists.", enfe);
            }
            State defaultState = state.getDefaultState();
            if (defaultState != null) {
                defaultState.setDefaultState(null);
                defaultState = em.merge(defaultState);
            }
            Collection<State_Change> listCurrent_state = state.getListCurrent_state();
            for (State_Change listCurrent_stateState_Change : listCurrent_state) {
                listCurrent_stateState_Change.setCurrent_state(null);
                listCurrent_stateState_Change = em.merge(listCurrent_stateState_Change);
            }
            Collection<State_Change> listnext_state = state.getListnext_state();
            for (State_Change listnext_stateState_Change : listnext_state) {
                listnext_stateState_Change.setNext_state(null);
                listnext_stateState_Change = em.merge(listnext_stateState_Change);
            }
            em.remove(state);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<State> findStateEntities() {
        return findStateEntities(true, -1, -1);
    }

    public List<State> findStateEntities(int maxResults, int firstResult) {
        return findStateEntities(false, maxResults, firstResult);
    }

    private List<State> findStateEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(State.class));
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

    public State findState(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(State.class, id);
        } finally {
            em.close();
        }
    }

    public int getStateCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<State> rt = cq.from(State.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
