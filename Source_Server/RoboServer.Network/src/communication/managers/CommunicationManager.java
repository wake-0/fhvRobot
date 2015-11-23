/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: CommunicationManager.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.managers;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import communication.IConfiguration;
import communication.pdu.ApplicationPDU;
import communication.pdu.NetworkPDU;
import communication.pdu.PDU;
import communication.pdu.PresentationPDU;
import communication.pdu.SessionPDU;
import communication.pdu.TransportPDU;

public class CommunicationManager {

	// Fields
	private final NetworkManager networkManager;
	private final TransportManager transportManager;
	private final SessionManager sessionManager;
	private final PresentationManager presentationManager;
	private final CurrentConfigurationService currentConfigurationService;

	// Constructor
	public CommunicationManager(IConfigurationManager clientManager) {
		this.currentConfigurationService = new CurrentConfigurationService();

		// Add a manager foreach layer
		this.networkManager = new NetworkManager(clientManager, currentConfigurationService);
		this.transportManager = new TransportManager(clientManager, currentConfigurationService);
		this.sessionManager = new SessionManager(clientManager, currentConfigurationService);
		this.presentationManager = new PresentationManager(clientManager, currentConfigurationService);
	}

	// Methods
	public IConfiguration getCurrentConfiguration() {
		return currentConfigurationService.getConfiguration();
	}

	public DatagramPacket createOpenConnectionDatagramPacket(IConfiguration configuration, String message) {
		int sessionId = 0b00000000;
		int flags = 0b10000000;

		PDU pdu = new NetworkPDU(new TransportPDU(new SessionPDU(flags, sessionId,
				new PresentationPDU(new ApplicationPDU(new PDU(message))))));

		return createDatagramPacketFromPDU(configuration, pdu);
	}

	public DatagramPacket createDatagramPacket(IConfiguration configuration, String message) {
		PDU pdu = createPDU(configuration, message);
		return createDatagramPacketFromPDU(configuration, pdu);
	}

	public void readDatagramPacket(DatagramPacket packet,
			IDataReceivedHandler<ApplicationPDU> applicationHandler, IAnswerHandler answerHandler) {

		NetworkPDU network = new NetworkPDU(new PDU(packet.getData()));
		if (networkManager.handleDataReceived(packet, network, answerHandler)) {
			return;
		}

		TransportPDU transport = new TransportPDU(network.getInnerData());
		if (transportManager.handleDataReceived(packet, transport, answerHandler)) {
			return;
		}

		SessionPDU session = new SessionPDU(transport.getInnerData());
		if (sessionManager.handleDataReceived(packet, session, answerHandler)) {
			return;
		}

		PresentationPDU presentation = new PresentationPDU(session.getInnerData());
		if (presentationManager.handleDataReceived(packet, presentation, answerHandler)) {
			return;
		}

		// Use handler so it is possible to decide if the message should be
		// handled by the application
		ApplicationPDU application = new ApplicationPDU(presentation.getInnerData());
		applicationHandler.handleDataReceived(packet, application, answerHandler);
	}

	private DatagramPacket createDatagramPacketFromPDU(IConfiguration configuration, PDU pdu) {
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

	private PDU createPDU(IConfiguration configuration, String message) {
		return new NetworkPDU(new TransportPDU(new SessionPDU(configuration.getSessionId(),
				new PresentationPDU(new ApplicationPDU(new PDU(message))))));
	}
}
