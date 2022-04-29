package it.unipi.iot.server;

import java.util.HashMap;

import it.unipi.iot.resource_devices.Actuator;
import it.unipi.iot.resource_devices.Sensor;

public class ResourceDeviceHandler {
	
	protected HashMap<String, Actuator> sprinklers = new HashMap<String, Actuator>();
	protected HashMap<String, Actuator> lights = new HashMap<String, Actuator>();
	protected HashMap<String, Sensor> temp_sensors = new HashMap<String, Sensor>();
	protected HashMap<String, Sensor> humidity_sensors = new HashMap<String, Sensor>();

}
