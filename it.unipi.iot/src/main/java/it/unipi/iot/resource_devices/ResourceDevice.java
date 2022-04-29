package it.unipi.iot.resource_devices;

import org.eclipse.californium.core.CoapClient;

public class ResourceDevice {
	
	protected String hostAddress;
	protected String deviceType;
	protected String resourceType;
	protected CoapClient client;
	protected boolean observable;

}
