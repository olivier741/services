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
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = LogManager.getLogger(Address.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addr_generator")
    @SequenceGenerator(name="addr_generator", sequenceName = "addr_seq",allocationSize=1)
    private Long address_id;

    private String address_name;
    private String BP;
    private String Contry;
    private String Region;
    private String District;
    private String town;
    
    @ElementCollection
        @CollectionTable(
        name = "LIST_PHONE_NUMBER",
        joinColumns = @JoinColumn(name = "address_id")
    )
    @Column(name = "PHONE_NUMBER")
    protected Set<String> listphone_numbe = new HashSet<String>();
    
    @ElementCollection
        @CollectionTable(
        name = "LIST_EMAIL",
        joinColumns = @JoinColumn(name = "address_id")
    )
    @Column(name = "EMAIL")
    protected Set<String> listEmail = new HashSet<String>();
    
    @OneToMany(mappedBy = "address",fetch = FetchType.LAZY)
    private Set<Person> listPerson = new HashSet<>();

    public Long getAddress_id() {
        return address_id;
    }

    public String getAddress_name() {
        return address_name;
    }

    public String getBP() {
        return BP;
    }

    public String getContry() {
        return Contry;
    }

    public String getRegion() {
        return Region;
    }

    public String getDistrict() {
        return District;
    }

    public String getTown() {
        return town;
    }

    public Set<String> getListphone_numbe() {
        return listphone_numbe;
    }

    public Set<String> getListEmail() {
        return listEmail;
    }

    public Set<Person> getListPerson() {
        return listPerson;
    }

    public void setAddress_id(Long address_id) {
        this.address_id = address_id;
    }

    public void setAddress_name(String address_name) {
        this.address_name = address_name;
    }

    public void setBP(String BP) {
        this.BP = BP;
    }

    public void setContry(String Contry) {
        this.Contry = Contry;
    }

    public void setRegion(String Region) {
        this.Region = Region;
    }

    public void setDistrict(String District) {
        this.District = District;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public void setListphone_numbe(Set<String> listphone_numbe) {
        this.listphone_numbe = listphone_numbe;
    }

    public void setListEmail(Set<String> listEmail) {
        this.listEmail = listEmail;
    }

    public void setListPerson(Set<Person> listPerson) {
        this.listPerson = listPerson;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.address_id);
        hash = 31 * hash + Objects.hashCode(this.address_name);
        hash = 31 * hash + Objects.hashCode(this.BP);
        hash = 31 * hash + Objects.hashCode(this.Contry);
        hash = 31 * hash + Objects.hashCode(this.Region);
        hash = 31 * hash + Objects.hashCode(this.District);
        hash = 31 * hash + Objects.hashCode(this.town);
        hash = 31 * hash + Objects.hashCode(this.listphone_numbe);
        hash = 31 * hash + Objects.hashCode(this.listEmail);
        hash = 31 * hash + Objects.hashCode(this.listPerson);
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
        final Address other = (Address) obj;
        if (!Objects.equals(this.address_name, other.address_name)) {
            return false;
        }
        if (!Objects.equals(this.BP, other.BP)) {
            return false;
        }
        if (!Objects.equals(this.Contry, other.Contry)) {
            return false;
        }
        if (!Objects.equals(this.Region, other.Region)) {
            return false;
        }
        if (!Objects.equals(this.District, other.District)) {
            return false;
        }
        if (!Objects.equals(this.town, other.town)) {
            return false;
        }
        if (!Objects.equals(this.address_id, other.address_id)) {
            return false;
        }
        if (!Objects.equals(this.listphone_numbe, other.listphone_numbe)) {
            return false;
        }
        if (!Objects.equals(this.listEmail, other.listEmail)) {
            return false;
        }
        if (!Objects.equals(this.listPerson, other.listPerson)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Address{" + "address_id=" + address_id + ", address_name=" + address_name + ", BP=" + BP + ", Contry=" + Contry + ", Region=" + Region + ", District=" + District + ", town=" + town + '}';
    }

    
    
}
