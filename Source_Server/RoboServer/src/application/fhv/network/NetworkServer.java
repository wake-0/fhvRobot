package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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

		// Values to send
		byte[] sendData = client.getSendData().getBytes();
		int length = sendData.length;
		InetAddress ipAddress = InetAddress.getByName(client.getIpAddress());
		int port = client.getPort();
		
		DatagramPacket sendPacket = new DatagramPacket(sendData, length, ipAddress, port);
		serverSocket.send(sendPacket);
		
		System.out.println("Message send: " + client.getSendData());
	}
	
	public void shutdown() {
		serverSocket.close();
	}
}