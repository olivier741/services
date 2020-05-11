/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.dao_commun_controller;

import com.tatsinktechnologic.entities.registration.Action;
import com.tatsinktechnologic.entities.registration.Command;
import com.tatsinktechnologic.entities.account.Contact;
import com.tatsinktechnologic.entities.account.Email;
import com.tatsinktechnologic.entities.registration.Notification_Conf;
import com.tatsinktechnologic.entities.registration.Parameter;
import com.tatsinktechnologic.entities.account.Permission;
import com.tatsinktechnologic.entities.account.Phone;
import com.tatsinktechnologic.entities.registration.Product;
import com.tatsinktechnologic.entities.registration.Promotion;
import com.tatsinktechnologic.entities.account.Role;
import com.tatsinktechnologic.entities.account.RolePermissionRel;
import com.tatsinktechnologic.entities.account.User;
import com.tatsinktechnologic.entities.account.UserContactRel;
import com.tatsinktechnologic.entities.account.UserRoleRel;
import com.tatsinktechnologic.dao_controller.exceptions.NonexistentEntityException;
import com.tatsinktechnologic.dao_controller.exceptions.RollbackFailureException;
import com.tatsinktechnologic.entities.account.RolePermissionRel_;
import com.tatsinktechnologic.entities.api_gateway.WS_AccessManagement;
import com.tatsinktechnologic.entities.api_gateway.WS_Block_API;
import com.tatsinktechnologic.entities.api_gateway.WS_Block_WebserviceRel;
import com.tatsinktechnologic.entities.api_gateway.WS_Client;
import com.tatsinktechnologic.entities.api_gateway.WS_Header_Param;
import com.tatsinktechnologic.entities.api_gateway.WS_Webservice;
import com.tatsinktechnologic.entities.chat_sms.ChatGroup;
import com.tatsinktechnologic.entities.notification_service.ContentMessage;
import com.tatsinktechnologic.entities.registration.Promotion_Table;
import com.tatsinktechnologic.entities.registration.Service;
import com.tatsinktechnologic.persistence.EntityManagerFactoryBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

/**
 *
 * @author olivier.tatsinkou
 */
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class Commun_Controller extends EntityManagerFactoryBean implements Serializable {

    private static HashMap<String, Set<String>> SET_ROLE_USERNAME = new HashMap<String, Set<String>>();
    private static HashMap<String, Set<String>> SET_PERM_ROLENAME = new HashMap<String, Set<String>>();
    private static HashMap<String, Set<String>> SET_PERM_USERNAME = new HashMap<String, Set<String>>();
    
    private static HashMap<String, User> SET_USER_USERNAME = new HashMap<String, User>();

    @Resource
    private UserTransaction utx;

    public static HashMap<String, Set<String>> getSET_ROLE_USERNAME() {
        return SET_ROLE_USERNAME;
    }

    public static HashMap<String, Set<String>> getSET_PERM_ROLENAME() {
        return SET_PERM_ROLENAME;
    }

    public static HashMap<String, Set<String>> getSET_PERM_USERNAME() {
        return SET_PERM_USERNAME;
    }

    public static HashMap<String, User> getSET_USER_USERNAME() {
        return SET_USER_USERNAME;
    }
    
    
     public void LoadUserByUsername() {
         
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<User> criteria = builder.createQuery(User.class);
            Root<User> entityRoot = criteria.from(User.class);
            criteria.select(entityRoot);
            Query query = em.createQuery(criteria);

            List<User> listEntity = query.getResultList();

            SET_USER_USERNAME.clear();
            for (User user :listEntity ){
                SET_USER_USERNAME.put(user.getUsername(), user);
            }
           
        } finally {
            em.close();
        }
    }

    public void loadRolesPermByUsername() {

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<RolePermissionRel> criteria_rolperm = builder.createQuery(RolePermissionRel.class);
            Root<RolePermissionRel> entityRoot_rolperm = criteria_rolperm.from(RolePermissionRel.class);
            criteria_rolperm.select(entityRoot_rolperm);

            Query query_rolperm = em.createQuery(criteria_rolperm);

            List<RolePermissionRel> listEntity_rolperm = query_rolperm.getResultList();

            SET_PERM_ROLENAME.clear();
            for (RolePermissionRel rolePerm : listEntity_rolperm) {
                String perm_name = rolePerm.getPermission().getPermissionStr();
                String role_name = rolePerm.getRole().getRoleName();
                if (SET_PERM_ROLENAME.containsKey(role_name)) {
                    SET_PERM_ROLENAME.get(role_name).add(perm_name);
                } else {
                    Set<String> set_perm = new HashSet<>();
                    set_perm.add(perm_name);
                    SET_PERM_ROLENAME.put(role_name, set_perm);
                }

            }

            CriteriaQuery<UserRoleRel> criteria = builder.createQuery(UserRoleRel.class);
            Root<UserRoleRel> entityRoot = criteria.from(UserRoleRel.class);
            criteria.select(entityRoot);
            Query query = em.createQuery(criteria);

            List<UserRoleRel> listUserRole = query.getResultList();

            SET_ROLE_USERNAME.clear();
            for (UserRoleRel user_role : listUserRole) {
                String user_name = user_role.getUser().getUsername();
                String role_name = user_role.getRole().getRoleName();
                if (SET_ROLE_USERNAME.containsKey(user_name)) {
                    SET_ROLE_USERNAME.get(user_name).add(role_name);
                } else {
                    Set<String> set_role = new HashSet<>();
                    set_role.add(role_name);
                    SET_ROLE_USERNAME.put(user_name, set_role);
                }
            }

            SET_PERM_USERNAME.clear();
            for (Map.Entry<String, Set<String>> variable : SET_ROLE_USERNAME.entrySet()) {
                String user_name = variable.getKey();
                Set<String> set_role = variable.getValue();
                Set<String> set_perm = new HashSet<String>();

                for (String role_name : set_role) {
                    if (SET_PERM_ROLENAME.containsKey(role_name)) {
                        set_perm.addAll(SET_PERM_ROLENAME.get(role_name));
                    }
                }

                if (set_perm != null && !set_perm.isEmpty()) {
                    SET_PERM_USERNAME.put(user_name, set_perm);
                }

            }

        } finally {
            em.close();
        }
    }

    public boolean checkPromotionTable(String table_name, String msisdn) {
        boolean result = false;

        EntityManager em = emf.createEntityManager();
        try {

            Query sqlQuery = em.createNativeQuery("Select * from " + table_name + " where msisdn = ?", Promotion_Table.class);
            List<Promotion_Table> listEntity = sqlQuery.setParameter(1, msisdn).getResultList();

            if (listEntity != null && !listEntity.isEmpty()) {
                result = true;
            }

        } finally {
            em.close();
        }

        return result;
    }

    public Promotion getOneByPromotion(String promotion_name) {
        Promotion result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Promotion> criteria = builder.createQuery(Promotion.class);
            Root<Promotion> entityRoot = criteria.from(Promotion.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("promotion_name"), promotion_name));
            Query query = em.createQuery(criteria);

            List<Promotion> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public Permission getOneByPermission(String resource) {
        Permission result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Permission> criteria = builder.createQuery(Permission.class);
            Root<Permission> entityRoot = criteria.from(Permission.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("permissionStr"), resource));
            Query query = em.createQuery(criteria);

            List<Permission> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public Role getOneByRole(String role_name) {
        Role result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
            Root<Role> entityRoot = criteria.from(Role.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("roleName"), role_name));
            Query query = em.createQuery(criteria);

            List<Role> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

   

    public Contact getOneByContact(String contact_name) {
        Contact result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Contact> criteria = builder.createQuery(Contact.class);
            Root<Contact> entityRoot = criteria.from(Contact.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("contact_name"), contact_name));
            Query query = em.createQuery(criteria);

            List<Contact> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public Phone getOneByPhone(String phone_name) {
        Phone result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Phone> criteria = builder.createQuery(Phone.class);
            Root<Phone> entityRoot = criteria.from(Phone.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("phone_number"), phone_name));
            Query query = em.createQuery(criteria);

            List<Phone> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public Email getOneByEmail(String email) {
        Email result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Email> criteria = builder.createQuery(Email.class);
            Root<Email> entityRoot = criteria.from(Email.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("email"), email));
            Query query = em.createQuery(criteria);

            List<Email> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public Service getOneByService(String service_name) {
        Service result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Service> criteria = builder.createQuery(Service.class);
            Root<Service> entityRoot = criteria.from(Service.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("service_name"), service_name));
            Query query = em.createQuery(criteria);

            List<Service> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public Product getOneByProduct(String product_code) {
        Product result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
            Root<Product> entityRoot = criteria.from(Product.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("product_code"), product_code));
            Query query = em.createQuery(criteria);

            List<Product> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public Command getOneByCommand(String command_name) {
        Command result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Command> criteria = builder.createQuery(Command.class);
            Root<Command> entityRoot = criteria.from(Command.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("command_name"), command_name));
            Query query = em.createQuery(criteria);

            List<Command> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public Action getOneByAction(String action_name) {
        Action result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Action> criteria = builder.createQuery(Action.class);
            Root<Action> entityRoot = criteria.from(Action.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("action_name"), action_name));
            Query query = em.createQuery(criteria);

            List<Action> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public Parameter getOneByParameter(String param_name) {
        Parameter result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Parameter> criteria = builder.createQuery(Parameter.class);
            Root<Parameter> entityRoot = criteria.from(Parameter.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("param_name"), param_name));
            Query query = em.createQuery(criteria);

            List<Parameter> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public ContentMessage getOneByContent(String content_label) {
        ContentMessage result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<ContentMessage> criteria = builder.createQuery(ContentMessage.class);
            Root<ContentMessage> entityRoot = criteria.from(ContentMessage.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("content_label"), content_label));
            Query query = em.createQuery(criteria);

            List<ContentMessage> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public Notification_Conf getOneByNotification_Conf(String nofication_name) {

        Notification_Conf result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Notification_Conf> criteria = builder.createQuery(Notification_Conf.class);
            Root<Notification_Conf> entityRoot = criteria.from(Notification_Conf.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("nofication_name"), nofication_name));
            Query query = em.createQuery(criteria);

            List<Notification_Conf> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public WS_Client getOneByWS_Client(String client_name) {
        WS_Client result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<WS_Client> criteria = builder.createQuery(WS_Client.class);
            Root<WS_Client> entityRoot = criteria.from(WS_Client.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("client_name"), client_name));
            Query query = em.createQuery(criteria);

            List<WS_Client> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public WS_Webservice getOneByWS_Webservice(String webservice_name) {
        WS_Webservice result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<WS_Webservice> criteria = builder.createQuery(WS_Webservice.class);
            Root<WS_Webservice> entityRoot = criteria.from(WS_Webservice.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("webservice_name"), webservice_name));
            Query query = em.createQuery(criteria);

            List<WS_Webservice> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public WS_AccessManagement getOneByWS_AccessManagement(String access_name) {
        WS_AccessManagement result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<WS_AccessManagement> criteria = builder.createQuery(WS_AccessManagement.class);
            Root<WS_AccessManagement> entityRoot = criteria.from(WS_AccessManagement.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("access_name"), access_name));
            Query query = em.createQuery(criteria);

            List<WS_AccessManagement> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public WS_Block_API getOneByBockname(String block_name) {
        WS_Block_API result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<WS_Block_API> criteria = builder.createQuery(WS_Block_API.class);
            Root<WS_Block_API> entityRoot = criteria.from(WS_Block_API.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("block_api_name"), block_name));
            Query query = em.createQuery(criteria);

            List<WS_Block_API> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public ChatGroup getOneByChatGroup(String chatGrp_channel) {
        ChatGroup result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<ChatGroup> criteria = builder.createQuery(ChatGroup.class);
            Root<ChatGroup> entityRoot = criteria.from(ChatGroup.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("group_channel"), chatGrp_channel));
            Query query = em.createQuery(criteria);

            List<ChatGroup> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public UserRoleRel getUserRoleRel(String user_name, String role_name) {
        UserRoleRel result = null;
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<UserRoleRel> criteria = builder.createQuery(UserRoleRel.class);
            Root<UserRoleRel> entityRoot = criteria.from(UserRoleRel.class);
            criteria.select(entityRoot);
            criteria.where(builder.and(builder.equal(entityRoot.get("user").get("username"), user_name),
                    builder.equal(entityRoot.get("role").get("roleName"), role_name)
            )
            );
            Query query = em.createQuery(criteria);

            List<UserRoleRel> listEntity = query.getResultList();

            if (listEntity != null && listEntity.size() != 0) {
                result = listEntity.get(0);
            }
        } finally {
            em.close();
        }

        return result;
    }

    public List<UserRoleRel> getUserRolebyUser(String user_name) {
        List<UserRoleRel> result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<UserRoleRel> criteria = builder.createQuery(UserRoleRel.class);
            Root<UserRoleRel> entityRoot = criteria.from(UserRoleRel.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("user").get("username"), user_name)
            );
            Query query = em.createQuery(criteria);

            result = query.getResultList();

        } finally {
            em.close();
        }

        return result;
    }

    public List<Permission> getPermissionByRole(String role_name) {
        List<Permission> result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<RolePermissionRel> criteria = builder.createQuery(RolePermissionRel.class);
            Root<RolePermissionRel> entityRoot = criteria.from(RolePermissionRel.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("role").get("roleName"), role_name)
            );
            Query query = em.createQuery(criteria);

            List<RolePermissionRel> listEntity = query.getResultList();
            result = new ArrayList<>();

            for (RolePermissionRel perm_role : listEntity) {
                if (perm_role.getPermission() != null) {
                    result.add(perm_role.getPermission());
                }
            }

        } finally {
            em.close();
        }

        return result;
    }

    public List<Role> getRolesByUsername(String user_name) {
        List<Role> result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<UserRoleRel> criteria = builder.createQuery(UserRoleRel.class);
            Root<UserRoleRel> entityRoot = criteria.from(UserRoleRel.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("user").get("username"), user_name)
            );
            Query query = em.createQuery(criteria);

            List<UserRoleRel> listEntity = query.getResultList();
            result = new ArrayList<>();

            for (UserRoleRel user_role : listEntity) {
                if (user_role.getRole() != null) {
                    result.add(user_role.getRole());
                }
            }

        } finally {
            em.close();
        }

        return result;
    }

    public List<Contact> getContactByUsername(String user_name) {
        List<Contact> result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<UserContactRel> criteria = builder.createQuery(UserContactRel.class);
            Root<UserContactRel> entityRoot = criteria.from(UserContactRel.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("user").get("username"), user_name)
            );
            Query query = em.createQuery(criteria);

            List<UserContactRel> listEntity = query.getResultList();
            result = new ArrayList<>();

            for (UserContactRel user_contact : listEntity) {
                if (user_contact.getContact() != null) {
                    result.add(user_contact.getContact());
                }
            }

        } finally {
            em.close();
        }

        return result;
    }

    public List<WS_Webservice> getwebserviceByBlockname(String block_api_name) {
        List<WS_Webservice> result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<WS_Block_WebserviceRel> criteria = builder.createQuery(WS_Block_WebserviceRel.class);
            Root<WS_Block_WebserviceRel> entityRoot = criteria.from(WS_Block_WebserviceRel.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("ws_block_api").get("block_api_name"), block_api_name)
            );
            Query query = em.createQuery(criteria);

            List<WS_Block_WebserviceRel> listEntity = query.getResultList();
            result = new ArrayList<>();

            for (WS_Block_WebserviceRel block_websrv : listEntity) {
                if (block_websrv.getWs_webservice() != null) {
                    result.add(block_websrv.getWs_webservice());
                }
            }

        } finally {
            em.close();
        }

        return result;
    }

    public List<Phone> getPhoneByContact(String contact_name) {
        List<Phone> result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Phone> criteria = builder.createQuery(Phone.class);
            Root<Phone> entityRoot = criteria.from(Phone.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("contact").get("contact_name"), contact_name)
            );
            Query query = em.createQuery(criteria);

            result = query.getResultList();

        } finally {
            em.close();
        }

        return result;
    }

    public List<Email> getEmailByContact(String contact_name) {
        List<Email> result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Email> criteria = builder.createQuery(Email.class);
            Root<Email> entityRoot = criteria.from(Email.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("contact").get("contact_name"), contact_name)
            );
            Query query = em.createQuery(criteria);

            result = query.getResultList();

        } finally {
            em.close();
        }

        return result;
    }

    public List<WS_Header_Param> getHeader_ParamByWebservice(String webservice_name) {
        List<WS_Header_Param> result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<WS_Header_Param> criteria = builder.createQuery(WS_Header_Param.class);
            Root<WS_Header_Param> entityRoot = criteria.from(WS_Header_Param.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("ws_webservice").get("webservice_name"), webservice_name)
            );
            Query query = em.createQuery(criteria);

            result = query.getResultList();

        } finally {
            em.close();
        }

        return result;
    }

//  
//  public List<User> getUserByChatGroup(String chatgrp_channel) {
//        List<User> result = null;
//
//        EntityManager em = emf.createEntityManager();
//        try {
//            CriteriaBuilder builder = em.getCriteriaBuilder();
//            CriteriaQuery<ChatGroup> criteria = builder.createQuery(ChatGroup.class);
//            Root<ChatGroup> entityRoot = criteria.from(ChatGroup.class);
//            criteria.select(entityRoot);
//            criteria.where(builder.equal(entityRoot.get("group_channel"), chatgrp_channel)
//            );
//            Query query = em.createQuery(criteria);
//
//            List<ChatGroup> listEntity = query.getResultList();
//            result = new ArrayList<>();
//
//            for (ChatGroup chatGrp : listEntity) {
//                if (chatGrp.getUser() != null) {
//                    result.add(chatGrp.getUser());
//                }
//            }
//
//        } finally {
//            em.close();
//        }
//
//        return result;
//    }
    public Action getActionByCommand(String command_name) {
        Action result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Command> criteria = builder.createQuery(Command.class);
            Root<Command> entityRoot = criteria.from(Command.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("command_code"), command_name));
            Query query = em.createQuery(criteria);

            Command entity = (Command) query.getSingleResult();
            if (entity != null && entity.getParameter() != null) {
                result = entity.getAction();
            }

        } finally {
            em.close();
        }

        return result;
    }

    public Parameter getParameterByCommand(String command_name) {
        Parameter result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Command> criteria = builder.createQuery(Command.class);
            Root<Command> entityRoot = criteria.from(Command.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("command_code"), command_name));
            Query query = em.createQuery(criteria);

            Command entity = (Command) query.getSingleResult();
            if (entity != null && entity.getParameter() != null) {
                result = entity.getParameter();
            }

        } finally {
            em.close();
        }

        return result;
    }

    public Service getServiceByProduct(String product_code) {
        Service result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
            Root<Product> entityRoot = criteria.from(Product.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("product_code"), product_code)
            );
            Query query = em.createQuery(criteria);

            List<Product> listentity = query.getResultList();
            if (listentity != null && !listentity.isEmpty()) {
                result = listentity.get(0).getService();
            }

        } finally {
            em.close();
        }

        return result;
    }

    public Service getServiceByContent(String content_label) {
        Service result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<ContentMessage> criteria = builder.createQuery(ContentMessage.class);
            Root<ContentMessage> entityRoot = criteria.from(ContentMessage.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("content_label"), content_label)
            );
            Query query = em.createQuery(criteria);

            List<ContentMessage> listentity = query.getResultList();
            if (listentity != null && !listentity.isEmpty()) {
                result = listentity.get(0).getService();
            }

        } finally {
            em.close();
        }

        return result;
    }

    public Promotion getPromotionByProduct(String product_code) {
        Promotion result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
            Root<Product> entityRoot = criteria.from(Product.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("product_code"), product_code)
            );
            Query query = em.createQuery(criteria);

            Product entity = (Product) query.getSingleResult();
            if (entity != null && entity.getPromotion() != null) {
                result = entity.getPromotion();
            }

        } finally {
            em.close();
        }

        return result;
    }

    public Product getProductByAction(String action_name) {
        Product result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Action> criteria = builder.createQuery(Action.class);
            Root<Action> entityRoot = criteria.from(Action.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("action_name"), action_name)
            );
            Query query = em.createQuery(criteria);

            Action entity = (Action) query.getSingleResult();
            if (entity != null && entity.getProduct() != null) {
                result = entity.getProduct();
            }

        } finally {
            em.close();
        }

        return result;
    }

    public Command getCommandByNotification_Conf(String notif_name) {
        Command result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Notification_Conf> criteria = builder.createQuery(Notification_Conf.class);
            Root<Notification_Conf> entityRoot = criteria.from(Notification_Conf.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("nofication_name"), notif_name)
            );
            Query query = em.createQuery(criteria);

            Notification_Conf entity = (Notification_Conf) query.getSingleResult();
            if (entity != null && entity.getCommand() != null) {
                result = entity.getCommand();
            }

        } finally {
            em.close();
        }

        return result;
    }

    public WS_Client getWS_ClientByWS_AccessManagement(String access_name) {
        WS_Client result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<WS_AccessManagement> criteria = builder.createQuery(WS_AccessManagement.class);
            Root<WS_AccessManagement> entityRoot = criteria.from(WS_AccessManagement.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("access_name"), access_name));
            Query query = em.createQuery(criteria);

            WS_AccessManagement entity = (WS_AccessManagement) query.getSingleResult();
            if (entity != null && entity.getWs_client() != null) {
                result = entity.getWs_client();
            }

        } finally {
            em.close();
        }

        return result;
    }

    public WS_Block_API getWS_Block_APIByWS_AccessManagement(String access_name) {
        WS_Block_API result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<WS_AccessManagement> criteria = builder.createQuery(WS_AccessManagement.class);
            Root<WS_AccessManagement> entityRoot = criteria.from(WS_AccessManagement.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("access_name"), access_name));
            Query query = em.createQuery(criteria);

            WS_AccessManagement entity = (WS_AccessManagement) query.getSingleResult();
            if (entity != null && entity.getWs_block_api() != null) {
                result = entity.getWs_block_api();
            }

        } finally {
            em.close();
        }

        return result;
    }

    public User getUserByChatGroup(String chatGrp_channel) {
        User result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<ChatGroup> criteria = builder.createQuery(ChatGroup.class);
            Root<ChatGroup> entityRoot = criteria.from(ChatGroup.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("group_channel"), chatGrp_channel));
            Query query = em.createQuery(criteria);

            ChatGroup entity = (ChatGroup) query.getSingleResult();
            if (entity != null && entity.getUser() != null) {
                result = entity.getUser();
            }

        } finally {
            em.close();
        }

        return result;
    }

    public ChatGroup getChatGroupByUser(String user_name) {
        ChatGroup result = null;

        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<ChatGroup> criteria = builder.createQuery(ChatGroup.class);
            Root<ChatGroup> entityRoot = criteria.from(ChatGroup.class);
            criteria.select(entityRoot);
            criteria.where(builder.equal(entityRoot.get("user").get("username"), user_name));
            Query query = em.createQuery(criteria);

            List<ChatGroup> listentity = query.getResultList();
            if (listentity != null && !listentity.isEmpty()) {
                result = listentity.get(0);
            }

        } finally {
            em.close();
        }

        return result;
    }

    public void deleteRolePermissionRelByRole(String role_name) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        Role role = getOneByRole(role_name);
        try {

            utx.begin();
            em = emf.createEntityManager();
            CriteriaBuilder builder = em.getCriteriaBuilder();

            // create delete
            CriteriaDelete<RolePermissionRel> deleteCriteria = builder.createCriteriaDelete(RolePermissionRel.class);
            // set the root class
            Root entityRoot = deleteCriteria.from(RolePermissionRel.class);
            // set where clause

            deleteCriteria.where(builder.equal(entityRoot.get("role"), role));
            // perform update
            em.createQuery(deleteCriteria).executeUpdate();

            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void deleteUserRoleRelByUser(String user_name) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        User user = SET_USER_USERNAME.get(user_name);
        try {

            utx.begin();
            em = emf.createEntityManager();
            CriteriaBuilder builder = em.getCriteriaBuilder();

            // create delete
            CriteriaDelete<UserRoleRel> deleteCriteria = builder.createCriteriaDelete(UserRoleRel.class);
            // set the root class
            Root entityRoot = deleteCriteria.from(UserRoleRel.class);
            // set where clause

            deleteCriteria.where(builder.equal(entityRoot.get("user"), user));
            // perform update
            em.createQuery(deleteCriteria).executeUpdate();

            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void deleteBockWebserviceRelByBlock_API(String block_name) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        WS_Block_API block_api = getOneByBockname(block_name);
        try {

            utx.begin();
            em = emf.createEntityManager();
            CriteriaBuilder builder = em.getCriteriaBuilder();

            // create delete
            CriteriaDelete<WS_Block_WebserviceRel> deleteCriteria = builder.createCriteriaDelete(WS_Block_WebserviceRel.class);
            // set the root class
            Root entityRoot = deleteCriteria.from(WS_Block_WebserviceRel.class);
            // set where clause

            deleteCriteria.where(builder.equal(entityRoot.get("ws_block_api"), block_api));
            // perform update
            em.createQuery(deleteCriteria).executeUpdate();

            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void deleteUserContactRelByUser(String user_name) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        User user = SET_USER_USERNAME.get(user_name);
        try {

            utx.begin();
            em = emf.createEntityManager();
            CriteriaBuilder builder = em.getCriteriaBuilder();

            // create delete
            CriteriaDelete<UserContactRel> deleteCriteria = builder.createCriteriaDelete(UserContactRel.class);
            // set the root class
            Root entityRoot = deleteCriteria.from(UserContactRel.class);
            // set where clause

            deleteCriteria.where(builder.equal(entityRoot.get("user"), user));
            // perform update
            em.createQuery(deleteCriteria).executeUpdate();

            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void deletePhoneByContact(String contact_name) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        Contact contact = getOneByContact(contact_name);
        try {

            utx.begin();
            em = emf.createEntityManager();
            CriteriaBuilder builder = em.getCriteriaBuilder();

            // create delete
            CriteriaDelete<Phone> deleteCriteria = builder.createCriteriaDelete(Phone.class);
            // set the root class
            Root entityRoot = deleteCriteria.from(Phone.class);
            // set where clause

            deleteCriteria.where(builder.equal(entityRoot.get("contact"), contact));
            // perform update
            em.createQuery(deleteCriteria).executeUpdate();

            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void deleteEmailByContact(String contact_name) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        Contact contact = getOneByContact(contact_name);
        try {

            utx.begin();
            em = emf.createEntityManager();
            CriteriaBuilder builder = em.getCriteriaBuilder();

            // create delete
            CriteriaDelete<Email> deleteCriteria = builder.createCriteriaDelete(Email.class);
            // set the root class
            Root entityRoot = deleteCriteria.from(Email.class);
            // set where clause

            deleteCriteria.where(builder.equal(entityRoot.get("contact"), contact));
            // perform update
            em.createQuery(deleteCriteria).executeUpdate();

            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void deleteHeaderParamByWebservice(String webservice_name) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        WS_Webservice websrv = getOneByWS_Webservice(webservice_name);
        try {

            utx.begin();
            em = emf.createEntityManager();
            CriteriaBuilder builder = em.getCriteriaBuilder();

            // create delete
            CriteriaDelete<Phone> deleteCriteria = builder.createCriteriaDelete(Phone.class);
            // set the root class
            Root entityRoot = deleteCriteria.from(Phone.class);
            // set where clause

            deleteCriteria.where(builder.equal(entityRoot.get("ws_webservice"), websrv));
            // perform update
            em.createQuery(deleteCriteria).executeUpdate();

            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

}
