package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import communication.configurations.IConfiguration;
import communication.heartbeat.HeartbeatManager;
import communication.heartbeat.IHeartbeatHandler;
import controllers.factory.IClientFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.IExtendedConfiguration;
import network.IClientController;

public class ClientController<T extends IExtendedConfiguration> implements IClientController<T>, IHeartbeatHandler<T> {

	public interface ICommandListener<T extends IExtendedConfiguration> {
		void commandReceived(T client, int command, byte[] payload);
	}

	// Fields
	private final ObservableList<T> clients;
	private final IClientFactory<T> factory;
	private final Map<Integer, List<ICommandListener<T>>> commandListeners;

	private final HashMap<T, HeartbeatManager<T>> clientTimers;

	private T selectedClient;

	// Constructor
	public ClientController(IClientFactory<T> factory) {
		this.clients = FXCollections.observableArrayList();
		this.clientTimers = new HashMap<>();
		this.commandListeners = new HashMap<>();
		this.factory = factory;
	}

	// Methods
	public List<T> getOperators() {
		List<T> operators = new ArrayList<>();

		for (T client : clients) {
			if (client.getIsOperator()) {
				operators.add(client);
			}
		}

		return operators;
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
	public IExtendedConfiguration createConfiguration() {
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

	public void addCommandListener(ICommandListener<T> commandListener, int command) {
		List<ClientController.ICommandListener<T>> listeners = commandListeners.get(command);

		if (listeners == null) {
			listeners = new ArrayList<>();
			commandListeners.put(command, listeners);

		}

		listeners.add(commandListener);
	}

	public void handleCommandReceived(T client, int command, byte[] payload) {
		List<ClientController.ICommandListener<T>> listeners = commandListeners.get(command);

		if (listeners != null) {
			for (ClientController.ICommandListener<T> l : listeners) {
				l.commandReceived(client, command, payload);
			}
		}
	}
}
