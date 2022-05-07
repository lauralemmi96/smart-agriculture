package it.unipi.iot.resource_devices;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.JSONObject;

public class Actuator extends ResourceDevice{

	protected String status;
	
	public Actuator(String hostAddress, String deviceType, String resourceType, boolean observable) {
		super(hostAddress, deviceType, resourceType, observable);
		// TODO Auto-generated constructor stub
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public void observeResource() {
		
		if(observable) {
			
			client.observe(
					new CoapHandler() {
						public void onLoad(CoapResponse response) {
							JSONObject responseJSON = new JSONObject(response.getResponseText());
							
							//read and store the value in the array
							status = responseJSON.getString(resourceType);
							

							
						}
							public void onError() {
								System.err.println("--- Orbservation Failed ---"); 
							}
					}, MediaTypeRegistry.APPLICATION_JSON);
			
			
		}else {
			System.out.println("The resource " + resourceType + " is not observable");
			return;
		}
	}
	
}
