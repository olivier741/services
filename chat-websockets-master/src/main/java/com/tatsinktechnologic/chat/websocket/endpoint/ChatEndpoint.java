package com.tatsinktechnologic.chat.websocket.endpoint;


import com.tatsinktechnologic.chat.model.payload.BroadcastConnectedUserPayload;
import com.tatsinktechnologic.chat.model.payload.SendTextMessagePayload;
import com.tatsinktechnologic.chat.model.payload.BroadcastTextMessagePayload;
import com.tatsinktechnologic.chat.model.payload.BroadcastAvailableUsersPayload;
import com.tatsinktechnologic.chat.model.payload.BroadcastDisconnectedUserPayload;
import com.tatsinktechnologic.chat.model.payload.WelcomeUserPayload;
import com.tatsinktechnologic.chat.model.WebSocketMessage;
import com.tatsinktechnologic.chat.websocket.codec.MessageDecoder;
import com.tatsinktechnologic.chat.websocket.codec.MessageEncoder;
import com.tatsinktechnologic.chat.websocket.config.CdiAwareConfigurator;

import javax.enterprise.context.Dependent;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * WebSocket websocket for the chat.
 *
 * @author olivier.tatsinkou
 */
@Dependent
@ServerEndpoint(
        value = "/chat",
        encoders = MessageEncoder.class,
        decoders = MessageDecoder.class,
        configurator = CdiAwareConfigurator.class)
public class ChatEndpoint {

    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        String username = session.getUserPrincipal().getName();
        welcomeUser(session, username);
        broadcastUserConnected(session, username);
        broadcastAvailableUsers();
    }

    @OnMessage
    public void onMessage(Session session, WebSocketMessage message) {
        if (message.getPayload() instanceof SendTextMessagePayload) {
            SendTextMessagePayload payload = (SendTextMessagePayload) message.getPayload();
            broadcastTextMessage(session.getUserPrincipal().getName(), payload.getContent());
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        String username = session.getUserPrincipal().getName();
        broadcastUserDisconnected(username);
        broadcastAvailableUsers();
    }

    private void welcomeUser(Session currentSession, String username) {
        WelcomeUserPayload payload = new WelcomeUserPayload();
        payload.setUsername(username);
        currentSession.getAsyncRemote().sendObject(new WebSocketMessage(payload));
    }

    private void broadcastUserConnected(Session currentSession, String username) {
        BroadcastConnectedUserPayload payload = new BroadcastConnectedUserPayload();
        payload.setUsername(username);
        broadcast(currentSession, new WebSocketMessage(payload));
    }

    private void broadcastUserDisconnected(String username) {
        BroadcastDisconnectedUserPayload payload = new BroadcastDisconnectedUserPayload();
        payload.setUsername(username);
        broadcast(new WebSocketMessage(payload));
    }

    private void broadcastTextMessage(String username, String text) {
        BroadcastTextMessagePayload payload = new BroadcastTextMessagePayload();
        payload.setContent(text);
        payload.setUsername(username);
        broadcast(new WebSocketMessage(payload));
    }

    private void broadcastAvailableUsers() {

        Set<String> usernames = sessions.stream()
                .map(Session::getUserPrincipal)
                .map(Principal::getName)
                .distinct()
                .collect(Collectors.toSet());

        BroadcastAvailableUsersPayload payload = new BroadcastAvailableUsersPayload();
        payload.setUsernames(usernames);
        broadcast(new WebSocketMessage(payload));
    }

    private void broadcast(WebSocketMessage message) {
        synchronized (sessions) {
            sessions.forEach(session -> {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendObject(message);
                }
            });
        }
    }

    private void broadcast(Session ignoredSession, WebSocketMessage message) {
        synchronized (sessions) {
            sessions.forEach(session -> {
                if (session.isOpen() && !session.getId().equals(ignoredSession.getId())) {
                    session.getAsyncRemote().sendObject(message);
                }
            });
        }
    }
}
