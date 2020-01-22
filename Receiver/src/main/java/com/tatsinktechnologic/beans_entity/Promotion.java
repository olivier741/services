/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans_entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.sql.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import org.hibernate.annotations.Check;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author olivier
 */
@Entity
@Check(constraints = "promotion_fee >= 0")
public class Promotion implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = LogManager.getLogger(Product.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "promo_generator")
    @SequenceGenerator(name="promo_generator", sequenceName = "promo_seq",allocationSize=1)
    private Long promotion_id;
    
    private String promotion_name;
    private String msisdn_table;
    private String msisdn_regex;
    private Date start_date;
    private Date end_date;
    private long promotion_fee;
    
    @Enumerated(EnumType.STRING)
    private Charge_Event charge_event;
    
    @Enumerated(EnumType.STRING)
    private Promo_Filter promotion_filter;
    
    @OneToMany(mappedBy = "promotion",fetch = FetchType.LAZY)
    protected Set<Product> listProduct = new HashSet<>();

    public Long getPromotion_id() {
        return promotion_id;
    }

    public String getPromotion_name() {
        return promotion_name;
    }

    public String getMsisdn_table() {
        return msisdn_table;
    }

    public String getMsisdn_regex() {
        return msisdn_regex;
    }

    public Date getStart_date() {
        return start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public long getPromotion_fee() {
        return promotion_fee;
    }

    public Charge_Event getCharge_event() {
        return charge_event;
    }

    public Promo_Filter getPromotion_filter() {
        return promotion_filter;
    }

    public Set<Product> getListProduct() {
        return listProduct;
    }

    public void setPromotion_id(Long promotion_id) {
        this.promotion_id = promotion_id;
    }

    public void setPromotion_name(String promotion_name) {
        this.promotion_name = promotion_name;
    }

    public void setMsisdn_table(String msisdn_table) {
        this.msisdn_table = msisdn_table;
    }

    public void setMsisdn_regex(String msisdn_regex) {
        this.msisdn_regex = msisdn_regex;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public void setPromotion_fee(long promotion_fee) {
        this.promotion_fee = promotion_fee;
    }

    public void setCharge_event(Charge_Event charge_event) {
        this.charge_event = charge_event;
    }

    public void setPromotion_filter(Promo_Filter promotion_filter) {
        this.promotion_filter = promotion_filter;
    }

    public void setListProduct(Set<Product> listProduct) {
        this.listProduct = listProduct;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.promotion_id);
        hash = 47 * hash + Objects.hashCode(this.promotion_name);
        hash = 47 * hash + Objects.hashCode(this.msisdn_table);
        hash = 47 * hash + Objects.hashCode(this.msisdn_regex);
        hash = 47 * hash + Objects.hashCode(this.start_date);
        hash = 47 * hash + Objects.hashCode(this.end_date);
        hash = 47 * hash + (int) (this.promotion_fee ^ (this.promotion_fee >>> 32));
        hash = 47 * hash + Objects.hashCode(this.charge_event);
        hash = 47 * hash + Objects.hashCode(this.promotion_filter);
        hash = 47 * hash + Objects.hashCode(this.listProduct);
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
        final Promotion other = (Promotion) obj;
        if (this.promotion_fee != other.promotion_fee) {
            return false;
        }
        if (!Objects.equals(this.promotion_name, other.promotion_name)) {
            return false;
        }
        if (!Objects.equals(this.msisdn_table, other.msisdn_table)) {
            return false;
        }
        if (!Objects.equals(this.msisdn_regex, other.msisdn_regex)) {
            return false;
        }
        if (!Objects.equals(this.promotion_id, other.promotion_id)) {
            return false;
        }
        if (!Objects.equals(this.start_date, other.start_date)) {
            return false;
        }
        if (!Objects.equals(this.end_date, other.end_date)) {
            return false;
        }
        if (this.charge_event != other.charge_event) {
            return false;
        }
        if (this.promotion_filter != other.promotion_filter) {
            return false;
        }
        if (!Objects.equals(this.listProduct, other.listProduct)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Promotion{" + "promotion_id=" + promotion_id + ", promotion_name=" + promotion_name + ", msisdn_table=" + msisdn_table + ", msisdn_regex=" + msisdn_regex + ", start_date=" + start_date + ", end_date=" + end_date + ", promotion_fee=" + promotion_fee + ", charge_event=" + charge_event + ", promotion_filter=" + promotion_filter + ", listProduct=" + listProduct + '}';
    }

}
