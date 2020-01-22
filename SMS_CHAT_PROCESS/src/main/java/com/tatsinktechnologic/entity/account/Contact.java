/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.entity.account;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author olivier.tatsinkou
 */
@Entity
@Table(name = "contact")
public class Contact implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer contact_id;
    
    @Column(nullable = false, unique = true)
    private String contact_name; 
    
     @Column(nullable = true)
    private String street;
     
    @Column(nullable = true)
    private String district;
    
    @Column(nullable = true)
    private String region;
    
    @Column(nullable = true)
    private String city;
     
    @Column(nullable = true)
    private String contry;

    @Column(nullable = true)
    private String postalCode;
    
    @OneToMany(mappedBy = "contact")
    private Set<UserContactRel> userContactRels = new HashSet<>();

    @OneToMany(mappedBy = "contact")
    protected Set<Phone> listphone_number = new HashSet<>();
    
    @OneToMany(mappedBy = "contact")
    protected Set<Email> listEmail = new HashSet<>();

    public Integer getContact_id() {
        return contact_id;
    }

    public void setContact_id(Integer contact_id) {
        this.contact_id = contact_id;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getContry() {
        return contry;
    }

    public void setContry(String contry) {
        this.contry = contry;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Set<UserContactRel> getUserContactRels() {
        return userContactRels;
    }

    public void setUserContactRels(Set<UserContactRel> userContactRels) {
        this.userContactRels = userContactRels;
    }

    public Set<Phone> getListphone_number() {
        return listphone_number;
    }

    public void setListphone_number(Set<Phone> listphone_number) {
        this.listphone_number = listphone_number;
    }

    public Set<Email> getListEmail() {
        return listEmail;
    }

    public void setListEmail(Set<Email> listEmail) {
        this.listEmail = listEmail;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.contact_id);
        hash = 59 * hash + Objects.hashCode(this.contact_name);
        hash = 59 * hash + Objects.hashCode(this.street);
        hash = 59 * hash + Objects.hashCode(this.district);
        hash = 59 * hash + Objects.hashCode(this.region);
        hash = 59 * hash + Objects.hashCode(this.city);
        hash = 59 * hash + Objects.hashCode(this.contry);
        hash = 59 * hash + Objects.hashCode(this.postalCode);
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
        final Contact other = (Contact) obj;
        if (!Objects.equals(this.contact_name, other.contact_name)) {
            return false;
        }
        if (!Objects.equals(this.street, other.street)) {
            return false;
        }
        if (!Objects.equals(this.district, other.district)) {
            return false;
        }
        if (!Objects.equals(this.region, other.region)) {
            return false;
        }
        if (!Objects.equals(this.city, other.city)) {
            return false;
        }
        if (!Objects.equals(this.contry, other.contry)) {
            return false;
        }
        if (!Objects.equals(this.postalCode, other.postalCode)) {
            return false;
        }
        if (!Objects.equals(this.contact_id, other.contact_id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Contact{" + "contact_id=" + contact_id + ", contact_name=" + contact_name + ", street=" + street + ", district=" + district + ", region=" + region + ", city=" + city + ", contry=" + contry + ", postalCode=" + postalCode + '}';
    }

    
   
}
