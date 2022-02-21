package etfbl.mdp.model;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.*;
import org.xml.sax.SAXException;



import javax.xml.parsers.DocumentBuilder;
public class User implements Serializable{
	public static ArrayList<User> loggedUsers = new ArrayList<>();
	private static File usersPath = new File("C:\\Users\\Korisnik\\eclipse-workspace\\CentralApplication\\userList.xml");
	private String username;
	private String password;
	private RailwayStation railwayStation;
	private boolean online = false;
	
	public User() {
		super();
	}
	
	public User(String username, String password, RailwayStation railwayStation) {
		this.username = username;
		this.railwayStation = railwayStation;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}
	
	public boolean isOnline() {
		return online;
	}
	
	public void setIsOnline(boolean online) {
		this.online = online;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public RailwayStation getRailwayStation() {
		return railwayStation;
	}

	public void setRailwayStation(RailwayStation railwayStation) {
		this.railwayStation = railwayStation;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", railwayStation=" + railwayStation + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(password, railwayStation, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(password, other.password) 
				&& Objects.equals(username, other.username);
	}
    
	private static String hashPassword(String passwd) {
		String passwordHash = "unknownpassword";
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(passwd.getBytes());
			passwordHash = new String(messageDigest.digest());
			
			passwordHash = Base64.getEncoder().encodeToString(passwordHash.getBytes());
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return passwordHash;
	}
	
	public static boolean logout(String username) {
		Iterator<User> iterator = loggedUsers.iterator();
		while(iterator.hasNext()) {
			User u = iterator.next();
			if(u.username.equals(username)) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}
	
	
	//dodaj novog korisnika u xml fajl
	public static boolean addNewUserToXMLFile(User newUser) {
		if(checkIfDataExists(newUser.getUsername())) {
			return false;
		}
		
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(usersPath);
			
			Element root = document.getDocumentElement();
			Element user = document.createElement("user");
			//dodaj username 
			Element username = document.createElement("username");
		    username.appendChild(document.createTextNode(newUser.getUsername()));
		    user.appendChild(username);
		    // dodaj password
			Element password = document.createElement("password");
			password.appendChild(document.createTextNode(hashPassword(newUser.getPassword())));
			user.appendChild(password);
			//dodaj stanicu
			Element location = document.createElement("location");
			location.appendChild(document.createTextNode(newUser.getRailwayStation().getLocation()));
			Element railwayStation = document.createElement("railwayStation");
			railwayStation.appendChild(location);
			user.appendChild(railwayStation);
			
			root.appendChild(user);
			DOMSource source = new DOMSource(document);

		    TransformerFactory transformerFactory = TransformerFactory.newInstance();
		    Transformer transformer = transformerFactory.newTransformer();
		    StreamResult result = new StreamResult(usersPath);
		    transformer.transform(source, result);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public static User login(String username, String password) {
		//ako je user ulogovan ne moze se opet ulogovati
		for(User u : loggedUsers) {
			if(username.equals(u.username))
				return null;
		}
		//provjeri da li postoji  user i uloguj 
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(usersPath);
			NodeList nodeList = document.getElementsByTagName("user");
			for (int i = 0; i < nodeList.getLength() ; i++) {
	            Node nNode = nodeList.item(i);
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {     

	            	Element element = (Element) nNode;
	            	String usernameFromXML = element.getElementsByTagName("username").item(0).getTextContent();
	            	String passwordFromXML = element.getElementsByTagName("password").item(0).getTextContent();
	            	if(usernameFromXML.equals(username)) {
	            		if(passwordFromXML.equals(hashPassword(password))) {
	            			NodeList nList = element.getElementsByTagName("railwayStation");
	            			Element railwayNode = (Element)nList.item(0);
	            			String userLocation = railwayNode.getChildNodes().item(0).getTextContent();
	            			User user = new User(username, password, new RailwayStation(userLocation));
	            			loggedUsers.add(user);
	            			return user;
	            		}
	            	}
	            }
	        }
		}
		catch(Exception ex) {
			System.out.println(ex.toString());
		}
		return null;
	}
	
	public static boolean checkIfDataExists(String username) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(usersPath);
			NodeList nodeList = document.getElementsByTagName("user");
			for (int i = 0; i < nodeList.getLength() ; i++) {
	            Node nNode = nodeList.item(i);
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {     
	            	Element element = (Element) nNode;
	            	String usernameFromXML = element.getElementsByTagName("username").item(0).getTextContent();
	            	if(usernameFromXML.equals(username)) {
	            		return true;
	            	}
	            }
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	public static ArrayList<String> getAllUsers(){
		
		ArrayList<String> allUsers = new ArrayList<>();
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(usersPath);
			NodeList nodeList = document.getElementsByTagName("user");
			for (int i = 0; i < nodeList.getLength() ; i++) {
	            Node nNode = nodeList.item(i);
	            Element element = (Element)nNode;
	            String username = element.getElementsByTagName("username").item(0).getTextContent();
	           // System.out.println(username);
	            allUsers.add(username);
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return allUsers;
	}
	
	public static void deleteUser(String username) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();  
			Document document = db.parse(usersPath);

	        XPathFactory xpf = XPathFactory.newInstance();
	        XPath xpath = xpf.newXPath();
	        XPathExpression expression = xpath.compile("/root/user[username='"+ username + "']");
	
	        Node b13Node = (Node) expression.evaluate(document, XPathConstants.NODE);
	        b13Node.getParentNode().removeChild(b13Node);
	
	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer t = tf.newTransformer();
	        t.transform(new DOMSource(document), new StreamResult(usersPath));
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static String[] listLoggedUsers() {
		String[] users = new String[loggedUsers.size()];
		for(int i = 0; i < loggedUsers.size(); i++) {
			users[i] = loggedUsers.get(i).getUsername() + "#" + loggedUsers.get(i).getRailwayStation().getLocation();
			//System.out.println(users[i]);
		}
		return users;
	}
 	
	
	public static void main(String args[]) {
		addNewUserToXMLFile(new User("Sergej", "sifra", new RailwayStation("A")));
		addNewUserToXMLFile(new User("Aleksa", "sifra", new RailwayStation("A")));
		addNewUserToXMLFile(new User("Dragan", "sifra", new RailwayStation("A")));
		addNewUserToXMLFile(new User("Milan", "sifra", new RailwayStation("A")));
		
		
		//System.out.println(login("Sergej", "sifra"));
		login("Aleksa", "sifra");
		login("Dragan", "sifra");
		
		
		System.out.println("================");
		for(String s : listLoggedUsers()) {
			System.out.println(s);
		}
		
		
		System.out.println("================");
		
		for(User u : loggedUsers) {
			System.out.println(u.username);
		}
	}
}
