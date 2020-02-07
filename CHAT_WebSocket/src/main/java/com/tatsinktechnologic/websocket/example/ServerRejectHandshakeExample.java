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
 * This example shows how to reject a handshake as a server from a client.
 *
 * For this you have to override onWebsocketHandshakeReceivedAsServer in your WebSocketServer class
 */
public class ServerRejectHandshakeExample extends ChatServer {

	public ServerRejectHandshakeExample( int port ) {
		super( new InetSocketAddress( port ));
	}

	@Override
	public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer( WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
		ServerHandshakeBuilder builder = super.onWebsocketHandshakeReceivedAsServer( conn, draft, request );
		//In this example we don't allow any resource descriptor ( "ws://localhost:8887/?roomid=1 will be rejected but ws://localhost:8887 is fine)
		if (! request.getResourceDescriptor().equals( "/" )) {
			throw new InvalidDataException( CloseFrame.POLICY_VALIDATION, "Not accepted!");
		}
		//If there are no cookies set reject it as well.
		if (!request.hasFieldValue( "Cookie" )) {
			throw new InvalidDataException( CloseFrame.POLICY_VALIDATION, "Not accepted!");
		}
		//If the cookie does not contain a specific value
		if (!request.getFieldValue( "Cookie" ).equals( "username=nemo" )) {
			throw new InvalidDataException( CloseFrame.POLICY_VALIDATION, "Not accepted!");
		}
		//If there is a Origin Field, it has to be localhost:8887
		if (request.hasFieldValue( "Origin" )) {
			if (!request.getFieldValue( "Origin" ).equals( "localhost:8887" )) {
				throw new InvalidDataException( CloseFrame.POLICY_VALIDATION, "Not accepted!");
			}
		}
		return builder;
	}


	public static void main( String[] args ) throws InterruptedException , IOException {
		int port = 8887; // 843 flash policy port
		try {
			port = Integer.parseInt( args[ 0 ] );
		} catch ( Exception ex ) {
		}
		ServerRejectHandshakeExample s = new ServerRejectHandshakeExample( port );
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
