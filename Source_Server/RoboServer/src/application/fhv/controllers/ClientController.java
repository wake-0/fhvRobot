package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import communication.configurations.IConfiguration;
import communication.heartbeat.HeartbeatManager;
import communication.heartbeat.IHeartbeatHandler;
import communication.managers.IConfigurationManager;
import controllers.factory.IClientFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import network.IClientProvider;

public class ClientController<T extends IConfiguration>
		implements IClientProvider<T>, IConfigurationManager, IHeartbeatHandler<T> {

	// Fields
	private final ObservableList<T> clients;
	private final IClientFactory<T> factory;

	private final HashMap<T, HeartbeatManager<T>> clientTimers;

	private T selectedClient;

	// Constructor
	public ClientController(IClientFactory<T> factory) {
		this.clients = FXCollections.observableArrayList();
		this.clientTimers = new HashMap<>();
		this.factory = factory;
	}

	@Override
	public void addClient(T client) {
		if (client == null) {
			return;
		}

		clients.add(client);

		// Each minute check heart beat
		HeartbeatManager<T> manager = new HeartbeatManager<T>(client, this);
		manager.run();

		clientTimers.put(client, manager);
	}

	@Override
	public void removeClient(T client) {
		if (client == null) {
			return;
		}

		clients.remove(client);
		clientTimers.remove(client);
	}

	@Override
	public ObservableList<T> getClients() {
		return clients;
	}

	@Override
	public IConfiguration createConfiguration() {
		T client = factory.create();
		addClient(client);
		return client;
	}

	@Override
	public List<IConfiguration> getConfigurations() {
		return new ArrayList<>(clients);
	}

	public T getSelectedClient() {
		return selectedClient;
	}

	public void setSelectedClient(T selectedClient) {
		this.selectedClient = selectedClient;
	}

	@Override
	public void handleNoHeartbeat(T client) {
		Platform.runLater(() -> {
			removeClient(client);
		});
		System.out.println("Missing heart beat, client disconnected: [" + client.getSessionId() + "]");
	}

	@Override
	public void handleHeartbeat(T client) {
		client.cleanHeartBeatCount();
	}
}
