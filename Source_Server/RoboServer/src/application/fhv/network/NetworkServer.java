package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import models.Client;

public class NetworkServer implements Runnable {

	// field which stores the clients
	private List<Client> clients;
	
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
				updateClients();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateClients() {
		clients.clear();
		connections.forEach(connection -> clients.add(connection.getClient()));
	}
	
	public void send(Client client) {
		connections.forEach(connection ->
		{
			if (connection.getClient().compareTo(client) == 0) {
				connection.send();
			}
		});
	}
}
