package com.tatsinktechnologic.chat.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tatsinktechnologic.chat.model.payload.BroadcastAvailableUsersPayload;
import com.tatsinktechnologic.chat.model.payload.BroadcastConnectedUserPayload;
import com.tatsinktechnologic.chat.model.payload.BroadcastDisconnectedUserPayload;
import com.tatsinktechnologic.chat.model.payload.BroadcastTextMessagePayload;
import com.tatsinktechnologic.chat.model.payload.SendTextMessagePayload;
import com.tatsinktechnologic.chat.model.payload.WelcomeUserPayload;

/**
 * WebSocket message.
 * <p>
 * The <code>type<code/> field indicates the type of the <code>payload</code> field.
 *
 * @author olivier.tatsinkou
 */
public class WebSocketMessage {

    private String type;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = BroadcastAvailableUsersPayload.class, name = BroadcastAvailableUsersPayload.TYPE),
            @JsonSubTypes.Type(value = BroadcastConnectedUserPayload.class, name = BroadcastConnectedUserPayload.TYPE),
            @JsonSubTypes.Type(value = BroadcastDisconnectedUserPayload.class, name = BroadcastDisconnectedUserPayload.TYPE),
            @JsonSubTypes.Type(value = BroadcastTextMessagePayload.class, name = BroadcastTextMessagePayload.TYPE),
            @JsonSubTypes.Type(value = SendTextMessagePayload.class, name = SendTextMessagePayload.TYPE),
            @JsonSubTypes.Type(value = WelcomeUserPayload.class, name = WelcomeUserPayload.TYPE)
    })
    private Payload payload;

    public WebSocketMessage() {

    }

    public WebSocketMessage(Payload payload) {
        this.type = payload.getType();
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }
}
