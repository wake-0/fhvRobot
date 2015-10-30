package communication;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import models.Client;

public class SessionManager {

	private static SessionManager instance;
	
	// fields
	private final int minSessionNumber = 0;
	private final int maxSessionNumber = 255;
	private HashMap<Client, Integer> clientSession;
	
	// private ctor
	private SessionManager() {
		clientSession = new HashMap<>();
	}
	
	// singleton
	public static SessionManager getInstance() {
		if (instance == null) {
			instance = new SessionManager();
		}
		return instance;
	}
	
	// methods
	public int getSession(Client client) {
		return clientSession.get(client);
	}
	
	public int createSession(Client client) {
		if (!clientSession.containsKey(client)) {
			clientSession.put(client, -1);
		}
		
		int oldSession = clientSession.get(client);
		int newSession = createNewSessionNumber(oldSession);
		clientSession.replace(client, newSession);
		
		return newSession;
	}
	
	public void removeClient(Client client) {
		clientSession.remove(client);
	}
	
	private int createNewSessionNumber(int oldSessionNumber) {
		int newNumber = oldSessionNumber;
		{
			// Create new session number between min and max
			newNumber = ThreadLocalRandom.current().nextInt(minSessionNumber, maxSessionNumber + 1);
		} while(newNumber == oldSessionNumber);
		
		return newNumber;
	}
	
}
