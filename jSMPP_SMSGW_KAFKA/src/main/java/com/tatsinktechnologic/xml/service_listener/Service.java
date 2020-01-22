/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml.service_listener;

import java.util.Objects;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name = "service")
//@Order(elements={"name/first", "name[1]/surname", "age/date", "name[2]/nickname"})
public class Service {

    @Attribute(name = "service_id", required = true)
    private String service_id;

    @Element(name = "description", required = false)
    private String description;

    @Element(name = "poolthread", required = false)
    private int poolthread;
    
    

    @Element(name = "mofilterSMS", required = true)
    private MoFilterSMS mofilter;

    @Element(name = "mtfilterSMS", required = true)
    private MtFilterSMS mtfilter;

    public Service(@Attribute(name = "service_id", required = true) String service_id,
            @Element(name = "description", required = false) String description,
            @Element(name = "poolthread", required = false) int poolthread,
            
            @Element(name = "mofilterSMS", required = true) MoFilterSMS mofilter,
            @Element(name = "mtfilterSMS", required = true) MtFilterSMS mtfilter) {
        this.service_id = service_id;
        this.description = description;
        this.poolthread = poolthread;
        this.mofilter = mofilter;
        this.mtfilter = mtfilter;
    }

    public String getService_id() {
        return service_id;
    }

    public String getDescription() {
        return description;
    }

    public MoFilterSMS getMofilter() {
        return mofilter;
    }

    public MtFilterSMS getMtfilter() {
        return mtfilter;
    }

    public int getPoolthread() {
        return poolthread;
    }

   
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.service_id);
        hash = 11 * hash + Objects.hashCode(this.description);
        hash = 11 * hash + this.poolthread;
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
        if (this.poolthread != other.poolthread) {
            return false;
        }
       
        if (!Objects.equals(this.service_id, other.service_id)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Service{" + "service_id=" + service_id + ", description=" + description + ", poolthread=" + poolthread + '}';
    }


    

}
