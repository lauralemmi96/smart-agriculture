package it.unipi.iot.resource_devices;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.JSONObject;

import it.unipi.iot.server.ResourceDeviceHandler;


public class Sensor extends ResourceDevice{

	protected int max_observations = 10;
	protected int []observed_values;
	protected int index = 0;
	
	//Count the number of obs above/under thresholds
	protected int above = 0;
	protected int below = 0;
	
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
						
							
							//System.out.println("res: " + resourceType + ", Value: " + observed_values[index]);
							
							//Take the ResourceDeviceHandler instance
							final ResourceDeviceHandler handler = ResourceDeviceHandler.getInstance();
							
							/*
							 * If the area is auto managed I must check 
							 * temp/hum and activate/deactivate
							 * lights and sprinklers in the same area
							 */
							
							
							if(handler.getIdArea().get(area).isAutoManage()) {
								int max_threshold = 0, min_threshold = 0;
								
								
								switch(resourceType) {
								case "temperature":
									max_threshold = handler.getIdArea().get(area).getMaxTemp();
									min_threshold = handler.getIdArea().get(area).getMinTemp();
									break;
								case "humidity":
									max_threshold = handler.getIdArea().get(area).getMaxHum();
									min_threshold = handler.getIdArea().get(area).getMinHum();
									break;
								default:
									System.out.println("Error: ResourceType not defined");
									
								}
								//check if under/above tolerance
								if(observed_values[index] > max_threshold) {
									above++;
									System.out.println(resourceType + ": Observed Value :" + observed_values[index] + ", above counter: " + above);
									if(above == 5) {
										
										//Temperature too high, switch off the lights
										if(resourceType.compareTo("temperature")==0) {
											
											System.out.println("Switch off lights in area " + area);
											
											new Thread() {
												public void run() {
													handler.setAreaLightStatus(area, "OFF");
												}
											}.start();
											
											
										//Humidity too high, switch off the sprinklers	
										}else if(resourceType.compareTo("humidity")==0) {
											
											System.out.println("Switch off sprinklers in area " + area);
											
											new Thread() {
												public void run() {
													handler.setAreaSprinklerStatus(area, "OFF");
												}
											}.start();
											
										}
										
										above = 0;
									}
								}
								
								if(observed_values[index] < min_threshold) {
									below++;
									System.out.println(resourceType + ": Observed Value :" + observed_values[index] + ", below counter: " + below);
									if(below == 5) {
										
										//Temperature too low, switch on the lights
										if(resourceType.compareTo("temperature")==0) {
											System.out.println("Switch on lights in area " + area);
											
											new Thread() {
												public void run() {
													handler.setAreaLightStatus(area, "ON");
												}
											}.start();
											
											
										//Humidity too low, switch on the sprinklers	
										}else if(resourceType.compareTo("humidity")==0) {
											System.out.println("Switch on sprinklers in area " + area);
											new Thread() {
												public void run() {
													handler.setAreaSprinklerStatus(area, "ON");
												}
											}.start();
											
										}
										below = 0;
									}
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
			
		}else {
			System.out.println("The resource " + resourceType + " is not observable");
			return;
		}
	}

}
