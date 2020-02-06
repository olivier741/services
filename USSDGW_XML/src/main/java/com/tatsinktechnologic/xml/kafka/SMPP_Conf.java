/*
 * Copyright 2020 olivier.tatsinkou.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tatsinktechnologic.xml.kafka;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name = "smpp_conf")
public class SMPP_Conf {

    @Element(name = "deliviery_topic", required = true)
    private String deliviery_topic;

    @Element(name = "enable_delivery", required = true)
    private int enable_delivery;

    @Element(name = "date_format", required = true)
    private String date_format;

    public SMPP_Conf(@Element(name = "deliviery_topic", required = true) String deliviery_topic,
            @Element(name = "enable_delivery", required = true) int enable_delivery,
            @Element(name = "date_format", required = true) String date_format) {
        this.deliviery_topic = deliviery_topic;
        this.enable_delivery = enable_delivery;
        this.date_format = date_format;
    }

    public String getDeliviery_topic() {
        return deliviery_topic;
    }

    public int getEnable_delivery() {
        return enable_delivery;
    }

    public String getDate_format() {
        return date_format;
    }

    @Override
    public String toString() {
        return "SMPP_Conf{" + "deliviery_topic=" + deliviery_topic + ", enable_delivery=" + enable_delivery + ", date_format=" + date_format + '}';
    }
    
    

}
