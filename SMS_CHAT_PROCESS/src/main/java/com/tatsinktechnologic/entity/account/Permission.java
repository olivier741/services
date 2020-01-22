/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entity.account;

/**
 *
 * @author olivier
 */
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "permission")
public class Permission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer permission_id;

    @Column(name = "permission_str", nullable = false,unique = true)
    private String permissionStr;
    
    @OneToMany(mappedBy = "permission")
    private Set<RolePermissionRel> rolePermissionRels = new HashSet<>();

    public Integer getPermission_id() {
        return permission_id;
    }

    public void setPermission_id(Integer permission_id) {
        this.permission_id = permission_id;
    }

    public String getPermissionStr() {
        return permissionStr;
    }

    public void setPermissionStr(String permissionStr) {
        this.permissionStr = permissionStr;
    }

    public Set<RolePermissionRel> getRolePermissionRels() {
        return rolePermissionRels;
    }

    public void setRolePermissionRels(Set<RolePermissionRel> rolePermissionRels) {
        this.rolePermissionRels = rolePermissionRels;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.permission_id);
        hash = 97 * hash + Objects.hashCode(this.permissionStr);
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
        final Permission other = (Permission) obj;
        if (!Objects.equals(this.permissionStr, other.permissionStr)) {
            return false;
        }
        if (!Objects.equals(this.permission_id, other.permission_id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Permission{" + "permission_id=" + permission_id + ", permissionStr=" + permissionStr + '}';
    }

    
   
  
}