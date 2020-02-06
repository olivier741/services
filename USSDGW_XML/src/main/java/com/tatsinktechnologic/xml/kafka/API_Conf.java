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
@Root(name = "api_conf")
public class API_Conf {

    @Element(name = "URL", required = true)
    private String URL;

    @Element(name = "ussdMessage_Timeout", required = true)
    private int ussdMessage_Timeout;

    public API_Conf(@Element(name = "URL", required = true) String URL,
            @Element(name = "ussdMessage_Timeout", required = true) int ussdMessage_Timeout) {
        this.URL = URL;
        this.ussdMessage_Timeout = ussdMessage_Timeout;
    }

    public String getURL() {
        return URL;
    }

    public int getUssdMessage_Timeout() {
        return ussdMessage_Timeout;
    }

    @Override
    public String toString() {
        return "API_Conf{" + "URL=" + URL + ", ussdMessage_Timeout=" + ussdMessage_Timeout + '}';
    }

    
}
