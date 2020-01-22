/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.main;

import com.tatsinktechnologic.dao_commun_repository.CommunDAO_Repository;
import com.tatsinktechnologic.entities.api_gateway.Security_Mode;
import com.tatsinktechnologic.gateway.security.handler.AuthenticationFilter_tokenAuth;
import com.tatsinktechnologic.gateway.security.handler.BasicAuthAuthorizationInterceptor;
import com.tatsinktechnologic.gateway.server.API_GatewayService_userTokenAuth;
import com.tatsinktechnologic.gateway.security.AuthenticationService_tokenAuth;
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
        String security_mode = communConf.getApp_conf().getSecurity_mode();
        
        if (security_mode!=null){
            
            if (security_mode.equalsIgnoreCase(Security_Mode.REST_BASIC_AUTH.name())){
                  // basic auth
//                providers.add(new BasicAuthAuthorizationInterceptor());
//
//                factoryBean.setResourceClasses(API_GatewayService.class);
//                factoryBean.setProviders(providers);
//                factoryBean.setResourceProvider(new SingletonResourceProvider(new API_GatewayService()));
//                factoryBean.getInInterceptors().add(new LoggingInInterceptor());
//                factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
            }else if (security_mode.equalsIgnoreCase(Security_Mode.REST_TOKEN_AUTH_ROLE.name())){
                 // token authentication with role

//                listResourceClasses.add(CategoryService_userTokenAuth_Role.class);
//                listResourceClasses.add(AuthenticationService_tokenAuth.class);
//                factoryBean.setResourceClasses(listResourceClasses);
//
//                providers.add(new AuthenticationFilter_tokenAuth_Role());
//                factoryBean.setProviders(providers);
//
//                listResourceProvider.add(new SingletonResourceProvider(new CategoryService_userTokenAuth_Role()));
//                listResourceProvider.add(new SingletonResourceProvider(new AuthenticationService_tokenAuth()));
//
//                factoryBean.setResourceProviders(listResourceProvider);
//                factoryBean.getInInterceptors().add(new LoggingInInterceptor());
//                factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
            }else {
                // REST_TOKEN_AUTH is default authentication
                listResourceClasses.add(API_GatewayService_userTokenAuth.class);
                listResourceClasses.add(AuthenticationService_tokenAuth.class);
                factoryBean.setResourceClasses(listResourceClasses);

                providers.add(new AuthenticationFilter_tokenAuth());
                factoryBean.setProviders(providers);

                listResourceProvider.add(new SingletonResourceProvider(new API_GatewayService_userTokenAuth()));
                listResourceProvider.add(new SingletonResourceProvider(new AuthenticationService_tokenAuth()));

                factoryBean.setResourceProviders(listResourceProvider);       
                factoryBean.getInInterceptors().add(new LoggingInInterceptor());
                factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());

            }
        }else {
              // REST_TOKEN_AUTH is default authentication
                listResourceClasses.add(API_GatewayService_userTokenAuth.class);
                listResourceClasses.add(AuthenticationService_tokenAuth.class);
                factoryBean.setResourceClasses(listResourceClasses);

                providers.add(new AuthenticationFilter_tokenAuth());
                factoryBean.setProviders(providers);

                listResourceProvider.add(new SingletonResourceProvider(new API_GatewayService_userTokenAuth()));
                listResourceProvider.add(new SingletonResourceProvider(new AuthenticationService_tokenAuth()));

                factoryBean.setResourceProviders(listResourceProvider);       
                factoryBean.getInInterceptors().add(new LoggingInInterceptor());
                factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
 
        }
       
        factoryBean.setAddress(URL);
        
        Server server = factoryBean.create();

    }
    
    
}
