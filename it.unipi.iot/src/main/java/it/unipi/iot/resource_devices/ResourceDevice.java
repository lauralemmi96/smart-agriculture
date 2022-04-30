package it.unipi.iot.resource_devices;

import org.eclipse.californium.core.CoapClient;

public class ResourceDevice {
	
	protected String hostAddress;
	protected String deviceType;
	protected String resourceType;
	protected CoapClient client;
	protected boolean observable;
	
	public ResourceDevice(String hostAddress, String deviceType, String resourceType, boolean observable) {
		
		this.hostAddress = hostAddress;
		this.deviceType = deviceType;
		this.resourceType = resourceType;
		this.observable = observable;
		//System.out.println("coap://[" + this.hostAddress + "]:5683/"+ this.resourceType);
		this.client = new CoapClient("coap://[" + this.hostAddress + "]:5683/"+ this.resourceType);
	}

	public String getDeviceType() {
		return deviceType;
	}

	public String getResourceType() {
		return resourceType;
	}

	public CoapClient getClient() {
		return client;
	}

	public boolean isObservable() {
		return observable;
	}
	
	

}
