package it.unipi.iot.server;

import org.eclipse.californium.core.CoapServer;

public class Server extends CoapServer{
	
	public Server() {
		super();
		this.add(new Registration("registration"));
		this.start();
		System.out.println("---- SERVER STARTED ----");
	}

}
