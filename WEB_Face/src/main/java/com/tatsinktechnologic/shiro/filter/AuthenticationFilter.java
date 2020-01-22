/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.shiro.filter;


import com.tatsinktechnologic.bean.login.LogonMB;
import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.account.User;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @author olivier.tatsinkou
 */
public class AuthenticationFilter implements Filter{
    private FilterConfig customedFilterConfig;
    
    @Inject
    private Commun_Controller commun_controller;
    
    public void doFilter(ServletRequest req, ServletResponse resp,FilterChain chain) throws IOException, ServletException {
        
         HttpServletRequest httpRequest = (HttpServletRequest) req;
         HttpServletResponse httpResponse = (HttpServletResponse) resp;

         String newPath = httpRequest.getContextPath(); 
         String Url_link = httpRequest.getRequestURI();
         String URL = Url_link.replace(newPath, "");
         LogonMB logon = (LogonMB) httpRequest.getSession().getAttribute("logonMB");
             
          
            if (logon!=null){
              
               
               if (SecurityUtils.getSubject().getPrincipal() != null){

                   String username = SecurityUtils.getSubject().getPrincipal().toString();
                   User user = commun_controller.getOneByUsername(username);
                   
                   if (user.isIsReset()) {
                       ((HttpServletResponse)resp).sendRedirect(newPath + "/Login/reset_password.xhtml");
                   }else if (SecurityUtils.getSubject().hasRole("root") || SecurityUtils.getSubject().isPermitted(URL) || Url_link.equals(newPath + "/web_site/index.xhtml") ){
                        chain.doFilter(req, resp);
                   }else {
                        ((HttpServletResponse)resp).sendRedirect(newPath + "/web_site/index.xhtml");
                   }
              } else {
                 if (!SecurityUtils.getSubject().isAuthenticated()){
                       SecurityUtils.getSubject().logout();
                       ((HttpServletResponse)resp).sendRedirect(newPath + "/Login/login.xhtml");
                 }else{
                    chain.doFilter(req, resp); 
                 }
                 
              }
           }else{
              if (SecurityUtils.getSubject().getPrincipal() != null){
                   String username = SecurityUtils.getSubject().getPrincipal().toString();
                   User user = commun_controller.getOneByUsername(username);
                   
                   if (user.isIsReset()){
                        SecurityUtils.getSubject().logout();
                        ((HttpServletResponse)resp).sendRedirect(newPath + "/Login/login.xhtml");
                   }else if (SecurityUtils.getSubject().hasRole("root") || SecurityUtils.getSubject().isPermitted(URL) || Url_link.equals(newPath + "/web_site/index.xhtml")){
                        chain.doFilter(req, resp);
                   }else {
                        ((HttpServletResponse)resp).sendRedirect(newPath + "/web_site/index.xhtml");
                   }
               
              }else{
                  if (!SecurityUtils.getSubject().isAuthenticated()){
                       SecurityUtils.getSubject().logout();
                       ((HttpServletResponse)resp).sendRedirect(newPath + "/Login/login.xhtml");
                 }else{
                    chain.doFilter(req, resp); 
                 }
                 
              }
           }

    }
    
    public void init(FilterConfig customedFilterConfig) throws ServletException {
        this.customedFilterConfig = customedFilterConfig;
    }
    
    public void destroy() {
        customedFilterConfig = null;
    }
}
