package it.unipi.iot.server;

import java.util.HashMap;

import it.unipi.iot.resource_devices.Actuator;
import it.unipi.iot.resource_devices.Sensor;

public class ResourceDeviceHandler {
	
	//A single instance of this class is needed to maintain the devices info
	private static ResourceDeviceHandler single_instance = null;
	
	protected HashMap<String, Actuator> sprinklers = new HashMap<String, Actuator>();
	protected HashMap<String, Actuator> lights = new HashMap<String, Actuator>();
	protected HashMap<String, Sensor> temp_sensors = new HashMap<String, Sensor>();
	protected HashMap<String, Sensor> humidity_sensors = new HashMap<String, Sensor>();

	private ResourceDeviceHandler()
    {
      
    }
	
	public static ResourceDeviceHandler getInstance(){
		if (single_instance == null)
            single_instance = new ResourceDeviceHandler();
 
        return single_instance;
    }
}
