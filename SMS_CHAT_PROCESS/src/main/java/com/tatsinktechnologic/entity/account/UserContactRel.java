/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entity.account;

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
@Table(name = "user_contact_rel", uniqueConstraints = {@UniqueConstraint(columnNames = { "user_id","contact_id" })})
public class UserContactRel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer user_contact_id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contact_id", nullable = true)
    private Contact contact;
    
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp  connection_date;

    public Integer getUser_contact_id() {
        return user_contact_id;
    }

    public void setUser_contact_id(Integer user_contact_id) {
        this.user_contact_id = user_contact_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Timestamp getConnection_date() {
        return connection_date;
    }

    public void setConnection_date(Timestamp connection_date) {
        this.connection_date = connection_date;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.user_contact_id);
        hash = 23 * hash + Objects.hashCode(this.user);
        hash = 23 * hash + Objects.hashCode(this.contact);
        hash = 23 * hash + Objects.hashCode(this.connection_date);
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
        final UserContactRel other = (UserContactRel) obj;
        if (!Objects.equals(this.user_contact_id, other.user_contact_id)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.contact, other.contact)) {
            return false;
        }
        if (!Objects.equals(this.connection_date, other.connection_date)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UserContactRel{" + "user_contact_id=" + user_contact_id + ", user=" + user + ", contact=" + contact + ", connection_date=" + connection_date + '}';
    }
    
    
}
