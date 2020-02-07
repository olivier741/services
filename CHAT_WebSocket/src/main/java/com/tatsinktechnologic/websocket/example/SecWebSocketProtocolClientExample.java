package com.tatsinktechnologic.websocket.example;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.extensions.IExtension;
import org.java_websocket.protocols.IProtocol;
import org.java_websocket.protocols.Protocol;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This example demonstrates how to use a specific Sec-WebSocket-Protocol for your connection.
 */
public class SecWebSocketProtocolClientExample {

    public static void main( String[] args ) throws URISyntaxException {
        // This draft only allows you to use the specific Sec-WebSocket-Protocol without a fallback.
        Draft_6455 draft_ocppOnly = new Draft_6455(Collections.<IExtension>emptyList(), Collections.<IProtocol>singletonList(new Protocol("ocpp2.0")));

        // This draft allows the specific Sec-WebSocket-Protocol and also provides a fallback, if the other endpoint does not accept the specific Sec-WebSocket-Protocol
        ArrayList<IProtocol> protocols = new ArrayList<IProtocol>();
        protocols.add(new Protocol("ocpp2.0"));
        protocols.add(new Protocol(""));
        Draft_6455 draft_ocppAndFallBack = new Draft_6455(Collections.<IExtension>emptyList(), protocols);

        ExampleClient c = new ExampleClient( new URI( "ws://echo.websocket.org" ), draft_ocppAndFallBack );
        c.connect();
    }
}
