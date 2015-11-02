package communication.managers;

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
}
