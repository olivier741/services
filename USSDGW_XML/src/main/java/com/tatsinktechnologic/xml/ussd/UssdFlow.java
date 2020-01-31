/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml.ussd;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name = "ussd_flow")
public class UssdFlow {

    @Attribute(name = "service", required = true)
    private String service;

    @Attribute(name = "kafka_topic", required = true)
    private String kafka_topic;

    @Element(name = "menu", required = true)
    private Menu menu;

    public UssdFlow(@Attribute(name = "service", required = true) String service,
                    @Attribute(name = "kafka_topic", required = true) String kafka_topic,
                    @Element(name = "menu", required = true) Menu menu ) {
       
        this.service=service;
        this.kafka_topic=kafka_topic;
        this.menu=menu;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getKafka_topic() {
        return kafka_topic;
    }

    public void setKafka_topic(String kafka_topic) {
        this.kafka_topic = kafka_topic;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
    
    
    
    

}
