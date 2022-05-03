package it.unipi.iot.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import it.unipi.iot.resource_devices.Actuator;
import it.unipi.iot.resource_devices.Area;
import it.unipi.iot.resource_devices.ResourceDevice;
import it.unipi.iot.resource_devices.Sensor;

public class ResourceDeviceHandler {
	
	//A single instance of this class is needed to maintain shared and consistent info of the devices
	private static ResourceDeviceHandler single_instance = null;
	
	private static int deviceID = 0;
	
	protected HashMap<Integer, Actuator> sprinklers = new HashMap<Integer, Actuator>();	//ID - sprinkler
	protected HashMap<Integer, Actuator> lights = new HashMap<Integer, Actuator>();		//ID - light
	protected HashMap<Integer, Sensor> tempSensors = new HashMap<Integer, Sensor>();		//ID - temp
	protected HashMap<Integer, Sensor> humiditySensors = new HashMap<Integer, Sensor>();	//ID - humidity
	protected HashMap<Integer, ResourceDevice> idDeviceMap = new HashMap<Integer, ResourceDevice>();	//id - Device
	
	
	//Areas List 
	protected HashMap<String, Area> idArea = new HashMap<String, Area>();
	protected HashMap<Area, ArrayList<ResourceDevice>> areas = new HashMap<Area, ArrayList<ResourceDevice>>();

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

	public void addSprinklers(Integer id, Actuator actuator) {
		sprinklers.put(id, actuator);
	}
	
	public void addLights(Integer id, Actuator actuator) {
		lights.put(id, actuator);
	}
	
	public void addTempSens(Integer id, Sensor sensor) {
		tempSensors.put(id, sensor);
	}
	
	public void addHumiditySens(Integer id, Sensor sensor) {
		humiditySensors.put(id, sensor);
	}
	
	
	public HashMap<Integer, Actuator> getSprinklers() {
		return sprinklers;
	}

	public HashMap<Integer, Actuator> getLights() {
		return lights;
	}

	public HashMap<Integer, Sensor> getTempSensors() {
		return tempSensors;
	}

	public HashMap<Integer, Sensor> getHumiditySensors() {
		return humiditySensors;
	}
	
	
	public HashMap<Area, ArrayList<ResourceDevice>> getAreas(){
		return areas;
	}
	
	public ResourceDevice getAddressFromId(Integer id) {
		return idDeviceMap.get(id);
	}

	public HashMap<Integer,ResourceDevice> getIdAddressMap(){
		return this.idDeviceMap;
	}
	
	public HashMap<String, Area> getIdArea() {
		return idArea;
	}
	
/*
 * 
 * 		RETURNS DEVICES LISTS
 * 
 */
	
	// PRINT THE LIST OF ALL THE SPRINKLER ACTUATORS
	public void sprinklerActuatorList() {
		for(Integer id: sprinklers.keySet()) {
			Actuator act = sprinklers.get(id);
			System.out.println("Area: " + act.getArea() + ", ID: " + act.getId() + ", addr: " + act.getHostAddress() + ", type: " + act.getDeviceType() + ", Resource: " + act.getResourceType() + ", Status: " + act.getStatus());
		}
	}
	
	// PRINT THE LIST OF ALL THE LIGHT ACTUATORS
	public void lightActuatorList() {
		for(Integer id: lights.keySet()) {
			Actuator act = lights.get(id);
			System.out.println("Area: " + act.getArea() + ", ID: " + act.getId() + ", addr: " + act.getHostAddress() + ", type: " + act.getDeviceType() + ", Resource: " + act.getResourceType()+ ", Status: " + act.getStatus());
		}
	}
	
	// PRINT THE LIST OF ALL THE TEMP SENSORS
	public void tempSensorList() {
		for(Integer id: tempSensors.keySet()) {
			Sensor s = tempSensors.get(id);
			System.out.println("Area: " + s.getArea() + ", ID: " + s.getId() + ", addr: " + s.getHostAddress() + ", type: " + s.getDeviceType() + ", Resource: " + s.getResourceType());
		}
	}
	
	//	PRINT THE LIST OF ALL THE HUM SENSORS
	public void humiditySensorList() {
		for(Integer id: humiditySensors.keySet()) {
			Sensor s = humiditySensors.get(id);
			System.out.println("Area: " + s.getArea() + ", ID: " + s.getId() + ", addr: " + s.getHostAddress() + ", type: " + s.getDeviceType() + ", Resource: " + s.getResourceType());
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
		
		for(Area area_obj: areas.keySet()) {
			String area = area_obj.getId();
			//print area name
			System.out.print("[ " + area + " ] : [ ");
			
			//get device list
			ArrayList<ResourceDevice> device = areas.get(area_obj);
			
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
		for(Integer id: tempSensors.keySet()) {
			Sensor s = tempSensors.get(id);
			System.out.println("Sensor Area: " + s.getArea() + ", Address: " + s.getHostAddress() + 
					", Average Temperatures: " + s.getLastAvgObservation());
		}
	}
	
	//GET THE HUM AVG OF ALL THE SENSORS
	public void getSensorsHumidities() {
		for(Integer id: humiditySensors.keySet()) {
			Sensor s = humiditySensors.get(id);
			System.out.println("Sensor Area: " + s.getArea() + ", Address: " + s.getHostAddress() + 
					", Average Humidities: " + s.getLastAvgObservation());
		}
	}
	
	//GET STATUS OF ALL THE SPRIKNLER
	public void getSprinklersStatus() {
		for(Integer id: sprinklers.keySet()) {
			Actuator act = sprinklers.get(id);
			System.out.println("Actuator Area: " + act.getArea() + ", Address: " + id + 
					", Status: " + act.getStatus());
		}
	}
	
	//GET STATUS OF ALL THE LIGHTS
	public void getLightsStatus() {
		for(Integer id: lights.keySet()) {
			Actuator act = lights.get(id);
			System.out.println("Actuator Area: " + act.getArea() + ", Address: " + act.getHostAddress() + 
					", Status: " + act.getStatus());
		}
	}

	// GET LAST TEMP MEASUR. FOR ALL THE SENSORS 
	public void getLastSensorsTemperatures() {

		for(Integer id: tempSensors.keySet()) {
			
			Sensor s = tempSensors.get(id);

			System.out.print("Sensor Area: " + s.getArea() + ", Address: " + id); 
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
		for(Integer id: humiditySensors.keySet()) {
			Sensor s = humiditySensors.get(id);
			System.out.print("Sensor Area: " + s.getArea() + ", Address: " + s.getHostAddress()); 
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
		
		if(!find) {
			
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

		
		if(!areas.containsKey(idArea.get(area))) {
			ArrayList<ResourceDevice> list = new ArrayList<>();
			list.add(rd);
			areas.put(idArea.get(area), list);
		}else {
			if(!areas.get(idArea.get(area)).contains(rd)) {
				areas.get(idArea.get(area)).add(rd);
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
		
	
		if(!find) {
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
			if(areas.get(old).isEmpty()) {
				areas.remove(old);
				idArea.remove(old);
			}
			
			System.out.println("Resource Device: " + rd.getResourceType() + " removed from area " + old + "\n");
			return;
		}
		
		System.out.println("Resource Device " + rd.getResourceType() + ": Area was not set\n");
		return;
	}

	// GET LIST OF AREAS WITH LIGHT DEVICES
	public void lightAreasList() {
		
		System.out.print("[ ");
		for(Area area_obj: areas.keySet()) {
			ArrayList<ResourceDevice> device = areas.get(area_obj);
			for(ResourceDevice d: device) {
				if(d.getResourceType().compareTo("light") == 0) {
					System.out.print(area_obj.getId() + " ");
					break;
				}
					
			}
		}
		System.out.println("]");
			
		
	}
	
	// GET LIST OF AREAS WITH SPRINKLER DEVICES
	public void sprinklerAreasList() {
		
		System.out.print("[ ");
		for(Area area_obj: areas.keySet()) {
			ArrayList<ResourceDevice> device = areas.get(area_obj);
			for(ResourceDevice d: device) {
				if(d.getResourceType().compareTo("sprinkler") == 0) {
					System.out.print(area_obj.getId() + " ");
					break;
				}
					
			}
		}
		System.out.println("]");
			
		
	}


	public Area generateArea(String id) {
		if(!idArea.containsKey(id)) {
			System.out.println("	--Generating Area " + id + "--	");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			
	
			try {
				
				System.out.print("Insert max temperature tolerated in this area: ");
				int max_t = Integer.valueOf(reader.readLine());
				
				System.out.print("Insert min temperature tolerated in this area: ");
				int min_t = Integer.valueOf(reader.readLine());
				
				System.out.print("Insert max humidity tolerated in this area: ");
				int max_h = Integer.valueOf(reader.readLine());
				
				System.out.print("Insert min humidity tolerated in this area: ");
				int min_h = Integer.valueOf(reader.readLine());
				
				Area area = new Area(id, max_t, min_t, max_h, min_h);
				idArea.put(id, area);
				
				return area;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
			
	}
	
}
