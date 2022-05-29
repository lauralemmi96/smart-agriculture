package it.unipi.iot;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import it.unipi.iot.resource_devices.Area;
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
		

		System.out.println("Type \"!help\" to know the commands\n");
				
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
						
					case "!getAreasInfo":
						showAreasInfo();
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
						
					case "!switchAreaMode":
						switchAreaMode();
						break;
						
					case "!editAreaThreshold":
						editAreaThreshold();
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
		System.out.println("!getAreasInfo			--> Get the list of areas and their info");
		System.out.println("!getAreasList			--> Get the list of areas and their devices");
		//System.out.println("!getLastTemp		-->	Get the list of the last temp measurements");
		//System.out.println("!getLastHum		-->	Get the list of the last humidity measurements");
		System.out.println("!getAvgTemperature	-->	Get the Avg temperature of the last 10 measurements for all the sensors");
		System.out.println("!getAvgHumidity		-->	Get the Avg humidity of the last 10 measurements for all the sensors");
		System.out.println("!getSprinklerStatus	-->	Get the status of the sprinklers");
		System.out.println("!getLightStatus		-->	Get the status of the lights");
		
		System.out.println("");
		System.out.println("	--	POST COMMANDS	--	");
		System.out.println("!setAreaSprinklerStatus	-->	Set the status of sprinklers in a area");
		System.out.println("!setAreaLightStatus		-->	Set the status of lights in a area");
		System.out.println("!setDeviceArea		-->	Set the area the device belongs to");
		System.out.println("!removeDeviceArea		-->	Remove the area the device belongs to");
		System.out.println("!switchAreaMode		-->	Set the management mode of an area");
		System.out.println("!editAreaThreshold		-->	Modify the min/max thresholds for temp and humidity");
		

		
		
		
		System.out.println("");
		

	}
	
/*
 * 
 * 		GET METHODS
 * 
 */
	
	//Get the list of the sensors (area, id, address, resType)
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
	
	//Get the list of the actuators (area, id, address, resType, status)
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
	
	private static boolean showAreasInfo() {
		System.out.println("Available areas: ");
		for(String areaId: handler.getIdArea().keySet()) {
			System.out.print("[ ");
			handler.getIdArea().get(areaId).printAreaInfo();
			System.out.println(" ]");
		}
		System.out.println("");
		
		if(handler.getIdArea().keySet().size() == 0)
			return false;
		return true;
		
	}
	
	
/*
 * 
 * 		SET METHODS
 * 
 */
	
	//Get the status of sprinklers within the area
	private static void setAreaSprinklerStatus() {
		
		System.out.println("Available areas with Sprinklers: ");
		handler.sprinklerAreasList();
		
		System.out.println("\nType the area of the sprinkler you want to switch");
		
		try {
			String area = reader.readLine();
			boolean valid = true;
			
			if(!handler.getAreas().containsKey(handler.getIdArea().get(area))) {
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
				if(handler.setAreaSprinklerStatus(area, status) < 0)
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
		
		System.out.println("Available Areas with Lights: ");
		handler.lightAreasList();
		
		System.out.println("\nType the area of the lights you want to switch");
		
		try {
			String area = reader.readLine();
			boolean valid = true;
			
			if(!handler.getAreas().containsKey(handler.getIdArea().get(area))) {
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
				if(handler.setAreaLightStatus(area, status) < 0)
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
		
		System.out.println("Available Devices: ");
		handler.devicesList();
		
		System.out.println("\nType the ID of the device");
		
		try {
			String id = reader.readLine();
			
			int deviceID = 0;
			try{
				deviceID = Integer.parseInt(id);
			} catch(NumberFormatException e) {
				System.out.println("The input must be a number\\n");
				return;
			}
		        
		    
			if(!handler.getDevice(deviceID)) {
				System.out.println("Error! This is not a device id.\n ");
				return;
			}
			
			System.out.println("Type the area");
		
			String area = reader.readLine().toLowerCase();
			handler.addDeviceArea(deviceID, area);
			
		} catch (IOException e) {
			e.printStackTrace();
		} 

		
	}
	
	//Remove the area a device belongs to
	private static void removeDeviceArea() {
		
		System.out.println("Available Devices: ");
		handler.devicesList();
		
		System.out.println("\nType the ID of the device");
	
		try {
			String id = reader.readLine();
			
			int deviceID = 0;
			try{
				deviceID = Integer.parseInt(id);
			} catch(NumberFormatException e) {
				System.out.println("The input must be a number\\n");
				return;
			}
			if(!handler.getDevice(deviceID)) {
				System.out.println("Error! This is not a device id.\n ");
				return;
			}
			
			handler.removeDeviceArea(deviceID);
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	private static void switchAreaMode() {
		
		if(showAreasInfo()) {
			try {
				System.out.print("Type the area where management mode will be changed: ");
				
			
				String area = reader.readLine();
				
				if(handler.getIdArea().get(area) == null) {
					System.out.println("Error! Area Id present in the system");
					return;
				}
				
				System.out.println("Type the new management mode (Auto[1] / Manual[0]):");
			
				String auto = reader.readLine();
				
				
				if(auto.compareTo("0") == 0)
					handler.getIdArea().get(area).setAutoManage(false);
				else if(auto.compareTo("1") == 0)
					handler.getIdArea().get(area).setAutoManage(true);
				else {
					System.out.println("Error: value not valid\n");
					return;
				}
		
					
			} catch(IOException e) {
				
				e.printStackTrace();
			}
		}else {
			return;
		}
			
		System.out.println("");
	}
	
	private static void editAreaThreshold() {
		
		if(showAreasInfo()) {
			
			try {
				
				System.out.print("Type the area where management mode will be changed: ");
				
				
				String area = reader.readLine();
				
				if(handler.getIdArea().get(area) == null) {
					System.out.println("Error! Area Id present in the system");
					return;
				}
				
				Area area_obj = handler.getIdArea().get(area);
				
				System.out.print("Insert max temperature tolerated in this area: ");
				int max_t = 0;
				try{
					max_t = Integer.parseInt(reader.readLine());
				} catch(NumberFormatException e) {
					System.out.println("The input must be a number\\n");
					return;
				}
			        
				
				System.out.print("Insert min temperature tolerated in this area: ");
				int min_t  = 0;
				try{
					min_t = Integer.parseInt(reader.readLine());
				} catch(NumberFormatException e) {
					System.out.println("The input must be a number\\n");
					return;
				}
			        
			  
				System.out.print("Insert max humidity tolerated in this area: ");
				int max_h = 0;
				try{
					max_h = Integer.parseInt(reader.readLine());
				} catch(NumberFormatException e) {
					System.out.println("The input must be a number\\n");
					return;
				}
			        
			   
				System.out.print("Insert min humidity tolerated in this area: ");
				int min_h = 0;
				try{
					min_h = Integer.parseInt(reader.readLine());
				}catch(NumberFormatException e) {
					System.out.println("The input must be a number\\n");
					return;
				}
			        
			    
				if((max_t < min_t) || (max_h < min_h)) {
					System.out.println("Error in typing parameters. No modification done\n");
					return;
				}
				
				area_obj.setMaxTemp(max_t);
				area_obj.setMinTemp(min_t);
				area_obj.setMaxHum(max_h);
				area_obj.setMinHum(min_h);
				
				System.out.println("Modifications done");
				
				
			}catch(IOException e) {
				
				e.printStackTrace();
			}
			
			
		}else
			return;
		
		System.out.println("");
	}
	
	
	
	
	
	
	
}
