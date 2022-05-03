package it.unipi.iot.resource_devices;

import java.util.ArrayList;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.JSONObject;

import it.unipi.iot.server.ResourceDeviceHandler;


public class Sensor extends ResourceDevice{

	protected int max_observations = 10;
	protected int []observed_values;
	protected int index = 0;
	
	//Min and Max th values for the observed resources
	protected int min_threashold = 0;
	protected int max_threashold = 0;
	
	//Count the number of obs above/under threasholds
	protected int above = 0;
	protected int below = 0;
	
	protected boolean full = false; 
	
	
	
	public Sensor(String hostAddress, String deviceType, String resourceType, boolean observable, Integer min, Integer max) {
		super(hostAddress, deviceType, resourceType, observable);
		
		//set vector dimension
		observed_values = new int[max_observations];
		this.min_threashold = min;
		this.max_threashold = max;
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

							//check if under/above tolerance
							if(observed_values[index] > max_threashold) {
								System.out.println("Observed Value Above MAX!!");
								above++;
								if(above == 10) {
									
									if(resourceType == "temperature") {
										//Find the lights in the same area and switch them off
										ResourceDeviceHandler handler = ResourceDeviceHandler.getInstance();
										ArrayList<ResourceDevice> deviceSameArea = handler.getAreas().get(area);
										
									}
									above = 0;
								}
							}
							
							if(observed_values[index] < min_threashold) {
								System.out.println("Observed Value Below MIN!!");
								below++;
								if(below == 10) {
									//DEACTIVATE THE ACTUATORS IN THE SAME AREA
									below = 0;
								}
							}
							
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
