package com.tatsinktechnologic.websocket.example;

import org.java_websocket.WebSocketImpl;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Simple example to reconnect blocking and non-blocking.
 */
public class ReconnectClientExample {
	public static void main( String[] args ) throws URISyntaxException, InterruptedException {
		ExampleClient c = new ExampleClient( new URI( "ws://localhost:8887" ) );
		//Connect to a server normally
		c.connectBlocking();
		c.send( "hi" );
		Thread.sleep( 10 );
		c.closeBlocking();
		//Disconnect manually and reconnect blocking
		c.reconnectBlocking();
		c.send( "it's" );
		Thread.sleep( 10000 );
		c.closeBlocking();
		//Disconnect manually and reconnect
		c.reconnect();
		Thread.sleep( 100 );
		c.send( "me" );
		Thread.sleep( 100 );
		c.closeBlocking();
	}
}
