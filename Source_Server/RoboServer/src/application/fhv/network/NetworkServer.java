package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import models.Client;

public class NetworkServer implements Runnable {

	// field which stores the clients
	private IClientProvider clientProvider;
	
	// server specific stuff
	private int port = 997;
	private ServerSocket serverSocket;
	private List<NetworkConnection> connections;

	// constructors
	public NetworkServer(IClientProvider clientProvider) {
		try {
			this.clientProvider = clientProvider;
			this.connections = new ArrayList<>();
			this.serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// methods
	@Override
	public void run() {
		try {
			while(true) {
				// wait for connection
				Socket clientSocket = serverSocket.accept();
				// create new connection
				NetworkConnection connection = new NetworkConnection(clientSocket, new Client());
				// start new connection
				new Thread(connection).start();
				// update list of connections and clients
				addConnection(connection);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Client> getClients() {
		return connections
			.stream()
			.map(connection -> connection.getClient())
			.collect(Collectors.toList());
	}
	
	public void send(Client client) {
		NetworkConnection connection = getConnectionFromClient(client);
		if (connection != null) {
			connection.send();
		}
	}

	public void kill(Client client) {
		NetworkConnection connection = getConnectionFromClient(client);
		if (connection != null) {
			removeConnection(connection);
			connection.close();
		}
	}
	
	private void addConnection(NetworkConnection connection) {
		connections.add(connection);
		clientProvider.addClient(connection.getClient());
	}
	
	private void removeConnection(NetworkConnection connection) {
		connections.remove(connection);
		clientProvider.removeClient(connection.getClient());
	}
	
	private NetworkConnection getConnectionFromClient(Client client) {
		return connections
			.stream()
	        .filter(connection -> connection.getClient().compareTo(client) == 0)
	        .findFirst().get();
	}
}
