package etfbl.mdp.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import etfbl.gui.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class NotificationThread extends Thread {
	public static final String ADDRESS = "224.0.0.11";
	public MulticastSocket socket;
    
		
	public NotificationThread(MulticastSocket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
			byte buffer[] = new byte[256];
			InetAddress address;
			try {
				address = InetAddress.getByName(ADDRESS);
				while (true) {
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					socket.receive(packet);
			        String received = new String(packet.getData(), 0, packet.getLength());
			        String parsed[] = received.split("-");
			        	
			        String username = parsed[0];
			        String content = parsed[1];
				    FXMLLoader loader = new FXMLLoader(getClass().getResource("/etfbl/gui/NotificationsView.fxml"));	
				    Parent rootParent = loader.load();
				    NotificationsController notificationsController = loader.getController();
				    Platform.runLater(() -> Main.mainController.showNotification(content, username));
			            
			    } 
			} catch (Exception ex) {
				ex.printStackTrace();
			} 
	}
}
