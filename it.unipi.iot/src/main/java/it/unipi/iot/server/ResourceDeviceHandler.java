package it.unipi.iot.server;

import java.util.HashMap;

import it.unipi.iot.resource_devices.Actuator;
import it.unipi.iot.resource_devices.Sensor;

public class ResourceDeviceHandler {
	
	//A single instance of this class is needed to maintain shared and consistent info of the devices
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
	
	public void sprinklerActuatorList() {
		for(String addr: sprinklers.keySet()) {
			Actuator act = sprinklers.get(addr);
			System.out.println("ADDR: " + addr + ", DeviceType: " + act.getDeviceType() + 
					", Resource: " + act.getResourceType() + ", Status: " + act.getStatus());
		}
	}
	
	public void lightActuatorList() {
		for(String addr: lights.keySet()) {
			Actuator act = lights.get(addr);
			System.out.println("ADDR: " + addr + ", DeviceType: " + act.getDeviceType() + 
					", Resource: " + act.getResourceType() + ", Status: " + act.getStatus());
		}
	}
	
	public void tempSensorList() {
		for(String addr: tempSensors.keySet()) {
			Sensor s = tempSensors.get(addr);
			System.out.println("ADDR: " + addr + ", DeviceType: " + s.getDeviceType() + 
					", Resource: " + s.getResourceType());
		}
	}
	
	public void humiditySensorList() {
		for(String addr: humiditySensors.keySet()) {
			Sensor s = humiditySensors.get(addr);
			System.out.println("ADDR: " + addr + ", DeviceType: " + s.getDeviceType() + 
					", Resource: " + s.getResourceType());
		}
	}
	
	public void getSensorsTemperatures() {
		for(String addr: tempSensors.keySet()) {
			Sensor s = tempSensors.get(addr);
			System.out.println("Sensor Address: " + addr + 
					", Average Temperatures: " + s.getLastAvgObservation());
		}
	}
	
	public void getSensorsHumidities() {
		for(String addr: humiditySensors.keySet()) {
			Sensor s = humiditySensors.get(addr);
			System.out.println("Sensor Address: " + addr + 
					", Average Humidities: " + s.getLastAvgObservation());
		}
	}
	
	public void getSprinklersStatus() {
		for(String addr: sprinklers.keySet()) {
			Actuator act = sprinklers.get(addr);
			System.out.println("Actuator Address: " + addr + 
					", Status: " + act.getStatus());
		}
	}
	
	public void getLightsStatus() {
		for(String addr: lights.keySet()) {
			Actuator act = lights.get(addr);
			System.out.println("Actuator Address: " + addr + 
					", Status: " + act.getStatus());
		}
	}

	public void getLastSensorsTemperatures() {

		for(String addr: tempSensors.keySet()) {
			
			Sensor s = tempSensors.get(addr);

			System.out.print("Sensor Address: " + addr); 
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

	public void getLastSensorsHumidities() {
		for(String addr: humiditySensors.keySet()) {
			Sensor s = humiditySensors.get(addr);
			System.out.print("Sensor Address: " + addr); 
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
	
	
	
	
}
