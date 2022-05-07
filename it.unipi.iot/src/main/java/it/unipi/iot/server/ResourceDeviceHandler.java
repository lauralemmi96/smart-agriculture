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
	private static ResourceDeviceHandler singleInstance = null;
	
	//ID counter to be assigned to devices
	private static int deviceID = 0;
	
	/* DATA STRUCTURES TO HOLD DEVICES */
	protected HashMap<Integer, Actuator> sprinklers = new HashMap<Integer, Actuator>();	//ID - sprinkler
	protected HashMap<Integer, Actuator> lights = new HashMap<Integer, Actuator>();		//ID - light
	protected HashMap<Integer, Sensor> tempSensors = new HashMap<Integer, Sensor>();		//ID - temp
	protected HashMap<Integer, Sensor> humiditySensors = new HashMap<Integer, Sensor>();	//ID - humidity
	protected HashMap<Integer, ResourceDevice> idDeviceMap = new HashMap<Integer, ResourceDevice>();	//id - Device
	
	
	/* CONSTATS DEFINITION */
	static final int MIN_VARIATION = 1;
	static final int MAX_VARIATION = 5;
	static final int DEFAULT_AREA_MAX_TEMP = 30;
	static final int DEFAULT_AREA_MIN_TEMP = 0;
	static final int DEFAULT_AREA_MAX_HUM = 100;
	static final int DEFAULT_AREA_MIN_HUM = 0;
	
	//Areas List 
	protected HashMap<String, Area> idArea = new HashMap<String, Area>();
	protected HashMap<Area, ArrayList<ResourceDevice>> areas = new HashMap<Area, ArrayList<ResourceDevice>>();

	private ResourceDeviceHandler()
    {
		Area area = new Area("default", DEFAULT_AREA_MAX_TEMP, DEFAULT_AREA_MIN_TEMP, DEFAULT_AREA_MAX_HUM, DEFAULT_AREA_MIN_HUM);
        this.idArea.put("default", area);
        ArrayList<ResourceDevice> devices = new ArrayList<ResourceDevice>();
        this.areas.put(area, devices);
      
    }
	
	public static ResourceDeviceHandler getInstance(){
		if (singleInstance == null) {
            singleInstance = new ResourceDeviceHandler();
		}
 
        return singleInstance;
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
	
	public ResourceDevice getDeviceFromId(Integer id) {
		return idDeviceMap.get(id);
	}

	public HashMap<Integer,ResourceDevice> getIdDeviceMap(){
		return this.idDeviceMap;
	}
	
	public void setIdDeviceMap(Integer id, ResourceDevice rd){
		this.idDeviceMap.put(id, rd);
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
		
		
		if(tempSensors.containsKey(id))
			return true;
		if(humiditySensors.containsKey(id))
			return true;
		if(sprinklers.containsKey(id))
			return true;
		if(lights.containsKey(id))
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
			
			//print area name
			System.out.print("[ " + area_obj.getId() + " ] :");
			
			//get device list
			ArrayList<ResourceDevice> device = areas.get(area_obj);
			
			for(ResourceDevice d: device) {
					System.out.print("\n{ID: " + d.getId() + ", IP: " + d.getHostAddress() + ", type: " 
							+ d.getDeviceType() + ", res: " + d.getResourceType() + " } " );
					

			}
			System.out.println("");
					
			
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
			System.out.println("Actuator Area: " + act.getArea() + ", ID: " + id +  
					", Address: " + act.getHostAddress() +
					", Status: " + act.getStatus());
		}
	}
	
	//GET STATUS OF ALL THE LIGHTS
	public void getLightsStatus() {
		for(Integer id: lights.keySet()) {
			Actuator act = lights.get(id);
			System.out.println("Actuator Area: " + act.getArea() + ", ID: " + id 
					+ ", Address: " + act.getHostAddress() + 
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
	public int setSprinklerStatus(Integer id, String newStatus) {
		
		CoapClient c = sprinklers.get(id).getClient();		
		
		//Check if the new status is equal to the previuos one: in case no request sent
		if(newStatus.compareTo(sprinklers.get(id).getStatus()) == 0) {
			//System.out.println("The Sprinkler " + id + " is already " + newStatus);
			return 0;
		}
		//Prepare post payload
		String requestAttribute = "status=" + newStatus;
		
		//send post request
		CoapResponse response = c.post(requestAttribute, MediaTypeRegistry.TEXT_PLAIN);
		 
		//Check the return code: Success 2.xx
		if(!response.getCode().toString().startsWith("2")) {
			System.out.println("Error code: " + response.getCode().toString());
			return -1;
		}else
			System.out.println("Sprinkler " + id + " set to " + newStatus);
		return 1;
		
		
	
	}
	
	//	SET STATUS OF SPRINKLERS WITHIN A AREA
	public int setAreaSprinklerStatus(String area, String status) {

		int howMany = 0;
		int result = 0;
		
		if(areas.containsKey(idArea.get(area))) {
			ArrayList<ResourceDevice> device = areas.get(idArea.get(area));
			for(ResourceDevice d: device) {
				result = 0;
				if(d.getResourceType().compareTo("sprinkler") == 0) {
					result = setSprinklerStatus(d.getId(), status);
					if(result == -1)
						return -1;
					else
						howMany += result;
				}
					
			}
		}

		return howMany;

	}
	
	
	// SET STATUS OF A LIGHT 
	public int setLightStatus(Integer id, String newStatus) {
		
		CoapClient c = lights.get(id).getClient();

		
		
		//Check if the new status is equal to the previuos one: in case no request sent
		if(newStatus.compareTo(lights.get(id).getStatus()) == 0) {
			//System.out.println("The Light " + id + " is already " + newStatus);
			return 0;
		}
		
		//Prepare post payload
		String requestAttribute = "status=" + newStatus;
		
		//send post request
		CoapResponse response = c.post(requestAttribute, MediaTypeRegistry.TEXT_PLAIN);
		
		//Check the return code: Success 2.xx
		if(!response.getCode().toString().startsWith("2")) {
			System.out.println("Error code: " + response.getCode().toString());
			return -1;
		}else
			System.out.println("Light " + id + " set to " + newStatus);
		
		return 1;
		
		
	
	}
	
	//	SET STATUS OF LIGHTS WITHIN A AREA
	public int setAreaLightStatus(String area, String status) {
		
		int howMany = 0;
		int result = 0;
		
		if(areas.containsKey(idArea.get(area))) {
			ArrayList<ResourceDevice> device = areas.get(idArea.get(area));
			for(ResourceDevice d: device) {
				result = 0;
				if(d.getResourceType().compareTo("light") == 0) {
					result = setLightStatus(d.getId(), status);
					if(result == -1)
						return -1;
					else
						howMany += result;
				}
					
			}
		}
		
		return howMany;

	}
	

	//Edit the range for generating temperature/humidity because light/sprinkler switched
	public boolean editSensorMinMax(int id, boolean increase, boolean decrease) {
		
		if(!increase && !decrease || increase && decrease)
			return false;
		
		CoapClient c = idDeviceMap.get(id).getClient();
		
		int randomInt = (int)Math.floor(Math.random()*(MAX_VARIATION-MIN_VARIATION+1)+MIN_VARIATION);
		
		String requestAttribute = null;
		//Prepare post payload
		if(increase)
			requestAttribute = "increase=" + randomInt;
		
		if(decrease)
			requestAttribute = "decrease=" + randomInt;
		
		//send post request
		CoapResponse response = c.post(requestAttribute, MediaTypeRegistry.TEXT_PLAIN);
		
		//Check the return code: Success 2.xx
		if(!response.getCode().toString().startsWith("2")) {
			System.out.println("Error code: " + response.getCode().toString());
			return false;
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
			
			System.out.println("No Device with that id\n");
			
		}
		
		
		
		
	}
	
	//Add the resource to the area
	public void addResourceArea(ResourceDevice rd, String area) {
		
		
			
		//If a area was already set, remove the device from that list.
		if(rd.getArea() != null) {
			areas.get(idArea.get(rd.getArea())).remove(rd);
			
			//If the area remains empty remove it 
			//if(areas.get(idArea.get(rd.getArea())).isEmpty() && area.compareTo("default")!= 0)
			if(areas.get(idArea.get(rd.getArea())).isEmpty())
				areas.remove(idArea.get(rd.getArea()));
		}
		
		
		rd.setArea(area);

		
		if(!areas.containsKey(idArea.get(area))) {
			System.out.println("The area " + area + " does not exist. Start creation.");
			Area area_obj = generateArea(area);
			ArrayList<ResourceDevice> list = new ArrayList<>();
			list.add(rd);
			areas.put(area_obj, list);
		}else {
			System.out.println("The area " + area + " already exists. Just add the device.");
			if(!areas.get(idArea.get(area)).contains(rd)) {
				areas.get(idArea.get(area)).add(rd);
			}
			
			
		}
		
		System.out.println("Device Area set to: \"" +rd.getArea() + "\" for resource: " + rd.getResourceType());
				
		
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
		
		String old = rd.getArea();
		if(rd.getArea() != null) {
			areas.get(idArea.get(old)).remove(rd);
			
			//If the area remains empty remove it 
			if(areas.get(idArea.get(old)).isEmpty()) {
				areas.remove(idArea.get(old));
				idArea.remove(old);
			}
			
			
			//Reassign device to default area
			rd.setArea("default");
			Area def = idArea.get("default");
			
			if(def == null) {
				def = generateArea("default");
				idArea.put("default", def);
				ArrayList<ResourceDevice> d = new ArrayList<ResourceDevice>();
				d.add(rd);
				areas.put(def, d);
			}else {
				areas.get(def).add(rd);
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
