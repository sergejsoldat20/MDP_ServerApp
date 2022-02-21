package etfbl.mdp.api;

import java.util.ArrayList;

import etfbl.mdp.rest.Line;
import etfbl.mdp.rest.Timetable;
import etfbl.mdp.rest.TimetableData;
import etfbl.gui.*;
public class TimetableService {
	
	TimetableData td = new TimetableData();
	
	public ArrayList<Line> getAllLinesForStation(String station) {
		ArrayList<Timetable> lines = TimetableData.allLinesForStation(station);
		ArrayList<Line> result = new ArrayList<>();
		for(Timetable t : lines) {
			result.add(new Line(t.toString()));
		}
		return result;
	}
	
	public boolean updateArrivalTime(int id, String station) {
		if(TimetableData.getLineByID(id) != null) {
			Timetable t = TimetableData.getLineByID(id);
			if(station.equals(t.getNextStation())) {
				TimetableData.updateRealArrivalTime(t);
				return true;
			}
		}
		return false;
	}
	
	public boolean deleteLine(int id) {
		if(TimetableData.getLineByID(id) != null) {
			TimetableData.deleteLineByID(id);
			return true;
		}
		return false;
	}
	
	public static void main(String args[]){
		System.out.println(TimetableData.getLineByID(171));
		//updateArrivalTime(171);
		System.out.println(TimetableData.getLineByID(171));
		
		//updateArrivalTime(171);
		System.out.println(TimetableData.getLineByID(171));
		
		Timetable t = TimetableData.getLineByID(714);
		System.out.println(t);
		System.out.println(t.getNextStation());
		
		
		TimetableData.updateRealArrivalTime(t);
		System.out.println("===============================");
		System.out.println(t);
		System.out.println(t.getNextStation());
		
		
	}	
}
