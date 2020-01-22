/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entity.sms_chat;

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

/**
 *
 * @author olivier.tatsinkou
 */
@Entity
@Table(name = "pushphone")
public class PushPhone implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private int pushphone_id;

    @Column(nullable = false)
    private String phone_number;
    private String operator;
    private String contry_code;

    @OneToMany(mappedBy = "pushphone")
    private Set<PushPhoneGroupRel> pushPhoneGroup_UserRel = new HashSet<>();

    public int getPushphone_id() {
        return pushphone_id;
    }

    public void setPushphone_id(int pushphone_id) {
        this.pushphone_id = pushphone_id;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getContry_code() {
        return contry_code;
    }

    public void setContry_code(String contry_code) {
        this.contry_code = contry_code;
    }

    public Set<PushPhoneGroupRel> getPushPhoneGroup_UserRel() {
        return pushPhoneGroup_UserRel;
    }

    public void setPushPhoneGroup_UserRel(Set<PushPhoneGroupRel> pushPhoneGroup_UserRel) {
        this.pushPhoneGroup_UserRel = pushPhoneGroup_UserRel;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.pushphone_id;
        hash = 59 * hash + Objects.hashCode(this.phone_number);
        hash = 59 * hash + Objects.hashCode(this.operator);
        hash = 59 * hash + Objects.hashCode(this.contry_code);
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
        final PushPhone other = (PushPhone) obj;
        if (this.pushphone_id != other.pushphone_id) {
            return false;
        }
        if (!Objects.equals(this.phone_number, other.phone_number)) {
            return false;
        }
        if (!Objects.equals(this.operator, other.operator)) {
            return false;
        }
        if (!Objects.equals(this.contry_code, other.contry_code)) {
            return false;
        }
        return true;
    }

   

}
