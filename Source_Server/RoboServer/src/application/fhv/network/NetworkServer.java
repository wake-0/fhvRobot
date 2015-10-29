package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
	private DatagramSocket serverSocket;
	private List<NetworkConnection> connections;

	// constructors
	public NetworkServer(IClientProvider clientProvider) {
		try {
			this.clientProvider = clientProvider;
			this.connections = new ArrayList<>();
			this.serverSocket = new DatagramSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// methods
	@Override
	public void run() {
		try {
			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];
			
			while(true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				String sentence = new String(receivePacket.getData());
				System.out.println("RECEIVED: " + sentence);                   
				InetAddress IPAddress = receivePacket.getAddress();                   
				int port = receivePacket.getPort();                   
				String capitalizedSentence = sentence.toUpperCase();                   
				sendData = capitalizedSentence.getBytes();                   
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				serverSocket.send(sendPacket);
				
				/*
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				if (!serverSocket.getLocalAddress().equals(receivePacket.getAddress())) {
					// Check if it is part of an existing NetworkConnection
					String message = new String(receivePacket.getData());
					sendData = receivePacket.getData();
					
					Client c = new Client();
					NetworkConnection connection = new NetworkConnection(receivePacket.getAddress(), new Client(), serverSocket, port);
					//c.setSendData();
					connection.send("answer:" + message);
					
					addConnection(connection);
					
					// Send message back
					// DatagramPacket sendPacket =new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), port);                   
					//serverSocket.send(sendPacket);
					
					// wait for connection
					//Socket clientSocket = serverSocket.accept();
					// create new connection
					//NetworkConnection connection = new NetworkConnection(clientSocket, new Client());
					// start new connection
					//new Thread(connection).start();
					// update list of connections and clients
					//addConnection(connection);	
				}
				*/
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
		// NetworkConnection connection = getConnectionFromClient(client);
		// if (connection != null) {
		// 	connection.send();
		// }
	}

	public int getPort() {
		return port;
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
