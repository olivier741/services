/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.daoUtil;

import com.tatsinktechnologic.beans_entity.Configuration;
import com.tatsinktechnologic.beans_entity.Process_Unit;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author olivier
 */
public class Receiver_DaoUtil implements Serializable{
     

    public static HashMap<String,Configuration> getConfiguration_ProcessUnit(EntityManagerFactory emf,Process_Unit process_unit){
        
        EntityManager em = emf.createEntityManager();
        HashMap<String,Configuration> result = new HashMap<String,Configuration>();
        String node_name = process_unit.getProcess_unit_id().getNode_name();
        String server_name= process_unit.getProcess_unit_id().getServer_name();
        try {
            
           CriteriaBuilder builder = em.getCriteriaBuilder();

           CriteriaQuery<Configuration> criteria = builder.createQuery( Configuration.class );
           Root<Configuration> configurationRoot = criteria.from(Configuration.class );
           
           criteria.where( builder.and( builder.equal( configurationRoot.get("process_unit")
                                                                        .get("process_unit_id")
                                                                        .get("node_name"),node_name),
                                        builder.equal( configurationRoot.get("process_unit")
                                                                        .get("process_unit_id")
                                                                        .get("server_name"),server_name)
                                        )
                          );
            List<Configuration> resultList = em.createQuery( criteria ).getResultList();
            
            // Place results in map
            for (Configuration conf: resultList) {
               result.put(conf.getProcess_param(),conf);
            }

            return result;
        } finally {
            em.close();
        }
        
    }
        
}
