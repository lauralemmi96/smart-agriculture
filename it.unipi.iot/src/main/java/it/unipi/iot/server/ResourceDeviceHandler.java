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
	
	private static int deviceID = 0;
	//Map for each resouce 
	protected HashMap<String, Actuator> sprinklers = new HashMap<String, Actuator>();
	protected HashMap<String, Actuator> lights = new HashMap<String, Actuator>();
	protected HashMap<String, Sensor> tempSensors = new HashMap<String, Sensor>();
	protected HashMap<String, Sensor> humiditySensors = new HashMap<String, Sensor>();
	protected HashMap<Integer, ResourceDevice> idDeviceMap = new HashMap<Integer, ResourceDevice>();
	
	//Device Area
	protected HashMap<Integer, String> idArea = new HashMap<Integer, String>();
	
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
	
	public int getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(int deviceID) {
		ResourceDeviceHandler.deviceID = deviceID;
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
	
	public void addidArea(Integer id, String area) {
		idArea.put(id,  area);
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
	
	public String getIdArea(Integer id) {
		return idArea.get(id);
	}
	
	public HashMap<String, ArrayList<ResourceDevice>> getAreas(){
		return areas;
	}
	
	public ResourceDevice getAddressFromId(Integer id) {
		return idDeviceMap.get(id);
	}

	public HashMap<Integer,ResourceDevice> getIdAddressMap(){
		return this.idDeviceMap;
	}
	
/*
 * 
 * 		RETURNS DEVICES LISTS
 * 
 */
	
	// PRINT THE LIST OF ALL THE SPRINKLER ACTUATORS
	public void sprinklerActuatorList() {
		for(String addr: sprinklers.keySet()) {
			Actuator act = sprinklers.get(addr);
			System.out.println("Area: " + act.getArea() + ", ID: " + act.getId() + ", addr: " + addr + ", type: " + act.getDeviceType() + ", Resource: " + act.getResourceType() + ", Status: " + act.getStatus());
		}
	}
	
	// PRINT THE LIST OF ALL THE LIGHT ACTUATORS
	public void lightActuatorList() {
		for(String addr: lights.keySet()) {
			Actuator act = lights.get(addr);
			System.out.println("Area: " + act.getArea() + ", ID: " + act.getId() + ", addr: " + addr + ", type: " + act.getDeviceType() + ", Resource: " + act.getResourceType()+ ", Status: " + act.getStatus());
		}
	}
	
	// PRINT THE LIST OF ALL THE TEMP SENSORS
	public void tempSensorList() {
		for(String addr: tempSensors.keySet()) {
			Sensor s = tempSensors.get(addr);
			System.out.println("Area: " + s.getArea() + ", ID: " + s.getId() + ", addr: " + addr + ", type: " + s.getDeviceType() + ", Resource: " + s.getResourceType());
		}
	}
	
	//	PRINT THE LIST OF ALL THE HUM SENSORS
	public void humiditySensorList() {
		for(String addr: humiditySensors.keySet()) {
			Sensor s = humiditySensors.get(addr);
			System.out.println("Area: " + s.getArea() + ", ID: " + s.getId() + ", addr: " + addr + ", type: " + s.getDeviceType() + ", Resource: " + s.getResourceType());
		}
	}
	
	//	PRINT THE LIST OF ALL THE DEVICES
	public void devicesList() {
		
		this.tempSensorList();
		this.humiditySensorList();
		this.sprinklerActuatorList();
		this.lightActuatorList();
		
		
	}
	
	// RETURNS IF A DEVICE WITH THAT ADDRESS IS PRESENT
	public boolean getDevice(Integer id) {
		
		String address = idDeviceMap.get(id).getHostAddress();
		
		if(tempSensors.containsKey(address))
			return true;
		if(humiditySensors.containsKey(address))
			return true;
		if(sprinklers.containsKey(address))
			return true;
		if(lights.containsKey(address))
			return true;
		
		return false;
	}

	// PRINT LIST OF AREAS
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

	// GET LAST TEMP MEASUR. FOR ALL THE SENSORS 
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

	// GET LAST HUM MEASUR. FOR ALL THE SENSORS 
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
	public void addDeviceArea(Integer id, String area) {
		
		boolean find = false;
		
		ResourceDevice rd = idDeviceMap.get(id);
		if(rd != null) {
			addResourceArea(rd, area);
			find = true;
		}
		
		/*
		Sensor temp = null;
		Sensor hum = null;
		Actuator light = null;
		Actuator sprinkler = null;
		
		temp = tempSensors.get(address);
		hum = humiditySensors.get(address);
		light = lights.get(address);
		sprinkler = sprinklers.get(address);
		
		if(temp != null) {
			addResourceArea(temp, area);
			find = true;
		}
		
		if(hum != null) {
			addResourceArea(hum, area);
			find = true;
		}
		
		if(light != null) {
			addResourceArea(light, area);
			find = true;
		}
		
		if(sprinkler != null) {
			addResourceArea(sprinkler, area);
			find = true;
		}
				
		*/
		if(find) {
			
			idArea.put(id, area);
			System.out.println("");
			
		}else {
			System.out.println("No Device with that address\n");
		}
		
		
		
		
	}
	
	//Add the resource to the area
	public void addResourceArea(ResourceDevice rd, String area) {
		
		
			
		//If a area was already set, remove the device from that list.
		if(rd.getArea() != null) {
			String old = rd.getArea();
			areas.get(old).remove(rd);
			
			//If the area remains empty remove it 
			if(areas.get(old).isEmpty())
				areas.remove(old);
		}
		
		
		rd.setArea(area);

		
		if(!areas.containsKey(area)) {
			ArrayList<ResourceDevice> list = new ArrayList<>();
			list.add(rd);
			areas.put(area, list);
		}else {
			if(!areas.get(area).contains(rd)) {
				areas.get(area).add(rd);
			}
			
			
		}
		System.out.println("Device Area set for resource: " + rd.getResourceType());
				
		
	}
	
	
	
	//REMOVE DEVICE FROM AREA
	public void removeDeviceArea(Integer id) {
		
		boolean find = false;
		
		ResourceDevice rd = idDeviceMap.get(id);
		if(rd != null) {
			removeResourceArea(rd);
			find = true;
		}
		
		/*
		Sensor temp = null;
		Sensor hum = null;
		Actuator light = null;
		Actuator sprinkler = null;
		
		
		temp = tempSensors.get(address);
		hum = tempSensors.get(address);
		light = lights.get(address);
		sprinkler = sprinklers.get(address);
		
		if(temp != null) {
			removeResourceArea(temp);
			find = true;
		}
		
		if(hum != null) {
			removeResourceArea(hum);
			find = true;
		}
		
		if(light != null) {
			removeResourceArea(light);
			find = true;
		}
		
		if(sprinkler != null) {
			removeResourceArea(sprinkler);
			find = true;
		}
				
		*/
		if(find) {
			idArea.remove(id);
			System.out.println("");
			
		}else {
			System.out.println("No Device with that id\n");
		}
		
			
	}
	
	// Remove resource from the area
	private void removeResourceArea(ResourceDevice rd) {
		
		//If a area was already set, remove the device from that list.
		if(rd.getArea() != null) {
			String old = rd.getArea();
			areas.get(old).remove(rd);
			
			//If the area remains empty remove it 
			if(areas.get(old).isEmpty())
				areas.remove(old);
			
			System.out.println("Resource Device: " + rd.getResourceType() + " removed from area " + old + "\n");
			return;
		}
		
		System.out.println("Resource Device " + rd.getResourceType() + ": Area was not set\n");
		return;
	}

	// GET LIST OF AREAS WITH LIGHT DEVICES
	public void lightAreasList() {
		
		System.out.print("[ ");
		for(String area: areas.keySet()) {
			ArrayList<ResourceDevice> device = areas.get(area);
			for(ResourceDevice d: device) {
				if(d.getResourceType().compareTo("light") == 0) {
					System.out.print(area + " ");
					break;
				}
					
			}
		}
		System.out.println("]");
			
		
	}
	
	// GET LIST OF AREAS WITH SPRINKLER DEVICES
	public void sprinklerAreasList() {
		
		System.out.print("[ ");
		for(String area: areas.keySet()) {
			ArrayList<ResourceDevice> device = areas.get(area);
			for(ResourceDevice d: device) {
				if(d.getResourceType().compareTo("sprinkler") == 0) {
					System.out.print(area + " ");
					break;
				}
					
			}
		}
		System.out.println("]");
			
		
	}

	
	
	
	
	
}
