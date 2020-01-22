/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entity.sms_chat;

import com.tatsinktechnologic.xml.Chat_Type;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author olivier.tatsinkou
 */
@Entity
@Table(name = "chatgroup",uniqueConstraints = {@UniqueConstraint(columnNames = {"group_channel", "master_msisdn"})})
public class ChatGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private int chatgroup_id;

    @Column(nullable = false)
    private String group_channel;
    
    private String master_msisdn;

    @Enumerated(EnumType.STRING)
    private Chat_Type chat_type;
    
    private boolean isReceive = false;
    
    @OneToMany(mappedBy = "chatgroup")
    private Set<ChatGroup_Register> listChatGroup_Register = new HashSet<>();

    public int getChatgroup_id() {
        return chatgroup_id;
    }

    public void setChatgroup_id(int chatgroup_id) {
        this.chatgroup_id = chatgroup_id;
    }

    public String getMaster_msisdn() {
        return master_msisdn;
    }

    public void setMaster_msisdn(String master_msisdn) {
        this.master_msisdn = master_msisdn;
    }


    public String getGroup_channel() {
        return group_channel;
    }

    public void setGroup_channel(String group_channel) {
        this.group_channel = group_channel;
    }

    public Chat_Type getChat_type() {
        return chat_type;
    }

    public void setChat_type(Chat_Type chat_type) {
        this.chat_type = chat_type;
    }

    public boolean isIsReceive() {
        return isReceive;
    }

    public void setIsReceive(boolean isReceive) {
        this.isReceive = isReceive;
    }


    public Set<ChatGroup_Register> getListChatGroup_Register() {
        return listChatGroup_Register;
    }

    public void setListChatGroup_Register(Set<ChatGroup_Register> listChatGroup_Register) {
        this.listChatGroup_Register = listChatGroup_Register;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.chatgroup_id;
        hash = 73 * hash + Objects.hashCode(this.group_channel);
        hash = 73 * hash + Objects.hashCode(this.chat_type);
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
        final ChatGroup other = (ChatGroup) obj;
        if (this.chatgroup_id != other.chatgroup_id) {
            return false;
        }
        if (!Objects.equals(this.group_channel, other.group_channel)) {
            return false;
        }
        if (this.chat_type != other.chat_type) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ChatGroup{" + "chatgroup_id=" + chatgroup_id + ", group_channel=" + group_channel + ", chat_type=" + chat_type + '}';
    }
    
    

}
