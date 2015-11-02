package communication.managers;

import java.net.DatagramPacket;
import java.net.InetAddress;

import communication.pdu.NetworkPDUDecorator;
import communication.pdu.PDU;
import communication.pdu.SessionPDUDecorator;
import communication.pdu.TransportPDUDecorator;
import models.Client;

public class CommunicationManager {
	
	private NetworkManager networkManager;
	private TransportManager transportManager;
	private SessionManager sessionManager;

	public CommunicationManager() {
		networkManager = new NetworkManager();
		transportManager = new TransportManager();
		sessionManager = new SessionManager();
	}
	
	public void addClient(Client client) {
		networkManager.addClient(client);
		transportManager.addClient(client);
		sessionManager.addClient(client);
	}
	
	public void removeClient(Client client) {
		networkManager.removeClient(client);
		transportManager.removeClient(client);
		sessionManager.removeClient(client);
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
