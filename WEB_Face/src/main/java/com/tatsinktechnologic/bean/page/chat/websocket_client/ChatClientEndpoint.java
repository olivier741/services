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
import com.tatsinktechnologic.bean.page.chat.MessageWs;
import com.tatsinktechnologic.bean.page.chat.websocket_server.MessageWSDecoder;
import com.tatsinktechnologic.bean.page.chat.websocket_server.MessageWSEncoder;
import java.io.Serializable;
import java.net.URI;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

@ClientEndpoint(encoders = MessageWSEncoder.class, decoders = MessageWSDecoder.class)
public class ChatClientEndpoint implements Serializable{

    Session userSession = null;
    private MessageHandler messageHandler;
    private String URL;

    public ChatClientEndpoint(String url) {
        this.URL = url;
        try {
            URI endpointURI = new URI(URL);
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        this.userSession = null;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a
     * client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(MessageWs msgWS) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(msgWS);
        }
    }

    /**
     * register message handler
     *
     * @param message
     */
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    /**
     * Send a message.
     *
     * @param user
     * @param message
     */
    public void sendMessage(MessageWs msgWS) {
        this.userSession.getAsyncRemote().sendObject(msgWS);
    }

    /**
     * Message handler.
     *
     * @author Jiji_Sasidharan
     */
    public static interface MessageHandler {

        public void handleMessage(MessageWs msgWS);
    }



}
