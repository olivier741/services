/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.login;

import java.io.IOException;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
import org.omnifaces.util.Faces;

/**
 *
 * @author olivier.tatsinkou
 */

@Named
@RequestScoped
public class Logout {
    public static final String HOME_URL = "/Login/login.xhtml";

    public void submit() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest origRequest = (HttpServletRequest)facesContext.getExternalContext().getRequest();
        String contextPath = origRequest.getContextPath();
        
        SecurityUtils.getSubject().logout();
        Faces.invalidateSession();
        Faces.getExternalContext().getFlash().setKeepMessages(true);
        Faces.redirect(contextPath+HOME_URL);
    }


}
