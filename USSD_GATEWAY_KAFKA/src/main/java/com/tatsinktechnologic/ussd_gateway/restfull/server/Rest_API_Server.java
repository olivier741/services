/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.ussd_gateway.restfull.server;


import com.tatsinktechnologic.gateway.security.handler.AuthenticationFilter_tokenAuth;
import com.tatsinktechnologic.ussd_gateway.ussdapp.config.CommunDAO_Repository;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author olivier
 */
public class Rest_API_Server {
    
    
    private static Logger logger = Logger.getLogger(Rest_API_Server.class);
    private static CommunDAO_Repository communConf = CommunDAO_Repository.getConfigurationLoader();
    
    
    public static void main(String[] args) {
       PropertyConfigurator.configure("etc" + File.separator + "log4j.cfg");
        logger.info("Load log4j config file done.");
        

        // TODO code application logic here
        JAXRSServerFactoryBean factoryBean = new JAXRSServerFactoryBean();
        List<Object> providers = new ArrayList<>();
        List<Class<?>> listResourceClasses = new ArrayList<Class<?>>();
        List<ResourceProvider> listResourceProvider = new ArrayList<ResourceProvider>();
        String URL = communConf.getApp_conf().getUrl();
        
          // REST_TOKEN_AUTH is default authentication
        listResourceClasses.add(API_GatewayService_userTokenAuth.class);
        factoryBean.setResourceClasses(listResourceClasses);

        providers.add(new AuthenticationFilter_tokenAuth());
        factoryBean.setProviders(providers);

        listResourceProvider.add(new SingletonResourceProvider(new API_GatewayService_userTokenAuth()));

        factoryBean.setResourceProviders(listResourceProvider);       
        factoryBean.getInInterceptors().add(new LoggingInInterceptor());
        factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
       
        factoryBean.setAddress(URL);
        
        Server server = factoryBean.create();

    }
    
    
}
