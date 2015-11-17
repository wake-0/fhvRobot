package communication.managers;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import communication.IClientConfiguration;
import communication.pdu.ApplicationPDUDecorator;
import communication.pdu.NetworkPDUDecorator;
import communication.pdu.PDU;
import communication.pdu.PresentationPDUDecorator;
import communication.pdu.SessionPDUDecorator;
import communication.pdu.TransportPDUDecorator;

@Singleton
public class CommunicationManager {

	private final NetworkManager networkManager;
	private final TransportManager transportManager;
	private final SessionManager sessionManager;
	private final PresentationManager presentationManager;
	private final CurrentConfigurationService currentConfigurationService;
	
	@Inject
	public CommunicationManager(NetworkManager networkManager, TransportManager transportManager,
			SessionManager sessionManager, PresentationManager presentationManager,
			CurrentConfigurationService currentConfigurationService) {
		this.networkManager = networkManager;
		this.transportManager = transportManager;
		this.sessionManager = sessionManager;
		this.presentationManager = presentationManager;
		this.currentConfigurationService = currentConfigurationService;
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
		
		// Use handler so it is possible to decide if the message 
		// should be handled by the application
		applicationHandler.handleDataReceived(packet, presentation.getInnerData(), answerHandler);
	}
}
