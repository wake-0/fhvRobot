package network;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import communication.IClient;
import communication.managers.CommunicationManager;
import communication.managers.IApplicationMessageHandler;
import models.Client;

 @Singleton
public class NetworkHelper implements IApplicationMessageHandler{

	private final CommunicationManager communicationManager;
	
	@Inject
	public NetworkHelper(CommunicationManager communicationManager) {
		this.communicationManager = communicationManager;
	}
	
	public DatagramPacket handleSendData(Client client) {
		return communicationManager.createDatagramPacket(client, client.getSendData());
	}
	
	public void handleReceivedData(DatagramPacket packet, Client client) throws UnknownHostException {
		communicationManager.addClient(client);
		communicationManager.setIpAddress(client, InetAddress.getByName(client.getIpAddress()));
		communicationManager.setPort(client, client.getPort());
		communicationManager.readDatagramPacket(client, packet, this);
	}

	@Override
	public void handleMessage(IClient client, String message) {
		
		if (client instanceof Client) {
			((Client)client).setReceiveData(message);
		}
	}
}
