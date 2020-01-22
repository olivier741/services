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
package com.tatsinktechnologic.configuration;

import com.tatsinktechnologic.xml.service_listener.Service_listener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author olivier.tatsinkou
 */
public class ConfigLoader {

    private static Logger logger = Logger.getLogger(ConfigLoader.class);

    private Service_listener service_listener;
    private Properties consumer_props;
    private Properties product_props;

    private ConfigLoader() {

        File file_service_listener = new File("etc" + File.separator + "service_listener.xml");

        Serializer serializer_service_listener = new Persister();

        // get configuration of service_listener.xml
        try {
            service_listener = serializer_service_listener.read(Service_listener.class, file_service_listener);
            logger.info("successfull load : etc" + File.separator + "service_listener.xml");
        } catch (Exception e) {
            logger.error("ERROR in config file service_listener.xml", e);
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

    public Service_listener getService_listener() {
        return service_listener;
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

}
