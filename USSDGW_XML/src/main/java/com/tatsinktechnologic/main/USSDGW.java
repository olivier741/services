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
package com.tatsinktechnologic.main;

import com.tatsinktechnologic.config.ConfigLoader;
import com.tatsinktechnologic.xml.ussd.Ussd;
import java.io.File;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author olivier.tatsinkou
 */
public class USSDGW {

    /**
     * @param args the command line arguments
     */
    
    private static Logger logger = Logger.getLogger(USSDGW.class);
    private static ConfigLoader communConf;
    
    public static void main(String[] args) {
        // TODO code application logic here
        
         PropertyConfigurator.configure("etc" + File.separator + "log4j.cfg");
        logger.info("Load log4j config file done.");
        communConf = ConfigLoader.getConfigurationLoader();
        List<Ussd> listUssd = communConf.getListUssd();
        
        for(Ussd ussd : listUssd){
            
        }
        
    }
    
}
