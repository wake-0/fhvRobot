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
import java.util.Arrays;

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
		PDU pdu = PDUFactory.createOpenConnectionPDU();
		return DatagramFactory.createPacketFromPDU(configuration, pdu);
	}

	public DatagramPacket createHeartbeatDatagramPacket(IConfiguration configuration) {
		PDU pdu = PDUFactory.createHeartbeatPDU(configuration);
		return DatagramFactory.createPacketFromPDU(configuration, pdu);
	}

	public DatagramPacket createDatagramPacket(IConfiguration configuration, int flag, int command, byte[] payload) {
		PDU pdu = PDUFactory.createApplicationPDU(configuration, flag, command, payload);
		return DatagramFactory.createPacketFromPDU(configuration, pdu);
	}

	public void readDatagramPacket(DatagramPacket packet, IDataReceivedHandler<ApplicationPDU> applicationHandler,
			IAnswerHandler answerHandler) {

		byte[] data = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());

		NetworkPDU network = PDUFactory.createNetworkPDU(data);
		if (network == null || networkManager.handleDataReceived(packet, network, answerHandler)) {
			return;
		}

		TransportPDU transport = PDUFactory.createTransportPDU(network.getInnerData());
		if (transport == null || transportManager.handleDataReceived(packet, transport, answerHandler)) {
			return;
		}

		SessionPDU session = PDUFactory.createSessionPDU(transport.getInnerData());
		if (session == null || sessionManager.handleDataReceived(packet, session, answerHandler)) {
			return;
		}

		PresentationPDU presentation = PDUFactory.createPresentationPDU(session.getInnerData());
		if (presentation == null || presentationManager.handleDataReceived(packet, presentation, answerHandler)) {
			return;
		}

		// Use handler so it is possible to decide if the message should be
		// handled by the application
		ApplicationPDU application = PDUFactory.createApplicationPDU(presentation.getInnerData());
		if (application == null) {
			return;
		}
		applicationHandler.handleDataReceived(packet, application, answerHandler);
	}
}
