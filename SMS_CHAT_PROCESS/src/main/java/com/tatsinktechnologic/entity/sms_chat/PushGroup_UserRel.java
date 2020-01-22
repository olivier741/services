/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entity.sms_chat;

import com.tatsinktechnologic.entity.account.User;
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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author olivier.tatsinkou
 */
@Entity
@Table(name = "pushgroup_user",uniqueConstraints = {@UniqueConstraint(columnNames = { "user_id","pushgroup_id" })})
public class PushGroup_UserRel implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private int pushgroup_user_id;
    
    @Lob
    private String message;
    
    private String Channel;
    private Timestamp  send_time;
    private boolean status = true;
    
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pushgroup_id", nullable = false)
    private PushGroup pushgroup;
    
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp  create_date;

    public int getPushgroup_user_id() {
        return pushgroup_user_id;
    }

    public void setPushgroup_user_id(int pushgroup_user_id) {
        this.pushgroup_user_id = pushgroup_user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChannel() {
        return Channel;
    }

    public void setChannel(String Channel) {
        this.Channel = Channel;
    }

    public Timestamp getSend_time() {
        return send_time;
    }

    public void setSend_time(Timestamp send_time) {
        this.send_time = send_time;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        hash = 89 * hash + this.pushgroup_user_id;
        hash = 89 * hash + Objects.hashCode(this.message);
        hash = 89 * hash + Objects.hashCode(this.Channel);
        hash = 89 * hash + Objects.hashCode(this.send_time);
        hash = 89 * hash + (this.status ? 1 : 0);
        hash = 89 * hash + Objects.hashCode(this.user);
        hash = 89 * hash + Objects.hashCode(this.create_date);
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
        final PushGroup_UserRel other = (PushGroup_UserRel) obj;
        if (this.pushgroup_user_id != other.pushgroup_user_id) {
            return false;
        }
        if (this.status != other.status) {
            return false;
        }
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        if (!Objects.equals(this.Channel, other.Channel)) {
            return false;
        }
        if (!Objects.equals(this.send_time, other.send_time)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.create_date, other.create_date)) {
            return false;
        }
        return true;
    }
   
    
    


}
