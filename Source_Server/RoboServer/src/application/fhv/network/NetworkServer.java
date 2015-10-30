package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import communication.NetworkPDUDecorator;
import communication.PDU;
import communication.PresentationPDUDecorator;
import communication.SessionPDUDecorator;
import communication.TransportPDUDecorator;
import models.Client;

public class NetworkServer implements Runnable {

	// field which stores the clients
	private IClientProvider clientProvider;
	
	// server specific stuff
	private final int port = 997;
	private DatagramSocket serverSocket;

	// constructors
	public NetworkServer(IClientProvider clientProvider) {
		try {
			this.clientProvider = clientProvider;
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
			
			while(true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				
				// Check a client with the ip address does already exists
				Client client = clientProvider.getClientByIp(receivePacket.getAddress().getHostName());
				if (client == null) {
					client = new Client();
					client.setIpAddress(receivePacket.getAddress().getHostAddress());
					client.setPort(receivePacket.getPort());
					clientProvider.addClient(client);	
				}
				
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
		PDU pdu = new PDU(client.getSendData());
		PDU pduToSend = new SessionPDUDecorator(new PresentationPDUDecorator(pdu));
		
		byte[] sendData = pduToSend.getData();
		int length = sendData.length;
		// Enhance/Decorate pdu
		TransportPDUDecorator transport = new TransportPDUDecorator(pduToSend, client.getPort());
		NetworkPDUDecorator network = new NetworkPDUDecorator(transport, InetAddress.getByName(client.getIpAddress()));
		
		DatagramPacket sendPacket = new DatagramPacket(sendData, length, network.getIpAddress(), transport.getPort());
		serverSocket.send(sendPacket);
		
		System.out.println("Message send: " + client.getSendData());
	}
	
	public void shutdown() {
		serverSocket.close();
	}
}