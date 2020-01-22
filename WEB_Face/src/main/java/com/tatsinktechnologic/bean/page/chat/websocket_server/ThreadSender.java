package com.tatsinktechnologic.bean.page.chat.websocket_server;

import java.util.List;

import javax.websocket.Session;

public class ThreadSender {

	public ThreadSender() {

	}

	public static void sendMessageBroadCast(String message){
		List<Session> sessions = ChatServerEndpoint.getSessions();
		for (Session s : sessions){
			if (s.isOpen()) {
				s.getAsyncRemote().sendText(message);
			}
		}
	}

}
