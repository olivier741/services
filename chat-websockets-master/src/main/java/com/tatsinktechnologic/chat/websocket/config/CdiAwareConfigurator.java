package com.tatsinktechnologic.chat.websocket.config;

import javax.enterprise.inject.spi.CDI;
import javax.websocket.server.ServerEndpointConfig;

/**
 * CDI configurator for WebSocket endpoints (instances are managed by CDI).
 *
 * @author olivier.tatsinkou
 */
public class CdiAwareConfigurator extends ServerEndpointConfig.Configurator {

    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        return CDI.current().select(endpointClass).get();
    }
}