package com.tatsinktechnologic.websocket.example;

import org.java_websocket.server.SSLParametersWebSocketServerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

/**
 * Copy of SSLServerExample except we use @link SSLEngineWebSocketServerFactory to customize clientMode/ClientAuth to force client to present a cert.
 * Example of Two-way ssl/MutualAuthentication/ClientAuthentication
 */
public class TwoWaySSLServerExample {

	/*
	 * Keystore with certificate created like so (in JKS format):
	 *
	 *keytool -genkey -keyalg RSA -validity 3650 -keystore "keystore.jks" -storepass "storepassword" -keypass "keypassword" -alias "default" -dname "CN=127.0.0.1, OU=MyOrgUnit, O=MyOrg, L=MyCity, S=MyRegion, C=MyCountry"
	 */
	public static void main( String[] args ) throws Exception {
		ChatServer chatserver = new ChatServer( 8887 ); // Firefox does allow multible ssl connection only via port 443 //tested on FF16

		// load up the key store
		String STORETYPE = "JKS";
		String KEYSTORE = "keystore.jks";
		String STOREPASSWORD = "storepassword";
		String KEYPASSWORD = "keypassword";

		KeyStore ks = KeyStore.getInstance( STORETYPE );
		File kf = new File( KEYSTORE );
		ks.load( new FileInputStream( kf ), STOREPASSWORD.toCharArray() );

		KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
		kmf.init( ks, KEYPASSWORD.toCharArray() );
		TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
		tmf.init( ks );

		SSLContext sslContext = SSLContext.getInstance( "TLS" );
		sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );

		SSLParameters sslParameters = new SSLParameters();
		// This is all we need
		sslParameters.setNeedClientAuth(true);
		chatserver.setWebSocketFactory( new SSLParametersWebSocketServerFactory(sslContext, sslParameters));

		chatserver.start();

	}
}
