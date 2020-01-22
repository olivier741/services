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
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name="user_connection")
public class Rcv_User_Connection {
    @Attribute(name="service_id",required = true) 
    private String service_id;
    @Attribute(name="password" ,required = true)
    private String password ;
    @Attribute( name="ws_security_mode" ,required = true)
    private int security_mode;

    
    @ElementList(inline=true, name="address" ,required = true) 
    private List<Rcv_Address> listaddress = new ArrayList<Rcv_Address>();

    public Rcv_User_Connection( @Attribute(name="service_id",required = true) String service_id,
                                @Attribute(name="password" ,required = true) String password,
                                @Attribute( name="ws_security_mode" ,required = true) int security_mode,
                                @ElementList(inline=true, name="address" ,required = true) List<Rcv_Address> listaddress) {
        this.service_id = service_id;
        this.password=password;
        this.security_mode=security_mode;
        this.listaddress=listaddress;
    }

    public String getService_id() {
        return service_id;
    }

    public String getPassword() {
        return password;
    }
    
    public int getSecurity_mode() {
        return security_mode;
    }
    
    public List<Rcv_Address> getListaddress() {
        return listaddress;
    }

    @Override
    public String toString() {
        return "Rcv_User_Connection{" + "service_id=" + service_id + ", password=" + password + ", security_mode=" + security_mode + ", listaddress=" + listaddress + '}';
    }

}
