/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 *
 * @author olivier.tatsinkou
 */
public class ConnectionParameter {
    private ConnectionParameter() {

    }
    /*
     * Expects a config in the following format
     *
     * poolName = "test pool"
     * jdbcUrl = ""
     * maximumPoolSize = 10
     * minimumIdle = 2
     * username = ""
     * password = ""
     * cachePrepStmts = true
     * prepStmtCacheSize = 256
     * prepStmtCacheSqlLimit = 2048
     * useServerPrepStmts = true
     *
     * Let HikariCP bleed out here on purpose
     */
    public static HikariDataSource getDataSourceFromConfig( Config conf, MetricRegistry metricRegistry , HealthCheckRegistry healthCheckRegistry) {

        HikariConfig jdbcConfig = new HikariConfig();
        try {
          jdbcConfig.setPoolName(conf.getString("poolName"));  
        } catch (Exception e) {
        }
        try {
          jdbcConfig.setMaximumPoolSize(conf.getInt("maximumPoolSize"));  
        } catch (Exception e) {
        }
        try {
          jdbcConfig.setMinimumIdle(conf.getInt("minimumIdle"));  
        } catch (Exception e) {
        }
        
        try {
           jdbcConfig.setJdbcUrl(conf.getString("jdbcUrl")); 
        } catch (Exception e) {
        }
        
        try {
           jdbcConfig.setUsername(conf.getString("username")); 
        } catch (Exception e) {
        }
        
        try {
          jdbcConfig.setPassword(conf.getString("password"));  
        } catch (Exception e) {
        }
        
        
        try {
            jdbcConfig.setAllowPoolSuspension(conf.getBoolean("allowPoolSuspension"));
         } catch (Exception e) {
              
         }
        try {
            jdbcConfig.setAutoCommit(conf.getBoolean("autoCommit"));
        } catch (Exception e) {
        }
        
        try {
            jdbcConfig.setCatalog(conf.getString("catalog"));
        } catch (Exception e) {
        }
         
        try {
          jdbcConfig.setConnectionInitSql(conf.getString("connectionInitSql"));  
        } catch (Exception e) {
        }
        
        try {
          jdbcConfig.setConnectionTestQuery(conf.getString("connectionTestQuery"));  
        } catch (Exception e) {
        }
        
        try {
           jdbcConfig.setConnectionTimeout(conf.getLong("connectionTimeout")); 
        } catch (Exception e) {
        }
            
        try {
           jdbcConfig.setDataSourceClassName(conf.getString("dataSourceClassName")); 
        } catch (Exception e) {
        }
    
        try {
          jdbcConfig.setDataSourceJNDI(conf.getString("dataSourceJNDI"));  
        } catch (Exception e) {
        }

        try {
          jdbcConfig.setDriverClassName(conf.getString("driverClassName"));  
        } catch (Exception e) {
        }
        
        try {
          jdbcConfig.setIdleTimeout(conf.getLong("idleTimeout"));  
        } catch (Exception e) {
        }    
            
        try {
          jdbcConfig.setInitializationFailTimeout(conf.getLong("initializationFailTimeout"));  
        } catch (Exception e) {
        }   
        
        try {
          jdbcConfig.setIsolateInternalQueries(conf.getBoolean("isolateInternalQueries"));  
        } catch (Exception e) {
        }  
        
        try {
          jdbcConfig.setLeakDetectionThreshold(conf.getLong("leakDetectionThreshold"));  
        } catch (Exception e) {
        }  
        
        try {
          jdbcConfig.setMaxLifetime(conf.getLong("maxLifetime"));  
        } catch (Exception e) {
        }
        
        try {
          jdbcConfig.setReadOnly(conf.getBoolean("readOnly"));  
        } catch (Exception e) {
        }
        
       
        try {
          jdbcConfig.setRegisterMbeans(conf.getBoolean("registerMbeans"));  
        } catch (Exception e) {
        }
        
        try {
          jdbcConfig.setSchema(conf.getString("schema"));  
        } catch (Exception e) {
        }

        
        try {
          jdbcConfig.setTransactionIsolation(conf.getString("transactionIsolation"));  
        } catch (Exception e) {
        }
        
        try {
          jdbcConfig.setValidationTimeout(conf.getLong("validationTimeout"));  
        } catch (Exception e) {
        }
        

        jdbcConfig.addDataSourceProperty("password", conf.getString("password"));
        jdbcConfig.addDataSourceProperty("cachePrepStmts", conf.getBoolean("cachePrepStmts"));
        jdbcConfig.addDataSourceProperty("prepStmtCacheSize", conf.getInt("prepStmtCacheSize"));
        jdbcConfig.addDataSourceProperty("prepStmtCacheSqlLimit", conf.getInt("prepStmtCacheSqlLimit"));
        jdbcConfig.addDataSourceProperty("useServerPrepStmts", conf.getBoolean("useServerPrepStmts"));

        // Add HealthCheck
        jdbcConfig.setHealthCheckRegistry(healthCheckRegistry);

        // Add Metrics
        jdbcConfig.setMetricRegistry(metricRegistry);
        return new HikariDataSource(jdbcConfig);
    }
}

