package etfbl.soap;

import etfbl.mdp.model.*;
public class SOAPService {

	public User loginUser(String username, String password) {
		return User.login(username, password);
	}
	
	public boolean logout(String username) {
		return User.logout(username);
	}
	
	public String[] loggedUsers() {
		return User.listLoggedUsers();
	}
	
}
