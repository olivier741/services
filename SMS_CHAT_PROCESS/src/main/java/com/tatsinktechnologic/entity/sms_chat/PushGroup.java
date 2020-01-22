/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entity.sms_chat;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author olivier.tatsinkou
 */
@Entity
@Table(name = "pushgroup")
public class PushGroup implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private int pushgroup_id;
    
    private String group_name;
    
    @OneToMany(mappedBy = "pushgroup")
    private Set<PushGroup_UserRel> pushGroup_UserRelRels = new HashSet<>();
    


    public int getPushgroup_id() {
        return pushgroup_id;
    }

    public void setPushgroup_id(int pushgroup_id) {
        this.pushgroup_id = pushgroup_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public Set<PushGroup_UserRel> getPushGroup_UserRelRels() {
        return pushGroup_UserRelRels;
    }

    public void setPushGroup_UserRelRels(Set<PushGroup_UserRel> pushGroup_UserRelRels) {
        this.pushGroup_UserRelRels = pushGroup_UserRelRels;
    }
    
    
}
