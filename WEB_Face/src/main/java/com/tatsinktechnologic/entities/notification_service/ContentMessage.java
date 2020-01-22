/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entities.notification_service;

import com.tatsinktechnologic.entities.registration.Service;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
 * @author olivier.tatsinkou
 */
@Entity
@Table(name = "contentMessage")
public class ContentMessage implements Serializable {

       @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private int content_id;

    private String content_label;

    @Lob
    private String message;

    @Enumerated(EnumType.STRING)
    private Message_Status message_status;

    @Column(nullable = true)
    private Timestamp submit_time;

    @Column(nullable = true)
    private Timestamp confirm_time;

    @Column(nullable = true)
    private Timestamp accpt_time;

    @Column(nullable = false)
    private Timestamp launch_time;

    @Column(nullable = false)
    private Timestamp expire_time;

    @Column(nullable = true)
    @CreationTimestamp
    private Timestamp create_time;
    
    @Column(nullable = true)
    @UpdateTimestamp
    private Timestamp update_time;
    
    @Column(nullable = true)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;


    public int getContent_id() {
        return content_id;
    }

    public void setContent_id(int content_id) {
        this.content_id = content_id;
    }

    public String getContent_label() {
        return content_label;
    }

    public void setContent_label(String content_label) {
        this.content_label = content_label;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message_Status getMessage_status() {
        return message_status;
    }

    public void setMessage_status(Message_Status message_status) {
        this.message_status = message_status;
    }

    public Timestamp getConfirm_time() {
        return confirm_time;
    }

    public void setConfirm_time(Timestamp confirm_time) {
        this.confirm_time = confirm_time;
    }

    public Timestamp getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(Timestamp expire_time) {
        this.expire_time = expire_time;
    }

    public Timestamp getLaunch_time() {
        return launch_time;
    }

    public void setLaunch_time(Timestamp launch_time) {
        this.launch_time = launch_time;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }


    public Timestamp getSubmit_time() {
        return submit_time;
    }

    public void setSubmit_time(Timestamp submit_time) {
        this.submit_time = submit_time;
    }

    public Timestamp getAccpt_time() {
        return accpt_time;
    }

    public void setAccpt_time(Timestamp accpt_time) {
        this.accpt_time = accpt_time;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + this.content_id;
        hash = 71 * hash + Objects.hashCode(this.content_label);
        hash = 71 * hash + Objects.hashCode(this.message);
        hash = 71 * hash + Objects.hashCode(this.message_status);
        hash = 71 * hash + Objects.hashCode(this.submit_time);
        hash = 71 * hash + Objects.hashCode(this.confirm_time);
        hash = 71 * hash + Objects.hashCode(this.accpt_time);
        hash = 71 * hash + Objects.hashCode(this.launch_time);
        hash = 71 * hash + Objects.hashCode(this.expire_time);
        hash = 71 * hash + Objects.hashCode(this.create_time);
        hash = 71 * hash + Objects.hashCode(this.update_time);
        hash = 71 * hash + Objects.hashCode(this.description);
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
        final ContentMessage other = (ContentMessage) obj;
        if (this.content_id != other.content_id) {
            return false;
        }
        if (!Objects.equals(this.content_label, other.content_label)) {
            return false;
        }
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (this.message_status != other.message_status) {
            return false;
        }
        if (!Objects.equals(this.submit_time, other.submit_time)) {
            return false;
        }
        if (!Objects.equals(this.confirm_time, other.confirm_time)) {
            return false;
        }
        if (!Objects.equals(this.accpt_time, other.accpt_time)) {
            return false;
        }
        if (!Objects.equals(this.launch_time, other.launch_time)) {
            return false;
        }
        if (!Objects.equals(this.expire_time, other.expire_time)) {
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
        return "ContentMessage{" + "content_id=" + content_id + ", content_label=" + content_label + ", message=" + message + ", message_status=" + message_status + ", submit_time=" + submit_time + ", confirm_time=" + confirm_time + ", accpt_time=" + accpt_time + ", launch_time=" + launch_time + ", expire_time=" + expire_time + ", create_time=" + create_time + ", update_time=" + update_time + ", description=" + description + '}';
    }
    
}
