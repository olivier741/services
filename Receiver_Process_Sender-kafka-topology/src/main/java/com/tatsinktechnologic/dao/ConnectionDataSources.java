/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao;

/**
 *
 * @author olivier.tatsinkou
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;


import com.zaxxer.hikari.HikariDataSource;
import java.io.File;


public class ConnectionDataSources {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionDataSources.class);

    /*
     * Normally we would be using the app config but since this is an example
     * we will be using a localized example config.
     */

      private static final Config conf = ConfigFactory.parseFile(new File("etc" + File.separator + "pools.conf")).resolve();


    /*
     *  This pool is made for short quick transactions that the web application uses.
     *  Using enum singleton pattern for lazy singletons
     */

    private enum Receiver_DataSource {

        INSTANCE(ConnectionParameter.getDataSourceFromConfig(conf.getConfig("pools.receiver"), null, null));
        private final HikariDataSource dataSource ;
        
        private Receiver_DataSource(HikariDataSource dataSource) {
            this.dataSource = dataSource;
        }

        public HikariDataSource getDataSource() {
            return dataSource;
        }
    }

    public static HikariDataSource getReceiver_DataSource() {
        return Receiver_DataSource.INSTANCE.getDataSource();
    }
    
    
    private enum Sender_DataSource {

        INSTANCE(ConnectionParameter.getDataSourceFromConfig(conf.getConfig("pools.sender"), null, null));
        private final HikariDataSource dataSource ;
        
        private Sender_DataSource(HikariDataSource dataSource) {
            this.dataSource = dataSource;
        }

        public HikariDataSource getDataSource() {
            return dataSource;
        }
    }

    public static HikariDataSource getSender_DataSource() {
        return Sender_DataSource.INSTANCE.getDataSource();
    }

   

    private enum Process_DataSource {
        
        INSTANCE(ConnectionParameter.getDataSourceFromConfig(conf.getConfig("pools.process"), null, null));
        
        private final HikariDataSource dataSource;
        
        private Process_DataSource(HikariDataSource dataSource) {
            this.dataSource = dataSource;
        }

        public HikariDataSource getDataSource() {
            return dataSource;
        }

    }

    public static HikariDataSource getProcess_DataSource() {
        return Process_DataSource.INSTANCE.getDataSource();
    }

}

