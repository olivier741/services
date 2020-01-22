/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans_entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import java.sql.Date;
import java.util.Objects;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.Transient;
import org.hibernate.annotations.Check;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author olivier
 */
@Entity
@Check(constraints = "charge_fee >= 0")
public class Charge_His implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = LogManager.getLogger(Charge_His.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chg_generator")
    @SequenceGenerator(name="chg_generator", sequenceName = "chg_seq",allocationSize=1)
    private Long charge_his_id;

    private String msisdn;
    private Charge_Event charge_mode;
    private Date charge_date;
    private long charge_fee;
    
    @Lob 
    private String charge_request;
    
    @Lob 
    private String charge_response;
    
    private long duration;
     
    private String charge_error;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    
   @ManyToOne
    @JoinColumns({
        @JoinColumn(
            name = "server_name",
            referencedColumnName = "server_name"),
        @JoinColumn(
            name = "node_name",
            referencedColumnName = "node_name")
    })
    private Process_Unit process_unit;

    public Long getCharge_his_id() {
        return charge_his_id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public Charge_Event getCharge_mode() {
        return charge_mode;
    }

    public Date getCharge_date() {
        return charge_date;
    }

    public long getCharge_fee() {
        return charge_fee;
    }

    public String getCharge_request() {
        return charge_request;
    }

    public String getCharge_response() {
        return charge_response;
    }

    public long getDuration() {
        return duration;
    }

    public String getCharge_error() {
        return charge_error;
    }

    public Product getProduct() {
        return product;
    }

    public Process_Unit getProcess_unit() {
        return process_unit;
    }

    public void setCharge_his_id(Long charge_his_id) {
        this.charge_his_id = charge_his_id;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setCharge_mode(Charge_Event charge_mode) {
        this.charge_mode = charge_mode;
    }

    public void setCharge_date(Date charge_date) {
        this.charge_date = charge_date;
    }

    public void setCharge_fee(long charge_fee) {
        this.charge_fee = charge_fee;
    }

    public void setCharge_request(String charge_request) {
        this.charge_request = charge_request;
    }

    public void setCharge_response(String charge_response) {
        this.charge_response = charge_response;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setCharge_error(String charge_error) {
        this.charge_error = charge_error;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setProcess_unit(Process_Unit process_unit) {
        this.process_unit = process_unit;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.charge_his_id);
        hash = 53 * hash + Objects.hashCode(this.msisdn);
        hash = 53 * hash + Objects.hashCode(this.charge_mode);
        hash = 53 * hash + Objects.hashCode(this.charge_date);
        hash = 53 * hash + (int) (this.charge_fee ^ (this.charge_fee >>> 32));
        hash = 53 * hash + Objects.hashCode(this.charge_request);
        hash = 53 * hash + Objects.hashCode(this.charge_response);
        hash = 53 * hash + (int) (this.duration ^ (this.duration >>> 32));
        hash = 53 * hash + Objects.hashCode(this.charge_error);
        hash = 53 * hash + Objects.hashCode(this.product);
        hash = 53 * hash + Objects.hashCode(this.process_unit);
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
        final Charge_His other = (Charge_His) obj;
        if (this.charge_fee != other.charge_fee) {
            return false;
        }
        if (this.duration != other.duration) {
            return false;
        }
        if (!Objects.equals(this.msisdn, other.msisdn)) {
            return false;
        }
        if (!Objects.equals(this.charge_request, other.charge_request)) {
            return false;
        }
        if (!Objects.equals(this.charge_response, other.charge_response)) {
            return false;
        }
        if (!Objects.equals(this.charge_error, other.charge_error)) {
            return false;
        }
        if (!Objects.equals(this.charge_his_id, other.charge_his_id)) {
            return false;
        }
        if (this.charge_mode != other.charge_mode) {
            return false;
        }
        if (!Objects.equals(this.charge_date, other.charge_date)) {
            return false;
        }
        if (!Objects.equals(this.product, other.product)) {
            return false;
        }
        if (!Objects.equals(this.process_unit, other.process_unit)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Charge_His{" + "charge_his_id=" + charge_his_id + ", msisdn=" + msisdn + ", charge_mode=" + charge_mode + ", charge_date=" + charge_date + ", charge_fee=" + charge_fee + ", charge_request=" + charge_request + ", charge_response=" + charge_response + ", duration=" + duration + ", charge_error=" + charge_error + ", product=" + product + ", process_unit=" + process_unit + '}';
    }


}
