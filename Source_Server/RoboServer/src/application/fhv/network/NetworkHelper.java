package network;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import communication.managers.CommunicationManager;
import models.Client;

 @Singleton
public class NetworkHelper {

	private final CommunicationManager communicationManager;
	
	@Inject
	public NetworkHelper(CommunicationManager communicationManager) {
		this.communicationManager = communicationManager;
	}
	
	public DatagramPacket handleSendData(Client client) {
		return communicationManager.createDatagramPacket(client, client.getSendData());
	}
	
	public String handleReceivedData(DatagramPacket packet, Client client) throws UnknownHostException {
		communicationManager.addClient(client);
		communicationManager.setIpAddress(client, InetAddress.getByName(client.getIpAddress()));
		communicationManager.setPort(client, client.getPort());
		
		String message = communicationManager.readDatagramPacket(client, packet);
		client.setReceiveData(message);
		
		return message;
	}
}
