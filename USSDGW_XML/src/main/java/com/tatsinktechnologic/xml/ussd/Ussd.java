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
package com.tatsinktechnologic.xml.ussd;

import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 *
 * @author olivier.tatsinkou
 */
@Root(name = "ussd")
public class Ussd {

    @ElementList(inline = true, name = "ussd_flow", required = true)
    private List<UssdFlow> listUssdFlow = new ArrayList<UssdFlow>();

    public Ussd(@ElementList(inline = true, name = "ussd_flow", required = true) List<UssdFlow> listUssdFlow) {
        this.listUssdFlow = listUssdFlow;
    }

    public List<UssdFlow> getListUssdFlow() {
        return listUssdFlow;
    }

    public void setListUssdFlow(List<UssdFlow> listUssdFlow) {
        this.listUssdFlow = listUssdFlow;
    }
    
    

}
