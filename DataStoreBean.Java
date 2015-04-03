package com.router;

/**
 * Datastructure to store instance of processing object
 * @author rskroy
 *
 */
public class DataStoreBean {
	private String conveyorSystem;
	private String departures;
	private String bags;
	private Integer routingType; // TODO: Might have used a constant/Enum
	
	public Integer getRoutingType() {
		return routingType;
	}

	public void setRoutingType(Integer routingType) {
		this.routingType = routingType;
	}

	public DataStoreBean(String conveyorSystem, String departures, String bags, Integer routingType){
		this.conveyorSystem = conveyorSystem;
		this.departures = departures;
		this.bags = bags;
		this.routingType = routingType;
	}
	
	public String getConveyorSystem() {
		return conveyorSystem;
	}
	public void setConveyorSystem(String conveyorSystem) {
		this.conveyorSystem = conveyorSystem;
	}
	public String getDepartures() {
		return departures;
	}
	public void setDepartures(String departures) {
		this.departures = departures;
	}
	public String getBags() {
		return bags;
	}
	public void setBags(String bags) {
		this.bags = bags;
	}
	
	public boolean equals(Object obj) {
		if (! (obj instanceof DataStoreBean)) return false;
		DataStoreBean other = (DataStoreBean) obj;
		return conveyorSystem.equals(other.conveyorSystem) && departures.equals(other.departures) &&
				bags.equals(other.bags);
	}
}
