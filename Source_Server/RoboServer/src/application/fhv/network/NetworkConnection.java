package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import models.Client;

public class NetworkConnection implements Comparable<NetworkConnection> {

	// fields
	private InetAddress ipAddress;
	private Client client;
	private boolean closed;
	private DatagramSocket serverSocket;
	private int port;

	// constructor
	public NetworkConnection(InetAddress ipAddress, Client client, DatagramSocket serverSocket, int port) {
		this.ipAddress = ipAddress;
		this.client = client;
		this.closed = false;
		this.serverSocket = serverSocket;
		this.port = port;
	}

	// methods
	public Client getClient() {
		return client;
	}

	public void receive(String message) {
		client.setReceiveData(message);
	}

	public void send(String message) {
		try {
			// byte[] sendData = client.getSendData().getBytes();
			byte[] sendData = message.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
			serverSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		closed = true;
		client.clear();
	}

	@Override
	public int compareTo(NetworkConnection o) {
		if (o == null) {
			return -1;
		}
		return client.compareTo(o.client);
	}
}
