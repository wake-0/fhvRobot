/*
 * Copyright (c) 2015 - 2015, Kevin Wallis, All rights reserved.
 * 
 * Projectname: RoboServer.Network
 * Filename: NetworkManager.java
 * 
 * @author: Kevin Wallis
 * @version: 1
 */
package communication.managers;

import java.net.DatagramPacket;
import java.util.List;

import communication.configurations.Configuration;
import communication.configurations.ConfigurationFactory;
import communication.configurations.ConfigurationSettings;
import communication.configurations.IConfiguration;
import communication.pdu.NetworkPDU;
import communication.pdu.PDUFactory;
import communication.pdu.SessionPDU;
import communication.pdu.TransportPDU;

public class NetworkManager extends LayerManager<NetworkPDU> {

	// Constructor
	public NetworkManager(IConfigurationManager manager, CurrentConfigurationService currentClientService) {
		super(manager, currentClientService);
	}

	// Methods
	@Override
	public boolean handleDataReceived(DatagramPacket packet, NetworkPDU pdu, IAnswerHandler sender) {

		List<IConfiguration> configurations = manager.getConfigurations();

		// No free slot exists
		if (!freeSlotExists(configurations, sender, packet)) {
			return true;
		}

		// Try find a configuration
		String ipAddress = packet.getAddress().getHostName();
		IConfiguration currentConfiguration = getConfiguration(configurations, ipAddress, pdu);

		// No configuration exists, then create a new one
		if (currentConfiguration == null) {
			currentConfiguration = manager.createConfiguration();
			currentConfiguration.setIpAddress(ipAddress);
		}

		// Heart beat increase
		currentConfiguration.increaseHeartBeatCount();
		currentConfigurationService.setConfiguration(currentConfiguration);
		return false;
	}

	private boolean freeSlotExists(List<IConfiguration> configurations, IAnswerHandler sender, DatagramPacket packet) {

		boolean freeSlotExists = true;

		// Check free slot exists
		if (configurations.size() >= ConfigurationSettings.MAX_CONFIGURATION_COUNT) {
			Configuration answerConfiguration = ConfigurationFactory.createConfiguration(packet);
			DatagramPacket answerPacket = DatagramFactory.createNoFreeSlotPacket(answerConfiguration);
			sender.answer(answerPacket);
			freeSlotExists = false;
		}

		return freeSlotExists;
	}

	private IConfiguration getConfiguration(List<IConfiguration> configurations, String ipAddress, NetworkPDU pdu) {

		// This is a hack for checking session id is already used
		TransportPDU transportPDU = PDUFactory.createTransportPDU(pdu.getData());
		SessionPDU sessionPDU = PDUFactory.createSessionPDU(transportPDU.getData());

		int sessionId = sessionPDU.getSessionId();
		int flags = sessionPDU.getFlags();

		// It is necessary to create a new configuration
		if (sessionId == ConfigurationSettings.DEFAULT_SESSION_ID
				&& flags == ConfigurationSettings.REQUEST_SESSION_FLAGS) {
			return null;
		}

		return findConfiguration(configurations, ipAddress, sessionId);
	}

	private IConfiguration findConfiguration(List<IConfiguration> configurations, String ipAddress, int sessionId) {
		for (IConfiguration config : configurations) {
			if (config.getIpAddress().equals(ipAddress) && config.getSessionId() == sessionId) {
				return config;
			}
		}

		return null;
	}
}
