package it.unipi.iot;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

import it.unipi.iot.server.Server;

public class MyClient {
	
	private static Server server;
	
	public static void main(String[] args) {
		
		System.out.println("---- CLIENT STARTED ----");
		
		server = new Server();
		new Thread() {
			public void run() {
				server.start();
			}
		}.start();
		
		
	}
}
