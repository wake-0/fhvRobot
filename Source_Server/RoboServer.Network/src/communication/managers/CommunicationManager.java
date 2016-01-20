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

import communication.configurations.Configuration;
import communication.configurations.ConfigurationFactory;
import communication.configurations.ConfigurationSettings;
import communication.configurations.IConfiguration;
import communication.pdu.ApplicationPDU;
import communication.pdu.NetworkPDU;
import communication.pdu.PDUFactory;
import communication.pdu.PresentationPDU;
import communication.pdu.SessionPDU;
import communication.pdu.TransportPDU;

public class CommunicationManager<T extends IConfiguration> {

	// Fields
	private final NetworkManager<T> networkManager;
	private final TransportManager<T> transportManager;
	private final SessionManager<T> sessionManager;
	private final PresentationManager<T> presentationManager;
	private final TempConfigurationsService tempConfigurationsService;
	private final IConfigurationManager<T> clientManager;

	// Constructor
	public CommunicationManager(IConfigurationManager<T> clientManager) {
		this.clientManager = clientManager;
		this.tempConfigurationsService = new TempConfigurationsService();

		// Add a manager for each layer
		this.networkManager = new NetworkManager<T>(clientManager, tempConfigurationsService);
		this.transportManager = new TransportManager<T>(clientManager, tempConfigurationsService);
		this.sessionManager = new SessionManager<T>(clientManager, tempConfigurationsService);
		this.presentationManager = new PresentationManager<T>(clientManager, tempConfigurationsService);
	}

	// Methods
	public void readDatagramPacket(DatagramPacket packet,
			IDataReceivedHandler<ApplicationPDU, IConfiguration> applicationHandler, IAnswerHandler answerHandler) {

		// Check free slot exists
		if (!freeSlotExists(answerHandler, packet)) {
			return;
		}

		byte[] data = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
		// Find or create temp configuration
		IConfiguration configuration = tempConfigurationsService.findConfiguration(packet, data);
		if (configuration == null) {
			configuration = tempConfigurationsService.createConfiguration();
		}

		// Otherwise check network stack
		NetworkPDU network = PDUFactory.createNetworkPDU(data);
		if (network == null || networkManager.handleDataReceived(packet, network, configuration, answerHandler)) {
			return;
		}

		TransportPDU transport = PDUFactory.createTransportPDU(network.getInnerData());
		if (transport == null || transportManager.handleDataReceived(packet, transport, configuration, answerHandler)) {
			return;
		}

		SessionPDU session = PDUFactory.createSessionPDU(transport.getInnerData());
		if (session == null || sessionManager.handleDataReceived(packet, session, configuration, answerHandler)) {
			return;
		}

		PresentationPDU presentation = PDUFactory.createPresentationPDU(session.getInnerData());
		if (presentation == null
				|| presentationManager.handleDataReceived(packet, presentation, configuration, answerHandler)) {
			return;
		}

		// Use handler so it is possible to decide if the message should be
		// handled by the application
		ApplicationPDU application = PDUFactory.createApplicationPDU(presentation.getInnerData());
		if (application == null) {
			return;
		}

		applicationHandler.handleDataReceived(packet, application, configuration, answerHandler);
	}

	private boolean freeSlotExists(IAnswerHandler answerHandler, DatagramPacket packet) {

		boolean freeSlotExists = true;

		// Check free slot exists
		if (clientManager.getConfigurations().size() >= ConfigurationSettings.MAX_CONFIGURATION_COUNT) {
			Configuration answerConfiguration = ConfigurationFactory.createConfiguration(packet);
			DatagramPacket answerPacket = DatagramFactory.createNoFreeSlotPacket(answerConfiguration);
			answerHandler.answer(answerPacket);
			freeSlotExists = false;
		}

		return freeSlotExists;
	}
}
