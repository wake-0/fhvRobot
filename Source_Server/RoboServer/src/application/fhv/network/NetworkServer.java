package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import communication.managers.CommunicationManager;
import models.Client;
import models.ClientFactory;

@Singleton
public class NetworkServer implements Runnable {

	// field which stores the clients
	private IClientProvider clientProvider;
	private CommunicationManager communicationManager;
	private NetworkHelper helper;
	
	private boolean isRunning = true;
	
	// server specific stuff
	//private final int port = 997;
	private final int port = 8632;
	private DatagramSocket serverSocket;

	// constructors
	@Inject
	public NetworkServer(IClientProvider clientProvider, CommunicationManager communicationManager, NetworkHelper helper) {
		try {
			this.clientProvider = clientProvider;
			this.serverSocket = new DatagramSocket(port);
			this.communicationManager = communicationManager;
			this.helper = helper;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// methods
	@Override
	public void run() {
		try {
			byte[] receiveData = new byte[1024];
			
			while(isRunning) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				
				// Check a client with the ip address does already exists
				Client client = clientProvider.getClientByIp(receivePacket.getAddress().getHostName());
				if (client == null) {
					client = ClientFactory.createClient(
							receivePacket.getAddress().getHostAddress(), 
							receivePacket.getPort());
					
					clientProvider.addClient(client);	
				}
				
				String message = helper.handleReceivedData(receivePacket, client);
				
				System.out.println("Message received: " + message);
				client.setSendData(message);
				
				send(client);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void removeClient(Client client) {
		communicationManager.removeClient(client);
	}
	
	public void send(Client client) throws IOException {
		if (client == null) { return; }
		
		DatagramPacket sendPacket = helper.handleSendData(client); 
		serverSocket.send(sendPacket);
		
		System.out.println("Message send: " + sendPacket.getData());
	}
	
	public void shutdown() {
		serverSocket.close();
		isRunning = false;
	}
}