/*
 * Copyright 2018 olivier.tatsinkou.
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
package com.tatsinktechnologic.xml.user_listener;

import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name="receiver_user")
public class Receiver_User_Listener {

    @Element( name="failover" ,required = true)
    private Failover failover;
    
    @Element( name="smpp_enableReport" ,required = true)
    private int smpp_enableReport; 
    
    @Element( name="smspp_dataFormat" ,required = false)
    private String smspp_dataFormat;  
     
    @ElementList(inline=true, name="user_connection" ,required = true)
    private List<Rcv_User_Connection> listUser_Connection = new ArrayList<Rcv_User_Connection>();

    public Receiver_User_Listener(@Element( name="failover" ,required = true) Failover failover,
                                  @Element( name="smpp_enableReport" ,required = true) int smpp_enableReport,
                                  @Element( name="smspp_dataFormat" ,required = false)String smspp_dataFormat,  
                                  @ElementList(inline=true, name="user_connection" ,required = true) List<Rcv_User_Connection> listUser_Connection) {
        this.failover=failover;
        this.smpp_enableReport=smpp_enableReport;
        this.smspp_dataFormat=smspp_dataFormat;
        this.listUser_Connection = listUser_Connection;     
    }

    public Failover getFailover() {
        return failover;
    }

    public int getSmpp_enableReport() {
        return smpp_enableReport;
    }

    public String getSmspp_dataFormat() {
        return smspp_dataFormat;
    }

    public List<Rcv_User_Connection> getListUser_Connection() {
        return listUser_Connection;
    }

    public  Rcv_User_Connection selectUser_con(String service_id){
        Rcv_User_Connection result = null;
        if (listUser_Connection != null){
            for (Rcv_User_Connection e : listUser_Connection){
                if (e.getService_id().equals(service_id)){
                    result = e;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Receiver_User_Listener{" + "failover=" + failover + ", smpp_enableReport=" + smpp_enableReport + ", smspp_dataFormat=" + smspp_dataFormat + ", listUser_Connection=" + listUser_Connection + '}';
    }
 
}
