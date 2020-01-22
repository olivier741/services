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
package com.tatsinktechnologic.main;

import com.tatsinktechnologic.configuration.Smpp_ConfigLoader;
import com.tatsinktechnologic.ws.server.SOAP_HttpServerManagement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tatsinktechnologic.smpp.gateway.SMSGateway;

/**
 *
 * @author olivier.tatsinkou
 */
public class SMSGW {

    /**
     * @param args the command line arguments
     */
    private static final Logger logger = LoggerFactory.getLogger(SMSGW.class);
    
    public static void main(String[] args) {
        // TODO code application logic here
        logger.info("Initialising...");
        
        logger.info("############# START SMS GATEWAY ###################");
        SMSGateway.getSenderGateway();
        
        List<Runnable> runnables = new ArrayList<Runnable>();
        ExecutorService execute = Executors.newSingleThreadExecutor();
        runnables.add(new SOAP_HttpServerManagement());
        SOAP_HttpServerManagement.executeRunnables(execute, runnables);
        
    }
    
}
