package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import communication.IClient;
import communication.managers.CommunicationManager;
import communication.managers.IDataReceivedHandler;
import communication.managers.IAnswerHandler;
import communication.managers.IClientManager;
import models.Client;
import network.receiver.INetworkReceiver;
import network.receiver.LoggerNetworkReceiver;
import network.sender.INetworkSender;
import network.sender.LoggerNetworkSender;

@Singleton
public class NetworkServer implements Runnable, IDataReceivedHandler, 
IAnswerHandler, IClientManager {

	// field which stores the clients
	private final CommunicationManager communicationManager;
	private final INetworkSender sender;
	private final INetworkReceiver receiver;
	private final DatagramSocket serverSocket;
	private final IClientProvider clientProvider;
	
	// server specific stuff
	private boolean isRunning = true;
	private final int roboPort = 997;
	private final int receivePacketSize = 1024;

	private DatagramPacket receivePacket;

	// constructors
	@Inject
	public NetworkServer(CommunicationManager communicationManager, IClientProvider clientProvider)
			throws SocketException {
		this.serverSocket = new DatagramSocket(roboPort);
		this.communicationManager = communicationManager;
		this.clientProvider = clientProvider;
		
		// Added network sender and receiver which can log
		this.sender = new LoggerNetworkSender(serverSocket);
		this.receiver = new LoggerNetworkReceiver(serverSocket);
	}

	// methods
	@Override
	public void run() {
		while (isRunning) {
			byte[] receiveData = new byte[receivePacketSize];
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			receiver.receive(receivePacket);

			if (serverSocket.isClosed()) { continue; }
			
			communicationManager.readDatagramPacket(receivePacket, this, this);
		}
	}

	@Override
	public boolean handleDataReceived(DatagramPacket packet, byte[] data, IAnswerHandler sender) {
		try {
			String name = new String(data);
			
			Client client = (Client)communicationManager.getCurrentClient();
			client.setName(name);
			client.setReceiveData(name);
			
			// TODO: handle other message

			// Only for test purposes
			client.setSendData(name);
			send(client);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void send(Client client) throws IOException {
		if (client == null) {
			return;
		}

		DatagramPacket sendPacket = communicationManager.createDatagramPacket(client, client.getSendData());
		sender.send(sendPacket);
	}	

	public void removeClient(Client client) {
		communicationManager.removeClient(client);
	}
	
	public void shutdown() {
		serverSocket.close();
		isRunning = false;
	}
	
	@Override
	public void answer(byte[] data) {
		InetAddress address = receivePacket.getAddress();
		DatagramPacket answerPacket = new DatagramPacket(data, data.length, address, roboPort);
		sender.send(answerPacket);
	}

	
	@Override
	public IClient createClient() {
		Client client = new Client();
		// Add client to the right layers
		clientProvider.addClient(client);
		communicationManager.addClient(client);
		return client;
	}
}