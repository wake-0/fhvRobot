package communication.managers;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import communication.IClient;
import communication.pdu.ApplicationPDUDecorator;
import communication.pdu.NetworkPDUDecorator;
import communication.pdu.PDU;
import communication.pdu.PresentationPDUDecorator;
import communication.pdu.SessionPDUDecorator;
import communication.pdu.TransportPDUDecorator;

@Singleton
public class CommunicationManager {

	private NetworkManager networkManager;
	private TransportManager transportManager;
	private SessionManager sessionManager;
	private PresentationManager presentationManager;
	private CurrentClientService currentClientService;
	
	@Inject
	public CommunicationManager(NetworkManager networkManager, TransportManager transportManager,
			SessionManager sessionManager, PresentationManager presentationManager,
			CurrentClientService currentClientService) {
		this.networkManager = networkManager;
		this.transportManager = transportManager;
		this.sessionManager = sessionManager;
		this.presentationManager = presentationManager;
		this.currentClientService = currentClientService;
	}

	public void addClient(IClient client) {
		networkManager.addClient(client);
		transportManager.addClient(client);
		sessionManager.addClient(client);
		presentationManager.addClient(client);
	}

	public void removeClient(IClient client) {
		networkManager.removeClient(client);
		transportManager.removeClient(client);
		sessionManager.removeClient(client);
		presentationManager.removeClient(client);
	}

	public String getIpAddress(IClient client) {
		return networkManager.getValue(client);
	}

	public void setPort(IClient client, int port) {
		transportManager.setValueOfClient(client, port);
	}
	
	public void setIpAddress(IClient client, String ipAddress) {
		networkManager.setValueOfClient(client, ipAddress);
	}
	
	public void setSessionId(IClient client, int sessionId) {
		sessionManager.setValueOfClient(client, sessionId);
	}
	
	public int getPort(IClient client) {
		return transportManager.getValue(client);
	}

	public int getSession(IClient client) {
		return sessionManager.getSession(client);
	}
	
	public IClient getCurrentClient() {
		return currentClientService.getClient();
	}
	
	private PDU createPDU(IClient client, String message) {
		return new NetworkPDUDecorator(getIpAddress(client), new TransportPDUDecorator(getPort(client),
				new SessionPDUDecorator(getSession(client), new PresentationPDUDecorator(new ApplicationPDUDecorator(new PDU(message))))));
	}

	public DatagramPacket createDatagramPacket(IClient client, String message) {
		PDU pdu = createPDU(client, message);
		byte[] data = pdu.getEnhancedData();
		int length = data.length;
		InetAddress ipAddress;
		try {
			ipAddress = InetAddress.getByName(getIpAddress(client));
		} catch (UnknownHostException e) {
			ipAddress = InetAddress.getLoopbackAddress();
		}
		
		return new DatagramPacket(data, length, ipAddress, getPort(client));
	}
	
	public void readDatagramPacket(DatagramPacket packet, 
			IDataReceivedHandler applicationHandler, IAnswerHandler sender) {
		
		NetworkPDUDecorator network = new NetworkPDUDecorator(new PDU(packet.getData())); 
		if (networkManager.handleDataReceived(packet, network.getInnerData(), sender)) { return; }

		TransportPDUDecorator transport = new TransportPDUDecorator(network);
		if (transportManager.handleDataReceived(packet, transport.getInnerData(), sender)) { return; }
		
		SessionPDUDecorator session = new SessionPDUDecorator(transport);
		if (sessionManager.handleDataReceived(packet, session.getInnerData(), sender)) { return; }
		
		PresentationPDUDecorator presentation = new PresentationPDUDecorator(session);
		if (presentationManager.handleDataReceived(packet, presentation.getInnerData(), sender)) { return; }
		
		// Use handler so it is possible to decide if the message 
		// should be handled by the application
		applicationHandler.handleDataReceived(packet, presentation.getInnerData(), sender);
	}
}
