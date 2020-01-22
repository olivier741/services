/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans_entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author olivier
 */
@Entity
public class Content_Message implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = LogManager.getLogger(Content_Message.class);
       
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cont_msg_generator")
    @SequenceGenerator(name="cont_msg_generator", sequenceName = "cont_msg_seq",allocationSize=1)
    private Long content_msg_id;

    @Lob
    private String message;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public Long getContent_msg_id() {
        return content_msg_id;
    }

    public String getMessage() {
        return message;
    }

    public Product getProduct() {
        return product;
    }

    public void setContent_msg_id(Long content_msg_id) {
        this.content_msg_id = content_msg_id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.content_msg_id);
        hash = 17 * hash + Objects.hashCode(this.message);
        hash = 17 * hash + Objects.hashCode(this.product);
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
        final Content_Message other = (Content_Message) obj;
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        if (!Objects.equals(this.content_msg_id, other.content_msg_id)) {
            return false;
        }
        if (!Objects.equals(this.product, other.product)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Content_Message{" + "content_msg_id=" + content_msg_id + ", message=" + message + ", product=" + product + '}';
    }

}
