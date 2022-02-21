package etfbl.mdp.model;

public class RailwayStation {

	private String location;
	
	public RailwayStation() {
		
	} 
	
    public RailwayStation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
    
}
