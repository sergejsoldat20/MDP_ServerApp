package etfbl.mdp.chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.ls.LSOutput;


public class ChatThread extends Thread{
	private ChatServer chatServer;
	private String username;
	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;
	private Socket socket;
	public boolean running = true;
	public ChatThread() {
		super();
	}
	
	public ChatThread(ChatServer chatServer,Socket socket) {
		super();
		this.chatServer = chatServer;
		this.socket = socket;
		try {
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			objectInputStream = new ObjectInputStream(socket.getInputStream());
			this.username = (String) objectInputStream.readObject();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}
	
	public String getUsername() {
		return username;
	}
	
	public void run() {
		while(running) {
			try {
				if(objectInputStream != null ) {
					String messageString = (String)objectInputStream.readObject();
					String [] messagePart = messageString.split("#");
					processMessage(messagePart);
				}
				
			} catch (ClassNotFoundException e) {
				
			} catch (IOException e) {
				
			}
		}
		chatServer.remove(this);
		close();
	}
	
	public boolean writeMessage(String message) {
		if(!socket.isConnected()) {
			close();
			return false;
		}
		try {
			objectOutputStream.writeObject(message);
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean processMessage(String[] messageParts) {
		if(messageParts.length < 4 && messageParts.length > 5) {
			return false;
		}
		
		switch (messageParts[0]) {
			case "TEXT":
			{
				
				if(messageParts.length != 4) {
				
					return false;
				}
				
				String senderString = messageParts[1];
				String receiverString = messageParts[2];
				String contentString = messageParts[3];
				
				boolean isSent = chatServer.sendMessage("UNICAST",this.username ,receiverString,"MESSAGE_RESPONSE" + 
				"#" + senderString + 
				"#" + contentString);
				/*if(isSent) {
					//System.out.println("Poruka je poslana");
				}else {
					//System.out.println("Poruka nije poslana");
					writeMessage("ERROR, username of receiver is wrong");
				}*/
				
				break;
			}
			case "LOGIN": {
				chatServer.sendMessage("BROADCAST", this.username, null, "LOGIN_RESPONSE" + "#" + username);
				break;
			}
			case "LOGOUT" : {
				chatServer.sendMessage("BROADCAST",this.username, null, "LOGOUT_RESPONSE" + "#" + username);
				chatServer.remove(this);
				running = false;
				
			}
		}
		
		return true;
	}
	
	
	public void close() {
		try {
			if(objectOutputStream != null) {
				objectOutputStream.flush();
				objectOutputStream.close();
			}
			if(objectInputStream != null) {
				objectInputStream.close();
			}
			if(socket != null) {
				socket.close();
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
