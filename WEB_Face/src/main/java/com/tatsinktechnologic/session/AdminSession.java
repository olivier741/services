/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.session;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;

/**
 *
 * @author olivier
 */
@SessionScoped
public class AdminSession implements Serializable {

    private boolean isLoggedIn = true;

    //avoid multiple redirects when redirecting user back to previous page after session expiration
    private boolean userRedirected = false;

    public boolean isLoggedIn(){
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public boolean isUserRedirected() {
        return userRedirected;
    }

    public void setUserRedirected(boolean userRedirected) {
        this.userRedirected = userRedirected;
    }
}

