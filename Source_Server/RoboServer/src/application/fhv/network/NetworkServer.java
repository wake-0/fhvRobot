package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import communication.managers.CommunicationManager;
import communication.pdu.PDU;
import models.Client;
import models.ClientFactory;

public class NetworkServer implements Runnable {

	// field which stores the clients
	private IClientProvider clientProvider;
	private CommunicationManager communicationManager;
	
	// server specific stuff
	private final int port = 997;
	private DatagramSocket serverSocket;

	// constructors
	public NetworkServer(IClientProvider clientProvider) {
		try {
			this.clientProvider = clientProvider;
			this.serverSocket = new DatagramSocket(port);
			this.communicationManager = new CommunicationManager();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// methods
	@Override
	public void run() {
		try {
			byte[] receiveData = new byte[1024];
			
			while(true) {
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
				
				// Use CommunicationManager
				communicationManager.addClient(client);
				communicationManager.setIpAddress(client, InetAddress.getByName(client.getIpAddress()));
				communicationManager.setPort(client, client.getPort());
				
				String sentence = new String(receivePacket.getData());
				client.setReceiveData(sentence);
				System.out.println("Message received: " + sentence);
				client.setSendData(sentence);
				
				send(client);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(Client client) throws IOException {
		if (client == null) { return; }
		
		// Create PDU
		PDU pdu = communicationManager.createPDU(client, client.getSendData());
		byte[] sendData = pdu.getData();
		int length = sendData.length;
		
		DatagramPacket sendPacket = new DatagramPacket(
				sendData, length, 
				communicationManager.getIpAddress(client), 
				communicationManager.getPort(client));
		
		serverSocket.send(sendPacket);
		
		System.out.println("Message send: " + client.getSendData());
	}
	
	public void shutdown() {
		serverSocket.close();
	}
}