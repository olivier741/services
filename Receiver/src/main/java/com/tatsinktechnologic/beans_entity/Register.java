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
import java.sql.Date;
import java.util.Objects;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author olivier
 */
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"msisdn" , "product_id"})})
public class Register implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = LogManager.getLogger(Register.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reg_generator")
    @SequenceGenerator(name="reg_generator", sequenceName = "reg_seq",allocationSize=1)
    private Long register_id;

    private String msisdn;
    private String Status;
    private Date registration_date;
    private Date lastRenew_date;
    private Date expire_date;
    private Date unregister_date;
    private int number_time_register;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public Long getRegister_id() {
        return register_id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getStatus() {
        return Status;
    }

    public Date getRegistration_date() {
        return registration_date;
    }

    public Date getLastRenew_date() {
        return lastRenew_date;
    }

    public Date getExpire_date() {
        return expire_date;
    }

    public Date getUnregister_date() {
        return unregister_date;
    }

    public int getNumber_time_register() {
        return number_time_register;
    }

    public Product getProduct() {
        return product;
    }

    public void setRegister_id(Long register_id) {
        this.register_id = register_id;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public void setRegistration_date(Date registration_date) {
        this.registration_date = registration_date;
    }

    public void setLastRenew_date(Date lastRenew_date) {
        this.lastRenew_date = lastRenew_date;
    }

    public void setExpire_date(Date expire_date) {
        this.expire_date = expire_date;
    }

    public void setUnregister_date(Date unregister_date) {
        this.unregister_date = unregister_date;
    }

    public void setNumber_time_register(int number_time_register) {
        this.number_time_register = number_time_register;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.register_id);
        hash = 17 * hash + Objects.hashCode(this.msisdn);
        hash = 17 * hash + Objects.hashCode(this.Status);
        hash = 17 * hash + Objects.hashCode(this.registration_date);
        hash = 17 * hash + Objects.hashCode(this.lastRenew_date);
        hash = 17 * hash + Objects.hashCode(this.expire_date);
        hash = 17 * hash + Objects.hashCode(this.unregister_date);
        hash = 17 * hash + this.number_time_register;
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
        final Register other = (Register) obj;
        if (this.number_time_register != other.number_time_register) {
            return false;
        }
        if (!Objects.equals(this.msisdn, other.msisdn)) {
            return false;
        }
        if (!Objects.equals(this.Status, other.Status)) {
            return false;
        }
        if (!Objects.equals(this.register_id, other.register_id)) {
            return false;
        }
        if (!Objects.equals(this.registration_date, other.registration_date)) {
            return false;
        }
        if (!Objects.equals(this.lastRenew_date, other.lastRenew_date)) {
            return false;
        }
        if (!Objects.equals(this.expire_date, other.expire_date)) {
            return false;
        }
        if (!Objects.equals(this.unregister_date, other.unregister_date)) {
            return false;
        }
        if (!Objects.equals(this.product, other.product)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Register{" + "register_id=" + register_id + ", msisdn=" + msisdn + ", Status=" + Status + ", registration_date=" + registration_date + ", lastRenew_date=" + lastRenew_date + ", expire_date=" + expire_date + ", unregister_date=" + unregister_date + ", number_time_register=" + number_time_register + ", product=" + product + '}';
    }

    
 
}
