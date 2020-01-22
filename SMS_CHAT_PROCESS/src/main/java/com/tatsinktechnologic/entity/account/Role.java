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
@Table(name = "role")
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer role_id;

    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    @OneToMany(mappedBy = "role")
    private Set<UserRoleRel> userRoleRels = new HashSet<UserRoleRel>();

   
    @OneToMany(mappedBy = "role")
    private Set<RolePermissionRel> rolePermissionRels = new HashSet<>();

    public Integer getRole_id() {
        return role_id;
    }

    public void setRole_id(Integer role_id) {
        this.role_id = role_id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<UserRoleRel> getUserRoleRels() {
        return userRoleRels;
    }

    public void setUserRoleRels(Set<UserRoleRel> userRoleRels) {
        this.userRoleRels = userRoleRels;
    }

    public Set<RolePermissionRel> getRolePermissionRels() {
        return rolePermissionRels;
    }

    public void setRolePermissionRels(Set<RolePermissionRel> rolePermissionRels) {
        this.rolePermissionRels = rolePermissionRels;
    }
    
     public boolean hasRoleName() {
        return roleName != null && !"".equals(roleName.trim());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + this.role_id;
        hash = 59 * hash + Objects.hashCode(this.roleName);
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
        final Role other = (Role) obj;
        if (this.role_id != other.role_id) {
            return false;
        }
        if (!Objects.equals(this.roleName, other.roleName)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "Role{" + "role_id=" + role_id + ", roleName=" + roleName + '}';
    }

  
}
