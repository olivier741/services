package com.tatsinktechnologic.bean.initBean;



import com.tatsinktechnologic.dao_commun_controller.Commun_Controller;
import com.tatsinktechnologic.entities.account.Role;
import com.tatsinktechnologic.entities.account.User;
import com.tatsinktechnologic.entities.account.UserRoleRel;
import com.tatsinktechnologic.dao_controller.RoleJpaController;
import com.tatsinktechnologic.dao_controller.UserJpaController;
import com.tatsinktechnologic.dao_controller.UserRoleRelJpaController;
import com.tatsinktechnologic.utils.CredentialEncrypter;
import com.tatsinktechnologic.utils.PasswordGenerators;
import com.tatsinktechnologic.utils.TwoTuple;
import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Calendar;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;


@Singleton
@Startup
public class InitAppMB implements Serializable {

    @Inject
    private Commun_Controller commun_repo;
    
    @Inject
    private RoleJpaController role_controller;
    
    @Inject
    private UserJpaController user_controller;
    
    @Inject 
    private UserRoleRelJpaController userRole_controller;
  
    @PostConstruct
    public void init() {
      
            java.sql.Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
            TwoTuple<String, String> hashAndSalt = CredentialEncrypter.saltedHash("admin");
            
            User user = new User();
            user.setUsername("admin");
            user.setPassword("admin");
            user.setIslock(false);
            user.setIsReset(false);
            user.setPassword(hashAndSalt.t1);
            user.setSalt(hashAndSalt.t2);        
            user.setCreate_date(date);
           
            Role  role = new Role();
            role.setRoleName("root");

            try {
                Role tempRole = commun_repo.getOneByRole("root");
                User tempUser = commun_repo.getOneByUsername("admin");
                UserRoleRel tempUserRoleRel = commun_repo.getUserRoleRel("admin", "root");
             
                if (tempRole==null) {
                    role_controller.create(role);
                }
            
                if (tempUser==null) {
                    user_controller.create(user);
                }
 
                if (tempUserRoleRel==null){
                    
                    UserRoleRel userRole = new UserRoleRel();
                    tempRole = commun_repo.getOneByRole("root");
                    tempUser = commun_repo.getOneByUsername("admin");
                    
                    if(tempRole!=null) {
                        userRole.setRole(tempRole);
                    }else{
                       role.setRole_id(1);
                       userRole.setRole(role);
                    }
                    
                    if (tempUser != null){
                        userRole.setUser(tempUser);
                    }else{
                        user.setUser_id(1);
                        userRole.setUser(user);
                    }
               
                    userRole_controller.create(userRole);
                }

            } catch (Exception e) {
             
                e.printStackTrace();
            }
    }

}
