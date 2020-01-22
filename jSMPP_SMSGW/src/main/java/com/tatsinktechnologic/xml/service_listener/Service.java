/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml.service_listener;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
/**
 *
 * @author olivier.tatsinkou
 */
@Root(name="service")
//@Order(elements={"name/first", "name[1]/surname", "age/date", "name[2]/nickname"})
public class Service  {
    @Attribute(name="service_id",required = true) 
    private String service_id;
    
    @Element(name="description",required = false) 
    private String description;
    
    @Element(name="mofilterSMS" ,required = true) 
    private MoFilterSMS mofilter;
    
    @Element( name="mtfilterSMS" ,required = true) 
    private MtFilterSMS mtfilter;

    public Service(@Attribute(name="service_id",required = true) String service_id, 
                   @Element(name="description",required = false) String description,
                   @Element( name="mofilterSMS" ,required = true) MoFilterSMS mofilter,
                   @Element(name="mtfilterSMS" ,required = true) MtFilterSMS mtfilter) {
        this.service_id = service_id;
        this.description = description;
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

    @Override
    public String toString() {
        return "Service{" + "service_name=" + service_id + ", description=" + description + ", mofilter=" + mofilter + ", mtfilter=" + mtfilter + '}';
    }

}
