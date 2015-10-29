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
	private int port = 997;
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
			byte[] sendData = new byte[1024];
			
			while(true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				String sentence = new String(receivePacket.getData());
				System.out.println("RECEIVED: " + sentence);                   
				InetAddress IPAddress = receivePacket.getAddress();
				
				// Check a client with the ip address does already exists
				Client client = clientProvider.getClientByIp(IPAddress.getHostName());
				if (client == null) {
					client = new Client();
					client.setIpAddress(receivePacket.getAddress().getHostAddress());
					client.setPort(receivePacket.getPort());
					clientProvider.addClient(client);	
				}
				
				client.setReceiveData(sentence);
				
				String capitalizedSentence = "answer:" + sentence.toUpperCase();                   
				sendData = capitalizedSentence.getBytes();
				
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(client.getIpAddress()), client.getPort());
				serverSocket.send(sendPacket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(Client client) throws IOException {
		if (client == null) { return; }
		byte[] sendData = client.getSendData().getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(client.getIpAddress()), client.getPort());
		serverSocket.send(sendPacket);
	}
	
	public void shutdown() {
		serverSocket.close();
	}
}