package it.unipi.iot.server;

import org.eclipse.californium.core.CaliforniumLogger;
import org.eclipse.californium.core.CoapServer;

public class Server extends CoapServer{
	
	private static ResourceDeviceHandler handler;
	
	public Server() {
		this.add(new Registration("registration"));
		this.start();
		this.handler = new ResourceDeviceHandler();
		CaliforniumLogger.disableLogging();
		System.out.println("---- SERVER STARTED ----");
	}
	
	public ResourceDeviceHandler getHandlerInstance() {
		return this.handler;
	}

}
