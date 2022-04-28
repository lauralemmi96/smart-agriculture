package it.unipi.iot.server;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class Registration extends CoapResource{

	public Registration(String name) {
		super(name);
		System.out.println("Registration Resource created\n");
	}
	
	public void handleGET(CoapExchange exchange) {
		
		System.out.println("[SERVER]: Handling Registration Request");
		//exchange.respond("REGISTERED");
		
		exchange.accept();
		
		//	Get node address	 
		String source_address = exchange.getSourceAddress().getHostAddress();
		System.out.println("SRC ADDR: " + source_address + "\n");
		
		// Create CoapClient Object	and get node information
		
		CoapClient client = new CoapClient("coap://[" + source_address + "]:5683/.well-known/core");
		CoapResponse response = client.get(MediaTypeRegistry.APPLICATION_JSON);
		
		
		String responseText = response.getResponseText();
		System.out.println("Client response: " + responseText);
		
		/*
		 * Client response: </.well-known/core>;ct=40,
		 * </humidity>;title="Humidity Sensor";rt="humidity";if="sensor";obs
		 * 
		 */
		
		String []fragment = responseText.split(",");
		
		//Check the return code: Success 2.xx
		System.out.println("Response Code: " + response.getCode());
		
		
	}
	
	

}
