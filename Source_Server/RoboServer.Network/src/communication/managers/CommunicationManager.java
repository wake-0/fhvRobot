package communication.managers;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import communication.IClientConfiguration;
import communication.pdu.ApplicationPDUDecorator;
import communication.pdu.NetworkPDUDecorator;
import communication.pdu.PDU;
import communication.pdu.PresentationPDUDecorator;
import communication.pdu.SessionPDUDecorator;
import communication.pdu.TransportPDUDecorator;

public class CommunicationManager {

	private final NetworkManager networkManager;
	private final TransportManager transportManager;
	private final SessionManager sessionManager;
	private final PresentationManager presentationManager;
	private final CurrentConfigurationService currentConfigurationService;
	
	public CommunicationManager(IClientManager clientManager) {
		
		this.currentConfigurationService = new CurrentConfigurationService();
		
		this.networkManager = new NetworkManager(clientManager, currentConfigurationService);
		this.transportManager = new TransportManager(clientManager, currentConfigurationService);
		this.sessionManager = new SessionManager(clientManager, currentConfigurationService);
		this.presentationManager = new PresentationManager(clientManager, currentConfigurationService);
	}

	private PDU createPDU(IClientConfiguration configuration, String message) {
		return new NetworkPDUDecorator(configuration.getIpAddress(), new TransportPDUDecorator(configuration.getPort(),
				new SessionPDUDecorator(configuration.getSessionId(), new PresentationPDUDecorator(new ApplicationPDUDecorator(new PDU(message))))));
	}

	public IClientConfiguration getCurrentClientConfiguration() {
		return currentConfigurationService.getClient();
	}
	
	public DatagramPacket createDatagramPacket(IClientConfiguration configuration, String message) {
		PDU pdu = createPDU(configuration, message);
		byte[] data = pdu.getEnhancedData();
		int length = data.length;
		InetAddress ipAddress;
		
		try {
			ipAddress = InetAddress.getByName(configuration.getIpAddress());
		} catch (UnknownHostException e) {
			ipAddress = InetAddress.getLoopbackAddress();
		}
		
		return new DatagramPacket(data, length, ipAddress, configuration.getPort());
	}
	
	public void readDatagramPacket(DatagramPacket packet, IDataReceivedHandler applicationHandler, IAnswerHandler answerHandler) {
		
		NetworkPDUDecorator network = new NetworkPDUDecorator(new PDU(packet.getData())); 
		if (networkManager.handleDataReceived(packet, network.getData(), answerHandler)) { return; }

		TransportPDUDecorator transport = new TransportPDUDecorator(network);
		if (transportManager.handleDataReceived(packet, transport.getData(), answerHandler)) { return; }
		
		SessionPDUDecorator session = new SessionPDUDecorator(transport);
		if (sessionManager.handleDataReceived(packet, session.getData(), answerHandler)) { return; }
		
		PresentationPDUDecorator presentation = new PresentationPDUDecorator(session);
		if (presentationManager.handleDataReceived(packet, presentation.getData(), answerHandler)) { return; }
		
		// Use handler so it is possible to decide if the message should be handled by the application
		applicationHandler.handleDataReceived(packet, presentation.getInnerData(), answerHandler);
	}
}
