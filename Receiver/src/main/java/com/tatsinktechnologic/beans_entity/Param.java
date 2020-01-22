/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.beans_entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
/**
 *
 * @author olivier
 */
@Embeddable
public class Param implements Serializable {
    
    @Transient
    private Logger logger = LogManager.getLogger(Mt_Message_His.class);
    
    private String param_name;
    private String paramDescription;

    public String getParam_name() {
        return param_name;
    }

    public String getParamDescription() {
        return paramDescription;
    }
    

    public void setParam_name(String param_name) {
        this.param_name = param_name;
    }

    public void setParamDescription(String paramDescription) {
        this.paramDescription = paramDescription;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.param_name);
        hash = 89 * hash + Objects.hashCode(this.paramDescription);
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
        final Param other = (Param) obj;
        if (!Objects.equals(this.param_name, other.param_name)) {
            return false;
        }
        if (!Objects.equals(this.paramDescription, other.paramDescription)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Param{" + "param_name=" + param_name + ", paramDescription=" + paramDescription + '}';
    }
    
    
   
  
    
}
