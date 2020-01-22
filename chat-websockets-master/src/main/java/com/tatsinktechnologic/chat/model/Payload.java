package com.tatsinktechnologic.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Interface that must be implemented by classes that represents the payload of a {@link WebSocketMessage}.
 *
 * @author olivier.tatsinkou
 */
public interface Payload {

    @JsonIgnore
    String getType();
}
