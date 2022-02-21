package etfbl.gui;

import java.io.IOException;
import java.util.ArrayList;


import etfbl.mdp.model.RailwayStation;
import etfbl.mdp.model.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {
	
	@FXML
	public Button addUser;
	
	@FXML
	public Button lines;
	
	@FXML
	public Button allUsers;
	
	@FXML
	public TextField newStation;
	
	@FXML
	public TextField newPassword;
	
	@FXML
	public TextField newUsername;
	
	@FXML
	public TextField deleteUsername;
	
	@FXML
	public Button deleteUser;
	
	@FXML
	public TextArea usersList;
	
	@FXML
	public void initialize() {
		Main.mainController = this;
	}
	
	@FXML
	public void addNewUser(MouseEvent event) {
		String username = newUsername.getText();
		String password = newPassword.getText();
		String station = newStation.getText();
		if(username != null && password != null && station != null) {
			User newUser = new User(username, password, new RailwayStation(station));
			User.addNewUserToXMLFile(newUser);
			User.getAllUsers();
		}
		
		ArrayList<String> users = User.getAllUsers();
		
		for(String u : users) {
			System.out.println(u);
		}
	}
	
	@FXML
	public void deleteUserAction(MouseEvent event) {
		String username = deleteUsername.getText();
		if(username != null) {
			User.deleteUser(username);
		}
		
		ArrayList<String> users = User.getAllUsers();
		
		for(String u : users) {
			System.out.println(u);
		}
	}
	
	@FXML
	public void showAllUsers(MouseEvent event) {
		//allUsers.setDisable(true);
		ArrayList<String> users = User.getAllUsers();
		String text = "";
		for(String s : users) {
			text += s + "\n";
		}
		usersList.setText(text);
	}
	
	@FXML
	public void showNewScene(MouseEvent event) {
		try {
			 Stage noviProzor = new Stage();
	         noviProzor.initModality(Modality.APPLICATION_MODAL);
	         FXMLLoader loader = new FXMLLoader(getClass().getResource("LinesPageView.fxml"));
	         LinesController mainPageController = new LinesController();
	         Parent root = loader.load();
	         Scene scene = new Scene(root);
	         noviProzor.setScene(scene);
	         noviProzor.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	 public void showNotification(String content, String sender) {
	    	FXMLLoader loader = new FXMLLoader(getClass().getResource("NotificationsView.fxml"));	
			Parent rootParent;
			try {
				rootParent = loader.load();
				NotificationsController notificationsController = loader.getController();
				
				Platform.runLater(() -> {
					notificationsController.setContent(content);
					notificationsController.setSender(sender);
					Scene scene = new Scene(rootParent);
					Stage stage = new Stage();
					stage.setScene(scene);
					stage.show();
				});
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
	    }
}
