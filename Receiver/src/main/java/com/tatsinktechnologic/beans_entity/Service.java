/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans_entity;

import java.io.Serializable;
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
import java.sql.Date;
import javax.persistence.Transient;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author olivier
 */
@Entity
public class Service implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = LogManager.getLogger(Service.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "svc_generator")
    @SequenceGenerator(name="svc_generator", sequenceName = "svc_seq",allocationSize=1)
    private Long service_id;

    private String service_name;
    private String rcv_shortCode_channel;
    private String send_shortCode_channel;
    private String service_provider;
    private String service_description;
    private Date create_date;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;
    
    @OneToMany(mappedBy = "service",fetch = FetchType.LAZY)
    private Set<Product> listProduct = new HashSet<>();

    public Long getService_id() {
        return service_id;
    }

    public String getService_name() {
        return service_name;
    }

    public String getRcv_shortCode_channel() {
        return rcv_shortCode_channel;
    }

    public String getSend_shortCode_channel() {
        return send_shortCode_channel;
    }

    public String getService_provider() {
        return service_provider;
    }

    public String getService_description() {
        return service_description;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public Provider getProvider() {
        return provider;
    }

    public Set<Product> getListProduct() {
        return listProduct;
    }

    public void setService_id(Long service_id) {
        this.service_id = service_id;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public void setRcv_shortCode_channel(String rcv_shortCode_channel) {
        this.rcv_shortCode_channel = rcv_shortCode_channel;
    }

    public void setSend_shortCode_channel(String send_shortCode_channel) {
        this.send_shortCode_channel = send_shortCode_channel;
    }

    public void setService_provider(String service_provider) {
        this.service_provider = service_provider;
    }

    public void setService_description(String service_description) {
        this.service_description = service_description;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public void setListProduct(Set<Product> listProduct) {
        this.listProduct = listProduct;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.service_id);
        hash = 23 * hash + Objects.hashCode(this.service_name);
        hash = 23 * hash + Objects.hashCode(this.rcv_shortCode_channel);
        hash = 23 * hash + Objects.hashCode(this.send_shortCode_channel);
        hash = 23 * hash + Objects.hashCode(this.service_provider);
        hash = 23 * hash + Objects.hashCode(this.service_description);
        hash = 23 * hash + Objects.hashCode(this.create_date);
        hash = 23 * hash + Objects.hashCode(this.provider);
        hash = 23 * hash + Objects.hashCode(this.listProduct);
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
        final Service other = (Service) obj;
        if (!Objects.equals(this.service_name, other.service_name)) {
            return false;
        }
        if (!Objects.equals(this.rcv_shortCode_channel, other.rcv_shortCode_channel)) {
            return false;
        }
        if (!Objects.equals(this.send_shortCode_channel, other.send_shortCode_channel)) {
            return false;
        }
        if (!Objects.equals(this.service_provider, other.service_provider)) {
            return false;
        }
        if (!Objects.equals(this.service_description, other.service_description)) {
            return false;
        }
        if (!Objects.equals(this.service_id, other.service_id)) {
            return false;
        }
        if (!Objects.equals(this.create_date, other.create_date)) {
            return false;
        }
        if (!Objects.equals(this.provider, other.provider)) {
            return false;
        }
        if (!Objects.equals(this.listProduct, other.listProduct)) {
            return false;
        }
        return true;
    }

 
    
    
}
