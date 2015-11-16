package communication.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import communication.IClient;

public abstract class LayerManager<T> implements IDataReceivedHandler {

	// fields
	protected HashMap<IClient, T> clientMap;
	protected IClientManager manager;
	protected CurrentClientService currentClientService;
	
	// Constructor
	public LayerManager(IClientManager manager, CurrentClientService currentClientService) {
		clientMap = new HashMap<>();
		this.manager = manager;
		this.currentClientService = currentClientService;
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
	
	protected <E> IClient getClientByValue(Map<IClient, E> map, E value) {
		IClient client = getKeyByValue(map, value);
		return client == null ? manager.createClient() : client;
	}
	
	protected abstract T getDefaultValue();
	
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (Objects.equals(value, entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
}
