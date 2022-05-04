package it.unipi.iot.resource_devices;

public class Area {
	
	protected String id;
	protected int maxTemp = 0;
	protected int minTemp = 0;
	protected int maxHum = 0;
	protected int minHum = 0;
	protected boolean autoManage = false;
	
	

	public Area(String id, int maxTemp, int minTemp, int maxHum, int minHum) {
		this.id = id;
		this.maxTemp = maxTemp;
		this.minTemp = minTemp;
		this.maxHum = maxHum;
		this.minHum = minHum;
		System.out.println("New area " + id + " has been created");
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
	
	public boolean isAutoManage() {
		return autoManage;
	}

	public void setAutoManage(boolean autoManage) {
		this.autoManage = autoManage;
	}
	
	public void printAreaInfo() {
		System.out.print("Area: " + this.getId() + ", AutoMode: " + this.isAutoManage() + 
				", Min Temp: " + this.getMinTemp() + ", Max Temp: " + this.getMaxTemp() + 
				", Min Hum: " + this.getMinHum() + ", Max Hum: " + this.getMaxHum());
	}
	
	
	

}
