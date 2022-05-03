package it.unipi.iot.server;


import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import it.unipi.iot.resource_devices.Actuator;
import it.unipi.iot.resource_devices.Sensor;

public class Registration extends CoapResource{

	public Registration(String name) {
		super(name);
		System.out.println("Registration Resource created\n");
	}
	
	public void handleGET(CoapExchange exchange) {
		
		System.out.println("[SERVER]: Handling Registration Request");
		
		exchange.accept();
		
		System.out.println("Exchange message: " + exchange.getRequestText());
		
		//	Get node address	 
		String source_address = exchange.getSourceAddress().getHostAddress();
		System.out.println("SRC ADDR: " + source_address);
		
		// Create CoapClient Object	and get node information
		
		CoapClient client = new CoapClient("coap://[" + source_address + "]:5683/.well-known/core");
		CoapResponse response = client.get(MediaTypeRegistry.APPLICATION_JSON);
		
		
		String responseText = response.getResponseText();
		//System.out.println("Client response: " + responseText);
		
		//Check the return code: Success 2.xx
		if(!response.getCode().toString().startsWith("2")) {
			System.out.println("Error code: " + response.getCode().toString());
			return;
		}
		
		
		/*
		 * Client response: </.well-known/core>;ct=40,
		 * </humidity>;title="Humidity Sensor";rt="humidity";if="sensor";obs,
		 * </temperature>;"title=\"Temperature Sensor\";rt=\"temperature\";if=\"sensor\";obs 
		 */

		boolean registered = true;
		
		String []fragment = responseText.split(",");
		for(int i = 1; i < fragment.length; i++) {
			
			if(!deviceRegistration(source_address, fragment[i])) {
				System.out.println("Error in registering a resource\n");
				registered = false;
			}

			
		}
		
		
		ResourceDeviceHandler handler = ResourceDeviceHandler.getInstance();
		handler.addAddressArea(source_address, "default");
		
		if(registered)
			System.out.println("[SERVER]: Device Registered\n");
			
		
		
	}
	
	//For each resource, register the device
	private boolean deviceRegistration(String sourceAddress, String resource) {
		
		boolean registered = false;
		
		String []splitSemicolon = resource.split(";");
		
		//Take the resource name
		String resType = splitSemicolon[2].substring(splitSemicolon[2].indexOf("\"") + 1 , splitSemicolon[2].length()-1);
		
		//Take the device type (sensor/actuator)
		String deviceType = splitSemicolon[3].substring(splitSemicolon[3].indexOf("\"") + 1 , splitSemicolon[3].length()-1);
		
		//Check if it is observable
		String obs = splitSemicolon[splitSemicolon.length-1];
		boolean observable = (obs.compareTo("obs") == 0);
			
		ResourceDeviceHandler handler = ResourceDeviceHandler.getInstance();
		
		//	Switch sensor/actuator. I instantiate a Sensor/Actuator. 
		//	Add it to the data structure base on the res
		if(deviceType.compareTo("sensor") == 0) {
			
			final Sensor sensor = new Sensor(sourceAddress, deviceType, resType, observable);
			
			//	add the sensor in the ResourceDeviceHandler map
			if(resType.compareTo("humidity") == 0)
				handler.addHumiditySens(sourceAddress, sensor);
			else if(resType.compareTo("temperature") == 0)
				handler.addTempSens(sourceAddress, sensor);
			
			handler.addResourceArea(sensor, "default");
			
			if(observable) {
				new Thread() {
					public void run() {
						sensor.observeResource();
					}
				}.start();
				
			}
			System.out.println("Resource: " + resType + ", Resource observable: " + observable);
			registered = true;
			
		}else if(deviceType.compareTo("actuator") == 0) {
			
			Actuator actuator = new Actuator(sourceAddress, deviceType, resType, observable);
			
//			add the actuator in the ResourceDeviceHandler map
			if(resType.compareTo("sprinkler") == 0)
				handler.addSprinklers(sourceAddress, actuator);
			else if(resType.compareTo("light") == 0)
				handler.addLights(sourceAddress, actuator);
			
			handler.addResourceArea(actuator, "default");
			
			if(observable)
				actuator.observeResource();
			
			System.out.println("Resource: " + resType + ", Resource observable: " + observable);
			
			registered = true;
		}
		
		return registered;
		
		
		
		
		
	}
	
	

}
