/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entities.api_gateway;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author olivier
 */
@Entity
@Table(name = "ws_client")
public class WS_Client implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long ws_client_id;

    @Column(nullable = false, unique = true)
    private String client_name;
    
    @Column(nullable = false)
    private String login;
    
    @Column(nullable = false)
    private String login_salt;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String password_salt;
    
    @Column(nullable = true)
    private String ip_address;
    
    @Column(nullable = true)
    private int max_request;

    @Column(nullable = true)
    private String description;
    
    @Column(nullable = true)
    @CreationTimestamp
    private Timestamp create_time;
    
    @Column(nullable = true)
    @UpdateTimestamp
    private Timestamp update_time;
    
    
    @OneToMany(mappedBy = "ws_client")
    private Set<WS_AccessManagement> listAccessManagement = new HashSet<>();

    public Long getWs_client_id() {
        return ws_client_id;
    }

    public void setWs_client_id(Long ws_client_id) {
        this.ws_client_id = ws_client_id;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin_salt() {
        return login_salt;
    }

    public void setLogin_salt(String login_salt) {
        this.login_salt = login_salt;
    }

    public String getPassword_salt() {
        return password_salt;
    }

    public void setPassword_salt(String password_salt) {
        this.password_salt = password_salt;
    }

   

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public int getMax_request() {
        return max_request;
    }

    public void setMax_request(int max_request) {
        this.max_request = max_request;
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

    public Set<WS_AccessManagement> getListAccessManagement() {
        return listAccessManagement;
    }

    public void setListAccessManagement(Set<WS_AccessManagement> listAccessManagement) {
        this.listAccessManagement = listAccessManagement;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.ws_client_id);
        hash = 59 * hash + Objects.hashCode(this.client_name);
        hash = 59 * hash + Objects.hashCode(this.login);
        hash = 59 * hash + Objects.hashCode(this.login_salt);
        hash = 59 * hash + Objects.hashCode(this.password);
        hash = 59 * hash + Objects.hashCode(this.password_salt);
        hash = 59 * hash + Objects.hashCode(this.ip_address);
        hash = 59 * hash + this.max_request;
        hash = 59 * hash + Objects.hashCode(this.description);
        hash = 59 * hash + Objects.hashCode(this.create_time);
        hash = 59 * hash + Objects.hashCode(this.update_time);
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
        final WS_Client other = (WS_Client) obj;
        if (this.max_request != other.max_request) {
            return false;
        }
        if (!Objects.equals(this.client_name, other.client_name)) {
            return false;
        }
        if (!Objects.equals(this.login, other.login)) {
            return false;
        }
        if (!Objects.equals(this.login_salt, other.login_salt)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        if (!Objects.equals(this.password_salt, other.password_salt)) {
            return false;
        }
        if (!Objects.equals(this.ip_address, other.ip_address)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.ws_client_id, other.ws_client_id)) {
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
        return "WS_Client{" + "ws_client_id=" + ws_client_id + ", client_name=" + client_name + ", login=" + login + ", login_salt=" + login_salt + ", password=" + password + ", password_salt=" + password_salt + ", ip_address=" + ip_address + ", max_request=" + max_request + ", description=" + description + ", create_time=" + create_time + ", update_time=" + update_time + '}';
    }

  

}
