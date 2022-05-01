package it.unipi.iot.server;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import it.unipi.iot.resource_devices.Actuator;
import it.unipi.iot.resource_devices.ResourceDevice;
import it.unipi.iot.resource_devices.Sensor;

public class ResourceDeviceHandler {
	
	//A single instance of this class is needed to maintain shared and consistent info of the devices
	private static ResourceDeviceHandler single_instance = null;
	
	//Map for each resouce 
	protected HashMap<String, Actuator> sprinklers = new HashMap<String, Actuator>();
	protected HashMap<String, Actuator> lights = new HashMap<String, Actuator>();
	protected HashMap<String, Sensor> tempSensors = new HashMap<String, Sensor>();
	protected HashMap<String, Sensor> humiditySensors = new HashMap<String, Sensor>();
	
	//Device Map
	protected HashMap<String, ResourceDevice> devices = new HashMap<String, ResourceDevice>();
	
	//Areas List
	protected HashMap<String, ArrayList<ResourceDevice>> areas = new HashMap<String, ArrayList<ResourceDevice>>();

	private ResourceDeviceHandler()
    {
      
    }
	
	public static ResourceDeviceHandler getInstance(){
		if (single_instance == null)
            single_instance = new ResourceDeviceHandler();
 
        return single_instance;
    }
	
	public void addSprinklers(String address, Actuator actuator) {
		sprinklers.put(address, actuator);
	}
	
	public void addLights(String address, Actuator actuator) {
		lights.put(address, actuator);
	}
	
	public void addTempSens(String address, Sensor sensor) {
		tempSensors.put(address, sensor);
	}
	
	public void addHumiditySens(String address, Sensor sensor) {
		humiditySensors.put(address, sensor);
	}
	
	public void addDevice(String address, ResourceDevice rd) {
		devices.put(address, rd);
	}

	public HashMap<String, Actuator> getSprinklers() {
		return sprinklers;
	}

	public HashMap<String, Actuator> getLights() {
		return lights;
	}

	public HashMap<String, Sensor> getTempSensors() {
		return tempSensors;
	}

	public HashMap<String, Sensor> getHumiditySensors() {
		return humiditySensors;
	}
	
	
	public HashMap<String, ResourceDevice> getDevice(){
		return devices;
	}
	
	public HashMap<String, ArrayList<ResourceDevice>> getAreas(){
		return areas;
	}
	
	// PRINT THE LIST OF ALL THE SPRINKLER ACTUATORS
	public void sprinklerActuatorList() {
		for(String addr: sprinklers.keySet()) {
			Actuator act = sprinklers.get(addr);
			System.out.println("Area: " + act.getArea() + ", addr: " + addr + ", Status: " + act.getStatus());
		}
	}
	
	// PRINT THE LIST OF ALL THE LIGHT ACTUATORS
	public void lightActuatorList() {
		for(String addr: lights.keySet()) {
			Actuator act = lights.get(addr);
			System.out.println("Area: " + act.getArea() + ", addr: " + addr + ", Status: " + act.getStatus());
		}
	}
	
	// PRINT THE LIST OF ALL THE TEMP SENSORS
	public void tempSensorList() {
		for(String addr: tempSensors.keySet()) {
			Sensor s = tempSensors.get(addr);
			System.out.println("Area: " + s.getArea() + ", addr: " + addr + ", Resource: " + s.getResourceType());
		}
	}
	
	//	PRINT THE LIST OF ALL THE HUM SENSORS
	public void humiditySensorList() {
		for(String addr: humiditySensors.keySet()) {
			Sensor s = humiditySensors.get(addr);
			System.out.println("Area: " + s.getArea() + ", addr: " + addr + ", Resource: " + s.getResourceType());
		}
	}
	
	//	PRINT THE LIST OF ALL THE DEVICES
	public void devicesList() {
		for(String addr: devices.keySet()) {
			ResourceDevice rd = devices.get(addr);
			System.out.println("ADDR: " + addr + ", Type: " + rd.getDeviceType());
		}
	}
	
/*
 * 
 * 		GET FUNCTIONS
 * 	
 */
	
	//GET THE TEMP AVG OF ALL THE SENSORS
	public void getSensorsTemperatures() {
		for(String addr: tempSensors.keySet()) {
			Sensor s = tempSensors.get(addr);
			System.out.println("Sensor Area: " + s.getArea() + ", Address: " + addr + 
					", Average Temperatures: " + s.getLastAvgObservation());
		}
	}
	
	//GET THE HUM AVG OF ALL THE SENSORS
	public void getSensorsHumidities() {
		for(String addr: humiditySensors.keySet()) {
			Sensor s = humiditySensors.get(addr);
			System.out.println("Sensor Area: " + s.getArea() + ", Address: " + addr + 
					", Average Humidities: " + s.getLastAvgObservation());
		}
	}
	
	//GET STATUS OF ALL THE SPRIKNLER
	public void getSprinklersStatus() {
		for(String addr: sprinklers.keySet()) {
			Actuator act = sprinklers.get(addr);
			System.out.println("Actuator Area: " + act.getArea() + ", Address: " + addr + 
					", Status: " + act.getStatus());
		}
	}
	
	//GET STATUS OF ALL THE LIGHTS
	public void getLightsStatus() {
		for(String addr: lights.keySet()) {
			Actuator act = lights.get(addr);
			System.out.println("Actuator Area: " + act.getArea() + ", Address: " + addr + 
					", Status: " + act.getStatus());
		}
	}

	// GET LAST TTEMP MEAS. FOR ALL THE SENSORS 
	public void getLastSensorsTemperatures() {

		for(String addr: tempSensors.keySet()) {
			
			Sensor s = tempSensors.get(addr);

			System.out.print("Sensor Area: " + s.getArea() + ", Address: " + addr); 
			if(!s.getFull() && s.getIndex() == 0)
				System.out.println(", No observation available");
			else {
				System.out.print(", Last Temperatures: [ ");
				if(s.getFull()) {
					for(int i = 0; i < s.getLastObservations().length; i++)
						System.out.print(s.getLastObservations()[i] + " ");
				}else {
					for(int i = 0; i < s.getIndex(); i++)
						System.out.print(s.getLastObservations()[i] + " ");
				}
				System.out.println("]");
			}
			
		}
		
	}

	// GET LAST HUM MEAS. FOR ALL THE SENSORS 
	public void getLastSensorsHumidities() {
		for(String addr: humiditySensors.keySet()) {
			Sensor s = humiditySensors.get(addr);
			System.out.print("Sensor Area: " + s.getArea() + ", Address: " + addr); 
			if(!s.getFull() && s.getIndex() == 0)
				System.out.println(", No observation available");
			else {
				System.out.print(", Last Humidities: [ ");
				if(s.getFull()) {
					for(int i = 0; i < s.getLastObservations().length; i++)
						System.out.print(s.getLastObservations()[i] + " ");
				}else {
					for(int i = 0; i < s.getIndex(); i++)
						System.out.print(s.getLastObservations()[i] + " ");
				}
				System.out.println("]");
			}
		}
		
	}
	
	// PRINT LIST OF AREAS WITH LIGHT DEVICES
	public void getAreasList() {
		
		if(areas.isEmpty()) {
			System.out.println("No Area defined yet\n");
			return;
		}
		
		for(String area: areas.keySet()) {
			//print area name
			System.out.print("[ " + area + " ] : [ ");
			
			//get device list
			ArrayList<ResourceDevice> device = areas.get(area);
			
			for(ResourceDevice d: device) {
					System.out.print("{IP: " + d.getHostAddress() + ", type: " 
							+ d.getDeviceType() + ", res: " + d.getResourceType() + " } " );
					

			}
			System.out.println(" ]");
					
			
		}
		
			
		
	}
	
/*
 * 
 * 		SET FUNCTIONS
 * 
 */
	
	// SET STATUS OF A SPRINKLER
	public boolean setSprinklerStatus(String address, String newStatus) {
		
		CoapClient c = sprinklers.get(address).getClient();
		
		//Prepare post payload
		String requestAttribute = "status=" + newStatus;
		
		//send post request
		CoapResponse response = c.post(requestAttribute, MediaTypeRegistry.TEXT_PLAIN);
		
		System.out.println(response.getResponseText()); 
		//Check the return code: Success 2.xx
		if(!response.getCode().toString().startsWith("2")) {
			System.out.println("Error code: " + response.getCode().toString());
			return false;
		}
		
		return true;
		
		
	
	}
	
	//	SET STATUS OF SPRINKLERS WITHIN A AREA
	public boolean setAreaSprinklerStatus(String area, String status) {
		
		if(areas.containsKey(area)) {
			ArrayList<ResourceDevice> device = areas.get(area);
			for(ResourceDevice d: device) {
				if(d.getResourceType().compareTo("sprinkler") == 0) {
					if(!setSprinklerStatus(d.getHostAddress(), status))
						return false;
				}
					
			}
		}
		return true;

	}
	
	
	// SET STATUS OF A LIGHT 
	public boolean setLightStatus(String address, String newStatus) {
		
		CoapClient c = lights.get(address).getClient();
		
		//Prepare post payload
		String requestAttribute = "status=" + newStatus;
		
		//send post request
		CoapResponse response = c.post(requestAttribute, MediaTypeRegistry.TEXT_PLAIN);
		System.out.println(response.getResponseText());
		//Check the return code: Success 2.xx
		if(!response.getCode().toString().startsWith("2")) {
			System.out.println("Error code: " + response.getCode().toString());
			return false;
		}
		
		return true;
		
		
	
	}
	
	//	SET STATUS OF LIGHTS WITHIN A AREA
	public boolean setAreaLightStatus(String area, String status) {
		
		if(areas.containsKey(area)) {
			ArrayList<ResourceDevice> device = areas.get(area);
			for(ResourceDevice d: device) {
				if(d.getResourceType().compareTo("light") == 0) {
					if(!setLightStatus(d.getHostAddress(), status))
						return false;
				}
					
			}
		}
		return true;

	}
	
	
	/*
	 * 
	 * 		DEVICE - AREA RELATED 
	 * 
	 */
	
	
	// ADD DEVICE TO AREA
	public void addDeviceArea(String address, String area) {
		
		ResourceDevice rd = null;
		
		rd = devices.get(address);
				
		
		if(rd != null) {
			rd.setArea(area);

			
			if(!areas.containsKey(areas)) {
				ArrayList<ResourceDevice> list = new ArrayList<>();
				list.add(rd);
				areas.put(area, list);
			}else {
				if(!areas.get(area).contains(rd)) {
					areas.get(area).add(rd);
				}
				
				System.out.println("Device Area set\n");
			}
			
			
		}else {
			System.out.println("No Device with that address\n");
		}
		
		
		
		
	}

	// GET LIST OF AREAS WITH LIGHT DEVICES
	public void lightAreasList() {
		
		System.out.println("[ ");
		for(String area: areas.keySet()) {
			ArrayList<ResourceDevice> device = areas.get(area);
			for(ResourceDevice d: device) {
				if(d.getResourceType().compareTo("light") == 0) {
					System.out.println(area + " ");
					break;
				}
					
			}
		}
		System.out.println("]");
			
		
	}
	
	// GET LIST OF AREAS WITH SPRINKLER DEVICES
	public void sprinklerAreasList() {
		
		System.out.println("[ ");
		for(String area: areas.keySet()) {
			ArrayList<ResourceDevice> device = areas.get(area);
			for(ResourceDevice d: device) {
				if(d.getResourceType().compareTo("sprinkler") == 0) {
					System.out.println(area + " ");
					break;
				}
					
			}
		}
		System.out.println("]");
			
		
	}

	
	
	
	
	
}
