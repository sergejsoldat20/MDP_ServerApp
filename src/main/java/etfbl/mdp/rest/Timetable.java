package etfbl.mdp.rest;


import java.io.Serializable;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Timetable implements Serializable {
	
	public static final String timeFormatString = "HH:mm";
	public static final String onStationString = "Train is on station";
	public static final String notOnStationString = "Train is not on station";
	
	private int lineID;
	private ArrayList<String> stations = new ArrayList<>();
	private ArrayList<LocalTime> plannedArrivalTime = new ArrayList<>();
	private ArrayList<LocalTime> realArrivalTime = new ArrayList<>();
	private ArrayList<Boolean> trainOnStation = new ArrayList<>();
	
	public Timetable() {
		super();
	}
	
	public Timetable(int lineID, ArrayList<String> stations, ArrayList<LocalTime> plannedArrivalTime, 
			ArrayList<LocalTime> realArrivalTime, ArrayList<Boolean> trainOnStation) {
		this.lineID = lineID;
		this.stations = stations;
		this.plannedArrivalTime = plannedArrivalTime;
		this.realArrivalTime = realArrivalTime;
		this.trainOnStation = trainOnStation;
	}
	
	public String toString() {
		String result = "LineID - "+ lineID +  " - ";
		for(int i = 0; i < realArrivalTime.size(); i++) {
			result += stations.get(i) + " - " + plannedArrivalTime.get(i) +
					" - " + realArrivalTime.get(i) + " - " + onStationString + " - ";
		} 
		
		for(int i = realArrivalTime.size(); i < stations.size(); i++) {
			result += stations.get(i) + " - " + plannedArrivalTime.get(i) +
					" - " + "NO TIME" + " - " + notOnStationString +  " - ";
		}
		
		return result;
	}
	
	public ArrayList<String> getStations() {
		return stations;
	}

	public void setStations(ArrayList<String> stations) {
		this.stations = stations;
	}

	public ArrayList<LocalTime> getPlannedArrivalTime() {
		return plannedArrivalTime;
	}

	public void setPlannedArrivalTime(ArrayList<LocalTime> plannedArrivalTime) {
		this.plannedArrivalTime = plannedArrivalTime;
	}

	public ArrayList<LocalTime> getRealArrivalTime() {
		return realArrivalTime;
	}

	public void setRealArrivalTime(ArrayList<LocalTime> realArrivalTime) {
		this.realArrivalTime = realArrivalTime;
	}

	public ArrayList<Boolean> getTrainOnStation() {
		return trainOnStation;
	}

	public void setTrainOnStation(ArrayList<Boolean> trainOnStation) {
		this.trainOnStation = trainOnStation;
	}
	
	public int getLineID() {
		return lineID;
	}
	
	public String getNextStation() {
		if(stations.size() == realArrivalTime.size()) {
			return null;
		}
		return stations.get(realArrivalTime.size());
	}
	
	public HashMap<String, String> toMap(){
		HashMap<String, String> map = new HashMap<>();
		map.put("ID", Integer.toString(lineID));
		map.put("Line", this.toString());
		return map;
	}
	
	

	
}
