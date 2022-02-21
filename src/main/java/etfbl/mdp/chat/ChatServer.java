package etfbl.mdp.chat;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.omg.CORBA.PUBLIC_MEMBER;

public class ChatServer {
	
	private int port;
	private ArrayList<ChatThread> onlineUsers = new ArrayList<ChatThread>();

	
	public ChatServer(int port) {
		super();
		this.port = port;
	}
	
	public void startServer() {
		boolean runningServer = true;
		
		try {
			SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
			ServerSocket serverSocket = sslServerSocketFactory.createServerSocket(port);
			while(runningServer) {
				SSLSocket socket = (SSLSocket) serverSocket.accept();
				if(!runningServer) {
					break;
				}
				ChatThread chatThread = new ChatThread(this,socket);
				onlineUsers.add(chatThread);
				chatThread.start();
				
			}
			
			serverSocket.close();
			for(ChatThread chatThread : onlineUsers) {
				chatThread.close();
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
	}
	
	
	public synchronized boolean sendMessage(String typeOfMessage,String senderString,String receiverString, String messageString) {
		switch (typeOfMessage) {
		case "BROADCAST":{
		
			for(ChatThread chatThread : onlineUsers) {
				if(!chatThread.getUsername().equals(senderString)) {
					if(!chatThread.writeMessage(messageString)) {
						remove(chatThread);
					}
				}
			}
			return true;
		}
		case "UNICAST":{
			ChatThread receiverChatThread = getChatThread(receiverString);
			if(receiverChatThread == null) {
				return false;
			}
			if(!receiverChatThread.writeMessage(messageString)) {
				remove(receiverChatThread);
				return false;
			}
			return true;
		}
		default:
			return false;
		}
	}
	
	
	public ChatThread getChatThread(String usernameString) {
		for(ChatThread chatThread : onlineUsers) {
			if(chatThread.getUsername().equals(usernameString)) {
				return chatThread;
			}
		}
		return null;
	}
	synchronized void remove(ChatThread  chatThread) {
		System.out.println("Remove method");
		String disconnectedClient = "";
		for(int i = 0; i < onlineUsers.size(); ++i) {
			ChatThread ct = onlineUsers.get(i);
			if(ct.equals(chatThread)) {
				ct.close();
				disconnectedClient = ct.getUsername();
				onlineUsers.remove(i);
				break;
			}
		}
		sendMessage("BROADCAST",chatThread.getUsername(), null, chatThread.getUsername() + " je napustion chat");
	}
	
	public static void main(String args[]) {
		
		System.setProperty("javax.net.ssl.keyStore","keystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "securemdp");
		int port = 1500;
		ChatServer chatServer = new ChatServer(1500);
		chatServer.startServer();
	}
}
