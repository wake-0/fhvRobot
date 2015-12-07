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

import communication.configurations.ConfigurationSettings;
import communication.configurations.IConfiguration;
import communication.pdu.ApplicationPDU;
import communication.pdu.NetworkPDU;
import communication.pdu.PDU;
import communication.pdu.PDUFactory;
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

	public DatagramPacket createOpenConnectionDatagramPacket(IConfiguration configuration) {
		PDU pdu = createOpenConnectionPDU();
		return DatagramFactory.createPacketFromPDU(configuration, pdu);
	}

	public DatagramPacket createDatagramPacket(IConfiguration configuration, int command, byte[] payload) {
		PDU pdu = createApplicationPDU(configuration, command, payload);
		return DatagramFactory.createPacketFromPDU(configuration, pdu);
	}

	public void readDatagramPacket(DatagramPacket packet, IDataReceivedHandler<ApplicationPDU> applicationHandler,
			IAnswerHandler answerHandler) {

		NetworkPDU network = PDUFactory.createNetworkPDU(packet.getData());
		if (networkManager.handleDataReceived(packet, network, answerHandler)) {
			return;
		}

		TransportPDU transport = PDUFactory.createTransportPDU(network.getInnerData());
		if (transportManager.handleDataReceived(packet, transport, answerHandler)) {
			return;
		}

		SessionPDU session = PDUFactory.createSessionPDU(transport.getInnerData());
		if (sessionManager.handleDataReceived(packet, session, answerHandler)) {
			return;
		}

		PresentationPDU presentation = PDUFactory.createPresentationPDU(session.getInnerData());
		if (presentationManager.handleDataReceived(packet, presentation, answerHandler)) {
			return;
		}

		// Use handler so it is possible to decide if the message should be
		// handled by the application
		ApplicationPDU application = PDUFactory.createApplicationPDU(presentation.getInnerData());
		applicationHandler.handleDataReceived(packet, application, answerHandler);
	}

	private PDU createOpenConnectionPDU() {
		int sessionId = ConfigurationSettings.DEFAULT_SESSION_ID;
		int flags = ConfigurationSettings.REQUEST_SESSION_FLAGS;
		String openMessage = ConfigurationSettings.OPEN_MESSAGE;

		return new NetworkPDU(new TransportPDU(
				new SessionPDU(flags, sessionId, new PresentationPDU(new ApplicationPDU(new PDU(openMessage))))));
	}

	private PDU createApplicationPDU(IConfiguration configuration, int command, byte[] payload) {
		return new NetworkPDU(new TransportPDU(new SessionPDU(configuration.getSessionId(),
				new PresentationPDU(new ApplicationPDU(command, new PDU(payload))))));
	}
}
