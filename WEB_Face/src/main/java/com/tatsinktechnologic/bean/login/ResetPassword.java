/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.login;


import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.account.User;
import com.tatsinktechnologic.dao_controller.UserJpaController;
import com.tatsinktechnologic.utils.CredentialEncrypter;
import com.tatsinktechnologic.utils.PasswordGenerators;
import com.tatsinktechnologic.utils.TwoTuple;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.omnifaces.util.Faces;

/**
 *
 * @author olivier.tatsinkou
 */
@Named
@SessionScoped
public class ResetPassword implements Serializable{
  
    private String username;
    private String password;
    private String current_password;
    private String changed_password;
    private UIInput changed_UIpassword;
    private String conf_password;
    private String captchaUserInput;
    
    
    @Inject
    private Commun_Controller commun_controller;
    
    @Inject
    private UserJpaController user_controller;

    private static final Logger log = Logger.getLogger(ResetPassword.class.getName());

    public void reset_login(String login) throws IOException {
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest origRequest = (HttpServletRequest)facesContext.getExternalContext().getRequest();
        String contextPath = origRequest.getContextPath();
        
        User user = commun_controller.getSET_USER_USERNAME().get(login);
        try {
            
            TwoTuple<String, String> hashAndSalt = CredentialEncrypter.saltedHash(changed_password);
            java.sql.Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
            user.setIsReset(false);
            user.setPassword(hashAndSalt.t1);
            user.setSalt(hashAndSalt.t2);
            user.setLast_update(date);
            
             user_controller.edit(user);          
             FacesMessage facesMessage = new FacesMessage( login + " : You have Success Change password");
             facesContext.addMessage(null, facesMessage);
        } catch (Exception e) {
            
            FacesMessage facesMessage = new FacesMessage( login + " : You have failed to change Default password");
            facesContext.addMessage(null, facesMessage);
        }
         FacesMessage facesMessage = new FacesMessage("YOU ARE SUCCESS RESET YOUR PASSWORD. YOU MUST LOGIN WITH NEW PASSWORD" );
         facesContext.addMessage(null, facesMessage);
         Faces.getExternalContext().getFlash().setKeepMessages(true);
         Faces.redirect(contextPath+"/Login/login.xhtml");
         SecurityUtils.getSubject().logout();
        
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

  
    public String getCurrent_password() {
        return current_password;
    }

    public void setCurrent_password(String current_password) {
        this.current_password = current_password;
    }

    public String getChanged_password() {
        return changed_password;
    }

    public void setChanged_password(String changed_password) {
        this.changed_password = changed_password;
    }

    public UIInput getChanged_UIpassword() {
        return changed_UIpassword;
    }

    public void setChanged_UIpassword(UIInput changed_UIpassword) {
        this.changed_UIpassword = changed_UIpassword;
    }

    public String getConf_password() {
        return conf_password;
    }

    public void setConf_password(String conf_password) {
        this.conf_password = conf_password;
    }

    public String getCaptchaUserInput() {
        return captchaUserInput;
    }

    public void setCaptchaUserInput(String captchaUserInput) {
        this.captchaUserInput = captchaUserInput;
    }
    
    

    public String getCurrentUser() {
       
        return SecurityUtils.getSubject().getPrincipal().toString();
    }
    
    public Subject getSubject() {
        return SecurityUtils.getSubject();
    }
    
    
    
    public void validateCurPass(FacesContext context, UIComponent component, Object value)  throws ValidatorException{ 
        String curPass = value.toString();
        String id = (String) component.getAttributes().get("username");

        if (id !=null){
             User user = commun_controller.getSET_USER_USERNAME().get(id);

            if (user!=null){

                String curPassEncrypt = new Sha256Hash(curPass, Base64.decode(user.getSalt()), 1024).toBase64();

                if (!user.getPassword().equals(curPassEncrypt)) {
                    System.out.println("encrypt current pass :"+curPass + "database value ="+user.getPassword());
                    FacesMessage errorMessage = new FacesMessage("Current password is not correct.");
                    throw new ValidatorException(errorMessage);
                }
            }else {
                String message = context.getApplication().evaluateExpressionGet(context, "Don't have User with loging "+id, String.class);
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
                throw new ValidatorException(msg);
            }
        }else{
             FacesMessage errorMessage = new FacesMessage("Login provide to system is Null");
             throw new ValidatorException(errorMessage);
        }
       
       
    }
    
   public void validateNewPass(FacesContext context, UIComponent component, Object value) throws ValidatorException{

        PasswordGenerators passGen = new PasswordGenerators();
        
        changed_password = changed_UIpassword.getLocalValue().toString();
        conf_password = value.toString();
        System.out.println("1 password : "+ changed_password +" confirmation : "+conf_password);
        if (!changed_password.equals(conf_password)) {
            System.out.println("2 password : "+ changed_password +" confirmation : "+conf_password);
            FacesMessage errorMessage = new FacesMessage("New passwords and Confirmation password must match.");
            throw new ValidatorException(errorMessage);
        }else if (!passGen.isPasswordValid(changed_password) && !passGen.isPasswordValid(conf_password)){
           System.out.println("3 password : "+ changed_password +" confirmation : "+conf_password);
           FacesMessage errorMessage = new FacesMessage(passGen.getPassword_Message().toString());
           throw new ValidatorException(errorMessage); 
        }
    }


}
