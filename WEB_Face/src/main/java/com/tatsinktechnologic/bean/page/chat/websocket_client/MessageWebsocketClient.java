/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tatsinktechnologic.bean.page.chat.websocket_client;

/**
 *
 * @author olivier.tatsinkou
 */
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

public class MessageWebsocketClient extends WebSocketClient{

    public MessageWebsocketClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public MessageWebsocketClient(URI serverURI) {
        super(serverURI);
    }

    public MessageWebsocketClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {

        send("Hello, it is me. Mario :)");

        System.out.println("opened connection");

        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    @Override
    public void onMessage(String message) {

        System.out.println("received: " + message);

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println("Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);

    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }

//    public static void main(String[] args) throws URISyntaxException {
//
//        ExampleClient c = new ExampleClient(new URI("ws://localhost:8887")); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
//
//        c.connect();
//
//    }
}
