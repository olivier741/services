/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans_entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import org.hibernate.annotations.Check;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author olivier
 */
@Entity
@Check(constraints = "fee >= 0")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = LogManager.getLogger(Product.class);
        
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prd_generator")
    @SequenceGenerator(name="prd_generator", sequenceName = "prd_seq",allocationSize=1)
    private Long product_id;

    private String product_name;
    private String product_code;
    private long fee;
    private String policy; // daily, weekly, monthly
    private String cyle;   // 03 or 04 time per day (can use cront)
    private Date start_date;
    private Date end_date;
    private Time start_reg_time;
    private Time end_reg_time;
           
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = true)
    private Promotion promotion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
   
    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    private Set<Action> listAction = new HashSet<>();
    
    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    private Set<Register> listRegister= new HashSet<>();
    
    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    private Set<Charge_His> listCharge_His= new HashSet<>();
    
    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    private Set<Content_Message> listContent_Message= new HashSet<>();

    public Logger getLogger() {
        return logger;
    }

    public Long getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getProduct_code() {
        return product_code;
    }

    public long getFee() {
        return fee;
    }

    public String getPolicy() {
        return policy;
    }

    public String getCyle() {
        return cyle;
    }

    public Date getStart_date() {
        return start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public Time getStart_reg_time() {
        return start_reg_time;
    }

    public Time getEnd_reg_time() {
        return end_reg_time;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public Service getService() {
        return service;
    }

    public Set<Action> getListAction() {
        return listAction;
    }

    public Set<Register> getListRegister() {
        return listRegister;
    }

    public Set<Charge_His> getListCharge_His() {
        return listCharge_His;
    }

    public Set<Content_Message> getListContent_Message() {
        return listContent_Message;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public void setCyle(String cyle) {
        this.cyle = cyle;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public void setStart_reg_time(Time start_reg_time) {
        this.start_reg_time = start_reg_time;
    }

    public void setEnd_reg_time(Time end_reg_time) {
        this.end_reg_time = end_reg_time;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setListAction(Set<Action> listAction) {
        this.listAction = listAction;
    }

    public void setListRegister(Set<Register> listRegister) {
        this.listRegister = listRegister;
    }

    public void setListCharge_His(Set<Charge_His> listCharge_His) {
        this.listCharge_His = listCharge_His;
    }

    public void setListContent_Message(Set<Content_Message> listContent_Message) {
        this.listContent_Message = listContent_Message;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.logger);
        hash = 53 * hash + Objects.hashCode(this.product_id);
        hash = 53 * hash + Objects.hashCode(this.product_name);
        hash = 53 * hash + Objects.hashCode(this.product_code);
        hash = 53 * hash + (int) (this.fee ^ (this.fee >>> 32));
        hash = 53 * hash + Objects.hashCode(this.policy);
        hash = 53 * hash + Objects.hashCode(this.cyle);
        hash = 53 * hash + Objects.hashCode(this.start_date);
        hash = 53 * hash + Objects.hashCode(this.end_date);
        hash = 53 * hash + Objects.hashCode(this.start_reg_time);
        hash = 53 * hash + Objects.hashCode(this.end_reg_time);
        hash = 53 * hash + Objects.hashCode(this.promotion);
        hash = 53 * hash + Objects.hashCode(this.service);
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
        final Product other = (Product) obj;
        if (this.fee != other.fee) {
            return false;
        }
        if (!Objects.equals(this.product_name, other.product_name)) {
            return false;
        }
        if (!Objects.equals(this.product_code, other.product_code)) {
            return false;
        }
        if (!Objects.equals(this.policy, other.policy)) {
            return false;
        }
        if (!Objects.equals(this.cyle, other.cyle)) {
            return false;
        }
        if (!Objects.equals(this.logger, other.logger)) {
            return false;
        }
        if (!Objects.equals(this.product_id, other.product_id)) {
            return false;
        }
        if (!Objects.equals(this.start_date, other.start_date)) {
            return false;
        }
        if (!Objects.equals(this.end_date, other.end_date)) {
            return false;
        }
        if (!Objects.equals(this.start_reg_time, other.start_reg_time)) {
            return false;
        }
        if (!Objects.equals(this.end_reg_time, other.end_reg_time)) {
            return false;
        }
        if (!Objects.equals(this.promotion, other.promotion)) {
            return false;
        }
        if (!Objects.equals(this.service, other.service)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Product{" + "logger=" + logger + ", product_id=" + product_id + ", product_name=" + product_name + ", product_code=" + product_code + ", fee=" + fee + ", policy=" + policy + ", cyle=" + cyle + ", start_date=" + start_date + ", end_date=" + end_date + ", start_reg_time=" + start_reg_time + ", end_reg_time=" + end_reg_time + ", promotion=" + promotion + ", service=" + service + '}';
    }

}
