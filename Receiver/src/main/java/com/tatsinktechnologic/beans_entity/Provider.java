/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans_entity;

import java.io.Serializable;
import java.sql.Date;
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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author olivier
 */
@Entity
public class Provider implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = LogManager.getLogger(Provider.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pvd_generator")
    @SequenceGenerator(name="pvd_generator", sequenceName = "pvd_seq",allocationSize=1)
    private Long provider_id;
    
    private String provider_name;  
    private Date agreement_date;
    private Provider_Type provider_type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_category_id", nullable = false)
    private Provider_Category provider_category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_address_id", nullable = false)
    private Provider_Address provider_address;
    
    @OneToMany(mappedBy = "provider",fetch = FetchType.LAZY)
    private Set<Service> listService = new HashSet<>();
    
    @OneToMany(mappedBy = "provider",fetch = FetchType.LAZY)
    private Set<Person> listPerson = new HashSet<>();

    public Long getProvider_id() {
        return provider_id;
    }

    public String getProvider_name() {
        return provider_name;
    }

    public Date getAgreement_date() {
        return agreement_date;
    }

    public Provider_Type getProvider_type() {
        return provider_type;
    }

    public Provider_Category getProvider_category() {
        return provider_category;
    }

    public Provider_Address getProvider_address() {
        return provider_address;
    }

    public Set<Service> getListService() {
        return listService;
    }

    public Set<Person> getListPerson() {
        return listPerson;
    }

    public void setProvider_id(Long provider_id) {
        this.provider_id = provider_id;
    }

    public void setProvider_name(String provider_name) {
        this.provider_name = provider_name;
    }

    public void setAgreement_date(Date agreement_date) {
        this.agreement_date = agreement_date;
    }

    public void setProvider_type(Provider_Type provider_type) {
        this.provider_type = provider_type;
    }

    public void setProvider_category(Provider_Category provider_category) {
        this.provider_category = provider_category;
    }

    public void setProvider_address(Provider_Address provider_address) {
        this.provider_address = provider_address;
    }

    public void setListService(Set<Service> listService) {
        this.listService = listService;
    }

    public void setListPerson(Set<Person> listPerson) {
        this.listPerson = listPerson;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.provider_id);
        hash = 97 * hash + Objects.hashCode(this.provider_name);
        hash = 97 * hash + Objects.hashCode(this.agreement_date);
        hash = 97 * hash + Objects.hashCode(this.provider_type);
        hash = 97 * hash + Objects.hashCode(this.provider_category);
        hash = 97 * hash + Objects.hashCode(this.provider_address);
        hash = 97 * hash + Objects.hashCode(this.listService);
        hash = 97 * hash + Objects.hashCode(this.listPerson);
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
        final Provider other = (Provider) obj;
        if (!Objects.equals(this.provider_name, other.provider_name)) {
            return false;
        }
        if (!Objects.equals(this.provider_id, other.provider_id)) {
            return false;
        }
        if (!Objects.equals(this.agreement_date, other.agreement_date)) {
            return false;
        }
        if (this.provider_type != other.provider_type) {
            return false;
        }
        if (!Objects.equals(this.provider_category, other.provider_category)) {
            return false;
        }
        if (!Objects.equals(this.provider_address, other.provider_address)) {
            return false;
        }
        if (!Objects.equals(this.listService, other.listService)) {
            return false;
        }
        if (!Objects.equals(this.listPerson, other.listPerson)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Provider{" + "provider_id=" + provider_id + ", provider_name=" + provider_name + ", agreement_date=" + agreement_date + ", provider_type=" + provider_type + ", provider_category=" + provider_category + ", provider_address=" + provider_address + '}';
    }

  
    
}
