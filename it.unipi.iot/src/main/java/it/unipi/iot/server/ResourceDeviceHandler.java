package it.unipi.iot.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.JSONObject;

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
	protected HashMap<String, ArrayList<Integer>> addressIDs = new HashMap<String, ArrayList<Integer>>();	//address - List of IDs
	
	
	
	/* CONSTATS DEFINITION */
	static final int MIN_VARIATION = 1;
	static final int MAX_VARIATION = 5;
	static final int DEFAULT_AREA_MAX_TEMP = 30;
	static final int DEFAULT_AREA_MIN_TEMP = 0;
	static final int DEFAULT_AREA_MAX_HUM = 100;
	static final int DEFAULT_AREA_MIN_HUM = 0;
	
	/* DATA STRUCTURES FOR AREAS */
	protected HashMap<String, Area> idArea = new HashMap<String, Area>();	//Area ID - Area object
	protected HashMap<Area, ArrayList<ResourceDevice>> areas = new HashMap<Area, ArrayList<ResourceDevice>>();	//Area - List of devices

	private ResourceDeviceHandler()
    {
		//Create the "default" area and set its parameters
		Area area = new Area("default", DEFAULT_AREA_MAX_TEMP, DEFAULT_AREA_MIN_TEMP, DEFAULT_AREA_MAX_HUM, DEFAULT_AREA_MIN_HUM);
        this.idArea.put("default", area);
        ArrayList<ResourceDevice> devices = new ArrayList<ResourceDevice>();
        this.areas.put(area, devices);
      
    }
	
	//Get the instance of ResourceDeviceHandler. Only one instance atomically shared between everyone
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
	
	public HashMap<String, ArrayList<Integer>> getAddressIDs() {
		return addressIDs;
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

	//PRINT ADDRESSES
	public void getAddressesList() {
		
		System.out.print("[");
		for(String addr: addressIDs.keySet()) {
			System.out.print(" " + addr);
		}
		System.out.println(" ]\n");
	}
	
	//PRINT THE LIST OF IDs FOR A GIVEN ADDRESS
	public void getAddressIDs(String address){
		
		if(!addressIDs.containsKey(address))
			System.out.println("[" + address + "]: []");
		else {
			System.out.println("[" + address + "]:");
			for(Integer id: addressIDs.get(address)) {
				System.out.println("[ ID: " + id + ", Resource: " + idDeviceMap.get(id).getResourceType() + " ]");
			}
			System.out.println("");
		}
		
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
		JSONObject json = new JSONObject();
		json.put("status", newStatus);
		
		//send post request
		
		CoapResponse response = c.post(json.toString(), MediaTypeRegistry.APPLICATION_JSON);
		 
		//Check the return code: Success 2.xx
		if(!response.getCode().toString().startsWith("2")) {
			System.out.println("Error code: " + response.getCode().toString());
			return -1;
		}else {
			sprinklers.get(id).setStatus(newStatus);
			System.out.println("Area: " + sprinklers.get(id).getArea() + ", Sprinkler " + id + " set to " + newStatus + "\n");
		}
		return 1;
		
		
	
	}
	
	//	SET STATUS OF SPRINKLERS WITHIN A AREA
	public int setAreaSprinklerStatus(String area, String status) {

		int howMany = 0;
		int result = 0;
		boolean increase = (status.compareTo("ON") == 0) ? true : false;
		boolean decrease = (status.compareTo("OFF") == 0) ? true : false;
		
		if(areas.containsKey(idArea.get(area))) {
			//System.out.println("Set Sprinklers of area " + area + " to " + status);
			ArrayList<ResourceDevice> device = areas.get(idArea.get(area));
			//For each sprinkler in the area I call setSprinklerStatus to set the status
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
			//Only if at least one sprinkler in the area changes its status then the humidity ranges are changed
			if(howMany > 0) {
				for(ResourceDevice d: device) {
					if(d.getResourceType().compareTo("humidity") == 0) {
						if(!editSensorMinMax(d.getId(), increase, decrease))
							return -1;
					}
				}
			}
			//For that area I change the status of its sprinklers
			idArea.get(area).setSprinklersStatus(status);
			if(howMany == 0) {
				//System.out.println("Sprinklers of area " + area + " are already " + status);
			}
			
			//System.out.println("");
		}

		return howMany;

	}

	// SET STATUS OF A LIGHT 
	public int setLightStatus(Integer id, String newStatus) {
		
		CoapClient c = lights.get(id).getClient();

		
		
		//Check if the new status is equal to the previous one: in case no request sent
		if(newStatus.compareTo(lights.get(id).getStatus()) == 0) {
			//System.out.println("The Light " + id + " is already " + newStatus);
			return 0;
		}
		
		//Prepare post payload
		JSONObject json = new JSONObject();
		json.put("status", newStatus);
		
		//send post request
		CoapResponse response = c.post(json.toString(), MediaTypeRegistry.APPLICATION_JSON);
		
		//Check the return code: Success 2.xx
		if(!response.getCode().toString().startsWith("2")) {
			System.out.println("Error code: " + response.getCode().toString());
			return -1;
		}else {
			lights.get(id).setStatus(newStatus);
			System.out.println("Area: "+ lights.get(id).getArea() + ", Light " + id + " set to " + newStatus + "\n");
		}
		
		return 1;
		
		
	
	}
	
	//	SET STATUS OF LIGHTS WITHIN A AREA
	public int setAreaLightStatus(String area, String status) {
		
		int howMany = 0;
		int result = 0;
		boolean increase = (status.compareTo("ON") == 0) ? true : false;
		boolean decrease = (status.compareTo("OFF") == 0) ? true : false;
		
		if(areas.containsKey(idArea.get(area))) {
			//System.out.println("Set Lights of area " + area + " to " + status);
			ArrayList<ResourceDevice> device = areas.get(idArea.get(area));
			//For each light in the area I call setLightStatus to set the status
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
			//Only if at least one light in the area changes its status then the temperature ranges are changed
			if(howMany > 0) {
				for(ResourceDevice d: device) {
					if(d.getResourceType().compareTo("temperature") == 0) {
						if(!editSensorMinMax(d.getId(), increase, decrease))
							return -1;
					}
				}
			}
			idArea.get(area).setLightsStatus(status);
			if(howMany == 0) {
				//System.out.println("Lights of area " + area + " are already " + status);
			}
			
			//System.out.println("");
		}
		
		return howMany;

	}

	//Edit the range for generating temperature/humidity because light/sprinkler switched
	public boolean editSensorMinMax(int id, boolean increase, boolean decrease) {
		
		if(!increase && !decrease || increase && decrease)
			return false;
		
		CoapClient c = idDeviceMap.get(id).getClient();
		
		int randomInt = (int)Math.floor(Math.random()*(MAX_VARIATION-MIN_VARIATION+1)+MIN_VARIATION);
		
		
		JSONObject json = new JSONObject();
		
		//Prepare post payload
		if(increase)
			json.put("increase", randomInt);
			
		
		if(decrease)
			json.put("decrease", randomInt);
			
		
		//send post request
		CoapResponse response = c.post(json.toString(), MediaTypeRegistry.APPLICATION_JSON);
		
		//Check the return code: Success 2.xx
		if(!response.getCode().toString().startsWith("2")) {
			System.out.println("Error code: " + response.getCode().toString());
			return false;
		}
		
		return true;
		
	}
	
	//Unregister Device
	public boolean unRegisterDevice(String address) {
		
		CoapClient c = new CoapClient("coap://[" + address + "]:5683/unregister");
		
		JSONObject json = new JSONObject();
		
		
		json.put("unregister", "true");
			
		
		//send post request
		CoapResponse response = c.post(json.toString(), MediaTypeRegistry.APPLICATION_JSON);
		
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
			
			//if sprinkler I switch the resource based on the area
			if(rd.getResourceType().compareTo("sprinkler") == 0) {
				String status = idArea.get(rd.getArea()).getSprinklersStatus();
				if(setSprinklerStatus(rd.getId(), status) < 0)
					System.out.println("Error in changing the sprinkler status accordingly to the area");
			}
			
			//if light I switch the resource based on the area
			if(rd.getResourceType().compareTo("light") == 0) {
				String status = idArea.get(rd.getArea()).getLightsStatus();
				if(setLightStatus(rd.getId(), status) < 0)
					System.out.println("Error in changing the sprinkler status accordingly to the area");
			}
			find = true;
		}
		
		if(!find) {
			
			System.out.println("No Device with that id\n");
			
		}
		
		
		
		
	}
	
	//Add the resource to the area
	public void addResourceArea(ResourceDevice rd, String area) {
		
		Area old = null;
			
		//If a area was already set, take the area. If no error, the device will be removed from that list.
		if(rd.getArea() != null && (rd.getArea().compareTo(area)!=0)) {
			old = idArea.get(rd.getArea());
		}
		
		
		if(!areas.containsKey(idArea.get(area))) {
			System.out.println("The area " + area + " does not exist. Start creation.");
			Area area_obj = null;
			
			
			if(idArea.containsKey(area))	//Area already exists
				area_obj = idArea.get(area);
			else							//Area must be created
				area_obj = generateArea(area);
			
			if(area_obj == null) {
				System.out.println("Error in generating the area\n");
				return;
			}
			ArrayList<ResourceDevice> list = new ArrayList<>();
			list.add(rd);
			areas.put(area_obj, list);
		}else {
			System.out.println("The area " + area + " already exists. Just add the device.");
			if(!areas.get(idArea.get(area)).contains(rd)) {
				areas.get(idArea.get(area)).add(rd);
			}
			
			
		}
		
		rd.setArea(area);
		System.out.println("Device Area set to: \"" +rd.getArea() + "\" for resource: " + rd.getResourceType() + "\n");
		
		//If a area was already set, remove the device from that list.
		if(old != null) {
			areas.get(old).remove(rd);
			
			//If the area remains empty remove it 
			if(areas.get(old).isEmpty()){
				areas.remove(old);
				if(old.getId().compareTo("default") != 0)
					idArea.remove(old.getId());
			}
		}
				
		
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
		if(rd.getArea().compareTo("default") != 0) {	//If is already in default area, do nothing
			addDeviceArea(rd.getId(), "default");
	
			
			System.out.println("Resource Device: " + rd.getResourceType() + " removed from area " + old + "\n");
			return;
		}
		
		System.out.println("Resource Device " + rd.getResourceType() + ": was already in \"default\" area\n");
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

	// CREATE A NEW AREA
	public Area generateArea(String id) {
		if(!idArea.containsKey(id)) {
			System.out.println("	--Generating Area " + id + "--	");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			
	
			try {
				
				System.out.print("Insert max temperature tolerated in this area: ");
				int max_t = 0;
				try{
					max_t = Integer.parseInt(reader.readLine());
				} catch(NumberFormatException e) {
					System.out.println("The input must be a number\n");
					return null;
				}
			        
			    
				System.out.print("Insert min temperature tolerated in this area: ");
				int min_t = 0;
				try{
					min_t = Integer.parseInt(reader.readLine());
				} catch(NumberFormatException e) {
					System.out.println("The input must be a number\n");
					return null;
				}
			        
			    
				System.out.print("Insert max humidity tolerated in this area: ");
				int max_h = 0;
				try{
					max_h = Integer.parseInt(reader.readLine());
				} catch(NumberFormatException e) {
					System.out.println("The input must be a number\n");
					return null;
				}
			        
			    
				System.out.print("Insert min humidity tolerated in this area: ");
				int min_h = 0;
				try{
					min_h = Integer.parseInt(reader.readLine());
				} catch(NumberFormatException e) {
					System.out.println("The input must be a number\n");
					return null;
				}
			   
				if((max_t < min_t) || (max_h < min_h)) {
					System.out.println("Error in typing parameters\n");
					return null;
				}
					
				
				Area area = new Area(id, max_t, min_t, max_h, min_h);
				idArea.put(id, area);
				
				return area;
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
			
	}
	
	
	/*
	 * 
	 * REMOVE DEVICES
	 * 
	 */
	
	//REMOVE ALL DEVICES WITH GIVEN ADDRESS
	public boolean removeDevicesAddress(String address) {
		
		//I take all the IDs of the device with the given address
		ArrayList<Integer> ids = new ArrayList<>();
		for(Integer el: this.getAddressIDs().get(address))
			ids.add(el);

		//For each device, I remove it
		for(Integer id: ids) {
			if(!removeDevice(id))
				return false;
		}
		
		ids.clear();
		System.out.println("Removed devices with address: " + address);
		
		//Notify the device that has been unregistered from the application
		if(unRegisterDevice(address))
			System.out.println("Unregister devices with address: " + address + "\n");
		return true;
			
	}
	
	//REMOVE DEVICE WITH A GIVE ID
	public boolean removeDevice(Integer id) {
		
		ResourceDevice rd = idDeviceMap.get(id);
		String address = rd.getHostAddress();
		
		switch(rd.getResourceType()) {
		case "humidity":
			removeDeviceArea(id);	//remove from an area and put it in the default one
			areas.get(idArea.get("default")).remove(rd);	//remove also from default area
			humiditySensors.remove(id);		//remove from humiditySens map
			idDeviceMap.remove(id);
			break;
		case "temperature":
			removeDeviceArea(id);	//remove from an area and put it in the default one
			areas.get(idArea.get("default")).remove(rd);	//remove also from default area
			tempSensors.remove(id);		//remove from tempSensors map
			idDeviceMap.remove(id);
			break;
		case "sprinkler":
			removeDeviceArea(id);	//remove from an area and put it in the default one
			areas.get(idArea.get("default")).remove(rd);	//remove also from default area
			setSprinklerStatus(id, "OFF");	//switch off before unregister
			sprinklers.remove(id);		//remove from sprinklers map
			idDeviceMap.remove(id);
			break;
		case "light":
			removeDeviceArea(id);	//remove from an area and put it in the default one
			areas.get(idArea.get("default")).remove(rd);	//remove also from default area
			setLightStatus(id, "OFF");	//switch off before unregister
			lights.remove(id);			//remove from light map
			idDeviceMap.remove(id);
			break;
		default:
			System.out.println("Error in removing device " + id + "\n");
			return false;
		}
		
		//Remove the ID for the list 
		int index = this.getAddressIDs().get(address).indexOf(id);	//take the index position
		this.getAddressIDs().get(address).remove(index);

		
		System.out.println("Device " + id + " removed\n");
		
		//remove the address from the map
		if(this.getAddressIDs().get(address).isEmpty()) {
			System.out.println("No more devices with the address: " + address + ". Remove it\n");
			this.getAddressIDs().remove(address);
		}
		
		return true;
		
	}

	//REMOVE ALL DEVICES
	public boolean removeAllDevices() {
		
		System.out.println("Removing all the Devices...");
		
		//I take all the devices addresses in the system
		ArrayList<String> addresses = new ArrayList<>();
		for(String address: addressIDs.keySet())
			addresses.add(address);
		
		//For each address, I call the remove function
		for(String address: addresses)
			if(!removeDevicesAddress(address))
				return false;
		
		addresses.clear();
		System.out.println("All devices have been unregistered and removed\n");
		return true;
	}
	
}
