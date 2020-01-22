/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml.service_listener;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import java.util.regex.Pattern;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name = "service_listener")
public class Service_listener {

    @Element(name = "fault_msg", required = false)
    private String fault_msg;

    @Element(name = "kafka_topic_crud", required = true)
    private Kafka_Topic_Crud kafka_topic_crud;

    @Element(name = "delivery_topic", required = false)
    private String delivery_topic;
    
    @Element(name = "smpp_report", required = false)
    private int smpp_report;

    @ElementList(inline = true, name = "service", required = true)
    private List<Service> Services = new ArrayList<Service>();

    public Service_listener(@Element(name = "fault_msg", required = false) String fault_msg,
            @Element(name = "kafka_topic_crud", required = true) Kafka_Topic_Crud kafka_topic_crud,
            @Element(name = "delivery_topic", required = false) String delivery_topic,
            @Element(name = "smpp_report", required = false) int smpp_report,
            @ElementList(inline = true, name = "service", required = true) List<Service> Services) {
        this.fault_msg = fault_msg;

        this.kafka_topic_crud = kafka_topic_crud;
        this.delivery_topic = delivery_topic;
        this.smpp_report=smpp_report;
        this.Services = Services;
    }

    public String getFault_msg() {
        return fault_msg;
    }

    public Kafka_Topic_Crud getKafka_topic_crud() {
        return kafka_topic_crud;
    }

    public String getDelivery_topic() {
        return delivery_topic;
    }

    public List<Service> getServices() {
        return Services;
    }

    public int getSmpp_report() {
        return smpp_report;
    }

    public static List<Service> getReport_ServiceMo(final String receiver, final String sender, List<Service> listService) {

        List<Service> result = new ArrayList<Service>();
        for (Service serv : listService) {
            result.add(serv);
        }

        CollectionUtils.filter(result, new Predicate() {

            @Override
            public boolean evaluate(Object o) {

                String checkSender = null;
                String checkReceiver = null;

                checkSender = ((Service) o).getMofilter().getSender();
                checkReceiver = ((Service) o).getMofilter().getReceiver();

                Pattern pattern_send = Pattern.compile(checkSender);
                Pattern pattern_rec = Pattern.compile(checkReceiver);

                boolean match_send = pattern_send.matcher(sender).find();
                boolean match_rec = pattern_rec.matcher(receiver).find();

                return match_send && match_rec;
            }

        });

        return result;
    }

    public static List<Service> getSMS_ServiceMo(final String content, final String receiver, final String sender, List<Service> listService) {

        List<Service> result = new ArrayList<Service>();
        for (Service serv : listService) {
            result.add(serv);
        }

        CollectionUtils.filter(result, new Predicate() {

            @Override
            public boolean evaluate(Object o) {

                String checkContent = null;
                String checkSender = null;
                String checkReceiver = null;

                checkContent = ((Service) o).getMofilter().getContent();
                checkSender = ((Service) o).getMofilter().getSender();
                checkReceiver = ((Service) o).getMofilter().getReceiver();

                Pattern pattern_cont = Pattern.compile(checkContent);
                Pattern pattern_send = Pattern.compile(checkSender);
                Pattern pattern_rec = Pattern.compile(checkReceiver);

                boolean match_cont = pattern_cont.matcher(content).find();
                boolean match_send = pattern_send.matcher(sender).find();
                boolean match_rec = pattern_rec.matcher(receiver).find();

                return match_cont && match_send && match_rec;
            }

        });

        return result;
    }

    public static List<Service> getSMS_ServiceMt(final String user, final String content, final String receiver, final String sender, List<Service> listService) {

        List<Service> result = new ArrayList<Service>();
        for (Service serv : listService) {
            result.add(serv);
        }

        CollectionUtils.filter(result, new Predicate() {

            @Override
            public boolean evaluate(Object o) {

                String checkContent = null;
                String checkSender = null;
                String checkReceiver = null;
                String checkService_id = null;

                checkContent = ((Service) o).getMtfilter().getContent();
                checkSender = ((Service) o).getMtfilter().getSender();
                checkReceiver = ((Service) o).getMtfilter().getReceiver();
                checkService_id = ((Service) o).getService_id();

                Pattern pattern_cont = Pattern.compile(checkContent);
                Pattern pattern_send = Pattern.compile(checkSender);
                Pattern pattern_rec = Pattern.compile(checkReceiver);

                boolean match_cont = pattern_cont.matcher(content).find();
                boolean match_send = pattern_send.matcher(sender).find();
                boolean match_rec = pattern_rec.matcher(receiver).find();

                return checkService_id.equals(user) && match_cont && match_send && match_rec;
            }

        });

        return result;
    }

    @Override
    public String toString() {
        return "Service_listener{" + "fault_msg=" + fault_msg + ", kafka_topic_crud=" + kafka_topic_crud + ", delivery_topic=" + delivery_topic + ", Services=" + Services + '}';
    }

}
