/*
 * Copyright 2019 olivier.tatsinkou.
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
@Root(name="kafka_topic_crud")
public class Kafka_Topic_Crud {
    @Element(name="zookeeper_addr",required = false) 
    private String zookeeper_addr;
    
    @Element(name="replication",required = false) 
    private int replication;
    
    @Element(name="partitions",required = false) 
    private int partitions;
    
    @Element(name="sessionTimeOutInMs",required = false) 
    private int sessionTimeOutInMs;
    
    @Element(name="connectionTimeOutInMs",required = false) 
    private int connectionTimeOutInMs;
    
   

    public Kafka_Topic_Crud(    @Element(name="zookeeper_addr",required = false) String zookeeper_addr,
                                @Element(name="replication",required = false) int replication, 
                                @Element(name="partitions",required = false) int partitions,
                                @Element(name="sessionTimeOutInMs",required = false)  int sessionTimeOutInMs,   
                                @Element(name="connectionTimeOutInMs",required = false)  int connectionTimeOutInMs
                               ) {
        this.zookeeper_addr = zookeeper_addr;
        this.replication = replication;
        this.partitions = partitions;
        this.sessionTimeOutInMs=sessionTimeOutInMs;
        this.connectionTimeOutInMs=connectionTimeOutInMs;
    }

    public String getZookeeper_addr() {
        return zookeeper_addr;
    }

    public int getReplication() {
        return replication;
    }

    public int getPartitions() {
        return partitions;
    }

    public int getSessionTimeOutInMs() {
        return sessionTimeOutInMs;
    }

    public int getConnectionTimeOutInMs() {
        return connectionTimeOutInMs;
    }

  
    
    @Override
    public String toString() {
        return "Kafka_Topic_Crud{" + "zookeeper_addr=" + zookeeper_addr + ", replication=" + replication + ", partitions=" + partitions + '}';
    }

    
}
