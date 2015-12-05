package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import communication.IConfiguration;
import communication.managers.IConfigurationManager;
import controllers.factory.IClientFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import network.IClientProvider;

public class ClientController<T extends IConfiguration> implements IClientProvider<T>, IConfigurationManager {

	// Fields
	private final ObservableList<T> clients;
	private final IClientFactory<T> factory;

	private final HashMap<T, Timer> clientTimers;

	private T selectedClient;

	// Constructor
	public ClientController(IClientFactory<T> factory) {
		clients = FXCollections.observableArrayList();
		clientTimers = new HashMap<>();
		this.factory = factory;
	}

	@Override
	public void addClient(T client) {
		if (client == null) {
			return;
		}

		clients.add(client);
		Timer timer = new Timer();
		clientTimers.put(client, timer);

		// Each minute check heart beat
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (client.getHeartBeatCount() == 0) {
					removeClient(client);
				} else {
					client.cleanHeartBeatCount();
				}
			}
		}, 1 * 60 * 1000, 1 * 60 * 1000);
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

}
