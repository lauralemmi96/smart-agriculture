package it.unipi.iot.server;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class ChangeLightStatus extends CoapResource{

	public ChangeLightStatus(String name) {
		super(name);
		System.out.println("Listening for light status change requests\n");	
	}
	
	public void handleGET(CoapExchange exchange) {
		
		System.out.println("[SERVER]: Handling Light Status Change Request");
		
		System.out.println("REQUEST: " + exchange.getRequestText());
		ResourceDeviceHandler handler = ResourceDeviceHandler.getInstance();

		
		//	Get node address	 
		String source_address = exchange.getSourceAddress().getHostAddress();
		System.out.println("SRC ADDR: " + source_address);
		
		System.out.println("SRC ADDR: " + source_address + 
				", ID: " + handler.getTempSensors().get(source_address).getId() + 
				", Area: " + handler.getTempSensors().get(source_address).getArea());
		
		
		
		
		
	}


}
