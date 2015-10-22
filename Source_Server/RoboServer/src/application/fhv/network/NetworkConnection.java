package network;

import java.net.Socket;

import models.Client;

public class NetworkConnection implements Runnable {

	private Socket socket;
	private Client client;
	
	public NetworkConnection(Socket socket, Client client) {
		this.socket = socket;
		this.client = client;
	}
	
	@Override
	public void run() {
		
	}

}
