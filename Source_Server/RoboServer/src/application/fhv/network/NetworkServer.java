package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import communication.managers.CommunicationManager;
import models.Client;
import models.ClientFactory;
import network.receiver.INetworkReceiver;
import network.receiver.LoggerNetworkReceiver;
import network.sender.INetworkSender;
import network.sender.LoggerNetworkSender;

@Singleton
public class NetworkServer implements Runnable {

	// field which stores the clients
	private final IClientProvider clientProvider;
	private final CommunicationManager communicationManager;
	private final NetworkHelper helper;
	private final INetworkSender sender;
	private final INetworkReceiver receiver;
	private final DatagramSocket serverSocket;
	
	// server specific stuff
	private boolean isRunning = true;
	private final int roboPort = 997;
	private final int receivePacketSize = 1024;
	
	// constructors
	@Inject
	public NetworkServer(IClientProvider clientProvider, CommunicationManager communicationManager, NetworkHelper helper) throws SocketException {
		this.serverSocket = new DatagramSocket(roboPort);
		this.clientProvider = clientProvider;
		this.communicationManager = communicationManager;
		this.helper = helper;
		
		// Added network sender and receiver which can log
		this.sender = new LoggerNetworkSender(serverSocket);
		this.receiver = new LoggerNetworkReceiver(serverSocket);	
	}

	// methods
	@Override
	public void run() {
		try {
			byte[] receiveData = new byte[receivePacketSize];
			
			while(isRunning) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				receiver.receive(receivePacket);
				
				// Check a client with the ip address does already exists otherwise add them and return this client
				Client client = getClientFromPacket(receivePacket);
				// This message is not needed when test phase is over
				String message = helper.handleReceivedData(receivePacket, client);
				
				// Only for test purposes
				client.setSendData(message);
				send(client);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Client getClientFromPacket(DatagramPacket packet) {
		Client client = clientProvider.getClientByIp(packet.getAddress().getHostName());
		if (client == null) {
			client = ClientFactory.createClient(
					packet.getAddress().getHostAddress(), 
					packet.getPort());
			
			clientProvider.addClient(client);	
		}
		
		return client;
	}
	
	public void removeClient(Client client) {
		communicationManager.removeClient(client);
	}
	
	public void send(Client client) throws IOException {
		if (client == null) { return; }
		
		DatagramPacket sendPacket = helper.handleSendData(client);
		sender.send(sendPacket);
	}
	
	public void shutdown() {
		serverSocket.close();
		isRunning = false;
	}
}