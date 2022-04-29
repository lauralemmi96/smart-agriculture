package it.unipi.iot.server;

import java.util.HashMap;

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
		 * </humidity>;title="Humidity Sensor";rt="humidity";if="sensor";obs,
		 * </temperature>;"title=\"Temperature Sensor\";rt=\"temperature\";if=\"sensor\";obs"

		 * 
		 */
		HashMap<String, String> resource = new HashMap<String, String>();
		String []fragment = responseText.split(",");
		for(int i = 1; i < fragment.length; i++) {
			resource = info(fragment[i]);
			
		}
		
		//Check the return code: Success 2.xx
		System.out.println("Response Code: " + response.getCode());
		
		
	}
	
	private HashMap<String, String> info(String resource) {
		
		HashMap<String, String> info = new HashMap<String, String>();
		String []splitSemicolon = resource.split(";");
		
		//Take the resource name
		String resType = splitSemicolon[2].substring(splitSemicolon[2].indexOf("\"") + 1 , splitSemicolon[2].length()-1);
		info.put("Resource", resType);
		System.out.println("Resource: " + info.get("Resource"));
		
		//Take the device type (sensor/actuator)
		String deviceType = splitSemicolon[2].substring(splitSemicolon[3].indexOf("\"") + 1 , splitSemicolon[3].length()-1);
		info.put("Device", deviceType);
		System.out.println("Device Type: " + info.get("Device"));
		
		//Check if it is observable
		String obs = splitSemicolon[splitSemicolon.length-1];
		if(obs.contains("obs"))
			info.put("obs", "y");
		else
			info.put("obs", "n");
		System.out.println("OBS: " + info.get("obs"));
		return info;
		
		
	}
	
	

}
