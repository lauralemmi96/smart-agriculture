package it.unipi.iot.server;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class ChangeSprinklerStatus extends CoapResource{

	public ChangeSprinklerStatus(String name) {
		super(name);
		System.out.println("Listening for sprinkler status change requests\n");
		
	}
	
public void handleGET(CoapExchange exchange) {
		
	
		System.out.println("[SERVER]: Handling Sprinkler Status Change Request");
		
		System.out.println("REQUEST: " + exchange.getRequestText());
		ResourceDeviceHandler handler = ResourceDeviceHandler.getInstance();

		//	Get node address	 
		String source_address = exchange.getSourceAddress().getHostAddress();
		System.out.println("SRC ADDR: " + source_address + 
				", ID: " + handler.getHumiditySensors().get(source_address).getId() + 
				", Area: " + handler.getHumiditySensors().get(source_address).getArea());
		
		
		
		
	}

}
