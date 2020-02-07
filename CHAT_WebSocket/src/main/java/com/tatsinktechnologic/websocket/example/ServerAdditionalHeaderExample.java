package com.tatsinktechnologic.websocket.example;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

/**
 * This example shows how to add additional headers to your server handshake response
 *
 * For this you have to override onWebsocketHandshakeReceivedAsServer in your WebSocketServer class
 *
 * We are simple adding the additional header "Access-Control-Allow-Origin" to our server response
 */
public class ServerAdditionalHeaderExample extends ChatServer {

	public ServerAdditionalHeaderExample( int port ) {
		super( new InetSocketAddress( port ));
	}

	@Override
	public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer( WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
		ServerHandshakeBuilder builder = super.onWebsocketHandshakeReceivedAsServer( conn, draft, request );
		builder.put( "Access-Control-Allow-Origin" , "*");
		return builder;
	}


	public static void main( String[] args ) throws InterruptedException , IOException {
		int port = 8887; // 843 flash policy port
		try {
			port = Integer.parseInt( args[ 0 ] );
		} catch ( Exception ex ) {
		}
		ServerAdditionalHeaderExample s = new ServerAdditionalHeaderExample( port );
		s.start();
		System.out.println( "Server started on port: " + s.getPort() );

		BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
		while ( true ) {
			String in = sysin.readLine();
			s.broadcast( in );
			if( in.equals( "exit" ) ) {
				s.stop(1000);
				break;
			}
		}
	}
}
