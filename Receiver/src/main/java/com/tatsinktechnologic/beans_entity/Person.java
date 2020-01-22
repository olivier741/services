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
import java.util.Objects;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author olivier
 */
@Entity
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = LogManager.getLogger(Person.class);
        
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pers_generator")
    @SequenceGenerator(name="pers_generator", sequenceName = "pers_seq",allocationSize=1)
    private Long person_id;

    private String first_name;
    private String last_name;
    private Date bith_date;
    private String bith_place;
    private String CNI;
    private String Profession;
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = true)
    private Address address;
         
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = true)
    private Provider provider;

    public Long getPerson_id() {
        return person_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public Date getBith_date() {
        return bith_date;
    }

    public String getBith_place() {
        return bith_place;
    }

    public String getCNI() {
        return CNI;
    }

    public String getProfession() {
        return Profession;
    }

    public Address getAddress() {
        return address;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setPerson_id(Long person_id) {
        this.person_id = person_id;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setBith_date(Date bith_date) {
        this.bith_date = bith_date;
    }

    public void setBith_place(String bith_place) {
        this.bith_place = bith_place;
    }

    public void setCNI(String CNI) {
        this.CNI = CNI;
    }

    public void setProfession(String Profession) {
        this.Profession = Profession;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.person_id);
        hash = 83 * hash + Objects.hashCode(this.first_name);
        hash = 83 * hash + Objects.hashCode(this.last_name);
        hash = 83 * hash + Objects.hashCode(this.bith_date);
        hash = 83 * hash + Objects.hashCode(this.bith_place);
        hash = 83 * hash + Objects.hashCode(this.CNI);
        hash = 83 * hash + Objects.hashCode(this.Profession);
        hash = 83 * hash + Objects.hashCode(this.address);
        hash = 83 * hash + Objects.hashCode(this.provider);
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
        final Person other = (Person) obj;
        if (!Objects.equals(this.first_name, other.first_name)) {
            return false;
        }
        if (!Objects.equals(this.last_name, other.last_name)) {
            return false;
        }
        if (!Objects.equals(this.bith_place, other.bith_place)) {
            return false;
        }
        if (!Objects.equals(this.CNI, other.CNI)) {
            return false;
        }
        if (!Objects.equals(this.Profession, other.Profession)) {
            return false;
        }
        if (!Objects.equals(this.person_id, other.person_id)) {
            return false;
        }
        if (!Objects.equals(this.bith_date, other.bith_date)) {
            return false;
        }
        if (!Objects.equals(this.address, other.address)) {
            return false;
        }
        if (!Objects.equals(this.provider, other.provider)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Person{" + "person_id=" + person_id + ", first_name=" + first_name + ", last_name=" + last_name + ", bith_date=" + bith_date + ", bith_place=" + bith_place + ", CNI=" + CNI + ", Profession=" + Profession + ", address=" + address + ", provider=" + provider + '}';
    }
  
    
    
}
