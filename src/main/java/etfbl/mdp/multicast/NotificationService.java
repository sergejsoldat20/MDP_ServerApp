package etfbl.mdp.multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;



public class NotificationService {
	
	public static final int PORT = 20000;
	public static final String ADDRESS = "224.0.0.11";

	public static MulticastSocket socket = null;
	public static boolean receive = true;

	public NotificationService() {
        try {
            socket = new MulticastSocket(PORT);
            InetAddress address = InetAddress.getByName(ADDRESS);
            socket.joinGroup(address);
            NotificationThread multicastThread = new NotificationThread(socket);
            multicastThread.start();
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
    }
}
