package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import communication.configurations.IConfiguration;
import controllers.ClientController.ICommandListener;
import controllers.ClientController.IOperatorChangedListener;
import models.Client;
import network.IClientController;

public class SingleClientController implements IClientController<Client> {

	// Fields
	private final Client client;
	private final Map<Integer, List<ICommandListener<Client>>> commandListeners;

	// Constructor
	public SingleClientController(Client client) {
		this.client = client;
		this.commandListeners = new HashMap<>();
	}

	// Methods
	@Override
	public IConfiguration createConfiguration() {
		return client;
	}

	@Override
	public List<IConfiguration> getConfigurations() {
		List<IConfiguration> clientList = new ArrayList<>();
		clientList.add(client);
		return clientList;
	}

	@Override
	public void addClient(Client client) {
		// Do nothing
	}

	@Override
	public void removeClient(Client client) {
		// Do nothing
	}

	@Override
	public List<Client> getClients() {
		List<Client> clientList = new ArrayList<>();
		clientList.add(client);
		return clientList;

	}

	public void addCommandListener(ICommandListener<Client> commandListener, int command) {
		List<ClientController.ICommandListener<Client>> listeners = commandListeners.get(command);

		if (listeners == null) {
			listeners = new ArrayList<>();
			commandListeners.put(command, listeners);

		}

		listeners.add(commandListener);
	}

	public void handleCommandReceived(Client client, int command, byte[] payload) {
		List<ClientController.ICommandListener<Client>> listeners = commandListeners.get(command);

		if (listeners != null) {
			for (ClientController.ICommandListener<Client> l : listeners) {
				l.commandReceived(client, command, payload);
			}
		}

	}

	@Override
	public void setOperator(Client operator) {
		// TODO: add callback for operator changed
		operator.setIsOperator(true);
	}

	@Override
	public void releaseOperator(Client operator) {
		operator.setIsOperator(false);
	}

	@Override
	public List<Client> getOperators() {
		List<Client> operators = new ArrayList<>();
		if (client.getIsOperator()) {
			operators.add(client);
		}
		return operators;
	}

	@Override
	public void addOperatorChangedListener(IOperatorChangedListener<Client> operatorListener) {
		// TODO Auto-generated method stub

	}
}
