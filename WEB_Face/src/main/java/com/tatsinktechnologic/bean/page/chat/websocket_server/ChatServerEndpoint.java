package com.tatsinktechnologic.bean.page.chat.websocket_server;

import com.tatsinktechnologic.bean.page.chat.ChatMemory;
import com.tatsinktechnologic.bean.page.chat.MessageWs;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/sendmsg", encoders = MessageWSEncoder.class, decoders = MessageWSDecoder.class)
public class ChatServerEndpoint implements Serializable {

    private static final long serialVersionUID = -3538397497899905150L;

    private static ArrayList<Session> sessions = new ArrayList<Session>();

    public ChatServerEndpoint() {
    }

    @OnMessage
    public void messageReceiver(Session session, MessageWs msgWS) {
        try {
            String operationMsgWS = msgWS.getOperation();
            if (operationMsgWS.equals("addNicknameSession")) {
                // Adicionando o nickname do usuario na sessao p/ msg unicast
                session.getUserProperties().put("nickname", msgWS.getSource());
            } else if (operationMsgWS.equals("sendText")) {
                // ---- UNICAST
                // Remetente
                String userSource = (String) session.getUserProperties().get("nickname");
                if (msgWS.getBody().startsWith("@")) {
                    String[] brokenMsgBody = msgWS.getBody().split(" ");
                    // Destinatario
                    String userDestination = brokenMsgBody[0].replace("@", "");
                    msgWS.setDestination(userDestination);
                    // Remetente envia para ele mesmo
                    if (userSource.equals(userDestination)) {
                        this.sendUnicastMSGToUserSource(msgWS, session);

                    } else if (ChatMemory.allOnlines.contains(userDestination)) {
                        this.sendUnicastMSG(msgWS, userDestination, session);

                    } else {
                        // Envia a msg de erro para usuario remetente
                        msgWS.setBody("This user does not exist: " + userDestination);
                        this.sendUnicastMSGToUserSource(msgWS, session);
                    }

                } else {
                    // ---- BROADCAST
                    this.sendBroadCastMsg(msgWS);
                }
            }
            System.out.println("************** MESSAGE SEND BY WEBSOCKET *******************");
            System.out.println(operationMsgWS+"-->"+ msgWS);
        } catch (IOException | EncodeException e) {
            System.err.println("***** Error onMessage: " + e.getMessage());
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        session.getUserProperties().put("nickname", ChatMemory.lastUserOnline);
        System.out.println("************** OPEN WEBSOCKET *******************");
        System.out.println(session.getUserProperties().get("nickname"));
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) throws IOException, EncodeException {
        System.out.println("************** CLOSE WEBSOCKET *******************");
        String nickname = (String) session.getUserProperties().get("nickname");
        ChatMemory.allOnlines.remove(nickname);
        sessions.remove(session);
        MessageWs msgWS = new MessageWs();
        msgWS.setSource(nickname);
        msgWS.setDestination("all");
        msgWS.setBody("User: " + nickname + " left");
        msgWS.setOperation("logoutUser");
        msgWS.setTimestamp(new Date());
        this.sendBroadCastMsg(msgWS);
        System.out.println(msgWS);

    }

    @OnError
    public void onError(Session session, Throwable t) throws IOException, EncodeException {

    }

   
    public void sendUnicastMSG(MessageWs msgWS, String destination, Session session) throws IOException, EncodeException {
        for (Session s : sessions) {
            if (s.isOpen() && s.getUserProperties().containsValue(destination)) {
                msgWS.setDestination(destination);
                s.getBasicRemote().sendObject(msgWS);
                // Enviando para o usuario source (origem)
                session.getBasicRemote().sendObject(msgWS);
                break;
            }
        }
    }

  
    public void sendUnicastMSGToUserSource(MessageWs msgWS, Session session) throws IOException, EncodeException {
        String userSource = (String) session.getUserProperties().get("nickname");
        msgWS.setSource(userSource);
        session.getBasicRemote().sendObject(msgWS);
    }

   
    public void sendBroadCastMsg(MessageWs msgWS) throws IOException, EncodeException {
        for (Session s : sessions) {
            if (s.isOpen()) {
                s.getBasicRemote().sendObject(msgWS);
            }
        }
    }

    // GET AND SET
    public static ArrayList<Session> getSessions() {
        return sessions;
    }

    public static void setSessions(ArrayList<Session> sessions) {
        ChatServerEndpoint.sessions = sessions;
    }

}
