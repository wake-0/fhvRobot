package communication.managers;

import java.util.HashMap;

import communication.IClient;

public abstract class LayerManager<T> {

	// fields
	protected HashMap<IClient, T> clientMap;
	
	// Constructor
	public LayerManager() {
		clientMap = new HashMap<>();
	}
	
	// Methods
	public T getValue(IClient client) {
		return clientMap.get(client);
	}
	
	public void setValueOfClient(IClient client, T value) {
		if (clientMap.containsKey(client)) {
			clientMap.replace(client, value);
			return;
		}
		
		clientMap.put(client, value);
	}
	
	public void removeClient(IClient client) {
		clientMap.remove(client);
	}
	
	public void addClient(IClient client, T value) {
		clientMap.put(client, value);
	}
	
	public void addClient(IClient client) {
		if (!clientMap.containsKey(client)) {
			clientMap.put(client, getDefaultValue());
		} 
	}
	
	protected abstract T getDefaultValue();
}
