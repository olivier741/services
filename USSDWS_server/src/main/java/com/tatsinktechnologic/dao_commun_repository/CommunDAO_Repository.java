/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_commun_repository;

import com.tatsinktechnologic.entity.Mo_Fake;
import com.tatsinktechnologic.entity.State;
import com.tatsinktechnologic.entity.State_Change;
import com.tatsinktechnologic.entity.State_Level;
import com.tatsinktechnologic.entity.controller.Mo_FakeJpaController;
import com.tatsinktechnologic.entity.controller.StateJpaController;
import com.tatsinktechnologic.entity.controller.State_ChangeJpaController;
import com.tatsinktechnologic.xml.Ussd_API_Conf;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author olivier
 */
public class CommunDAO_Repository implements Serializable {

    private static Logger logger = Logger.getLogger(CommunDAO_Repository.class);
    private EntityManagerFactory emf;
    private Ussd_API_Conf ussd_api_conf;
    private Properties consumer_props;
    private Properties product_props;
    private List<State> ListState;
    private List<State_Change> listState_Change;
    private List<Mo_Fake> listMo_Fake;
    private HashMap<String, State_Change> FIRST_STATECHANGE;
    private HashMap<String, State_Change> ALL_STATECHANGE;
    private HashMap<String, Mo_Fake> SET_MO_FAKE;

    private CommunDAO_Repository() {

        Properties database_prop = null;
        try {
            // set up new properties object from file "jpa.properties"
            FileInputStream propFile = new FileInputStream("etc" + File.separator + "jpa.properties");
            database_prop = new Properties(System.getProperties());
            database_prop.load(propFile);

            // set the system properties
            System.setProperties(database_prop);

        } catch (java.io.FileNotFoundException e) {
            logger.error("Can't find jpa.properties");
        } catch (java.io.IOException e) {
            logger.error("I/O failed.");
        }

        File file_ussd_api = new File("etc" + File.separator + "USSDGW_API.xml");

        Serializer serializer_ussd_api = new Persister();

        // get configuration of service_listener.xml
        try {
            ussd_api_conf = serializer_ussd_api.read(Ussd_API_Conf.class, file_ussd_api);
            logger.info("successfull load : etc" + File.separator + "USSDGW_API.xml");
        } catch (Exception e) {
            logger.error("ERROR in config file service_listener.xml", e);
            System.out.println(e);
        }

        product_props = new Properties();

        try {
            product_props.load(new FileInputStream("etc" + File.separator + "producer.properties"));
        } catch (IOException e) {
            logger.error("cannot load producer.properties config file", e);
        }

        consumer_props = new Properties();
        try {
            consumer_props.load(new FileInputStream("etc" + File.separator + "consumer.properties"));
        } catch (IOException e) {
            logger.error("cannot load consumer.properties config file", e);
        }

        emf = Persistence.createEntityManagerFactory(database_prop.getProperty("DB_PU1.jpa.datasource"));
        load();
    }

    private static class SingletonCommunDAO {

        private static final CommunDAO_Repository _communDAO_Repository = new CommunDAO_Repository();
    }

    public static CommunDAO_Repository getConfigurationLoader() {
        return SingletonCommunDAO._communDAO_Repository;
    }

    public EntityManagerFactory getEmf() {
        return emf;
    }

    public Ussd_API_Conf getUssd_api_conf() {
        return ussd_api_conf;
    }

    public Properties getConsumer_props() {
        return consumer_props;
    }

    public Properties getProduct_props() {
        return product_props;
    }

    public List<State> getListState() {
        return ListState;
    }

    public List<State_Change> getListState_Change() {
        return listState_Change;
    }

    public List<Mo_Fake> getListMo_Fake() {
        return listMo_Fake;
    }

    public HashMap<String, State_Change> getFIRST_STATECHANGE() {
        return FIRST_STATECHANGE;
    }

    public HashMap<String, State_Change> getALL_STATECHANGE() {
        return ALL_STATECHANGE;
    }

    public HashMap<String, Mo_Fake> getSET_MO_FAKE() {
        return SET_MO_FAKE;
    }

    

    private void load() {
        StateJpaController state_controller = new StateJpaController(emf);
        State_ChangeJpaController state_change_controller = new State_ChangeJpaController(emf);
        Mo_FakeJpaController mofake_controller = new Mo_FakeJpaController(emf);

        listState_Change = state_change_controller.findState_ChangeEntities();
        ListState = state_controller.findStateEntities();
        listMo_Fake = mofake_controller.findMo_FakeEntities();

        FIRST_STATECHANGE = new HashMap<String, State_Change>();
        ALL_STATECHANGE = new HashMap<String, State_Change>();
        for (State_Change state_change : listState_Change) {
            if (state_change.getCurrent_state().getState_level() != null && state_change.getCurrent_state().getState_level() == State_Level.START) {
                FIRST_STATECHANGE.put(state_change.getInput(), state_change);
            }
            ALL_STATECHANGE.put(state_change.getState_change_id() + "_" + state_change.getInput(), state_change);
        }

        SET_MO_FAKE = new HashMap<String, Mo_Fake>();

        for (Mo_Fake mofake : listMo_Fake) {
            SET_MO_FAKE.put(mofake.getBizid()+"_"+mofake.getInput(), mofake);
        }

    }

}
