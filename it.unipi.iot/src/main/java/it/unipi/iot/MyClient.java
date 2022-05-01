package it.unipi.iot;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import it.unipi.iot.server.ResourceDeviceHandler;
import it.unipi.iot.server.Server;

public class MyClient {
	
	protected static Server server;
	protected static BufferedReader reader;
	protected static ResourceDeviceHandler handler;
	
	public static void main(String[] args) {
		
		System.out.println("---- CLIENT STARTED ----");
		
		server = new Server();
		
		//new thread to let server running 
		new Thread() {
			public void run() {
				server.start();
			}
		}.start();
		
		// Take ResourceDeviceHandler instance
		handler = ResourceDeviceHandler.getInstance();
		
		/* User interface */
		
		reader = new BufferedReader(new InputStreamReader(System.in));
		showCommands();
		
		while(true) {
			System.out.println("Type a command\n");
			
			try {
				
				String command = reader.readLine();
				
				switch(command) {
					
					case "!help":
						showCommands();
						break;
						
					case "!getSensors":
						getSensors();
						break;
						
					case "!getActuators":
						getActuators();
						break;
						
					case "!getAreasList":
						getAreasList();
						break;
						
					case "!getAvgTemperature":
						getAvgTemperature();
						break;
						
					case "!getLastTemp":
						getLastTemp();
						break;
					
					case "!getLastHum":
						getLastHum();
						break;
					
					case "!getAvgHumidity":
						getAvgHumidity();
						break;
						
					case "!getSprinklerStatus":
						getSprinklerStatus();
						break;
						
					case "!getLightStatus":
						getLightStatus();
						break;
						
					case "!setAreaSprinklerStatus":
						setAreaSprinklerStatus();
						break;
						
					case "!setAreaLightStatus":
						setAreaLightStatus();
						break;
						
					case "!setDeviceArea":
						setDeviceArea();
						break;
						
					case "!removeDeviceArea":
						removeDeviceArea();
						break;
						
					default:
						System.out.println("Command not defined\n");
						break;
				}
				
				
				
				
				
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		
		
		
	}
	
	private static void showCommands() {
		
		System.out.println("");
		System.out.println("	--	This is the list of accepted command	--	\n");
		System.out.println("!help 			-->	Get the list of the available commands\n");
		
		
		System.out.println("	--	GET COMMANDS	--	");
		
		System.out.println("!getSensors 		-->	Get the list of registered sensors");
		System.out.println("!getActuators 		-->	Get the list of registered actuators");	
		System.out.println("!getAreasList		--> Get the list of areas and their info");
		System.out.println("!getLastTemp		-->	Get the list of the last temp measurements");
		System.out.println("!getLastHum		-->	Get the list of the last humidity measurements");
		System.out.println("!getAvgTemperature	-->	Get the Avg temperature of the last 10 measurements for all the sensors");
		System.out.println("!getAvgHumidity		-->	Get the Avg humidity of the last 10 measurements for all the sensors");
		System.out.println("!getSprinklerStatus	-->	Get the status of the sprinklers");
		System.out.println("!getLightStatus		-->	Get the status of the lights");
		System.out.println("!getSprinklerStatus	-->	Get the status of the sprinklers");
		
		System.out.println("");
		System.out.println("	--	POST COMMANDS	--	");
		System.out.println("!setAreaSprinklerStatus	-->	Set the status of a sprinkler");
		System.out.println("!setAreaLightStatus		-->	Set the status of lights in a area");
		System.out.println("!setDeviceArea		-->	Set the area the device belongs to");
		System.out.println("!removeDeviceArea		-->	Remove the area the device belongs to");

		
		
		
		System.out.println("");
		

	}
	
/*
 * 
 * 		GET METHODS
 * 
 */
	
	//Get the list of the sensors (area, address, resType)
	private static void getSensors() {
		
		if(handler.getHumiditySensors().isEmpty() && handler.getTempSensors().isEmpty()) {
			System.out.println("No Sensor Registered\n");
			return;
		}
	
		System.out.println(" 	--	Sensors List	--	");
		handler.humiditySensorList();
		handler.tempSensorList();
		System.out.println("");
	}
	
	//Get the list of the actuators (area, address, resType, status)
	private static void getActuators() {
		
		if(handler.getSprinklers().isEmpty() && handler.getLights().isEmpty()) {
			System.out.println("No Actuator Registered\n");
			return;
		}
		
		System.out.println(" 	--	Actuators List	--	");
		handler.sprinklerActuatorList();
		handler.lightActuatorList();
		System.out.println("");
	}
	
	//For all the temp sensors, get the last (max 10) measurements
	private static void getLastTemp() {
		System.out.println("	--	Last Temperatures Detected	--	");
		handler.getLastSensorsTemperatures();
		System.out.println("");
	}
	
	//For all the hum sensors, get the last (max 10) measurements
	private static void getLastHum() {
		System.out.println("	--	Last Humidities Detected	--	");
		handler.getLastSensorsHumidities();
		System.out.println("");
	}
	
	//For all the temp sensors, get the avg of the last 10 measurements
	private static void getAvgTemperature() {
		
		System.out.println("	--	Last Average Temperatures Detected	--	");
		handler.getSensorsTemperatures();
		System.out.println("");
	}
	
	//For all the hum sensors, get the avg of the last 10 measurements
	private static void getAvgHumidity() {
		
		System.out.println("	--	Last Average Humidities Detected	--	");
		handler.getSensorsHumidities();
		System.out.println("");
	}
	
	//Get the status of all the sprinklers
	private static void getSprinklerStatus() {
		System.out.println("	--	Get Sprinklers Status	--	");
		handler.getSprinklersStatus();
		System.out.println("");
	}
	
	//Get the status of all the lights
	private static void getLightStatus() {
		System.out.println("	--	Get Lights Status	--	");
		handler.getLightsStatus();
		System.out.println("");
	}
	
	//Get the list of the areas with their devices
	private static void getAreasList() {
		System.out.println("	--	Get Areas Info	--	");
		handler.getAreasList();
		System.out.println("");
	}
	
/*
 * 
 * 		SET METHODS
 * 
 */
	
	//Get the status of sprinklers within the area
	private static void setAreaSprinklerStatus() {
		System.out.println("Type the area of the sprinkler you want to switch");
		System.out.println("Available Sprinklers: ");
		handler.sprinklerAreasList();
		try {
			String area = reader.readLine();
			boolean valid = true;
			
			if(!handler.getAreas().containsKey(area)) {
				System.out.println("Error! This is not a valid area.\n ");
				return;
			}	
			
			System.out.println("Type the new status: ON or OFF");
			String status = reader.readLine().toUpperCase();
			
			while((status.compareTo("ON")!= 0) && (status.compareTo("OFF")!= 0)) {
					System.out.println("Error! Invalid status.\n "
							+ "Retry or type \"!Exit\" for a new operation");
					
					status = reader.readLine().toUpperCase();
					if(status.compareTo("!Exit") == 0) {
						valid = false;
						break;
					}
				}
			if(valid) {
				if(!handler.setSprinklerStatus(area, status))
					System.out.println("Something went wrong!\n");
				else
					System.out.println("Status Changed\n");
			}
				
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Get the status of the Lights within the area
	private static void setAreaLightStatus() {
		System.out.println("Type the area of the lights you want to switch");
		System.out.println("Available Areas with Lights: ");
		handler.lightAreasList();
		try {
			String area = reader.readLine();
			boolean valid = true;
			
			if(!handler.getAreas().containsKey(area)) {
				System.out.println("Error! This is not a valid area.\n ");
				return;
			}	
			
			System.out.println("Type the new status: ON or OFF");
			String status = reader.readLine().toUpperCase();
			
			while((status.compareTo("ON")!= 0) && (status.compareTo("OFF")!= 0)) {
					System.out.println("Error! Invalid status.\n "
							+ "Retry or type \"!Exit\" for a new operation");
					
					status = reader.readLine().toUpperCase();
					if(status.compareTo("!Exit") == 0) {
						valid = false;
						break;
					}
				}
			if(valid) {
				if(!handler.setAreaLightStatus(area, status))
					System.out.println("Something went wrong!\n");
				else
					System.out.println("Status Changed\n");
			}
				
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Set the area a device belongs to
	private static void setDeviceArea() {
		System.out.println("Type the address of the device");
		System.out.println("Available Devices: ");
		handler.devicesList();
		
		try {
			String address = reader.readLine();
			
			if(!handler.getDevice(address)) {
				System.out.println("Error! This is not a device address.\n ");
				return;
			}
			
			System.out.println("Type the area");
			String area = reader.readLine().toLowerCase();
			handler.addDeviceArea(address, area);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	
	//Remove the area a device belongs to
	private static void removeDeviceArea() {
		System.out.println("Type the address of the device");
		System.out.println("Available Devices: ");
		handler.devicesList();
		
		try {
			String address = reader.readLine();
			
			if(!handler.getDevice(address)) {
				System.out.println("Error! This is not a device address.\n ");
				return;
			}
			
			handler.removeDeviceArea(address);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
