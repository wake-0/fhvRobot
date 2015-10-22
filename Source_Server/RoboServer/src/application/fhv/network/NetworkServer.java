package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import models.Client;

public class NetworkServer implements Runnable {

	// field which stores the clients
	private ObservableList<Client> clients;
	
	// server specific stuff
	private int port = 997;
	private ServerSocket serverSocket;
	private List<NetworkConnection> connections;

	// constructors
	public NetworkServer(ObservableList<Client> clients) {
		try {
			this.clients = clients;
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
				connections.add(connection);

				// Update list
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						clients.add(connection.getClient());
					}
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private NetworkConnection getConnectionFromClient(Client client) {
		return connections
		.stream()
        .filter(connection -> connection.getClient().compareTo(client) == 0)
        .findFirst().get();
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
			connections.remove(connection);
			connection.kill();
		}
	}
}
