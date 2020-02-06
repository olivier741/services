/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml.kafka;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name = "ussd_config")
public class USSD_Conf {

    @Element(name = "connect_type", required = true)
    private String connect_type;
    
    @Element(name = "kafka_conf", required = true)
    private KAFKA_Conf kafka_conf;

   @Element(name = "api_conf", required = true)
    private API_Conf api_conf;
   
   @Element(name = "smpp_conf", required = true)
    private SMPP_Conf smpp_conf;

    public USSD_Conf(@Element(name = "connect_type", required = true)String connect_type,
            @Element(name = "kafka_conf", required = true) KAFKA_Conf kafka_conf,
            @Element(name = "api_conf", required = true) API_Conf api_conf,
            @Element(name = "smpp_conf", required = true) SMPP_Conf smpp_conf
            ) {
        this.kafka_conf = kafka_conf;
        this.connect_type=connect_type;
        this.api_conf=api_conf;
        this.smpp_conf=smpp_conf;
        
    }

    public String getConnect_type() {
        return connect_type;
    }

    public API_Conf getApi_conf() {
        return api_conf;
    }

    public KAFKA_Conf getKafka_conf() {
        return kafka_conf;
    }

    public SMPP_Conf getSmpp_conf() {
        return smpp_conf;
    }

    @Override
    public String toString() {
        return "USSD_Conf{" + "connect_type=" + connect_type + ", kafka_conf=" + kafka_conf + ", api_conf=" + api_conf + ", smpp_conf=" + smpp_conf + '}';
    }
    
    

}
