package com.tatsinktechnologic.persistence;


import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.enterprise.inject.Produces;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EntityManagerFactoryBean {
    
   @PersistenceUnit(unitName = "DB_PU1")
    protected EntityManagerFactory emf;
    
    @Produces 
    public EntityManagerFactory getEntityManagerFactory() {

            if (emf == null) {
                emf = Persistence.createEntityManagerFactory("DB_PU1");
            }
            return emf;
    }

}