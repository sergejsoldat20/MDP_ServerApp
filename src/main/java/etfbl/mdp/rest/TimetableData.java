package etfbl.mdp.rest;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
public class TimetableData {
	
	
	public static final String instanceName  = "Raspored voznje";
	public static int timetableIDCounter = 0;
	public ArrayList<Timetable> lines = new ArrayList<Timetable>();
	public ArrayList<String> linesString = new ArrayList<String>();
	
	
	
	public TimetableData() {
		
		//lines.add(parseStringToTimetable("LineID - 88 - D - 01:55 - 01:33 - Train is on station - F - 01:01 - NO TIME - Train is not on station - "));
		//lines.add(parseStringToTimetable("LineID - 100 - E - 01:55 - 01:33 - Train is on station - F - 01:01 - NO TIME - Train is not on station - "));
	}
	
	//funkcija vraca sve linije u bazi
	public static ArrayList<Timetable> getAllLines(){
		ArrayList<Timetable> result = new ArrayList<>();
		ArrayList<String> keys = getAllKeys();
		for(String key : keys) {
			System.out.println(getTimetableByKey(key));
			result.add(getTimetableByKey(key));
		}
		return result;
	}
	
	//brise samo jednu liniju po kljucu iz baze
	public static void delete(String key) {
		JedisPool pool = new JedisPool("localhost");
		try (Jedis jedis = pool.getResource()){
			jedis.del(key);
		}
		pool.close();
	}
	
	//ZAVRSENA FUNKCIJA - dodaje novu liniju u bazu
	public static void addTimetable(Timetable timetable) {
		JedisPool pool = new JedisPool("localhost");
		try (Jedis jedis = pool.getResource()){
			jedis.set(instanceName, "OK");
			int lineID = ++timetableIDCounter;
			jedis.hmset(instanceName + ":lines:map:" + Integer.toString(lineID), timetable.toMap());
		}
		pool.close();
	}
	
	//GOTOVA FUNKCIJA - vraca liniju iz baze po kljucu
	public static Timetable getTimetableByKey(String key) {
		JedisPool pool = new JedisPool("localhost");
		try (Jedis jedis = pool.getResource()){
			Map<String, String> fields = jedis.hgetAll(key);
			//System.out.println(fields.values());
			if(fields.values().toString() != null) {
				return parseStringToTimetable(fields.values().toString());
						
			}
			
		}
		pool.close();
		return null;
	}
	
	//gotova funkcija - prebacuje string u vrijeme
	public static LocalTime stringToTime(String time) {
		String[] parsedTime = time.split(":");
		return LocalTime.of(Integer.parseInt(parsedTime[0]), Integer.parseInt(parsedTime[1]));
	}
	
	//gotova funkcija - azuriranje vremena
	public static void updateRealArrivalTime(Timetable line) {
		if(line.getStations().size() == line.getRealArrivalTime().size()) {
			return;
		}
		LocalTime temp = LocalTime.now();
		String timeString = temp.toString();
		LocalTime newTime = LocalTime.of(Integer.parseInt(timeString.split(":")[0]), Integer.parseInt(timeString.split(":")[1]));
		line.getRealArrivalTime().add(newTime);
		int id = line.getLineID();
		deleteLineByID(id);
		addTimetable(line);
	}
	
	//gotova funkcija - prebacuje string u objekat timetable
	public static Timetable parseStringToTimetable(String string) {
		ArrayList<String> stations = new ArrayList<String>();
		ArrayList<LocalTime> arrivalTime = new ArrayList<>();
		ArrayList<LocalTime>  realArrivalTime = new ArrayList<>();
		ArrayList<Boolean> trainOnStation = new ArrayList<>();
		try {
			String [] parsed = string.split(" - ");
			int lineID = Integer.parseInt(parsed[1]);
			for(int i = 2; i < parsed.length - 1 ; i+=4) {
				stations.add(parsed[i]);
				String firstTime = parsed[i+1];
				String secondTime = parsed[i+2];
				String onStation = parsed[i+3];
				arrivalTime.add(stringToTime(firstTime));
				if(!"NO TIME".equals(secondTime)) {
					realArrivalTime.add(stringToTime(secondTime));
				}
				if("Train is on station".equals(onStation)) {
					trainOnStation.add(true);
				} else {
					trainOnStation.add(false);
				}
				
			}
			return new Timetable(lineID, stations, arrivalTime, realArrivalTime, trainOnStation);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	//GOTOVA FUNKCIJA - vraca sve kljuceve
	public static ArrayList<String> getAllKeys() {
		JedisPool pool = new JedisPool("localhost");
		List<String> keys = new ArrayList<String>();
		try (Jedis jedis = pool.getResource()){
			ScanParams sp = new ScanParams();
			String cursor = "0";
			int counter = 0;
			sp.match(instanceName + ":lines:map:*");
			do {
				ScanResult<String> scanResult = jedis.scan(cursor, sp);
				List<String> tempKeys = scanResult.getResult();
				keys.addAll(tempKeys);
				cursor = scanResult.getStringCursor();
				
			}while(!"0".equals(cursor));
		}
		keys.remove(instanceName + ":lines:map:");
		pool.close();
		return new ArrayList<String>(keys);
	}
	
	//gotova funkcija - vraca sve linije za odredjenu stanicu
	public static ArrayList<Timetable> allLinesForStation(String station){
		ArrayList<Timetable> result = new ArrayList<>();
		ArrayList<String> keys = getAllKeys();
		keys.remove(instanceName + ":lines:map:");
		JedisPool jedisPool = new JedisPool("localhost");
		try (Jedis jedis = jedisPool.getResource()){
			for(String key : keys) {
				//System.out.println(station);
				Map<String, String> line = jedis.hgetAll(key);
				Timetable temp = parseStringToTimetable(line.values().toString());
				if(temp.getStations().contains(station)) {
					result.add(temp);
				}
			}
		}
		jedisPool.close();
		return result;
	}
	
	
	public static ArrayList<String> getAllLinesToString(){
		ArrayList<String> keys = getAllKeys();
		ArrayList<String> lines = new ArrayList<>();
		for(String key : keys) {
			lines.add(getTimetableByKey(key).toString());
		//	System.out.println(getTimetableByKey(key));
		}
		return lines;
	}
	
	public static void deleteLineByID(int id) {
		ArrayList<String> keys = getAllKeys();
		for(String key : keys) {
			Timetable t = getTimetableByKey(key);
			if(t.getLineID() == id) {
				System.out.println("brisanje");
				delete(key);
			}
		}
	}
	
	public static Timetable getLineByID(int id) {
		ArrayList<String> keys = getAllKeys();
		for(String key : keys) {
			Timetable t = getTimetableByKey(key);
			if(t.getLineID() == id) {
				return t;
			}
		}
		return null;
	} 
	
	public static void main(String args[]) {
		JedisPool pool = new JedisPool("localhost");
		ArrayList<String> stanice = new ArrayList<>();
		stanice.add("A");
		stanice.add("B");
		ArrayList<LocalTime> arrivalReal = new ArrayList<>();
		arrivalReal.add(LocalTime.of(1, 33));
		
		ArrayList<LocalTime> arrivalPlaned = new ArrayList<>();
		arrivalPlaned.add(LocalTime.of(1, 55));
		arrivalPlaned.add(LocalTime.of(1, 1));
		ArrayList<Boolean> onStation = new ArrayList<>();
		onStation.add(true);
		onStation.add(true);
		int id = 5;
		
		Timetable t = new Timetable(id, stanice ,arrivalPlaned, arrivalReal, onStation);
		Timetable t1 = parseStringToTimetable("LineID - 88 - D - 01:55 - 01:33 - Train is on station - F - 01:01 - NO TIME - Train is not on station - ");
		Timetable t2 = new Timetable(89, stanice ,arrivalPlaned, arrivalReal, onStation);
		//Timetable t4 = new Timetable(100, stanice ,arrivalPlaned, arrivalReal, onStation);
		addTimetable(t);
		addTimetable(t1);
		addTimetable(t2);
		//addTimetable(t4);
		
		
		
		//getAllLinesToString();
		//ArrayList<Timetable> result  = allLinesForStation("D");
		//Timetable t3 = getTimetableByKey(instanceName + ":lines:map:" + Integer.toString(1)); 
		
		//getTimetableByKey(instanceName + ":lines:map:" + Integer.toString(1)); 
		///getTimetableByKey(instanceName + ":lines:map:" + Integer.toString(3)); 
		
		System.out.println(t.getRealArrivalTime().get(0));
		System.out.println(t1.getRealArrivalTime().get(0));
		System.out.println(t2);
		
		
		
		//updateRealArrivalTime(t3);
		//System.out.println(t1);
		
		//ArrayList<String> keys = getAllKeys();
		
	
		/*for(String key : keys) {
			System.out.println(getTimetableByKey(key));
		}*/
			
		
		/*for(int i = 0; i < keys.size(); i++) {
			//Timetable t44 = getTimetableByKey(keys.get(i));
			//System.out.print(t44);
		}*/
		
		//System.out.println(getTimetableByKey(instanceName + ":lines:map:" + "1"));
		//System.out.println(getTimetableByKey(instanceName + ":lines:map:" + "2"));
		//System.out.println(getTimetableByKey(instanceName + ":lines:map:" + "2"));
		 
		//for(Timetable tt : result)
		////	System.out.println(tt);
		///TimetableData td = new TimetableData();
		
		//System.out.println(lines.get(0));
		//System.out.println(td.lines.get(1));
		//System.out.println(td.lines.get(0));
		
		
		
		
	}
	
}
