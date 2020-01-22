/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.tatsinktechnologic.client.restfull;


import com.tatsinktechnologic.entities.api_gateway.Rest_Method;
import com.tatsinktechnologic.entities.api_gateway.Security_Mode;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Request
 */
public class Request {

    private String user_name;
    
    private String password;
            
    private Rest_Method method;

    private String host;

    private String path;

    private String rest_request;

    private int connexion_timeout;

    private int request_timeout;
    
    private Security_Mode sec_mode;
    
    private MultivaluedMap<String, String> headers;

    private String content_type;
    
    private String media_type;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rest_Method getMethod() {
        return method;
    }

    public void setMethod(Rest_Method method) {
        this.method = method;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRest_request() {
        return rest_request;
    }

    public void setRest_request(String rest_request) {
        this.rest_request = rest_request;
    }

    public int getConnexion_timeout() {
        return connexion_timeout;
    }

    public void setConnexion_timeout(int connexion_timeout) {
        this.connexion_timeout = connexion_timeout;
    }

    public int getRequest_timeout() {
        return request_timeout;
    }

    public void setRequest_timeout(int request_timeout) {
        this.request_timeout = request_timeout;
    }

    public Security_Mode getSec_mode() {
        return sec_mode;
    }

    public void setSec_mode(Security_Mode sec_mode) {
        this.sec_mode = sec_mode;
    }

    public MultivaluedMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(MultivaluedMap<String, String> headers) {
        this.headers = headers;
    }

    

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }
    
    
   
   
}
