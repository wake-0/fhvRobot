package communication.managers;

import java.net.DatagramPacket;
import java.net.InetAddress;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import communication.pdu.NetworkPDUDecorator;
import communication.pdu.PDU;
import communication.pdu.SessionPDUDecorator;
import communication.pdu.TransportPDUDecorator;
import models.Client;

@Singleton
public class CommunicationManager {
	
	private NetworkManager networkManager;
	private TransportManager transportManager;
	private SessionManager sessionManager;
	private PresentationManager presentationManager;
	
	@Inject
	public CommunicationManager(NetworkManager networkManager, TransportManager transportManager, 
			SessionManager sessionManager, PresentationManager presentationManager) {
		this.networkManager = networkManager;
		this.transportManager = transportManager;
		this.sessionManager = sessionManager;
		this.presentationManager = presentationManager;
	}
	
	public void addClient(Client client) {
		networkManager.addClient(client);
		transportManager.addClient(client);
		sessionManager.addClient(client);
		presentationManager.addClient(client);
	}
	
	public void removeClient(Client client) {
		networkManager.removeClient(client);
		transportManager.removeClient(client);
		sessionManager.removeClient(client);
		presentationManager.removeClient(client);
	}
	
	public InetAddress getIpAddress(Client client) {
		return networkManager.getValue(client);
	}
	
	public void setIpAddress(Client client, InetAddress ipAddress) {
		networkManager.setValueOfClient(client, ipAddress);
	}
	
	public int getPort(Client client) {
		return transportManager.getValue(client);
	}
	
	public void setPort(Client client, int port) {
		transportManager.setValueOfClient(client, port);
	}
	
	public int getSession(Client client) {
		return sessionManager.getSession(client);
	}
	
	public ClientType getClientType(Client client) {
		return presentationManager.getValue(client);
	}
	
	public void setClientType(Client client, ClientType type) {
		presentationManager.setValueOfClient(client, type);
	}
	
	public PDU createPDU(Client client, String message) {
		return new NetworkPDUDecorator(new TransportPDUDecorator(new SessionPDUDecorator(new PDU(message)), getPort(client)), getIpAddress(client));
	}

	public DatagramPacket createDatagramPacket(Client client, String message) {
		// Create PDU
		PDU pdu = createPDU(client, client.getSendData());
		byte[] sendData = pdu.getData();
		int length = sendData.length;
		
		return new DatagramPacket(sendData, length, getIpAddress(client), getPort(client));
	}
}
