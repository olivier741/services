/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entity.sms_chat;

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

/**
 *
 * @author olivier.tatsinkou
 */
@Entity
@Table(name = "pushphone_group",uniqueConstraints = {@UniqueConstraint(columnNames = { "pushphone_id","pushgroup_id" })})
public class PushPhoneGroupRel implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private int pushphone_group_user_id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pushphone_id", nullable = false)
    private PushPhone pushphone;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pushgroup_id", nullable = false)
    private PushGroup pushgroup;
    
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp  create_date;

    public int getPushphone_group_user_id() {
        return pushphone_group_user_id;
    }

    public void setPushphone_group_user_id(int pushphone_group_user_id) {
        this.pushphone_group_user_id = pushphone_group_user_id;
    }

    public PushPhone getPushphone() {
        return pushphone;
    }

    public void setPushphone(PushPhone pushphone) {
        this.pushphone = pushphone;
    }

    public PushGroup getPushgroup() {
        return pushgroup;
    }

    public void setPushgroup(PushGroup pushgroup) {
        this.pushgroup = pushgroup;
    }

    public Timestamp getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Timestamp create_date) {
        this.create_date = create_date;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.pushphone_group_user_id;
        hash = 47 * hash + Objects.hashCode(this.create_date);
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
        final PushPhoneGroupRel other = (PushPhoneGroupRel) obj;
        if (this.pushphone_group_user_id != other.pushphone_group_user_id) {
            return false;
        }
        if (!Objects.equals(this.create_date, other.create_date)) {
            return false;
        }
        return true;
    }

    
    
    
}
