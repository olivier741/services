/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.shiro.realm;

/**
 *
 * @author olivier
 */

import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.account.User;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.SimpleByteSource;


public class DBRealm extends AuthorizingRealm {

    private Commun_Controller commune_controller;

    public DBRealm() {
        commune_controller = lookupUserService();
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        if (principals == null) {
		throw new AuthorizationException("Principal is not null!");
	}

        String username = StringUtils.trim((String) principals.getPrimaryPrincipal());

        Set<String> roles = commune_controller.listRolesByUsername(username);
        Set<String> permissions = commune_controller.listPermissionsByUsername(username);
        
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(roles);
        authorizationInfo.setStringPermissions(permissions);
        
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

              
        String username = StringUtils.trim((String) token.getPrincipal());
        if (StringUtils.isEmpty(username)) {
            throw new AuthenticationException();
        }

        User user = commune_controller.getOneByUsername(username);
        
        if (user == null) {
            throw new AuthenticationException();
        }
        
        System.out.println("----------------------------- islock1 = "+user.isIslock());
        
        if (user.isIslock()){
            System.out.println("----------------------------- islock 2 = "+user.isIslock());
            throw new LockedAccountException("YOUR ACCOUNT IS LOCK");
        }
        
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(username, user.getPassword(),new SimpleByteSource(Base64.decode(user.getSalt())), getName());

        return authenticationInfo;
    }
    
    
    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {

        super.clearCachedAuthorizationInfo(principals);

    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {

        super.clearCachedAuthenticationInfo(principals);

    }


    @Override
    public void clearCache(PrincipalCollection principals) {

        super.clearCache(principals);

    }

    public void clearAllCachedAuthorizationInfo() {

        getAuthorizationCache().clear();

    }

    public void clearAllCachedAuthenticationInfo() {

        getAuthenticationCache().clear();
    }

    public void clearAllCache() {

        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();

    }



    @SuppressWarnings("rawtypes")
    private Commun_Controller lookupUserService() {
        Commun_Controller commune_controller = null;

        try {
            BeanManager beanManager = InitialContext.doLookup("java:comp/BeanManager");
            @SuppressWarnings("unchecked")
            Bean<Commun_Controller> communServiceBean = (Bean) beanManager.getBeans(Commun_Controller.class).iterator().next();
            CreationalContext context = beanManager.createCreationalContext(communServiceBean);
            commune_controller = (Commun_Controller) beanManager.getReference(communServiceBean, Commun_Controller.class, context);
        } catch (NamingException e) {
            throw new AuthenticationException("can not load Commun_Controller");
        }

        return commune_controller;
    }

}