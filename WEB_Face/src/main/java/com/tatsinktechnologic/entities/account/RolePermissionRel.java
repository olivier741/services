/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entities.account;

/**
 *
 * @author olivier
 */

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "role_permission_rel",uniqueConstraints = {@UniqueConstraint(columnNames = { "role_id","permission_id" })})
public class RolePermissionRel  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer role_permission_id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = true)
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permission_id", nullable = true)
    private Permission permission;
    
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp  connection_date;

    public Integer getRole_permission_id() {
        return role_permission_id;
    }

    public void setRole_permission_id(Integer role_permission_id) {
        this.role_permission_id = role_permission_id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public Timestamp getConnection_date() {
        return connection_date;
    }

    public void setConnection_date(Timestamp connection_date) {
        this.connection_date = connection_date;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.role_permission_id);
        hash = 97 * hash + Objects.hashCode(this.role);
        hash = 97 * hash + Objects.hashCode(this.permission);
        hash = 97 * hash + Objects.hashCode(this.connection_date);
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
        final RolePermissionRel other = (RolePermissionRel) obj;
        if (!Objects.equals(this.role_permission_id, other.role_permission_id)) {
            return false;
        }
        if (!Objects.equals(this.role, other.role)) {
            return false;
        }
        if (!Objects.equals(this.permission, other.permission)) {
            return false;
        }
        if (!Objects.equals(this.connection_date, other.connection_date)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RolePermissionRel{" + "role_permission_id=" + role_permission_id + ", role=" + role + ", permission=" + permission + ", connection_date=" + connection_date + '}';
    }

}
