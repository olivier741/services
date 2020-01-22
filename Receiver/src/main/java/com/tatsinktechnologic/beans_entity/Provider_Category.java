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
public class Provider_Category implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Transient
    private Logger logger = LogManager.getLogger(Provider_Category.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pvd_cag_generator")
    @SequenceGenerator(name="pvd_cag_generator", sequenceName = "pvd_cag_seq",allocationSize=1)
    private Long provider_category_id;

    private String Categery_name;
    private String Description;
    
    @OneToMany(mappedBy = "provider_category",fetch = FetchType.LAZY)
    private Set<Provider> listProvider = new HashSet<>();

    public Long getProvider_category_id() {
        return provider_category_id;
    }

    public String getCategery_name() {
        return Categery_name;
    }

    public String getDescription() {
        return Description;
    }

    public Set<Provider> getListProvider() {
        return listProvider;
    }

    public void setProvider_category_id(Long provider_category_id) {
        this.provider_category_id = provider_category_id;
    }

    public void setCategery_name(String Categery_name) {
        this.Categery_name = Categery_name;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public void setListProvider(Set<Provider> listProvider) {
        this.listProvider = listProvider;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.provider_category_id);
        hash = 67 * hash + Objects.hashCode(this.Categery_name);
        hash = 67 * hash + Objects.hashCode(this.Description);
        hash = 67 * hash + Objects.hashCode(this.listProvider);
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
        final Provider_Category other = (Provider_Category) obj;
        if (!Objects.equals(this.Categery_name, other.Categery_name)) {
            return false;
        }
        if (!Objects.equals(this.Description, other.Description)) {
            return false;
        }
        if (!Objects.equals(this.provider_category_id, other.provider_category_id)) {
            return false;
        }
        if (!Objects.equals(this.listProvider, other.listProvider)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Provider_Category{" + "provider_category_id=" + provider_category_id + ", Categery_name=" + Categery_name + ", Description=" + Description + '}';
    }

    

   
   
   
}
