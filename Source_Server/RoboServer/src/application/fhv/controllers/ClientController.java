package controllers;

import java.util.ArrayList;
import java.util.List;

import communication.IConfiguration;
import communication.managers.IConfigurationManager;
import controllers.factory.IClientFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import network.IClientProvider;

public class ClientController<T extends IConfiguration> implements IClientProvider<T>, IConfigurationManager {

	// Fields
	private ObservableList<T> clients;
	private IClientFactory<T> factory;

	// Constructor
	public ClientController(IClientFactory<T> factory) {
		clients = FXCollections.observableArrayList();

		this.factory = factory;
	}

	@Override
	public void addClient(T client) {
		clients.add(client);
	}

	@Override
	public void removeClient(T client) {
		clients.remove(client);
	}

	@Override
	public ObservableList<T> getClients() {
		return clients;
	}

	@Override
	public IConfiguration createConfiguration() {
		T client = factory.create();
		clients.add(client);
		return client;
	}

	@Override
	public List<IConfiguration> getConfigurations() {
		return new ArrayList<>(clients);
	}

}
