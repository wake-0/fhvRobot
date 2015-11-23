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
import communication.pdu.ApplicationPDUDecorator;
import communication.pdu.NetworkPDUDecorator;
import communication.pdu.PDU;
import communication.pdu.PresentationPDUDecorator;
import communication.pdu.SessionPDUDecorator;
import communication.pdu.TransportPDUDecorator;

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

		PDU pdu = new NetworkPDUDecorator(new TransportPDUDecorator(new SessionPDUDecorator(flags, sessionId,
				new PresentationPDUDecorator(new ApplicationPDUDecorator(new PDU(message))))));

		return createDatagramPacketFromPDU(configuration, pdu);
	}

	public DatagramPacket createDatagramPacket(IConfiguration configuration, String message) {
		PDU pdu = createPDU(configuration, message);
		return createDatagramPacketFromPDU(configuration, pdu);
	}

	public void readDatagramPacket(DatagramPacket packet, IDataReceivedHandler applicationHandler,
			IAnswerHandler answerHandler) {

		NetworkPDUDecorator network = new NetworkPDUDecorator(new PDU(packet.getData()));
		if (networkManager.handleDataReceived(packet, network, answerHandler)) {
			return;
		}

		TransportPDUDecorator transport = new TransportPDUDecorator(network.getInnerData());
		if (transportManager.handleDataReceived(packet, transport, answerHandler)) {
			return;
		}

		SessionPDUDecorator session = new SessionPDUDecorator(transport.getInnerData());
		if (sessionManager.handleDataReceived(packet, session, answerHandler)) {
			return;
		}

		PresentationPDUDecorator presentation = new PresentationPDUDecorator(session.getInnerData());
		if (presentationManager.handleDataReceived(packet, presentation, answerHandler)) {
			return;
		}

		// Use handler so it is possible to decide if the message should be
		// handled by the application
		ApplicationPDUDecorator application = new ApplicationPDUDecorator(presentation.getInnerData());
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
		return new NetworkPDUDecorator(new TransportPDUDecorator(new SessionPDUDecorator(configuration.getSessionId(),
				new PresentationPDUDecorator(new ApplicationPDUDecorator(new PDU(message))))));
	}
}
