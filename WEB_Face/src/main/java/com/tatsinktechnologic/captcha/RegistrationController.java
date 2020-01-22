/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.captcha;

import com.tatsinktechnologic.bean.login.MyCaptcha;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author test
 */
@ManagedBean
@SessionScoped
public class RegistrationController {

    private String captchaUserInput;
    
    public RegistrationController() {
    }

    public String getCaptchaUserInput() {
        return captchaUserInput;
    }

    public void setCaptchaUserInput(String captchaUserInput) {
        this.captchaUserInput = captchaUserInput;
    }
    
    public String doRegisterBuyer(){
        HttpServletRequest request = (HttpServletRequest) FacesContext
            .getCurrentInstance().getExternalContext().getRequest();
            //Boolean isResponseCorrect = Boolean.FALSE;
            javax.servlet.http.HttpSession session = request.getSession();
            String parm = this.getCaptchaUserInput();
            String c = (String) session.getAttribute(MyCaptcha.CAPTCHA_KEY);
            if (parm.equals(c)) {
                 //CAPTCHA CORRECT INPUT
                return "success";
            }
            else {
                //CAPTCHA INCORRECT INPUT  
                return "failure";
            }

        //return "";
    }
    
}
