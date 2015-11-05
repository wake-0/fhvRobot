package communication.managers;

import java.util.HashMap;

import models.Client;

public abstract class LayerManager<T> {

	// fields
	protected HashMap<Client, T> clientMap;
	
	// Constructor
	public LayerManager() {
		clientMap = new HashMap<>();
	}
	
	// Methods
	public T getValue(Client client) {
		return clientMap.get(client);
	}
	
	public void setValueOfClient(Client client, T value) {
		if (clientMap.containsKey(client)) {
			clientMap.replace(client, value);
			return;
		}
		
		clientMap.put(client, value);
	}
	
	public void removeClient(Client client) {
		clientMap.remove(client);
	}
	
	public void addClient(Client client, T value) {
		clientMap.put(client, value);
	}
	
	public void addClient(Client client) {
		if (!clientMap.containsKey(client)) {
			clientMap.put(client, getDefaultValue());
		} 
	}
	
	protected abstract T getDefaultValue();
}
