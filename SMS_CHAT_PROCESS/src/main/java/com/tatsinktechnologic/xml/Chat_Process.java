/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name = "chat_process")
public class Chat_Process {

    @Attribute(name = "type", required = true)
    private Chat_Type type;

    @Attribute(name = "regx_label", required = false)
    private String regx_label;

    @Element(name = "service_name", required = false)
    private String service_name;

    @Element(name = "list_product_name", required = false)
    private String list_product_name;

    @Element(name = "numberThread", required = true)
    private int numberThread;

    @Element(name = "threadPool", required = true)
    private int threadPool;

    @Element(name = "sleep_duration", required = true)
    private int sleep_duration;

    @Element(name = "maxQueue", required = true)
    private int maxQueue;

    @Element(name = "msg_format", required = false)
    private String msg_format;

    public Chat_Process(@Attribute(name = "type", required = true) Chat_Type type,
            @Attribute(name = "regx_label", required = false) String regx_label,
            @Element(name = "service_name", required = false) String service_name,
            @Element(name = "list_product_name", required = false) String list_product_name,
            @Element(name = "numberThread", required = true) int numberThread,
            @Element(name = "threadPool", required = true) int threadPool,
            @Element(name = "sleep_duration", required = true) int sleep_duration,
            @Element(name = "maxQueue", required = true) int maxQueue,
            @Element(name = "msg_format", required = false) String msg_format) {
        this.type = type;
        this.regx_label = regx_label;
        this.service_name = service_name;
        this.list_product_name = list_product_name;
        this.numberThread = numberThread;
        this.threadPool = threadPool;
        this.sleep_duration = sleep_duration;
        this.maxQueue = maxQueue;
        this.msg_format = msg_format;
    }

    public int getNumberThread() {
        return numberThread;
    }

    public int getThreadPool() {
        return threadPool;
    }

    public int getSleep_duration() {
        return sleep_duration;
    }

    public int getMaxQueue() {
        return maxQueue;
    }

    public Chat_Type getType() {
        return type;
    }

    public String getRegx_label() {
        return regx_label;
    }

    public String getService_name() {
        return service_name;
    }

    public String getList_product_name() {
        return list_product_name;
    }

    public String getMsg_format() {
        return msg_format;
    }

    @Override
    public String toString() {
        return "Chat_Process{" + "type=" + type + ", regx_label=" + regx_label + ", service_name=" + service_name + ", list_product_name=" + list_product_name + ", numberThread=" + numberThread + ", threadPool=" + threadPool + ", sleep_duration=" + sleep_duration + ", maxQueue=" + maxQueue + ", msg_format=" + msg_format + '}';
    }

}
