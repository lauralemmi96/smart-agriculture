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
		
		System.out.println("	--	This is the list of accepted command	--	\n");
		System.out.println("!help 			-->	Get the list of the available commands");
		/*
		System.out.println("!getSensors 		-->	Get the list of registered sensors");
		System.out.println("!getActuators 		-->	Get the list of registered actuators");	
		*/
		System.out.println("!getLastTemp		-->	Get the list of the last temp measurements");
		System.out.println("!getLastHum		-->	Get the list of the last humidity measurements");
		System.out.println("!getAvgTemperature	-->	Get the Avg temperature of the last 10 measurements for all the sensors");
		System.out.println("!getAvgHumidity		-->	Get the Avg humidity of the last 10 measurements for all the sensors");
		System.out.println("!getSprinklerStatus	-->	Get the status of the sprinklers");
		System.out.println("!getLightStatus		-->	Get the status of the lights");
		System.out.println("");

	}
	
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
	
	private static void getLastTemp() {
		System.out.println("	--	Last Temperatures Detected	--	");
		handler.getLastSensorsTemperatures();
		System.out.println("");
	}
	
	private static void getLastHum() {
		System.out.println("	--	Last Humidities Detected	--	");
		handler.getLastSensorsHumidities();
		System.out.println("");
	}
	
	private static void getAvgTemperature() {
		
		System.out.println("	--	Last Average Temperatures Detected	--	");
		handler.getSensorsTemperatures();
		System.out.println("");
	}
	
	private static void getAvgHumidity() {
		
		System.out.println("	--	Last Average Humidities Detected	--	");
		handler.getSensorsHumidities();
		System.out.println("");
	}
	
	private static void getSprinklerStatus() {
		System.out.println("	--	Get Sprinklers Status	--	");
		handler.getSprinklersStatus();
		System.out.println("");
	}
	
	private static void getLightStatus() {
		System.out.println("	--	Get Lights Status	--	");
		handler.getLightsStatus();
		System.out.println("");
	}
	
	
	
}
