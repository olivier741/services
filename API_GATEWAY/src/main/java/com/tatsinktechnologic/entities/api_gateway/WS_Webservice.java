/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entities.api_gateway;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


/**
 *
 * @author olivier
 */
@Entity
@Table(name = "ws_webservice")
public class WS_Webservice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long ws_webservice_id;

    @Column(nullable = false, unique = true)
    private String webservice_name;
    
    @Column(nullable = false)
    private String api_url;
     
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type_API webservice_type;
   
    @Column(nullable = true)
    private String api_rest_uri;
    
    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private Rest_Method api_rest_method;
    
    @Column(nullable = true)
    private String rest_media_type;
    
    @Column(nullable = true)
    private String content_type;
    
    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private Bind_Parameter rest_bind_param;
    
    @Column(nullable = true)
    @Lob 
    private String api_rest_request;
    
    @Column(nullable = true)
    @Lob 
    private String api_soap_wsdl;
    
    @Column(nullable = true)
    @Lob 
    private String api_soap_xsd;
    
    @Column(nullable = true)
    @Lob
    private String api_soap_request;  
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private API_Function api_function;

    @Column(nullable = true)
    private String remote_operator; 
    
    @Column(nullable = true)
    private int remote_con_timeout; 
    
    @Column(nullable = true)
    private int remote_req_timeout; 
    
    @Column(nullable = true)
    private int remote_max_connexion; 
    
    @Column(nullable = true)
    private long remote_max_request;
    
    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private Security_Mode remote_secutity_mode;
    
        
    @Column(nullable = true)
    private String security_login; 
    
    @Column(nullable = true)
    private String security_password; 
   
    @Column(nullable = true)
    private String authorizationType;
     
    @Column(nullable = true)
    private String token; 
    
    @Column(nullable = true)
    private Timestamp expire_time_token; 


    @Column(nullable = true)
    private String description;
    
    @Column(nullable = true)
    @CreationTimestamp
    private Timestamp create_time;
    
    @Column(nullable = true)
    @UpdateTimestamp
    private Timestamp update_time;
    
   @OneToMany(mappedBy = "ws_webservice")
   private Set<WS_Block_WebserviceRel> listWS_Block_WebserviceRel = new HashSet<>();
   
   @OneToMany(mappedBy = "ws_webservice")
    protected Set<WS_Header_Param> listWS_Header_Param = new HashSet<>();

    public Long getWs_webservice_id() {
        return ws_webservice_id;
    }

    public void setWs_webservice_id(Long ws_webservice_id) {
        this.ws_webservice_id = ws_webservice_id;
    }

    public String getWebservice_name() {
        return webservice_name;
    }

    public void setWebservice_name(String webservice_name) {
        this.webservice_name = webservice_name;
    }

    public String getApi_url() {
        return api_url;
    }

    public void setApi_url(String api_url) {
        this.api_url = api_url;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }
    
    

    public Type_API getWebservice_type() {
        return webservice_type;
    }

    public void setWebservice_type(Type_API webservice_type) {
        this.webservice_type = webservice_type;
    }

    public String getApi_rest_uri() {
        return api_rest_uri;
    }

    public void setApi_rest_uri(String api_rest_uri) {
        this.api_rest_uri = api_rest_uri;
    }

    public Rest_Method getApi_rest_method() {
        return api_rest_method;
    }

    public void setApi_rest_method(Rest_Method api_rest_method) {
        this.api_rest_method = api_rest_method;
    }

    public String getRest_media_type() {
        return rest_media_type;
    }

    public void setRest_media_type(String rest_media_type) {
        this.rest_media_type = rest_media_type;
    }

    public Bind_Parameter getRest_bind_param() {
        return rest_bind_param;
    }

    public void setRest_bind_param(Bind_Parameter rest_bind_param) {
        this.rest_bind_param = rest_bind_param;
    }

    public String getApi_rest_request() {
        return api_rest_request;
    }

    public void setApi_rest_request(String api_rest_request) {
        this.api_rest_request = api_rest_request;
    }

    public String getApi_soap_wsdl() {
        return api_soap_wsdl;
    }

    public void setApi_soap_wsdl(String api_soap_wsdl) {
        this.api_soap_wsdl = api_soap_wsdl;
    }

    public String getApi_soap_xsd() {
        return api_soap_xsd;
    }

    public void setApi_soap_xsd(String api_soap_xsd) {
        this.api_soap_xsd = api_soap_xsd;
    }

    public String getApi_soap_request() {
        return api_soap_request;
    }

    public void setApi_soap_request(String api_soap_request) {
        this.api_soap_request = api_soap_request;
    }

    public API_Function getApi_function() {
        return api_function;
    }

    public void setApi_function(API_Function api_function) {
        this.api_function = api_function;
    }

    public String getRemote_operator() {
        return remote_operator;
    }

    public void setRemote_operator(String remote_operator) {
        this.remote_operator = remote_operator;
    }

    public int getRemote_max_connexion() {
        return remote_max_connexion;
    }

    public void setRemote_max_connexion(int remote_max_connexion) {
        this.remote_max_connexion = remote_max_connexion;
    }

    public long getRemote_max_request() {
        return remote_max_request;
    }

    public void setRemote_max_request(long remote_max_request) {
        this.remote_max_request = remote_max_request;
    }

    public Security_Mode getRemote_secutity_mode() {
        return remote_secutity_mode;
    }

    public void setRemote_secutity_mode(Security_Mode remote_secutity_mode) {
        this.remote_secutity_mode = remote_secutity_mode;
    }

    public String getSecurity_login() {
        return security_login;
    }

    public void setSecurity_login(String security_login) {
        this.security_login = security_login;
    }

    public String getSecurity_password() {
        return security_password;
    }

    public void setSecurity_password(String security_password) {
        this.security_password = security_password;
    }

    public String getAuthorizationType() {
        return authorizationType;
    }

    public void setAuthorizationType(String authorizationType) {
        this.authorizationType = authorizationType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Timestamp getExpire_time_token() {
        return expire_time_token;
    }

    public void setExpire_time_token(Timestamp expire_time_token) {
        this.expire_time_token = expire_time_token;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

    public Set<WS_Block_WebserviceRel> getListWS_Block_WebserviceRel() {
        return listWS_Block_WebserviceRel;
    }

    public void setListWS_Block_WebserviceRel(Set<WS_Block_WebserviceRel> listWS_Block_WebserviceRel) {
        this.listWS_Block_WebserviceRel = listWS_Block_WebserviceRel;
    }

    public int getRemote_con_timeout() {
        return remote_con_timeout;
    }

    public void setRemote_con_timeout(int remote_con_timeout) {
        this.remote_con_timeout = remote_con_timeout;
    }

    public int getRemote_req_timeout() {
        return remote_req_timeout;
    }

    public void setRemote_req_timeout(int remote_req_timeout) {
        this.remote_req_timeout = remote_req_timeout;
    }

    public Set<WS_Header_Param> getListWS_Header_Param() {
        return listWS_Header_Param;
    }

    public void setListWS_Header_Param(Set<WS_Header_Param> listWS_Header_Param) {
        this.listWS_Header_Param = listWS_Header_Param;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.ws_webservice_id);
        hash = 97 * hash + Objects.hashCode(this.webservice_name);
        hash = 97 * hash + Objects.hashCode(this.api_url);
        hash = 97 * hash + Objects.hashCode(this.webservice_type);
        hash = 97 * hash + Objects.hashCode(this.api_rest_uri);
        hash = 97 * hash + Objects.hashCode(this.api_rest_method);
        hash = 97 * hash + Objects.hashCode(this.rest_media_type);
        hash = 97 * hash + Objects.hashCode(this.content_type);
        hash = 97 * hash + Objects.hashCode(this.rest_bind_param);
        hash = 97 * hash + Objects.hashCode(this.api_rest_request);
        hash = 97 * hash + Objects.hashCode(this.api_soap_wsdl);
        hash = 97 * hash + Objects.hashCode(this.api_soap_xsd);
        hash = 97 * hash + Objects.hashCode(this.api_soap_request);
        hash = 97 * hash + Objects.hashCode(this.api_function);
        hash = 97 * hash + Objects.hashCode(this.remote_operator);
        hash = 97 * hash + this.remote_max_connexion;
        hash = 97 * hash + (int) (this.remote_max_request ^ (this.remote_max_request >>> 32));
        hash = 97 * hash + Objects.hashCode(this.remote_secutity_mode);
        hash = 97 * hash + Objects.hashCode(this.security_login);
        hash = 97 * hash + Objects.hashCode(this.security_password);
        hash = 97 * hash + Objects.hashCode(this.authorizationType);
        hash = 97 * hash + Objects.hashCode(this.token);
        hash = 97 * hash + Objects.hashCode(this.expire_time_token);
        hash = 97 * hash + Objects.hashCode(this.description);
        hash = 97 * hash + Objects.hashCode(this.create_time);
        hash = 97 * hash + Objects.hashCode(this.update_time);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WS_Webservice other = (WS_Webservice) obj;
        if (this.remote_max_connexion != other.remote_max_connexion) {
            return false;
        }
        if (this.remote_max_request != other.remote_max_request) {
            return false;
        }
        if (!Objects.equals(this.webservice_name, other.webservice_name)) {
            return false;
        }
        if (!Objects.equals(this.api_url, other.api_url)) {
            return false;
        }
        if (!Objects.equals(this.api_rest_uri, other.api_rest_uri)) {
            return false;
        }
        if (!Objects.equals(this.rest_media_type, other.rest_media_type)) {
            return false;
        }
        if (!Objects.equals(this.content_type, other.content_type)) {
            return false;
        }
        if (!Objects.equals(this.api_rest_request, other.api_rest_request)) {
            return false;
        }
        if (!Objects.equals(this.api_soap_wsdl, other.api_soap_wsdl)) {
            return false;
        }
        if (!Objects.equals(this.api_soap_xsd, other.api_soap_xsd)) {
            return false;
        }
        if (!Objects.equals(this.api_soap_request, other.api_soap_request)) {
            return false;
        }
        if (!Objects.equals(this.remote_operator, other.remote_operator)) {
            return false;
        }
        if (!Objects.equals(this.security_login, other.security_login)) {
            return false;
        }
        if (!Objects.equals(this.security_password, other.security_password)) {
            return false;
        }
        if (!Objects.equals(this.authorizationType, other.authorizationType)) {
            return false;
        }
        if (!Objects.equals(this.token, other.token)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.ws_webservice_id, other.ws_webservice_id)) {
            return false;
        }
        if (this.webservice_type != other.webservice_type) {
            return false;
        }
        if (this.api_rest_method != other.api_rest_method) {
            return false;
        }
        if (this.rest_bind_param != other.rest_bind_param) {
            return false;
        }
        if (this.api_function != other.api_function) {
            return false;
        }
        if (this.remote_secutity_mode != other.remote_secutity_mode) {
            return false;
        }
        if (!Objects.equals(this.expire_time_token, other.expire_time_token)) {
            return false;
        }
        if (!Objects.equals(this.create_time, other.create_time)) {
            return false;
        }
        if (!Objects.equals(this.update_time, other.update_time)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WS_Webservice{" + "ws_webservice_id=" + ws_webservice_id + ", webservice_name=" + webservice_name + ", api_url=" + api_url + ", webservice_type=" + webservice_type + ", api_rest_uri=" + api_rest_uri + ", api_rest_method=" + api_rest_method + ", rest_media_type=" + rest_media_type + ", content_type=" + content_type + ", rest_bind_param=" + rest_bind_param + ", api_rest_request=" + api_rest_request + ", api_soap_wsdl=" + api_soap_wsdl + ", api_soap_xsd=" + api_soap_xsd + ", api_soap_request=" + api_soap_request + ", api_function=" + api_function + ", remote_operator=" + remote_operator + ", remote_max_connexion=" + remote_max_connexion + ", remote_max_request=" + remote_max_request + ", remote_secutity_mode=" + remote_secutity_mode + ", security_login=" + security_login + ", security_password=" + security_password + ", authorizationType=" + authorizationType + ", token=" + token + ", expire_time_token=" + expire_time_token + ", description=" + description + ", create_time=" + create_time + ", update_time=" + update_time + '}';
    }

   
}
