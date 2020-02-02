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
package com.tatsinktechnologic.config;

import com.tatsinktechnologic.beans.UssdMenu;
import com.tatsinktechnologic.utils.FileManagement;
import com.tatsinktechnologic.xml.kafka.Kafka_Topic_Crud;
import com.tatsinktechnologic.xml.ussd.Menu;
import com.tatsinktechnologic.xml.ussd.NextMenu;
import com.tatsinktechnologic.xml.ussd.Ussd;
import com.tatsinktechnologic.xml.ussd.UssdFlow;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author olivier.tatsinkou
 */
public class ConfigLoader {

    private static Logger logger = Logger.getLogger(ConfigLoader.class);

    private Kafka_Topic_Crud kafka_Topic_Crud;
    private List<Ussd> listUssd;
    private  HashMap<String,UssdMenu> setUssdMenu;

    private Properties consumer_props;
    private Properties product_props;

    private ConfigLoader() {

        File file_kafka_config_listener = new File("etc" + File.separator + "kafka_config.xml");

        Serializer serializer_kafka_config_listener = new Persister();

        // get configuration of service_listener.xml
        try {
            kafka_Topic_Crud = serializer_kafka_config_listener.read(Kafka_Topic_Crud.class, file_kafka_config_listener);
            logger.info("successfull load : etc" + File.separator + "kafka_config.xml");
        } catch (Exception e) {
            logger.error("ERROR in config file kafka_config.xml", e);
        }

        try {

            List<File> listFile = FileManagement.getListFile("etc" + File.separator + "ussd_menu", "xml");
            if (listFile != null) {
                listUssd = new ArrayList<Ussd>();
                for (File file : listFile) {
                    try {
                        Serializer serializer_ussd_listener = new Persister();
                        Ussd ussd = serializer_ussd_listener.read(Ussd.class, file);
                        listUssd.add(ussd);
                        logger.info("successfull load : " + file.getAbsolutePath());
                    } catch (Exception e) {
                        logger.error("ERROR in config file" + file.getAbsolutePath(), e);
                    }

                }
            }
        } catch (Exception e) {
        }

        setUssdMenu = new HashMap<String,UssdMenu>();
        if (listUssd != null && !listUssd.isEmpty()) {
            for (Ussd ussd : listUssd) {
                List<UssdFlow> listUssdFlow = ussd.getListUssdFlow();
                for (UssdFlow ussdflow : listUssdFlow) {
                    Menu menu = ussdflow.getMenu();
                    loadSetUssdMenu(menu, "");
                }

            }
        }

        product_props = new Properties();

        try {
            product_props.load(new FileInputStream("etc" + File.separator + "producer.properties"));
        } catch (IOException e) {
            logger.error("cannot load producer.properties config file", e);
        }

        consumer_props = new Properties();
        try {
            consumer_props.load(new FileInputStream("etc" + File.separator + "consumer.properties"));
        } catch (IOException e) {
            logger.error("cannot load consumer.properties config file", e);
        }

    }

    private static class SingletonConfig {

        private static final ConfigLoader _configLoad = new ConfigLoader();
    }

    public static ConfigLoader getConfigurationLoader() {
        return SingletonConfig._configLoad;
    }

    public Kafka_Topic_Crud getKafka_Topic_Crud() {
        return kafka_Topic_Crud;
    }

    public List<Ussd> getListUssd() {
        return listUssd;
    }

    public Properties getConsumer_props() {
        return consumer_props;
    }

    public void setConsumer_props(Properties consumer_props) {
        this.consumer_props = consumer_props;
    }

    public Properties getProduct_props() {
        return product_props;
    }

    public void setProduct_props(Properties product_props) {
        this.product_props = product_props;
    }

    public HashMap<String, UssdMenu> getSetUssdMenu() {
        return setUssdMenu;
    }
    
    

    private void loadSetUssdMenu(Menu menu, String pre_input) {
        String input = menu.getInput();
        String status = menu.getStatus();
        String resp = menu.getResp();
        String action = menu.getAction();
        String desc = menu.getDesc();

        if (!StringUtils.isBlank(pre_input)) {
            input = pre_input + "-" + input;
        }

        if (!StringUtils.isBlank(resp)) {
            resp = resp.replace("_eof_", "\n");
        }

        UssdMenu ussdMenu = new UssdMenu(input, status, resp, action, desc);
        if (!setUssdMenu.containsKey(input)) {
            setUssdMenu.put(input, ussdMenu);
            logger.info("successfull load Menu: " + ussdMenu);
            NextMenu nextMenu = menu.getNextMenu();
            if (nextMenu != null) {
                List<Menu> listMenu = nextMenu.getListMenu();
                if (listMenu != null && !listMenu.isEmpty()) {
                    for (Menu menu1 : listMenu) {
                        loadSetUssdMenu(menu1, input);
                    }
                }
            }
        }else{
          logger.error("-------------Error: ussd flow already exist----------------"); 
          logger.error(ussdMenu);  
          
        }

    }

}
