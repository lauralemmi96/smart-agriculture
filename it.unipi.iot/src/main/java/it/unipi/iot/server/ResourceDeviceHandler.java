package it.unipi.iot.server;

import java.util.HashMap;

import it.unipi.iot.resource_devices.Actuator;
import it.unipi.iot.resource_devices.Sensor;

public class ResourceDeviceHandler {
	
	//A single instance of this class is needed to maintain the devices info
	private static ResourceDeviceHandler single_instance = null;
	
	protected HashMap<String, Actuator> sprinklers = new HashMap<String, Actuator>();
	protected HashMap<String, Actuator> lights = new HashMap<String, Actuator>();
	protected HashMap<String, Sensor> tempSensors = new HashMap<String, Sensor>();
	protected HashMap<String, Sensor> humiditySensors = new HashMap<String, Sensor>();

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
	
	public void sprinklerList() {
		for(String addr: sprinklers.keySet()) {
			Actuator act = sprinklers.get(addr);
			System.out.println("ADDR: " + addr + ", DeviceType: " + act.getDeviceType() + 
					", Resource: " + act.getResourceType() + ", Status: " + act.getStatus());
		}
	}
	
	public void lightList() {
		for(String addr: lights.keySet()) {
			Actuator act = lights.get(addr);
			System.out.println("ADDR: " + addr + ", DeviceType: " + act.getDeviceType() + 
					", Resource: " + act.getResourceType() + ", Status: " + act.getStatus());
		}
	}
	
	public void tempList() {
		for(String addr: tempSensors.keySet()) {
			Sensor s = tempSensors.get(addr);
			System.out.println("ADDR: " + addr + ", DeviceType: " + s.getDeviceType() + 
					", Resource: " + s.getResourceType());
		}
	}
	
	public void humidityList() {
		for(String addr: humiditySensors.keySet()) {
			Sensor s = tempSensors.get(addr);
			System.out.println("ADDR: " + addr + ", DeviceType: " + s.getDeviceType() + 
					", Resource: " + s.getResourceType());
		}
	}
	
	
	
	
}
