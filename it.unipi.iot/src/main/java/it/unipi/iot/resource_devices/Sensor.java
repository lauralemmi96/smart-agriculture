package it.unipi.iot.resource_devices;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.JSONObject;

public class Sensor extends ResourceDevice{

	protected int max_observations = 10;
	protected int []observed_values;
	protected int index = 0;
	protected boolean full = false; 
	
	
	
	public Sensor(String hostAddress, String deviceType, String resourceType, boolean observable) {
		super(hostAddress, deviceType, resourceType, observable);
		
		//set vector dimension
		observed_values = new int[max_observations];
	}
	
	
	public int getIndex() {
		return index;
	}
	
	public boolean getFull() {
		return full;
	}
	
	public int[] getLastObservations() {
		return observed_values;
	}
	
	public float getLastAvgObservation() {
		if(!full) {
			System.out.println("Wait some time, not enough observations made yet");
			return 0;
		}
		int avg = 0;
		for(int i = 0; i < max_observations; i++)
			avg += observed_values[i];
		return avg/max_observations;
	}
	
	public void observeResource() {
		
		if(observable) {
			
			client.observe(
					new CoapHandler() {
						public void onLoad(CoapResponse response) {
							JSONObject responseJSON = new JSONObject(response.getResponseText());
							
	
							//read and store the value in the array
							observed_values[index] = responseJSON.getInt(resourceType);
							//System.out.println("Device type: " + deviceType + ", status: " + observed_values[index]);

							//update the index
							index = (index+1)%max_observations;
							
							//check if the array become full
							if(!full && index == 0)
								full = true;
							
						}
							public void onError() {
								System.err.println("--- Orbservation Failed ---"); 
							}
					}, MediaTypeRegistry.APPLICATION_JSON);
			/*
			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
		}else {
			System.out.println("The resource " + resourceType + " is not observable");
			return;
		}
	}

}
