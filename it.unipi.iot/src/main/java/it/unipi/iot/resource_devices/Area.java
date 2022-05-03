package it.unipi.iot.resource_devices;

public class Area {
	
	String id;
	int maxTemp = 0;
	int minTemp = 0;
	int maxHum = 0;
	int minHum = 0;
	
	public Area(String id, int maxTemp, int minTemp, int maxHum, int minHum) {
		this.id = id;
		this.maxTemp = maxTemp;
		this.minTemp = minTemp;
		this.maxHum = maxHum;
		this.minHum = minHum;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getMaxTemp() {
		return maxTemp;
	}

	public void setMaxTemp(int maxTemp) {
		this.maxTemp = maxTemp;
	}

	public int getMinTemp() {
		return minTemp;
	}

	public void setMinTemp(int minTemp) {
		this.minTemp = minTemp;
	}

	public int getMaxHum() {
		return maxHum;
	}

	public void setMaxHum(int maxHum) {
		this.maxHum = maxHum;
	}

	public int getMinHum() {
		return minHum;
	}

	public void setMinHum(int minHum) {
		this.minHum = minHum;
	}
	
	
	

}
