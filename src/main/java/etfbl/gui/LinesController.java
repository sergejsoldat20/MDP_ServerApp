package etfbl.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


import etfbl.mdp.rest.Timetable;
import etfbl.mdp.rest.TimetableData;
import etfbl.mdp.rmi.server.RMIService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LinesController {
	
	@FXML
	public Button newLine;
	
	@FXML
	public Button notificationButton;
	
	@FXML
	public Button allLines;
	
	@FXML
	public TextArea lines;
	
	@FXML
	public TextField  plannedArrivalTime;
	
	@FXML
	public TextField stations;
	
	@FXML
	public TextField deleteID;
	
	@FXML
	public Button deleteAllButton;
	
	@FXML
	public Button addReportsButton;
	
	@FXML
	public Button deleteLine;
	
	@FXML
	public ComboBox reports = new ComboBox();
	
	@FXML
	public Button downloadReportsButton;
	
	@FXML
	public TextArea reportsInfoArea;
	
	@FXML
	void initialize() {
	
	}
	
	@FXML
	public void addNewLine(MouseEvent event) {
		ArrayList<String> stationsList = new ArrayList<>();
		ArrayList<LocalTime> arrivalTime = new ArrayList<>();
		ArrayList<Boolean> onStation = new ArrayList<>();
		ArrayList<LocalTime> realArrivalTime = new ArrayList<>();
		String[] splitedStations = stations.getText().split(" ");
		String[] splitedTime = plannedArrivalTime.getText().split(" ");
		
		for(String s : splitedStations) {
			stationsList.add(s);
		}
		for(String s : splitedTime) {
			LocalTime time = TimetableData.stringToTime(s);
			arrivalTime.add(time);
		}
		if(stationsList.size() == arrivalTime.size()) {
			for(int i = 0; i < stationsList.size(); i++) {
				onStation.add(false);
			}
			int id = new Random().nextInt(1000);
			Timetable newTT = new Timetable(id, stationsList, arrivalTime, realArrivalTime, onStation);
			System.out.println(newTT);
			TimetableData.addTimetable(newTT);
		}
	}
	
	@FXML
	public void showAllLines(MouseEvent event) {
		ArrayList<String> linesList = TimetableData.getAllLinesToString();
		String text = "";
		for(String s : linesList) {
			text += s + "\n";
		}
		lines.setText(text);
	}
	
	@FXML
	public void deleteLineByID(MouseEvent event) {
		int lineID = Integer.parseInt(deleteID.getText());
		System.out.println(lineID);
		deleteOneLineByID(lineID);
	}
	
	public static void showLinesTest() {
		ArrayList<String> linesList = TimetableData.getAllLinesToString();
		for(String s : linesList) {
			System.out.println(s);
		}
	}
	
	@FXML
	public void deleteAll(MouseEvent event) {
		ArrayList<String> keys = TimetableData.getAllKeys();
		for(String key : keys) {
			TimetableData.delete(key);
		}
	}
	
	public static void deleteOneLineByID(int id) {
		ArrayList<String> keys = TimetableData.getAllKeys();
		for(String s : keys) {
			Timetable t = TimetableData.getTimetableByKey(s);
			if(t.getLineID() == id) {
				System.out.println("aaaaa");
				TimetableData.delete(s);
			}
		}
	}
	
	@FXML
	public void downloadReport(MouseEvent event) {
		String filename = reports.getValue().toString();
		//System.out.println(filename);
		if(filename != null) {
			File file = new File("downloads" + File.separator + filename);
			if(!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				FileOutputStream out = new FileOutputStream(file);
				byte[] data = RMIService.downloadReport(filename);
				out.write(data);
				out.flush();
				out.close();
 			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@FXML
	public void addReportsToComboBox() {
		ArrayList<String> optionsList = RMIService.getUploadedReports();
		ObservableList<String> options = 
			    FXCollections.observableArrayList(
			        optionsList
			    );
		reports.setItems(options);
	}
	
	@FXML
	public void setReportsInfo() {
	
		String text = "";
		for(String s : RMIService.getReportsInfo()) {
			text += s + "\n";
		}
		reportsInfoArea.setText(text);
		//System.out.println(text);
	}
	
	
	 
	
	
	public static void main(String args[]) {
		//showLinesTest();
		System.out.println("==============");
		ArrayList<String> st  = new ArrayList<String>();
		st.add("D");
		st.add("E");
		ArrayList<LocalTime> time = new ArrayList<LocalTime>();
		
		time.add(LocalTime.of(4, 14));
		time.add(LocalTime.of(4, 14));
		ArrayList<LocalTime> reTime = new ArrayList<>();
		ArrayList<Boolean> onSt = new ArrayList<>();
		onSt.add(false);
		onSt.add(false);
		
		//deleteAllLines();
		Timetable t = new Timetable(22, st, time, reTime, onSt);
		Timetable t1 = new Timetable(66, st, time, reTime, onSt);
		Timetable t2 = new Timetable(77, st, time, reTime, onSt);
		Timetable t3 = new Timetable(27, st, time, reTime, onSt);
		Timetable t4 = new Timetable(12, st, time, reTime, onSt);
		//System.out.println(t);
		//timetableData.addTimetable(t);
		//timetableData.addTimetable(t1);
		///timetableData.addTimetable(t2);
		//timetableData.addTimetable(t3);
		///timetableData.addTimetable(t4);
		////showLinesTest();
		System.out.println("==============");
		//timetableData.deleteLineByID(27);
		//timetableData.deleteLineByID(77);
		//timetableData.deleteLineByID(12);
		////showLinesTest();
		
		//deleteLineByIDTEST(22);
		//deleteLineByIDTEST(66);
		
		//showLinesTest();

	}
}
