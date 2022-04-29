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
						
					case "!getSensors":
						getSensors();
						
					case "!getActuators":
						getActuators();
						
					case "!getAvgTemperature":
						getAvgTemperature();
					
					case "!getAvgHumidity":
						getAvgHumidity();
						
					case "!getSprinklerStatus":
						getSprinklerStatus();
						
					case "getLightStatus":
						getLightStatus();
						
					default:
						System.out.println("Command not defined\n");
				}
				
				
				
				
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		
		
		
	}
	
	private static void showCommands() {
		
		System.out.println("	--	This is the list of accepted command	--	\n");
		System.out.println("!help 	-->	Get the list of the available commands");
		System.out.println("!getSensors 	--> Get the list of registered sensors");
		System.out.println("!getActuators 	--> Get the list of registered actuators");
		System.out.println("!getAvgTemperature		-->	Get the Average temperature of the last 10 measurements for all the sensors");
		System.out.println("!getAvgHumidity		-->	Get the Average humidity of the last 10 measurements for all the sensors");
		System.out.println("!getSprinklerStatus		-->	Get the status of a sprinkler");
		System.out.println("!getLightStatus		-->	Get the status of a light");

	}
	
	private static void getSensors() {
		
		if(handler.getHumiditySensors().isEmpty() && handler.getTempSensors().isEmpty()) {
			System.out.println("No Sensor Registered");
			return;
		}
	
		System.out.println(" 	--	Sensors List	--	");
		handler.getHumiditySensors();
		handler.getTempSensors();
	}
	
	private static void getActuators() {
		
		if(handler.getSprinklers().isEmpty() && handler.getLights().isEmpty()) {
			System.out.println("No Actuator Registered");
			return;
		}
		
		System.out.println(" 	--	Actuators List	--	");
		handler.getSprinklers();
		handler.getLights();
	}
	
	private static void getAvgTemperature() {
		
		System.out.println("	--	Last Average Temperatures Detected	--	");
		handler.getSensorsTemperatures();
	}
	
	private static void getAvgHumidity() {
		
		System.out.println("	--	Last Average Humidities Detected	--	");
		handler.getSensorsHumidities();
	}
	
	private static void getSprinklerStatus() {
		System.out.println("	--	Get Sprinklers Status	--	");
		handler.getSprinklersStatus();;
	}
	
	private static void getLightStatus() {
		System.out.println("	--	Get Lights Status	--	");
		handler.getLightsStatus();;
	}
	
	
	
}
