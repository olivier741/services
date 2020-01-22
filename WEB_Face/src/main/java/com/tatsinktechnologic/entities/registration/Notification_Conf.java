/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entities.registration;

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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author olivier
 */
@Entity
@Table(name = "notification_conf")
public class Notification_Conf implements Serializable {
       
        
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private int config_id;

    @Column(nullable = false, unique = true)
    private String nofication_name;
    
    @Column(nullable = true)
    @Lob
    private String notification_value;
    
    @Column(nullable = true)
    private String description;
    
    @Column(nullable = true)
    @CreationTimestamp
    private Timestamp create_time;
    
    @Column(nullable = true)
    @UpdateTimestamp
    private Timestamp update_time;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "command_id", nullable = true)
    private Command command;

    public int getConfig_id() {
        return config_id;
    }

    public void setConfig_id(int config_id) {
        this.config_id = config_id;
    }

    public String getNofication_name() {
        return nofication_name;
    }

    public void setNofication_name(String nofication_name) {
        this.nofication_name = nofication_name;
    }

    public String getNotification_value() {
        return notification_value;
    }

    public void setNotification_value(String notification_value) {
        this.notification_value = notification_value;
    }

   

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
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
    
    

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + this.config_id;
        hash = 89 * hash + Objects.hashCode(this.nofication_name);
        hash = 89 * hash + Objects.hashCode(this.notification_value);
        hash = 89 * hash + Objects.hashCode(this.description);
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
        final Notification_Conf other = (Notification_Conf) obj;
        if (this.config_id != other.config_id) {
            return false;
        }
        if (!Objects.equals(this.nofication_name, other.nofication_name)) {
            return false;
        }
        if (!Objects.equals(this.notification_value, other.notification_value)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Notification_Conf{" + "config_id=" + config_id + ", nofication_name=" + nofication_name + ", notification_value=" + notification_value + ", description=" + description + ", command=" + command + '}';
    }

  
}
